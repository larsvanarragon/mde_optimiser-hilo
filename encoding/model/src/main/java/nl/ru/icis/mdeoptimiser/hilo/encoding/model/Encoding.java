package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

public class Encoding {
  private Map<String, Map<String, BitSet>> encodings = new HashMap<String, Map<String, BitSet>>();
  
  private Map<String, List<String>> identifiersIndex = new HashMap<String, List<String>>();
  
  private Repository repository = Repository.getInstance();
  
  public Encoding() {
    
  }
  
  public EObject findEObjectInstance(String relation) {
    return null;
  }

  public Map<String, BitSet> getEncodedRelation(String relationName) {
    return encodings.get(relationName);
  }
  
  public void addRelation(String relationName, String fromPackageName, String fromObject, String toPackageName, String toObject) {
    if (relationExists(relationName, fromPackageName, fromObject, toPackageName, toObject)) {
      System.out.println("[INFO] Relation for " + fromPackageName + fromObject + " and " + toPackageName + toObject + " already exists, skipping..");
      return;
    }
    
    String relation = relationName + fromPackageName + fromObject + toPackageName + toObject;
    encodings.put(relation, new HashMap<String, BitSet>());
    identifiersIndex.put(relation, new ArrayList<>());
  }
  
  public void addRelationInstance(String identifier, String relationName, String fromPackageName, String fromObject, String toPackageName, String toObject) {
    if (!relationExists(relationName, fromPackageName, fromObject, toPackageName, toObject)) {
      System.out.println("[ERROR] Attempted to add instance to nonexisting relation: " + fromPackageName + fromObject + toPackageName + toObject);
      return;
    }
    
    String relation = relationName + fromPackageName + fromObject + toPackageName + toObject;
    
    encodings.get(relation).put(identifier, new BitSet());
    
    identifiersIndex.get(relation).add(identifier);
  }
  
  public boolean relationExists(String relationName, String fromPackageName, String fromObjectName, String toPackageName, String toObjectName) {
    return encodings.get(relationName + fromPackageName + fromObjectName + toPackageName + toObjectName) != null &&
        identifiersIndex.get(relationName + fromPackageName + fromObjectName + toPackageName + toObjectName) != null;
  }
  
  public boolean relationExists(String relationName) {
    return encodings.get(relationName) != null && identifiersIndex.get(relationName) != null;
  }
  
  public boolean relationInstanceExists(String relationName, String instanceName) {
    if (encodings.get(relationName) == null || identifiersIndex.get(relationName) == null) {
      return false;
    }
    return encodings.get(relationName).get(instanceName) != null && identifiersIndex.get(relationName).indexOf(instanceName) != -1;
  }
  
  public Encoding copy() {
    Encoding copiedEncoding = new Encoding();
    
    HashMap<String, Map<String, BitSet>> copiedRelations = new HashMap<>();
    for (String relation : encodings.keySet()) {
      HashMap<String, BitSet> relationInstances = new HashMap<>();
      for (String relationInstanceKey : encodings.get(relation).keySet()) {
        relationInstances.put(relationInstanceKey, (BitSet) encodings.get(relation).get(relationInstanceKey).clone());
      }
      copiedRelations.put(relation, relationInstances);
    }
    copiedEncoding.encodings = copiedRelations;
    
    
    HashMap<String, List<String>> copiedIdentifiersIndices = new HashMap<>();
    for (String relation : identifiersIndex.keySet()) {
      ArrayList<String> identifiers = new ArrayList<>();
      for (String identifier : identifiersIndex.get(relation)) {
        identifiers.add(identifier);
      }
      copiedIdentifiersIndices.put(relation, identifiers);
    }
    copiedEncoding.identifiersIndex = copiedIdentifiersIndices;
    
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
