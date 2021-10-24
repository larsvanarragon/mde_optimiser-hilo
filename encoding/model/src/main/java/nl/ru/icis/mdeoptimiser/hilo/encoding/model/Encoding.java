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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.DuplicateIdentifierEObjectPairException;
import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.IdentifierEObjectPairNotExistsException;

public class Encoding {
  private Map<String, Map<String, Set<String>>> encodings = new HashMap<String, Map<String, Set<String>>>();
  
//  private Map<String, List<String>> identifiersIndex = new HashMap<String, List<String>>();
  
  private Repository repository;
  
  //TODO remove this later
  public static ArrayList<Long> averages = new ArrayList<>();
  
  public Encoding() {
    this.repository = Repository.getInstance();
  }
  
  public EList<EObject> getRelatedInstancesFor(String relation, EObject object) throws Exception {
    String identifier = repository.getIdentifierForEObject(object);
    
    if (identifier == null) {
      throw new Exception("Identifier could not be found for given object");
    }
    
    return getRelatedInstancesFor(relation, identifier);
  }
  
  public EList<EObject> getRelatedInstancesFor(String relation, String identifier) {
    long startTime = System.nanoTime();
    
    if (!relationInstanceExists(relation, identifier)) {
      System.out.println("[ERROR] Can't get instances for a non-existing relation:" + relation);
      return null;
    }
    
    EList<EObject> returnList = new BasicEList<>();
    
//    BitSet encoding = encodings.get(relation).get(identifier);
//    List<String> identifiers = identifiersIndex.get(relation);
    
    for (String relatedIdentifier : encodings.get(relation).get(identifier)) {
      returnList.add(repository.getEObjectForIdentifier(relatedIdentifier));
    }
    
//    for (int i = 0; i < encoding.size(); i++) {
//      if (encoding.get(i)) {
//        EObject toAdd = repository.getEObjectForIdentifier(identifiers.get(i));
//        returnList.add(toAdd);
//      }
//    }
    
    averages.add(System.nanoTime() - startTime);
    
    return returnList;
  }
  
  public void addIdentifierEObjectBiMap(String identifier, EObject object) throws DuplicateIdentifierEObjectPairException {
    repository.addIdentifierEObjectBiMap(identifier, object);
  }
  
//  public void setIdentifierRelatedToInstance(String relation, String identifier, EObject object, boolean value) throws Exception {
//    if (!relationInstanceExists(relation, identifier)) {
//      System.out.println("[ERROR]: Trying to relate EObject to non existing relation");
//      System.exit(1);
//    }
//    String otherObjectIdentifier = repository.getIdentifierForEObject(object);
////    int indexOfOtherObject = getIndexFor(relation, otherObjectIdentifier);
//    
//    encodings.get(relation).get(identifier).set(indexOfOtherObject, value);
//  }

  protected Map<String, Set<String>> getEncodedRelation(String relationName) {
    return encodings.get(relationName);
  }
  
  public void addRelation(String relationName, String fromPackageName, String fromObject, String toPackageName, String toObject) {
    if (relationExists(relationName, fromPackageName, fromObject, toPackageName, toObject)) {
      System.out.println("[INFO] Relation for " + fromPackageName + fromObject + " and " + toPackageName + toObject + " already exists, skipping..");
      return;
    }
    
    String relation = relationName + fromPackageName + fromObject + toPackageName + toObject;
    encodings.put(relation, new HashMap<String, Set<String>>());
//    identifiersIndex.put(relation, new ArrayList<>());
  }
  
  public void addRelationInstance(String identifier, String relationName, String fromPackageName, String fromObject, String toPackageName, String toObject) {
    addRelationInstance(identifier, relationName + fromPackageName + fromObject + toPackageName + toObject);
  }
  
  public void addRelationInstance(String identifier, String relation) {
    if (!relationExists(relation)) {
      System.out.println("[ERROR] Attempted to add instance to nonexisting relation: " + relation);
      return;
    }
    
    encodings.get(relation).put(identifier, new LinkedHashSet<>());
  }
  
  public boolean relationExists(String relationName, String fromPackageName, String fromObjectName, String toPackageName, String toObjectName) {
    return encodings.get(relationName + fromPackageName + fromObjectName + toPackageName + toObjectName) != null;
  }
  
  public boolean relationExists(String relationName) {
    return encodings.get(relationName) != null;
  }
  
  public boolean relationInstanceExists(String relationName, String instanceName) {
    if (encodings.get(relationName) == null) {
      return false;
    }
    return encodings.get(relationName).get(instanceName) != null;// && identifiersIndex.get(relationName).indexOf(instanceName) != -1;
  }
  
//  public List<String> getIdentifiersIndicesForRelation(String relation) {
//    if (!relationExists(relation)) {
//      return null;
//    }
//    
//    return identifiersIndex.get(relation);
//  }
  
