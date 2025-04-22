package gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Seat_GUI extends JFrame {
	
	public Seat_GUI() {
		setTitle("Seat");
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.BLACK);
        
        //Phần màn hình
        JPanel pnlNorth = new JPanel();
        ImageIcon imgManHinh = new ImageIcon("img/manHinh.png");
        JLabel imageLabel = new JLabel(imgManHinh);
        pnlNorth.add(imageLabel);
        add(pnlNorth,BorderLayout.NORTH);
        
	}

	 public static void main(String[] args) {
		 Seat_GUI seat_GUI = new Seat_GUI();
		 seat_GUI.setVisible(true);
	    }
}
