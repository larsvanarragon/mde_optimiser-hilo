package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.problem.AbstractProblem;

import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.experiment.Experiment;
import nl.ru.icis.mdeoptimiser.hilo.experiment.ExperimentProblem;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.AbstractModelNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPFactory;

public class ModelExperiment extends Experiment {
  private static AlgorithmFactory factory;
  
  public ModelExperiment(NRP model) {
    super(model);
    
    this.problem = problem();
    
    initFactory();
  }
  
  public ModelExperiment(NRP model, int evaluations, int popsize) {
    super(model, evaluations, popsize);
    
    this.problem = problem();
    
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
        .withMaxEvaluations(config.evaluations)
        .withProperty("populationSize", config.populationSize)
        .run();
  }

  @Override
  public ExperimentProblem problem() {
    return new AbstractModelNRP((NRP) this.model);
  }

  @Override
  public Experiment copy() {
    return new ModelExperiment((NRP) EcoreUtil.copy(model), config.evaluations, config.populationSize);
  }

  @Override
  protected String name() {
    return "ModelExperiment";
  }
}
