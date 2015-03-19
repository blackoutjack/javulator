package calculator;

import java.util.List;
import java.util.ArrayList;
import java.util.Deque;

public class Main {

  private static Logger logger;

  /**
   * Set configuration based on command-line options.
   * @param args All arguments passed from the command line.
   * @return A list of non-option arguments.
   */
  protected static List<String> processOptions(String[] argsAndOpts) {
    // Normally I'd use a library such as Args4J for command-line
    // option setup, but I'll roll my own to avoid dependencies.
    List<String> args = new ArrayList<String>();
    String logfile = null;
    Logger.Level loglevel = null;
    for (int i=0; i<argsAndOpts.length; i++) {
      String a = argsAndOpts[i];
      if (a.startsWith("-")) {
        String opt = a.substring(1);
        if (opt.equals("l")) {
          i++;
          if (i < argsAndOpts.length) {
            // Log level value is case-insensitive.
            String val = argsAndOpts[i].toUpperCase();
            if (val.equals("NONE")) {
              loglevel = Logger.Level.NONE;
            } else if (val.equals("INFO")) {
              loglevel = Logger.Level.INFO;
            } else if (val.equals("ERROR")) {
              loglevel = Logger.Level.ERROR;
            } else if (val.equals("DEBUG")) {
              loglevel = Logger.Level.DEBUG;
            } else {
              System.err.println("Unknown logging level: " + val);
              loglevel = Logger.Level.INFO;
            }
          } else {
            System.err.println("The -l options requires a value: NONE|INFO|ERROR|DEBUG");
          }
        } else if (opt.equals("f")) {
          i++;
          if (i < argsAndOpts.length) {
            logfile = argsAndOpts[i];
          } else {
            System.err.println("The -f options requires a filename value");
          }
        } else if (opt.equals("h")) {
          System.err.println(getUsage());
          System.exit(0);
        } else {
          // Expressions can't start with a '-', so this is an error.
          System.err.println("Unknown option: -" + opt);
        }
      } else {
        // Correct syntax and is checked elsewhere.
        args.add(a);
      }
    }
    logger = new Logger(loglevel, logfile);

    return args;
  }
  
  /**
   * Get usage instructions.
   */
  public static String getUsage() {
    StringBuilder usage = new StringBuilder();
    usage.append("Usage: calculator.Main [OPTIONS] EXPRESSION+\n");
    usage.append("Expressions:\n");
    usage.append("  An expression EXP is either a function call FUN(EXP[,EXP]*) or a number N.\n");
    usage.append("  FUN must be in {add, sub, mult, div, let}. The first 4 of these functions\n");
    usage.append("  each take 2 expression parameters, while the let function takes 3 parameters:\n");
    usage.append("  an identifier, a value expression, and an expression for which the reference\n");
    usage.append("  is valid.\n");
    usage.append("Options:\n");
    usage.append("  -h        Print this message and quit\n");
    usage.append("  -l LEVEL  Set the logging level: NONE|INFO|ERROR|DEBUG\n");
    usage.append("  -f FILE   Set the log file (default=log.txt)\n");
    return usage.toString();
  }

  public static void main(String[] args) {
    List<String> exps = processOptions(args);
    if (exps.size() == 0) {
      System.err.println(getUsage());
    }
    for (String expstr : exps) {
      logger.debug("Processing expression: " + expstr);

      try {
        // Tokenize the input.
        Lexer lexer = new Lexer();
        Deque<Token> tokens = lexer.tokenize(expstr);

        // Parse the expression.
        Expression exp = Expression.parse(tokens);
        // Evaluate the expression with a scope that is initially empty.
        double val = exp.getValue(null);
        String valstr = Double.toString(val);
        logger.info(expstr + " evaluates to " + valstr);
        System.out.println(valstr);
      } catch (ParseException ex) {
        logger.error(ex.getMessage());
      } catch (UndefinedReferenceException ex) {
        logger.error(ex.getMessage());
      }
    }

    return;
  }

}
