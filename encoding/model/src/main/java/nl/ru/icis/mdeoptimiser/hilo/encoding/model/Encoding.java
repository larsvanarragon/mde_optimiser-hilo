package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;

import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.DuplicateIdentifierEObjectPairException;
import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.IdentifierEObjectPairNotExistsException;
import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.NoSuchRelationException;
import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.NoSuchRelationInstanceException;
import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.NullIdentifierException;

public class Encoding {
  // The instantiation of the formal encoding
  // We really want to define these types as HashMaps and LinkedHashSets as they give the best performance.
  private HashMap<String, HashMap<String, LinkedHashSet<String>>> encodings = new HashMap<String, HashMap<String, LinkedHashSet<String>>>();
  
  // The Bijective Map containing relations between identifiers and EObjects.
  private Repository repository;
  
  //TODO remove this later
  public static ArrayList<Long> averages = new ArrayList<>();
  public static ArrayList<Long> copyAverages = new ArrayList<>();
  
  public Encoding() {
    this.repository = Repository.getInstance();
  }
  
  public EList<EObject> getRelatedInstancesFor(String relation, EObject object) throws NoSuchRelationInstanceException {
    String identifier = repository.getIdentifierForEObject(object);
    return getRelatedInstancesFor(relation, identifier);
  }
  
  /**
   * Retrieves an EList of all EObjects that are related to an EObject for it's identifier, 
   * and which are contained in the relation specified.
   * @param relation the relation from which to retrieve the objects
   * @param identifier the instance identifier for the relation, this is the same as the identifier for the source EObject
   * @return list of all EObjects as the correct Impls for the relation
   * @throws NoSuchRelationInstanceException there is no relation instance for relation with identifier
   */
  public EList<EObject> getRelatedInstancesFor(String relation, String identifier) throws NoSuchRelationInstanceException {
//    long startTime = System.nanoTime();
    
    if (!relationInstanceExists(relation, identifier)) {
      throw new NoSuchRelationInstanceException(relation, identifier);
    }
    
    EList<EObject> returnList = new BasicEList<>();
    
    for (String relatedIdentifier : encodings.get(relation).get(identifier)) {
      returnList.add(repository.getEObjectForIdentifier(relatedIdentifier));
    }
    
//    averages.add(System.nanoTime() - startTime);
//    System.out.println(System.nanoTime() - startTime);
    
    return returnList;
  }

  protected HashMap<String, LinkedHashSet<String>> getEncodedRelation(String relationName) {
    return encodings.get(relationName);
  }
  
  public void addRelation(String relationName, String fromPackageName, String fromObject, String toPackageName, String toObject) {
    if (relationExists(relationName, fromPackageName, fromObject, toPackageName, toObject)) {
      System.out.println("[INFO] Relation for " + fromPackageName + fromObject + " and " + toPackageName + toObject + " already exists, skipping..");
      return;
    }
    
    String relation = relationName + fromPackageName + fromObject + toPackageName + toObject;
    encodings.put(relation, new HashMap<String, LinkedHashSet<String>>());
  }
  
  public void addRelationInstance(String identifier, String relationName, String fromPackageName, String fromObject, String toPackageName, String toObject) throws NoSuchRelationException {
    addRelationInstance(identifier, relationName + fromPackageName + fromObject + toPackageName + toObject);
  }
  
  public void addRelationInstance(String identifier, String relation) throws NoSuchRelationException {
    if (!relationExists(relation)) {
      throw new NoSuchRelationException(relation);
    }
    
    encodings.get(relation).put(identifier, new LinkedHashSet<>());
  }
  
  public boolean relationExists(String relationName, String fromPackageName, String fromObjectName, String toPackageName, String toObjectName) {
    return relationExists(relationName + fromPackageName + fromObjectName + toPackageName + toObjectName);
  }
  
  public boolean relationExists(String relationName) {
    return encodings.get(relationName) != null;
  }
  
  public boolean relationInstanceExists(String relationName, String instanceName) {
    if (encodings.get(relationName) == null) {
      return false;
    }
    return encodings.get(relationName).get(instanceName) != null;
  }
  
  public void addNewEObject(EObject newObject, String givenIdentifier) throws DuplicateIdentifierEObjectPairException {
    repository.addEObjectWithIdentifier(givenIdentifier, newObject);
  }

