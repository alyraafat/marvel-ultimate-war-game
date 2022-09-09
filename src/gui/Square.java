package gui;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import model.world.Damageable;

public class Square extends StackPane {	
	int x,y;
	boolean occupied;
	String name;
	Damageable d;
	public Square(int x,int y, Damageable d) {
		this.x = x;
		this.y = y;
		this.occupied = false;
		this.d = d;
	}

	public Square(Node... arg0) {
		super(arg0);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	

}
