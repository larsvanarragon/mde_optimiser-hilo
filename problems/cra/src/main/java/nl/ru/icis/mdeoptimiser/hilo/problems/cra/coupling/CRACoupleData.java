package nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling;

import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;

public class CRACoupleData {
  private static Encoding encoding;
  
  public static final String CLASSMODEL_TO_CLASS_RELATION = "classesarchitectureCRAClassModelarchitectureCRAClass";
  public static final String CLASSMODEL_TO_FEATURE_RELATION = "featuresarchitectureCRAClassModelarchitectureCRAFeature";
  //isEncapsulatedByarchitectureCRAAttributearchitectureCRAClass
  //encapsulatesarchitectureCRAClassarchitectureCRAFeature
  //functionalDependencyarchitectureCRAMethodarchitectureCRAMethod
  //isEncapsulatedByarchitectureCRAMethodarchitectureCRAClass
  //dataDependencyarchitectureCRAMethodarchitectureCRAAttribute
  //isEncapsulatedByarchitectureCRAFeaturearchitectureCRAClass
  
  public static Encoding getCurrentEncoding() {
    return encoding;
  }
  
  public static void setCurrentEncoding(Encoding newEncoding) {
    encoding = newEncoding;
  }
}
