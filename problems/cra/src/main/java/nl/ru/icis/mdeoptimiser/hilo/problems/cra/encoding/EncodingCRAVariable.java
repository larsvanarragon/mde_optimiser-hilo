package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import org.moeaframework.core.Variable;

import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;

public class EncodingCRAVariable implements Variable {
  
  private Encoding encoding;
  
  public EncodingCRAVariable(Encoding encoding) {
    this.encoding = encoding;
  }

  @Override
  public Variable copy() {
    EncodingCRAVariable var = new EncodingCRAVariable(encoding.copy());
    return var;
  }

  @Override
  public void randomize() {
    // TODO Auto-generated method stub
    
  }

}
