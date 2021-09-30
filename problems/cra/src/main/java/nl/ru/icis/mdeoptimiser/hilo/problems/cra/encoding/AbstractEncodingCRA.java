package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

import models.cra.fitness.MaximiseCRA;
import models.cra.fitness.MinimiseClasslessFeatures;
import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;

public class AbstractEncodingCRA extends AbstractProblem {
  // Static values for the amount of objectives, constraints and variables
  private static final int N_OBJECTIVES = 1;
  private static final int N_CONSTRAINTS = 1;
  private static final int N_VARIABLES = 1;
  
  // Fitness & Constraint classes
  private MaximiseCRA maximiseCRA;
  private MinimiseClasslessFeatures minimiseClasslessFeatures;

  // The original encoding of the original model instance which will be mutated
  private Encoding originalEncoding;
  // Original model instance used as filler, AspectJ handles all calls using the encoding
  private ClassModel cra;
  
  public AbstractEncodingCRA(Encoding originalEncoding, ClassModel cra) {
    super(N_VARIABLES, N_OBJECTIVES, N_CONSTRAINTS);
    
    this.maximiseCRA = new MaximiseCRA();
    this.minimiseClasslessFeatures = new MinimiseClasslessFeatures();
    
    this.originalEncoding = originalEncoding;
    this.cra = cra;
  }

  @Override
  public void evaluate(Solution solution) {
    // Set current Encoding in AspectJ
    
    uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution wrapperSolution = 
        new uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution(cra);
    
    solution.setObjective(0, maximiseCRA.computeFitness(wrapperSolution));
    solution.setConstraint(0, minimiseClasslessFeatures.computeFitness(wrapperSolution));
  }

  @Override
  public Solution newSolution() {
    Solution solution = new Solution(N_VARIABLES, N_OBJECTIVES, N_CONSTRAINTS);
    solution.setVariable(0, new EncodingCRAVariable(originalEncoding.copy()));
    return solution;
  }

}
