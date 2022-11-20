import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.*;

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

    final Random rand = new Random();

    class Actor {
        final Color color;        // Color an existing JavaFX class
        boolean isSatisfied;      // false by default

        Actor(Color color) {      // Constructor to initialize
            this.color = color;
        }
    }

    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        double threshold = 0.7;

        // TODO update world
        world = alg(world);
    }

    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime
    // That's why we must have "@Override" and "public" (just accept for now)
    @Override
    public void init() {
//        test();    // <---------------- Uncomment to TEST!

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.25, 0.25, 0.50};
        // Number of locations (places) in world (must be a square)
        int nLocations = 90000;   // Should also try 90 000

        // TODO initialize the world
        Actor[] actors = generateDistribution(nLocations, dist);
        out.println("actors.length1: " + actors.length);

        shuffle(actors);
        out.println("actors.length2: " + actors.length);

        world = toMatrix(actors);

        out.println("world.length: " + world.length);

        // Should be last
        fixScreenSize(nLocations);
    }

    // ---------------  Methods ------------------------------

    // TODO Many ...
    <T> void shuffle(T[] arr) {
        for (int i = arr.length; i > 1; i--) {
            int j = rand.nextInt(i);
            T tmp = arr[j];
            arr[j] = arr[i - 1];
            arr[i - 1] = tmp;
        }
    }

    Actor[][] toMatrix(Actor[] arr) {
//        out.println("arr.length: " + arr.length);
//        out.println("sqrt(arr.lengt"): " + sqrt(arr.length));
        int size = (int) round(sqrt(arr.length));
//        out.println("size: " + size);
        Actor[][] matrix = new Actor[size][size];
        for (int i = 0; i < arr.length; i++) {
            matrix[i / size][i % size] = arr[i];
        }
        return matrix;
    }

    Actor[] generateDistribution(int n, double[] d) {
        Actor[] array = new Actor[n];
        out.println("array.length: " + array.length);
        int dist1 = (int) StrictMath.floor(n * d[0]);
        int dist2 = (int) StrictMath.floor(n * d[1]);
        int count1 = 0;
        int count2 = 0;
        for (int i = 0; i < dist1; i++) {
            array[i] = new Actor(Color.RED);
            count1++;
        }
        for (int i = dist1; i < dist1 + dist2; i++) {
            array[i] = new Actor(Color.BLUE);
            count2++;
        }
        out.println("Number of RED: " + count1);
        out.println("Number of BLUE: " + count2);
        return array;
    }

    boolean pleased(Actor[][] w, int row, int col) {
        int n = 0;
        int c = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (isValidLocation(w.length, i, j) && !(i == row && j == col)) {
                    if (w[i][j] != null) {
                        n++;
                        if (w[i][j].color == w[row][col].color) {
                            c++;
                        }
                    }
                }
            }
        }
        return (c >= (n / 2));
    }

    Actor[][] alg(Actor[][] w) {
        out.println("w.length: " + w.length);
        Actor[][] temp = new Actor[w.length][w.length];
        Integer[] indexForNulls = new Integer[45000];
        out.println("indexForNulls.length: " + indexForNulls.length);
        int count1 = 0;
        int count2 = 0;
        int t = 0;
        int big = (w.length^2)*10;
        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w.length; j++) {

                if (w[i][j] != null) {
                    count1++;
//                    out.println("Denna ock");
                    temp[i][j] = new Actor(w[i][j].color);
                    if (pleased(w, i, j)) {
                        temp[i][j].isSatisfied = true;
                    }

                } else {
                    count2++;
//                    out.println("Använt");
                    indexForNulls[t] = ((i+1)*w.length*big + j);
                    t++;
                }
            }
        }
        out.println("Count sum: " + (count1 + count2));
        shuffle(indexForNulls);
        int m = 0;
        int row;
        int col;
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp.length; j++) {
                if (temp[i][j] != null) {
                    if(!temp[i][j].isSatisfied) {
                        out.println("-----");
                        out.println("m: " + m);
                        out.println("index value: " + indexForNulls[m]);
                        out.println("i: " + i);
                        out.println("j: " + j);
                        col = indexForNulls[m] % big;
                        row = ((indexForNulls[m]-col)/big)/temp.length - 1;
                        temp[row][col] = temp[i][j];
                        temp[i][j] = null;
                        m++;
                    }
                }
            }
        }
        return temp;
    }

    void printActors(Actor[][] testWorld) {
        String print = "";
        String s;
        String co;
        for (int i = 0; i < testWorld.length; i++) {
            for (int j = 0; j < testWorld[i].length; j++) {
                if (testWorld[i][j] != null) {
                    if (testWorld[i][j].isSatisfied) {
                        s = "T";
                    } else {
                        s = "F";
                    }
                    if (testWorld[i][j].color == Color.BLUE) {
                        co = "B";
                    } else {
                        co = "R";
                    }
                    print = print + s + co + " ";
                } else {
                    print = print + "NU ";
                }
            }
            out.println(print);
            print = "";
        }
    }

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
        Actor[][] testWorld = new Actor[][]{
                {new Actor(Color.RED), new Actor(Color.RED), null, null},
                {null, new Actor(Color.BLUE), null, null},
                {new Actor(Color.RED), null, new Actor(Color.BLUE), null},
                {null, new Actor(Color.BLUE), new Actor(Color.BLUE), new Actor(Color.RED)}

        };
        double th = 0.5;   // Simple threshold used for testing

        int size = testWorld.length;
