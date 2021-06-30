package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model;

import java.util.Properties;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.AlgorithmProvider;

import uk.ac.kcl.inf.mdeoptimiser.languages.validation.algorithm.UnexpectedAlgorithmException;

public class ModelNRPFactory extends AlgorithmProvider {

  @Override
  public Algorithm getAlgorithm(String name, Properties properties, Problem problem) {
    if ("NSGAII".equals(name)) {
      // Create an initial random population of population size (From MDEOptimiser? Not sure why TODO)
      var initialization =
          new RandomInitialization(problem, 40);
  
      return new NSGAII(
          problem,
          new NondominatedSortingPopulation(),
          null, // no archive
          null, // default selection the the one built in
          new ModelNRPVariation(),
          new RandomInitialization(problem, 40));
    } else {
      throw new UnexpectedAlgorithmException(name);
    }
  }

}
