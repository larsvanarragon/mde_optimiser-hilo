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
    Solution c0 = parents[0].copy();
    Solution c1 = parents[1].copy();
    
    if (!(PRNG.nextDouble() <= P_HUX)) {
      return parents;
    }
    
    var parent0Model = ((ModelNRPVariable) parents[0].getVariable(0)).getModel();
    var parent1Model = ((ModelNRPVariable) parents[1].getVariable(0)).getModel();
    
    var child0Model = ((ModelNRPVariable) c0.getVariable(0)).getModel();
    var child1Model = ((ModelNRPVariable) c1.getVariable(0)).getModel();
    
    for (int i = 0; i < parent0Model.getAvailableArtifacts().size(); i++) {
      var parent0Artifact = parent0Model.getAvailableArtifacts().get(i);
      var parent1Artifact = parent1Model.getAvailableArtifacts().get(i);

      var child0Artifact = child0Model.getAvailableArtifacts().get(i);
      var child1Artifact = child1Model.getAvailableArtifacts().get(i);
      
      if (
          (parent0Artifact.getSolutions().isEmpty() != parent1Artifact.getSolutions().isEmpty()) &&
          PRNG.nextBoolean()
          ) {
        
        if (child0Model.getSolutions().get(0).getSelectedArtifacts().contains(child0Artifact)) {
          child0Model.getSolutions().get(0).getSelectedArtifacts().remove(child0Artifact);
        } else {
          child0Model.getSolutions().get(0).getSelectedArtifacts().add(child0Artifact);
        }
        
        if (child1Model.getSolutions().get(0).getSelectedArtifacts().contains(child1Artifact)) {
          child1Model.getSolutions().get(0).getSelectedArtifacts().remove(child1Artifact);
        } else {
          child1Model.getSolutions().get(0).getSelectedArtifacts().add(child1Artifact);
        }
      }
    }
    
    return new Solution[] {c0, c1};
  }

  private Solution simulateBitFlipVariation(Solution parent) {
    Solution child = parent.copy();
    
    var childModel = ((ModelNRPVariable) child.getVariable(0)).getModel();
    
    for (int i = 0; i < childModel.getAvailableArtifacts().size(); i++) {
      var childArtifact = childModel.getAvailableArtifacts().get(i);
      
      if (PRNG.nextDouble() <= P_BITFLIP) {
        
        if (childModel.getSolutions().get(0).getSelectedArtifacts().contains(childArtifact)) {
          childModel.getSolutions().get(0).getSelectedArtifacts().remove(childArtifact);
        } else {
          childModel.getSolutions().get(0).getSelectedArtifacts().add(childArtifact);
        }
        
      }
    }
    
    return child;
  }
}
