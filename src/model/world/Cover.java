package model.world;

import java.awt.Point;

public class Cover implements Damageable {
	private int currentHP;
	private int maxHP;
	private Point location;

	public Cover(int x, int y) {
		this.currentHP = (int)(( Math.random() * 900) + 100);
		maxHP = currentHP;
		location = new Point(x, y);
	}

	public int getCurrentHP() {
		return this.currentHP;
	}

	public void setCurrentHP(int newHp) {
		if (newHp < 0) {
			currentHP = 0;
		
		} else
			currentHP = newHp;
	}

	public Point getLocation() {
		return location;
	}

	public int getMaxHP() {
		return maxHP;
	}

	

	

}
