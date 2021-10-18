package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import java.util.HashMap;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.HashBiMap;

import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.DuplicateIdentifierEObjectPairException;

public class Repository {
  private static Repository repository = new Repository();
  
  private HashBiMap<String, EObject> data = HashBiMap.create();
  
  private HashMap<String, Integer> index = new HashMap<>();
  
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

  public String addEObjectGeneratingIdentifier(EObject createdObject) throws DuplicateIdentifierEObjectPairException {
    if (getIdentifierForEObject(createdObject) != null) {
      throw new DuplicateIdentifierEObjectPairException(getIdentifierForEObject(createdObject), createdObject);
    }
    
    String identifier = generateIdentifierFor(createdObject.eClass().getEPackage().getName() + createdObject.eClass().getName());
    data.put(identifier, createdObject);
    return identifier;
  }
  
  private String generateIdentifierFor(String className) {
    // Check whether it already has an index
    if (index.get(className) == null) {
      index.put(className, 0);
    }
    
    // Get the current index for this class and bump it by one for the next one
    Integer classIndex = index.get(className);
    index.put(className, classIndex+1);
    
    return className + classIndex;
  }
}
