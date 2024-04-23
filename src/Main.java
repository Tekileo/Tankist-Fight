import java.awt.Color;

import javax.swing.JFrame;


public class Main {

	public static void main(String[] args) {
		JFrame obj=new JFrame();
		JFrame menu=new JFrame();
		Gameplay gamePlay = new Gameplay();

		menu.setBounds(10, 10, 800, 630);
		menu.setTitle("Tankist Fight");	
		menu.setBackground(Color.gray);
		
		menu.setVisible(false);
		
		obj.setBounds(10, 10, 800, 630);
		obj.setTitle("Tankist Fight");	
		obj.setBackground(Color.gray);
		obj.setResizable(false);
		
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		obj.add(gamePlay);
		obj.setVisible(true);



	}

}
