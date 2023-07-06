package nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

import models.cra.fitness.architectureCRA.Feature;
import models.cra.fitness.architectureCRA.Method;
import models.cra.fitness.architectureCRA.impl.ClassModelImpl;
import models.cra.fitness.architectureCRA.ClassModel;
import models.cra.fitness.architectureCRA.impl.FeatureImpl;
import models.cra.fitness.architectureCRA.impl.MethodImpl;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;

public aspect CRACoupler {
  
  pointcut addTreeToGraph(EObject root, CRAEGraphImpl graph): 
    call(boolean nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling.CRAEGraphImpl.addTree(org.eclipse.emf.ecore.EObject))
    && args(root) && target(graph);
  
  boolean around(EObject root, CRAEGraphImpl graph): addTreeToGraph(root, graph) {
    List<EObject> toAdd = new ArrayList<>();
    
    for (EStructuralFeature feature : root.eClass().getEAllStructuralFeatures()) {
      if (feature instanceof EReferenceImpl) {
        addReferencesToList(root, (EReferenceImpl) feature, toAdd, graph.getEncoding());
      }
    }
    boolean changed = graph.add(root);
    for (EObject object : toAdd) {
      changed = graph.add(object);
    }
    
    return changed;
  }
 
  protected void addReferencesToList(EObject object, EReferenceImpl reference, List<EObject> addToList, Encoding encoding) {
    String relation = reference.getName() + object.eClass().getEPackage().getName() + object.eClass().getName();
    relation += reference.basicGetEReferenceType().getEPackage().getName() + reference.basicGetEReferenceType().getName();
    
    try {
    addToList.addAll(encoding.getRelatedInstancesFor(relation, object));
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  // START INTERCEPTING ClassModelImpl GETTERS
  /**
   * Pointcuts to intercept the getter for the classes of a ClassModelImpl
   * @param classModel
   */
  pointcut getClassesFromClassModelImpl(ClassModelImpl classModel): 
    call(EList<models.cra.fitness.architectureCRA.Class> ClassModelImpl.getClasses())
    && target (classModel);
  
  EList<models.cra.fitness.architectureCRA.Class> around(ClassModelImpl classModel): getClassesFromClassModelImpl(classModel) {
//    long startTime = System.nanoTime();
    
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(classModel);
    }
    
    EList<models.cra.fitness.architectureCRA.Class> returnList = returnClassesFromClassModel(classModel);
//    System.out.println(System.nanoTime() - startTime);
    return returnList;
  }
    
  pointcut getClassesFromClassModel(ClassModel classModel): 
    call(EList<models.cra.fitness.architectureCRA.Class> ClassModel.getClasses())
    && target (classModel);
  
  EList<models.cra.fitness.architectureCRA.Class> around(ClassModel classModel): getClassesFromClassModel(classModel) {
//    long startTime = System.nanoTime();
    
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(classModel);
    }
    
    EList<models.cra.fitness.architectureCRA.Class> returnList =  returnClassesFromClassModel(classModel);
//    System.out.println(System.nanoTime() - startTime);
    return returnList;
  }
  
  EList<models.cra.fitness.architectureCRA.Class> returnClassesFromClassModel(ClassModel classModel) {    
    try {
      return (EList<models.cra.fitness.architectureCRA.Class>)(EList<?>) 
          CRACoupleData.getCurrentEncoding().getRelatedInstancesFor(CRACoupleData.CLASSMODEL_TO_CLASS_RELATION, classModel);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }
  
  /**
   * Pointcut to intercept the getter for the features of a ClassModelImpl
   * @param classModel
   */
//  pointcut getFeaturesFromClassModel(ClassModel classModel): 
//    call(EList<models.cra.fitness.architectureCRA.Feature> ClassModel.getFeatures())
//    && target (classModel);
//  
//  EList<models.cra.fitness.architectureCRA.Feature> around(ClassModel classModel): getFeaturesFromClassModel(classModel) {
//    if (!ExperimentConfig.isAspectJEnabled) {
//      return proceed(classModel);
//    }
//    
//    return returnFeaturesFromClassModel(classModel);
//  }
//  
//  pointcut getFeaturesFromClassModelImpl(ClassModelImpl classModel): 
//    call(EList<models.cra.fitness.architectureCRA.Feature> ClassModelImpl.getFeatures())
//    && target (classModel);
//  
//  EList<models.cra.fitness.architectureCRA.Feature> around(ClassModelImpl classModel): getFeaturesFromClassModelImpl(classModel) {
//    if (!ExperimentConfig.isAspectJEnabled) {
//      return proceed(classModel);
//    }
//    
//    return returnFeaturesFromClassModel(classModel);
//  }
//  
//  EList<models.cra.fitness.architectureCRA.Feature>  returnFeaturesFromClassModel(ClassModel classModel) {
//    Encoding encoding = CRACoupleData.getCurrentEncoding();
//    
//    try {
//      return (EList<models.cra.fitness.architectureCRA.Feature>)(EList<?>) 
//          encoding.getRelatedInstancesFor(CRACoupleData.CLASSMODEL_TO_FEATURE_RELATION, classModel);
//    } catch (Exception e) {
//      e.printStackTrace();
//      System.exit(1);
//      return null;
//    }
//  }
  // END INTERCEPTING ClassModelImpl GETTERS
  
  // START INTERCEPTING FeatureImpl GETTERS
  pointcut basicEncapsulatedByFromFeature(FeatureImpl feature):
    call(models.cra.fitness.architectureCRA.Class FeatureImpl.basicGetIsEncapsulatedBy())
    && target (feature);
  
  models.cra.fitness.architectureCRA.Class around(FeatureImpl feature) : basicEncapsulatedByFromFeature(feature) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(feature);
    }
    
    Encoding encoding = CRACoupleData.getCurrentEncoding();
    
    EList<EObject> encapsulated;
    try {
      encapsulated = encoding.getRelatedInstancesFor(CRACoupleData.FEATURE_TO_CLASS_RELATION, feature);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
    if (encapsulated.size() > 1) {
      System.out.println("[ERROR] Encoding broke, a feature can't be encapsulated by two classes");
      return null;
    }
    
    if (encapsulated.size() < 1) {
      return null;
    }
    
    return (models.cra.fitness.architectureCRA.Class) encapsulated.get(0);
  }
  
  pointcut getIsEncapsulatedByFeature(models.cra.fitness.architectureCRA.Feature feature):
    call(models.cra.fitness.architectureCRA.Class Feature.getIsEncapsulatedBy())
    && target(feature);
  
  models.cra.fitness.architectureCRA.Class around(Feature feature) : getIsEncapsulatedByFeature(feature) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(feature);
    }
    
    return returnClassFromFeature(feature);
  }
  
  pointcut getIsEncapsulatedByFeatureImpl():
    call(models.cra.fitness.architectureCRA.Class models.cra.fitness.architectureCRA.Feature.getIsEncapsulatedBy())
    && target(models.cra.fitness.architectureCRA.impl.FeatureImpl);
  
  models.cra.fitness.architectureCRA.Class around() : getIsEncapsulatedByFeatureImpl() {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed();
    }
    
    return returnClassFromFeature((FeatureImpl) thisJoinPoint.getThis());
  }
  
  models.cra.fitness.architectureCRA.Class returnClassFromFeature(Feature feature) {
    Encoding encoding = CRACoupleData.getCurrentEncoding();
    
    EList<EObject> encapsulated;
    try {
      encapsulated = encoding.getRelatedInstancesFor(CRACoupleData.FEATURE_TO_CLASS_RELATION, feature);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
    
    if (encapsulated.size() > 1) {
      System.out.println("[ERROR] Encoding broke, a feature can't be encapsulated by two classes");
      return null;
    }
    
    if (encapsulated.size() < 1) {
      return null;
    }
    
    return (models.cra.fitness.architectureCRA.Class) encapsulated.get(0);
  }
  // END INTERCEPTING FeatureImpl GETTERS
  
  // BEGIN INTERCEPTING ClassImpl GETTERS
  pointcut getEncapsulatesClass(models.cra.fitness.architectureCRA.Class clazz):
    call(EList<models.cra.fitness.architectureCRA.Feature> models.cra.fitness.architectureCRA.Class.getEncapsulates())
    && target(clazz);
  
  EList<models.cra.fitness.architectureCRA.Feature> around(models.cra.fitness.architectureCRA.Class clazz) : getEncapsulatesClass(clazz) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(clazz);
    }
    
    return returnFeaturesFromClass(clazz);
  }
  
  pointcut getEncapsulatesClassImpl(models.cra.fitness.architectureCRA.impl.ClassImpl clazz):
    call(EList<models.cra.fitness.architectureCRA.Feature> models.cra.fitness.architectureCRA.impl.ClassImpl.getEncapsulates())
    && target(clazz);
  
  EList<models.cra.fitness.architectureCRA.Feature> around(models.cra.fitness.architectureCRA.impl.ClassImpl clazz) : getEncapsulatesClassImpl(clazz) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(clazz);
    }
    
    return returnFeaturesFromClass(clazz);
  }
  
  EList<models.cra.fitness.architectureCRA.Feature> returnFeaturesFromClass(models.cra.fitness.architectureCRA.Class clazz) {
    Encoding encoding = CRACoupleData.getCurrentEncoding();
    
    try {
      return (EList<models.cra.fitness.architectureCRA.Feature>)(EList<?>) 
          encoding.getRelatedInstancesFor(CRACoupleData.CLASS_TO_FEATURE_RELATION, clazz);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }
  // END INTERCEPTING ClassImpl GETTERS
  
  // START INTERCEPTING MethodImpl GETTERS
//  pointcut getDataDependencyMethod(Method method):
//    call (EList<models.cra.fitness.architectureCRA.Attribute> Method.getDataDependency())
//    && target(method);
//  
//  EList<models.cra.fitness.architectureCRA.Attribute> around(Method method) : getDataDependencyMethod(method) {
//    if (!ExperimentConfig.isAspectJEnabled) {
//      return proceed(method);
//    }
//    
//    return returnAttributesFromMethod(method);
//  }
//  
//  pointcut getDataDependencyMethodImpl(MethodImpl method):
//    call (EList<models.cra.fitness.architectureCRA.Attribute> MethodImpl.getDataDependency())
//    && target(method);
//  
//  EList<models.cra.fitness.architectureCRA.Attribute> around(MethodImpl method) : getDataDependencyMethodImpl(method) {
//    if (!ExperimentConfig.isAspectJEnabled) {
//      return proceed(method);
//    }
//    
//    return returnAttributesFromMethod(method);
//  }
//  
//  EList<models.cra.fitness.architectureCRA.Attribute> returnAttributesFromMethod(Method method) {
//    Encoding encoding = CRACoupleData.getCurrentEncoding();
//    
//    try {
//      return (EList<models.cra.fitness.architectureCRA.Attribute>) (EList<?>) 
//          encoding.getRelatedInstancesFor(CRACoupleData.METHOD_TO_ATTRIBUTE_DD_RELATION, method);
//    } catch (Exception e) {
//      e.printStackTrace();
//      System.exit(1);
//      return null;
//    }
//  }
//  
//  pointcut getFunctionalDependencyMethod(Method method):
//    call (EList<models.cra.fitness.architectureCRA.Method> Method.getFunctionalDependency())
//    && target(method);
//  
//  EList<models.cra.fitness.architectureCRA.Method> around(Method method) : getFunctionalDependencyMethod(method) {
//    if (!ExperimentConfig.isAspectJEnabled) {
//      return proceed(method);
//    }
//    
//    return returnMethodsFromMethod(method);
//  }
//  
//  pointcut getFunctionalDependencyMethodImpl(MethodImpl method):
//    call (EList<models.cra.fitness.architectureCRA.Method> MethodImpl.getFunctionalDependency())
//    && target(method);
//  
//  EList<models.cra.fitness.architectureCRA.Method> around(MethodImpl method) : getFunctionalDependencyMethodImpl(method) {
//    if (!ExperimentConfig.isAspectJEnabled) {
//      return proceed(method);
//    }
//    
//    return returnMethodsFromMethod(method);
//  }
//  
//  EList<models.cra.fitness.architectureCRA.Method> returnMethodsFromMethod(Method method) {
//    Encoding encoding = CRACoupleData.getCurrentEncoding();
//    
//    try {
//      return (EList<models.cra.fitness.architectureCRA.Method>) (EList<?>) 
//          encoding.getRelatedInstancesFor(CRACoupleData.METHOD_TO_ATTRIBUTE_FD_RELATION, method);
//    } catch (Exception e) {
//      e.printStackTrace();
//      System.exit(1);
//      return null;
//    }
//  }
  // END INTERCEPTING MethodImpl GETTERS
  
  // START INTERCEPTING AttributeImpl GETTERS
  // There are none
  // END INTERCEPTING AttributeImpl GETTERS
}
