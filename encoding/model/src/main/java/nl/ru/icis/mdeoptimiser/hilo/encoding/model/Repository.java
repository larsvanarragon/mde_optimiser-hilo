package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.HashBiMap;

public class Repository {
  private static Repository repository = new Repository();
  
  private HashBiMap<String, EObject> data = HashBiMap.create();
  
  private Repository() {
    
  }
  
  public static Repository getInstance() {
    return repository;
  }
  
  public void addIdentifierEObjectBiMap(String identifier, EObject object) {
    if (data.get(identifier) != null || data.inverse().get(object) != null) {
      System.out.println("[ERROR]: This Identifier EObject relation already exists");
      System.exit(1);
    }
    
    data.put(identifier, object);
  }
  
  public String getIdentifierForEObject(EObject object) {
    return data.inverse().get(object);
  }
  
  public EObject getEObjectForIdentifier(String identifier) {
    return data.get(identifier);
  }
}