  public void addNewEObject(EObject newObject) throws DuplicateIdentifierEObjectPairException, NoSuchRelationException {
    // Add EObject to Repository
    String identifier = repository.addEObjectGeneratingIdentifier(newObject);
    
    // Instantiate all relevant relation instances for this object
    for (EReference metaRelation : newObject.eClass().getEReferences()) {      
      String relation = metaRelation.getName() + newObject.eClass().getEPackage().getName() + newObject.eClass().getName() +
          metaRelation.getEReferenceType().getEPackage().getName() + metaRelation.getEReferenceType().getName();
      
      this.addRelationInstance(identifier, relation);
    }
  }

  public void addRelationBetween(EReference type, EObject source, EObject target) throws NoSuchRelationInstanceException, NullIdentifierException {
    setRelationBetween(type, source, target, true);
  }

  public void removeRelationBetween(EReference type, EObject source, EObject target) throws NoSuchRelationInstanceException, NullIdentifierException {
    setRelationBetween(type, source, target, false);
  }
  
  public void setRelationBetween(EReference type, EObject source, EObject target, boolean value) throws NoSuchRelationInstanceException, NullIdentifierException {
    String relation = type.getName() + type.getEContainingClass().getEPackage().getName() + type.getEContainingClass().getName()
                          + type.getEReferenceType().getEPackage().getName() + type.getEReferenceType().getName();
    
    String sourceIdentifier = repository.getIdentifierForEObject(source);
    String targetIdentifier = repository.getIdentifierForEObject(target);
    
    setRelationBetween(relation, sourceIdentifier, targetIdentifier, value);
  }
  
  public void setRelationBetween(String relation, String sourceIdentifier, String targetIdentifier, boolean value) throws NoSuchRelationInstanceException, NullIdentifierException {
    if (!relationInstanceExists(relation, sourceIdentifier)) {
      throw new NoSuchRelationInstanceException(relation, sourceIdentifier);
    }
    
    if ("".equals(targetIdentifier) || targetIdentifier == null) {
      throw new NullIdentifierException("Target identifier for relation: '" + relation + "' with instance '" + sourceIdentifier + "' is not supplied");
    }
    
    if (value) {
      encodings.get(relation).get(sourceIdentifier).add(targetIdentifier);
    } else {
      encodings.get(relation).get(sourceIdentifier).remove(targetIdentifier);
    }
  }

  public void markForDeletion(EObject toDelete) throws IdentifierEObjectPairNotExistsException {
    repository.markForDeletion(toDelete);
  }
  
  public void deleteUnusedEObjects() {
    // TODO ideally, this should iterate over all of the relations, touching the repository and thereby unmarking them for deletion
    // Then this should perform repository.doDeletion() to delete all not referenced EObjects in the relations.
  }
  
  public Encoding copy() {
//    long startTime = System.nanoTime();
    Encoding copiedEncoding = new Encoding();
    
    HashMap<String, HashMap<String, LinkedHashSet<String>>> copiedRelations = new HashMap<>();
    for (String relation : encodings.keySet()) {
      HashMap<String, LinkedHashSet<String>> relationInstances = new HashMap<>();
      for (String relationInstanceKey : encodings.get(relation).keySet()) {
        relationInstances.put(relationInstanceKey, new LinkedHashSet<String>(encodings.get(relation).get(relationInstanceKey)));
      }
      copiedRelations.put(relation, relationInstances);
    }
    copiedEncoding.encodings = copiedRelations;
    
//    copyAverages.add(System.nanoTime() - startTime);
//    System.out.println(copyAverages.size());
    
    return copiedEncoding;
  }
  
  public boolean equals(Encoding otherEncoding) {
    for (String relation : encodings.keySet()) {
      if (!otherEncoding.relationExists(relation)) {
        return false;
      }
      
      for (String relationInstance : encodings.get(relation).keySet()) {
        if (!otherEncoding.relationInstanceExists(relation, relationInstance)) {
          return false;
        }
        
        if (!encodings.get(relation).get(relationInstance).equals(otherEncoding.getEncodedRelation(relation).get(relationInstance))) {
          return false;
        }
      }
    }
    
    return true;
  }
}
