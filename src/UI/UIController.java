package UI;

import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class UIController extends AnimationTimer {

    @FXML
    private Label fpsLbl;

    @FXML
    private Circle moveMeCircle;

    @FXML
    private AnchorPane root;

    Label lblGameOver = new Label("Game over!\n Press space to restart");

    private ArrayList<Rectangle> rectangles = new ArrayList<>();

    private ArrayList<Bullet> bullets = new ArrayList<>();

    int fps = 0;

    int deltaX = -5;
    int deltaY = 5;

    int bulletSpeed = 10;

    double spawnRate = 1;

    boolean left = false;
    boolean right = false;
    boolean up = false;
    boolean down = false;

    boolean firing = true;

    MouseEvent click;

    long start = 0;

    long lastFire = 0;

    @FXML
    public void initialize() {
        lblGameOver.setLayoutX(400);
        lblGameOver.setLayoutY(100);
        lblGameOver.setStyle("-fx-font: 72px 'Agency FB'; -fx-text-fill: blue; -fx-text-alignment: center");
        lblGameOver.setVisible(false);
        root.getChildren().add(lblGameOver);
        super.start();
        setHandlers();

    }

    private void setHandlers() {

        root.setOnMousePressed(mouseEvent -> {
            //System.out.println(mouseEvent.getButton());
            switch (mouseEvent.getButton()) {
                case SECONDARY:
                    firing = true;
                    click = mouseEvent;
            }
        });

        root.setOnMouseDragged(mouseEvent -> {
            click = mouseEvent;
        });

        root.setOnMouseReleased(mouseEvent -> {
            //System.out.println(mouseEvent.getButton());
            switch (mouseEvent.getButton()) {
                case SECONDARY:
                    firing = false;
                    click = mouseEvent;
            }

        });

        root.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.W) {
                up = true;
            } else if (event.getCode() == javafx.scene.input.KeyCode.S) {
                down = true;
            } else if (event.getCode() == javafx.scene.input.KeyCode.A) {
                left = true;
            } else if (event.getCode() == javafx.scene.input.KeyCode.D) {
                right = true;
            } else if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                restartGame();

            }
        });

        root.setOnKeyReleased(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.W) {
                up = false;
            } else if (event.getCode() == javafx.scene.input.KeyCode.S) {
                down = false;
            } else if (event.getCode() == javafx.scene.input.KeyCode.A) {
                left = false;
            } else if (event.getCode() == javafx.scene.input.KeyCode.D) {
                right = false;
            }
        });
    }

    private void fire(long l) {
        if (l >= lastFire + 150000000) {
            lastFire = l;
            Bullet bullet = new Bullet(moveMeCircle.getCenterX(), moveMeCircle.getCenterY(), click.getX(), click.getY());

            double angle = getAngleBetween(bullet);
            angle = angle * Math.PI / 180;
            double endX = bullet.getCenterX() + 4000 * Math.cos(angle);
            double endY = bullet.getCenterY() + 4000 * Math.sin(angle);
            Line line = new Line(bullet.getCenterX(), bullet.getCenterY(), endX, endY);

            PathTransition transition = new PathTransition();
            transition.setNode(bullet);
            transition.setPath(line);
            transition.setCycleCount(1);
            transition.setDuration(Duration.seconds(3));
            transition.play();

            bullets.add(bullet);
            System.out.println(lastFire);
        }
    }

    private void restartGame() {
        root.getChildren().removeAll(rectangles);
        rectangles.clear();
        super.start();
        lblGameOver.setVisible(false);
        moveMeCircle.setCenterX(50);
        moveMeCircle.setCenterY(360);
        start = 0;
        fps = 0;
        spawnRate = 0;
    }

    @Override
    public void handle(long l) {
        root.requestFocus();
        if (start == 0) {
            start = l;
        } else {
            if (l >= start + 1000000000) {
                fpsLbl.setText("FPS: " + ((l - start) / 1000000000) * fps);
                //System.out.println(fps);
                renderShape();
                if (fps < 59) {
                    System.out.println(rectangles.size());
                }
                fps = 0;
                start = l;
                spawnRate += 0.05;
            }
        }
        updateElements();
        updateSprite();
        handleCollisions();
        if (firing) {
            fire(l);
        }
        fps++;

    }

    private void updateSprite() {
        if (up) {
            moveMeCircle.setCenterY(moveMeCircle.getCenterY() - deltaY);
        }
        if (down) {
            moveMeCircle.setCenterY(moveMeCircle.getCenterY() + deltaY);
        }
        if (right) {
            moveMeCircle.setCenterX(moveMeCircle.getCenterX() - deltaX);
        }
        if (left) {
            moveMeCircle.setCenterX(moveMeCircle.getCenterX() + deltaX);
        }
    }

    private void handleCollisions() {
        for (Rectangle rectangle : rectangles) {
            if (moveMeCircle.getLayoutBounds().intersects(rectangle.getLayoutBounds())) {
                //System.out.println("COLLISION");
                super.stop();
                lblGameOver.setVisible(true);
            }
        }
    }

    private void renderShape() {
        for (Iterator<Rectangle> iterator = rectangles.listIterator(); iterator.hasNext(); ) {
            Rectangle rectangle = iterator.next();
            if (rectangle.getX() + rectangle.getWidth() < 0) {
                iterator.remove();
                root.getChildren().remove(rectangle);
            }
        }

        for (Iterator<Bullet> iterator = bullets.listIterator(); iterator.hasNext(); ) {
            Bullet bullet = iterator.next();
            if (bullet.getCenterX() < 0 || bullet.getCenterX() > 1280 || bullet.getCenterY() < 0 || bullet.getCenterY() > 720) {
                iterator.remove();
                root.getChildren().remove(bullet);
            }
        }

        Random rand;
        for (int i = 0; i < spawnRate; i++) {
            rand = new Random();
            int posX = 1280;
            int posY = rand.nextInt(720);
            rectangles.add(new Rectangle(posX, posY, 100, 50));
        }


    }

    public static double getAngleBetween(Bullet p1) {

        double xDistance = p1.getCenterX() - p1.getDestinationX();

        double yDistance = p1.getCenterY() - p1.getDestinationY();

        double tanc = yDistance / xDistance;

        double angle = Math.toDegrees(Math.atan(tanc));

        double rotation; //the angle to cast towards


        //tan only goes to 180 so reverse the angle when its on the left hand side

        if (xDistance > 0)

            rotation = angle - 180;

        else

            rotation = angle;


        //if the angle is equal to 90 it must be inverted, this is to prevent a bug where the lines were drawn the wrong way

        if (Math.abs(angle) == 90)

            return -angle;


        return rotation;

    }

    private void updateElements() {
        Random rand = new Random();
        for (Rectangle rect : rectangles) {
            if (!root.getChildren().contains(rect)) {
                rect.setWidth(rand.nextInt(300 + 650));
                root.getChildren().add(rect);
            }
        }


        for (Circle bullet : bullets) {
            if (!root.getChildren().contains(bullet)) {
                root.getChildren().add(bullet);
            }
        }

        for (Rectangle rect : rectangles) {
            rect.setX(rect.getX() + deltaX * 2);
        }
    }


}
