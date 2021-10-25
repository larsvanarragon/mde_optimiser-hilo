package nl.ru.icis.mdeoptimiser.hilo.experiment;

import org.eclipse.emf.ecore.EObject;
import org.moeaframework.core.NondominatedPopulation;

import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;

public abstract class Experiment {
  // 
  protected EObject model;
  
  protected ExperimentConfig config;
  
  protected ExperimentProblem problem;
  
  private long timeTaken = 0;
  
  protected NondominatedPopulation result;
  
  public Experiment(EObject model) {
    this.model = model;
    
    this.config = new ExperimentConfig();
  }
  
  public Experiment(EObject model, int evaluations, int populationSize) {
    this.model = model;
    
    this.config = new ExperimentConfig(evaluations, populationSize);
  }
  
  protected abstract NondominatedPopulation doExperiment();
  
  public abstract ExperimentProblem problem();
  
  public abstract Experiment copy();
  
  protected abstract String name();
  
  protected abstract String stringResults();
  
  public long run() {
    long startTimeBits = System.nanoTime();
    this.result = doExperiment();
    this.timeTaken = System.nanoTime() - startTimeBits;
    
    return this.timeTaken;
  }
  
  public boolean requiresAJ() {
    return problem.requiresAspectJ();
  }
  
  public NondominatedPopulation result() {
    return result;
  }
  
  public long timeTaken() {
    return timeTaken;
  }
}
