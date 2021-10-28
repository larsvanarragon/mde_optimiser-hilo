package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.HashBiMap;

import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.DuplicateIdentifierEObjectPairException;
import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.IdentifierEObjectPairNotExistsException;

public class Repository {
  private static Repository repository = new Repository();
  
  private HashBiMap<String, EObject> data = HashBiMap.create();
  
  private HashMap<String, Integer> index = new HashMap<>();
  
  private LinkedHashSet<String> deleteSet = new LinkedHashSet<>();
  
  private Repository() {
    
  }
  
  protected static Repository getInstance() {
    return repository;
  }
  
  protected static void clear() {
    repository = new Repository();
  }
    
  protected String getIdentifierForEObject(EObject object) {
    String identifier = data.inverse().get(object);
    return identifier;
  }
  
  protected EObject getEObjectForIdentifier(String identifier) {
    return data.get(identifier);
  }

  protected String addEObjectGeneratingIdentifier(EObject newObject) throws DuplicateIdentifierEObjectPairException {
    if (getIdentifierForEObject(newObject) != null) {
      throw new DuplicateIdentifierEObjectPairException(getIdentifierForEObject(newObject), newObject);
    }
    
    String identifier = generateIdentifierFor(newObject.eClass().getEPackage().getName() + newObject.eClass().getName());
    data.put(identifier, newObject);
    return identifier;
  }
  
  protected void addEObjectWithIdentifier(String identifier, EObject newObject) throws DuplicateIdentifierEObjectPairException {
    if (data.get(identifier) != null || data.inverse().get(newObject) != null) {
      throw new DuplicateIdentifierEObjectPairException(identifier, newObject);
    }
    
    data.put(identifier, newObject);
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

  protected void markForDeletion(EObject toDelete) throws IdentifierEObjectPairNotExistsException {
    String identifier = getIdentifierForEObject(toDelete);
    
    if (identifier == null) {
      throw new IdentifierEObjectPairNotExistsException("N/A", toDelete);
    }
    
    deleteSet.add(identifier);
  }
  
  protected void doDeletion() {
   for (String identifier : deleteSet) {
     data.remove(identifier);
   }
   
   deleteSet.clear();
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Repository[\n");
    
    for (String id : data.keySet()) {
      builder.append("\tID:");
      builder.append(id);
      builder.append(", OBJECT:");
      builder.append(data.get(id).toString());
      builder.append("\n");
    }
    
    builder.append("]");
    return builder.toString();
  }
}
