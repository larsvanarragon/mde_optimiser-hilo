package nl.ru.icis.mdeoptimiser.hilo.problems.nrp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;

import models.nrp.nextReleaseProblem.EcorePackage;
import models.nrp.nextReleaseProblem.NRP;
import models.nrp.nextReleaseProblem.SoftwareArtifact;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.bool.AbstractBooleanNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.Batch;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.BooleanExperiment;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.Experiment;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.ModelExperiment;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.AbstractModelNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPFactory;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPVariable;
import nl.ru.icis.mdeoptimiser.hilo.util.HILOUtil;

public class Main {
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/nrp/models";
  private static final String MODEL_NAME = "nrp-model-25-cus-50-req-203-sa.xmi";
  
  private static HenshinResourceSet resourceSet = new HenshinResourceSet(RESOURCE_LOCATION);
  
  private static boolean AJEnabled = true;
  
  private static boolean createReferencePareto = true;
  
  private static final int EVALUATIONS_START_VALUE = 4000;
  private static final int EVALUATIONS_END_VALUE = 5000;
//  private static final int EVALUATIONS_END_VALUE = 10_000;
  private static final int EVALUATIONS_INCREMENT_STEP = 1000;
  
  private static final int POPSIZE_START_VALUE = 400;
  private static final int POPSIZE_END_VALUE = 800;
//  private static final int POPSIZE_END_VALUE = 350;
  private static final int POPSIZE_INCREMENT_STEP = 400;
  
  private static final int EXPERIMENTS_PER_CYCLE = 5;
  
  // TODO this does not work if the finals don't produce the correct integer (when dividing doesn't give a whole number)
  private static Batch[][] bitResults = new Batch[calcNEvals()][calcNPopSize()];
  private static Batch[][] modelResults = new Batch[calcNEvals()][calcNPopSize()];
//  private static List<Double>[][] hypervolumes = new ArrayList[calcNEvals()][calcNPopSize()];
  
  private static SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd_hh-mm-ss");
  
  // TODO TTESTING to check the hypervolumes mannwhitneymaywillcox
  // TODO show that they are not different (effect size to show how big difference VDA)
  // TODO if different check the seeds
  
  public static void main( String[] args ) throws Exception {
    if (createReferencePareto) {
      System.out.println("Creating reference pareto front for model instance: " + MODEL_NAME);
      makeReferencePoint();
      System.exit(0);
    }
    
    for (int popsize = POPSIZE_START_VALUE; popsize <= POPSIZE_END_VALUE; popsize += POPSIZE_INCREMENT_STEP) {
      for (int evaluations = EVALUATIONS_START_VALUE; evaluations <= EVALUATIONS_END_VALUE; evaluations += EVALUATIONS_INCREMENT_STEP) {
        //logging
        System.out.println("Running 2 batches of " + EXPERIMENTS_PER_CYCLE + " experiments with popsize: " + popsize + " and evaluations: " + evaluations);
        
        runExperiments(popsize, evaluations);
        
        //logging
        System.out.println("Finished 2 batches of " + EXPERIMENTS_PER_CYCLE + " experiments with popsize: " + popsize + " and evaluations: " + evaluations);
      }
    }
    
    printResultsToCSV("bitResults", bitResults);
    printResultsToCSV("modelResults", modelResults);
//    printResultsToCSV("hypervolumes", hypervolumes);
  }
  
  private static void runExperiments(int popsize, int evaluations) {
    int evaluationIndex = calcEvalIndex(evaluations);
    int popsizeIndex = calcPopSizeIndex(popsize);
    
    Batch bitBatch = new Batch(new BooleanExperiment(getModel(), evaluations, popsize), EXPERIMENTS_PER_CYCLE);
    AJEnabled = bitBatch.requiresAJ();
    bitBatch.run();
    bitResults[evaluationIndex][popsizeIndex] = bitBatch;
    
    Batch modelBatch = new Batch(new ModelExperiment(getModel(), evaluations, popsize), EXPERIMENTS_PER_CYCLE);
    AJEnabled = modelBatch.requiresAJ();
    modelBatch.run();
    modelResults[evaluationIndex][popsizeIndex] = modelBatch;
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
  
  private static void printResultsToCSV(String csvName, Batch[][] results) throws IOException {
    StringBuilder builder = new StringBuilder();
    
    for(int row = 0; row < calcNEvals(); row++) {
      for(int column = 0; column < calcNPopSize(); column++) {
        builder.append(results[row][column].generateResults());
        if (column < results[row].length)
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
  
  private static final int REFERENCE_POPSIZE = 2000;
  private static final int REFERENCE_EVALUATIONS = 25_000;
  
//  private static final int REFERENCE_POPSIZE = 100;
//  private static final int REFERENCE_EVALUATIONS = 500;
  
  public static void makeReferencePoint() {
    Experiment boolExperiment = new BooleanExperiment(getModel(), REFERENCE_EVALUATIONS, REFERENCE_POPSIZE);
    Experiment modelExperiment = new ModelExperiment(getModel(), REFERENCE_EVALUATIONS, REFERENCE_POPSIZE);
    
    System.out.println("Running long Boolean Experiment...");
    AJEnabled = boolExperiment.requiresAJ();
    boolExperiment.run();
    
    System.out.println("Running long Model Experiment...");
    AJEnabled = modelExperiment.requiresAJ();
    modelExperiment.run();
    
    System.out.println("Combining pareto fronts...");
    
    System.out.println(boolExperiment.result().size());
    System.out.println(modelExperiment.result().size());
    
    System.out.println(convertModelToBitVector(modelExperiment.result()).size());
    Population result = convertModelToBitVector(modelExperiment.result());
    result.addAll(boolExperiment.result());
    
    System.out.println(result.size());
    
    System.out.println("Writing pareto front to file...");
    HILOUtil.writeBinaryVariablePopulationToFile(result, "combinedParetoFront" + dt.format(new Date()));
    
    System.out.println("Finished!");
  }
  
  // Assuming that the population of solutions have 1 variable with an NRP model  
  private static Population convertModelToBitVector(NondominatedPopulation modelPop) {
    Population result = new Population();
    
    for (Solution sol : modelPop) {
      Solution toAdd = new Solution(sol.getNumberOfVariables(), sol.getNumberOfObjectives(), sol.getNumberOfConstraints());
      toAdd.setVariable(0, convertNRPToBoolArray(((ModelNRPVariable) sol.getVariable(0)).getModel())); 
      result.add(toAdd);
    }
    
    return result;
  }
  
  private static BinaryVariable convertNRPToBoolArray(NRP model) {
    BinaryVariable result = new BinaryVariable(model.getAvailableArtifacts().size());
    
    for (int i = 0; i < model.getAvailableArtifacts().size(); i++) {
      if (model.getSolutions().get(0).getSelectedArtifacts().contains(model.getAvailableArtifacts().get(i))) {
        result.set(i, true);
      }
    }
    
    return result;
  }
}
