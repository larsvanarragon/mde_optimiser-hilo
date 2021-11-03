package nl.ru.icis.mdeoptimiser.hilo.problems.cra.experiment;

import java.util.OptionalDouble;
import java.util.ArrayList;

import org.eclipse.xtext.testing.util.ParseHelper;
import org.moeaframework.core.NondominatedPopulation;

import nl.ru.icis.mdeoptimiser.hilo.experiment.Experiment;
import nl.ru.icis.mdeoptimiser.hilo.experiment.ExperimentProblem;
import uk.ac.kcl.inf.mdeoptimiser.languages.mopt.Optimisation;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.OptimisationInterpreter;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.henshin.MdeoRuleApplicationImpl;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.moea.problem.MoeaOptimisationProblem;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.moea.problem.MoeaOptimisationSolution;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.operators.mutation.application.AbstractMutationStrategy;

public class MDEOExperiment extends Experiment {
  private String moptFile;
  private String modelInstance;
  
  private ParseHelper<Optimisation> parseHelper;
  private Optimisation optimisationModel;
  
  private double bestFitness;
  
  public MDEOExperiment(ParseHelper<Optimisation> parseHelper, int evaluations, int populationSize, String modelInstance) {
    super(null, evaluations, populationSize);
    this.moptFile = makeMOPTFile(evaluations, populationSize, modelInstance);
    this.modelInstance = modelInstance;
    this.parseHelper = parseHelper;
    try {
      this.optimisationModel = parseHelper.parse(moptFile);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  protected NondominatedPopulation doExperiment() {
    var optimisationInterpreter = new OptimisationInterpreter("", optimisationModel);
    var optimisationOutcome = optimisationInterpreter.start();
    MoeaOptimisationSolution sol = ((ArrayList<MoeaOptimisationSolution>) optimisationOutcome.getAccumulator().get("Approximation Set", this.config.evaluations-1)).get(0);
    bestFitness = sol.getObjective(0);
    return null;
  }

  @Override
  public ExperimentProblem problem() {
    return null;
  }

  @Override
  public Experiment copy() {
    return new MDEOExperiment(parseHelper, this.config.evaluations, this.config.populationSize, modelInstance);
  }

  @Override
  protected String name() {
    return "MDEOExperiment";
  }

  @Override
  protected String stringResults() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("<timeTaken: ");
    builder.append((double) this.timeTaken() / 1_000_000_000);
    builder.append(" second(s), bestFitness: ");
    builder.append(bestFitness);
    
    builder.append(" matching: ");
    OptionalDouble average = MdeoRuleApplicationImpl.timings.stream().mapToDouble(a -> a).average();
    builder.append(average.toString());

    builder.append(" mutation: ");
    OptionalDouble mutationAverage = MdeoRuleApplicationImpl.mutationTimings.stream().mapToDouble(a -> a).average();
    builder.append(mutationAverage.toString());
    
    builder.append(" copy: ");
    OptionalDouble copyAverage = AbstractMutationStrategy.timings.stream().mapToDouble(a -> a).average();
    builder.append(copyAverage.toString());
    
    builder.append(" evaluation: ");
    OptionalDouble evaluationAverage = MoeaOptimisationProblem.timings.stream().mapToDouble(a -> a).average();
    builder.append(evaluationAverage.toString());
    
    builder.append(" entireMutation: ");
    OptionalDouble entireMutationAverage = AbstractMutationStrategy.mutationTimings.stream().mapToDouble(a -> a).average();
    builder.append(entireMutationAverage.toString());
        
    builder.append(">\n");

    timings.put("timeTaken", OptionalDouble.of((double) this.timeTaken() / 1_000_000_000));
    timings.put("bestFitness", OptionalDouble.of(bestFitness));
    timings.put("matching", average);
    timings.put("mutation", mutationAverage);
    timings.put("entireMutation", entireMutationAverage);
    timings.put("copy", copyAverage);
    timings.put("evaluation", evaluationAverage);
    
    MdeoRuleApplicationImpl.timings.clear();
    MdeoRuleApplicationImpl.mutationTimings.clear();
    AbstractMutationStrategy.timings.clear();
    AbstractMutationStrategy.mutationTimings.clear();
    MoeaOptimisationProblem.timings.clear();
    
    // TODO Auto-generated method stub
    return builder.toString();
  }
  
  private String makeMOPTFile(int evaluations, int populationSize, String modelInstance) {
    return "problem {\n"
        + "  basepath <src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/cra/>\n"
        + "  metamodel <models.cra.fitness.architectureCRA.ArchitectureCRAPackage>\n"
        + "  model <" + modelInstance + ">\n"
        + "}\n"
        + "goal {\n"
        + "  objective CRA maximise java { \"models.cra.fitness.MaximiseCRA\" }\n"
        + "  constraint MinimiseClasslessFeatures java { \"models.cra.fitness.MinimiseClasslessFeatures\" }\n"
        + "  constraint MinimiseEmptyClasses java { \"models.cra.fitness.MinimiseEmptyClasses\" }\n"
        + "}\n"
        + "search { \n"
        + "  mutate using <craEvolvers.henshin> unit \"createClass\"\n"
        + "  mutate using <craEvolvers.henshin> unit \"assignFeature\"\n"
        + "  mutate using <craEvolvers.henshin> unit \"moveFeature\"\n"
        + "  mutate using <craEvolvers.henshin> unit \"deleteEmptyClass\"\n"
        + "}\n"
        + "solver {\n"
        + "  optimisation provider moea algorithm NSGAII {\n"
        + "    population: " + populationSize + "\n"
        + "    variation: mutation\n"
        + "    mutation.step: 1\n"
        + "    mutation.strategy: random\n"
        + "  }\n"
        + "  termination {\n"
        + "    evolutions: " + evaluations + "\n"
        + "  }\n"
        + "  batches 1\n"
        + "}";
  }

}
