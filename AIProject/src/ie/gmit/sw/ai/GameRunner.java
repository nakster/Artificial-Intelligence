package ie.gmit.sw.ai;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ie.gmit.sw.ai.nn.NnFight;
import ie.gmit.sw.ai.node.Node;
import ie.gmit.sw.ai.node.PlayerNode;
import ie.gmit.sw.ai.sprites.ControlledSprite;
import ie.gmit.sw.ai.sprites.Sprite;

// this class runs the game 
public class GameRunner implements KeyListener {
	private static final int MAZE_DIMENSION = 100;
	private static final int IMAGE_COUNT = 15;
	public static boolean GAME_OVER = false;
	private ControlledSprite player;
	private PlayerNode p;
	private GameView view;
	private Maze model;
	private int currentRow;
	private int currentCol;
	public int attack;
	private NnFight nfight = new NnFight();

	// constuctor 
	public GameRunner() throws Exception {

		//train it the neural network and pass it to the maze constructor
		nfight.train();

		model = new Maze(MAZE_DIMENSION, nfight);
		view = new GameView(model);

		//get the sprites 
		Sprite[] sprites = getSprites();
		view.setSprites(sprites);

		// player is being placed inside the update view 
		updateView();
		
		// set the dimensions
		Dimension d = new Dimension(GameView.DEFAULT_VIEW_SIZE, GameView.DEFAULT_VIEW_SIZE);
		view.setPreferredSize(d);
		view.setMinimumSize(d);
		view.setMaximumSize(d);

		// Title of the frame 
		JFrame f = new JFrame("GMIT - B.Sc. in Computing (Software Development)");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.addKeyListener(this);
		f.getContentPane().setLayout(new FlowLayout());
		f.add(view);
		f.setSize(1000, 1000);
		f.setLocation(100, 100);
		f.pack();
		f.setVisible(true);
	}

	// update the view and also set the player 
	private void updateView() {

		// set the player here 
		view.setPlayer(model.getP());
		currentRow = model.getP().getRow();
		currentCol = model.getP().getCol();
		// update view 
		view.setCurrentRow(currentRow);
		view.setCurrentCol(currentCol);

	}
	
