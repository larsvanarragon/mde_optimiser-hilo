package nl.ru.icis.mdeoptimiser.hilo.problems.nrp.coupling;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import models.nrp.nextReleaseProblem.NRP;
import models.nrp.nextReleaseProblem.SoftwareArtifact;
import models.nrp.nextReleaseProblem.Solution;
import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.Main;

public aspect NRPCoupler {

  private static NRP model = Main.getModel();
  
  pointcut getSelectedArtifacts(): call(EList<SoftwareArtifact> models.nrp.nextReleaseProblem.Solution.getSelectedArtifacts());

  EList<SoftwareArtifact> around(): getSelectedArtifacts() {
    if (!ExperimentConfig.isAspectJEnabled) {
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
    if (!ExperimentConfig.isAspectJEnabled) {
      return proceed(sa);
    }
    
    EList<Solution> returnList = new BasicEList<Solution>();
    
    if(NRPCoupleData.getRelation(NRPCoupleData.SOLUTION_RELATION)[model.getAvailableArtifacts().indexOf(sa)]) {
      returnList.add(model.getSolutions().get(0));
    }
    
    return returnList;
  }
}
