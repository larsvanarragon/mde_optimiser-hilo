package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.variable.BinaryVariable;

import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.Main;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPVariable;
import nl.ru.icis.mdeoptimiser.hilo.util.HILOUtil;

public class HypervolumeEvaluator {

  private Hypervolume evaluator;
  
  private Problem problem;
  
  private NondominatedPopulation referencePopulation;
  
  private boolean requiresAJ;
  
  public HypervolumeEvaluator(Problem problem, String referenceFile, boolean requiresAJ) {
    this.requiresAJ = requiresAJ;
    
    this.problem = problem;
    this.referencePopulation = HILOUtil.readBinaryVariablePopulationFromFile(referenceFile);
    
    evaluateObjectives(referencePopulation);
    
    this.evaluator = new Hypervolume(this.problem, this.referencePopulation);
  }
  
  private void evaluateObjectives(NondominatedPopulation toEvaluatePopulation) {
    boolean old = Main.AJEnabled();
    Main.setAJEnabled(requiresAJ);
    
    for (Solution sol : toEvaluatePopulation) {
      problem.evaluate(sol);
    }
    
    Main.setAJEnabled(old);
  }
  
  public double evaluate(NondominatedPopulation population) {
    if (population.size() <= 0) {
      System.out.println("Can't evaluate empty population");
      System.exit(1);
    }
    
    boolean old = Main.AJEnabled();
    Main.setAJEnabled(requiresAJ);
        
    double returnValue = evaluator.evaluate(convertVariables(population));
    
    Main.setAJEnabled(old);
    
    return returnValue;
  }
  
  // Assuming pop is not empty
  private NondominatedPopulation convertVariables(NondominatedPopulation toConvert) {
    if (toConvert.get(0).getVariable(0) instanceof BinaryVariable) {
      return toConvert;
    } else if (toConvert.get(0).getVariable(0) instanceof ModelNRPVariable) {
      NondominatedPopulation returnPopulation = convertModelVariableToBinaryVariable(toConvert); 
      evaluateObjectives(returnPopulation);
      return returnPopulation;
    } else {
      return toConvert;
    }
  }
  
  private NondominatedPopulation convertModelVariableToBinaryVariable(NondominatedPopulation toConvert) {
    return HILOUtil.convertModelToBitVector(toConvert);   
  }
}
