package nl.ru.icis.mdeoptimiser.hilo.encoding.model;

public class Main {
  // TODO Comment
  private static final String RESOURCE_LOCATION = "src/main/resources/nl/ru/icis/mdeoptimiser/hilo/encoding/model";
  private static final String ECORE_FILENAME = "nextReleaseProblem.ecore";
  private static final String MODEL_INSTANCE = "nrp-model-25-cus-50-req-203-sa.xmi";
  
  public static void main( String[] args ) throws Exception {
    Converter converter = new Converter(RESOURCE_LOCATION, ECORE_FILENAME, MODEL_INSTANCE);
    
//    Encoding encoding = converter.convert();
    System.out.println("Test");
  }
}
