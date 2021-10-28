package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EcoreEList;

import nl.ru.icis.mdeoptimiser.hilo.encoding.io.ModelLoader;

public class Converter {
  private String resourceLocation;
  private String ecoreFilename;
  private String XMIFilename;
  
  ModelLoader modelLoader;
  
  // The metaModel and metaModelInstance contain the structure of the ecore model and xmi instance
  // They do NOT contain the class model as its appropriate instance
  // These resources are used to create and instantiate the relations in the Encoding
  private Resource metaModel;
  private Resource metaModelInstance;
  
  // The structuredModelInstance contains the model instance from the xmi file structured
  // using the package that is given to convert(EPackage), i.e. it contains the class model
  // as its appropriate instance. It is used to fill the Repository with the structured classes
  private Resource structuredModelInstance; 
  
  private Encoding result;
  
  private List<DynamicEObjectImpl> convertedMetaInstances = new ArrayList<>();
  private List<EObject> convertedStructuredInstances = new ArrayList<>();
  
  public Converter(String resourceLocation, String ecoreFilename, String XMIFilename, EPackage structuredModelPackage) throws Exception {
    this.resourceLocation = resourceLocation;
    this.ecoreFilename = ecoreFilename;
    this.XMIFilename = XMIFilename;
    
    this.modelLoader = new ModelLoader(resourceLocation);
    
    // Load the meta model from the ecore file
    this.metaModel = this.modelLoader.loadMetaModel(ecoreFilename);
    
    // Load the metaModelInstance from the xmi file
    this.metaModelInstance = modelLoader.loadModelInstance(XMIFilename, (EPackage) metaModel.getContents().get(0));
    
    // Load the structuredModelInstance using the structuredModelPackage
    this.structuredModelInstance = modelLoader.loadModelInstance(XMIFilename, structuredModelPackage);
  }

  public Encoding convert() throws Exception {
    // Clear old repository
    Encoding.clearRepository();
    convertedMetaInstances.clear();
    convertedStructuredInstances.clear();
    
    // Fill in the result with a fresh encoding
    this.result = new Encoding();
    
    // Instantiate the encoding using the meta models
    instantiateEncoding();
    
    // Filling up the repository using the structured model instance
    initializeRepository();
    
    return result;
  }
  
  private void instantiateEncoding() throws Exception {
    // Instantiate the result encoding relations using the meta model
    for (EObject obj : metaModel.getContents()) {
      if (obj instanceof EPackageImpl) {
        convertEPackageImpl((EPackageImpl) obj, result);
      }
    }
    
    // Fill the meta relations using the instances in the metaMod
    for (EObject obj : metaModelInstance.getContents()) {
      if (obj instanceof DynamicEObjectImpl) {
        convertDynamicEObjectImpl((DynamicEObjectImpl) obj, result);
      }
    }
  }
  
  private void initializeRepository() throws Exception {    
    for (EObject structuredObject : this.structuredModelInstance.getContents()) {
      addStructuredObjectToRepository(structuredObject);
    }
  }
  
  private void addStructuredObjectToRepository(EObject structuredObject) throws Exception {
    // Check if structuredObject has already been added to the repository
    if (this.convertedStructuredInstances.contains(structuredObject)) {
//      System.out.println("[INFO]: Structured object has already been added to the repository, skipping..");
      return;
    }
    
    // Generate identifier based on URIFragment and add it to the encoding
    String identifier = this.structuredModelInstance.getURIFragment(structuredObject);
    if ("".equals(identifier) || "//-1".equals(identifier) || "/-1".equals(identifier)) {
      throw new Exception("Converter could not instantiate an identifier for a structuredObject to be added to the Repository");
    }
    
    result.addNewEObject(structuredObject, identifier);
    convertedStructuredInstances.add(structuredObject);
    
    // For all other structuredObjects within this one add them as well
    for (EStructuralFeature metaRelation : structuredObject.eClass().getEStructuralFeatures()) {
      if (!(metaRelation instanceof EReferenceImpl)) {
        continue;
      }
      
      Object toHandle = structuredObject.eGet(metaRelation);
      if (toHandle instanceof EObjectEList) {
        for (Object instance : (EObjectEList) toHandle) {
          if (!(instance instanceof EObject)) {
            throw new Exception("instance from EObjectContainmentEList was not an EObject");
          }
          
          addStructuredObjectToRepository((EObject) instance);
        }
      } else if (toHandle != null) {
        System.out.println("Weird type: " + toHandle.getClass().getCanonicalName());
      }
    }
  }
  
