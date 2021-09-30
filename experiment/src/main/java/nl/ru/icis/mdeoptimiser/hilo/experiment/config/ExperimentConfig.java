package nl.ru.icis.mdeoptimiser.hilo.experiment.config;

public class ExperimentConfig {
  // Configurable values
  public int evaluations;
  public int populationSize;
  
  // Default values
  private static final int DEFAULT_EVALUATIONS = 1000;
  private static final int DEFAULT_POPULATIONSIZE = 200;
  
  // Static boolean keeping track of whether AspectJ is enabled.
  public static boolean isAspectJEnabled = false;

  public ExperimentConfig() {
    this.evaluations = DEFAULT_EVALUATIONS;
    this.populationSize = DEFAULT_POPULATIONSIZE;
  }
  
  public ExperimentConfig(int evaluations, int populationSize) {
    this.evaluations = evaluations;
    this.populationSize = populationSize;
  }
}
