package gui;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import model.world.Cover;
import model.world.Champion;
import model.world.Damageable;


import java.util.ArrayList;

import engine.Game;

public class Board {
	Game game;
    GridPane board;
    String theme;
    Stage s;
    public Square[][] squares = new Square[5][5];
    
    public Board(GridPane board, String theme,Game game,Stage s){
        this.board = board;
        this.theme = theme;
        this.game = game;
        this.s = s;
        makeBoard();
    }


    public void makeBoard(){
    	Object[][] b = game.getBoard();
    	board.getChildren().clear();
        for(int i=0; i<b.length; i++){
            for(int j=0; j<b[i].length; j++){
            	Damageable x = (Damageable) b[i][j];
                Square square = new Square(j,4-i,x);
                if(x instanceof Cover) {
                	VBox vbox = new VBox();
                	vbox.setAlignment(Pos.CENTER);
                	Piece p = new Piece("Cover",x);
                	eventHandler(vbox, p);
                	p.progressBar.setProgress((double)(x.getCurrentHP())/x.getMaxHP());
            		p.progressBar.setId("progressBar-green");
                	if(p.progressBar.getProgress()<0.7) {
                		p.progressBar.setId("progressBar-orange");
                	}
                	if(p.progressBar.getProgress()<0.3) {
                		p.progressBar.setId("progressBar-red");
                	}
//                	if(p.progressBar.getProgress()<0.6) {
//                		p.progressBar.setId("progressBar-red");
//                		Disintegration dis = new Disintegration();
//                		Popup pop = new Popup();
//                		pop.getContent().add(dis.createContent(p.getImage()));
//                		pop.show(s);
//                		pop.setAutoHide(true);
//                	}
                	
                	vbox.getChildren().addAll(p.progressBar,p);
                	Tooltip hp = new Tooltip(x.getCurrentHP()+" hp");
                    square.getChildren().addAll(vbox);
                    Tooltip.install(p.progressBar, hp);
                }else if(x instanceof Champion){
                	VBox vbox = new VBox();
                	vbox.setAlignment(Pos.CENTER);
                	Piece p = new Piece(((Champion)x).getName(),x);
                	eventHandler(vbox, p);
                	p.progressBar.setProgress((double)(x.getCurrentHP())/x.getMaxHP());
                	p.progressBar.setId("progressBar-green");
                	if(p.progressBar.getProgress()<0.7) {
                		p.progressBar.setId("progressBar-orange");
                	}
                	if(p.progressBar.getProgress()<0.3) {
                		p.progressBar.setId("progressBar-red");
                	}
//                	if(p.progressBar.getProgress()<0.6) {
//                		p.progressBar.setId("progressBar-red");
//                		Disintegration dis = new Disintegration();
//                		dis.createContent(p.getImage());
//                	}
                	
                	vbox.getChildren().addAll(p.progressBar,p);
                	Tooltip hp = new Tooltip(x.getCurrentHP()+" hp");
                    square.getChildren().addAll(vbox);
                    Tooltip.install(p.progressBar, hp);
                }
//                square.setName("Square" + i + j);
                square.setPrefHeight(75);
                square.setPrefWidth(75);
                square.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
//                setTheme(square, theme, j, i);
                board.add(square, j,4-i, 1, 1);
                squares[j][4-i] = square;
            }
        }
//        for(Damageable c: game.getDead()) {
//        	int y = (int) c.getLocation().getY();
//        	int x = (int) c.getLocation().getX();
////        	Square square = new Square(y,4-x,c);
//        	Disintegration dis = new Disintegration();
//        	if(c instanceof Champion) {
//        		Piece p = new Piece(((Champion)c).getName(),c);
////            	square.getChildren().add(p);
//        		Popup pop = new Popup();
//        		pop.getContent().add(dis.createContent(p.getImage()));
//        		pop.show(s);
//        		pop.setAutoHide(true);        
//        	}else {
//            	Piece p = new Piece("Cover",c);
//            	Popup pop = new Popup();
//        		pop.getContent().add(dis.createContent(p.getImage()));
//        		pop.show(s);
//        		pop.setAutoHide(true);        	
//        	}
//        }
//        game.getDead().clear();
    }
    private void eventHandler(VBox vbox,Piece p) {
    	vbox.setOnMouseEntered(e->{
    		p.progressBar.setVisible(true);
    	});
    	vbox.setOnMouseExited(e->{
    		p.progressBar.setVisible(false);
    	});
    }
    private void setTheme(Square square, String theme, int i, int j){
        Color color1 = Color.web("#ffffff00");
        Color color2 = Color.web("#ffffff00");

        switch (theme) {
            case "Coral" -> {
                color1 = Color.web("#b1e4b9");
                color2 = Color.web("#70a2a3");
            }
            case "Dusk" -> {
                color1 = Color.web("#cbb7ae");
                color2 = Color.web("#716677");
            }
            case "Wheat" -> {
                color1 = Color.web("#eaefce");
                color2 = Color.web("#bbbe65");
            }
            case "Marine" -> {
                color1 = Color.web("#9dacff");
                color2 = Color.web("#6f74d2");
            }
            case "Emerald" -> {
                color1 = Color.web("#adbd90");
                color2 = Color.web("#6e8f72");
            }
            case "Sandcastle" -> {
                color1 = Color.web("#e4c16f");
                color2 = Color.web("#b88b4a");
            }
        }

        if((i+j)%2==0){
            square.setBackground(new Background(new BackgroundFill(color1, CornerRadii.EMPTY, Insets.EMPTY)));
        }else{
            square.setBackground(new Background(new BackgroundFill(color2, CornerRadii.EMPTY, Insets.EMPTY)));
        }

    }
}

//    private void addPiece(Square square, Piece piece){
//        square.getChildren().add(piece);
//        square.occupied = true;
//    }
