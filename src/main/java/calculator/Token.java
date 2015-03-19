package calculator;

public class Token {
  
  public enum Type {
    OPEN,
    CLOSE,
    COMMA,
    IDENTIFIER,
    NUMBER,
  }

  private Type type;
  private String text;

  public Token(Type t, String txt) {
    type = t;
    text = txt;
  }

  public Token(Type t, char c) {
    type = t;
    text = new StringBuilder().append(c).toString();
  }

  public String getText() {
    return text;
  }

  public Type getType() {
    return type;
  }

  public double doubleValue() {
    // Normally this should throw an exception, but with the limited
    // usage, it's easy to see that this is satisfied.
    assert type == Type.NUMBER
        : "Attempting to get the double value of a non-numeric token";
    // NumberFormatException is a RuntimeException, so catching it isn't
    // required. As with the assertion above, proper formatting is
    // ensured elsewhere.
    return Double.parseDouble(text);
  }

}
