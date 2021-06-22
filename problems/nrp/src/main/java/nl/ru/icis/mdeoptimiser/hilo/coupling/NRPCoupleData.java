package nl.ru.icis.mdeoptimiser.hilo.coupling;

import java.util.Map;

public class NRPCoupleData {
  private static Map<String, boolean[]> relations;
  
  public static boolean[] getRelation(String key) {
    return relations.get(key);
  }
  
  public static boolean[] setRelation(String key, boolean[] value) {
    return relations.put(key, value);
  }
}
