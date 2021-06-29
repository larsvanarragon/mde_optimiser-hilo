package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model;

import java.util.Random;

import org.moeaframework.core.Variable;

import models.nrp.nextReleaseProblem.NRP;

public class ModelNRPVariable implements Variable {
  
  private NRP model;
  
  // Randomizer
  private Random random = new Random();
  
  public ModelNRPVariable(NRP model) {
    this.model = model;
  }

  @Override
  public Variable copy() {
    return new ModelNRPVariable(model);
  }

  @Override
  public void randomize() {
    model.getSolutions().get(0).getSelectedArtifacts().clear();
    
    for (int i = 0; i < model.getAvailableArtifacts().size(); i++) { 
      if (random.nextBoolean()) {
        model.getSolutions().get(0).getSelectedArtifacts().add(model.getAvailableArtifacts().get(i));
      }
    }
  }

  public NRP getModel() {
    return model;
  }
}