  private void handleEClassifier(EClassifier classifier, Encoding encoding) throws Exception {
    if (classifier instanceof EPackageImpl) {
      convertEPackageImpl((EPackageImpl) classifier, encoding);
    } else if (classifier instanceof EClassImpl) {
      convertEClassImpl((EClassImpl) classifier, encoding);
    }
  }
  
  private void convertEPackageImpl(EPackageImpl pack, Encoding encoding) throws Exception {
    for (EClassifier classifier : pack.getEClassifiers()) {
      handleEClassifier(classifier, encoding);
    }
  }
  
  private void convertEClassImpl(EClassImpl c, Encoding encoding) throws Exception {
    for (EStructuralFeature feature : c.getEStructuralFeatures()) {
      if (feature instanceof EReferenceImpl) {
        if (!(((EReferenceImpl) feature).basicGetEType() instanceof EClassImpl)) {
          throw new Exception("Unexpected type type while converting meta class");
        }
        
        EClassImpl toClass = ((EClassImpl) ((EReferenceImpl) feature).basicGetEType());
        encoding.addRelation(feature.getName(), c.basicGetEPackage().getName(), c.getName(), toClass.basicGetEPackage().getName(), toClass.getName());
      }
    }
    
    // Handle any of the super types in this meta class
    for (EClass superClass : c.getESuperTypes()) {
      if (!(superClass instanceof EClassImpl)) {
        throw new Exception("Unexpected superClass type while converting super types of meta class");
      }
      convertEClassImpl((EClassImpl) superClass, encoding);
    }
  }
  
  private void convertDynamicEObjectImpl(DynamicEObjectImpl dynamicObject, Encoding encoding) throws Exception {
    if (!(dynamicObject.eClass() instanceof EClassImpl)) {
      System.out.println("Meta class has an incorrect type");
      return;
    }
    EClassImpl fromMetaClass = (EClassImpl) dynamicObject.eClass();
    
    if (convertedMetaInstances.contains(dynamicObject)) {
//      System.out.println("[INFO]: Object instance has already been converted, skipping..");
      return;
    }
    convertedMetaInstances.add(dynamicObject);
    
    // Generate identifier for the to be converted dynamic object
    // We look at any relation going outward from the dynamicObject, thus we call it the fromIdentifier
    String fromIdentifier = metaModelInstance.getURIFragment(dynamicObject);
    
    // Instantiate the relations for the immediate type of this dynamic object
    instantiateRelationsFor(dynamicObject, fromMetaClass, fromIdentifier, encoding);
    
    // Do the same for all of the super types of this dynamic object
    for (EClass superClass : fromMetaClass.getESuperTypes()) {
      if (!(superClass instanceof EClassImpl)) {
        throw new Exception("Unexpected EClass type when looking at the super class for a dyanic object");
      }
      instantiateRelationsFor(dynamicObject, (EClassImpl) superClass, fromIdentifier, encoding);
    }
    
    // For all meta relation in this class add them as an instance in the encoding
//    for (EStructuralFeature metaClassRelation : dynamicObject.eClass().getEStructuralFeatures()) {
//      if (metaClassRelation instanceof EReferenceImpl) {
//        if (!(((EReferenceImpl) metaClassRelation).basicGetEType() instanceof EClassImpl)) {
//          throw new Exception("Meta class found to be of wrong type!");
//        }
//        EClassImpl toMetaClass = ((EClassImpl) ((EReferenceImpl) metaClassRelation).basicGetEType());
//        
//        // Define the name of the relation
//        String relation = metaClassRelation.getName() + fromMetaClass.basicGetEPackage().getName() + fromMetaClass.getName() +
//            toMetaClass.basicGetEPackage().getName() + toMetaClass.getName();
//        
//        // Add a new relation instance to the encoding for the found reference
//        encoding.addRelationInstance(fromIdentifier, relation);
//        
//        // For all object in this meta relation from THIS dynamic object we recursively calls this method
//        // Additionally for any object we find for THIS relation we ensure that it is set in our encoding
//        Object toHandle = dynamicObject.eGet(metaClassRelation);
//        // Either this toHandle is a EcoreList filled with DynamicEObjectImpls
//        if (toHandle instanceof EcoreEList) {
//          for (Object obj : (EcoreEList) toHandle) {
//            if (obj instanceof DynamicEObjectImpl) {
//              encoding.addDestinationToRelation(relation, fromIdentifier, metaModelInstance.getURIFragment((DynamicEObjectImpl) obj), true);
//              convertDynamicEObjectImpl((DynamicEObjectImpl) obj, encoding);
//            } else {
//              System.out.println("[ERROR]: Iterating over instances got from a meta reference gave unexpected type: " + obj.getClass().getCanonicalName());
//              System.exit(1);
//            }
//          }
//        } 
//        // Or a singular DynamicEObjectImpl.
//        else if (toHandle instanceof DynamicEObjectImpl) { 
//          encoding.addDestinationToRelation(relation, fromIdentifier, metaModelInstance.getURIFragment((DynamicEObjectImpl) toHandle), true);
//          convertDynamicEObjectImpl((DynamicEObjectImpl) toHandle, encoding);
//        } else if (toHandle == null) {
//          System.out.println("[WARNING]: Meta relation did not find any instances");
//        } else {
//          System.out.println("[ERROR]: Instance got from a meta reference gave unexpected type: " + toHandle.getClass().getCanonicalName());
//          System.exit(1);
//        }
//      }
//    }
  }
  
