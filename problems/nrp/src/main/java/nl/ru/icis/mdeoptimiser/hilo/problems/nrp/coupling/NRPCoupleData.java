package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.coupling;

import java.util.HashMap;
import java.util.Map;

// TODO clean this up neatly
public class NRPCoupleData {
  private static Map<String, boolean[]> relations = new HashMap<>();
  
  public static final String SOLUTION_RELATION = "solution";
  
  public static boolean[] getRelation(String key) {
    return relations.get(key);
  }
  
  public static boolean[] setRelation(String key, boolean[] value) {
    return relations.put(key, value);
  }
}
