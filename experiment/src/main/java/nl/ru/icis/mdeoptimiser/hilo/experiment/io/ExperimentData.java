package nl.ru.icis.mdeoptimiser.hilo.experiment.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.NondominatedPopulation.DuplicateMode;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;

import nl.ru.icis.mdeoptimiser.hilo.experiment.Batch;

public class ExperimentData {
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
      
      File file = new File(System.getProperty("user.dir") + RESULTS_FOLDER, csvName + ".txt");
      file.createNewFile();
      
      System.out.println("[INFO] Writing results to CSV: " + file.toString());
      
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write(builder.toString());
      writer.close();
      
      System.out.println("[INFO] Finished writing results to CSV: " + file.toString());
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
  
  public static NondominatedPopulation newArchive() {
    return new NondominatedPopulation(new ParetoDominanceComparator(), DuplicateMode.ALLOW_DUPLICATE_OBJECTIVES);
  }
}
