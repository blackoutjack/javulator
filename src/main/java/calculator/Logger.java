package calculator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

public class Logger {
  
  public enum Level {
    NONE,
    INFO,
    ERROR,
    DEBUG,
  };

  public static final String DEFAULT_FILENAME = "log.txt";
  public static final Level DEFAULT_LEVEL = Level.ERROR;

  private Level level;
  private OutputStream log = System.err;

  public Logger(Level l, String filename) {
    if (l == null) {
      l = DEFAULT_LEVEL;
    }
    level = l;

    if (l == Level.NONE) {
      // No need to open a file.
      return;
    }

    if (filename == null) {
      filename = DEFAULT_FILENAME;
    }
    openFile(filename);
  }

  protected void openFile(String logfile) {
    try {
      // Allow logging to any file that the user can access.
      // SecurityManager could be used to restrict file access further.
      log = new FileOutputStream(logfile, true);
    } catch (IOException ex) {
      log = System.err;
      error("Unable to open " + logfile + "; log information will be written to stderr");
    }
  }

  protected void write(String type, String msg) {
    String fullmsg = type + ": " + msg + "\n";
    try {
      log.write(fullmsg.getBytes(Charset.forName("UTF-8")));
    } catch (IOException ex) {
      System.err.println("Unable to log message: " + fullmsg);
    }
  }

  public void error(String msg) {
    // Always print errors to the command line.
    boolean doit = level == Level.NONE || level == Level.INFO;
    if (log != System.err || !doit) {
      System.err.println(msg);
    }
    if (!doit) return;
    write("ERROR", msg); 
  }

  public void info(String msg) {
    if (level == Level.NONE) return;
    write("INFO", msg); 
  }

  public void debug(String msg) {
    if (level != Level.DEBUG) return;
    write("DEBUG", msg); 
  }
}
