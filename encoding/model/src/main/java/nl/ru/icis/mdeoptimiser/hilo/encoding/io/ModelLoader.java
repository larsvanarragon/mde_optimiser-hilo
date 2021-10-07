package nl.ru.icis.mdeoptimiser.hilo.encoding.io;

import java.io.File;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class ModelLoader {

  private String resourceLocation;
  
  public ModelLoader(String resourceLocation) {
    this.resourceLocation = resourceLocation;
  }
  
  // TODO perform some simple checks and throw errors where appropriate
  public Resource loadMetaModel(String ecoreFileName) {
    File ecoreFile = new File(resourceLocation + "/" + ecoreFileName);
    ResourceSet resourceSet = new ResourceSetImpl();
    
    resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE );
    Registry registry = resourceSet.getResourceFactoryRegistry();
    
    Map<String,Object> factoryMap = registry.getExtensionToFactoryMap();
    factoryMap.put("ecore", new EcoreResourceFactoryImpl());
    URI uri = URI.createFileURI(ecoreFile.getAbsolutePath());
    
    return resourceSet.getResource(uri, true);
  }
  
  public Resource loadModelInstance(String instanceFileName, EPackage metaModelEPackage) {
    File instanceFile = new File(resourceLocation + "/" + instanceFileName);
    ResourceSet resourceSet = new ResourceSetImpl();
    
    resourceSet.getPackageRegistry().put(metaModelEPackage.getNsURI(), metaModelEPackage);
    Registry registry = resourceSet.getResourceFactoryRegistry();
    
    Map<String,Object> factoryMap = registry.getExtensionToFactoryMap();
    factoryMap.put("xmi", new XMIResourceFactoryImpl());
    URI uri = URI.createFileURI(instanceFile.getAbsolutePath());
    
    return resourceSet.getResource(uri, true);
  }
  
  public org.eclipse.emf.henshin.model.Module loadHenshinModule(String henshinFileName) {
    HenshinResourceSet resourceSet = new HenshinResourceSet(resourceLocation);
    return resourceSet.getModule(henshinFileName, false);
  }
  
  public String getResourceLocation() {
    return resourceLocation;
  }

  public void setResourceLocation(String resourceLocation) {
    this.resourceLocation = resourceLocation;
  }
}
