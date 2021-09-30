package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Encoding {
  private Map<String, Map<String, BitSet>> encodings = new HashMap<String, Map<String, BitSet>>();
  
  public Encoding() {
    
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
  }
  
  public void addRelationInstance(String identifier, String relationName, String fromPackageName, String fromObject, String toPackageName, String toObject) {
    if (!relationExists(relationName, fromPackageName, fromObject, toPackageName, toObject)) {
      System.out.println("[ERROR] Attempted to add instance to nonexisting relation: " + fromPackageName + fromObject + toPackageName + toObject);
      return;
    }
    
    String relation = relationName + fromPackageName + fromObject + toPackageName + toObject;
    
    encodings.get(relation).put(identifier, new BitSet());
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
    return encodings.get(relationName).get(instanceName) != null;
  }
  
  public Encoding copy() {
    Encoding copiedEncoding = new Encoding();
    
    for (String relation : encodings.keySet()) {
      HashMap<String, BitSet> relationInstances = new HashMap<>();
      for (String relationInstanceKey : encodings.get(relation).keySet()) {
        relationInstances.put(relationInstanceKey, (BitSet) encodings.get(relation).get(relationInstanceKey).clone());
      }
    }
    
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
