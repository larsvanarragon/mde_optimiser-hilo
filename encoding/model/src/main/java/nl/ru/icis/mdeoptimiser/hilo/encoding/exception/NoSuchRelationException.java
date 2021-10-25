package nl.ru.icis.mdeoptimiser.hilo.encoding.exception;

public class NoSuchRelationException extends Exception {
  public NoSuchRelationException(String relation) {
    super("The relation: '" + relation + "' does not exist");
  }
  
  public NoSuchRelationException(String relation, String extraMessage) {
    super("The relation '" + relation + "' " + extraMessage);
  }
}
