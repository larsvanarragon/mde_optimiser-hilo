package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

import models.nrp.fitness.MaximiseSatisfaction;
import models.nrp.fitness.MinimiseCost;
import models.nrp.nextReleaseProblem.NRP;
public class AbstractModelNRP extends AbstractProblem {
  
  private static final int N_OBJECTIVES = 2;
  
  private static final int MINCOST_INDEX = 0;
  private static final int MAXSAT_INDEX = 1;
  
  private static final int N_CONSTRAINTS = 0;
  private static final int N_VARIABLES = 1;
  
  private MinimiseCost fitnessMinCost;
  private MaximiseSatisfaction fitnessMaxSat;
  
  private NRP originalModel;
  
  public AbstractModelNRP(NRP originalModel) {
    super(N_VARIABLES, N_OBJECTIVES, N_CONSTRAINTS);
    
    this.fitnessMinCost = new MinimiseCost();
    this.fitnessMaxSat = new MaximiseSatisfaction();
    
    this.originalModel = originalModel;
  }

  @Override
  public void evaluate(Solution solution) {    
    uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution wrapperSolution = 
        new uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution(((ModelNRPVariable) solution.getVariable(0)).getModel());
    
    solution.setObjective(MINCOST_INDEX, fitnessMinCost.computeFitness(wrapperSolution));
    solution.setObjective(MAXSAT_INDEX, fitnessMaxSat.computeFitness(wrapperSolution));
  }

  @Override
  public Solution newSolution() {
    Solution solution = new Solution(N_VARIABLES, N_OBJECTIVES, N_CONSTRAINTS);
    solution.setVariable(0, new ModelNRPVariable(EcoreUtil.copy(originalModel)));
    return solution;
  }
}
