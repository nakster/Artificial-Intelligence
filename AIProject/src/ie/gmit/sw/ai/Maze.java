package ie.gmit.sw.ai;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ie.gmit.sw.ai.fight.NnFuzzyFight;
import ie.gmit.sw.ai.nn.NnFight;
import ie.gmit.sw.ai.node.Node;
import ie.gmit.sw.ai.node.PlayerNode;

public class Maze {
	
	// changed the it to a node array 
	private Node[][] maze; //An array does not lend itself to the type of maze generation algos we use in the labs. There are no "walls" to carve...
	
	// this is the lock object we are going to use to run the while loop in the runnable 
	// this prevents deadlock
	private Object lock =  new Object();
	// this the executor
	private ExecutorService executor = Executors.newCachedThreadPool();
	// player class 
	private PlayerNode p;
	// here i am initializing the neural network class 
	// train the neural network 
	// we don't train network each time
	private NnFight f;
	private Node door;
	
	public Maze(int dimension, NnFight nfight){
		// Initializing 
		this.f= nfight;
		
		//second
		maze = new Node[dimension][dimension];
		init();
		buildMaze();
	
		// items 
		int featureNumber = (int)((dimension * dimension) * 0.01); //Change this value to control the number of objects
		
		// made these into int rather than chars 
		addFeature(1, 0, featureNumber); //1 is a sword, 0 is a hedge
		addFeature(2, 0, featureNumber); //2 is help, 0 is a hedge
		addFeature(3, 0, featureNumber); //3 is a bomb, 0 is a hedge
		addFeature(4, 0, featureNumber); //4 is a hydrogen bomb, 0 is a hedge
		
		featureNumber = (int)((dimension * dimension) * 0.0001); //Change this value to control the number of spiders

		//the player was instantiated in the gameRunner 
		player(5, -1);
		
		// spiders 
		addFeature(6, -1, featureNumber); //6 is a Black Spider, 0 is a hedge
		addFeature(7, -1, featureNumber * 2); //7 is a Blue Spider, 0 is a hedge
		addFeature(8, -1, featureNumber); //8 is a Brown Spider, 0 is a hedge
		addFeature(9, -1, featureNumber); //9 is a Green Spider, 0 is a hedge
		addFeature(10, -1, featureNumber); //: is a Grey Spider, 0 is a hedge
		addFeature(11, -1, featureNumber * 2); //; is a Orange Spider, 0 is a hedge
		addFeature(12, -1, featureNumber); //< is a Red Spider, 0 is a hedge
		addFeature(13, -1, featureNumber * 2); //= is a Yellow Spider, 0 is a hedge
		placeExit(14, -1);
	}
	
	//O(N2)
	private void init(){
		for (int row = 0; row < maze.length; row++){
			for (int col = 0; col < maze[row].length; col++){
				//maze[row][col] = '0'; //Index 0 is a hedge...
				// here at the start of the game we are going to init the whole
				// maze with a value of 0
				maze[row][col] = new Node(row, col, 0);
			}
		}
	}
	
	// changed add feature so now it takes in ints O(N)
	private void addFeature(int feature, int replace, int number){
		
		int counter = 0;
		// this will draw the amount of spiders and hedges 
		while (counter < number){ //Keep looping until feature number of items have been added
			int row = (int) (maze.length * Math.random());
			int col = (int) (maze[0].length * Math.random());
			// 
			if (maze[row][col].getType() == replace){
				
				// sending all the spiders to the thread pool class and the player as well
				if(feature > 5 && feature < 14) {
					NnFuzzyFight f = new NnFuzzyFight(row,col,feature,lock,maze, p, this.f);
					// we are going to execute the thread pool 
					executor.execute(f);
				}
				// change its type to what the name of the spider is 
				maze[row][col].setType(feature);
				counter++;
			}
		}
	}
	
	// place the door O(N)
	private void placeExit(int feature, int replace) {
		boolean placedDoor = false;
		while(!placedDoor){
			int row = (int) (maze.length * Math.random());
			int col = (int) (maze[0].length * Math.random());
			
			if (maze[row][col].getType() == replace){
				door = new Node(row,col,feature);
				maze[row][col] = door;
//				System.out.println(maze[row][col]);
				door.setGoalNode(true);
				p.setExit(door);
				placedDoor = true;
			}
		}
	}
	// O(N2)
	private void buildMaze(){ 
		for (int row = 1; row < maze.length - 1; row++){
			for (int col = 1; col < maze[row].length - 1; col++){
				int num = (int) (Math.random() * 10);
				if (isRoom(row, col)) continue;
				if (num > 5 && col + 1 < maze[row].length - 1){
					//maze[row][col + 1] = '\u0020'; //\u0020 = 0x20 = 32 (base 10) = SPACE
					// space part of the maze is now set to type of -1 
					maze[row][col + 1].setType(-1);
				}else {
//					if (row + 1 < maze.length - 1) maze[row + 1][col] = '\u0020';
					if (row + 1 < maze.length - 1) maze[row + 1][col].setType(-1);
				}
			}
		}	
	}
	
	// this method puts the player onto the maze O(N)
	public void player(int feature, int replace){
		// boolean check if the player has been put up 
		boolean placed = false;
		while(!placed){
			// get random row and col values 
			int row = (int) (maze.length * Math.random());
			int col = (int) (maze[0].length * Math.random());
			
			// if the row and col values are not -1 meaning its not space rather an hedge 
			// basicaly saying its not empty dont place it 
			if (maze[row][col].getType() == replace){
				// if the row and col value are empty the place the player 
				p = new PlayerNode(row,col,feature, maze);
				maze[row][col] = p;
				// return true if the player has been placed 
				placed = true;
			}
		}
	}
	
	private boolean isRoom(int row, int col){ //Flaky and only works half the time, but reduces the number of rooms
//		return row > 1 && maze[row - 1][col] == '\u0020' && maze[row - 1][col + 1] == '\u0020';
		return row > 1 && maze[row - 1][col].getType() == -1 && maze[row - 1][col + 1].getType() == -1;
	}
	
	// made the setters and getters into Node type 
	public Node[][] getMaze(){
		return this.maze;
	}
	
	public Node get(int row, int col){
		return this.maze[row][col];
	}
	
	public void set(int row, int col, Node node){
		node.setCol(col);
		node.setRow(row);
		
		this.maze[row][col] = node;
	}
	
	public int size(){
		return this.maze.length;
	}
	
	public PlayerNode getP() {
		return this.p;
	}

	public void setP(int row, int col) {
		this.maze[row][col] = this.p;
		this.p.setRow(row);
		this.p.setCol(col);
	}

	public Node getDoor(){
		return this.door;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (int row = 0; row < maze.length; row++){
			for (int col = 0; col < maze[row].length; col++){
				sb.append(maze[row][col]);
				if (col < maze[row].length - 1) sb.append(",");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}