package nl.ru.icis.mdeoptimiser.hilo.problems.cra;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.algorithm.PeriodicAction;
import org.moeaframework.analysis.collector.ApproximationSetCollector;
import org.moeaframework.analysis.collector.Collector;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.termination.MaxFunctionEvaluations;

import com.google.inject.Injector;
import com.google.inject.Key;

import models.cra.fitness.architectureCRA.ArchitectureCRAPackage;
import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.io.ModelLoader;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Converter;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
import nl.ru.icis.mdeoptimiser.hilo.experiment.Batch;
import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.AbstractEncodingCRA;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding.EncodingCRAFactory;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.experiment.EncodingExperiment;
import uk.ac.kcl.inf.mdeoptimiser.languages.MoptStandaloneSetup;
import uk.ac.kcl.inf.mdeoptimiser.languages.mopt.Optimisation;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.OptimisationInterpreter;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.henshin.MdeoRuleApplicationImpl;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.moea.instrumentation.PopulationCollector;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.moea.problem.MoeaOptimisationProblem;

public class Main {
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/cra";
  private static final String ECORE_FILENAME = "architectureCRA.ecore";
  private static final String MODEL_INSTANCE = "TTC_InputRDG_E.xmi";
  private static final String HENSHIN_FILENAME = "craEvolvers.henshin";
  
  private static SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
  
  private static final String MOPT_FILE = "problem {\n"
      + "  basepath <src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/cra/>\n"
      + "  metamodel <models.cra.fitness.architectureCRA.ArchitectureCRAPackage>\n"
      + "  model <TTC_InputRDG_E.xmi>\n"
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
    
    ModelLoader modelLoader = new ModelLoader(RESOURCE_LOCATION);
    
    org.eclipse.emf.henshin.model.Module henshinModule = modelLoader.loadHenshinModule(HENSHIN_FILENAME);
    ArrayList<Unit> units = new ArrayList<>(henshinModule.getUnits());
    
    EncodingExperiment encodedExperiment = new EncodingExperiment(cra, encoding, units);
    
    File file = new File(System.getProperty("user.dir") + "/results", "encodingResults" + dt.format(new Date()) + ".txt");
    file.createNewFile();
    BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
    writer.append("ModelInstance: " + MODEL_INSTANCE + "\n");
    writer.close();
    
    Batch batch = new Batch(encodedExperiment, 30, file);
    ExperimentConfig.isAspectJEnabled = batch.requiresAJ();
    batch.run();
//    runMDEOptimiser();
//    System.out.println("BEST:" + AbstractEncodingCRA.bestObjective);
  }
  
  private static void runMDEOptimiser() throws Exception {
    ExperimentConfig.isAspectJEnabled = false;
    var model = parseHelper.parse(MOPT_FILE);
    
    var startTime = System.nanoTime();
    var optimisationInterpreter = new OptimisationInterpreter("", model);
    var optimisationOutcome = optimisationInterpreter.start();
    var endTime = System.nanoTime();
    
    var experimentDuration = (endTime - startTime) / 1000000;
    System.out.println(experimentDuration);
  }
}
