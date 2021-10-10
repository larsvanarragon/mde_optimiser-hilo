package nl.ru.icis.mdeoptimiser.hilo.problems.cra;

import java.util.ArrayList;

import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;

import com.google.inject.Injector;
import com.google.inject.Key;

import models.cra.fitness.architectureCRA.ArchitectureCRAPackage;
import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.io.ModelLoader;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Converter;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.AbstractEncodingCRA;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.EncodingCRAFactory;
import uk.ac.kcl.inf.mdeoptimiser.languages.MoptStandaloneSetup;
import uk.ac.kcl.inf.mdeoptimiser.languages.mopt.Optimisation;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.OptimisationInterpreter;

public class Main {
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/cra";
  private static final String ECORE_FILENAME = "architectureCRA.ecore";
  private static final String MODEL_INSTANCE = "TTC_InputRDG_A.xmi";
  private static final String HENSHIN_FILENAME = "craEvolvers.henshin";
  
  private static final String MOPT_FILE = "problem {\n"
      + "  basepath <src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/cra/>\n"
      + "  metamodel <models.cra.fitness.architectureCRA.ArchitectureCRAPackage>\n"
      + "  model <TTC_InputRDG_A.xmi>\n"
      + "}\n"
      + "goal {\n"
      + "  objective CRA maximise java { \"models.cra.fitness.MaximiseCRA\" }\n"
      + "  constraint MinimiseClasslessFeatures java { \"models.cra.fitness.MinimiseClasslessFeatures\" }\n"
      + "  constraint MinimiseEmptyClasses java { \"models.cra.fitness.MinimiseEmptyClasses\" }\n"
      + "}\n"
      + "search { \n"
      + "  mutate using <craEvolvers.henshin> unit \"createClass\"\n"
      + "  mutate using <craEvolvers.henshin> unit \"assignFeature\"\n"
      + "  mutate using <craEvolvers.henshin> unit \"moveFeature\"\n"
      + "  mutate using <craEvolvers.henshin> unit \"deleteEmptyClass\"\n"
      + "}\n"
      + "solver {\n"
      + "  optimisation provider moea algorithm NSGAII {\n"
      + "    population: 40\n"
      + "    variation: mutation\n"
      + "    mutation.step: 1\n"
      + "    mutation.strategy: random\n"
      + "  }\n"
      + "  termination {\n"
      + "    evolutions: 500\n"
      + "  }\n"
      + "  batches 1\n"
      + "}";
  
  private static Injector injector = new MoptStandaloneSetup().createInjectorAndDoEMFRegistration();
  private static ParseHelper<Optimisation> parseHelper = injector.getInstance(new Key<ParseHelper<Optimisation>>() {});
  
  public static void main( String[] args ) throws Exception {
    Converter converter = new Converter(RESOURCE_LOCATION, ECORE_FILENAME, MODEL_INSTANCE, ArchitectureCRAPackage.eINSTANCE);
    Encoding encoding = converter.convert();
    ClassModel cra = (ClassModel) converter.getStructuredModelInstance().getContents().get(0);
    
//    runMDEOptimiser();
    
    ModelLoader modelLoader = new ModelLoader(RESOURCE_LOCATION);
    
    org.eclipse.emf.henshin.model.Module henshinModule = modelLoader.loadHenshinModule(HENSHIN_FILENAME);
    ArrayList<Unit> units = new ArrayList<>(henshinModule.getUnits());
    AbstractEncodingCRA encodedCRAProblem = new AbstractEncodingCRA(encoding, cra, units);
    
    AlgorithmFactory factory = new AlgorithmFactory();
    factory.addProvider(new EncodingCRAFactory());
    
    ExperimentConfig.isAspectJEnabled = true;
    
    NondominatedPopulation result = new Executor().usingAlgorithmFactory(factory)
        .withProblem(encodedCRAProblem)
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(500)
        .withProperty("populationSize", 40)
        .run();
  }
  
  private static void runMDEOptimiser() throws Exception {
    var model = parseHelper.parse(MOPT_FILE);
    
    var startTime = System.nanoTime();
    var optimisationInterpreter = new OptimisationInterpreter("", model);
    var optimisationOutcome = optimisationInterpreter.start();
    var endTime = System.nanoTime();
    
    var experimentDuration = (endTime - startTime) / 1000000;
    System.out.println(experimentDuration);
  }
}
