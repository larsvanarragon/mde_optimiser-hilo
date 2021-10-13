package nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

import models.cra.fitness.architectureCRA.impl.ClassImpl;
import models.cra.fitness.architectureCRA.impl.ClassModelImpl;
import models.cra.fitness.architectureCRA.impl.FeatureImpl;
import models.cra.fitness.architectureCRA.impl.MethodImpl;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;

public aspect CRACoupler {
//  import java.util.ArrayList;
//  import java.util.List;
//
//  import org.eclipse.emf.common.util.EList;
//  import org.eclipse.emf.ecore.EObject;
//  import org.eclipse.emf.ecore.EStructuralFeature;
//  import org.eclipse.emf.ecore.impl.EReferenceImpl;
//
//  import models.cra.fitness.architectureCRA.impl.ClassImpl;
//  import models.cra.fitness.architectureCRA.impl.ClassModelImpl;
//  import models.cra.fitness.architectureCRA.impl.FeatureImpl;
//  import models.cra.fitness.architectureCRA.impl.MethodImpl;
//  import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
//  import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;
  
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
    
    addToList.addAll(encoding.getRelatedInstancesFor(relation, object));
  }
  
  // START INTERCEPTING ClassModelImpl GETTERS
  /**
   * Pointcut to intercept the getter for the classes of a ClassModelImpl
   * @param classModel
   */
  pointcut getClassesFromClassModel(ClassModelImpl classModel): 
    call(EList<models.cra.fitness.architectureCRA.Class> ClassModelImpl.getClasses())
    && target (classModel);
  
  EList<models.cra.fitness.architectureCRA.Class> around(ClassModelImpl classModel): getClassesFromClassModel(classModel) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(classModel);
    }
    
    Encoding encoding = CRACoupleData.getCurrentEncoding();
    
    return (EList<models.cra.fitness.architectureCRA.Class>)(EList<?>) 
        encoding.getRelatedInstancesFor(CRACoupleData.CLASSMODEL_TO_CLASS_RELATION, classModel);
  }
  
  /**
   * Pointcut to intercept the getter for the features of a ClassModelImpl
   * @param classModel
   */
  pointcut getFeaturesFromClassModel(ClassModelImpl classModel): 
    call(EList<models.cra.fitness.architectureCRA.Feature> ClassModelImpl.getFeatures())
    && target (classModel);
  
  EList<models.cra.fitness.architectureCRA.Feature> around(ClassModelImpl classModel): getFeaturesFromClassModel(classModel) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(classModel);
    }
    
    Encoding encoding = CRACoupleData.getCurrentEncoding();
    
    return (EList<models.cra.fitness.architectureCRA.Feature>)(EList<?>) 
        encoding.getRelatedInstancesFor(CRACoupleData.CLASSMODEL_TO_FEATURE_RELATION, classModel);
  }
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
    
    EList<EObject> encapsulated = encoding.getRelatedInstancesFor(CRACoupleData.FEATURE_TO_CLASS_RELATION, feature);
    if (encapsulated.size() > 1) {
      System.out.println("[ERROR] Encoding broke, a feature can't be encapsulated by two classes");
      return null;
    }
    
    if (encapsulated.size() < 1) {
      return null;
    }
    
    return (models.cra.fitness.architectureCRA.Class) encapsulated.get(0);
  }
  
  pointcut getIsEncapsulatedByFeature(models.cra.fitness.architectureCRA.impl.FeatureImpl feature):
    call(models.cra.fitness.architectureCRA.Class FeatureImpl.getIsEncapsulatedBy())
    && target(feature);
  
  models.cra.fitness.architectureCRA.Class around(FeatureImpl feature) : getIsEncapsulatedByFeature(feature) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(feature);
    }
    
    Encoding encoding = CRACoupleData.getCurrentEncoding();
    
    EList<EObject> encapsulated = encoding.getRelatedInstancesFor(CRACoupleData.FEATURE_TO_CLASS_RELATION, feature);
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
  pointcut getEncapsulatesClass(ClassImpl clazz):
    call(EList<models.cra.fitness.architectureCRA.Feature> ClassImpl.getEncapsulates())
    && target(clazz);
  
  EList<models.cra.fitness.architectureCRA.Feature> around(ClassImpl clazz) : getEncapsulatesClass(clazz) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(clazz);
    }
    
    Encoding encoding = CRACoupleData.getCurrentEncoding();
    
    return (EList<models.cra.fitness.architectureCRA.Feature>)(EList<?>) 
        encoding.getRelatedInstancesFor(CRACoupleData.CLASS_TO_FEATURE_RELATION, clazz);
  }
  // END INTERCEPTING ClassImpl GETTERS
  
  // START INTERCEPTING MethodImpl GETTERS
  pointcut getDataDependencyMethod(MethodImpl method):
    call (EList<models.cra.fitness.architectureCRA.Attribute> MethodImpl.getDataDependency())
    && target(method);
  
  EList<models.cra.fitness.architectureCRA.Attribute> around(MethodImpl method) : getDataDependencyMethod(method) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(method);
    }
    
    Encoding encoding = CRACoupleData.getCurrentEncoding();
    
    return (EList<models.cra.fitness.architectureCRA.Attribute>) (EList<?>) 
        encoding.getRelatedInstancesFor(CRACoupleData.METHOD_TO_ATTRIBUTE_DD_RELATION, method);
  }
  
  pointcut getFunctionalDependencyMethod(MethodImpl method):
    call (EList<models.cra.fitness.architectureCRA.Method> MethodImpl.getFunctionalDependency())
    && target(method);
  
  EList<models.cra.fitness.architectureCRA.Method> around(MethodImpl method) : getFunctionalDependencyMethod(method) {
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(method);
    }
    
    Encoding encoding = CRACoupleData.getCurrentEncoding();
    
    return (EList<models.cra.fitness.architectureCRA.Method>) (EList<?>) 
        encoding.getRelatedInstancesFor(CRACoupleData.METHOD_TO_ATTRIBUTE_FD_RELATION, method);
  }
  // END INTERCEPTING MethodImpl GETTERS
  
  // START INTERCEPTING AttributeImpl GETTERS
  // There are none
  // END INTERCEPTING AttributeImpl GETTERS
}
