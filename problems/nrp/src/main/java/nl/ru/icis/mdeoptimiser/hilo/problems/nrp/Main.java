package nl.ru.icis.mdeoptimiser.hilo.problems.nrp;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.variable.EncodingUtils;

import nl.ru.icis.mdeoptimiser.hilo.coupling.NRPEncodingTranslator;

public class Main {
  public static void main( String[] args ) {
    NRPEncodingTranslator translator = new NRPEncodingTranslator();
    AbstractNRP problem = new AbstractNRP(translator);
    NondominatedPopulation result = new Executor().withProblem(problem)
                  .withAlgorithm("NSGAII")
                  .withMaxEvaluations(500)
                  .run();
    
    for (boolean b : EncodingUtils.getBinary(result.get(0).getVariable(0))) {
      System.out.print(b + ", ");
    }
  }
}