  private void instantiateRelationsFor(DynamicEObjectImpl instance, EClassImpl type, String fromIdentifier, Encoding encoding) throws Exception {
    // For all meta relation in this class add them as an instance in the encoding
    for (EStructuralFeature metaClassRelation : type.getEStructuralFeatures()) {
      if (metaClassRelation instanceof EReferenceImpl) {
        if (!(((EReferenceImpl) metaClassRelation).basicGetEType() instanceof EClassImpl)) {
          throw new Exception("Meta class found to be of wrong type!");
        }
        EClassImpl toMetaClass = ((EClassImpl) ((EReferenceImpl) metaClassRelation).basicGetEType());
        
        // Define the name of the relation
        String relation = metaClassRelation.getName() + type.basicGetEPackage().getName() + type.getName() +
            toMetaClass.basicGetEPackage().getName() + toMetaClass.getName();
        
        // Add a new relation instance to the encoding for the found reference
        encoding.addRelationInstance(fromIdentifier, relation);
        
        // For all objects in this meta relation from THIS dynamic object we recursively call this method
        // Additionally for any object we find for THIS relation we ensure that it is set in our encoding
        Object toHandle = instance.eGet(metaClassRelation);
        // Either this toHandle is a EcoreList filled with DynamicEObjectImpls
        if (toHandle instanceof EcoreEList) {
          for (Object obj : (EcoreEList) toHandle) {
            if (obj instanceof DynamicEObjectImpl) {
              encoding.setRelationBetween(relation, fromIdentifier, metaModelInstance.getURIFragment((DynamicEObjectImpl) obj), true);
              convertDynamicEObjectImpl((DynamicEObjectImpl) obj, encoding);
            } else {
              System.out.println("[ERROR]: Iterating over instances got from a meta reference gave unexpected type: " + obj.getClass().getCanonicalName());
              System.exit(1);
            }
          }
        } 
        // Or a singular DynamicEObjectImpl.
        else if (toHandle instanceof DynamicEObjectImpl) { 
          encoding.setRelationBetween(relation, fromIdentifier, metaModelInstance.getURIFragment((DynamicEObjectImpl) toHandle), true);
          convertDynamicEObjectImpl((DynamicEObjectImpl) toHandle, encoding);
        } else if (toHandle == null) {
//          System.out.println("[WARNING]: Meta relation did not find any instances");
        } else {
          System.out.println("[ERROR]: Instance got from a meta reference gave unexpected type: " + toHandle.getClass().getCanonicalName());
          System.exit(1);
        }
      }
    }
  }

  public String getXMIFilename() {
    return XMIFilename;
  }

  public void setXMIFilename(String XMIFilename) {
    this.XMIFilename = XMIFilename;
  }
  
  public Encoding getResult() {
    return this.result;
  }
  
  public Resource getStructuredModelInstance() {
    return this.structuredModelInstance;
  }
}
