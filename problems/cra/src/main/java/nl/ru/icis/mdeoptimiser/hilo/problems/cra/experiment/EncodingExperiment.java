package nl.ru.icis.mdeoptimiser.hilo.problems.cra.experiment;

import java.util.ArrayList;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.termination.MaxFunctionEvaluations;

import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
import nl.ru.icis.mdeoptimiser.hilo.experiment.Experiment;
import nl.ru.icis.mdeoptimiser.hilo.experiment.ExperimentProblem;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.AbstractEncodingCRA;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.EncodingCRAFactory;

public class EncodingExperiment extends Experiment {
  private AlgorithmFactory factory;
  
  private Encoding encoding;
  
  private ArrayList<Unit> units;

  public EncodingExperiment(ClassModel cra, Encoding encoding, ArrayList<Unit> units) {
    super(cra);
    this.encoding = encoding;
    this.units = units;
    initializeFactory();
  }

  public EncodingExperiment(ClassModel cra, Encoding encoding, ArrayList<Unit> units, int evaluations, int populationSize) {
    super(cra, evaluations, populationSize);
    this.encoding = encoding;
    this.units = units;
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
    return new EncodingExperiment((ClassModel) EcoreUtil.copy(model), encoding.copy(), units, config.evaluations, config.populationSize);
  }

  @Override
  protected ExperimentProblem problem() {
    return new AbstractEncodingCRA(encoding, (ClassModel) model, units);
  }

  @Override
  protected String name() {
    return "EncodedCRAExperiment";
  }

}
