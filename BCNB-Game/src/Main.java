import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.*;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

//This program is intended to run a basic platformer game.
public class Main extends Application {

    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();//begins keeping track of key pushes

    private ArrayList<Node> platforms = new ArrayList<Node>();//creates the solid space on the map

    private Pane appRoot = new Pane();//initializes the viewable game
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();

    private Node player;//creates the enemy and player as nodes and tracks their velocities.
    private Node enemy;
    private Point2D playerVelocity = new Point2D(0, 0);
    private Point2D enemyVelocity = new Point2D(0, 0);
    
    private boolean canJump = true;//makes it automatically possible to jump until the condition is changed

    private int levelWidth;//initializes the levels size
    private int levelHeight;

    private boolean running = true;//keeps track of if the program is still running

    private void initContent() {
        Rectangle bg = new Rectangle(1000, 800);//creates the window

        levelWidth = LevelData.LEVEL1[0].length() * 60;//keeps track of the width of the level
        levelHeight = LevelData.LEVEL1.length * 60;//keeps track of the height of the level

        for (int i = 0; i < LevelData.LEVEL1.length; i++){//loops over the level and creates it in the pane
            String line = LevelData.LEVEL1[i];
            for (int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {//switch statement that creates nothing for 0's and blocks for 1's
                    case '0':
                        break;
                    case '1':
                        Node platform = createEntity(j*60, i*60, 60, 60, Color.BROWN);
                        platforms.add(platform);
                        break;
                }
            }
        }

        player = createEntity(0, 600, 40, 40, Color.BLUE);//creates the model of the player as an entity
        
        enemy = createEntity(3500, 700, 40, 40, Color.YELLOW);//creates the model of the enemy as an entity

        player.translateXProperty().addListener((obs, old, newValue) -> {//creates the listener that takes key input
            int offset = newValue.intValue();

            if (offset > 640 && offset < levelWidth - 640) {
                gameRoot.setLayoutX(-(offset - 640));
            }
        });

        appRoot.getChildren().addAll(bg, gameRoot, uiRoot);//adds everything to the actual application
    }

    private void update() {//runs constantly to play the game
        if (isPressed(KeyCode.W) && player.getTranslateY() >= 5) {//moves the player up if they press w
            jumpPlayer();
        }

        if (isPressed(KeyCode.A) && player.getTranslateX() >= 5) {//moves the player left if they press a
            movePlayerX(-5);
        }

        if (isPressed(KeyCode.D) && player.getTranslateX() + 40 <= levelWidth - 5) {//moves the player right if they press d
            movePlayerX(5);
        }

        if (playerVelocity.getY() < 10) {//adds velocity to the player, making them fall
            playerVelocity = playerVelocity.add(0, 1);
        }//adding this code to the enemyVelocity breaks jumpPlayer.
        
        if(isPlayerAboveGround() || isKilled()){//if player falls off the level or is killed by the enemy the game ends
        	System.exit(0);
        }
        
        //code intended to act as pseudo-ai for the enemy, happens too fast and depletes resources
        
        /**int x = 1;
        x+=4;
        
        if(x % 3 == 1){
        	enemyMoveRand();
        }**/

        movePlayerY((int)playerVelocity.getY());//moves the player downwards based on their velocity
        moveEnemyY((int)enemyVelocity.getY());//moves the enemy downwards based on their velocity, enemy never has velocity
    }

    private void movePlayerX(int value) {//moves the player along the x axis
        boolean movingRight = value > 0;//checks if the player is moving right based on whether or not value is negative

        for (int i = 0; i < Math.abs(value); i++) {//loops over the level to move the player
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {//prevents the player from going through the level
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX()) {//moves the player right
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateX() == platform.getTranslateX() + 60) {//moves the player left
                            return;
                        }
                    }
                }
            }
            
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1));//moves the player based on the previously executed actions
            
        }
    }

    private void movePlayerY(int value) {//moves the player upwards
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {//loops to check that the player can jump
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {//prevents the player from jumping through the level
                    if (movingDown) {//moves the player downwards after they hit a ceiling
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateY() == platform.getTranslateY() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));//moves the player upwards or downwards based on their status
        }
    }

    private void jumpPlayer() {//gives the player negative velocity if they press w, based on the canJump status
        if (canJump) {
            playerVelocity = playerVelocity.add(0, -30);
            canJump = false;
        }
    }
    
    private boolean isPlayerAboveGround(){//
    	if(player.getTranslateY() > levelHeight){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private void moveEnemyX(int value) {//moves the enemy along the x axis
        boolean movingRight = value > 0;//checks if the enemy is moving right based on whether or not value is negative

        for (int i = 0; i < Math.abs(value); i++) {//loops over the level to move the enemy
            for (Node platform : platforms) {
                if (enemy.getBoundsInParent().intersects(platform.getBoundsInParent())) {//prevents the enemy from going through the level
                    if (movingRight) {
                        if (enemy.getTranslateX() + 40 == platform.getTranslateX()) {//moves the enemy right
                            return;
                        }
                    } else {
                        if (enemy.getTranslateX() == platform.getTranslateX() + 60) {//moves the enemy left
                            return;
                        }
                    }
                }
            }
            
            enemy.setTranslateX(enemy.getTranslateX() + (movingRight ? 1 : -1));//moves the enemy based on the previously executed actions
            
        }
    }
    
    private void moveEnemyY(int value) {//moves the enemy upwards
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {//loops to check that the enemy can jump
            for (Node platform : platforms) {
                if (enemy.getBoundsInParent().intersects(platform.getBoundsInParent())) {//prevents the enemy from jumping through the level
                    if (movingDown) {
                        if (enemy.getTranslateY() + 40 == platform.getTranslateY()) {
                        	enemy.setTranslateY(enemy.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    }
                }
            }
            enemy.setTranslateY(enemy.getTranslateY() + (movingDown ? 1 : -1));//moves the enemy upwards or downwards based on their status
        }
    }
    
    private void enemyMoveRand(){//randomly chooses a direction for the enemy to move, and moves them
    	Random rand = new Random();
    	for(int i = 0; i < 1; i++){
 	    	int r = rand.nextInt(2);
 	    	if(r == 1){
 	    		moveEnemyX(50);
 	    	}else if(r == 0){
 	    		moveEnemyX(-50);
 	    	}
    	}
    }
    
    private boolean isKilled(){//checks if the player touchs the enemy, and then kills them if they do
    	if(player.getTranslateX() == enemy.getTranslateX() && player.getTranslateY() >= enemy.getTranslateY()){
    		return true;
    	}else{
    		return false;
    	}
    }

    private Node createEntity(int x, int y, int w, int h, Color color) {//creates an entity that is movable
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);

        gameRoot.getChildren().add(entity);
        return entity;
    }

    private boolean isPressed(KeyCode key) {//detects keypresses
        return keys.getOrDefault(key, false);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initContent();//launches game

        Scene scene = new Scene(appRoot);//sets game as the scene
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));//detects all input from the keyboard
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
        primaryStage.setTitle("Fario");//names and shows the visual aspect of the game
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    update();
                }
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}