package nl.ru.icis.mdeoptimiser.hilo.problems.nrp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.eclipse.xtext.testing.util.ParseHelper;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.variable.EncodingUtils;

import com.google.inject.Injector;
import com.google.inject.Key;

import nl.ru.icis.mdeoptimiser.hilo.coupling.NRPEncodingTranslator;
import uk.ac.kcl.inf.mdeoptimiser.languages.MoptStandaloneSetup;
import uk.ac.kcl.inf.mdeoptimiser.languages.mopt.Optimisation;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.OptimisationInterpreter;

public class Main {
  public static void main( String[] args ) throws Exception {
    NRPEncodingTranslator translator = new NRPEncodingTranslator();
    AbstractNRP problem = new AbstractNRP(translator);
    NondominatedPopulation result = new Executor().withProblem(problem)
                  .withProperty("populationSize", 40) // Not working?
                  .withAlgorithm("NSGAII")
                  .withMaxEvaluations(500)
                  .run();
    
    System.out.println(result.size());
    for (boolean b : EncodingUtils.getBinary(result.get(0).getVariable(0))) {
      System.out.print(b + ", ");
    }
  }
  
  
}
