package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.bool;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

import models.nrp.fitness.MaximiseSatisfaction;
import models.nrp.fitness.MinimiseCost;
import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.experiment.ExperimentProblem;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.Main;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.coupling.NRPCoupleData;

public class AbstractBooleanNRP extends ExperimentProblem {
  
  private static final int N_OBJECTIVES = 2;
  
  private static final int MINCOST_INDEX = 0;
  private static final int MAXSAT_INDEX = 1;
  
  private static final int N_CONSTRAINTS = 0;
  private static final int N_VARIABLES = 1;
  
  private MinimiseCost fitnessMinCost;
  private MaximiseSatisfaction fitnessMaxSat;
  
  public AbstractBooleanNRP() {
    super(N_VARIABLES, N_OBJECTIVES, N_CONSTRAINTS);
    
    this.fitnessMinCost = new MinimiseCost();
    this.fitnessMaxSat = new MaximiseSatisfaction();
  }

  @Override
  public void evaluate(Solution solution) {
    // Store solution information
    NRPCoupleData.setRelation(NRPCoupleData.SOLUTION_RELATION, EncodingUtils.getBinary(solution.getVariable(0)));
    
    // Later use AspectJ to intercept the moeaSolution.getModel() function and just give the bare model
    NRP model = Main.getModel();
    uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution wrapperSolution = 
        new uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution(model);
    
    solution.setObjective(MINCOST_INDEX, fitnessMinCost.computeFitness(wrapperSolution));
    solution.setObjective(MAXSAT_INDEX, fitnessMaxSat.computeFitness(wrapperSolution));
  }

  @Override
  public Solution newSolution() {
    Solution solution = new Solution(N_VARIABLES, N_OBJECTIVES, N_CONSTRAINTS);
    solution.setVariable(0,  EncodingUtils.newBinary(Main.NRPArtifactsSize()));
    return solution;
  }

  @Override
  public boolean requiresAspectJ() {
    return true;
  }
}
