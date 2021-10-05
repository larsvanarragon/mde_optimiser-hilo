package nl.ru.icis.mdeoptimiser.hilo.problems.cra;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
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

  private static HenshinResourceSet resourceSet = new HenshinResourceSet(RESOURCE_LOCATION);
  
  public static void main( String[] args ) throws Exception {
    Converter converter = new Converter(RESOURCE_LOCATION, ECORE_FILENAME, MODEL_INSTANCE);

    var metamodel = ArchitectureCRAPackage.eINSTANCE;
    Resource modelInstance = resourceSet.getResource(MODEL_INSTANCE);
    ClassModel cra = (ClassModel) modelInstance.getContents().get(0);
    
    Encoding encoding = converter.convert(cra);
    
//    for (EStructuralFeature metaRelation : cra.eClass().getEAllStructuralFeatures()) {
//      if (metaRelation instanceof EReferenceImpl) {
//        Object test = cra.eGet(metaRelation);
//        if (test instanceof EObjectContainmentEList) {
//          
//        }
//        System.out.println(test.getClass().getCanonicalName());
//        
//      }
//    }
    
    AbstractEncodingCRA encodedCRAProblem = new AbstractEncodingCRA(encoding, cra);
    
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
