package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

import models.nrp.nextReleaseProblem.NRP;

public class ModelNRPVariable implements Variable {
  
  private NRP model;
  
  public ModelNRPVariable(NRP model) {
    this.model = model;
  }

  @Override
  public Variable copy() {
    ModelNRPVariable var = new ModelNRPVariable(EcoreUtil.copy(model));
    System.out.println(EcoreUtil.equals(model.getSolutions().get(0).getSelectedArtifacts().get(0), var.getModel().getSolutions().get(0).getSelectedArtifacts().get(0)));
    return var;
  }

  @Override
  public void randomize() {
    
    model.getSolutions().get(0).getSelectedArtifacts().clear();
    
    for (int i = 0; i < model.getAvailableArtifacts().size(); i++) { 
      if (PRNG.nextBoolean()) {
        model.getSolutions().get(0).getSelectedArtifacts().add(model.getAvailableArtifacts().get(i));
      }
    }
  }

  public NRP getModel() {
    return model;
  }
}
