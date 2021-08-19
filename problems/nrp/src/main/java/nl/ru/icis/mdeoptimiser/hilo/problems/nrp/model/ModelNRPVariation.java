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
          (parent0Model.getSolutions().get(0).getSelectedArtifacts().contains(parent0Artifact) !=
          parent1Model.getSolutions().get(0).getSelectedArtifacts().contains(parent1Artifact)) &&
          PRNG.nextBoolean()
          ) {
        
//        System.out.print("(");
        if (child0Model.getSolutions().get(0).getSelectedArtifacts().contains(child0Artifact)) {
//          System.out.print("remove0 ");
          child0Model.getSolutions().get(0).getSelectedArtifacts().remove(child0Artifact);
        } else {
//          System.out.print("add0 ");
          child0Model.getSolutions().get(0).getSelectedArtifacts().add(child0Artifact);
        }
        
        if (child1Model.getSolutions().get(0).getSelectedArtifacts().contains(child1Artifact)) {
//          System.out.print("remove1");
          child1Model.getSolutions().get(0).getSelectedArtifacts().remove(child1Artifact);
        } else {
//          System.out.print("add1");
          child1Model.getSolutions().get(0).getSelectedArtifacts().add(child1Artifact);
        }
//        System.out.print(")");
      }
    }
    
//    for (SoftwareArtifact artifact : ((ModelNRPVariable) parents[0].getVariable(0)).getModel().getAvailableArtifacts()) {
//      if (
//          (((ModelNRPVariable) parents[0].getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact) != 
//          ((ModelNRPVariable) parents[1].getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact)) &&
//          PRNG.nextBoolean()
//          ) {
//        
//        if (((ModelNRPVariable) p1.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact)) {
//          ((ModelNRPVariable) p1.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().remove(artifact);
//        } else {
//          ((ModelNRPVariable) p1.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().add(artifact);
//        }
//        
//        if (((ModelNRPVariable) p2.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact)) {
//          ((ModelNRPVariable) p2.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().remove(artifact);
//        } else {
//          ((ModelNRPVariable) p2.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().add(artifact);
//        }
//      }
//    }
    
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
    
//    for (SoftwareArtifact artifact : ((ModelNRPVariable) child.getVariable(0)).getModel().getAvailableArtifacts()) {
//      
//      if (PRNG.nextDouble() <= P_BITFLIP) {
//        
//        if (((ModelNRPVariable) child.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().contains(artifact)) {
//          ((ModelNRPVariable) child.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().remove(artifact);
//        } else {
//          ((ModelNRPVariable) child.getVariable(0)).getModel().getSolutions().get(0).getSelectedArtifacts().add(artifact);
//        }
//        
//      }
//      
//    }
//    
    return child;
  }
}
