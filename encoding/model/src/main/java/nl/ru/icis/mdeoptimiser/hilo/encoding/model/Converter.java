package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreEList;

import nl.ru.icis.mdeoptimiser.hilo.encoding.io.ModelLoader;

public class Converter {
  private String resourceLocation;
  private String ecoreFilename;
  private String XMIFilename;
  
  ModelLoader modelLoader;
  
  private Resource metaModel;
  private Resource modelInstance;
  
  private Encoding result;
  
  private List<DynamicEObjectImpl> converted = new ArrayList<>();
  
  public Converter(String resourceLocation, String ecoreFilename, String XMIFilename) {
    this.resourceLocation = resourceLocation;
    this.ecoreFilename = ecoreFilename;
    this.XMIFilename = XMIFilename;
    
    this.modelLoader = new ModelLoader(resourceLocation);
    this.metaModel = this.modelLoader.loadMetaModel(ecoreFilename);
  }

  public Encoding convert(EObject baseModelInstance) throws Exception {
    Encoding result = new Encoding();
    
    for (EObject obj : metaModel.getContents()) {
      if (obj instanceof EPackageImpl) {
        convertEPackageImpl((EPackageImpl) obj, result);
      }
    }
    
    if (XMIFilename == null || "".equals(XMIFilename)) {
      System.out.println("[ERROR] XMIFilename not supplied for the model instance of the metamodel");
      throw new Exception();
    }
    
    this.modelInstance = modelLoader.loadModelInstance(XMIFilename, (EPackage) metaModel.getContents().get(0));
    
    for (EObject obj : modelInstance.getContents()) {
      if (obj instanceof DynamicEObjectImpl) {
        convertDynamicEObjectImpl((DynamicEObjectImpl) obj, result);
      }
    }
    
    // Function that takes an EObject and uses it to initialize the model
    // IMPORTANT: This EObject must be the ROOT of the model instance as its
    //        generated interface (e.g. NRP or ClassModel for the NRP and CRA cases)
    initializeRepository(baseModelInstance);
    
    this.result = result;
    return result;
  }
  
  private void initializeRepository(EObject baseModelInstance) {
    for (EStructuralFeature metaRelation : baseModelInstance.eClass().getEAllStructuralFeatures()) {
      if (!(metaRelation instanceof EReferenceImpl)) {
        continue;
      }
      
      Object toHandle = baseModelInstance.eGet(metaRelation);
      if (toHandle instanceof EObjectContainmentEList) {
        
      } 
//      else if (toHandle instanceof EObjectContainment) {
//        TODO single EObject instead of more than one.
//      }
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
    for (EStructuralFeature feature : c.getEAllStructuralFeatures()) {
      if (feature instanceof EReferenceImpl) {
        if (!(((EReferenceImpl) feature).basicGetEType() instanceof EClassImpl)) {
          throw new Exception("Unexpected type type while converting meta class");
        }
        
        EClassImpl toClass = ((EClassImpl) ((EReferenceImpl) feature).basicGetEType());
        encoding.addRelation(feature.getName(), c.basicGetEPackage().getName(), c.getName(), toClass.basicGetEPackage().getName(), toClass.getName());
      }
    }
  }
  
  private void convertDynamicEObjectImpl(DynamicEObjectImpl dynamicObject, Encoding encoding) throws Exception {
    if (!(dynamicObject.eClass() instanceof EClassImpl)) {
      System.out.println("Meta class has an incorrect type");
      return;
    }
    EClassImpl fromMetaClass = (EClassImpl) dynamicObject.eClass();
    
    if (converted.contains(dynamicObject)) {
      System.out.println("[INFO]: Object instance has already been converted, skipping..");
      return;
    }
    converted.add(dynamicObject);
    
    // Generate identifier for the to be converted dynamic object
    // We look at any relation going outward from the dynamicObject, thus we call it the fromIdentifier
    String fromIdentifier = modelInstance.getURIFragment(dynamicObject);
    
    // For all meta relation in this class add them as an instance in the encoding
    for (EStructuralFeature metaClassRelation : dynamicObject.eClass().getEAllStructuralFeatures()) {
      if (metaClassRelation instanceof EReferenceImpl) {
        if (!(((EReferenceImpl) metaClassRelation).basicGetEType() instanceof EClassImpl)) {
          throw new Exception("Meta class found to be of wrong type!");
        }
        EClassImpl toMetaClass = ((EClassImpl) ((EReferenceImpl) metaClassRelation).basicGetEType());
        
        // Define the name of the relation
        String relation = metaClassRelation.getName() + fromMetaClass.basicGetEPackage().getName() + fromMetaClass.getName() +
            toMetaClass.basicGetEPackage().getName() + toMetaClass.getName();
        
        // Add a new relation instance to the encoding for the found reference
        encoding.addRelationInstance(fromIdentifier, relation);
        
        // For all object in this meta relation from THIS dynamic object we recursively calls this method
        // Additionally for any object we find for THIS relation we ensure that it is set in our encoding
        Object toHandle = dynamicObject.eGet(metaClassRelation);
        // Either this toHandle is a EcoreList filled with DynamicEObjectImpls
        if (toHandle instanceof EcoreEList) {
          for (Object obj : (EcoreEList) toHandle) {
            if (obj instanceof DynamicEObjectImpl) {
              encoding.addDestinationToRelation(relation, fromIdentifier, modelInstance.getURIFragment((DynamicEObjectImpl) obj), true);
              convertDynamicEObjectImpl((DynamicEObjectImpl) obj, encoding);
            } else {
              System.out.println("[ERROR]: Iterating over instances got from a meta reference gave unexpected type: " + obj.getClass().getCanonicalName());
              System.exit(1);
            }
          }
        } 
        // Or a singular DynamicEObjectImpl.
        else if (toHandle instanceof DynamicEObjectImpl) { 
          encoding.addDestinationToRelation(relation, fromIdentifier, modelInstance.getURIFragment((DynamicEObjectImpl) toHandle), true);
          convertDynamicEObjectImpl((DynamicEObjectImpl) toHandle, encoding);
        } else if (toHandle == null) {
          System.out.println("[WARNING]: Meta relation did not find any instances");
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

  public void setXMIFilename(String xMIFilename) {
    XMIFilename = xMIFilename;
  }
  
  public Encoding getLastResult() {
    return this.result;
  }
}
