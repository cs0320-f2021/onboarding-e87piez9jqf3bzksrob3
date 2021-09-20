package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  // use port 4567 by default when running server
  private static final int DEFAULT_PORT = 4567;
  private ArrayList<ArrayList<String>> stars;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // set up parsing of command line flags
    OptionParser parser = new OptionParser();

    // "./run --gui" will start a web server
    parser.accepts("gui");

    // use "--port <n>" to specify what port on which the server runs
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      MathBot mathBot = new MathBot();
      String input;
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          List<String> matchList = new ArrayList<>();
          Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
          Matcher regexMatcher = regex.matcher(input);
          while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
              // Add double-quoted string without the quotes
              matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
              // Add single-quoted string without the quotes
              matchList.add(regexMatcher.group(2));
            } else {
              // Add unquoted word
              matchList.add(regexMatcher.group());
            }
          }
          String[] arguments = matchList.toArray(new String[0]);

          switch (arguments[0]) {
            case "add":
              try {
                System.out.println(mathBot.add(Double.parseDouble(arguments[1]),
                    Double.parseDouble(arguments[2])));
              } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("ERROR: invalid usage of add");
                System.out.println("Correct usage: add <num1> <num2>");
              }
              break;
            case "subtract":
              try {
                System.out.println(mathBot.subtract(Double.parseDouble(arguments[1]),
                    Double.parseDouble(arguments[2])));
              } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("ERROR: invalid usage of subtract");
                System.out.println("Correct usage: subtract <num1> <num2>");
              }
              break;
            case "stars":
              starsHelper(arguments);
              break;
            case "naive_neighbors":
              naiveNeighborsHelper(arguments);
              break;
            default:
              System.out.println("ERROR: invalid command.");
              System.out.println("Valid commands: stars, naive_neighbors");
          }
        } catch (Exception e) {
          System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
    }

  }

  /**
   * Helper method called when the user runs the stars command.
   * Creates a new ArrayList of stars, attempts to open the file passed in, loads the stars into
   * the ArrayList and initalises their distances, and prints a confirmation message
   * @param arguments the array of command line arguments
   */
  private void starsHelper(String[] arguments) {
    if (arguments.length != 2) {
      System.out.println("ERROR: Invalid number of arguments for stars");
      System.out.println("Usage: stars <filepath>");
      return;
    }

    stars = new ArrayList<>(); // creates new ArrayList whenever the stars command is used

    try {
      File file = new File(arguments[1]);
      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        String[] starData = scanner.nextLine().split(",");
        if (!starData[0].equals("StarID")) {
          if (starDataErrorHelper(starData)) { // checks for incorrect data format in each line
            continue;
          }
          // converts star array data to an ArrayList
          ArrayList<String> star = new ArrayList<>(Arrays.asList(starData));
          star.add("0");
          stars.add(star);
        }
      }
      System.out.println("Read " + stars.size() + " stars from " + arguments[1]);
    } catch (FileNotFoundException e) {
      System.out.println("ERROR: File not found");
    }

  }

  /**
   * Helper method that checks for errors in a line of the input file.
   * It checks if the star data is in the correct format and prints an appropriate error message
   * if not
   * @param starData a line of the input file in the format:
   *                 StarID, Star Name, X, Y, Z
   * @return true if there's an error, false otherwise
   */
  private boolean starDataErrorHelper(String[] starData) {
    if (starData[0].equals("")) {
      System.out.println("Error: missing StarID. Skipping this star.");
      return true;
    } else {
      try {
        Double.parseDouble(starData[2]);
      } catch (NumberFormatException e) {
        System.out.println("Error: X value non-numeric. Skipping this star.");
        return true;
      }
      try {
        Double.parseDouble(starData[3]);
      } catch (NumberFormatException e) {
        System.out.println("Error: Y value non-numeric. Skipping this star.");
        return true;
      }
      try {
        Double.parseDouble(starData[4]);
      } catch (NumberFormatException e) {
        System.out.println("Error: Z value non-numeric. Skipping this star.");
        return true;
      }
    }
    return false;
  }

  /**
   * Private helper method called when the user runs the naive_neighbors command.
   * Checks for command validity, printing an appropriate error message if invalid.
   * Prints an appropriate error message if the naive_neighbors command is run before the star
   * command.
   * @param arguments the list of command line arguments
   */
  private void naiveNeighborsHelper(String[] arguments) {
    if (arguments.length != 5 && arguments.length != 3) {
      System.out.println("ERROR: Invalid number of arguments for naive_neighbors");
      System.out.println("naive_neighbors <k> <\"name\">");
      System.out.println("Usage: naive_neighbors <k> <x> <y> <z>");
      return;
    }

    if (stars == null || stars.isEmpty()) { // the stars command needs to be run before
      // naive_neighbors
      System.out.println("ERROR: You must run the stars command before running the naive_neighbors "
          + "command!");
      System.out.println("Usage: stars <filepath>");
      return;
    }

    if (arguments.length == 3) { // if the command is of the format stars <k> <starName>
      String starName = arguments[2].replace("\"", "");
      Optional<ArrayList<String>> starData =
          stars.stream().filter(a -> a.get(1).equals(starName)).findFirst();
      try {
        distanceMaker(starData.orElseThrow(), Integer.parseInt(arguments[1]));
      } catch (NoSuchElementException e) {
        System.out.println("ERROR: star passed in does not exist");
      }
    } else { // if the command is of the format stars <k> <x> <y> <z>
      // creates a new ArrayList with the location data, mimicking a star
      distanceMaker(new ArrayList<>(Arrays.asList("NULL", "NULL", arguments[2],
          arguments[3], arguments[4], "0")), Integer.parseInt(arguments[1]));
    }

  }

  /**
   * Helper method that calculates the distance for all stars relative to a single star/location.
   * Calls the kStars method to calculate and print the nearest k stars.
   * @param starData the star or set of coordinates relative to which distance for every other
   *                 star is to be calculated
   * @param k the number of stars whose IDs to print, passed as a parameter to the kStars method,
   *         which prints the stars
   */
  private void distanceMaker(ArrayList<String> starData, int k) {
    int initialSize = stars.size();
    // removes either the star passed in or the simulated star so it doesn't show up in the k stars
    stars.remove(starData);
    double baseX = Double.parseDouble(starData.get(2)), baseY = Double.parseDouble(starData.get(3)),
        baseZ = Double.parseDouble(starData.get(4));

    for (ArrayList<String> star: stars) {
      double currentX = Double.parseDouble(star.get(2)), currentY =
          Double.parseDouble(star.get(3)),
          currentZ = Double.parseDouble(star.get(4));

      double distance =
          Math.sqrt(Math.pow(baseX - currentX, 2)
              + Math.pow(baseY - currentY, 2)
              + Math.pow(baseZ - currentZ, 2));

      star.set(5, String.valueOf(distance));
    }

    // sorts the ArrayList of stars based on the numeric value of the distance
    stars.sort(Comparator.comparing(s -> Double.parseDouble(s.get(5))));
    kStars(k); // calculates, prints nearest k stars
    if (initialSize > stars.size()) { // if we removed a star earlier, add it back
      stars.add(starData);
    }
  }

  /**
   * Helper method that determines which stars to print, including randomisation of equidistant
   * stars, and prints the star IDs
   * @param k the number of star IDs to be printed
   */
  private void kStars(int k) {
    if (stars.size() < k) { // if we want fewer stars than we have, print them all
      stars.forEach(s -> System.out.println(s.get(0)));
    } else if (k > 0) {
      ArrayList<String> star = stars.get(k - 1);
      ArrayList<String> kStars = new ArrayList<>();
      ArrayList<String> equalStars = new ArrayList<>();
      stars.forEach(s -> {
        if (Double.parseDouble(s.get(5)) < Double.parseDouble(star.get(5))) {
          kStars.add(s.get(0));
        } else if (s.get(5).equals(star.get(5))) {
          equalStars.add(s.get(0));
        }
      });

      int requiredNumber = k - kStars.size();

      Random rand = new Random();
      for (int i = 0; i < requiredNumber; i++) {
        int randInt = rand.nextInt(equalStars.size());
        kStars.add(equalStars.remove(randInt)); // adds randomly selected star to k stars
      }
      kStars.forEach(System.out::println);
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_0);

    // this is the directory where FreeMarker templates are placed
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    // set port to run the server on
    Spark.port(port);

    // specify location of static resources (HTML, CSS, JS, images, etc.)
    Spark.externalStaticFileLocation("src/main/resources/static");

    // when there's a server error, use ExceptionPrinter to display error on GUI
    Spark.exception(Exception.class, new ExceptionPrinter());

    // initialize FreeMarker template engine (converts .ftl templates to HTML)
    FreeMarkerEngine freeMarker = createEngine();

    // setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      // status 500 generally means there was an internal server error
      res.status(500);

      // write stack trace to GUI
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * A handler to serve the site's main page.
   *
   * @return ModelAndView to render.
   * (main.ftl).
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // this is a map of variables that are used in the FreeMarker template
      Map<String, Object> variables = ImmutableMap.of("title",
          "Go go GUI");

      return new ModelAndView(variables, "main.ftl");
    }
  }
}
