package ie.gmit.sw.ai.fight;

import java.util.ArrayList;
import java.util.Random;

import ie.gmit.sw.ai.fuzzy.FuzzyFight;
import ie.gmit.sw.ai.gui.GameOverOption;
import ie.gmit.sw.ai.nn.NnFight;
import ie.gmit.sw.ai.node.Node;
import ie.gmit.sw.ai.node.PlayerNode;
import ie.gmit.sw.ai.sprites.Sprite;
import ie.gmit.sw.ai.traversers.AStarTraversator;
import ie.gmit.sw.ai.traversers.BasicHillClimbingTraversator;
import ie.gmit.sw.ai.traversers.DepthLimitedDFSTraversator;
import ie.gmit.sw.ai.traversers.Traversator;

public class NnFuzzyFight extends Sprite implements Runnable {

	// variables 
	private int row;
	private int col;
	private int feature;

	private Node node = new Node(row, col, feature);
	private Object lock;
	private Node[][] maze;

	// gets a random value 
	private Random random = new Random();
	private Node lastNode;
	private PlayerNode p;
	private Traversator traverse;
	private Node nextPosition;
	private boolean canMove;
	private NnFight nnfight;
	private int outcome;

	// for the game over menu 
	private GameOverOption gameOver; 

	// this to for spiders weapon and strength 
	private int weapon;
	private int attack;
	// nn spider strength
	private int spiderStrength;