//        out.println(isValidLocation(size, 0, 0));
//        out.println(!isValidLocation(size, -1, 0));
//        out.println(!isValidLocation(size, 0, 3));

        // TODO
        //out.println("Neighbours: " + neighbours(testWorld, 0, 1));'
        printActors(testWorld);
        testWorld = alg(testWorld);
        printActors(testWorld);
//        out.println(testWorld[0][1].isSatisfied);

//        int length = 3;
//        int i = 2;
//        int j = 2;
//        int varde = (i+1)*length*100000 + j;
//        int col = varde % 100000;
//        out.println("i: " + (((varde-col)/100000)/length - 1));
//        out.println("j: " + col);

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.25, 0.25, 0.50};
        // Number of locations (places) in world (must be a square)
        int nLocations = 900;   // Should also try 90 000

        // TODO initialize the world
//        Actor[] actors = generateDistribution(nLocations, dist);
//        shuffle(actors);
//        world = toMatrix(actors);
//
//        world = alg(world);

//        FR FR NU NU
//        NU FB NU NU
//        FR NU FB NU
//        NU FB FB FR

//        ---

//        TR TR NU NU
//        FB NU NU FR
//        NU FR TB NU
//        NU TB TB NU

        exit(0);
    }

    // ******************** NOTHING to do below this row, it's JavaFX stuff  **************

    double width = 500;   // Size for window
    double height = 500;
    final double margin = 50;
    double dotSize;

    void fixScreenSize(int nLocations) {
        // Adjust screen window
        dotSize = 9000 / nLocations;
        if (dotSize < 1) {
            dotSize = 2;
        }
        width = sqrt(nLocations) * dotSize + 2 * margin;
        height = width;
    }

    long lastUpdateTime;
    final long INTERVAL = 450_000_000;


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Build a scene graph
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        root.getChildren().addAll(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timer
        AnimationTimer timer = new AnimationTimer() {
            // This method called by FX, parameter is the current time
            public void handle(long now) {
                long elapsedNanos = now - lastUpdateTime;
                if (elapsedNanos > INTERVAL) {
                    updateWorld();
                    renderWorld(gc);
                    lastUpdateTime = now;
                }
            }
        };

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();

        timer.start();  // Start simulation
    }


    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g) {
        g.clearRect(0, 0, width, height);
        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = (int) (dotSize * col + margin);
                int y = (int) (dotSize * row + margin);
                if (world[row][col] != null) {
                    g.setFill(world[row][col].color);
                    g.fillOval(x, y, dotSize, dotSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
