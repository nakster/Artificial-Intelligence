package ie.gmit.sw.ai;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import ie.gmit.sw.ai.node.PlayerNode;
import ie.gmit.sw.ai.sprites.Sprite;

// gameView draws the game characters
public class GameView extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_VIEW_SIZE = 800;
	private int cellspan = 5;	
	private int cellpadding = 2;
	private Maze maze;
	private Sprite[] sprites;
	private int enemy_state = 5;
	private Timer timer;
	private int currentRow;
	private int currentCol;
	private boolean zoomOut = false;
	private int imageIndex = -1;
//	private int offset = 48; //The number 0 is ASCII 48.
//	private Color[] reds = {new Color(255,160,122), new Color(139,0,0), new Color(255, 0, 0)}; //Animate enemy "dots" to make them easier to see
	private PlayerNode p;

	// constructor 
	public GameView(Maze maze) throws Exception{
		this.maze = maze;
		setBackground(Color.LIGHT_GRAY);
		setDoubleBuffered(true);
		timer = new Timer(300, this);
		timer.start();
	}

	// set setCurrentRow
	public void setCurrentRow(int row) {
		if (row < cellpadding){
			currentRow = cellpadding;
		}else if (row > (maze.size() - 1) - cellpadding){
			currentRow = (maze.size() - 1) - cellpadding;
		}else{
			currentRow = row;
		}
	}

	public void setCurrentCol(int col) {
		if (col < cellpadding){
			currentCol = cellpadding;
		}else if (col > (maze.size() - 1) - cellpadding){
			currentCol = (maze.size() - 1) - cellpadding;
		}else{
			currentCol = col;
		}
	}

	// this here sets the player s
	public void setPlayer(PlayerNode p) {
		this.p = p;
	}
	// this is used for updating the health and weapon of the player 
	public PlayerNode getPlayer() {
		return this.p;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		cellspan = zoomOut ? maze.size() : 5;         
		final int size = DEFAULT_VIEW_SIZE/cellspan;
		g2.drawRect(0, 0, GameView.DEFAULT_VIEW_SIZE, GameView.DEFAULT_VIEW_SIZE);

		for(int row = 0; row < cellspan; row++) {
			for (int col = 0; col < cellspan; col++){  
				int x1 = col * size;
				int y1 = row * size;

				// this was made into a int  
				int ch = 0;

				if (zoomOut){
					ch = maze.get(row, col).getType();
					// checks if they are spiders 
					if (ch >= 5){

						//change the colours of the spiders
						if (row == currentRow && col == currentCol){
							g2.setColor(Color.YELLOW);
						}else if(ch == 6) {
							g2.setColor(Color.BLACK);
						}else if(ch == 7) {
							g2.setColor(Color.BLUE);
						}else if(ch == 8) {
							g2.setColor(new Color(102, 51, 0));
						}else if(ch == 9) {
							g2.setColor(Color.GREEN);
						}else if(ch == 10) {
							g2.setColor(Color.WHITE);
						}else if(ch == 11) {
							g2.setColor(Color.ORANGE);
						}else if(ch == 12) {
							g2.setColor(Color.RED);
						}else if(ch == 13) {
							g2.setColor(new Color(255,255,204));
						}else if(ch == 14) {
							g2.setColor(Color.PINK);
						}else {
							// fixed when player goes to the end it doesnt change colour 
							g2.setColor(Color.YELLOW);
						}
						g2.fillRect(x1, y1, size, size);
					}
				}else{
					ch = maze.get(currentRow - cellpadding + row, currentCol - cellpadding + col).getType();
				}

				imageIndex = (int) ch;
				//imageIndex -= offset;
				if (imageIndex < 0){
					g2.setColor(Color.LIGHT_GRAY);//Empty cell
					g2.fillRect(x1, y1, size, size);   			
				}else{
					g2.drawImage(sprites[imageIndex].getNext(), x1, y1, null);
				}
			}
		}
	}

	// toggle boolean to switch 
	public void toggleZoom(){
		zoomOut = !zoomOut;		
	}

	public void actionPerformed(ActionEvent e) {	
		if (enemy_state < 0 || enemy_state == 5){
			enemy_state = 6;
		}else{
			enemy_state = 5;
		}
		this.repaint();
	}

	public void setSprites(Sprite[] sprites){
		this.sprites = sprites;
	}
}