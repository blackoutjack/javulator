package calculator;

public class Scope {
  private Scope parent;
  private String identifier;
  private Expression exp;

  public Scope(Scope par, String id, Expression e) {
    parent = par;
    identifier = id;
    exp = e;
  }

  public double getValue(String id) throws ParseException, UndefinedReferenceException {
    if (identifier.equals(id)) {
      return exp.getValue(parent);
    }
    if (parent != null) {
      return parent.getValue(id);
    }
    throw new UndefinedReferenceException(id);
  }

}
