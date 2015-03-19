package calculator;

public class UndefinedReferenceException extends Exception {
  
  public UndefinedReferenceException(String id) {
    super("Undefined identifier: " + id);
  }
}
