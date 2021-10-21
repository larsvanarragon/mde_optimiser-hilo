package nl.ru.icis.mdeoptimiser.hilo.encoding.exception;

import org.eclipse.emf.ecore.EObject;

public class IdentifierEObjectPairNotExistsException extends Exception {
  public IdentifierEObjectPairNotExistsException(String identifier, EObject object) {
    super("Pair for possible EObject: (" + objectString(object) + ") or identifier: (" + identifier + ") does not exist");
  }
  
  private static String objectString(EObject object) {
    return object != null ? object + " " + object.toString() : "null";
  }
}
