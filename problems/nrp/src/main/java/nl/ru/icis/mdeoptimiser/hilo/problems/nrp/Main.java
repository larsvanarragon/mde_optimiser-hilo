package nl.ru.icis.mdeoptimiser.hilo.problems.nrp;

import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

import models.nrp.nextReleaseProblem.EcorePackage;

public class Main {
  public static void main( String[] args ) {
    HenshinResourceSet resourceSet = new HenshinResourceSet("src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/nrp/models");

    var metamodel = EcorePackage.eINSTANCE;
    var model = resourceSet.getResource("nrp-model-25-cus-50-req-203-sa.xmi").getContents().get(0);
    
    System.out.println(model.toString());
  }
}
