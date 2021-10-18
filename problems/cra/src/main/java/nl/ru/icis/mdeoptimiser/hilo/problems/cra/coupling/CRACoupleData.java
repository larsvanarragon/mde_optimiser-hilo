package nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling;

import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;

public class CRACoupleData {
  private static Encoding encoding;
  
  public static final String CLASSMODEL_TO_CLASS_RELATION = "classesarchitectureCRAClassModelarchitectureCRAClass";
  public static final String CLASSMODEL_TO_FEATURE_RELATION = "featuresarchitectureCRAClassModelarchitectureCRAFeature";
  public static final String CLASS_TO_FEATURE_RELATION = "encapsulatesarchitectureCRAClassarchitectureCRAFeature";
  public static final String METHOD_TO_ATTRIBUTE_FD_RELATION = "functionalDependencyarchitectureCRAMethodarchitectureCRAMethod";
  public static final String METHOD_TO_ATTRIBUTE_DD_RELATION = "dataDependencyarchitectureCRAMethodarchitectureCRAAttribute";
  public static final String FEATURE_TO_CLASS_RELATION = "isEncapsulatedByarchitectureCRAFeaturearchitectureCRAClass";
  
  public static Encoding getCurrentEncoding() {
    return encoding;
  }
  
  public static void setCurrentEncoding(Encoding newEncoding) {
    encoding = newEncoding;
  }
}
