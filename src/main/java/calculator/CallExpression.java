package calculator;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CallExpression extends Expression {

  private String identifier;
  private List<Expression> parameters;

  // Map of function name to arity.
  private static Map<String,Integer> functions;
  static {
    functions = new HashMap<String,Integer>();
    functions.put("add", new Integer(2));
    functions.put("sub", new Integer(2));
    functions.put("mult", new Integer(2));
    functions.put("div", new Integer(2));
    functions.put("let", new Integer(3));
  }

  public CallExpression(Token id, List<Expression> params) throws ParseException {
    super(id);
    assert id.getType() == Token.Type.IDENTIFIER;
    identifier = id.getText().toLowerCase();
    assert isFunctionName(identifier);
    parameters = params;
    checkArity();
  }

  public void checkArity() throws ParseException {
    int arity = functions.get(identifier);
    if (arity != parameters.size()) {
      throw new ParseException("Wrong number of parameters to " + identifier);
    }
  }

  protected static boolean isFunctionName(String txt) {
    txt = txt.toLowerCase();
    return functions.keySet().contains(txt);
  }

  @Override
  public double getValue(Scope scope) throws ParseException, UndefinedReferenceException {
    if (identifier.equals("add")) {
      return parameters.get(0).getValue(scope) + parameters.get(1).getValue(scope);
    } else if (identifier.equals("sub")) {
      return parameters.get(0).getValue(scope) - parameters.get(1).getValue(scope);
    } else if (identifier.equals("mult")) {
      return parameters.get(0).getValue(scope) * parameters.get(1).getValue(scope);
    } else if (identifier.equals("div")) {
      return parameters.get(0).getValue(scope) / parameters.get(1).getValue(scope);
    } else if (identifier.equals("let")) {
      Expression id = parameters.get(0);
      // %%% Refactoring needed for better type retrieval.
      if (id instanceof CallExpression || id.getToken().getType() != Token.Type.IDENTIFIER) {
        throw new ParseException("Only identifiers are allowed as the first argument to let");
      }
      String name = id.getToken().getText();
      Expression val = parameters.get(1);
      Expression exp = parameters.get(2);
      // Create the extended scope and get the value of the expression.
      Scope newScope = new Scope(scope, name, val);
      return exp.getValue(newScope);
    }

    assert false : "Malconstructed function call: " + identifier;
    return 0.0;
  }
}
