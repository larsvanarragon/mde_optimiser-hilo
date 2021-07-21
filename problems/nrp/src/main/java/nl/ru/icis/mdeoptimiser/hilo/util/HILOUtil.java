package nl.ru.icis.mdeoptimiser.hilo.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

public class HILOUtil {
  private static final String VALUE_SEPARATOR = "|";
  
  private static final String DATA_EXTENSION = ".dat";
  
  private static final String RESULTS_FOLDER = "/results";
  
  public static void writeResultsToCSV() {
    
  }
  
  // Expects a population containing BinaryVariables
  public static void writeBinaryVariablePopulationToFile(Population population, String fileName) {
    try {
      StringBuilder builder = new StringBuilder();
      
      for (Solution sol : population) {
        builder.append(sol.getVariable(0).toString());
        builder.append(VALUE_SEPARATOR);
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
}
