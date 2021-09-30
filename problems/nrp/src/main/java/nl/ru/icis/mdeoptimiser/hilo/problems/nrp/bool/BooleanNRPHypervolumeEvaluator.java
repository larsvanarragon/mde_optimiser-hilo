package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.bool;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.variable.BinaryVariable;

import nl.ru.icis.mdeoptimiser.hilo.experiment.ExperimentProblem;
import nl.ru.icis.mdeoptimiser.hilo.experiment.hypervolume.HypervolumeEvaluator;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.converter.Convertor;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPVariable;

public class BooleanNRPHypervolumeEvaluator extends HypervolumeEvaluator {

  public BooleanNRPHypervolumeEvaluator(ExperimentProblem problem, String referenceFile) {
    super(problem, referenceFile, true);
  }

  @Override
  protected NondominatedPopulation convertVariables(NondominatedPopulation toConvert) {
    if (toConvert.get(0).getVariable(0) instanceof BinaryVariable) {
      return toConvert;
    } else if (toConvert.get(0).getVariable(0) instanceof ModelNRPVariable) {
      NondominatedPopulation returnPopulation = Convertor.convertModelToBitVector(toConvert);
      evaluateObjectives(returnPopulation);
      return returnPopulation;
    } else {
      System.out.println("[ERROR]: The population to be converted contains unexpected Variables not of type BinaryVariable or ModelNRPVariable");
      return null;
    }
  }
}
