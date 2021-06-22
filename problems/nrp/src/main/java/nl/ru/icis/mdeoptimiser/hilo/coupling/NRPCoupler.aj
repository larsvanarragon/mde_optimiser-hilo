package nl.ru.icis.mdeoptimiser.hilo.coupling;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.moeaframework.core.variable.EncodingUtils;

import models.nrp.nextReleaseProblem.EcoreFactory;
import models.nrp.nextReleaseProblem.NRP;
import models.nrp.nextReleaseProblem.Solution;
import models.nrp.nextReleaseProblem.impl.EcoreFactoryImpl;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.AbstractNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.Main;

public aspect NRPCoupler {

  private static NRP model = Main.getModel();
  
  private static boolean[] currentNRPSolution;
  
  private static EcoreFactory factory = new EcoreFactoryImpl();
  
  pointcut getSolutions(): call(EList<Solution> models.nrp.nextReleaseProblem.NRP.getSolutions());
  
  EList<Solution> around(): getSolutions() {
    EList<Solution> returnList = new BasicEList<Solution>();
    Solution returnSol = factory.createSolution();
    
    System.out.println("Intercepted!");
//    
    for(int i = 0; i < currentNRPSolution.length; i++) {
      if (currentNRPSolution[i]) {
        returnSol.getSelectedArtifacts().add(model.getAvailableArtifacts().get(i));
      }
    }
      
    return returnList;
  }
  
//  pointcut getSelectedArtifacts(): call(EList<SoftwareArtifact> models.nrp.nextReleaseProblem.Solution.getSelectedArtifacts());
//
//  EList<SoftwareArtifact> around(): getSelectedArtifacts() {
//    EList<SoftwareArtifact> returnList = new BasicEList<SoftwareArtifact>();
//    
//    System.out.println("Intercepted!");
//    
//    for(int i = 0; i < currentNRPSolution.length; i++) {
//      if (currentNRPSolution[i]) {
//        returnList.add(model.getAvailableArtifacts().get(i));
//      }
//    }
//    
//    return returnList;
//  }
  
//  pointcut getSolutions(): call(EList<Solution> models.nrp.nextReleaseProblem.SoftwareArtifact.getSolutions());
}
