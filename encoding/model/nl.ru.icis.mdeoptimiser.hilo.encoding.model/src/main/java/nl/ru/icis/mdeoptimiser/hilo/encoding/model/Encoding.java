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
}
