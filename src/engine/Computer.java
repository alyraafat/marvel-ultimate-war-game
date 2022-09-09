package engine;

import java.util.ArrayList;

import model.abilities.*;
import model.effects.Effect;
import model.effects.EffectType;
import model.effects.Shield;
import model.world.Champion;
import model.world.Cover;
import model.world.Damageable;

import java.awt.*;
public class Computer extends Player{
	public Computer() {
		super(chooseName());
	}
	public static String chooseName() {
		int random = (int) (Math.random()*5 + 1);
		switch (random){
			case 1: return "Alpha"; 
			case 2: return "Beta"; 
			case 3: return "Tera"; 
			case 4: return "Bot"; 
			default: return "Gamma"; 
		}
	}

}