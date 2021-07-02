package nl.ru.icis.mdeoptimiser.hilo.problems.nrp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;

import models.nrp.nextReleaseProblem.EcorePackage;
import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.bool.AbstractBooleanNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.BooleanExperiment;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.Experiment;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.ModelExperiment;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.AbstractModelNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPFactory;

public class Main {
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/nrp/models";
  private static final String MODEL_NAME = "nrp-model-25-cus-50-req-203-sa.xmi";
  
  private static HenshinResourceSet resourceSet = new HenshinResourceSet(RESOURCE_LOCATION);
  
  private static boolean AJEnabled = true;
  
  private static final int EVALUATIONS_START_VALUE = 100;
  private static final int EVALUATIONS_END_VALUE = 300;
//  private static final int EVALUATIONS_END_VALUE = 10_000;
  private static final int EVALUATIONS_INCREMENT_STEP = 100;
  
  private static final int POPSIZE_START_VALUE = 50;
  private static final int POPSIZE_END_VALUE = 100;
//  private static final int POPSIZE_END_VALUE = 400;
  private static final int POPSIZE_INCREMENT_STEP = 50;
  
  private static final int EXPERIMENTS_PER_CYCLE = 20;
  
  // TODO this does not work if the finals don't produce the correct integer (when dividing doesn't give a whole number)
  private static long[][] bitResults = new long[calcNEvals()]
      [calcNPopSize()];
  private static long[][] modelResults = new long[calcNEvals()]
      [calcNPopSize()];
  
  private static SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd_hh-mm-ss");
  
  public static void main( String[] args ) throws Exception {
    for (int popsize = POPSIZE_START_VALUE; popsize <= POPSIZE_END_VALUE; popsize += POPSIZE_INCREMENT_STEP) {
      for (int evaluations = EVALUATIONS_START_VALUE; evaluations <= EVALUATIONS_END_VALUE; evaluations += EVALUATIONS_INCREMENT_STEP) {
        //logging
        System.out.println("Running " + EXPERIMENTS_PER_CYCLE + " experiments with popsize: " + popsize + " and evaluations: " + evaluations);
        
        runExperiments(popsize, evaluations);
        
        //logging
        System.out.println("Finished " + EXPERIMENTS_PER_CYCLE + " experiments with popsize: " + popsize + " and evaluations: " + evaluations);
      }
    }
    
    printResultsToCSV("bitResults", bitResults);
    printResultsToCSV("modelResults", modelResults);
  }
  
  private static void runExperiments(int popsize, int evaluations) {
    List<Long> bitResultsList = new ArrayList<>();
    List<Long> modelResultsList = new ArrayList<>();
    
    //logging
    System.out.print("Working on: ");
    for (int experiment = 0; experiment < EXPERIMENTS_PER_CYCLE; experiment++) {
    //logging
      System.out.print(experiment + " ");
      bitResultsList.add(doBitExperiment(popsize, evaluations, experiment));
      modelResultsList.add(doModelExperiment(popsize, evaluations, experiment));      
    }
    //logging
    System.out.println("finished!");
    
    int evaluationIndex = calcEvalIndex(evaluations);
    int popsizeIndex = calcPopSizeIndex(popsize);
    
    bitResults[evaluationIndex][popsizeIndex] = averageOfLongList(bitResultsList);
    modelResults[evaluationIndex][popsizeIndex] = averageOfLongList(modelResultsList);
  }
  
  private static long doBitExperiment(int popsize, int evaluations, int experimentNumber) {
    Experiment bitExperiment = new BooleanExperiment(getModel(), evaluations, popsize);
    AJEnabled = bitExperiment.requiresAJ();
    return bitExperiment.run();
  }
  
  private static long doModelExperiment(int popsize, int evaluations, int experimentNumber) {
    Experiment modelExperiment = new ModelExperiment(getModel(), evaluations, popsize);
    AJEnabled = modelExperiment.requiresAJ();
    return modelExperiment.run();
  }
  
