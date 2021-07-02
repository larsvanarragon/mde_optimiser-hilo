package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.problem.AbstractProblem;

import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.bool.AbstractBooleanNRP;

public class BooleanExperiment extends Experiment {
  
  public BooleanExperiment(NRP model) {
    super(model);
  }

  public BooleanExperiment(NRP model, int evaluations, int popsize) {
    super(model, evaluations, popsize);
  }

  @Override
  protected NondominatedPopulation doExperiment() {
    return new Executor().withProblem(this.problem)
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(this.evaluations)
        .withProperty("populationSize", this.populationSize)
        .run();
  }

  @Override
  protected AbstractProblem problem() {
    return new AbstractBooleanNRP();
  }

  @Override
  protected boolean initializeAJRequired() {
    return true;
  }

}