  public Encoding copy() {
    Encoding copiedEncoding = new Encoding();
    
    HashMap<String, Map<String, Set<String>>> copiedRelations = new HashMap<>();
    for (String relation : encodings.keySet()) {
      HashMap<String, Set<String>> relationInstances = new HashMap<>();
      for (String relationInstanceKey : encodings.get(relation).keySet()) {
        relationInstances.put(relationInstanceKey, new LinkedHashSet<String>(encodings.get(relation).get(relationInstanceKey)));
//        relationInstances.put(relationInstanceKey, (BitSet) encodings.get(relation).get(relationInstanceKey).clone());
      }
      copiedRelations.put(relation, relationInstances);
    }
    copiedEncoding.encodings = copiedRelations;
    
    
//    HashMap<String, List<String>> copiedIdentifiersIndices = new HashMap<>();
//    for (String relation : identifiersIndex.keySet()) {
//      ArrayList<String> identifiers = new ArrayList<>();
//      for (String identifier : identifiersIndex.get(relation)) {
//        identifiers.add(identifier);
//      }
//      copiedIdentifiersIndices.put(relation, identifiers);
//    }
//    copiedEncoding.identifiersIndex = copiedIdentifiersIndices;
    
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
    
//    for (String relation : identifiersIndex.keySet()) {
//      if (!otherEncoding.relationExists(relation) ) {
//        return false;
//      }
//      
//      if (!otherEncoding.getIdentifiersIndicesForRelation(relation).equals(identifiersIndex.get(relation))) {
//        return false;
//      }
//    }
    return true;
  }

  public void addDestinationToRelation(String relation, String fromClassIdentifier, String toClassIdentifier, boolean b) {
    if (!relationInstanceExists(relation, fromClassIdentifier)) {
      System.out.println("[WARNING]: no instance for the relation: " + relation + " with id: " + fromClassIdentifier + ", cannot add a relation");
      return;
    }
    
    if ("".equals(toClassIdentifier) || "".equals(relation) || "".equals(fromClassIdentifier)) {
      System.out.println("[WARNING]: empty values given as argument, this is not allowed");
      System.exit(1);
    }
    
//    int index = identifiersIndex.get(relation).size(); 
//    identifiersIndex.get(relation).add(toClassIdentifier);
    
    encodings.get(relation).get(fromClassIdentifier).add(toClassIdentifier);
  }
  
//  public int getIndexFor(String relation, String toIdentifier) throws Exception {
//    if (!relationExists(relation)) {
//      throw new Exception("Relation does not exist");
//    }
//    
//    int index = identifiersIndex.get(relation).indexOf(toIdentifier);
//    
//    if (index < 0) {
//      index = identifiersIndex.get(relation).size();
//      identifiersIndex.get(relation).add(toIdentifier);
//    }
//    
//    return index;
//  }

  public void addNewEObject(EObject createdObject) throws Exception {    
    String identifier = repository.addEObjectGeneratingIdentifier(createdObject);
    
//    String relationToPart = createdObject.eClass().getEPackage().getName() + createdObject.eClass().getName();
//    
//    for (String relation : identifiersIndex.keySet()) {
//      if (relationToPart.equals(relation.substring(relation.length() - relationToPart.length()))) {
//        getIndexFor(relation, identifier);
//      }
//    }
  }

  public void addRelationBetween(EReference type, EObject source, EObject target) throws Exception {
    setRelationBetween(type, source, target, true);
  }

  public void removeRelationBetween(EReference type, EObject source, EObject target) throws Exception {
    setRelationBetween(type, source, target, false);
  }
  
  public void setRelationBetween(EReference type, EObject source, EObject target, boolean value) throws Exception {
    String relation = type.getName() + type.getEContainingClass().getEPackage().getName() + type.getEContainingClass().getName()
                          + type.getEReferenceType().getEPackage().getName() + type.getEReferenceType().getName();
    
    String sourceIdentifier = repository.getIdentifierForEObject(source);
    String targetIdentifier = repository.getIdentifierForEObject(target);
    
    if (sourceIdentifier == null || targetIdentifier == null) {
      throw new Exception("No identfier found for either the source or the target");
    }
    
    // If this is a newly added object we don't know the relations yet, so we add it if it's missing
    if (!relationInstanceExists(relation, sourceIdentifier)) {
      addRelationInstance(sourceIdentifier, relation);
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
}
