package nl.ru.icis.mdeoptimiser.hilo.experiment;

import org.moeaframework.problem.AbstractProblem;

public abstract class ExperimentProblem extends AbstractProblem {

  public ExperimentProblem(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
    super(numberOfVariables, numberOfObjectives, numberOfConstraints);
  }
  
  public abstract boolean requiresAspectJ();
}
