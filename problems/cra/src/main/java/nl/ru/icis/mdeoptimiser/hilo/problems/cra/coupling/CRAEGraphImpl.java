package nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;

import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;

public class CRAEGraphImpl extends EGraphImpl {
  
  private Encoding encoding;
  
  public CRAEGraphImpl(ClassModel model, Encoding encoding) {
    super();
    this.encoding = encoding;
    
//    initializeContents(Collections.singleton(model));
    
    addTree(model);
  }

  @Override
  public boolean addTree(EObject root) {
    boolean changed = add(root);
    for (Iterator<EObject> it = root.eAllContents(); it.hasNext();) {
      if (add(it.next())) changed = true;
    }
    return changed;
  }
  
  public Encoding getEncoding() {
    return encoding;
  }
  
  @Override
  protected void didAdd(EObject object) {
    // Do NOT notify objects they have been added, this is not needed
    EClass type = object.eClass();
    getDomain(type).add(object);
    addEPackage(type.getEPackage());
  }
}
