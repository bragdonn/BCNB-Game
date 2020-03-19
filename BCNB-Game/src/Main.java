import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application{
	
	private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();
	
	private ArrayList<Node> platforms = new ArrayList<Node>();
	
	private Pane appRoot = new Pane();
	private Pane gameRoot = new Pane();
	private Pane uiRoot = new Pane();
	
	private Node player;
	private Point2D playerVelocity = new Point2D(0,0);
	private boolean canJump = true;
	
	private int levelWidth;
	
	private void initContent(){
		return;
	}
	
	private void update(){
		return;
	}
	
	private void movePlayerX(int value){
		return;
	}
	
	private void movePlayerX(int value){
		return;
	}
	
	private void jumpPlayer(){
		return;
	}
	
	private Node createEntity(int x, int y, int w, int h, Color color){
		return null;
	}
	
	private boolean isPressed(KeyCode key){
		return true;
	}
	
	public void start(Stage primaryStage) throws Exception{
		
	}

	public static void main(String[] args) {
		launch(args);

	}

}
