package nl.ru.icis.mdeoptimiser.hilo.coupling;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import models.nrp.nextReleaseProblem.NRP;
import models.nrp.nextReleaseProblem.SoftwareArtifact;
import models.nrp.nextReleaseProblem.Solution;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.Main;

public aspect NRPCoupler {

  private static NRP model = Main.getModel();
  
  pointcut getSelectedArtifacts(): call(EList<SoftwareArtifact> models.nrp.nextReleaseProblem.Solution.getSelectedArtifacts());

  EList<SoftwareArtifact> around(): getSelectedArtifacts() {
    if (!Main.AJEnabled()) {
      return proceed();
    }
    
    EList<SoftwareArtifact> returnList = new BasicEList<SoftwareArtifact>();
    
    for(int i = 0; i < NRPCoupleData.getRelation(NRPCoupleData.SOLUTION_RELATION).length; i++) {
      if (NRPCoupleData.getRelation(NRPCoupleData.SOLUTION_RELATION)[i]) {
        returnList.add(model.getAvailableArtifacts().get(i));
      }
    }
    
    return returnList;
  }
  
  pointcut getSolutions(SoftwareArtifact sa): call(EList<Solution> models.nrp.nextReleaseProblem.SoftwareArtifact.getSolutions()) && this(sa);
  
  EList<Solution> around(SoftwareArtifact sa): getSolutions(sa) {
    if (!Main.AJEnabled()) {
      return proceed(sa);
    }
    
    EList<Solution> returnList = new BasicEList<Solution>();
    
    if(NRPCoupleData.getRelation(NRPCoupleData.SOLUTION_RELATION)[model.getAvailableArtifacts().indexOf(sa)]) {
      returnList.add(model.getSolutions().get(0));
    }
    
    return returnList;
  }
  
//  pointcut getSolutions(): call(EList<Solution> models.nrp.nextReleaseProblem.NRP.getSolutions()) && !within(org.eclipse.emf..*);
//  
//  EList<Solution> around(): getSolutions() {
//    EList<Solution> returnList = new BasicEList<Solution>();
//    Solution returnSol = factory.createSolution();
//    
//    System.out.println("Intercepted!");
//    
//    for(int i = 0; i < NRPCoupleData.getRelation(NRPCoupleData.SOLUTION_RELATION).length; i++) {
//      if (NRPCoupleData.getRelation(NRPCoupleData.SOLUTION_RELATION)[i]) {
//        returnSol.getSelectedArtifacts().add(model.getAvailableArtifacts().get(i));
//      }
//    }
//      
//    return returnList;
//  }
  
//  pointcut getSolutions(): call(EList<Solution> models.nrp.nextReleaseProblem.SoftwareArtifact.getSolutions());
}
