package nl.ru.icis.mdeoptimiser.hilo.problems.cra.experiment;

import java.util.ArrayList;
import java.util.OptionalDouble;

import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.termination.MaxFunctionEvaluations;

import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Converter;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
import nl.ru.icis.mdeoptimiser.hilo.experiment.Experiment;
import nl.ru.icis.mdeoptimiser.hilo.experiment.ExperimentProblem;
import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.AbstractEncodingCRA;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.EncodingCRAFactory;

public class EncodingExperiment extends Experiment {
  private AlgorithmFactory factory;
  
  private Encoding encoding;
  
  private ArrayList<Unit> units;
  
  private Converter converter;

  public EncodingExperiment(ClassModel cra, Converter converter, ArrayList<Unit> units) throws Exception {
    super(cra);
    
    this.converter = converter;
    this.encoding = converter.convert();
    
    this.units = units;
    
    this.problem = problem();
    
    initializeFactory();
  }

  public EncodingExperiment(ClassModel cra, Converter converter, ArrayList<Unit> units, int evaluations, int populationSize) throws Exception {
    super(cra, evaluations, populationSize);
    
    this.converter = converter;
    this.encoding = converter.convert();
    
    this.units = units;
    
    this.problem = problem();
    
    initializeFactory();
  }
  
  private void initializeFactory() {
    factory = new AlgorithmFactory();
    factory.addProvider(new EncodingCRAFactory());
  }

  @Override
  protected NondominatedPopulation doExperiment() {
    return new Executor().usingAlgorithmFactory(factory)
        .withProblem(problem)
        .withAlgorithm("NSGAII")
        .withTerminationCondition(new MaxFunctionEvaluations(20000))
        .withProperty("populationSize", 40)
        .run();
  }

  @Override
  public Experiment copy() {
    try {
      boolean old = ExperimentConfig.isAspectJEnabled;
      ExperimentConfig.isAspectJEnabled = false;
      
      EncodingExperiment copy = new EncodingExperiment((ClassModel) model, converter, units, config.evaluations, config.populationSize);
      
      ExperimentConfig.isAspectJEnabled = old;
      return copy;
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    return null;
  }

  public ExperimentProblem problem() {
    return new AbstractEncodingCRA(encoding.copy(), (ClassModel) model, units);
  }

  @Override
  protected String name() {
    return "EncodedCRAExperiment";
  }

  @Override
  protected String stringResults() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("<timeTaken: ");
    builder.append((double) this.timeTaken() / 1_000_000_000);
    builder.append(" second(s), bestFitness: ");
    builder.append(result.get(0).getObjective(0));
    builder.append(", ");
    for (String resultName : ((AbstractEncodingCRA) problem).timings.keySet()) {
      OptionalDouble average = ((AbstractEncodingCRA) problem).timings.get(resultName).stream().mapToDouble(a -> a).average();

      builder.append(resultName);
      builder.append(": ");
      builder.append(average.toString());
      builder.append(", ");
      
      timings.put(resultName, average);
    }
    builder.delete((builder.length() - 2), builder.length());
    builder.append(">\n");
    
    timings.put("timeTaken", OptionalDouble.of((double) this.timeTaken() / 1_000_000_000));
    timings.put("bestFitness", OptionalDouble.of(result.get(0).getObjective(0)));
    
    return builder.toString();
  }

}
