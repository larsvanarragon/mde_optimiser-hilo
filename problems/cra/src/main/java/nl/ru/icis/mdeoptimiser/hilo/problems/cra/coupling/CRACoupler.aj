package nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;

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
    
    return false;
  }
 
  protected void addReferencesToList(EObject object, EReferenceImpl reference, List<EObject> addToList, Encoding encoding) {
    String relation = reference.getName() + object.eClass().getEPackage().getName() + object.eClass().getName();
    relation += reference.basicGetEReferenceType().getEPackage().getName() + reference.basicGetEReferenceType().getName();
    
    encoding.getRelatedInstancesFor(relation, "");
  }
}
