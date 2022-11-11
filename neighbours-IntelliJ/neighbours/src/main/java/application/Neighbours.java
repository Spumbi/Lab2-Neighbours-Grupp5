package application;

import static java.lang.Math.sqrt;
import static java.lang.System.exit;
import static java.lang.System.out;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/*
 *  Program to simulate segregation.
 *  See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 * NOTE:
 * - JavaFX first calls method init() and then method start() far below.
 * - To test uncomment call to test() first in init() method!
 *
 */
// Extends Application because of JavaFX (just accept for now)
public class Neighbours extends Application {

    class Actor {
        final Color color; // Color an existing JavaFX class
        boolean isSatisfied; // false by default

        Actor(Color color) { // Constructor to initialize
            this.color = color;
        }
    }

    // Below is the *only* accepted instance variable (i.e. variables outside any
    // method)
    // This variable may *only* be used in methods init() and updateWorld()
    Actor[][] world; // The world is a square matrix of Actors

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {

        // TODO update world
    }

    // This method initializes the world variable with a random distribution of
    // Actors
    // Method automatically called by JavaFX runtime
    // That's why we must have "@Override" and "public" (just accept for now)
    @Override
    public void init() {
        test(); // <---------------- Uncomment to TEST!

        // Number of locations (places) in world (must be a square)
        final int nLocations = 900; // Should also try 90 000

        // TODO initialize the world

        // Should be last
        fixScreenSize(nLocations);
    }

    // --------------- Methods ------------------------------

    // TODO Many ...

    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }

    // ----------- Utility methods -----------------

    // TODO (general method possible reusable elsewhere)

    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work. Important!!!!
    void test() {
        // A small hard coded world for testing
        final Actor[][] testWorld = { { new Actor(Color.RED), new Actor(Color.RED), null },
                { null, new Actor(Color.BLUE), null }, { new Actor(Color.RED), null, new Actor(Color.BLUE) } };
        final int size = testWorld.length;
        out.println(isValidLocation(size, 0, 0));
        out.println(!isValidLocation(size, -1, 0));
        out.println(!isValidLocation(size, 0, 3));

        // TODO

        exit(0);
    }

    // ******************** NOTHING to do below this row, it's JavaFX stuff
    // **************

    double width = 500; // Size for window
    double height = 500;
    final double margin = 50;
    double dotSize;

    void fixScreenSize(int nLocations) {
        // Adjust screen window
        this.dotSize = 9000 / nLocations;
        if (this.dotSize < 1) {
            this.dotSize = 2;
        }
        this.width = sqrt(nLocations) * this.dotSize + 2 * this.margin;
        this.height = this.width;
    }

    long lastUpdateTime;
    final long INTERVAL = 450_000_000;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Build a scene graph
        final Group root = new Group();
        final Canvas canvas = new Canvas(this.width, this.height);
        root.getChildren().addAll(canvas);
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timer
        final AnimationTimer timer = new AnimationTimer() {
            // This method called by FX, parameter is the current time
            @Override
            public void handle(long now) {
                final long elapsedNanos = now - Neighbours.this.lastUpdateTime;
                if (elapsedNanos > Neighbours.this.INTERVAL) {
                    updateWorld();
                    renderWorld(gc);
                    Neighbours.this.lastUpdateTime = now;
                }
            }
        };

        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();

        timer.start(); // Start simulation
    }

    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g) {
        g.clearRect(0, 0, this.width, this.height);
        final int size = this.world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                final int x = (int) (this.dotSize * col + this.margin);
                final int y = (int) (this.dotSize * row + this.margin);
                if (this.world[row][col] != null) {
                    g.setFill(this.world[row][col].color);
                    g.fillOval(x, y, this.dotSize, this.dotSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
