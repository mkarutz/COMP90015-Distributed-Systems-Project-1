package activitystreamer;

import activitystreamer.tester.TestControl;
import activitystreamer.tester.TestLoginAnon;
import activitystreamer.util.Settings;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tester {

  private static final Logger log = LogManager.getLogger();

  public static void main(String[] args) {

    log.info("reading command line options");

    Options options = new Options();
    options.addOption("rp", true, "remote port number");
    options.addOption("rh", true, "remote hostname");


    // build the parser
    CommandLineParser parser = new DefaultParser();

    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e1) {
    }

    if (cmd.hasOption("rh")) {
      Settings.setRemoteHostname(cmd.getOptionValue("rh"));
    }

    if (cmd.hasOption("rp")) {
      try {
        int port = Integer.parseInt(cmd.getOptionValue("rp"));
        Settings.setRemotePort(port);
      } catch (NumberFormatException e) {
        log.error("-rp requires a port number, parsed: " + cmd.getOptionValue("rp"));
      }
    }
    System.out.println("Starting test suite...\n");


    TestControl c = new TestControl(Settings.getRemoteHostname(), Settings.getRemotePort());
    c.addTest(new TestLoginAnon(c));
    new Thread(c).start();
  }
}
