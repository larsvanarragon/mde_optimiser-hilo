package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.binary.BitFlip;
import org.moeaframework.core.operator.binary.HUX;

import models.nrp.nextReleaseProblem.SoftwareArtifact;

public class ModelNRPVariation implements Variation {
  
  private static final double P_HUX = 1.0;
  private static final double P_BITFLIP = 0.01;
  
  private static HUX hux;
  private static BitFlip bitFlip;
  
  @Override
  public int getArity() {
    return 2;
  }

  @Override
  public Solution[] evolve(Solution[] parents) {
    Solution[] returnList = simulateHUXVariation(parents);
    
    return new Solution[] {simulateBitFlipVariation(returnList[0]), simulateBitFlipVariation(returnList[1])};
  }
  
  private Solution[] simulateHUXVariation(Solution[] parents) {
    Solution p1 = parents[0].copy();
    Solution p2 = parents[1].copy();
    
    if (!(PRNG.nextDouble() <= P_HUX)) {
      return parents;
    }
    
    for (SoftwareArtifact artifact : ((ModelNRPVariable) parents[0].getVariable(0)).getModel().getAvailableArtifacts()) {
      if (
          (((ModelNRPVariable) parents[0].getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact) != 
          ((ModelNRPVariable) parents[1].getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact)) &&
          PRNG.nextBoolean()
          ) {
        
        if (((ModelNRPVariable) p1.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact)) {
          ((ModelNRPVariable) p1.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().remove(artifact);
        } else {
          ((ModelNRPVariable) p1.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().add(artifact);
        }
        
        if (((ModelNRPVariable) p2.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact)) {
          ((ModelNRPVariable) p2.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().remove(artifact);
        } else {
          ((ModelNRPVariable) p2.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().add(artifact);
        }
      }
    }
    
    return new Solution[] {p1, p2};
  }

  private Solution simulateBitFlipVariation(Solution parent) {
    Solution child = parent.copy();
    
    for (SoftwareArtifact artifact : ((ModelNRPVariable) parent.getVariable(0)).getModel().getAvailableArtifacts()) {
      
      if (PRNG.nextDouble() <= P_BITFLIP) {
        
        if (((ModelNRPVariable) child.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact)) {
          ((ModelNRPVariable) child.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().remove(artifact);
        } else {
          ((ModelNRPVariable) child.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().add(artifact);
        }
        
      }
      
    }
    
    return child;
  }
}
