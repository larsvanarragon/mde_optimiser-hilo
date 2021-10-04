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
import org.eclipse.emf.ecore.util.EcoreUtil;

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

  public Encoding convert() throws Exception {
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
    
    this.result = result;
    return result;
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
          throw new Exception("A");
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
    EClassImpl objectMetaClass = (EClassImpl) dynamicObject.eClass();
    
    if (converted.contains(dynamicObject)) {
      System.out.println("Object has already been converted, skipping..");
      return;
    }
    converted.add(dynamicObject);
    
    // For all meta relation in this class add them as an instance in the encoding
    for (EStructuralFeature metaClassRelation : dynamicObject.eClass().getEAllStructuralFeatures()) {
      if (metaClassRelation instanceof EReferenceImpl) {
        if (!(((EReferenceImpl) metaClassRelation).basicGetEType() instanceof EClassImpl)) {
          throw new Exception("Meta class found to be of wrong type!");
        }
        EClassImpl toMetaClass = ((EClassImpl) ((EReferenceImpl) metaClassRelation).basicGetEType());
        
        addDynamicEObjectImpl(metaClassRelation.getName(), objectMetaClass.basicGetEPackage().getName(), objectMetaClass.getName(), 
            toMetaClass.basicGetEPackage().getName(), toMetaClass.getName(), dynamicObject, encoding);
      }
    }
    
    // For all other object in this class initialize that their relation is '1' in the encoding
    for (EObject relationDynamicObject : dynamicObject.eContents()) {
      if (!(relationDynamicObject instanceof DynamicEObjectImpl)) {
        continue;
      }
      
      EClassImpl toMetaClass = ((EClassImpl) ((DynamicEObjectImpl) relationDynamicObject).eClass());
      // For all structural features in the from class find the relation name
      String relationName = "";
      for (EStructuralFeature metaClassRelation : dynamicObject.eClass().getEAllStructuralFeatures()) {
        if (metaClassRelation instanceof EReferenceImpl) {
          if (((EReferenceImpl) metaClassRelation).basicGetEType().getName().equals(toMetaClass.getName())) {
            relationName = metaClassRelation.getName();
          }
        }
      }
      
      String relation = relationName + objectMetaClass.basicGetEPackage().getName() + objectMetaClass.getName() + toMetaClass.basicGetEPackage().getName() + toMetaClass.getName();
      String toClassIdentifier = modelInstance.getURIFragment(relationDynamicObject);
      
      encoding.setValueInRelationForIdentifier(relation, toClassIdentifier, true);
    }
    
    // Convert all other objects related to this class
    for (EObject relationDynamicObject : dynamicObject.eContents()) {
      if (relationDynamicObject instanceof DynamicEObjectImpl && !converted.contains(relationDynamicObject)) {
        convertDynamicEObjectImpl((DynamicEObjectImpl) relationDynamicObject, encoding);
      } else if (converted.contains(relationDynamicObject)) {
        System.out.println("Object has already been converted, skipping..");
      } else {
        System.out.println("Unexpected type in model instance" + relationDynamicObject.getClass().getName());
      }
    }
  }
  
  private void addDynamicEObjectImpl(String relationName, String fromPackage, String fromClass, String toPackage, String toClass, DynamicEObjectImpl toAdd, Encoding encoding) {
    // Create a unique identifier for the relation which is reproducible
    String identifier = modelInstance.getURIFragment(toAdd);
    
    encoding.addRelationInstance(identifier, relationName, fromPackage, fromClass, toPackage, toClass);
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
