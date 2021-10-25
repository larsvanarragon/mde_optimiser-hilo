package nl.ru.icis.mdeoptimiser.hilo.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.ru.icis.mdeoptimiser.hilo.experiment.hypervolume.HypervolumeEvaluator;

public class Batch {
  private Experiment experiment;
  private int size;
  
  private List<Long> results;
  
  private List<Double> hypervolumes;
  private HypervolumeEvaluator hvEvaluator;
  
  private File file;
  
  public Batch(Experiment experiment, int size, HypervolumeEvaluator hypervolumeEvaluator) {
    this(experiment, size);
    this.hvEvaluator = hypervolumeEvaluator;
    this.hypervolumes = new ArrayList<>();
  }
  
  public Batch(Experiment experiment, int size, File file) {
    this(experiment, size);
    this.file = file;
  }
  
  public Batch(Experiment experiment, int size) {
    this.experiment = experiment;
    this.size = size;
    this.results = new ArrayList<>();
    
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
      
      if (hvEvaluator != null) {
        hypervolumes.add(hvEvaluator.evaluate(experiment.result));
      }
      
      if (file != null) {
        try {
          BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
          writer.append(experiment.stringResults());
          writer.close();
        } catch (IOException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
      
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
      
      if (hypervolumes != null) {
        builder.append(" hv");
        builder.append(hypervolumes.get(i));
      }
      
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
