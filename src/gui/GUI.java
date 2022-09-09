package gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import engine.Computer;
import engine.Game;
import engine.Player;
import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Effect;
import model.world.Champion;
import model.world.Direction;
import model.world.Hero;
import model.world.Villain;

public class GUI extends Application {
	static final Color white = Color.rgb(235, 235, 235);
	Game g;
	Scene startScene;
	int firstPlayerChoose;
	int secondPlayerChoose;
	int leaderOneIndex;
	int leaderTwoIndex;
	int recX,recY;
	int abilityIndex;
	Player first;
	Player second;
	Board board;
	GridPane pane;
	Player winner;
	ArrayList<String> team1;
	ArrayList<String> team2;
	Button restartWithDifferentTeams;
	Button restartWithSameTeams;
	Button moveBtn;
	Button attackBtn;	
	VBox gameVbox;
	boolean singleTargetOn;
	boolean attackOn;
	boolean moveOn;
	HBox turnOrderHbox;
	VBox bigVbox;
	MediaPlayer mp;
	MediaPlayer champSongMP;
	
	public void start(Stage s) throws Exception {  
		team1 = new ArrayList<String>();
		team2 = new ArrayList<String>();
		firstPlayerChoose = 0;
		secondPlayerChoose = 0;
		leaderOneIndex =-1;
		leaderTwoIndex=-1;
		winner = null;
		recX = recY = 0;
		try {
			startGame();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		VBox startSceneVbox = new VBox();
		File file = new File("./assets/videos/opening3.mp4");
		Media opening = new Media(file.toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(opening);  
		mediaPlayer.setAutoPlay(true);  
		MediaView mediaView = new MediaView (mediaPlayer);
		mediaView.setFitHeight(Screen.getPrimary().getBounds().getHeight());
		mediaView.setFitWidth(Screen.getPrimary().getBounds().getWidth());
//		Button play = new Button("play");
//		play.setOnAction(e->{
//			mediaPlayer.play();
//		});
//		File theme = new File("./assets/songs/TheAvengersTheme2.mp3");
//		Media backMusix = new Media(theme.toURI().toString());
//		MediaPlayer mp = new MediaPlayer(backMusix);
//		mp.setCycleCount(MediaPlayer.INDEFINITE);
		VBox t = new VBox(mediaView);
		t.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));		
		t.setAlignment(Pos.CENTER);
		t.setMinWidth(Screen.getPrimary().getBounds().getWidth());
		t.setMinHeight(Screen.getPrimary().getBounds().getHeight());
		File theme = new File("./assets/songs/TheAvengersTheme2.mp3");
		Media backMusix = new Media(theme.toURI().toString());
		mp = new MediaPlayer(backMusix);
		MediaView mediaViewTheme = new MediaView (mp);
//		mp.setAutoPlay(true);  
//		mp.setCycleCount(MediaPlayer.INDEFINITE);
		mp.setOnEndOfMedia(new Runnable() {
	       public void run() {
	         mp.seek(Duration.ZERO);
	       }
		});
		
		mediaView.setOnMouseClicked(e->{
			mediaPlayer.pause();
			mp.setAutoPlay(true);  
//			mp.play();
			makeFadeOut(startScene.getRoot());
	    	startScene.setRoot(startSceneVbox);
//    		startScene.getRoot().getChildrenUnmodifiable().add(mediaViewTheme);
	    	makeFadeIn(startScene.getRoot());
		});
		mediaPlayer.play();

		//backgrounds 
		Image image = new Image("file:./assets/fightScene.jpg"); 
    	ImageView imageView = new ImageView(image);
    	Image image2 = new Image("file:./assets/versusScreen.jpg"); 
    	ImageView imageView2 = new ImageView(image2);
    	imageView2.setFitWidth(150);
    	Image image5 = new Image("file:./assets/choosingBackGround.jpg"); 
    	ImageView imageView5 = new ImageView(image2);
    	imageView5.setFitWidth(150);
    	Image image3 = new Image("file:./assets/startBtn.jfif");
    	ImageView imageView3 = new ImageView(image3);
    	Image icon = new Image("file:./assets/gameIcon.png");
        // apply a shadow effect.
    	imageView3.setFitHeight(70);
    	imageView3.setFitWidth(70);
    	imageView3.setTranslateX(100);	
    	Rectangle clip = new Rectangle(imageView3.getFitWidth(), imageView3.getFitHeight());
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        imageView3.setClip(clip);
    	Image image4 = new Image("file:./assets/choosingScreen.jpg");
        ImageView imageView4 = new ImageView(image4);
        Image image6 = new Image("file:./assets/winner.jpeg");
        ImageView imageView6 = new ImageView(image6);
    	BackgroundImage bImg = new BackgroundImage(
    			image,
                 BackgroundRepeat.NO_REPEAT,
                 BackgroundRepeat.NO_REPEAT,
                 BackgroundPosition.CENTER,
                 new BackgroundSize(100, 100, true, true, true, true)
        );
    	BackgroundImage bImg2 = new BackgroundImage(
    			image2,
    			BackgroundRepeat.NO_REPEAT,
    			BackgroundRepeat.NO_REPEAT,
    			BackgroundPosition.CENTER,
    			new BackgroundSize(100, 100, true, true, true, true)
    			);
    	BackgroundImage bImg3 = new BackgroundImage(
    			image4,
    			BackgroundRepeat.NO_REPEAT,
    			BackgroundRepeat.NO_REPEAT,
    			BackgroundPosition.CENTER,
    			new BackgroundSize(100, 100, true, true, true, true)
    			);
    	BackgroundImage bImg4 = new BackgroundImage(
    			image5,
    			BackgroundRepeat.NO_REPEAT,
    			BackgroundRepeat.NO_REPEAT,
    			BackgroundPosition.CENTER,
    			new BackgroundSize(100, 100, true, true, true, true)
    			);
    	BackgroundImage bImg6 = new BackgroundImage(
    			image6,
    			BackgroundRepeat.NO_REPEAT,
    			BackgroundRepeat.NO_REPEAT,
    			BackgroundPosition.CENTER,
    			new BackgroundSize(100, 100, true, true, true, true)
    			);
    	String css = this.getClass().getResource("app.css").toExternalForm();
//		StackPane sp = new StackPane(t,chooseGameMode);
        startScene=new Scene(t,600,400);
        TextField tf1 = textField("Please enter the name of the First Player");
        tf1.setFont(getFont("",20));
        tf1.setMinWidth(330);
        tf1.setOpacity(0.5);
        tf1.setBorder(new Border(new BorderStroke(Color.rgb(255,239,91),Color.rgb(255,239,91),Color.rgb(255,239,91),Color.rgb(255,239,91)
        		,BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,BorderStrokeStyle.SOLID,BorderStrokeStyle.SOLID,
        		CornerRadii.EMPTY, BorderWidths.DEFAULT,new Insets(0))));
        TextField tf2 = textField("Please enter the name of the Second Player");
        tf2.setFont(getFont("",20));
        tf2.setMinWidth(330);
        tf2.setBorder(new Border(new BorderStroke(Color.rgb(255,239,91),Color.rgb(255,239,91),Color.rgb(255,239,91),Color.rgb(255,239,91)
        		,BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,BorderStrokeStyle.SOLID,BorderStrokeStyle.SOLID,
        		CornerRadii.EMPTY, BorderWidths.DEFAULT,new Insets(0))));
        tf2.setOpacity(0.5);
        ArrayList<String> names = new ArrayList<String>(); 
        Background bGround = new Background(bImg);
        Background bGround2 = new Background(bImg2);
        Background bGround3 = new Background(bImg3);
        Background bGround4 = new Background(bImg4);
        Background bGround6 = new Background(bImg6);
        ImageView start = new ImageView(new Image("file:./assets/buttons/start.png"));
        start.setId("startbutton");
        start.setFitWidth(300);
        start.setFitHeight(300);
        ListView<ImageView> championListViewFirstPlayer = new ListView<ImageView>();
		championListViewFirstPlayer.setOrientation(Orientation.HORIZONTAL);
		championListViewFirstPlayer.setMinHeight(170);
		championListViewFirstPlayer.setMaxHeight(170);
		ListView<ImageView> championListViewSecondPlayer = new ListView<ImageView>();
		championListViewSecondPlayer.setOrientation(Orientation.HORIZONTAL);
		championListViewSecondPlayer.setMinHeight(170);
		championListViewSecondPlayer.setMaxHeight(170);
		HBox firstPlayerTeamHbox = new HBox(30);
    	firstPlayerTeamHbox.setAlignment(Pos.CENTER);
		HBox secondPlayerTeamHbox = new HBox(30);
		secondPlayerTeamHbox.setAlignment(Pos.CENTER);
		VBox firstPlayerTeamSceneVbox = new VBox(30);
	    firstPlayerTeamSceneVbox.setBackground(bGround4);
	    firstPlayerTeamSceneVbox.setMinHeight(Screen.getPrimary().getBounds().getHeight());
		VBox secondPlayerTeamSceneVbox = new VBox(30);
	    secondPlayerTeamSceneVbox.setBackground(bGround4);
		VBox firstPlayerTeamVbox = new VBox(30);
	    firstPlayerTeamVbox.setAlignment(Pos.TOP_CENTER);
		VBox secondPlayerTeamVbox = new VBox(30);
	    secondPlayerTeamVbox.setAlignment(Pos.TOP_CENTER);
		VBox firstPlayerLeaderVbox= new VBox();
		firstPlayerLeaderVbox.setBackground(bGround4);
	    firstPlayerLeaderVbox.setAlignment(Pos.CENTER);
		VBox secondPlayerLeaderVbox= new VBox();
		secondPlayerLeaderVbox.setBackground(bGround4);
	    secondPlayerLeaderVbox.setAlignment(Pos.CENTER);
		HBox vsMainHbox = new HBox();
		vsMainHbox.setAlignment(Pos.CENTER);
		VBox vsMainVbox = new VBox(30);
		vsMainVbox.setBackground(bGround2);	   
	    vsMainVbox.setMinHeight(Screen.getPrimary().getBounds().getHeight());       
	    vsMainVbox.setMinWidth(Screen.getPrimary().getBounds().getWidth()); 
		Label team1Name = new Label();
		team1Name.setMinWidth(Screen.getPrimary().getBounds().getWidth());
		team1Name.setAlignment(Pos.CENTER);
		Label team2Name = new Label();
		team2Name.setMinWidth(Screen.getPrimary().getBounds().getWidth());
		team2Name.setAlignment(Pos.CENTER);
	    Text gameTitle = new Text("Marvel Avengers:" + "\n" + "Ultimate War");
	    gameTitle.setFont(getFont("",45));
	    gameTitle.setFill(Color.rgb(237, 29, 36));
	    gameTitle.setId("GameTitle");
	    gameTitle.setTranslateX(50);
	    makeFadeInAndOut(gameTitle);
	    makeFadeIn2(gameTitle);
	    tf1.setTranslateX(50);
	    tf2.setTranslateX(50);
	    HBox blank = new HBox();
	    blank.setMinHeight(100);
    	startSceneVbox.setSpacing(30);
    	startSceneVbox.getChildren().addAll(blank,gameTitle,tf1,tf2,start);
    	startSceneVbox.setBackground(bGround3);
        startSceneVbox.setAlignment(Pos.CENTER_LEFT);
//        chooseGameMode.getChildren().addAll(gameTitle,normal,computer);
//        chooseGameMode.setSpacing(30);
//        chooseGameMode.setBackground(bGround3);
//        chooseGameMode.setAlignment(Pos.CENTER_LEFT);
	    start.setOnMouseClicked(e-> {  
        	if(!tf1.getText().isEmpty()&&!tf2.getText().isEmpty()) {
            		names.add(tf1.getText());
            		names.add(tf2.getText());
            		team1Name.setText(names.get(0)+", Choose your team!!!");
            		team1Name.setFont(getFont("Marvel",40));
            		team2Name.setFont(getFont("Marvel",40));
            		team1Name.setTextFill(Color.rgb(0,0,0));
            		team2Name.setTextFill(Color.rgb(0,0,0));
            		team2Name.setText(names.get(1)+", Choose your team!!!");
            		first = new Player(tf1.getText());
            		second = new Player(tf2.getText());
    		    	ObservableList<ImageView> temp = generateChampionListView(Game.getAvailableChampions());
    		    	championListViewFirstPlayer.getItems().addAll(temp);
    		    	makeFadeOut(startScene.getRoot());
    		    	startScene.setRoot(firstPlayerTeamSceneVbox);
//    	    		startScene.getRoot().getChildrenUnmodifiable().add(mediaViewTheme);
    		    	makeFadeIn(startScene.getRoot());	
        	}
        	else {
        		if(!tf1.getText().isEmpty()) {
            		names.add(tf1.getText());
            		team1Name.setText(names.get(0)+", Choose your team!!!");
            		team1Name.setFont(getFont("Marvel",40));
            		team1Name.setTextFill(Color.rgb(0,0,0));
            		first = new Player(tf1.getText());
            		second = new Computer();
    		    	ObservableList<ImageView> temp = generateChampionListView(Game.getAvailableChampions());
    		    	championListViewFirstPlayer.getItems().addAll(temp);
    		    	makeFadeOut(startScene.getRoot());
    		    	startScene.setRoot(firstPlayerTeamSceneVbox);
    		    	makeFadeIn(startScene.getRoot());
            	}else {
            		showAlert("Warning", "Please enter a valid name", AlertType.WARNING);
            	}  
        	}
        }); 
	    Button goToSecondTeamBtn = new Button("Assemble");
		goToSecondTeamBtn.setId("Assemble");
		goToSecondTeamBtn.setTextFill(white);
		goToSecondTeamBtn.setFont(getFont("",14));
	    Button goToFirstPlayerChooseLeaderScreen = new Button("Choose Leader");
	    goToFirstPlayerChooseLeaderScreen.setTextFill(white);
	    goToFirstPlayerChooseLeaderScreen.setFont(getFont("",14));
	    goToFirstPlayerChooseLeaderScreen.setId("Choose-FirstLeader");
	    goToFirstPlayerChooseLeaderScreen.setOnAction(e -> {
	    	Label label = new Label(first.getName() + ", Choose your Leader");
			label.setFont(getFont("Marvel",30));
	    	label.setAlignment(Pos.CENTER);
	    	HBox forLabel = new HBox();
	    	forLabel.getChildren().add(label);
	    	VBox space1 = new VBox();
			space1.setMinHeight(Screen.getPrimary().getBounds().getHeight()/10);
			space1.setMaxHeight(Screen.getPrimary().getBounds().getHeight()/10);
	    	forLabel.setAlignment(Pos.CENTER);
			firstPlayerLeaderVbox.getChildren().addAll(forLabel,makeLeaderHbox(first.getTeam(), true),space1,goToSecondTeamBtn);
    		makeFadeOut(startScene.getRoot());
	    	startScene.setRoot(firstPlayerLeaderVbox);
//    		startScene.getRoot().getChildrenUnmodifiable().add(mediaViewTheme);
	    	makeFadeIn(startScene.getRoot());
		});
		turnOrderHbox = new HBox(10);
		bigVbox = new VBox();
	    VBox endGameVbox = new VBox(10);
	    endGameVbox.setMinWidth(Screen.getPrimary().getBounds().getWidth());
	    endGameVbox.setAlignment(Pos.CENTER);
	    endGameVbox.setBackground(bGround6);
		gameVbox = new VBox(30);
		gameVbox.setBackground(bGround);
		gameVbox.setAlignment(Pos.CENTER);
	    gameVbox.setMinWidth(Screen.getPrimary().getBounds().getWidth());
	    HBox gameBtnsHbox = new HBox(20);
	    HBox gameTeamsAndBoardHbox = new HBox(20);
	    HBox gameFirstPlayerTeamHbox = new HBox(10);
	    gameFirstPlayerTeamHbox.setAlignment(Pos.CENTER);
	    VBox gameFirstPlayerTeamVbox = new VBox(5);
	    gameFirstPlayerTeamVbox.setAlignment(Pos.CENTER);
	    HBox gameSecondPlayerTeamHbox = new HBox(10);
	    gameSecondPlayerTeamHbox.setAlignment(Pos.CENTER);
	    VBox gameSecondPlayerTeamVbox = new VBox(5);
	    gameSecondPlayerTeamVbox.setAlignment(Pos.CENTER);
	    restartWithSameTeams = new Button("Restart with same teams");
	    restartWithSameTeams.setFont(getFont("",14));
	    pane = new GridPane();
	    restartWithSameTeams.setOnAction(e->{
	    	first.getTeam().clear();
	    	second.getTeam().clear();
	    	Game.getAvailableChampions().clear();
	    	Game.getAvailableAbilities().clear();
	    	try {
				startGame();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    	for(int i=0;i<team1.size();i++) {
	    		for(int j=0;j<Game.getAvailableChampions().size();j++) {
	    			if(team1.get(i).equals(Game.getAvailableChampions().get(j).getName())) {
	    	    		first.getTeam().add(Game.getAvailableChampions().get(j));
	    	    		break;
	    			}
	    		}
	    	}
	    	first.setLeader(first.getTeam().get(leaderOneIndex));
	    	for(int i=0;i<team2.size();i++) {
	    		for(int j=0;j<Game.getAvailableChampions().size();j++) {
	    			if(team2.get(i).equals(Game.getAvailableChampions().get(j).getName())) {
	    	    		second.getTeam().add(Game.getAvailableChampions().get(j));
	    	    		break;
	    			}
	    		}	    	
	    	}
	    	second.setLeader(second.getTeam().get(leaderTwoIndex));
	    	singleTargetOn = moveOn = attackOn = false;
	    	g = new Game(first, second);
			makeTurnOrderQueue();
			pane = new GridPane();
			board = new Board(pane,"Dusk",g,s);
			showCurrChamp();
			Label firstPlayerName = new Label(first.getName());
			firstPlayerName.setId("playername");
			firstPlayerName.setTextFill(Color.rgb(160,160,160));
			firstPlayerName.setAlignment(Pos.TOP_CENTER);
			HBox firstPlayerNameH = new HBox();
			firstPlayerNameH.getChildren().add(firstPlayerName);
			firstPlayerNameH.setAlignment(Pos.BASELINE_CENTER);
			Label secondPlayerName = new Label(second.getName());
			secondPlayerName.setAlignment(Pos.TOP_CENTER);
			secondPlayerName.setId("playername");
			secondPlayerName.setTextFill(Color.rgb(160,160,160));
			HBox secondPlayerNameH = new HBox();
			secondPlayerNameH.getChildren().add(secondPlayerName);
			secondPlayerNameH.setAlignment(Pos.BASELINE_CENTER);
			showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
			showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
			gameFirstPlayerTeamVbox.getChildren().clear();
			gameSecondPlayerTeamVbox.getChildren().clear();
			gameFirstPlayerTeamVbox.getChildren().addAll(firstPlayerNameH,gameFirstPlayerTeamHbox);
			gameSecondPlayerTeamVbox.getChildren().addAll(secondPlayerNameH,gameSecondPlayerTeamHbox);
//			gameFirstPlayerTeamVbox.setAlignment(Pos.TOP_LEFT);
			gameFirstPlayerTeamVbox.setMinWidth(Screen.getPrimary().getBounds().getWidth()/3);
			gameFirstPlayerTeamVbox.setMaxWidth(Screen.getPrimary().getBounds().getWidth()/3);
			pane.setAlignment(Pos.CENTER);
//			gameSecondPlayerTeamVbox.setAlignment(Pos.TOP_RIGHT);
			gameSecondPlayerTeamVbox.setMinWidth(Screen.getPrimary().getBounds().getWidth()/3);
			gameSecondPlayerTeamVbox.setMaxWidth(Screen.getPrimary().getBounds().getWidth()/3);
			gameTeamsAndBoardHbox.getChildren().clear();
			gameTeamsAndBoardHbox.getChildren().addAll(gameFirstPlayerTeamVbox,pane,gameSecondPlayerTeamVbox);
			gameVbox.getChildren().removeAll(bigVbox,gameTeamsAndBoardHbox,gameBtnsHbox);
			gameVbox.getChildren().addAll(bigVbox,gameTeamsAndBoardHbox,gameBtnsHbox);
			ImageView gif = getGIF(((Champion) g.getTurnOrder().peekMin()).getName());
			gif.setFitWidth(300);
			gif.setFitHeight(300);
//			if(first.getTeam().contains(g.getTurnOrder().peekMin())) {
//				if(gameSecondPlayerTeamVbox.getChildren().size()==3) {
//					gameSecondPlayerTeamVbox.getChildren().remove(gameSecondPlayerTeamVbox.getChildren().get(2));
//				}
//				if(gameFirstPlayerTeamVbox.getChildren().size()==3) {
//					gameFirstPlayerTeamVbox.getChildren().remove(gameFirstPlayerTeamVbox.getChildren().get(2));
//				}
//				gameFirstPlayerTeamVbox.getChildren().add(gif);
//			}else {
//				if(gameFirstPlayerTeamVbox.getChildren().size()==3) {
//					gameFirstPlayerTeamVbox.getChildren().remove(gameFirstPlayerTeamVbox.getChildren().get(2));
//				}
//				if(gameSecondPlayerTeamVbox.getChildren().size()==3) {
//					gameSecondPlayerTeamVbox.getChildren().remove(gameSecondPlayerTeamVbox.getChildren().get(2));
//				}
//				gameSecondPlayerTeamVbox.getChildren().add(gif);
//			}
			Popup p = new Popup();
			p.getContent().add(gif);
			p.show(s);
			p.setAutoHide(true);
			makeFadeOut(startScene.getRoot());
	    	startScene.setRoot(gameVbox);
	    	makeFadeIn(startScene.getRoot());
	    });
	    restartWithDifferentTeams = new Button("Return to main menu");
	    restartWithDifferentTeams.setFont(getFont("",14));
	    restartWithDifferentTeams.setOnAction(e->{
	    	try {
	    		Game.getAvailableChampions().clear();
		    	Game.getAvailableAbilities().clear();
				start(s);
				s.setFullScreen(true);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	    });
	    attackBtn = new Button("Attack");
	    attackBtn.setTextFill(white);
	    attackBtn.setFont(getFont("",10));
	    moveBtn = new Button("Move");
	    moveBtn.setTextFill(white);
	    moveBtn.setFont(getFont("",10));
	    attackBtn.setOnAction(e->{
	    	attackOn = true;
	    	singleTargetOn = false;
	    	moveOn = false;
	    	attackBtn.setId("button-pressed");
	    	moveBtn.setId("");
	    });
	    attackBtn.setFocusTraversable(false);
	    moveBtn.setOnAction(e->{
	    	moveOn = true;
	    	singleTargetOn = false;
	    	attackOn = false;
	    	moveBtn.setId("button-pressed");
	    	attackBtn.setId("");
	    });
	    Button instructionBtn = new Button("i");
	    instructionBtn.setFont(getFont("Kepler",10));
	    instructionBtn.setTextFill(white);
    	Tooltip instructionTp = new Tooltip("W: direct move or attack upwards "+ "\n" + "S: direct move or attack downwards" + "\n" + "A: direct move or attack left" + "\n" + "D: direct move or attack right" +"\n"+ "Z: use Champion First Ability" + "\n" + "X: use Champion Second Ability" + "\n" + "C: use Champion Third Ability" + "\n" + "V: use Punch if champion is disarmed" + "\n" + "L: use Leader Ability");
    	Tooltip.install(instructionBtn,instructionTp);
    	instructionTp.setFont(getFont("",10));
		Rectangle rec = new Rectangle(75,75);
		rec.setId("rec");
//		File abilitySE = new File("./assets/songs/AbilitySoundEffect.mp3");
//		Media abilitySEM = new Media(abilitySE.toURI().toString());
//		MediaPlayer abilitySEMP = new MediaPlayer(abilitySEM);
	    gameBtnsHbox.getChildren().addAll(attackBtn,moveBtn,instructionBtn);
	    gameBtnsHbox.setAlignment(Pos.CENTER);
	    gameBtnsHbox.setMinWidth(Screen.getPrimary().getBounds().getWidth());
	    gameVbox.setOnKeyPressed(e -> {
	    	   if (e.getCode() == KeyCode.W) {
		        	if(moveOn) {
		        		try {
		        			g.move(Direction.UP);
		        			board.makeBoard();
		        			mp.pause();
		        			File moveSE = new File("./assets/songs/MoveSoundEffect.mp3");
		        			Media moveSEM = new Media(moveSE.toURI().toString());
		        			MediaPlayer moveSEMP = new MediaPlayer(moveSEM);
		        			moveSEMP.play();
		        			moveSEMP.setOnEndOfMedia(new Runnable() {
		        				public void run() {
//		        					abilitySEMP.pause();
		        					mp.play();
		        				}
		        				
		        			});		        			showCurrChamp();
		    				showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
		    				showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
		    				makeTurnOrderQueue();
		        		} catch (NotEnoughResourcesException | UnallowedMovementException e1) {
		        			showAlert("Can not Move UP", e1.getMessage(), AlertType.WARNING);
		        		}
		        	}else if(attackOn) {
		        		try {
		    				g.attack(Direction.UP);
		    				board.makeBoard();
		    				mp.pause();
		    				File attackSE = new File("./assets/songs/AttackSoundEffect.mp3");
		    				Media attackSEM = new Media(attackSE.toURI().toString());
		    				MediaPlayer attackSEMP = new MediaPlayer(attackSEM);
		    				attackSEMP.play();
		    				attackSEMP.setOnEndOfMedia(new Runnable() {
		    					public void run() {
//		    						abilitySEMP.pause();
		    						mp.play();
		    					}
		    					
		    				});		    				showCurrChamp();
		    				showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
		    				showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
		    				isGameOver(endGameVbox);
		    				makeTurnOrderQueue();

		    			} catch (NotEnoughResourcesException | ChampionDisarmedException | InvalidTargetException e1) {
		    				showAlert("Can not Attack UP", e1.getMessage(), AlertType.WARNING);
		    			}
		        	}else if(singleTargetOn) {
		        		if(recY>0) {
			        		recY--;
//		        			pane.getChildren().remove(rec);
//		        			System.out.println(rec.getId());
			        		board.squares[recX][recY+1].getChildren().remove(rec);
			        		if(board.squares[recX][recY].getChildren().isEmpty()) {
				        		board.squares[recX][recY].getChildren().add(rec);
//				        		System.out.println("empty");
			        		}else {
			        			VBox piece = (VBox) board.squares[recX][recY].getChildren().get(0);
			        			board.squares[recX][recY].getChildren().clear();
			        			board.squares[recX][recY].getChildren().addAll(rec,piece);
//			        			System.out.println("piece");
			        		}
//		        			pane.add(rec,recX,recY);
		        		}
		    			showCurrChamp();
	    				makeTurnOrderQueue();

//		        		System.out.println(recX+","+recY);
		        	}
		        }else if (e.getCode() == KeyCode.S) {
		        	if(moveOn) {
		        		try {
		        			g.move(Direction.DOWN);
		        			board.makeBoard();
		        			showCurrChamp();
		        			mp.pause();
		        			File moveSE = new File("./assets/songs/MoveSoundEffect.mp3");
		        			Media moveSEM = new Media(moveSE.toURI().toString());
		        			MediaPlayer moveSEMP = new MediaPlayer(moveSEM);
		        			moveSEMP.play();
		        			moveSEMP.setOnEndOfMedia(new Runnable() {
		        				public void run() {
//		        					abilitySEMP.pause();
		        					mp.play();
		        				}
		        				
		        			});		    				showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
		    				showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
		    				makeTurnOrderQueue();

		        		} catch (NotEnoughResourcesException | UnallowedMovementException e1) {
		        			showAlert("Can not Move DOWN", e1.getMessage(), AlertType.WARNING);
		        		}
		        	}else if(attackOn) {
		        		try {
		    				g.attack(Direction.DOWN);
		    				board.makeBoard();
		    				showCurrChamp();
		    				mp.pause();
		    				File attackSE = new File("./assets/songs/AttackSoundEffect.mp3");
		    				Media attackSEM = new Media(attackSE.toURI().toString());
		    				MediaPlayer attackSEMP = new MediaPlayer(attackSEM);
		    				attackSEMP.play();
		    				attackSEMP.setOnEndOfMedia(new Runnable() {
		    					public void run() {
//		    						abilitySEMP.pause();
		    						mp.play();
		    					}
		    					
		    				});		    				showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
		    				showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
		    				isGameOver(endGameVbox);
		    				makeTurnOrderQueue();
		        		} catch (NotEnoughResourcesException | ChampionDisarmedException | InvalidTargetException e1) {
		    				showAlert("Can not Attack DOWN", e1.getMessage(), AlertType.WARNING);
		    			}
		        	}else if(singleTargetOn) {
		        		if(recY<4) {
			        		recY++;
			        		board.squares[recX][recY-1].getChildren().remove(rec);
			        		if(board.squares[recX][recY].getChildren().isEmpty()) {
				        		board.squares[recX][recY].getChildren().add(rec);

			        		}else {
			        			VBox piece = (VBox) board.squares[recX][recY].getChildren().get(0);
			        			board.squares[recX][recY].getChildren().clear();
			        			board.squares[recX][recY].getChildren().addAll(rec,piece);
			        		}
//		        			pane.getChildren().remove(rec);
//		        			pane.add(rec,recX,recY);
		        		}
	    				makeTurnOrderQueue();
		    			showCurrChamp();
//		        		System.out.println(recX+","+recY);
		        	}
		        }else if (e.getCode() == KeyCode.D) {
		        	if(moveOn) {
		        		try {
		        			g.move(Direction.RIGHT);
		        			board.makeBoard();
		        			mp.pause();
		        			File moveSE = new File("./assets/songs/MoveSoundEffect.mp3");
		        			Media moveSEM = new Media(moveSE.toURI().toString());
		        			MediaPlayer moveSEMP = new MediaPlayer(moveSEM);
		        			moveSEMP.play();
		        			moveSEMP.setOnEndOfMedia(new Runnable() {
		        				public void run() {
//		        					abilitySEMP.pause();
		        					mp.play();
		        				}
		        				
		        			});
		        			showCurrChamp();	
		    				makeTurnOrderQueue();
		    				showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
		    				showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
		        		} catch (NotEnoughResourcesException | UnallowedMovementException e1) {
		        			showAlert("Can not Move LEFT", e1.getMessage(), AlertType.WARNING);
		        		}
		        	}else if(attackOn) {
		        		try {
		    				g.attack(Direction.RIGHT);
		    				board.makeBoard();
		    				mp.pause();
		    				File attackSE = new File("./assets/songs/AttackSoundEffect.mp3");
		    				Media attackSEM = new Media(attackSE.toURI().toString());
		    				MediaPlayer attackSEMP = new MediaPlayer(attackSEM);
		    				attackSEMP.play();
		    				attackSEMP.setOnEndOfMedia(new Runnable() {
		    					public void run() {
//		    						abilitySEMP.pause();
		    						mp.play();
		    					}
		    					
		    				});		    				showCurrChamp();
		    				makeTurnOrderQueue();
		    				showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
		    				showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
		    				isGameOver(endGameVbox);
		    			} catch (NotEnoughResourcesException | ChampionDisarmedException | InvalidTargetException e1) {
		    				showAlert("Can not Attack DOWN", e1.getMessage(), AlertType.WARNING);
		    			}
		        	}else if(singleTargetOn) {
		        		if(recX<4) {
			        		recX++;
			        		board.squares[recX-1][recY].getChildren().remove(rec);
			        		if(board.squares[recX][recY].getChildren().isEmpty()) {
				        		board.squares[recX][recY].getChildren().add(rec);
//				        		System.out.println("empty");
			        		}else {
			        			VBox piece = (VBox) board.squares[recX][recY].getChildren().get(0);
			        			board.squares[recX][recY].getChildren().clear();
			        			board.squares[recX][recY].getChildren().addAll(rec,piece);
			        		}	
//		        			pane.getChildren().remove(rec);
//		        			pane.add(rec,recX,recY);
		        		}
		    			showCurrChamp();
	    				makeTurnOrderQueue();
//		        		System.out.println(recX+","+recY);
		        	}
					
		        }else if (e.getCode() == KeyCode.A) {
		        	if(moveOn) {
		        		try {
		        			g.move(Direction.LEFT);
		        			board.makeBoard();
		        			mp.pause();
		        			File moveSE = new File("./assets/songs/MoveSoundEffect.mp3");
		        			Media moveSEM = new Media(moveSE.toURI().toString());
		        			MediaPlayer moveSEMP = new MediaPlayer(moveSEM);
		        			moveSEMP.play();
		        			moveSEMP.setOnEndOfMedia(new Runnable() {
		        				public void run() {
//		        					abilitySEMP.pause();
		        					mp.play();
		        				}
		        				
		        			});		        			showCurrChamp();
		    				makeTurnOrderQueue();
		    				showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
		    				showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
		        		} catch (NotEnoughResourcesException | UnallowedMovementException e1) {
		        			showAlert("Can not Move RIGHT", e1.getMessage(), AlertType.WARNING);
		        		}
		        	}else if(attackOn) {
		        		try {
		    				g.attack(Direction.LEFT);
		    				board.makeBoard();
		    				mp.pause();
		    				File attackSE = new File("./assets/songs/AttackSoundEffect.mp3");
		    				Media attackSEM = new Media(attackSE.toURI().toString());
		    				MediaPlayer attackSEMP = new MediaPlayer(attackSEM);
		    				attackSEMP.play();
		    				attackSEMP.setOnEndOfMedia(new Runnable() {
		    					public void run() {
//		    						abilitySEMP.pause();
		    						mp.play();
		    					}
		    					
		    				});		    				showCurrChamp();
		    				makeTurnOrderQueue();
		    				showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
		    				showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
		    				isGameOver(endGameVbox);
		    			} catch (NotEnoughResourcesException | ChampionDisarmedException | InvalidTargetException e1) {
		    				showAlert("Can not Attack RIGHT", e1.getMessage(), AlertType.WARNING);
		    			}
		        	}else if(singleTargetOn) {
		        		if(recX>0) {	
			        		recX--;
			        		board.squares[recX+1][recY].getChildren().remove(rec);
			        		if(board.squares[recX][recY].getChildren().isEmpty()) {
				        		board.squares[recX][recY].getChildren().add(rec);
//				        		System.out.println("empty");
			        		}else {
			        			VBox piece = (VBox) board.squares[recX][recY].getChildren().get(0);
			        			board.squares[recX][recY].getChildren().clear();
			        			board.squares[recX][recY].getChildren().addAll(rec,piece);
			        		}
//		        			pane.getChildren().remove(rec);
//		        			pane.add(rec,recX,recY);
		        		}
		    			showCurrChamp();
	    				makeTurnOrderQueue();
//		        		System.out.println(recX+","+recY);
		        	}
					
		        }if(e.getCode()==KeyCode.ENTER&&singleTargetOn) {
		        	Ability ab = g.getCurrentChampion().getAbilities().get(abilityIndex);
	        		try {
		        		g.castAbility(ab, 4-recY, recX);
		        		mp.pause();
		        		File abilitySE = new File("./assets/songs/AbilitySoundEffect.mp3");
		        		Media abilitySEM = new Media(abilitySE.toURI().toString());
		        		MediaPlayer abilitySEMP = new MediaPlayer(abilitySEM);
		        		abilitySEMP.play();
		        		abilitySEMP.setOnEndOfMedia(new Runnable() {
							public void run() {
								abilitySEMP.pause();
								mp.play();
							}
		        			
		        		});
		        	} catch (NumberFormatException | NotEnoughResourcesException | AbilityUseException | InvalidTargetException
						| CloneNotSupportedException e1) {
		        		showAlert("Can not Cast SingleTarget Ability on location ("+(4-recY)+","+(recX)+")", e1.getMessage(), AlertType.WARNING);
		        	}
	        		singleTargetOn = false;
	        		board.squares[recX][recY].getChildren().remove(rec);
//	    			pane.getChildren().remove(rec);
    				makeTurnOrderQueue();
	            	board.makeBoard();
	    			showCurrChamp();
	            	showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
	    			showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
	    			isGameOver(endGameVbox);
	        	}else if (e.getCode() == KeyCode.E) {
		        		g.endTurn();
		    			makeTurnOrderQueue();
		    	    	board.makeBoard();
	        			showCurrChamp();
	        			singleTargetOn = false;
		    	    	showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
		    			showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
		    			ImageView gif = getGIF(((Champion) g.getTurnOrder().peekMin()).getName());
		    			gif.setFitWidth(300);
		    			gif.setFitHeight(300);
		    			Popup p = new Popup();
		    			p.getContent().add(gif);
		    			p.show(s);
		    			p.setAutoHide(true);
		        }else if(e.getCode() == KeyCode.Z) {
		        	abilityIndex = 0;
					showCurrChamp();
    				makeTurnOrderQueue();
    				File abilitySE = new File("./assets/songs/AbilitySoundEffect.mp3");
    				Media abilitySEM = new Media(abilitySE.toURI().toString());
    				MediaPlayer abilitySEMP = new MediaPlayer(abilitySEM);
		        	castAbilityListener2(mp,abilitySEMP,0,s,gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox, startScene, endGameVbox, rec);
		        	
		        }else if(e.getCode() == KeyCode.X) {
		        	abilityIndex = 1;
    				makeTurnOrderQueue();
					showCurrChamp();
					File abilitySE = new File("./assets/songs/AbilitySoundEffect.mp3");
					Media abilitySEM = new Media(abilitySE.toURI().toString());
					MediaPlayer abilitySEMP = new MediaPlayer(abilitySEM);
		        	castAbilityListener2(mp,abilitySEMP,1,s,gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox, startScene, endGameVbox, rec);
		        
		        }else if(e.getCode() == KeyCode.C) {
		        	abilityIndex = 2;
					showCurrChamp();
    				makeTurnOrderQueue();
    				File abilitySE = new File("./assets/songs/AbilitySoundEffect.mp3");
    				Media abilitySEM = new Media(abilitySE.toURI().toString());
    				MediaPlayer abilitySEMP = new MediaPlayer(abilitySEM);
		        	castAbilityListener2(mp,abilitySEMP,2,s,gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox, startScene, endGameVbox, rec);
		    
		        }else if(e.getCode() == KeyCode.V) {
		        	for(int i=0;i<g.getCurrentChampion().getAbilities().size();i++) {
		        		if(g.getCurrentChampion().getAbilities().get(i).getName().equals("Punch")) {
				        	abilityIndex = 3;
				        	File abilitySE = new File("./assets/songs/AbilitySoundEffect.mp3");
				    		Media abilitySEM = new Media(abilitySE.toURI().toString());
				    		MediaPlayer abilitySEMP = new MediaPlayer(abilitySEM);
		        			castAbilityListener2(mp,abilitySEMP,3,s,gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox, startScene, endGameVbox, rec);
		        			
		        			break;
		        		}
		        	}
		        	
		        }else if(e.getCode() == KeyCode.L) {
		        	try {
						g.useLeaderAbility();
						mp.pause();
						File abilitySE = new File("./assets/songs/AbilitySoundEffect.mp3");
						Media abilitySEM = new Media(abilitySE.toURI().toString());
						MediaPlayer abilitySEMP = new MediaPlayer(abilitySEM);
		        		abilitySEMP.play();
		        		abilitySEMP.setOnEndOfMedia(new Runnable(){

							public void run() {
								abilitySEMP.pause();
								mp.play();
							}
		        			
		        		});
					} catch (LeaderNotCurrentException | LeaderAbilityAlreadyUsedException e1) {
						showAlert("Can not use Leader Ability", e1.getMessage(), AlertType.WARNING);
					}
			    	board.makeBoard();
    				makeTurnOrderQueue();
					showCurrChamp();		
			    	showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
					showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
					isGameOver(endGameVbox);	      
				}
	    });
	    
//	    Button fightBtn = new Button("Let's GOOO");
//	    fightBtn.setOnAction(e->{
//	    	if(mediaPlayer2.getStatus()==Status.STOPPED) {
//	    		t2.getChildren().add(fightBtn);
//	    	}
//	    });
	    ImageView fightButtonImage = new ImageView(new Image("file:./assets/buttons/fightButton.png"));
		fightButtonImage.setFitWidth(200);
		fightButtonImage.setFitHeight(200);
		fightButtonImage.setId("fightbutton");
		fightButtonImage.setOnMouseClicked((e -> {
			g = new Game(first, second);
			makeTurnOrderQueue();
			board = new Board(pane,"Dusk",g,s);
			showCurrChamp();
			Label firstPlayerName = new Label(first.getName());
			firstPlayerName.setTextFill(Color.rgb(0,0,0));
			firstPlayerName.setFont(getFont("Marvel",30));
			firstPlayerName.setAlignment(Pos.TOP_CENTER);
			HBox firstPlayerNameH = new HBox();
			firstPlayerNameH.getChildren().add(firstPlayerName);
			firstPlayerNameH.setAlignment(Pos.BASELINE_CENTER);
			Label secondPlayerName = new Label(second.getName());
			secondPlayerName.setAlignment(Pos.TOP_CENTER);
			secondPlayerName.setFont(getFont("Marvel",30));
			secondPlayerName.setTextFill(Color.rgb(0,0,0));
			HBox secondPlayerNameH = new HBox();
			secondPlayerNameH.getChildren().add(secondPlayerName);
			secondPlayerNameH.setAlignment(Pos.BASELINE_CENTER);
			showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
			showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
			gameFirstPlayerTeamVbox.getChildren().addAll(firstPlayerNameH,gameFirstPlayerTeamHbox);
			gameSecondPlayerTeamVbox.getChildren().addAll(secondPlayerNameH,gameSecondPlayerTeamHbox);
//			gameFirstPlayerTeamVbox.setAlignment(Pos.TOP_LEFT);
			gameFirstPlayerTeamVbox.setMinWidth(Screen.getPrimary().getBounds().getWidth()/3);
			gameFirstPlayerTeamVbox.setMaxWidth(Screen.getPrimary().getBounds().getWidth()/3);
			pane.setAlignment(Pos.CENTER);
//			gameSecondPlayerTeamVbox.setAlignment(Pos.TOP_RIGHT);
			gameSecondPlayerTeamVbox.setMinWidth(Screen.getPrimary().getBounds().getWidth()/3);
			gameSecondPlayerTeamVbox.setMaxWidth(Screen.getPrimary().getBounds().getWidth()/3);
			gameTeamsAndBoardHbox.getChildren().addAll(gameFirstPlayerTeamVbox,pane,gameSecondPlayerTeamVbox);
			gameTeamsAndBoardHbox.setAlignment(Pos.CENTER);
			gameTeamsAndBoardHbox.setMinWidth(Screen.getPrimary().getBounds().getWidth());
			gameVbox.getChildren().addAll(bigVbox,gameTeamsAndBoardHbox,gameBtnsHbox);
			for(int i=0;i<first.getTeam().size();i++) {
				team1.add(first.getTeam().get(i).getName());
				team2.add(second.getTeam().get(i).getName());
			}
			ImageView gif = getGIF(((Champion) g.getTurnOrder().peekMin()).getName());
			gif.setFitWidth(300);
			gif.setFitHeight(300);
//			if(first.getTeam().contains(g.getTurnOrder().peekMin())) {
//				if(gameSecondPlayerTeamVbox.getChildren().size()==3) {
//					gameSecondPlayerTeamVbox.getChildren().remove(gameSecondPlayerTeamVbox.getChildren().get(2));
//				}
//				if(gameFirstPlayerTeamVbox.getChildren().size()==3) {
//					gameFirstPlayerTeamVbox.getChildren().remove(gameFirstPlayerTeamVbox.getChildren().get(2));
//				}
//				gameFirstPlayerTeamVbox.getChildren().add(gif);
//			}else {
//				if(gameFirstPlayerTeamVbox.getChildren().size()==3) {
//					gameFirstPlayerTeamVbox.getChildren().remove(gameFirstPlayerTeamVbox.getChildren().get(2));
//				}
//				if(gameSecondPlayerTeamVbox.getChildren().size()==3) {
//					gameSecondPlayerTeamVbox.getChildren().remove(gameSecondPlayerTeamVbox.getChildren().get(2));
//				}
//				gameSecondPlayerTeamVbox.getChildren().add(gif);
//			}
			File file2 = new File("./assets/videos/assemble2.mp4");
			Media fight = new Media(file2.toURI().toString());
			MediaPlayer mediaPlayer2 = new MediaPlayer(fight);  
			mediaPlayer2.setAutoPlay(true);  
			MediaView mediaView2 = new MediaView (mediaPlayer2);
			mediaView2.setFitHeight(Screen.getPrimary().getBounds().getHeight());
			mediaView2.setFitWidth(Screen.getPrimary().getBounds().getWidth());
			VBox t2 = new VBox(mediaView2);
			t2.setMinWidth(Screen.getPrimary().getBounds().getWidth());
			t2.setMinHeight(Screen.getPrimary().getBounds().getHeight());
			mp.pause();
			makeFadeOut(startScene.getRoot());
	    	startScene.setRoot(t2);
	    	makeFadeIn(startScene.getRoot());
	    	mediaPlayer2.play();
			mediaView2.setOnMouseClicked(e1->{
				mediaPlayer2.pause();
		    	mp.play();
				Popup p = new Popup();
				p.getContent().add(gif);
				p.show(s);
				p.setAutoHide(true);
				makeFadeOut(startScene.getRoot());
		    	startScene.setRoot(gameVbox);
		    	makeFadeIn(startScene.getRoot());
			});
//			makeFadeOut(startScene.getRoot());
//	    	startScene.setRoot(gameVbox);
//	    	makeFadeIn(startScene.getRoot());
//			Popup p = new Popup();
//			p.getContent().add(gif);
//			p.show(s);
//			p.setAutoHide(true);
			
		}));		
	    Button goToVsScreen = new Button("Assemble");
	    goToVsScreen.setTextFill(white);
	    goToVsScreen.setFont(getFont("",14));
		goToVsScreen.setId("Assemble2");
		goToVsScreen.setOnAction(e -> {
			if(leaderTwoIndex != -1) {
				VBox one = makeTeamVBox(first.getTeam(),Color.rgb(63,81,181));
				VBox two = makeTeamVBox(second.getTeam(),Color.rgb(250,23,72));
				HBox three = new HBox();
				HBox four = new HBox();
				Label versus = new Label("Fighting Champions!!!");
				versus.setAlignment(Pos.CENTER);
				versus.setTextFill(Color.rgb(255,239,91));
				versus.setFont(getFont("Marvel",45));
				three.getChildren().add(versus);
				four.getChildren().add(fightButtonImage);
				HBox spacing = new HBox();
				HBox spacing2 = new HBox();
				three.setAlignment(Pos.CENTER);
				four.setAlignment(Pos.BOTTOM_CENTER);
				four.setTranslateY(30);
				spacing.setMinWidth(Screen.getPrimary().getBounds().getWidth()/4);
				spacing2.setMinWidth(Screen.getPrimary().getBounds().getWidth()/4);
				HBox temp = new HBox();
				temp.getChildren().addAll(one,spacing,four,spacing2,two);
				temp.setAlignment(Pos.CENTER);
				vsMainVbox.getChildren().addAll(three,temp);
				second.setLeader(second.getTeam().get(leaderTwoIndex));
				makeFadeOut2(startScene.getRoot());
		    	startScene.setRoot(vsMainVbox);
		    	makeFadeIn2(startScene.getRoot());
			}
			else 
				showAlert("Warning", "Please choose a Leader", AlertType.WARNING);
		});
	    Button goToSecondPlayerChooseLeaderScreen = new Button("Choose Leader");
	    goToSecondPlayerChooseLeaderScreen.setTextFill(white);
	    goToSecondPlayerChooseLeaderScreen.setFont(getFont("",14));
    	goToSecondPlayerChooseLeaderScreen.setId("Choose-SecondLeader");
	    goToSecondPlayerChooseLeaderScreen.setOnAction(e -> {
	    	Label label = new Label(second.getName() + ", Choose your Leader");
			label.setFont(getFont("Marvel",30));
	    	label.setAlignment(Pos.CENTER);
	    	HBox forLabel = new HBox();
	    	forLabel.getChildren().add(label);
	    	VBox space1 = new VBox();
			space1.setMinHeight(Screen.getPrimary().getBounds().getHeight()/10);
			space1.setMaxHeight(Screen.getPrimary().getBounds().getHeight()/10);
	    	forLabel.setAlignment(Pos.CENTER);
			secondPlayerLeaderVbox.getChildren().addAll(forLabel,makeLeaderHbox(second.getTeam(), false),space1,goToVsScreen);
			makeFadeOut(startScene.getRoot());
	    	startScene.setRoot(secondPlayerLeaderVbox);
	    	makeFadeIn(startScene.getRoot());
	    });
	    goToSecondTeamBtn.setOnAction(e -> {
				if(leaderOneIndex != -1) {
					for(int i=0;i<championListViewFirstPlayer.getItems().size();i++) {
						championListViewSecondPlayer.getItems().add(championListViewFirstPlayer.getItems().get(i));
					}
					first.setLeader(first.getTeam().get(leaderOneIndex));
					makeFadeOut(startScene.getRoot());
			    	startScene.setRoot(secondPlayerTeamSceneVbox);
			    	makeFadeIn(startScene.getRoot());
				}
				else 
					showAlert("Warning", "Please choose a Leader", AlertType.WARNING);
//			else {
//				if(leaderOneIndex != -1) {
//					ArrayList<Champion> arr = second.getTeam();
//					while(arr.size()<3) {
//						boolean flag = false;
//						int random = (int) (Math.random()*Game.getAvailableChampions().size());
//						Champion c = Game.getAvailableChampions().get(random);
//						for(int j = 0 ; j < first.getTeam().size();j++) {
//							if(c == first.getTeam().get(j))
//								flag = true;
//						}
//						for(int j = 0 ; j < second.getTeam().size();j++) {
//							if(c == second.getTeam().get(j))
//								flag = true;
//						}
//						if(!flag)
//							arr.add(c);
//					}
//					int random =  (int) (Math.random()*arr.size());
//					first.setLeader(first.getTeam().get(leaderOneIndex));
//					second.setLeader(second.getTeam().get(random));
//					VBox one = makeTeamVBox(first.getTeam(),Color.rgb(63,81,181));
//					VBox two = makeTeamVBox(second.getTeam(),Color.rgb(250,23,72));
//					HBox three = new HBox();
//					HBox four = new HBox();
//					Label versus = new Label("Fighting Champions!!!");
//					versus.setAlignment(Pos.CENTER);
//					versus.setTextFill(Color.rgb(224,191,71));
//					versus.setFont(getFont("Marvel",45));
//					three.getChildren().add(versus);
//					four.getChildren().add(fightButtonImage);
//					HBox spacing = new HBox();
//					HBox spacing2 = new HBox();
//					three.setAlignment(Pos.CENTER);
//					four.setAlignment(Pos.BOTTOM_CENTER);
//					four.setTranslateY(30);
//					spacing.setMinWidth(Screen.getPrimary().getBounds().getWidth()/4);
//					spacing2.setMinWidth(Screen.getPrimary().getBounds().getWidth()/4);
//					HBox temp = new HBox();
//					temp.getChildren().addAll(one,spacing,four,spacing2,two);
//					temp.setAlignment(Pos.CENTER);
//					vsMainVbox.getChildren().addAll(three,temp);
//					makeFadeOut2(startScene.getRoot());
//			    	startScene.setRoot(vsMainVbox);
//			    	makeFadeIn2(startScene.getRoot());
//				}
//			}
			
		});

	    firstPlayerTeamVbox.getChildren().addAll(team1Name,firstPlayerTeamHbox);
	    VBox temp = new VBox();
	    VBox.setVgrow(temp, Priority.ALWAYS);
	    temp.setAlignment(Pos.BOTTOM_CENTER);
	    temp.getChildren().add(championListViewFirstPlayer);
	    firstPlayerTeamSceneVbox.getChildren().addAll(firstPlayerTeamVbox,temp);	 
	    
	    
	    secondPlayerTeamVbox.getChildren().addAll(team2Name,secondPlayerTeamHbox);
	    VBox temp2 = new VBox();
	    temp2.setAlignment(Pos.BOTTOM_CENTER);
	    temp2.getChildren().add(championListViewSecondPlayer);
	    VBox.setVgrow(temp2, Priority.ALWAYS);
	    secondPlayerTeamSceneVbox.getChildren().addAll(secondPlayerTeamVbox,temp2);
	    
	    
	    addingChamps(championListViewFirstPlayer, firstPlayerTeamHbox, 1,firstPlayerTeamVbox,goToFirstPlayerChooseLeaderScreen,firstPlayerTeamSceneVbox);
	    addingChamps(championListViewSecondPlayer, secondPlayerTeamHbox, 2,secondPlayerTeamVbox,goToSecondPlayerChooseLeaderScreen,secondPlayerTeamSceneVbox);  
    	// adding css stylesheet
    	startScene.getStylesheets().add(css);
        s.setTitle("Marvel: Ultimate War");  
        s.getIcons().add(icon);
        s.setFullScreen(true);
        s.setScene(startScene); 
        s.show();
   
    }	
	
	private void attackSound() {
		mp.pause();
		File attackSE = new File("./assets/songs/AttackSoundEffect.mp3");
		Media attackSEM = new Media(attackSE.toURI().toString());
		MediaPlayer attackSEMP = new MediaPlayer(attackSEM);
		attackSEMP.play();
		attackSEMP.setOnEndOfMedia(new Runnable() {
			public void run() {
//				abilitySEMP.pause();
				mp.play();
			}
			
		});
	}
	private void errorSound() {
		mp.pause();
		File errorSE = new File("./assets/songs/ErrorSoundEffect.mp3");
		Media errorSEM = new Media(errorSE.toURI().toString());
		MediaPlayer errorSEMP = new MediaPlayer(errorSEM);
		errorSEMP.play();
		errorSEMP.setOnEndOfMedia(new Runnable() {
			public void run() {
//				abilitySEMP.pause();
				mp.play();
			}
			
		});
	}
	private void moveSound() {
		mp.pause();
		File moveSE = new File("./assets/songs/MoveSoundEffect.mp3");
		Media moveSEM = new Media(moveSE.toURI().toString());
		MediaPlayer moveSEMP = new MediaPlayer(moveSEM);
		moveSEMP.play();
		moveSEMP.setOnEndOfMedia(new Runnable() {
			public void run() {
//				abilitySEMP.pause();
				mp.play();
			}
			
		});
	}
	private void showCurrChamp() {
		Glow glow = new Glow();
		glow.setLevel(5);
		Square sq = board.squares[g.getCurrentChampion().getLocation().y][4-g.getCurrentChampion().getLocation().x];
		sq.setEffect(glow);
		ImageView view = (ImageView) turnOrderHbox.getChildren().get(0);
		view.setFitWidth(80);
		view.setFitHeight(80);
		FadeTransition fade = new FadeTransition();     
		fade.setDuration(Duration.millis(1000)); 
        fade.setCycleCount(1000);  
		fade.setFromValue(1);
		fade.setToValue(0.2);
		fade.setAutoReverse(true);
		for(int i=0;i<sq.getChildren().size();i++) {
			if(sq.getChildren().get(i) instanceof VBox) {
				fade.setNode(((VBox) sq.getChildren().get(i)).getChildren().get(1));
			}
		}
		fade.play();

	}

	
	public ImageView getCelebGIF(String name) {
    	String path = "";
    	switch(name) {
	    	case "Captain America":path = "file:./assets/dance/captainamerica.gif" ;break;
	    	case "Deadpool":path = "file:./assets/dance/deadpool.gif" ;break;
	    	case "Dr Strange":path = "file:./assets/dance/strange.gif" ;break;
	    	case "Electro":path = "file:./assets/dance/electro.gif" ;break;
	    	case "Ghost Rider":path = "file:./assets/dance/ghostrider.gif" ;break;
	    	case "Hela":path = "file:./assets/dance/hela.gif" ;break;
	    	case "Hulk":path = "file:./assets/dance/hulk.gif" ;break;
	    	case "Iceman":path = "file:./assets/dance/iceman.gif" ;break;
	    	case "Ironman":path = "file:./assets/dance/ironman.gif" ;break;
	    	case "Loki":path = "file:./assets/dance/loki.gif" ;break;
	    	case "Quicksilver":path = "file:./assets/dance/quicksilver.gif" ;break;
	    	case "Spiderman":path = "file:./assets/dance/spiderman.gif" ;break;
	    	case "Thor":path = "file:./assets/dance/thor.gif" ;break;
	    	case "Venom": path = "file:./assets/dance/venom.gif" ; break;
	    	case "Yellow Jacket": path = "file:./assets/dance/yellowjacket.gif";break;
    	}
    	ImageView view = new ImageView(path);
    	view.setId(name);
    	view.setFitHeight(120);
    	view.setFitWidth(120);
    	return view;
    }
	public Image getWallPaper(String name) {
    	String path = "";
    	switch(name) {
    	case "Captain America":path = "file:./assets/wallpaper/captainamerica.png" ;break;
    	case "Deadpool":path = "file:./assets/wallpaper/deadpool.jpg" ;break;
    	case "Dr Strange":path = "file:./assets/wallpaper/drStrange.jpg" ;break;
    	case "Electro":path = "file:./assets/wallpaper/electro.jpg" ;break;
    	case "Ghost Rider":path = "file:./assets/wallpaper/ghostrider.jpg" ;break;
    	case "Hela":path = "file:./assets/wallpaper/hela.jpg" ;break;
    	case "Hulk":path = "file:./assets/wallpaper/hulk.jpg" ;break;
    	case "Iceman":path = "file:./assets/wallpaper/iceman.jpg" ;break;
    	case "Ironman":path = "file:./assets/wallpaper/ironman.jpg" ;break;
    	case "Loki":path = "file:./assets/wallpaper/loki.jpg" ;break;
    	case "Quicksilver":path = "file:./assets/wallpaper/quicksilver.jpg" ;break;
    	case "Spiderman":path = "file:./assets/wallpaper/spiderman.jpg" ;break;
    	case "Thor":path = "file:./assets/wallpaper/thor.jpg" ;break;
    	case "Venom": path = "file:./assets/wallpaper/venom.jpg" ; break;
    	case "Yellow Jacket": path = "file:./assets/wallpaper/yellowJacket.jpg";break;
    	
    	}
    	return new Image(path);
    }
	public Font getFont(String name,int s) {
		String path = "";
		switch(name) {
		case "Kepler" :path = "file:./assets/fonts/Kepler/Kepler296-lgZPV.otf";  break;
		case "Marvel" :path = "file:./assets/fonts/Marvel/Captainmarvel-anm9.ttf";  break;
		case "Captain America":path = "file:./assets/fonts/CaptainAmerica/CaptainOfAmericaRegular-ywX8e.ttf" ;break;
		case "Deadpool":path = "file:./assets/fonts/Deadpool/DeadpoolMovie-9AzL.ttf" ;break;
		case "Electro":path = "file:./assets/fonts/Electro/ElectroMagnetBoldItalic-AzV7.ttf" ;break;
		case "Ghost Rider":path = "file:./assets/fonts/GhostRider/GhostRiderMovie-EaPxg.ttf" ;break;
		case "Hela":path = "file:./assets/fonts/Hela/WarsOfAsgardCondensedItalic-RRLV.ttf" ;break;
		case "Hulk":path = "file:./assets/fonts/Hulk/HulksmashRegular-5EPj.ttf" ;break;
		case "Iceman":path = "file:./assets/fonts/IceMan/IceManItalic-d9gDV.ttf" ;break;
		case "Ironman":path = "file:./assets/fonts/IronMan/IronManOfWar001CNcv-9W7K.ttf" ;break;
		case "Loki":path = "file:./assets/fonts/Loki/Loki-Ge1G.ttf" ;break;
		case "Quicksilver":path = "file:./assets/fonts/QuickSilver/Timetravel-lgKje.ttf" ;break;
		case "Spiderman":path = "file:./assets/fonts/SpiderMan/Mightyspidey-pmaa.ttf";break;
		case "Thor":path = "file:./assets/fonts/Thor/Thor-GlyO.ttf" ;break;
		case "Venom": path = "file:./assets/fonts/Venom/VenomSansBold-XRmd.otf"  ; break;
		default: path = "file:./assets/fonts/Avengence/AvengeanceHeroicAvengerBoldItalic-R73A.ttf" ;break;
		
		}
	    Font font = Font.loadFont(path, s);
		return font;
	}

	private void castAbilityListener2(MediaPlayer mp, MediaPlayer abilitySEMP,int i,Stage s, HBox gameFirstPlayerTeamHbox, HBox gameSecondPlayerTeamHbox,Scene startScene,VBox endGameVbox,Rectangle rec) {
		Ability ab = g.getCurrentChampion().getAbilities().get(i);
		AreaOfEffect a = g.getCurrentChampion().getAbilities().get(i).getCastArea();
		moveBtn.setId("");
    	attackBtn.setId("");
		if(a==AreaOfEffect.SINGLETARGET) {
			singleTargetOn = true;
			attackOn = moveOn = false;
			showPopUp(mp,abilitySEMP,a,ab,s, gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox, endGameVbox, startScene,rec);
		}else if(a==AreaOfEffect.DIRECTIONAL) {
			attackOn = moveOn = singleTargetOn = false;
			showPopUp(mp, abilitySEMP,a,ab,s, gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox, endGameVbox, startScene,rec);
		}else {
			attackOn = moveOn = singleTargetOn = false;  	
			try {
				g.castAbility(ab);
				mp.pause();
        		abilitySEMP.play();
        		abilitySEMP.setOnEndOfMedia(new Runnable(){
					public void run() {
						abilitySEMP.pause();
						mp.play();
					}	
        		});
        		
			} catch (NotEnoughResourcesException | AbilityUseException | CloneNotSupportedException e1) {
				showAlert("Can not Cast Ability", e1.getMessage(), AlertType.WARNING);
			}
			board.makeBoard();
			showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
			showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
			isGameOver(endGameVbox);
		}
	}
	private void showPopUp(MediaPlayer mp, MediaPlayer abilitySEMP,AreaOfEffect a,Ability ab,Stage s, HBox gameFirstPlayerTeamHbox, HBox gameSecondPlayerTeamHbox,VBox endGameVbox,Scene startScene,Rectangle rec) {
		if(a==AreaOfEffect.DIRECTIONAL) {
			Popup p = new Popup();
			HBox hbox = new HBox(20);
			VBox vbox = new VBox(20);
			vbox.setId("popup");
			p.getContent().add(vbox);
			
			Button hideBtn = new Button("Hide");
		    hideBtn.setOnAction(e->{
		        p.hide();
		    });
			
			p.getContent().add(hbox);
			Button up = new Button("UP");
			Button down = new Button("DOWN");
			Button right = new Button("RIGHT");
			Button left = new Button("LEFT");
			hbox.getChildren().addAll(up,down,right,left);
			hbox.setId("popup");
			castAbilityDirectional(mp,abilitySEMP,up,ab,p, gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox,endGameVbox);
			castAbilityDirectional(mp,abilitySEMP,down,ab,p,gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox,endGameVbox);
			castAbilityDirectional(mp,abilitySEMP,right,ab, p,gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox,endGameVbox);
			castAbilityDirectional(mp,abilitySEMP,left,ab,p,gameFirstPlayerTeamHbox, gameSecondPlayerTeamHbox, endGameVbox);
			p.show(s);
		}else {
			recX = g.getCurrentChampion().getLocation().y;
			recY = 4-g.getCurrentChampion().getLocation().x;
			if(board.squares[recX][recY].getChildren().get(0) instanceof VBox) {
				VBox piece = (VBox) board.squares[recX][recY].getChildren().get(0);
				board.squares[recX][recY].getChildren().clear();
				board.squares[recX][recY].getChildren().addAll(rec,piece);
			}
		}
	}
	
	private VBox showChampionInfoScreen(Champion c,VBox vbox) {
		Image img = getWallPaper(c.getName());
		BackgroundImage bImg = new BackgroundImage(
    			 img,
                 BackgroundRepeat.NO_REPEAT,
                 BackgroundRepeat.NO_REPEAT,
                 BackgroundPosition.CENTER,
                 new BackgroundSize(100, 100, true, true, true, true)
        );
		Background g = new Background(bImg);
		VBox box = new VBox(10);
		box.setMinWidth(Screen.getPrimary().getBounds().getWidth());
		box.setMaxWidth(Screen.getPrimary().getBounds().getWidth());
//		box.setMinHeight(Screen.getPrimary().getBounds().getHeight());
		box.setBackground(g);
		Label name = new Label(c.getName());
		name.setFont(getFont(c.getName(),c.getName().equals("Loki")||c.getName().equals("Thor")||c.getName().equals("Ironman")?80:c.getName().equals("Hela")?60:40));
		name.setMinWidth(Screen.getPrimary().getBounds().getWidth());
		name.setAlignment(Pos.TOP_CENTER);
		name.setTextFill(getColor(c.getName()));
		Label info = generateChampionInfoWithoutName(c);
		info.setFont(getFont("",20));
		info.setAlignment(Pos.BASELINE_LEFT);
		info.setTextFill(getColor(c.getName()));
		info.setMinWidth(Screen.getPrimary().getBounds().getWidth());
		info.setTranslateX(100);
		ArrayList<Label> abilities = generateChampionAbilitiesInfo(c);
		Label abilityTitle = new Label("Abilities:");
		VBox tempAbilities = new VBox();
		abilityTitle.setTranslateX(100);
		abilityTitle.setFont(getFont("",25));
		abilityTitle.setTextFill(getColor(c.getName()));
		tempAbilities.getChildren().add(abilityTitle);
		for(int i = 0 ;i < abilities.size();i++) {
			Label ability = abilities.get(i);
			ability.setTranslateX(110);
			ability.setTextFill(getColor(c.getName()));
			ability.setFont(getFont("",20));
			ability.setTranslateX(110);
			tempAbilities.getChildren().add(ability);
		}
		
		Button backBtn = new Button("Back");
		backBtn.setTextFill(white);
		backBtn.setFont(getFont("",12));
		backBtn.setOnAction(e->{
			champSongMP.pause();
			mp.play();
			makeFadeOut(startScene.getRoot());
			startScene.setRoot(vbox);
			makeFadeIn(startScene.getRoot());
		});
		backBtn.setTranslateY(20);
		backBtn.setTranslateX(100);
		HBox temp = new HBox(backBtn);
//		temp.setMinHeight(Screen.getPrimary().getBounds().getHeight());
		temp.setAlignment(Pos.BOTTOM_LEFT);
		HBox.setHgrow(temp, Priority.ALWAYS);
		box.getChildren().addAll(name,info,tempAbilities,temp);
		return box;
	}
	public Color getColor (String name) {
		switch(name) {
    	case "Captain America":
    	case "Ironman":
    	case "Electro":
    	case "Thor":
    	case "Venom":
    	case "Dr Strange":
    	case "Ghost Rider":
    	case "Yellow Jacket":
    	case "Hulk": 
    	case "Deadpool":
    		return Color.rgb(163,163,163);
    	default: return Color.rgb(0,0,0);
    	}
	}
	
	private VBox generateWinnerDances() {
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		if(g.checkGameOver() != null) {
			Player win = g.checkGameOver();
			Label winLabel =  new Label(win.getName() + " wins!!!");
			winLabel.setId("choosing");
			winLabel.setTextFill(Color.rgb(150,120,0));
			winLabel.setAlignment(Pos.CENTER);
			ArrayList<String> team = (win == g.getFirstPlayer())? team1 : team2;
			for(int i = 0 ; i < team.size() ; i++) {
				int k = 30;
				ImageView view = this.getCelebGIF(team.get(i));
				view.setFitHeight(200);
				view.setFitWidth(200);
				HBox temp = new HBox();
				temp.setMinHeight(250);
				temp.setMaxHeight(250);
				temp.setMinWidth(250);
				temp.setMaxWidth(250);
				temp.getChildren().add(view);
				hbox.getChildren().add(temp);
			}
			box.getChildren().addAll(winLabel,hbox);
		}
	
		return box;
	}
	private void isGameOver(VBox endGameVbox) {
		if(g.checkGameOver()!=null) {
	 		endGameVbox.getChildren().clear();
			winner = g.checkGameOver();
			endGameVbox.getChildren().addAll(this.generateWinnerDances(),restartWithDifferentTeams,restartWithSameTeams);
			makeFadeOut(startScene.getRoot());
	    	startScene.setRoot(endGameVbox);
	    	makeFadeIn(startScene.getRoot());
//			mp.setVolume(0);
			mp.pause();
	    	File winSE = new File("./assets/songs/Winning.mp3");
			Media winSEM = new Media(winSE.toURI().toString());
			MediaPlayer winSEMP = new MediaPlayer(winSEM);
			winSEMP.play();
			winSEMP.setOnEndOfMedia(new Runnable() {
		       public void run() {
			         mp.seek(Duration.ZERO);
			   }
			});
		}
	}
	private void castAbilityDirectional(MediaPlayer mp, MediaPlayer abilitySEMP,Button btn, Ability ab,Popup p, HBox gameFirstPlayerTeamHbox, HBox gameSecondPlayerTeamHbox, VBox endGameVbox) {
		btn.setOnAction(e -> {
			String dir = btn.getText();
			Direction dirEnum;
			if(dir.equals("UP")) {
				dirEnum = Direction.UP;
			}else if(dir.equals("DOWN")) {
				dirEnum = Direction.DOWN;
			}else if(dir.equals("RIGHT")) {
				dirEnum = Direction.RIGHT;
			}else {
				dirEnum = Direction.LEFT;
			}
			try {
				g.castAbility(ab, dirEnum);
				mp.pause();
        		abilitySEMP.play();
        		abilitySEMP.setOnEndOfMedia(new Runnable(){

					public void run() {
						abilitySEMP.pause();
						mp.play();
					}
        			
        		});
			} catch (NotEnoughResourcesException | AbilityUseException | CloneNotSupportedException e1) {
				showAlert("Can not Cast Ability "+dir, e1.getMessage(), AlertType.WARNING);
			}
			board.makeBoard();
        	showChampsInGame(first,first.getLeader(),first.getTeam(),gameFirstPlayerTeamHbox, g.isFirstLeaderAbilityUsed());
			showChampsInGame(second,second.getLeader(),second.getTeam(),gameSecondPlayerTeamHbox,g.isSecondLeaderAbilityUsed());
			p.hide();
			isGameOver(endGameVbox);
		});
	}
	private void showChampsInGame(Player player,Champion leader,ArrayList<Champion> team, HBox hbox,boolean isLeaderAbilityUsed) {
		hbox.getChildren().clear();
		for(int i=0;i<team.size();i++) {
			String l="";
			Label lead = new Label();
			lead.setAlignment(Pos.CENTER);
			VBox vbox = new VBox();
			Label name = new Label(team.get(i).getName());
			name.setFont(getFont("",20));
			name.setMinWidth(100);
			name.setAlignment(Pos.BASELINE_CENTER);
			name.setTextFill(Color.rgb(235, 235, 235));
			ImageView view = new ImageView(getLogo(team.get(i).getName()));
			Label info = generateChampionInfo2(team.get(i));
			info.setTextFill(Color.rgb(235, 235, 235));
			info.setFont(getFont("Kepler",12));
			Button viewInfo = new Button("View Info");
			viewInfo.setTextFill(white);
			viewInfo.setFont(getFont("",8));
        	VBox infoVbox = showChampionInfoScreen(team.get(i),gameVbox);
        	Champion champ = team.get(i);
    		viewInfo.setOnAction(e->{
    			mp.pause();	
    			File champSong = new File(getSong(champ));
    			Media champSongM = new Media(champSong.toURI().toString());
    			champSongMP = new MediaPlayer(champSongM);
    			champSongMP.play();
    			champSongMP.setOnEndOfMedia(new Runnable() {
    		       public void run() {
    		    	   champSongMP.seek(Duration.ZERO);
    		       }
    			});
    			makeFadeIn(startScene.getRoot());
    			startScene.setRoot(infoVbox);
    			makeFadeOut(startScene.getRoot());
    		});
			Label effects = generateChampionEffectsInfo(team.get(i));
			effects.setFont(getFont("Kepler",12));
			effects.setTextFill(Color.rgb(235, 235, 235));
			if(team.get(i)==leader) {
				Image c = new Image("file:./assets/crown.png");
				ImageView crown = new ImageView(c);
				crown.setFitHeight(20);
				crown.setFitWidth(20);
				HBox t = new HBox();
				t.setAlignment(Pos.TOP_CENTER);
				t.getChildren().add(crown);
				vbox.getChildren().add(t);
				if(isLeaderAbilityUsed) {
					l += "Leader ability used"+"\n";
				}else {
					l += "Leader ability not used"+"\n";
				}
			}
			else {
				Image s = new Image("file:./assets/sword.png");
				ImageView sword = new ImageView(s);
				sword.setFitHeight(20);
				sword.setFitWidth(20);
				HBox t = new HBox();
				t.setAlignment(Pos.TOP_CENTER);
				t.getChildren().add(sword);
				vbox.getChildren().add(t);
			}
			
				
			lead.setText(l);
			lead.setFont(getFont("Marvel",8));
			lead.setTextFill(Color.rgb(235, 235, 235));
			vbox.getChildren().addAll(lead,view,name,info,effects,viewInfo);
//			vbox.setAlignment(Pos.CENTER);
			view.setFitWidth(100);
			view.setFitHeight(100);
			hbox.getChildren().add(vbox);
		}
	}
	private Label generateChampionEffectsInfo(Champion c) {
		Label effects = new Label("Applied Effects: ....");
		String info = "";
		Tooltip t = new Tooltip();
		Tooltip.install(effects, t);
		for(int i=0;i<c.getAppliedEffects().size();i++) {	
			Effect e = c.getAppliedEffects().get(i);
			info += "Name: "+e.getName()+", Duration: " +e.getDuration()+"turns, Type: "+e.getType()+"\n";
		}
		t.setText(info);
		Tooltip.install(effects, t);
		return effects;
	}
	private ArrayList<Label> generateChampionAbilitiesInfo(Champion c) {
		ArrayList<Label> abilities = new ArrayList<Label>();
	   	for(int i=1;i<=c.getAbilities().size();i++) {
    		Ability a = c.getAbilities().get(i-1);
    		Label ab = new Label("Ability "+i+": "+a.getName()+"\n");
    		String info = a.getName()+"\n";
    		info+="type: ";
    		if(a instanceof DamagingAbility) {
    			info+="Damaging Ability"+"\n";
    			info+="damage amount: "+ ((DamagingAbility) a).getDamageAmount()+"hp"+"\n";
    		}else if(a instanceof HealingAbility) {
    			info+="Healing Ability"+"\n";
    			info+="heal amount: "+ ((HealingAbility) a).getHealAmount()+"hp"+"\n";
    		}else {
    			info+="Crowd Control Ability"+"\n";
    			info+="Effect name: "+((CrowdControlAbility) a).getEffect().getName()+", Duration: " +((CrowdControlAbility) a).getEffect().getDuration()+"turns, Type: "+((CrowdControlAbility) a).getEffect().getType()+"\n";
    		}
    		info+= "base cooldown: "+a.getBaseCooldown()+"\n";
    		info+= "cast range: "+a.getCastRange()+"\n";
    		info+= "current cooldown: "+a.getCurrentCooldown()+"\n";
    		info+= "mana cost: "+a.getManaCost()+"\n";
    		info+= "required action points: "+a.getRequiredActionPoints()+"\n";
    		info+= "cast area: "+a.getCastArea()+"\n";
    		Tooltip t = new Tooltip(info);
    		Tooltip.install(ab, t);
    		ab.setAlignment(Pos.BASELINE_LEFT);
    		abilities.add(ab);
    	}	
	   	Label leaderAbility = new Label();
	   	if (c instanceof Hero) {
	   		leaderAbility.setText("Leader Ability: Removes all negative effects from the players entire team and " + "\n" + "adds an Embrace effect"
	   				+ "to them which lasts for 2 turns.");
	   	}
	   	else if (c instanceof Villain) {
	   		leaderAbility.setText("Leader Ability: Immediately eliminates all enemy champions"  + "\n" + " with less than 30 percent health"
	   				+ " points.");
	   	}
	   	else {
	   		leaderAbility.setText("Leader Ability: All champions on the board except for the leaders of each team will " + "\n" + " be stunned"
	   				+ "for 2 turns.");
	   	}
	   	abilities.add(leaderAbility);
	   	return abilities;
	}
	private void makeTurnOrderQueue() {
		bigVbox.getChildren().clear();
		turnOrderHbox.getChildren().clear();
		bigVbox.setMinWidth(Screen.getPrimary().getBounds().getWidth());
		bigVbox.setAlignment(Pos.CENTER);
		turnOrderHbox.setAlignment(Pos.CENTER);
		HBox hbox = new HBox();
		hbox.setMaxWidth(300);
		Champion c = (Champion) g.getTurnOrder().peekMin();
		ImageView logo = new ImageView(getLogo2(c.getName()));
		logo.setFitWidth(80);
		logo.setFitHeight(80);		
		Tooltip hpTP = new Tooltip(c.getCurrentHP()+" hp");
		Tooltip apTP = new Tooltip(c.getCurrentActionPoints()+" action points left");
		ProgressBar hp = new ProgressBar(1);
		ProgressBar actionPoints = new ProgressBar(1);
        Tooltip.install(hp, hpTP);
        Tooltip.install(actionPoints, apTP);
		hp.setProgress((double)(c.getCurrentHP())/c.getMaxHP());
		hp.setId("progressBar-green");
		actionPoints.setProgress((double)(c.getCurrentActionPoints())/c.getMaxActionPointsPerTurn());
		actionPoints.setId("progressBar-blue");
    	if(hp.getProgress()<0.7) {
    		hp.setId("progressBar-orange");
    	}
    	if(hp.getProgress()<0.3) {
    		hp.setId("progressBar-red");
    	}
    	if(actionPoints.getProgress()<0.7) {
    		actionPoints.setId("progressBar-orange");
    	}
    	if(actionPoints.getProgress()<0.3) {
    		actionPoints.setId("progressBar-red");
    	}
		if(first.getTeam().contains(c)) {
			VBox temp = new VBox();
			temp.getChildren().addAll(hp,actionPoints);
			hbox.getChildren().addAll(logo,temp);
			hbox.setAlignment(Pos.BASELINE_LEFT);
			bigVbox.getChildren().addAll(hbox);
		}
		if(second.getTeam().contains(c)) {
			VBox temp = new VBox();
			temp.getChildren().addAll(hp,actionPoints);
			hbox.getChildren().addAll(temp,logo);
			hbox.setAlignment(Pos.BASELINE_RIGHT);
			bigVbox.getChildren().addAll(hbox);
		}
		hbox.setMinWidth(Screen.getPrimary().getBounds().getWidth());
		ArrayList<Champion> temp = new ArrayList<Champion>();
		while(!g.getTurnOrder().isEmpty()) {
			ImageView view = new ImageView(getLogo2(((Champion) g.getTurnOrder().peekMin()).getName()));
			view.setFitHeight(50);
			view.setFitWidth(50);
			temp.add((Champion) g.getTurnOrder().remove());
			Button arrow = new Button();			
			arrow.setId("arrow-right");
			if(!g.getTurnOrder().isEmpty())
				turnOrderHbox.getChildren().addAll(view,arrow);
			else 
				turnOrderHbox.getChildren().addAll(view);
		}
		for(int i=0;i<temp.size();i++) {
			g.getTurnOrder().insert(temp.get(i));
		}
		((ImageView)turnOrderHbox.getChildren().get(0)).setFitHeight(75);
		((ImageView)turnOrderHbox.getChildren().get(0)).setFitWidth(75);
		bigVbox.getChildren().addAll(turnOrderHbox);
//		HBox.setHgrow(hbox, Priority.ALWAYS);
		HBox.setHgrow(turnOrderHbox, Priority.ALWAYS);
		
	}
	public void makeFadeOut(Node root) {	
	    FadeTransition fade = new FadeTransition();     
	    fade.setDuration(Duration.millis(0)); 
	    fade.setNode(root);
	    fade.setFromValue(1);
	    fade.setToValue(0.2);
	    fade.play();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	public void makeFadeIn(Node root) {
	    FadeTransition fade = new FadeTransition();     
        fade.setDuration(Duration.millis(0)); 
        fade.setNode(root);
        fade.setFromValue(0.3);
        fade.setToValue(1);
        fade.play();
	} 
	public void makeFadeOut2(Node root) {	
		FadeTransition fade = new FadeTransition();     
		fade.setDuration(Duration.millis(2000)); 
		fade.setNode(root);
		fade.setFromValue(1);
		fade.setToValue(0.2);
		fade.play();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	public void makeFadeIn2(Node root) {
		FadeTransition fade = new FadeTransition();     
		fade.setDuration(Duration.millis(2000)); 
		fade.setNode(root);
		fade.setFromValue(0.3);
		fade.setToValue(1);
		fade.play();
	} 
	public void makeFadeInAndOut(Node root) {
		FadeTransition fade = new FadeTransition();     
		fade.setDuration(Duration.millis(1500)); 
        fade.setCycleCount(1000);  
		fade.setNode(root);
		fade.setFromValue(0.6);
		fade.setToValue(1);
		fade.setAutoReverse(true);
		fade.play();
	} 
	public VBox makeTeamVBox(ArrayList<Champion> Team ,Color color) {
		VBox box = new VBox();
		VBox one = new VBox();
		one.setMaxHeight(150);
		one.setMaxWidth(150);
		VBox two = new VBox();
		two.setMaxHeight(150);
		two.setMaxWidth(150);
		VBox three = new VBox();
		three.setMaxHeight(150);
		three.setMaxWidth(150);
		Label f = new Label(Team.get(0).getName());
		f.setFont(getFont("",18));
		f.setMinWidth(150);
		f.setAlignment(Pos.BASELINE_CENTER);
		f.setTextFill(color);
		Label s = new Label(Team.get(1).getName());
		s.setFont(getFont("",18));
		s.setAlignment(Pos.BASELINE_CENTER);
		s.setMinWidth(150);
		s.setTextFill(color);
		Label t = new Label(Team.get(2).getName());
		t.setFont(getFont("",18));
		t.setTextFill(color);
		t.setMinWidth(150);
		t.setAlignment(Pos.BASELINE_CENTER);
		ImageView ov = new ImageView(getLogo(Team.get(0).getName()));
		ov.setFitHeight(150);
		ov.setFitWidth(150);
		ImageView tv = new ImageView(getLogo(Team.get(1).getName()));
		tv.setFitHeight(150);
		tv.setFitWidth(150);
		ImageView ttv = new ImageView(getLogo(Team.get(2).getName()));
		ttv.setFitHeight(150);
		ttv.setFitWidth(150);
		one.getChildren().addAll(f,ov);
		two.getChildren().addAll(s,tv);
		three.getChildren().addAll(t,ttv);
		box.getChildren().addAll(one,two,three);
		box.setMaxHeight(300);
		return box;
	}
	public HBox makeLeaderHbox(ArrayList<Champion> team,boolean whichTeam) {
		HBox box = new HBox();
		Label name1 = new Label(team.get(0).getName());
		name1.setFont(getFont("",17));
		name1.setAlignment(Pos.CENTER);
		Label name2 = new Label(team.get(1).getName());
		name2.setFont(getFont("",17));
		name2.setAlignment(Pos.CENTER);
		Label name3 = new Label(team.get(2).getName());
		name3.setFont(getFont("",17));
		name3.setAlignment(Pos.CENTER);
		ImageView img1 = new ImageView(getLogo(team.get(0).getName()));
		img1.setId("leader");
		img1.setFitHeight(175);
		img1.setFitWidth(175);
		img1.setOnMouseClicked(e -> {
			if(whichTeam)
				leaderOneIndex = 0;
			else
				leaderTwoIndex = 0;
		});
		ImageView img2 = new ImageView(getLogo(team.get(1).getName()));
		img2.setId("leader");
		img2.setFitHeight(175);
		img2.setFitWidth(175);
		img2.setOnMouseClicked(e -> {
			if(whichTeam)
				leaderOneIndex = 1;
			else
				leaderTwoIndex = 1;
		});
		ImageView img3 = new ImageView(getLogo(team.get(2).getName()));
		img3.setId("leader");
		img3.setFitHeight(175);
		img3.setFitWidth(175);
		img3.setOnMouseClicked(e -> {
			if(whichTeam)
				leaderOneIndex = 2;
			else
				leaderTwoIndex = 2;
		});
		VBox one = new VBox();
		HBox space1 = new HBox();
		space1.setMinWidth(Screen.getPrimary().getBounds().getWidth()/10);
		space1.setMaxWidth(Screen.getPrimary().getBounds().getWidth()/10);
		HBox space2 = new HBox();
		space2.setMinWidth(Screen.getPrimary().getBounds().getWidth()/10);
		space2.setMaxWidth(Screen.getPrimary().getBounds().getWidth()/10);
		one.getChildren().addAll(name1,img1);
		one.setAlignment(Pos.CENTER);
		VBox two = new VBox();
		two.getChildren().addAll(name2,img2);
		two.setAlignment(Pos.CENTER);
		VBox three = new VBox();
		three.setAlignment(Pos.CENTER);
		three.getChildren().addAll(name3,img3);
		box.getChildren().addAll(one,space1,two,space2,three);
		box.setAlignment(Pos.CENTER);
		return box;
	}
	public void backgroundMusic() {
		File theme = new File("./assets/songs/TheAvengersTheme2.mp3");
		Media backMusix = new Media(theme.toURI().toString());
		MediaPlayer mp = new MediaPlayer(backMusix);
//		mp.setAutoPlay(true);  
//		mp.setCycleCount(MediaPlayer.INDEFINITE);
		mp.setOnEndOfMedia(new Runnable() {
	       public void run() {
	         mp.seek(Duration.ZERO);
	       }
		});
		mp.setOnReady(new Runnable() {
	        public void run() {
	            mp.play();
	        }
	    });
		mp.play();
	}
	public String getSong(Champion c) {
		switch(c.getName()) {
		case "Spiderman": return "./assets/songs/Spider-Man.mp3";
		case "Ironman": return "./assets/songs/Ironman.mp3";
		case "Hulk": return "./assets/songs/Hulk.mp3";
		case "Deadpool": return "./assets/songs/Deadpool.mp3";
		case "Thor": return "./assets/songs/Thor.mp3";
		case "Venom": return "./assets/songs/Venom.mp3";
		default: return "./assets/songs/TheAvengersTheme2.mp3";
		}
	}
	public void addingChamps(ListView<ImageView> listView,HBox hbox,int x,VBox vbox,Button btn, VBox playerTeamSceneVbox) {
		listView.setOnMouseClicked(mouseEvent->{
    		ImageView imgView = listView.getSelectionModel().getSelectedItem();
    		int champIndex = -1;
    		int selectedIndex = listView.getSelectionModel().getSelectedIndex();
    		for(int i=0;i<Game.getAvailableChampions().size();i++) {
    			if(imgView!=null && imgView.getId().equals(Game.getAvailableChampions().get(i).getName())) {
    				champIndex = i;
    				break;
    			}
    		}
    		
    		VBox championInfoVbox = new VBox(5);
    		championInfoVbox.setAlignment(Pos.CENTER);
    		Button viewInfo = new Button("View Info");
    		viewInfo.setTextFill(white);
        	viewInfo.setFont(getFont("",14));
			viewInfo.setTextFill(white);
			viewInfo.setFont(getFont("",14));
			Champion c = Game.getAvailableChampions().get(champIndex);
        	VBox infoVbox = showChampionInfoScreen(Game.getAvailableChampions().get(champIndex),playerTeamSceneVbox);
    		viewInfo.setOnAction(e->{
    			mp.pause();	
    			File champSong = new File(getSong(c));
    			Media champSongM = new Media(champSong.toURI().toString());
    			champSongMP = new MediaPlayer(champSongM);
    			champSongMP.play();
    			champSongMP.setOnEndOfMedia(new Runnable() {
    		       public void run() {
    		    	   champSongMP.seek(Duration.ZERO);
    		       }
    			});
    			makeFadeIn(startScene.getRoot());
    			startScene.setRoot(infoVbox);
    			makeFadeOut(startScene.getRoot());	
    		});
    		Button removeChampBtn =new Button("Remove Champion"); 
    		removeChampBtn.setTextFill(white);
    		removeChampBtn.setFont(getFont("",14));
    		removeChampBtn.setId("Remove-Champion");
    		removeChampBtn.setOnAction(e -> {
    			listView.getItems().add(imgView);
    			hbox.getChildren().remove(championInfoVbox);
    			removeChamp(first.getTeam(),imgView.getId());
    			if(x==1) {
    				removeChamp(first.getTeam(),imgView.getId());
    				firstPlayerChoose--;
    			}
    			else {
    				removeChamp(second.getTeam(),imgView.getId());
    				secondPlayerChoose--;
    			}
    			vbox.getChildren().remove(btn);
            });
//    		ArrayList<Label> abilities = generateChampionAbilitiesInfo(Game.getAvailableChampions().get(champIndex));
//			Label ability1 = abilities.get(0);
//			Label ability2 = abilities.get(1);
//			Label ability3 = abilities.get(2);
			ImageView view = getGIF(imgView.getId());
			view.setFitHeight(200);
			view.setFitWidth(200);
			Label name = new Label(imgView.getId());
			name.setFont(getFont("",30));
			//,generateChampionInfo(Game.getAvailableChampions().get(champIndex)),ability1,ability2,ability3
			championInfoVbox.getChildren().addAll(view,name);
//			championInfoVbox.setMaxWidth(Screen.getPrimary().getBounds().getWidth());
			if(x==1) {
				if(firstPlayerChoose<3) {
					championInfoVbox.getChildren().addAll(viewInfo,removeChampBtn);
					hbox.getChildren().add(championInfoVbox);
					listView.getItems().remove(selectedIndex);
					first.getTeam().add(Game.getAvailableChampions().get(champIndex));
					firstPlayerChoose++;
					if(firstPlayerChoose==3) {
						vbox.getChildren().add(btn);
					}
					
				}else {
					showAlert("Warning", "You can not choose more than 3 champions", AlertType.WARNING);
				} 
			}
			else if(x==2) {
				if(secondPlayerChoose<3) {
					championInfoVbox.getChildren().addAll(viewInfo,removeChampBtn);
					hbox.getChildren().add(championInfoVbox);
					listView.getItems().remove(selectedIndex);
					second.getTeam().add(Game.getAvailableChampions().get(champIndex));
					secondPlayerChoose++;
					if(secondPlayerChoose==3) {
						vbox.getChildren().add(btn);
					}
					
				}else {
					showAlert("Warning", "You can not choose more than 3 champions", AlertType.WARNING);
				}	
			}
    	});
	}
	public void startGame() throws IOException {
		Game.loadAbilities("Abilities.csv");
		Game.loadChampions("Champions.csv");
	}
	public void showAlert(String title, String HeaderText, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(HeaderText);
		alert.showAndWait();
		mp.pause();
		File errorSE = new File("./assets/songs/ErrorSoundEffect.mp3");
		Media errorSEM = new Media(errorSE.toURI().toString());
		MediaPlayer errorSEMP = new MediaPlayer(errorSEM);
		errorSEMP.play();
		errorSEMP.setOnEndOfMedia(new Runnable() {
			public void run() {
//				abilitySEMP.pause();
				mp.play();
			}
			
		});
	}
	public Button buttonAndTextField(Stage s,ArrayList<String> names,String name, Scene scene, TextField tf) {
		Button btn=new Button(name);
		btn.setId(name);
        btn.setOnAction(e-> {  
            	if(!tf.getText().isEmpty()) {
            		names.add(tf.getText());
            		s.setScene(scene);
//            		s.show();
            	}else {
            		showAlert("Warning", "Please enter a valid name", AlertType.WARNING);
            	}
            }  
        ); 
        return btn;
	}
	
	public Button buttonToNavigate(Stage s,String name, Scene scene) {
		Button btn=new Button(name); 
		btn.setId(name);
        btn.setOnAction(e -> {
        	s.setScene(scene);
//        	s.show();
        }) ; 
        return btn;
	}
    public TextField textField(String s) {
        TextField tF = new TextField();
        tF.setPromptText(s);
        tF.setFocusTraversable(false);
        tF.setMaxWidth(300);
        tF.setOpacity(0.5);
		return tF;        
    }
    public void removeChamp(ArrayList<Champion> team, String name) {
    	for(int i=0;i<team.size();i++) {
    		if(team.get(i).getName().equals(name)){
    			team.remove(i);
    			break;
    		}
    	}
    }
	public ObservableList<ImageView> generateChampionListView(ArrayList<Champion> inputs) {
    	ObservableList<ImageView> view = FXCollections.observableArrayList();
    	for(int i = 0 ; i < inputs.size() ;i++) {
    		String name = inputs.get(i).getName();
    		Image logo = getLogo(name);
    		ImageView v = new ImageView(logo);
    		v.setId(name);
    		v.setFitHeight(120);
    		v.setFitWidth(120);
    		view.add(v);
    	}    	
    	return view;
    }
	public Label generateChampionInfo(Champion c) {
		Label label = new Label();
		String info = c.getName()+"\n";
    	if(c instanceof Hero)
        	info += "Type: Hero"+"\n";
    	else if(c instanceof Villain)
        	info += "Type: Villain"+"\n";
    	else
    		info += "Type: AntiHero"+"\n";
    	info += "Current hp: "+ c.getCurrentHP()+"hp"+"\n";
    	info += "Speed: "+c.getSpeed()+"\n";
    	info += "Attack Damage: "+c.getAttackDamage()+"\n";
    	info += "Attack Range: "+c.getAttackRange()+"\n";
    	info += "Mana: "+c.getMana()+"\n";
    	info += "Action Points: "+c.getCurrentActionPoints()+"\n";
    	info += "Max action pts per turn: "+c.getMaxActionPointsPerTurn()+"\n";
    	label.setText(info);
    	label.setWrapText(true);
    	label.setMaxWidth(150);
    	label.setMaxHeight(Screen.getPrimary().getBounds().getHeight());
    	return label;
    }
	public Label generateChampionInfoWithoutName(Champion c) {
		Label label = new Label();
		String info = "";
		if(c instanceof Hero)
			info += "Type: Hero"+"\n";
		else if(c instanceof Villain)
			info += "Type: Villain"+"\n";
		else
			info += "Type: AntiHero"+"\n";
		info += "Current hp: "+ c.getCurrentHP()+"hp"+"\n";
		info += "Speed: "+c.getSpeed()+"\n";
		info += "Attack Damage: "+c.getAttackDamage()+"\n";
		info += "Attack Range: "+c.getAttackRange()+"\n";
		info += "Mana: "+c.getMana()+"\n";
		info += "Action Points: "+c.getCurrentActionPoints()+"\n";
		info += "Max action pts per turn: "+c.getMaxActionPointsPerTurn()+"\n";
		label.setText(info);
//		label.setWrapText(true);
		label.setMaxWidth(150);
		label.setMaxHeight(Screen.getPrimary().getBounds().getHeight());
		return label;
	}
	public Label generateChampionInfo2(Champion c) {
		Label label = new Label();
		String info = "";
		if(c instanceof Hero)
			info += "Type: Hero"+"\n";
		else if(c instanceof Villain)
			info += "Type: Villain"+"\n";
		else
			info += "Type: AntiHero"+"\n";
		info += "Current hp: "+ c.getCurrentHP()+"hp"+"\n";
		info += "Mana: "+c.getMana()+"\n";
		info += "Action Points: "+c.getCurrentActionPoints()+"\n";
		label.setText(info);
		label.setWrapText(true);
		label.setMaxWidth(150);
		label.setMaxHeight(Screen.getPrimary().getBounds().getHeight());
		return label;
	}
    public Image getLogo(String name) {
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
    	
    	}
    	return new Image(path);
    }
    public Image getLogo2(String name) {
    	String path = "";
    	switch(name) {
    	case "Captain America":path = "file:./assets/symbols2/captainamerica.png" ;break;
    	case "Deadpool":path = "file:./assets/symbols2/deadpool.png" ;break;
    	case "Dr Strange":path = "file:./assets/symbols2/drStrange.png" ;break;
    	case "Electro":path = "file:./assets/symbols2/electro.png" ;break;
    	case "Ghost Rider":path = "file:./assets/symbols2/ghostRider.png" ;break;
    	case "Hela":path = "file:./assets/symbols/hela.png" ;break;
    	case "Hulk":path = "file:./assets/symbols2/hulk.png" ;break;
    	case "Iceman":path = "file:./assets/symbols/iceman.png" ;break;
    	case "Ironman":path = "file:./assets/symbols2/ironman.png" ;break;
    	case "Loki":path = "file:./assets/symbols2/loki.png" ;break;
    	case "Quicksilver":path = "file:./assets/symbols2/quicksilver.png" ;break;
    	case "Spiderman":path = "file:./assets/symbols2/spiderman.png" ;break;
    	case "Thor":path = "file:./assets/symbols2/thor.png" ;break;
    	case "Venom": path = "file:./assets/symbols2/venom.jpg" ; break;
    	case "Yellow Jacket": path = "file:./assets/symbols/yellowJacket.png";break;
    	
    	}
    	return new Image(path);
    }
    public ImageView getGIF(String name) {
    	String path = "";
    	switch(name) {
    	case "Captain America":path = "file:./assets/gif/capAmerica.gif" ;break;
    	case "Deadpool":path = "file:./assets/gif/deadpool.gif" ;break;
    	case "Dr Strange":path = "file:./assets/gif/doctorstrange.gif" ;break;
    	case "Electro":path = "file:./assets/gif/electro.gif" ;break;
    	case "Ghost Rider":path = "file:./assets/gif/ghost-rider.gif" ;break;
    	case "Hela":path = "file:./assets/gif/hela.gif" ;break;
    	case "Hulk":path = "file:./assets/gif/hulk.gif" ;break;
    	case "Iceman":path = "file:./assets/gif/iceMan.gif" ;break;
    	case "Ironman":path = "file:./assets/gif/ironMan.gif" ;break;
    	case "Loki":path = "file:./assets/gif/loki.gif" ;break;
    	case "Quicksilver":path = "file:./assets/gif/quickSilver.gif" ;break;
    	case "Spiderman":path = "file:./assets/gif/spiderMan.gif" ;break;
    	case "Thor":path = "file:./assets/gif/thor.gif" ;break;
    	case "Venom": path = "file:./assets/gif/venom.gif" ; break;
    	case "Yellow Jacket": path = "file:./assets/gif/yellowJacket.gif";break;
    	}
    	ImageView view = new ImageView(path);
    	view.setId(name);
    	view.setFitHeight(120);
		view.setFitWidth(120);
    	return view;
    }
    
    public void switchScenes(Stage s, Scene scene) {
		s.setScene(scene);
    }
    
	public static void main(String[] args) {
		launch(args);
	//	button= new Button();
	}
}