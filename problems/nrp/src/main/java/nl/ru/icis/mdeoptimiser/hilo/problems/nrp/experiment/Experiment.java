package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.problem.AbstractProblem;

import models.nrp.nextReleaseProblem.NRP;

public abstract class Experiment {
  protected NRP model;
  protected int evaluations;
  protected int populationSize;
  
  protected AbstractProblem problem;
  
  protected boolean requiresAJ;
  
  // Default values
  private static final int DEFAULT_EVALUATIONS = 1000;
  private static final int DEFAULT_POPSIZE = 200;
  
  protected NondominatedPopulation result;
  
  public Experiment(NRP model) {
    this(model, DEFAULT_EVALUATIONS, DEFAULT_POPSIZE);
  }
  
  public Experiment(NRP model, int evaluations, int populationSize) {
    this.model = model;
    this.evaluations = evaluations;
    this.populationSize = populationSize;
    this.requiresAJ = initializeAJRequired();
    
    this.problem = problem();
  }
  
  protected abstract NondominatedPopulation doExperiment();
  
  protected abstract AbstractProblem problem();
  
  protected abstract boolean initializeAJRequired();
  
  public long run() {
    long startTimeBits = System.nanoTime();
    this.result = doExperiment();
    return System.nanoTime() - startTimeBits;
  }
  
  public boolean requiresAJ() {
    return requiresAJ;
  }
  
  public NondominatedPopulation result() {
    return result;
  }
}
