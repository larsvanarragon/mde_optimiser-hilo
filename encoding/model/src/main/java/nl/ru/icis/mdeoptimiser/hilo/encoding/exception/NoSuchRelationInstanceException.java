package nl.ru.icis.mdeoptimiser.hilo.encoding.exception;

public class NoSuchRelationInstanceException extends NoSuchRelationException {
  public NoSuchRelationInstanceException(String relation, String instance) {
    super(relation, "contains no instance: '" + instance + "'");
  }
}
