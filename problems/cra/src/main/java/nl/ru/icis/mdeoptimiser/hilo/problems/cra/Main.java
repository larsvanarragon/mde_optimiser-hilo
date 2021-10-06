package nl.ru.icis.mdeoptimiser.hilo.problems.cra;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;

import models.cra.fitness.architectureCRA.ArchitectureCRAPackage;
import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Converter;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.AbstractEncodingCRA;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.EncodingCRAFactory;

public class Main {
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/cra";
  private static final String ECORE_FILENAME = "architectureCRA.ecore";
  private static final String MODEL_INSTANCE = "TTC_InputRDG_A.xmi";
  
  public static void main( String[] args ) throws Exception {
    Converter converter = new Converter(RESOURCE_LOCATION, ECORE_FILENAME, MODEL_INSTANCE, ArchitectureCRAPackage.eINSTANCE);
    Encoding encoding = converter.convert();
    ClassModel cra = (ClassModel) converter.getStructuredModelInstance().getContents().get(0);
    
    AbstractEncodingCRA encodedCRAProblem = new AbstractEncodingCRA(encoding, cra, null);
    
    AlgorithmFactory factory = new AlgorithmFactory();
    factory.addProvider(new EncodingCRAFactory());
    
    NondominatedPopulation result = new Executor().usingAlgorithmFactory(factory)
        .withProblem(encodedCRAProblem)
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(500)
        .withProperty("populationSize", 40)
        .run();
  }
}
