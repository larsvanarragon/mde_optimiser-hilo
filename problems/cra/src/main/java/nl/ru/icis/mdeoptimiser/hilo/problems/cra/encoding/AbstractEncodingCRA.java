package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import java.util.List;

import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

import models.cra.fitness.MaximiseCRA;
import models.cra.fitness.MinimiseClasslessFeatures;
import models.cra.fitness.MinimiseEmptyClasses;
import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;

public class AbstractEncodingCRA extends AbstractProblem {
  // Static values for the amount of objectives, constraints and variables
  private static final int N_OBJECTIVES = 1;
  private static final int N_CONSTRAINTS = 2;
  private static final int N_VARIABLES = 1;
  
  // Fitness & Constraint classes
  private MaximiseCRA maximiseCRA;
  private MinimiseClasslessFeatures minimiseClasslessFeatures;
  private MinimiseEmptyClasses minimiseEmptyClasses;

  // The original encoding of the original model instance which will be mutated
  private Encoding originalEncoding;
  // Original model instance used as filler, AspectJ handles all calls using the encoding
  private ClassModel cra;
  // Henshin mutation operators
  private List<Unit> henshinOperators;
  
  public AbstractEncodingCRA(Encoding originalEncoding, ClassModel cra, List<Unit> henshinOperators) {
    super(N_VARIABLES, N_OBJECTIVES, N_CONSTRAINTS);
    
    this.maximiseCRA = new MaximiseCRA();
    this.minimiseClasslessFeatures = new MinimiseClasslessFeatures();
    this.minimiseEmptyClasses = new MinimiseEmptyClasses();
    
    this.originalEncoding = originalEncoding;
    this.cra = cra;
    this.henshinOperators = henshinOperators;
  }

  @Override
  public void evaluate(Solution solution) {
    // Set current Encoding in AspectJ
    
    uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution wrapperSolution = 
        new uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution(cra);
    
    solution.setObjective(0, maximiseCRA.computeFitness(wrapperSolution));
    solution.setConstraint(0, minimiseClasslessFeatures.computeFitness(wrapperSolution));
    solution.setConstraint(1, minimiseEmptyClasses.computeFitness(wrapperSolution));
  }

  @Override
  public Solution newSolution() {
    Solution solution = new Solution(N_VARIABLES, N_OBJECTIVES, N_CONSTRAINTS);
    solution.setVariable(0, new EncodingCRAVariable(originalEncoding.copy(), cra, henshinOperators));
    return solution;
  }

}
