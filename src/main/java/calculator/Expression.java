package calculator;

import java.util.List;
import java.util.ArrayList;
import java.util.Deque;

public class Expression {
  // The base Expression class is instantiated only for identifiers and
  // numbers, so there's only one token.
  private Token token;

  protected Expression(Token tok) {
    token = tok;
  }

  public static Expression parse(Deque<Token> toks) throws ParseException, UndefinedReferenceException {
    Token toptok = toks.poll();
    Token.Type toptype = toptok.getType();
    String text = toptok.getText();

    if (toptype == Token.Type.NUMBER) {
      return new Expression(toptok);
    } else if (toptype == Token.Type.IDENTIFIER) {
      if (CallExpression.isFunctionName(text)) {

        List<Expression> params = parseParameters(toks);
        return new CallExpression(toptok, params);
      } else {
        return new Expression(toptok);
      }
    } else {
      throw new ParseException("Unexpected token: " + text);
    }
  }

  public static List<Expression> parseParameters(Deque<Token> toks) throws ParseException, UndefinedReferenceException {
    // Get a left paren and comma-separated parameter expressions
    // until a right paren.
    Token lp = toks.poll();
    if (lp.getType() != Token.Type.OPEN) {
      throw new ParseException("Invalid function call");
    }

    List<Expression> params = new ArrayList<Expression>();

    Expression subexp = null;
    while ((subexp = parse(toks)) != null) {
      params.add(subexp);
      Token punct = toks.poll();
      if (punct == null) {
        throw new ParseException("Unexpected end of input");
      }
      if (punct.getType() == Token.Type.CLOSE) {
        break;
      }
      if (punct.getType() != Token.Type.COMMA) {
        throw new ParseException("Unexpected token in parameter list: " + punct.getText());
      }
    }
    
    return params;
  }

  public Token getToken() {
    return token;
  }

  // Parse the expression and return its value.
  public double getValue(Scope scope) throws ParseException, UndefinedReferenceException {

    Token.Type type = token.getType();
    String text = token.getText();

    if (type == Token.Type.NUMBER) {
      return token.doubleValue();
    } else if (type == Token.Type.IDENTIFIER) {
      if (scope == null)
        throw new UndefinedReferenceException(text);
      return scope.getValue(text);
    } else {
      throw new ParseException("Unexpected token: " + text);
    }
  }

}
