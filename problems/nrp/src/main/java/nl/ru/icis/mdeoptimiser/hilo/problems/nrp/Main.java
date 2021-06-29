package nl.ru.icis.mdeoptimiser.hilo.problems.nrp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.variable.EncodingUtils;

import models.nrp.nextReleaseProblem.EcorePackage;
import models.nrp.nextReleaseProblem.NRP;

public class Main {
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/nrp/models";
  private static final String MODEL_NAME = "nrp-model-25-cus-50-req-203-sa.xmi";
  
  private static HenshinResourceSet resourceSet = new HenshinResourceSet(RESOURCE_LOCATION);
  
  private static NRP model;
  
  public static void main( String[] args ) throws Exception {
    
    AbstractBooleanNRP problem = new AbstractBooleanNRP();
    // Pareto front can compare using hypervolume (which has a calculation)
    // TODO Run with actual model but random mutation
    // TODO see about compile time weaving
    NondominatedPopulation result = new Executor().withProblem(problem)
                  .withAlgorithm("NSGAII")
                  .withMaxEvaluations(5000)
                  .run();
    
    AbstractModelNRP moeaProblem = new AbstractModelNRP();
    
    System.out.println(result.size());
    for (boolean b : EncodingUtils.getBinary(result.get(0).getVariable(0))) {
      System.out.print(b + ", ");
    }
  }
  
  public static NRP getModel() {
    if (model == null) {
      var metamodel = EcorePackage.eINSTANCE;
      model = (NRP) resourceSet.getResource(MODEL_NAME).getContents().get(0);
    }
    return model;
  }
  
  public static int NRPArtifactsSize() {
    return getModel().getAvailableArtifacts().size();
  }
}
