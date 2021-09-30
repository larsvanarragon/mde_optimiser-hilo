package nl.ru.icis.mdeoptimiser.hilo.experiment;

import java.util.ArrayList;
import java.util.List;

import nl.ru.icis.mdeoptimiser.hilo.experiment.hypervolume.HypervolumeEvaluator;

public class Batch {
  private Experiment experiment;
  private int size;
  
  private List<Long> results = new ArrayList<>();
  
  private List<Double> hypervolumes = new ArrayList<>();
  private HypervolumeEvaluator hvEvaluator;
  
  public Batch(Experiment experiment, int size, HypervolumeEvaluator hypervolumeEvaluator) {
    this.experiment = experiment;
    this.size = size;
    this.hvEvaluator = hypervolumeEvaluator;
    
    if (size < 1) {
      System.out.println("ERROR: Failed initializing batch, SEVERE ERROR! n needs to be >= 1");
      System.exit(1);
    }
  }

  public boolean requiresAJ() {
    return experiment.requiresAJ();
  }
  
  public void run() {
    if (experiment == null) {
      System.out.println("This Batch has already ran it's experiments, they are thrown in the garbage for memory consumptions");
      return;
    }
    
    System.out.print("Running batch of " + size + " " + experiment.name() + "s, working on: ");
    
    for (int i = 0; i < size; i++) {
      System.out.print((i+1) + " ");
      results.add(experiment.run());
      hypervolumes.add(hvEvaluator.evaluate(experiment.result));
      
      // Hopefully have the other experiment garbage collected
      experiment = experiment.copy();
    }
    
    this.experiment = null;
    
    System.out.println("finished!");
  }
  
  public String generateResults() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("(");
    for (int i = 0; i < size; i++) {
      builder.append("t");
      builder.append(results.get(i));
      
      builder.append(" hv");
      builder.append(hypervolumes.get(i));
      
      builder.append(" - ");
    }
    builder.delete((builder.length() - 3), builder.length());
    builder.append(")");
    
    return builder.toString();
  }
  
  public int size() {
    return size;
  }
}
