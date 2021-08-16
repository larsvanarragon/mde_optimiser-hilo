package nl.ru.icis.mdeoptimiser.hilo.problems.nrp;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.moeaframework.core.NondominatedPopulation;

import models.nrp.nextReleaseProblem.EcorePackage;
import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.bool.AbstractBooleanNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.Batch;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.BooleanExperiment;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.Experiment;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.HypervolumeEvaluator;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.ModelExperiment;
import nl.ru.icis.mdeoptimiser.hilo.util.HILOUtil;

public class Main {
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/nrp/models";
  private static final String MODEL_NAME = "nrp-model-25-cus-50-req-203-sa.xmi";
  
  private static final String COMBINED_PARETO_FRONT_NAME = "cpf_2000p_25000e.dat";
  
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
  
  private static SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
  
  // TODO TTESTING to check the hypervolumes mannwhitneymaywillcox
  // TODO show that they are not different (effect size to show how big difference VDA)
  // TODO if different check the seeds
  
  private static HypervolumeEvaluator hypervolumeEvaluator = new HypervolumeEvaluator(new AbstractBooleanNRP(), COMBINED_PARETO_FRONT_NAME, true);
  
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
    
    HILOUtil.writeResultsToCSV("bitResults_" + dt.format(new Date()), bitResults);
    HILOUtil.writeResultsToCSV("modelResults_" + dt.format(new Date()), modelResults);
  }
  
  private static void runExperiments(int popsize, int evaluations) {
    int evaluationIndex = calcEvalIndex(evaluations);
    int popsizeIndex = calcPopSizeIndex(popsize);
    
    Batch bitBatch = new Batch(new BooleanExperiment(getModel(), evaluations, popsize), EXPERIMENTS_PER_CYCLE, hypervolumeEvaluator);
    AJEnabled = bitBatch.requiresAJ();
    bitBatch.run();
    bitResults[evaluationIndex][popsizeIndex] = bitBatch;
    
    Batch modelBatch = new Batch(new ModelExperiment(getModel(), evaluations, popsize), EXPERIMENTS_PER_CYCLE, hypervolumeEvaluator);
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
  
  // Getter for the original model
  public static NRP getModel() {
    var metamodel = EcorePackage.eINSTANCE;
    return (NRP) resourceSet.getResource(MODEL_NAME).getContents().get(0);
  }
  
  // Check whether AspectJ is enabled
  public static boolean AJEnabled() {
    return AJEnabled;
  }
  
  public static void setAJEnabled(boolean value) {
    AJEnabled = value;
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
    
    // START Print both results separately
    HILOUtil.writeBinaryVariablePopulationToFile(boolExperiment.result(), "boolParetoFront" + dt.format(new Date()), true);
    HILOUtil.writeBinaryVariablePopulationToFile(HILOUtil.convertModelToBitVector(modelExperiment.result()), "modelParetoFront" + dt.format(new Date()), true);
    // END 
    
    System.out.println(HILOUtil.convertModelToBitVector(modelExperiment.result()).size());
    NondominatedPopulation result = boolExperiment.result();
    boolean changed = result.addAll(HILOUtil.convertModelToBitVector(modelExperiment.result()));
//    NondominatedPopulation result = HILOUtil.convertModelToBitVector(modelExperiment.result());
//    boolean changed = result.addAll(boolExperiment.result());
    
    System.out.println(changed);
    
    System.out.println(result.size());
    
    System.out.println("Writing pareto front to file...");
    HILOUtil.writeBinaryVariablePopulationToFile(result, "combinedParetoFront" + dt.format(new Date()), true);
    
    System.out.println("Finished!");
  }
}
