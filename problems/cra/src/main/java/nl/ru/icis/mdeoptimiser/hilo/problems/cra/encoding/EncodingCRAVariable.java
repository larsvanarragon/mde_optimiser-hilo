package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Variable;

import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;

public class EncodingCRAVariable implements Variable {
  
  private Encoding encoding;
  
  private ClassModel model;
  
  private List<Unit> henshinOperators;
  
  public EncodingCRAVariable(Encoding encoding, ClassModel model, List<Unit> henshinOperators) {
    this.encoding = encoding;
    this.model = model;
    this.henshinOperators = henshinOperators;
  }

  @Override
  public Variable copy() {
    EncodingCRAVariable var = new EncodingCRAVariable(encoding.copy(), model, henshinOperators);
    return var;
  }

  @Override
  public void randomize() {
    // TODO Auto-generated method stub
    
  }
  
  public Encoding getEncoding() {
    return this.encoding;
  }

  public ClassModel getModel() {
    return this.model;
  }

  public Unit getRandomOperator() {
    Collections.shuffle(henshinOperators);
    return henshinOperators.get(0);
  }
}
