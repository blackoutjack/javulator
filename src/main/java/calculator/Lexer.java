package calculator;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Arrays;

public class Lexer {

  private static char UPPER_BEGIN = 'A';
  private static char UPPER_END = 'Z';
  private static char LOWER_BEGIN = 'a';
  private static char LOWER_END = 'z';
  private static char NUM_BEGIN = '0';
  private static char NUM_END = '9';
  private static char DOT = '.';
  private static char LP = '(';
  private static char RP = ')';
  private static char COMMA = ',';
  // Whitespace
  private static char[] WS = { ' ', '\t', '\n', '\r' };

  static {
    // |binarySearch| expects the array to be sorted.
    Arrays.sort(WS);
  }

  public Deque<Token> tokenize(String text) throws ParseException {
    Deque tokens = new ArrayDeque<Token>();
    
    Token.Type pendtype = null;
    StringBuilder pend = null;
    for (char c : text.toCharArray()) {
      if ((c >= UPPER_BEGIN && c <= UPPER_END) || (c >= LOWER_BEGIN && c <= LOWER_END)) {
        if (pend == null) {
          pendtype = Token.Type.IDENTIFIER;
          pend = new StringBuilder();
        }
        pend.append(c);
      } else if ((c >= NUM_BEGIN && c <= NUM_END) || c == DOT) {
        if (pend == null) {
          pend = new StringBuilder();
          pendtype = Token.Type.NUMBER;
        }
        pend.append(c);
      } else {
        // All other tokens terminate the pending token.
        if (pend != null) {
          tokens.add(new Token(pendtype, pend.toString()));
          pendtype = null;
          pend = null;
        }
        if (c == LP) {
          tokens.add(new Token(Token.Type.OPEN, LP));
        } else if (c == RP) {
          tokens.add(new Token(Token.Type.CLOSE, RP));
        } else if (c == COMMA) {
          tokens.add(new Token(Token.Type.COMMA, COMMA));
        } else if (Arrays.binarySearch(WS, c) >= 0) {
          // Eat it
        } else {
          throw new ParseException("Unexpected character: '" + c + "'");
        }
      }
    }
    // The expression potentially ends with an id or number.
    if (pend != null) {
      tokens.add(new Token(pendtype, pend.toString()));
      pendtype = null;
      pend = null;
    }
    return tokens;
  }

}
