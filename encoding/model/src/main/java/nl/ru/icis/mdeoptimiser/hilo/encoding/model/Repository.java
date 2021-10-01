package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import java.util.HashMap;

import org.eclipse.emf.ecore.EObject;

public class Repository {
  private static Repository repository = new Repository();
  
  private HashMap<String, EObject> data = new HashMap<>();
  private HashMap<String, Integer> identifiers = new HashMap<>();
  
  private Repository() {
    
  }
  
  public static Repository getInstance() {
    return repository;
  }
  
  public int obtainNextIdentifier(String objectName) {
    if (!identifiers.containsKey(objectName)) {
      System.out.println("[ERROR]: Repository does not contain an identifier for: " + objectName);
      return -1;
    }
    
    int returnValue = identifiers.get(objectName);
    identifiers.put(objectName, returnValue+1);
    
    return returnValue;
  }
  
  public EObject getEObjectFor(String identifier) {
    EObject returnValue = data.get(identifier);
    
    if (returnValue == null) {
      System.out.println("[ERROR]: Repository has no EObject for identifier: " + identifier);
      return null;
    }
   
    return returnValue;
  }
}
