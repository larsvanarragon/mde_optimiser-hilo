package nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

import models.cra.fitness.architectureCRA.impl.ClassModelImpl;
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
    
    addToList.addAll(encoding.getRelatedInstancesFor(relation, object));
  }
  
  // START INTERCEPT MODEL CALLS
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
//  pointcut getFeaturesFromClassModel(ClassModelImpl classModel): 
//    call(EList<models.cra.fitness.architectureCRA.Feature> ClassModelImpl.getFeatures())
//    && target (classModel);
//  
//  EList<models.cra.fitness.architectureCRA.Feature> around(ClassModelImpl classModel): getFeaturesFromClassModel(classModel) {
//    if (!ExperimentConfig.isAspectJEnabled) {
//      return proceed(classModel);
//    }
//    
//    Encoding encoding = CRACoupleData.getCurrentEncoding();
//    
//    return (EList<models.cra.fitness.architectureCRA.Feature>)(EList<?>) 
//        encoding.getRelatedInstancesFor(CRACoupleData.CLASSMODEL_TO_FEATURE_RELATION, classModel);
//  }
  
  
}
