package ie.gmit.sw.ai.node;

import java.util.List;

import ie.gmit.sw.ai.traversers.AStarTraversator;

// this is the player class that extends the node 
public class PlayerNode extends Node{
	
	//variables 
	private Node[][] maze;
	private double playerHealth;
	private int weapon;
	private Node exit;
	private AStarTraversator traversator;
	private List<Node> playerPath;
	
	public PlayerNode(int row, int col, int type, Node[][] maze) {
		super(row, col, type);
		this.maze = maze;
	}
	
	//set and get health
	public double getPlayerHealth() {
		return playerHealth;
	}

	public void setPlayerHealth(double playerHealth) {
		this.playerHealth = playerHealth;
	}
	//get and set weapon  
	public int getWeapon() {
		return weapon;
	}

	// this is to set up the weapon for the player 
	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}
	
	public Node getsetExit(){
		return exit;
	}
	
	public void setExit(Node exit){
		this.exit = exit;
		traversator = new AStarTraversator(exit);
		startTraversator();
	}
	//find the exit 
	public List<Node> startTraversator(){
			traversator.traverse(maze, maze[getRow()][getCol()]);
			playerPath = traversator.getPath();	
			return playerPath;
	}
}
