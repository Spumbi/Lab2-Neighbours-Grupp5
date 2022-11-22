import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
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
        int rNeighbours = 5;
        // % of surrounding neighbours that are like me
        double threshold = 0.125 * rNeighbours;
        int numberOfNulls = getNulls(world);
        Integer[] indexForNullsRow = new Integer[numberOfNulls];
        Integer[] indexForNullsCol = new Integer[numberOfNulls];

        // TODO update world
        world = alg(world, threshold, indexForNullsRow, indexForNullsCol);
    }


    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime
    // That's why we must have "@Override" and "public" (just accept for now)
    @Override

    public void init() {
        //test();    // <---------------- Uncomment to TEST!
        double empty = 0.3;
        double redBlueRatio = 0.5;
        double red = (1-empty)*redBlueRatio;
        double blue = 1-empty-red;
        // %-distribution of RED, BLUE and NONE
        double[] dist = {red, blue, empty};

        int size = 300;
        // Number of locations (places) in world (must be a square)
        int nLocations = size*size;   // Should also try 90 000
        out.println("Locations: " + nLocations);

        // TODO initialize the world
        Actor[] actors = generateDistribution(nLocations, dist);

        shuffle(actors);

        world = toMatrix(actors);

        // Should be last
        fixScreenSize(nLocations);
    }

    // ---------------  Methods ------------------------------

    // TODO Many ...
    Actor[] generateDistribution(int n, double[] d) {
        Actor[] array = new Actor[n];
        int dist1 = (int) StrictMath.round(n * d[0]);
        int dist2 = (int) StrictMath.round(n * d[1]);
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
        out.println("Number of Empty: " + (n - count1 - count2));
        return array;
    }

    boolean pleased(Actor[][] w, int row, int col, double p) {
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
        return (c >= (n * p));
    }

    Actor[][] alg(Actor[][] w, double p, Integer[] indexForNullsRow, Integer[] indexForNullsCol) {
        Actor[][] temp = new Actor[w.length][w.length];
        Integer[] index = new Integer[indexForNullsRow.length];
        int t = 0;
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
        }
        shuffle(index);
        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w[i].length; j++) {
                if (w[i][j] != null) {
                    temp[i][j] = new Actor(w[i][j].color);
                    if (pleased(w, i, j, p)) {
                        temp[i][j].isSatisfied = true;
                    }
                } else {
                    indexForNullsRow[index[t]] = i;
                    indexForNullsCol[index[t]] = j;
                    t++;
                }
            }
        }
        t = 0;
        int row;
        int col;
        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w[i].length; j++) {
                if (w[i][j] != null) {
                    if(!w[i][j].isSatisfied) {
                        if (t == index.length) {
                            t = 0;
                        }
                        row = indexForNullsRow[t];
                        col = indexForNullsCol[t];
                        temp[row][col] = temp[i][j];
                        temp[i][j] = null;
                        indexForNullsRow[t] = i;
                        indexForNullsCol[t] = j;
                        t++;
                    }
                }
            }
        }
        return temp;
    }



    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }

    // ----------- Utility methods -----------------

    // TODO (general method possible reusable elsewhere)
    <T> void shuffle(T[] arr) {
        for (int i = arr.length; i > 1; i--) {
            int j = rand.nextInt(i);
            T tmp = arr[j];
            arr[j] = arr[i - 1];
            arr[i - 1] = tmp;
        }
    }

    Actor[][] toMatrix(Actor[] arr) {
        int size = (int) round(sqrt(arr.length));
        Actor[][] matrix = new Actor[size][size];
        for (int i = 0; i < arr.length; i++) {
            matrix[i / size][i % size] = arr[i];
        }
        return matrix;
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
                    print = print + co + s + " ";
                } else {
                    print = print + "NN ";
                }
            }
            out.println(print);
            print = "";
        }
    }

    int getNulls(Actor[][] w) {
        int n = 0;
        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w[i].length; j++) {
                if(w[i][j] == null){
                    n++;
                }
            }
        }
        return n;
    }

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
        double th = 0.55;   // Simple threshold used for testing

//        out.println(isValidLocation(size, 0, 0));
//        out.println(!isValidLocation(size, -1, 0));
//        out.println(!isValidLocation(size, 0, 3));

        // TODO
        //out.println("Neighbours: " + neighbours(testWorld, 0, 1));'
//        printActors(testWorld);
//        testWorld = alg(testWorld);
//        printActors(testWorld);
//        out.println(testWorld[0][1].isSatisfied);

//        int length = 3;
//        int i = 2;
//        int j = 2;
//        int varde = (i+1)*length*100000 + j;
//        int col = varde % 100000;
//        out.println("i: " + (((varde-col)/100000)/length - 1));
//        out.println("j: " + col);

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.4, 0.4, 0.2};
        // Number of locations (places) in world (must be a square)
        int nLocations = 100;   // Should also try 90 000

        // TODO initialize the world
//        Actor[] actors = generateDistribution(nLocations, dist);
//        shuffle(actors);
//        world = toMatrix(actors);
//        printActors(world);
//
//        world = alg(world, th);
//
//        printActors(world);
//        world = alg(world, th);
//
//        printActors(world);


        exit(0);
    }

    // ******************** NOTHING to do below this row, it's JavaFX stuff  **************

    double width = 500;   // size for window
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
    final long INTERVAL = 200_000_000;


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
