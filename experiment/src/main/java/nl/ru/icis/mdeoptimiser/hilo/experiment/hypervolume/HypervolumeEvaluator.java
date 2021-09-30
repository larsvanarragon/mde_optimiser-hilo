package nl.ru.icis.mdeoptimiser.hilo.experiment.hypervolume;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.Hypervolume;

import nl.ru.icis.mdeoptimiser.hilo.experiment.ExperimentProblem;
import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;
import nl.ru.icis.mdeoptimiser.hilo.experiment.io.ExperimentData;

public abstract class HypervolumeEvaluator {
  // The actual instantiated evaluator
  private Hypervolume evaluator;
  
  // The reference population must be of correct format for the problem
  private ExperimentProblem problem;
  private NondominatedPopulation referencePopulation;
  
  public HypervolumeEvaluator(ExperimentProblem problem, String referenceFile, boolean requiresAJ) {
    
    this.problem = problem;
    this.referencePopulation = ExperimentData.readBinaryVariablePopulationFromFile(referenceFile);
    
    evaluateObjectives(referencePopulation);
    
    this.evaluator = new Hypervolume(this.problem, this.referencePopulation);
  }
  
  protected void evaluateObjectives(NondominatedPopulation toEvaluatePopulation) {
    boolean old = ExperimentConfig.isAspectJEnabled;
    ExperimentConfig.isAspectJEnabled = problem.requiresAspectJ();
    
    for (Solution sol : toEvaluatePopulation) {
      problem.evaluate(sol);
    }
    
    ExperimentConfig.isAspectJEnabled = old;
  }
  
  public double evaluate(NondominatedPopulation population) {
    if (population.size() <= 0) {
      System.out.println("Can't evaluate empty population");
      System.exit(1);
    }

    boolean old = ExperimentConfig.isAspectJEnabled;
    ExperimentConfig.isAspectJEnabled = problem.requiresAspectJ();
        
    double returnValue = evaluator.evaluate(convertVariables(population));

    ExperimentConfig.isAspectJEnabled = old;
    
    return returnValue;
  }
  
  protected abstract NondominatedPopulation convertVariables(NondominatedPopulation toConvert);
}