  // START INDEX CALCULATION
  private static int calcEvalIndex(int evaluations) {
    if (evaluations - EVALUATIONS_START_VALUE == 0) {
      return 0;
    }
    return ((EVALUATIONS_END_VALUE - EVALUATIONS_START_VALUE)/EVALUATIONS_INCREMENT_STEP) -
        ((EVALUATIONS_END_VALUE - evaluations)/EVALUATIONS_INCREMENT_STEP);
  }
  
  private static int calcPopSizeIndex(int popsize) {
    if (popsize - POPSIZE_START_VALUE == 0) {
      return 0;
    }
    return ((POPSIZE_END_VALUE - POPSIZE_START_VALUE)/POPSIZE_INCREMENT_STEP) -
        ((POPSIZE_END_VALUE - popsize)/POPSIZE_INCREMENT_STEP);
  }

  private static int calcNEvals() {
    if (EVALUATIONS_START_VALUE == EVALUATIONS_END_VALUE) {
      return 1;
    }
    return ((EVALUATIONS_END_VALUE - EVALUATIONS_START_VALUE)/EVALUATIONS_INCREMENT_STEP) + 1;
  }
  
  private static int calcNPopSize() {
    if (POPSIZE_START_VALUE == POPSIZE_END_VALUE) {
      return 1;
    }
    return ((POPSIZE_END_VALUE - POPSIZE_START_VALUE)/POPSIZE_INCREMENT_STEP) + 1;
  }
  // END INDEX CALCULATION
  
  private static long averageOfLongList(List<Long> list) {
    BigInteger sum = BigInteger.valueOf(0);
    
    for (Long n : list) {
      sum = sum.add(BigInteger.valueOf(n));
    }
    
    return sum.divide(BigInteger.valueOf(list.size())).longValue();
  }
  
  private static void printResultsToCSV(String csvName, long[][] results) throws IOException {
    StringBuilder builder = new StringBuilder();
    
    for(int row = 0; row < calcNEvals(); row++) {
      for(int column = 0; column < calcNPopSize(); column++) {
        builder.append(results[row][column]);
        if (column < results.length)
          builder.append(",");
      }
      builder.append("\n");
    }
    
    File file = new File(System.getProperty("user.dir"), csvName + "_" + dt.format(new Date()) + ".txt");
    file.createNewFile();
    
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.write(builder.toString());
    writer.close();
  }
  
  private static void old() {
    AbstractBooleanNRP bitProblem = new AbstractBooleanNRP();
    // Pareto front can compare using hypervolume (which has a calculation)
    // TODO Run with actual model but random mutation
    // TODO see about compile time weaving
    
    long startTimeBits = System.nanoTime();
    NondominatedPopulation bitResult = new Executor().withProblem(bitProblem)
                  .withAlgorithm("NSGAII")
                  .withMaxEvaluations(1000)
                  .run();
    long endTimeBits = System.nanoTime() - startTimeBits;
    
    AJEnabled = false;
    AbstractModelNRP modelProblem = new AbstractModelNRP(getModel());
    AlgorithmFactory factory = new AlgorithmFactory();
    factory.addProvider(new ModelNRPFactory());
    
    long startTimeModel = System.nanoTime();
    NondominatedPopulation modelResult = new Executor().usingAlgorithmFactory(factory)
        .withProblem(modelProblem)
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(1000)
        .run();
    long endTimeModel = System.nanoTime() - startTimeModel;
    
    System.out.println(endTimeBits);
    System.out.println(endTimeModel);
    
    // TODO PRINT PROPERLY
//    System.out.println(bitResult.size());
//    for (boolean b : EncodingUtils.getBinary(bitResult.get(0).getVariable(0))) {
//      System.out.print(b + ", ");
//    }
    
//    System.out.println(modelResult.size());
    // TODO print which selected
  }
  
  // Getter for the original model
  public static NRP getModel() {
    var metamodel = EcorePackage.eINSTANCE;
    return (NRP) resourceSet.getResource(MODEL_NAME).getContents().get(0);
  }
  
  // Check whether AspectJ is enabled
  public static boolean AJEnabled() {
    return AJEnabled;
  }
  
  // How many artifacts are available in the original model
  public static int NRPArtifactsSize() {
    return getModel().getAvailableArtifacts().size();
  }
}
