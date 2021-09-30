package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.converter;


import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;

import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;
import nl.ru.icis.mdeoptimiser.hilo.experiment.io.ExperimentData;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.Main;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.bool.AbstractBooleanNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.AbstractModelNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPVariable;

public class Convertor {
  
  //Assuming that the population of solutions have 1 variable with an NRP model  
  public static NondominatedPopulation convertModelToBitVector(NondominatedPopulation modelPop) {
    boolean old = ExperimentConfig.isAspectJEnabled;
    ExperimentConfig.isAspectJEnabled = false;
    
    NondominatedPopulation result = ExperimentData.newArchive();
     
    for (Solution sol : modelPop) {
      Solution toAdd = new Solution(sol.getNumberOfVariables(), sol.getNumberOfObjectives(), sol.getNumberOfConstraints());
      toAdd.setVariable(0, convertNRPToBoolArray(((ModelNRPVariable) sol.getVariable(0)).getModel()));
      
      result.add(toAdd);
      
      // Also fills in the objectives of the toAdd solution
      if (!areNRPandBoolSame(sol, toAdd)) {
        System.out.println("SEVERE ERROR: Converted solution is not the same as the original!");
        System.exit(1);
      }
    }
    
    ExperimentConfig.isAspectJEnabled = old;

    return result;
  }
  
  public static BinaryVariable convertNRPToBoolArray(NRP model) {
    BinaryVariable result = new BinaryVariable(model.getAvailableArtifacts().size());
    
    for (int i = 0; i < model.getAvailableArtifacts().size(); i++) {
      if (model.getSolutions().get(0).getSelectedArtifacts().contains(model.getAvailableArtifacts().get(i))) {
        result.set(i, true);
      }
    }
    
    return result;
  }
  
//  public static void evaluateBooleanSolution(Solution solution) {
//    boolean old = Main.AJEnabled();
//    Main.setAJEnabled(false);
//    
//    new AbstractBooleanNRP().evaluate(solution);
//    
//    Main.setAJEnabled(old);
//  }
  
  public static boolean areNRPandBoolSame(Solution model, Solution bitvector) {
    AbstractBooleanNRP boolProblem = new AbstractBooleanNRP();
    AbstractModelNRP modelProblem = new AbstractModelNRP(Main.getModel());
    
    boolean old = ExperimentConfig.isAspectJEnabled;
    ExperimentConfig.isAspectJEnabled = true;
    
    boolProblem.evaluate(bitvector);
    
    ExperimentConfig.isAspectJEnabled = false;
    
    modelProblem.evaluate(model);
    
    ExperimentConfig.isAspectJEnabled = old;
    
    return (model.getObjective(0) == bitvector.getObjective(0)) && (model.getObjective(1) == bitvector.getObjective(1));
  }
}
