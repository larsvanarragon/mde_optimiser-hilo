package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.HashBiMap;

import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.DuplicateIdentifierEObjectPairException;

public class Repository {
  private static Repository repository = new Repository();
  
  private HashBiMap<String, EObject> data = HashBiMap.create();
  
  private Repository() {
    
  }
  
  public static Repository getInstance() {
    return repository;
  }
  
  public void addIdentifierEObjectBiMap(String identifier, EObject object) throws DuplicateIdentifierEObjectPairException {
    if (data.get(identifier) != null || data.inverse().get(object) != null) {
      throw new DuplicateIdentifierEObjectPairException(identifier, object);
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
