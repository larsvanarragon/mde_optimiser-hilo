package nl.ru.icis.mdeoptimiser.hilo.encoding.exception;

import org.eclipse.emf.ecore.EObject;

public class DuplicateIdentifierEObjectPairException extends Exception {
  public DuplicateIdentifierEObjectPairException(String identifier, EObject object) {
    super("Identifier or EObject already exist in some other pair for identifier: " + identifier + ", and for object" + object.eResource().getURIFragment(object));
  }
}
