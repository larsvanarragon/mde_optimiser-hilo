package nl.ru.icis.mdeoptimiser.hilo.problems.cra;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.xtext.testing.util.ParseHelper;

import com.google.inject.Injector;
import com.google.inject.Key;

import models.cra.fitness.architectureCRA.ArchitectureCRAPackage;
import models.cra.fitness.architectureCRA.ClassModel;
import nl.ru.icis.mdeoptimiser.hilo.encoding.io.ModelLoader;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Converter;
import nl.ru.icis.mdeoptimiser.hilo.experiment.Batch;
import nl.ru.icis.mdeoptimiser.hilo.experiment.config.ExperimentConfig;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.experiment.EncodingExperiment;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.experiment.MDEOExperiment;
import uk.ac.kcl.inf.mdeoptimiser.languages.MoptStandaloneSetup;
import uk.ac.kcl.inf.mdeoptimiser.languages.mopt.Optimisation;

public class Main {
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/cra";
  private static final String ECORE_FILENAME = "architectureCRA.ecore";
  private static final String MODEL_INSTANCE = "TTC_InputRDG_C.xmi";
  private static final String HENSHIN_FILENAME = "craEvolvers.henshin";
  
  private static final Integer BATCH_SIZE = 50;
  
  private static SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
  
  private static Injector injector = new MoptStandaloneSetup().createInjectorAndDoEMFRegistration();
  private static ParseHelper<Optimisation> parseHelper = injector.getInstance(new Key<ParseHelper<Optimisation>>() {});
  
  public static void main( String[] args ) throws Exception {
    Converter converter = new Converter(RESOURCE_LOCATION, ECORE_FILENAME, MODEL_INSTANCE, ArchitectureCRAPackage.eINSTANCE);
    ClassModel cra = (ClassModel) converter.getStructuredModelInstance().getContents().get(0);
    
    ModelLoader modelLoader = new ModelLoader(RESOURCE_LOCATION);
    
    org.eclipse.emf.henshin.model.Module henshinModule = modelLoader.loadHenshinModule(HENSHIN_FILENAME);
    ArrayList<Unit> units = new ArrayList<>(henshinModule.getUnits());
    
    EncodingExperiment encodedExperiment = new EncodingExperiment(cra, converter, units);
    
    //runENC
    File encFile = new File(System.getProperty("user.dir") + "/results", "encodingResults" + dt.format(new Date()) + ".txt");
    encFile.createNewFile();
    BufferedWriter encWriter = new BufferedWriter(new FileWriter(encFile, true));
    encWriter.append("ModelInstance: " + MODEL_INSTANCE + "\n");
    encWriter.close();

    Batch encBatch = new Batch(encodedExperiment, BATCH_SIZE, encFile);
    ExperimentConfig.isAspectJEnabled = encBatch.requiresAJ();
    encBatch.run();
    
    //runMDE
    File mdeFile = new File(System.getProperty("user.dir") + "/results", "mdeoResults" + dt.format(new Date()) + ".txt");
    mdeFile.createNewFile();
    BufferedWriter mdeWriter = new BufferedWriter(new FileWriter(mdeFile, true));
    mdeWriter.append("ModelInstance: " + MODEL_INSTANCE + "\n");
    mdeWriter.close();
    
    ExperimentConfig.isAspectJEnabled = false;
    MDEOExperiment mdeoExperiment = new MDEOExperiment(parseHelper, 500, 40, MODEL_INSTANCE);
    Batch mdeBatch = new Batch(mdeoExperiment, BATCH_SIZE, mdeFile);
    mdeBatch.run();
  }
}
