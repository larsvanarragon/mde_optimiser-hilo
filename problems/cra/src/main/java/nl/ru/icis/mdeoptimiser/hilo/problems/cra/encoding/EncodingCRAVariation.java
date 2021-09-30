package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class EncodingCRAVariation implements Variation {

  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public Solution[] evolve(Solution[] parents) {
    //simulate the henshin rules
    return parents;
  }

}
