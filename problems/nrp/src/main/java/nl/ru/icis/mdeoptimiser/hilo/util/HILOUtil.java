package nl.ru.icis.mdeoptimiser.hilo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.NondominatedPopulation.DuplicateMode;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;

import models.nrp.nextReleaseProblem.NRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.Main;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.bool.AbstractBooleanNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.experiment.Batch;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.AbstractModelNRP;
import nl.ru.icis.mdeoptimiser.hilo.problems.nrp.model.ModelNRPVariable;

public class HILOUtil {
  private static final String VALUE_SEPARATOR = "|";
  
  private static final String DATA_EXTENSION = ".dat";
  
  private static final String RESULTS_FOLDER = "/results";
  
  public static void writeResultsToCSV(String csvName, Batch[][] results) {
    try {
      StringBuilder builder = new StringBuilder();
      
      for(int row = 0; row < results.length; row++) {
        for(int column = 0; column < results[row].length; column++) {
          builder.append(results[row][column].generateResults());
          if (column < results[row].length)
            builder.append(",");
        }
        builder.append("\n");
      }
      
      File file = new File(System.getProperty("user.dir"), csvName + ".txt");
      
      file.createNewFile();
      
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write(builder.toString());
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  // Expects a population containing BinaryVariables
  public static void writeBinaryVariablePopulationToFile(NondominatedPopulation population, String fileName, boolean includingObjectives) {
    try {
      StringBuilder builder = new StringBuilder();
      
      for (Solution sol : population) {
        builder.append(sol.getVariable(0).toString());
        builder.append(VALUE_SEPARATOR);
        
        if (includingObjectives) {
          for (double objective : sol.getObjectives()) {
            builder.append(objective);
            builder.append(", ");
          }
          builder.delete(builder.length()-2, builder.length());
          builder.append(VALUE_SEPARATOR);
        }
        
        
        builder.append("\n");
      }
      builder.delete(builder.length()-VALUE_SEPARATOR.length(), builder.length());
      
      File file = new File(System.getProperty("user.dir") + RESULTS_FOLDER, fileName + DATA_EXTENSION);
      file.createNewFile();
      
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write(builder.toString());
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static NondominatedPopulation readBinaryVariablePopulationFromFile(String fileName) {
    NondominatedPopulation result = newArchive();
    
    try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + RESULTS_FOLDER + "/" + fileName))) {
      String line;
      while ((line = br.readLine()) != null) {
         result.add(createSolutionFromString(line));
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return result;
  }
  
  // Assuming line is in format of 'x1 x2 .. xpopSize|' where x in {0, 1}
  private static Solution createSolutionFromString(String line) {
    BinaryVariable variable = EncodingUtils.newBinary(line.length());
    
    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) == '1') {
        variable.set(i, true);
      } else if (line.charAt(i) == '0') {
        variable.set(i, false);
      } else if (line.charAt(i) == '|') {
        break;
      } else {
        System.out.println("WARNING: unrecognized symbol parsing population from file");
      }
      
      if (i >= variable.getNumberOfBits()) {
        System.out.println("ERROR: popSize doesn't match actual size of line");
      }
    }
    
    // TODO make this not random ints
    Solution solution = new Solution(1, 2, 0);
    solution.setVariable(0,  variable);
    return solution;
  }
  
  //Assuming that the population of solutions have 1 variable with an NRP model  
  public static NondominatedPopulation convertModelToBitVector(NondominatedPopulation modelPop) {
    boolean old = Main.AJEnabled();
    Main.setAJEnabled(false);
    
    NondominatedPopulation result = newArchive();
     
    for (Solution sol : modelPop) {
      Solution toAdd = new Solution(sol.getNumberOfVariables(), sol.getNumberOfObjectives(), sol.getNumberOfConstraints());
      toAdd.setVariable(0, convertNRPToBoolArray(((ModelNRPVariable) sol.getVariable(0)).getModel()));
      
      result.add(toAdd);
      
      // Also fills in the objectives of the toAdd solution
      if (!areNRPandBoolSame(sol, toAdd)) {
        System.out.println("SEVERE ERROR: Converted solution is not the same as the original!");
        System.exit(1);
      }
    }
    
    Main.setAJEnabled(old);
    
//    System.out.print("modelPopSize: " + modelPop.size() + " ");
//    System.out.print("resultPopSize: " + result.size() + " ");
    
    return result;
  }
  
  public static BinaryVariable convertNRPToBoolArray(NRP model) {
    BinaryVariable result = new BinaryVariable(model.getAvailableArtifacts().size());
    
    for (int i = 0; i < model.getAvailableArtifacts().size(); i++) {
      if (model.getSolutions().get(0).getSelectedArtifacts().contains(model.getAvailableArtifacts().get(i))) {
        result.set(i, true);
      }
    }
    
    return result;
  }
  
  public static void evaluateBooleanSolution(Solution solution) {
    boolean old = Main.AJEnabled();
    Main.setAJEnabled(false);
    
    new AbstractBooleanNRP().evaluate(solution);
    
    Main.setAJEnabled(old);
  }
  
  public static NondominatedPopulation newArchive() {
    return new NondominatedPopulation(new ParetoDominanceComparator(), DuplicateMode.ALLOW_DUPLICATE_OBJECTIVES);
  }
  
  public static boolean areNRPandBoolSame(Solution model, Solution bitvector) {
    AbstractBooleanNRP boolProblem = new AbstractBooleanNRP();
    AbstractModelNRP modelProblem = new AbstractModelNRP(Main.getModel());
    
    boolean old = Main.AJEnabled();
    Main.setAJEnabled(true);
    boolProblem.evaluate(bitvector);
    
    Main.setAJEnabled(false);
    modelProblem.evaluate(model);
    Main.setAJEnabled(old);
    
    return (model.getObjective(0) == bitvector.getObjective(0)) && (model.getObjective(1) == bitvector.getObjective(1));
  }
}
