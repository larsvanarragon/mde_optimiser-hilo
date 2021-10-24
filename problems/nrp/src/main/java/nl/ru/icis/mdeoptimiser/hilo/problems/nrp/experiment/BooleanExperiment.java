package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;

import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.experiment.Experiment;
import nl.ru.icis.mdeoptimiser.hilo.experiment.ExperimentProblem;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.bool.AbstractBooleanNRP;

public class BooleanExperiment extends Experiment {
  
  public BooleanExperiment(NRP model) {
    super(model);
    
    this.problem = problem();
  }

  public BooleanExperiment(NRP model, int evaluations, int popsize) {
    super(model, evaluations, popsize);
    
    this.problem = problem();
  }

  @Override
  protected NondominatedPopulation doExperiment() {
    return new Executor().withProblem(this.problem)
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(config.evaluations)
        .withProperty("populationSize", config.populationSize)
        .run();
  }

  @Override
  public ExperimentProblem problem() {
    return new AbstractBooleanNRP();
  }
  
  @Override
  public Experiment copy() {
    return new BooleanExperiment((NRP) EcoreUtil.copy(model), config.evaluations, config.populationSize);
  }

  @Override
  protected String name() {
    return "BooleanExperiment";
  }
}