	// keys for the movement of the player 
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT && currentCol < MAZE_DIMENSION - 1) {
			if (isValidMove(currentRow, currentCol + 1)) {
				player.setDirection(Direction.RIGHT);
				currentCol++;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT && currentCol > 0) {
			if (isValidMove(currentRow, currentCol - 1)) {
				player.setDirection(Direction.LEFT);
				currentCol--;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_UP && currentRow > 0) {
			if (isValidMove(currentRow - 1, currentCol)) {
				player.setDirection(Direction.UP);
				currentRow--;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN && currentRow < MAZE_DIMENSION - 1) {
			if (isValidMove(currentRow + 1, currentCol)) {
				player.setDirection(Direction.DOWN);
				currentRow++;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_Z) {
			view.toggleZoom();
		} else {
			return;
		}
		// update the view 
		updateView();
	}

	public void keyReleased(KeyEvent e) {
	} // Ignore

	public void keyTyped(KeyEvent e) {
	} // Ignore

	// This method here checks if the player comes in contact with the itemSprites //O(N)
	private boolean isValidMove(int row, int col) {
		// for empty space  
		if (row <= model.size() - 1 && col <= model.size() - 1 && model.get(row, col).getType() == -1) {
			//System.out.println(model.get(row, col));
			model.set(currentRow, currentCol, model.get(row, col));
			model.set(row, col, model.getP());
			return true;
			//this checks if its an item and picks it up
		}else if(model.get(row, col).getType() >= 1 && model.get(row, col).getType() <= 4) {
			this. p = view.getPlayer();
			System.out.println("\n-----------------------PickUp Menu------------------------");
			if(model.get(row, col).getType() == 1) {
				System.out.println("The Player has picked up Sword");
				//increase the health 
				addHealth(10);
				p.setWeapon(0);
				//set it to hedge 
				model.set(row, col, new Node(row, col, 0));
			}else if(model.get(row, col).getType() == 2) {
				System.out.println("The Player has picked up Help");
				//increase the health 
				addHealth(50);
				//p.setWeapon("Help");
				//set it to hedge 
				model.set(row, col, new Node(row, col, 0));
			}else if(model.get(row, col).getType() == 3) {
				System.out.println("The Player has picked up Bomb");
				//increase the health 
				addHealth(20);
				p.setWeapon(1);
				//set it to hedge 
				model.set(row, col, new Node(row, col, 0));

			}else if(model.get(row, col).getType() == 4) {
				System.out.println("The Player has picked up H-Bomb");
				//increase the health 
				addHealth(30);
				p.setWeapon(2);
				//set it to hedge 
				model.set(row, col, new Node(row, col, 0));
			}

			return true;
			// check if you found the exit and exit 
		}else if (row <= model.size() - 1 && col <= model.size() - 1 && model.get(row, col).getType() == -1|| model.get(row, col).getType() == 14){
			// when the player finds the exit door he exists the game 
			JFrame winning = new JFrame("Winning !!");
			winning.setLayout(new GridLayout(1, 1));
			winning.setSize(200, 200);
			JPanel panel = new JPanel(new FlowLayout());
			JLabel label = new JLabel("Congratulations you won !");
			panel.add(label);
			winning.add(panel);
			winning.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			winning.setVisible(true);

			System.out.println("Congratulations you Won!!!");
			//System.exit(0);
			return true;
		}
		else {
			return false; // Can't move
		}
	}

	// this increases the health of the spider 
	public void addHealth(int add) {

		double health = p.getPlayerHealth();
		double addHealth = health + add;
		p.setPlayerHealth(addHealth);
		System.out.println("The New Health Of the Player is " + p.getPlayerHealth());
	}

	private Sprite[] getSprites() throws Exception {
		// Read in the images from the resources directory as sprites. Note that each
		// sprite will be referenced by its index in the array, e.g. a 3 implies a
		// Bomb...
		// Ideally, the array should dynamically created from the images...

		player = new ControlledSprite("Main Player", 3, "resources/images/player/d1.png",
				"resources/images/player/d2.png", "resources/images/player/d3.png", "resources/images/player/l1.png",
				"resources/images/player/l2.png", "resources/images/player/l3.png", "resources/images/player/r1.png",
				"resources/images/player/r2.png", "resources/images/player/r3.png");

		Sprite[] sprites = new Sprite[IMAGE_COUNT];
		sprites[0] = new Sprite("Hedge", 1, "resources/images/objects/hedge.png");
		sprites[1] = new Sprite("Sword", 1, "resources/images/objects/sword.png");
		sprites[2] = new Sprite("Help", 1, "resources/images/objects/help.png");
		sprites[3] = new Sprite("Bomb", 1, "resources/images/objects/bomb.png");
		sprites[4] = new Sprite("Hydrogen Bomb", 1, "resources/images/objects/h_bomb.png");
		sprites[5] = player;
		sprites[6] = new Sprite("Black Spider", 2, "resources/images/spiders/black_spider_1.png",
				"resources/images/spiders/black_spider_2.png");
		sprites[7] = new Sprite("Blue Spider", 2, "resources/images/spiders/blue_spider_1.png",
				"resources/images/spiders/blue_spider_2.png");
		sprites[8] = new Sprite("Brown Spider", 2, "resources/images/spiders/brown_spider_1.png",
				"resources/images/spiders/brown_spider_2.png");
		sprites[9] = new Sprite("Green Spider", 2, "resources/images/spiders/green_spider_1.png",
				"resources/images/spiders/green_spider_2.png");
		sprites[10] = new Sprite("Grey Spider", 2, "resources/images/spiders/grey_spider_1.png",
				"resources/images/spiders/grey_spider_2.png");
		sprites[11] = new Sprite("Orange Spider", 2, "resources/images/spiders/orange_spider_1.png",
				"resources/images/spiders/orange_spider_2.png");
		sprites[12] = new Sprite("Red Spider", 2, "resources/images/spiders/red_spider_1.png",
				"resources/images/spiders/red_spider_2.png");
		sprites[13] = new Sprite("Yellow Spider", 2, "resources/images/spiders/yellow_spider_1.png",
				"resources/images/spiders/yellow_spider_2.png");

		// exit sprite 
		sprites[14] = new Sprite("Door", 1, "resources/images/objects/exit2.png");

		return sprites;
	}

	// runner 
	public static void main(String[] args) throws Exception {
		new GameRunner();
	}
}