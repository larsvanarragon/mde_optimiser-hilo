package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.problem.AbstractProblem;

import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.AbstractModelNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPFactory;

public class ModelExperiment extends Experiment {
  private static AlgorithmFactory factory;
  
  public ModelExperiment(NRP model) {
    super(model);
    initFactory();
  }
  
  public ModelExperiment(NRP model, int evaluations, int popsize) {
    super(model, evaluations, popsize);
    initFactory();
  }
  
  private static void initFactory() {
    if (factory != null) {
      return;
    }
    
    factory = new AlgorithmFactory();
    factory.addProvider(new ModelNRPFactory());
  }

  @Override
  protected NondominatedPopulation doExperiment() {
    return new Executor().usingAlgorithmFactory(factory)
        .withProblem(this.problem)
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(this.evaluations)
        .withProperty("populationSize", this.populationSize)
        .run();
  }

  @Override
  protected AbstractProblem problem() {
    return new AbstractModelNRP(this.model);
  }

  @Override
  protected boolean initializeAJRequired() {
    return false;
  }

}
