package com.eitan07.snakebutfr;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    // Constants
    final float WIDTH = 600;
    final float HEIGHT = 600;
    final short UNIT_SIZE = 30;

    // Variables
    static short TICK_LENGTH;

    Stage mainStage;
    Scene mainScene;
    Canvas canvas;
    Timer gameTimer;
    TimerTask timerTask;
    int gameTicks = 0;
    Direction snakeDir = Direction.RIGHT;
    List<Integer> x = new ArrayList<>(400);
    List<Integer> y = new ArrayList<>(400);
    int appleX;
    int appleY;
    int score = 0;
    boolean keyPressedDuringTick = false;

    // Dev options
    boolean debugMode = true;

    boolean showDebugData = false;
    boolean showGrid = false;
    boolean showSnakePartNum = false;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        Pane root = new Pane();
        canvas = new Canvas(600, 600);
        mainScene = new Scene(root, 600, 600);
        stage.setScene(mainScene);
        mainScene.setCursor(Cursor.NONE);
        stage.setResizable(false);
        stage.show();
        root.getChildren().add(canvas);
        initGame();
    }

    private void initGame() {
        resetCanvas();
        x.add(0, 3);
        y.add(0, 3);
        gameTimer = new Timer();
        drawPixel(3, 3);
        mainStage.setTitle("Snake");
        mainStage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("icon.png"))));
        handleKeyboardEvents();

        genAppleLoc();
        initTask();
        Timer countDownTmr = new Timer();
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        countDownTmr.schedule(new TimerTask() {
            int i = 3;

            @Override
            public void run() {
                if (i > 0) {
                    resetCanvas();
                    gc.setFill(Color.LIGHTGRAY);
                    gc.setFont(new Font("System Regular", 30));
                    gc.fillText(Integer.valueOf(i--).toString(), (WIDTH / 2) - 10, (HEIGHT / 2) + 10);
                } else {
                    countDownTmr.cancel();
                    resetCanvas();
                    gameTimer.schedule(timerTask, 0, TICK_LENGTH);
                }
            }
        }, 0, 1000);
    }

    private void gameTick() {
        resetCanvas();
        drawApple();
        moveSnake();
        checkBorderCollisions();
        drawSnake();
        keyPressedDuringTick = false;

        if (x.get(0) == appleX & y.get(0) == appleY) {
            genAppleLoc();
            drawApple();
            score++;
            addNewSnakePart();
        }
    }

    private void handleKeyboardEvents() {
        mainScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> {
                    if (!keyPressedDuringTick) {
                        if (snakeDir != Direction.DOWN)
                            snakeDir = Direction.UP;
                        keyPressedDuringTick = true;
                    }
                }
                case DOWN -> {

                    if (!keyPressedDuringTick) {
                        if (snakeDir != Direction.UP)
                            snakeDir = Direction.DOWN;
                        keyPressedDuringTick = true;
                    }
                }
                case LEFT -> {
                    if (!keyPressedDuringTick) {
                        if (snakeDir != Direction.RIGHT)
                            snakeDir = Direction.LEFT;
                        keyPressedDuringTick = true;
                    }
                }
                case RIGHT -> {
                    if (!keyPressedDuringTick) {
                        if (snakeDir != Direction.LEFT)
                            snakeDir = Direction.RIGHT;
                        keyPressedDuringTick = true;
                    }
                }
                case G -> showGrid = !showGrid;
                case N -> showSnakePartNum = !showSnakePartNum;
                case F3 -> showDebugData = !showDebugData;
                case SPACE -> {
                    if (debugMode) {
                        score++;
                        addNewSnakePart();
                    }
                }
            }
        });
    }

    private void resetCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void moveSnake() {
        for (int i = score; i > 0; i--) {
            x.set(i, x.get(i - 1));
            y.set(i, y.get(i - 1));
        }

        switch (snakeDir) {
            case UP -> y.set(0, y.get(0) - 1);
            case DOWN -> y.set(0, y.get(0) + 1);
            case LEFT -> x.set(0, x.get(0) - 1);
            case RIGHT -> x.set(0, x.get(0) + 1);
        }

        int headX = x.get(0);
        int headY = y.get(0);
        for (int i = 1; i < x.toArray().length; i++) {
            if (headX == x.get(i) && headY == y.get(i)) {
                System.exit(0);
            }
        }
    }

    private void drawSnake() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < score + 1; i++) {
            drawPixel(x.get(i), y.get(i));
            gc.setFill(Color.RED);
            if (showSnakePartNum) {
                gc.setFont(new Font("System Regular", 20));
                gc.fillText(Integer.toString(i), x.get(i) * UNIT_SIZE + 10, y.get(i) * UNIT_SIZE + 23);
            }
        }
    }

    private void drawPixel(int x, int y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.GREEN);
        gc.fillRect(x * UNIT_SIZE, y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
    }

    private void genAppleLoc() {
        Random rnd = new Random();

        appleX = rnd.nextInt((int) (WIDTH / UNIT_SIZE));
        appleY = rnd.nextInt((int) (HEIGHT / UNIT_SIZE));
    }

    private void drawApple() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.fillRect(appleX * UNIT_SIZE, appleY * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
    }

    private void addNewSnakePart() {
        x.add(score, 0);
        y.add(score, 0);
    }

    private void checkBorderCollisions() {
        int headX = x.get(0);
        int headY = y.get(0);

        if (headX < 0 || headX >= WIDTH / UNIT_SIZE || headY < 0 || headY >= HEIGHT / UNIT_SIZE) {
            System.exit(0);
        }
    }


    private void showDebugData() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.YELLOW);
        gc.setFont(new Font("System Regular", 12));
        gc.fillText(String.format("Score: %d\nX: %d\nY: %d\nDirection: %s\nNumber of snake parts: %d\nAX: %d\nAY: %d\nTick: %d\nKey Pressed: %s", score, x.get(0), y.get(0), snakeDir.name(), x.toArray().length, appleX, appleY, gameTicks, keyPressedDuringTick ? "TRUE" : "FALSE"), 10, 20);
    }

    private void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);

        for (int i = UNIT_SIZE; i < WIDTH; i += UNIT_SIZE) {
            gc.fillRect(i, 0, 1, HEIGHT);
        }

        for (int i = UNIT_SIZE; i < HEIGHT; i += UNIT_SIZE) {
            gc.fillRect(0, i, WIDTH, 1);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                TICK_LENGTH = (short) Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            TICK_LENGTH = 400;
        }
        launch();
    }

    private void initTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                gameTick();
                if (showGrid) {
                    drawGrid();
                }
                if (showDebugData) {
                    showDebugData();
                } else {
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.setFont(new Font("System Regular", 20));
                    gc.setFill(Color.RED);
                    gc.fillText(String.format("Score: %d", score), 10, 25);
                }
                gameTicks++;
            }
        };
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}