	// maze passes stuff to the constructor here 
	public NnFuzzyFight(int row, int col, int feature, Object lock, Node[][] maze, PlayerNode p, NnFight f) {
		// TODO Auto-generated constructor stub
		this.row = row;
		this.col = col;
		this.feature = feature;
		// player
		this.p = p;

		node.setCol(col);
		node.setRow(row);
		node.setType(feature);

		// assign the lock 
		this.lock = lock;
		this.maze = maze;

		// set the default player health 
		p.setPlayerHealth(100);
		// set the default player weapon 
		p.setWeapon(0);

		// this is for the neural network so it doesn't train every-time 
		this.nnfight = f;

		// this is where heuristic searches are implemented 
		// i have 5 spiders that use the searches to find the player when they get close
		// to him

		// fuzzy spiders 
		if(feature == 6) {
			// assign a search 
			// use the black spider to search player 
			traverse = new AStarTraversator(p);
		}
		if(feature == 7) {
			// use the blue spider to search player 
			traverse = new BasicHillClimbingTraversator(p);
		}
		if(feature == 8) {
			// use the brown spider to search player 
			traverse = new DepthLimitedDFSTraversator(10,p);
		}
		// neural net spiders 
		if(feature == 11) {
			// use the orange spider to search player 
			traverse = new DepthLimitedDFSTraversator(10,p);
		}
		if(feature == 13) {
			// use the yellow spider to search player 
			traverse = new BasicHillClimbingTraversator(p);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				Thread.sleep(1000);

				// fuzzy stuff and neural network stuff 
				// this is to assign the searches only when the player is in range 
				// this was done to make it a bit faster
				if( feature == 6 || feature == 7 || feature == 8 || feature == 11 || feature == 13 && (canMove && node.getHeuristic(p) < 10)){
					traverse(node.getRow(), node.getCol(), traverse);
				}
				// fuzzy stuff
				if(node.getHeuristic(p) == 1) {
					// when spider is near the player 
					// damage the players health
					// fuzzy fight 
					fuzzyFight(feature);
				}else if(node.getHeuristic(p) < 10){
					// here the spider decides what to do
					// when the player comes in contact 
					// neural net stuff
					fightNn(p.getPlayerHealth(),feature);    
				}else {
					randomMove();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// O(2N)
	// this method is used to follow the player when in reach
	// the spider will start following th player when the player is in range of 10 nodes
	private void followPlayer() {
		// TODO Auto-generated method stub
		if(nextPosition != null){
			synchronized(lock){
				// Figure out all the nodes around
				Node[] surroundingNodes = node.adjacentNodes(maze);
				//List of empty surrounding nodes
				ArrayList<Node> emptySurroundingNodes = new ArrayList<>();
				// Check if they are empty
				for(Node n : surroundingNodes){
					if(n.getType() == -1)
					{
						emptySurroundingNodes.add(n);
					}
				}

				// Check if they are empty
				for(Node n : emptySurroundingNodes){
					if(nextPosition.equals(n) )
					{		
						//New position of the object
						int newPositionX, newPositionY;
						//Previous position of the object
						int previousPositonX = node.getRow(), previousPositionY = node.getCol();

						System.out.println();
						newPositionX = nextPosition.getRow();
						newPositionY = nextPosition.getCol();

						node.setRow(newPositionX);
						node.setCol(newPositionY);

						maze[newPositionX][newPositionY] = node;
						maze[previousPositonX][previousPositionY] = new Node(previousPositonX, previousPositionY, -1);

						nextPosition = null;
						canMove = false;
						return;
					}	
				}
				// Move to random in empty
				randomMove();

				nextPosition = null;
				canMove = false;
				return;
			}
		}
		else{
			randomMove();

			canMove = false;
		}
	}

	// O(N)
	// the spiders use this method to move around randomly 
	public void randomMove() {

		synchronized(lock){
			// Figure out all the nodes around
			Node[] surroundingNodes = node.adjacentNodes(maze);
			//List of empty surrounding nodes
			ArrayList<Node> emptySurroundingNodes = new ArrayList<>();


			// Check if they are empty
			for(Node n : surroundingNodes){
				if(n.getType() == -1 && n != lastNode)
				{
					emptySurroundingNodes.add(n);
				}
			}                        
			// if their are emoty nodes then move to new location 
			if(emptySurroundingNodes.size() > 0){
				// get a random value to move to 
				int position = random.nextInt(emptySurroundingNodes.size());

				//New position of the object
				int newPositionX, newPositionY;
				//Previous position of the object
				int previousPositonX = node.getRow(), previousPositionY = node.getCol();

				// assign the new values to the spider node
				newPositionX = emptySurroundingNodes.get(position).getRow();
				newPositionY = emptySurroundingNodes.get(position).getCol();
				node.setRow(newPositionX);
				node.setCol(newPositionY);

				// also assign the last  pos the spider was so it doesnt move back their 
				lastNode = new Node(previousPositonX, previousPositionY, -1);
				maze[newPositionX][newPositionY] = node;
				maze[previousPositonX][previousPositionY] = lastNode;
			}	
			else if(emptySurroundingNodes.size() == 0) {

				// this here makes sure the spiders dont get stuck when they get in a corner
				//as they cant move to the last position

				//New position of the object
				int lastRow, lastcol;
				//Previous position of the object
				int oldrow = node.getRow(), oldCol = node.getCol();

				lastRow = lastNode.getRow();//nextPosition.getRow();
				lastcol = lastNode.getCol();//nextPosition.getCol();

				node.setRow(lastRow);
				node.setCol(lastcol);

				lastNode = new Node(oldrow, oldCol, -1);

				maze[lastRow][lastcol] = node;
				maze[oldrow][oldCol] = lastNode;
			}
		}

	}

	// fuzzy fight //O(1)
	public void fuzzyFight(int spidertype) {

		// check what type of spider it is and assign the weapon strength 
		if(spidertype == 6) {
			attack = 70;
			weapon = 80;
		}else if(spidertype == 7) {
			attack = 65;
			weapon = 65;
		}else if(spidertype == 8) {
			attack = 55;
			weapon = 60;
		}else if(spidertype == 9) {
			attack = 45;
			weapon = 50;
		}else if(spidertype == 10) {
			attack = 40;
			weapon = 40;
		}else if(spidertype == 11) {
			attack = 30;
			weapon = 30;
		}else if(spidertype == 12) {
			attack = 30;
			weapon = 20;
		}else if(spidertype == 13) {
			attack = 20;
			weapon = 20;
		}

		// here we get the new health 
		FuzzyFight f = new FuzzyFight();

		// get the prev health of the player 
		double health = p.getPlayerHealth();
		// get the damage done by the spider 
		double damage = f.PlayerHealth(weapon, attack);
		// calculate the new health
		double newhealth = health - damage;
		// set the players new health
		p.setPlayerHealth(newhealth);

		System.out.println("\n-----------------------Health Stats------------------------");
		System.out.println("Players Old Health is " + health);
		System.out.println("The Spiders weapon strength is " + weapon);
		System.out.println("The Spiders attack strength is " + attack);
		System.out.println("The New Health of the Player " + p.getPlayerHealth());

		// check if the player is dead then end the game 
		if(p.getPlayerHealth() <= 0) {
			gameOver = new GameOverOption();
			gameOver.display("Game Over The Player Has Died");
		}
	}

	// this sets the searching alogrithems and finds them //O(1)
	// looks for the player 
	public void traverse(int row, int col, Traversator t){
		t.traverse(maze, maze[row][col]);
		nextPosition = t.getNextNode();
		if(nextPosition != null){
			canMove = true;
		} else {
			canMove = false;
		}
	}

	// this engages with the spider //O(N) because of for loop inside the action method 
	public void fightNn(double health, double spidertype) {

		System.out.println("\n-----------------------Neural Net Menu------------------------");
		System.out.println("Neural Network Spider is within Range of the Player");

		// assign the spider strength 
		if(spidertype == 6) {
			spiderStrength = 80;
		}else if(spidertype == 7) {
			spiderStrength = 65;
		}else if(spidertype == 8) {
			spiderStrength = 55;
		}else if(spidertype == 9) {
			spiderStrength = 45;
		}else if(spidertype == 10) {
			spiderStrength = 40;	
		}else if(spidertype == 11) {
			spiderStrength = 80;
		}else if(spidertype == 12) {
			spiderStrength = 30;
		}else if(spidertype == 13) {
			spiderStrength = 80;
		}

		//here make it so we can send it to nn
		if(spiderStrength <= 30) {
			spiderStrength = 0;
		}else if(spiderStrength > 30 && spiderStrength < 60) {
			spiderStrength = 1;
		}else if(spiderStrength > 60) {
			spiderStrength = 2;
		}

		System.out.println("The spider strength is " + spiderStrength);

		// checks the health of player 
		if(health <= 30) {
			health = 0;
			System.out.println("Players Health is less than 30");
		}else if(health > 30 && health < 60) {
			health = 1;
			System.out.println("Players Health is > than 30 and < 60 ");
		}else if(health > 60) {
			health = 2;
			System.out.println("Players Health is > 60 ");
		}

		//gets the current weapon of the player 
		double weapon = p.getWeapon();
		System.out.println("The weapon is now " + weapon);

		try {
			// run the nn and get the outcome 
			outcome = nnfight.action(health, weapon , spiderStrength);
			// if the value panic move around 
			if(outcome == 1) {
				System.out.println("The spider is panicking");
				randomMove();
			}//if its attack follow the player 
			else if(outcome == 2) {
				System.out.println("The spider is attacking");
				followPlayer();  
			}// if hide move away 
			else if (outcome == 3) {
				System.out.println("The spider is hiding");
				randomMove();
			}else {
				System.out.println("None of the options");
				randomMove();
			}

		} catch (Exception e) {
		}
	}

}



