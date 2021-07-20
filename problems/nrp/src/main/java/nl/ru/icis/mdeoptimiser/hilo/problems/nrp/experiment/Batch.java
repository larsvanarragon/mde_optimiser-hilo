package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment;

import java.util.ArrayList;
import java.util.List;

public class Batch {
  private Experiment experiment;
  private int n;
  
  private List<Long> results = new ArrayList<>();
  
  public Batch(Experiment experiment, int n) {
    this.experiment = experiment;
    this.n = n;
    
    if (n < 1) {
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
    
    System.out.print("Running batch of " + n + " " + experiment.name() + "s, working on: ");
    
    for (int i = 0; i < n; i++) {
      System.out.print((i+1) + " ");
      results.add(experiment.run());
      
      // Hopefully have the other experiment garbage collected
      experiment = experiment.copy();
    }
    
    this.experiment = null;
    
    System.out.println("finished!");
  }
  
  public String generateResults() {
    StringBuilder builder = new StringBuilder();
    
    builder.append("(");
    for (Long r : results) {
      builder.append(r);
      builder.append(" - ");
    }
    builder.delete((builder.length() - 3), builder.length());
    builder.append(")");
    
    return builder.toString();
  }
}
