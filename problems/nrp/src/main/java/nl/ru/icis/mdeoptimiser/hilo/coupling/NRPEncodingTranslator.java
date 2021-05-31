package nl.ru.icis.mdeoptimiser.hilo.coupling;

import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

import models.nrp.nextReleaseProblem.NRP;
import models.nrp.nextReleaseProblem.SoftwareArtifact;

public class NRPEncodingTranslator {
  HenshinResourceSet resourceSet = new HenshinResourceSet("src/main/resources/nl/ru/icis/mdeoptimiser/hilo/problems/nrp/models");
  
  private NRP originalModel;

  public NRPEncodingTranslator() {
    this.originalModel = getModel();
  }
  
  public NRP translate(boolean[] encoding) {
    NRP returnModel = getModel();
    var artifacts = originalModel.getAvailableArtifacts();
    for (SoftwareArtifact artifact : artifacts) {
      if (encoding[artifacts.indexOf(artifact)]) {
        // TODO add artifact to Solution
      }
    }
    return returnModel;
  }
  
  public int NRPArtifactsSize() {
    return originalModel.getAvailableArtifacts().size();
  }
  
  public NRP getModel() {
    return (NRP) resourceSet.getResource("nrp-model-25-cus-50-req-203-sa.xmi").getContents().get(0);
  }
}
