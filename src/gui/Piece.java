package gui;

import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import model.world.Champion;
import model.world.Damageable;

import java.util.ArrayList;

import engine.Game;

public class Piece extends ImageView {
    String name;
    ArrayList<String> possibleMoves;
	ProgressBar progressBar;
	Damageable d;
    public Piece(String name,Damageable d){
    	super(getLogo(name));
    	this.d = d;
    	progressBar = new ProgressBar(1);
    	progressBar.setMaxHeight(10);
    	progressBar.setMaxWidth(60);
    	progressBar.setVisible(false);
//    	Tooltip hp = new Tooltip(d.getCurrentHP()+" hp");
//        Tooltip.install(progressBar, hp);
    	this.name = name;
    	this.setFitHeight(60);
    	this.setFitWidth(60);
    	this.setId(name);
        addEventHandler();
    }
    public static Image getLogo(String name) {
    	String path = "";
    	switch(name) {
    	case "Captain America":path = "file:./assets/symbols/captainAmericaSymbol.gif" ;break;
    	case "Deadpool":path = "file:./assets/symbols/deadPool.png" ;break;
    	case "Dr Strange":path = "file:./assets/symbols/drStrange.png" ;break;
    	case "Electro":path = "file:./assets/symbols/electro.png" ;break;
    	case "Ghost Rider":path = "file:./assets/symbols/ghostRider.png" ;break;
    	case "Hela":path = "file:./assets/symbols/hela.png" ;break;
    	case "Hulk":path = "file:./assets/symbols/hulk.png" ;break;
    	case "Iceman":path = "file:./assets/symbols/iceman.png" ;break;
    	case "Ironman":path = "file:./assets/symbols/ironman.png" ;break;
    	case "Loki":path = "file:./assets/symbols/loki.png" ;break;
    	case "Quicksilver":path = "file:./assets/symbols/quickSilver.png" ;break;
    	case "Spiderman":path = "file:./assets/symbols/spiderman.png" ;break;
    	case "Thor":path = "file:./assets/symbols/thor.png" ;break;
    	case "Venom": path = "file:./assets/symbols/venom.png" ; break;
    	case "Yellow Jacket": path = "file:./assets/symbols/yellowJacket.png";break;
    	case "Cover": path = "file:./assets/symbols/cover.png";break;
    	}
    	return new Image(path);
    }
    public String getLogoPath(String name) {
    	String path = "";
    	switch(name) {
    	case "Captain America":path = "file:./assets/symbols/captainAmericaSymbol.gif" ;break;
    	case "Deadpool":path = "file:./assets/symbols/deadPool.png" ;break;
    	case "Dr Strange":path = "file:./assets/symbols/drStrange.png" ;break;
    	case "Electro":path = "file:./assets/symbols/electro.png" ;break;
    	case "Ghost Rider":path = "file:./assets/symbols/ghostRider.png" ;break;
    	case "Hela":path = "file:./assets/symbols/hela.png" ;break;
    	case "Hulk":path = "file:./assets/symbols/hulk.png" ;break;
    	case "Iceman":path = "file:./assets/symbols/iceman.png" ;break;
    	case "Ironman":path = "file:./assets/symbols/ironman.png" ;break;
    	case "Loki":path = "file:./assets/symbols/loki.png" ;break;
    	case "Quicksilver":path = "file:./assets/symbols/quickSilver.png" ;break;
    	case "Spiderman":path = "file:./assets/symbols/spiderman.png" ;break;
    	case "Thor":path = "file:./assets/symbols/thor.png" ;break;
    	case "Venom": path = "file:./assets/symbols/venom.png" ; break;
    	case "Yellow Jacket": path = "file:./assets/symbols/yellowJacket.png";break;
    	case "Cover": path = "file:./assets/symbols/cover.png";break;
    	}
    	return path;
    }

    private void addEventHandler(){
//    	this.setOnMouseEntered(e->{
//    		progressBar.setProgress(d.getCurrentHP());
//        	progressBar.setVisible(true); 
//    	});

    }

//    public void getAllPossibleMoves() {}
//
//    public void showAllPossibleMoves(boolean val){
//        if(val){
//            Glow glow = new Glow();
//            glow.setLevel(0.3);
//            for(String move : possibleMoves){
//                Square square = getSquareByName(move);
//                square.setEffect(glow);
//
//                Piece piece = getPieceByName(move);
//                if(piece == null) continue;
//                if(piece.type.equals("King")){
//                    square.setBorder(new Border(new BorderStroke(Color.DARKRED,
//                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
//                }
//                else{
//                    square.setBorder(new Border(new BorderStroke(Color.BLACK,
//                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.2))));
//                }
//            }
//        }
//        else{
//            for(String move : possibleMoves){
//                Square square = getSquareByName(move);
//                square.setEffect(null);
//                square.setBorder(new Border(new BorderStroke(Color.BLACK,
//                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
//            }
//        }
//    }

//    public Square getSquareByName(String name){
//        for(Square square : Game.cb.squares){
//            if(square.name.equals(name)){
//                return square;
//            }
//        }
//
//        return null;
//    }

//    public Piece getPieceByName(String name){
//        for(Square square : Game.cb.squares){
//            if(square.getChildren().size() == 0) continue;
//
//            if(square.name.equals(name))
//                return (Piece) square.getChildren().get(0);
//
//        }
//        return null;
//    }


}