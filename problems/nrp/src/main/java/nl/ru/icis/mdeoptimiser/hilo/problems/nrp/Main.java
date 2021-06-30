package nl.ru.icis.mdeoptimiser.hilo.problems.nrp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.EncodingUtils;

import models.nrp.nextReleaseProblem.EcorePackage;
import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.AbstractModelNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPFactory;

public class Main {
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/nrp/models";
  private static final String MODEL_NAME = "nrp-model-25-cus-50-req-203-sa.xmi";
  
  private static HenshinResourceSet resourceSet = new HenshinResourceSet(RESOURCE_LOCATION);
  
  private static NRP model;
  
  private static boolean AJEnabled = true;
  
  public static void main( String[] args ) throws Exception {
    
//    AbstractBooleanNRP bitProblem = new AbstractBooleanNRP();
//    // Pareto front can compare using hypervolume (which has a calculation)
//    // TODO Run with actual model but random mutation
//    // TODO see about compile time weaving
//    NondominatedPopulation bitResult = new Executor().withProblem(bitProblem)
//                  .withAlgorithm("NSGAII")
//                  .withMaxEvaluations(5000)
//                  .run();
    
    AJEnabled = false;
    AbstractModelNRP modelProblem = new AbstractModelNRP(getModel());
    AlgorithmFactory factory = new AlgorithmFactory();
    factory.addProvider(new ModelNRPFactory());
    
    NondominatedPopulation modelResult = new Executor().usingAlgorithmFactory(factory)
        .withProblem(modelProblem)
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(5000)
        .run();
    
//    System.out.println(bitResult.size());
//    for (boolean b : EncodingUtils.getBinary(bitResult.get(0).getVariable(0))) {
//      System.out.print(b + ", ");
//    }
    
//    System.out.println(modelResult.size());
    // TODO print which selected
    
    
    
// // Create an initial random population of population size
//    var initialization =
//        new RandomInitialization(problem, (Integer) properties.get("populationSize"));
//
//    return new NSGAII(
//        problem,
//        new NondominatedSortingPopulation(),
//        null, // no archive
//        null, // default selection the the one built in
//        getVariation(properties),
//        getRandomInitialization(problem, properties));
  }
  
  public static NRP getModel() {
    if (model == null) {
      var metamodel = EcorePackage.eINSTANCE;
      model = (NRP) resourceSet.getResource(MODEL_NAME).getContents().get(0);
    }
    return model;
  }
  
  public static boolean AJEnabled() {
    return AJEnabled;
  }
  
  public static int NRPArtifactsSize() {
    return getModel().getAvailableArtifacts().size();
  }
}
