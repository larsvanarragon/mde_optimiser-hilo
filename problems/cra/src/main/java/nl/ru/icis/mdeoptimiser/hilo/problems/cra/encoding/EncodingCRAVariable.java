package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Variable;

import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;

public class EncodingCRAVariable implements Variable {
  
  private Encoding encoding;
  
  private ClassModel model;

  private static ArrayList<Unit> operators = new ArrayList<Unit>();
  
  public EncodingCRAVariable(Encoding encoding, ClassModel model, ArrayList<Unit> operators) {
    this.encoding = encoding;
    this.model = model;
    this.operators = operators;
  }

  @Override
  public Variable copy() {
    EncodingCRAVariable var = new EncodingCRAVariable(encoding.copy(), model, operators);
    return var;
  }

  @Override
  public void randomize() {
    new EncodingCRAVariation().mutate(this);
  }
  
  public Encoding getEncoding() {
    return this.encoding;
  }

  public ClassModel getModel() {
    return this.model;
  }
  
  public ArrayList<Unit> getOperators() {
    return operators;
  }
}
