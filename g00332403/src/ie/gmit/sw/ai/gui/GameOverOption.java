package ie.gmit.sw.ai.gui;

import javax.swing.*;
import java.awt.*;

public class GameOverOption {
	// exits out of game 
    public void display(String message) {

    	// display the message that the game is over 
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel(message));
        panel.add(new JLabel(""));

        // show dialog box
        JOptionPane.showConfirmDialog(null, panel, "Game Over!", JOptionPane.DEFAULT_OPTION);
        
        // exit the game 
        System.exit(0);
    } 
}
