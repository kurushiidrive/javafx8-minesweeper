import java.net.URL;
import java.util.Optional;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/*
 * Godfried Boateng; 20-26 March 2017
 */

public class Minesweeper extends Application {

	private Label numflags;
	private int num;		// the number showed on the "Time: " Label

	public static void main(String[] args) {
		launch (args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane root = new BorderPane ();

		MenuBar bar = new MenuBar ();

		Menu game = new Menu ("Game");
		MenuItem beg = new MenuItem("Beginner");
		MenuItem inter = new MenuItem ("Intermediate");
		MenuItem expert = new MenuItem ("Expert");
		MenuItem custom = new MenuItem ("Custom");
		MenuItem quit = new MenuItem ("Quit");
		quit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle (ActionEvent ae)
			{
				Platform.exit();
			}
		});
		game.getItems().addAll(beg, inter, expert, custom, quit);

		Menu options = new Menu ("Options");
		MenuItem nom = new MenuItem ("Set Number of Mines");
		MenuItem gridsize = new MenuItem ("Grid Size");
		options.getItems().addAll(nom, gridsize);

		Menu help = new Menu ("Help");
		MenuItem about = new MenuItem ("About");
		MenuItem htp = new MenuItem ("How To Play");
		help.getItems().addAll (about, htp);

		bar.getMenus().addAll (game, options, help);

		root.setTop (bar);


		HBox hud = new HBox (8);
		hud.setAlignment (Pos.CENTER);
		hud.setBackground (new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
		numflags = new Label ("Number of flags: ");
		setNum (0);
		Label time = new Label ("Time: " + num);
		hud.getChildren().addAll(numflags, time);

		root.setBottom (hud);


		VBox center = new VBox (); center.setAlignment(Pos.CENTER);
		ImageView face = new ImageView ();

		MineMap map = new MineMap ();
		face.setImage(map.getSmile());
		center.getChildren().add(face);

		MinesweeperModel mm = new MinesweeperModel (10, 10, 10); // default; able to be changed by menu actions
		MinesweeperController mc = new MinesweeperController (mm);
		map.setModel(mm);
		center.getChildren().add(map);
		root.setCenter(center);

		numflags.setText("Number of flags: " + mm.getFlags());

		AnimationTimer timer = new AnimationTimer () {
			long old_time = 0;

			@Override
			public void handle (long now)
			{
				if (now - old_time >= 1000000000)
				{
					time.setText ("Time: " + (++num));
					old_time = now;
				}
			}
		};

		map.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				int row = map.y_to_row(me.getY());
				int col = map.x_to_col(me.getX());
				mc.action(row, col, me.getButton() == MouseButton.PRIMARY ? true : false);
				if (mc.first_click())
					timer.start();
				map.resetCells();
				if (mc.lose()) {
					face.setImage(map.getLose());
					for (int i = 0; i < mc.getModel().getNumRows(); i++)
					{
						for (int j = 0; j < mc.getModel().getNumCols(); j++)
						{
							if (mc.getModel().isFlagged(i, j) && !mc.getModel().isMine(i, j))
								map.cellAt(i, j).setImage(map.getMine_wrong());
							else if (mc.getModel().isMine(i, j) && !mc.getModel().isRevealed(i, j))
								map.cellAt(i, j).setImage(map.getMine());
						}
					}
					timer.stop();
				}
				else if (mc.win()) {
					face.setImage(map.getWin());
					timer.stop();
				}
			}
		});

		map.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				face.setImage(map.getOoh());
			}
		});

		map.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				face.setImage(map.getSmile());
			}
		});

		face.setOnMouseClicked (new EventHandler<MouseEvent>() {
			@Override
			public void handle (MouseEvent me) {
				mc.custom_reset (mc.getModel().getNumRows(), mc.getModel().getNumCols(), mc.getModel().getNumMines());
				map.setModel (mc.getModel());
				face.setImage(map.getSmile());

				timer.stop();
				setNum (0);
				time.setText("Time: " + num);
			}
		});

		beg.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent ae) {
				mc.beg_reset();
				map.setModel(mc.getModel());
				face.setImage(map.getSmile());

				timer.stop();
				setNum (0);
				time.setText("Time: " + num);
			}
		});
		inter.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent ae) {
				mc.inter_reset();
				map.setModel(mc.getModel());
				face.setImage(map.getSmile());

				timer.stop();
				setNum (0);
				time.setText("Time: " + num);
			}
		});
		expert.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent ae) {
				mc.expert_reset();
				map.setModel(mc.getModel());
				face.setImage(map.getSmile());

				timer.stop();
				setNum (0);
				time.setText("Time: " + num);
			}
		});
		custom.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent ae) {
				int rows = 0, cols = 0, numMines = 0;
				TextInputDialog tid_rows = new TextInputDialog ();
				tid_rows.setTitle("Number of Rows");
				tid_rows.setHeaderText("Number of Rows");
				Optional<String> tmp_r = tid_rows.showAndWait();
				if (tmp_r.isPresent()) {
					try {
						rows = Integer.parseInt(tmp_r.get());
					}
					catch (NumberFormatException exc)
					{
						exc.printStackTrace();
					}
				}

				TextInputDialog tid_cols = new TextInputDialog ();
				tid_cols.setTitle("Number of Columns");
				tid_cols.setHeaderText("Number of Columns");
				Optional<String> tmp_c = tid_cols.showAndWait();
				if (tmp_c.isPresent()) { 
					try { 
						cols = Integer.parseInt(tmp_c.get());
					}
					catch (NumberFormatException exc)
					{
						exc.printStackTrace();
					}
				}

				TextInputDialog tid_mines = new TextInputDialog ();
				tid_mines.setTitle("Number of Mines");
				tid_mines.setHeaderText("Number of Mines");
				Optional<String> tmp_m = tid_mines.showAndWait();
				if (tmp_m.isPresent()) {
					try {
						numMines = Integer.parseInt(tmp_m.get());
					}
					catch (NumberFormatException exc)
					{
						exc.printStackTrace();
					}
				}

				mc.custom_reset(rows, cols, numMines);
				map.setModel(mc.getModel());
				face.setImage(map.getSmile());

				timer.stop();
				setNum (0);
				time.setText("Time: " + num);
			}	
		});

		nom.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent ae) {
				int numMines = 0;
				TextInputDialog tid_mines = new TextInputDialog ();
				tid_mines.setTitle("Number of Mines");
				tid_mines.setHeaderText("Number of Mines");
				Optional<String> tmp_m = tid_mines.showAndWait();
				if (tmp_m.isPresent()) {
					try {
						numMines = Integer.parseInt(tmp_m.get());
					}
					catch (NumberFormatException exc)
					{
						exc.printStackTrace();
					}
				}

				if (numMines >= 0 && numMines <= mc.getModel().getNumRows() * mc.getModel().getNumCols() - 1) {
					mc.mine_reset(numMines);
					map.setModel(mc.getModel());
					face.setImage(map.getSmile());

					timer.stop();
					setNum (0);
					time.setText("Time: " + num);
				}
				else
				{
					Alert alert = new Alert (AlertType.ERROR);
					alert.setHeaderText("Invalid Number of Mines Entered");
					alert.setTitle("Invalid Number of Mines Entered");
					alert.showAndWait();
				}
			}
		});

		gridsize.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent ae) {
				int rows = 0, cols = 0;

				TextInputDialog tid_rows = new TextInputDialog ();
				tid_rows.setTitle("Number of Rows");
				tid_rows.setHeaderText("Number of Rows");
				Optional<String> tmp_r = tid_rows.showAndWait();
				if (tmp_r.isPresent()) {
					try {
						rows = Integer.parseInt(tmp_r.get());
					}
					catch (NumberFormatException exc)
					{
						exc.printStackTrace();
					}
				}

				TextInputDialog tid_cols = new TextInputDialog ();
				tid_cols.setTitle("Number of Columns");
				tid_cols.setHeaderText("Number of Columns");
				Optional<String> tmp_c = tid_cols.showAndWait();
				if (tmp_c.isPresent()) { 
					try { 
						cols = Integer.parseInt(tmp_c.get());
					}
					catch (NumberFormatException exc)
					{
						exc.printStackTrace();
					}
				}

				mc.size_reset(rows, cols);
				map.setModel(mc.getModel());
				face.setImage(map.getSmile());


				timer.stop();
				setNum (0);
				time.setText("Time: " + num);
			}
		});

		about.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent ae) {
				Stage about_page = new Stage ();
				about_page.setTitle("About Minesweeper");

				VBox rt = new VBox (); rt.setAlignment(Pos.CENTER);
				Button okay = new Button ("OK");
				okay.setOnAction (new EventHandler<ActionEvent> () {
					@Override
					public void handle (ActionEvent ae) {
						about_page.close();
					}
				});
				WebView wv = new WebView ();
				WebEngine we = wv.getEngine();
				String url = getClass().getResource("about.html").toExternalForm();
				we.load (url);
				rt.getChildren().addAll(wv, okay);

				Scene scn = new Scene (rt);
				about_page.setScene(scn);
				about_page.show ();
			}
		});

		htp.setOnAction (new EventHandler<ActionEvent> () {
			@Override
			public void handle (ActionEvent ae) {
				Stage how_to_play = new Stage ();
				how_to_play.setTitle ("How to Play");

				VBox rt = new VBox (); rt.setAlignment (Pos.CENTER);
				Button okay = new Button ("OK");
				okay.setOnAction (new EventHandler<ActionEvent> () {
					@Override
					public void handle (ActionEvent ae) {
						how_to_play.close ();
					}
				});
				WebView wv = new WebView ();
				WebEngine we = wv.getEngine ();
				String url = getClass().getResource("howtoplay.html").toExternalForm();
				we.load(url);
				rt.getChildren().addAll (wv, okay);

				Scene scn = new Scene (rt);
				how_to_play.setScene(scn);
				how_to_play.show ();
			}
		});

		Scene scene = new Scene (root, 500, 500);
		stage.setScene (scene);
		stage.setTitle ("Minesweeper");
		stage.show(); 
	}

	private class MineMap extends Group
	{
		private Image unrevealed;
		private Image one;
		private Image two;
		private Image three;
		private Image four;
		private Image five;
		private Image six;
		private Image seven;
		private Image eight;
		private Image nil;
		private Image mine;

		private Image flag;
		private Image question;
		private Image mine_lose;
		private Image mine_revealed;
		private Image mine_wrong;

		private Image ooh;
		private Image lose;
		private Image smile;
		private Image win;

		private MinesweeperModel model;
		private ImageView[][] cells;
		private double imgwidth;
		private double imgheight;

		MineMap ()
		{
			model = null;
			cells = null;

			unrevealed = new Image ("file:minesweeper_images/images/blank.gif");
			one = new Image ("file:minesweeper_images/images/num_1.gif");
			two = new Image ("file:minesweeper_images/images/num_2.gif");
			three = new Image ("file:minesweeper_images/images/num_3.gif");
			four = new Image ("file:minesweeper_images/images/num_4.gif");
			five = new Image ("file:minesweeper_images/images/num_5.gif");
			six = new Image ("file:minesweeper_images/images/num_6.gif");
			seven = new Image ("file:minesweeper_images/images/num_7.gif");
			eight = new Image ("file:minesweeper_images/images/num_8.gif");
			nil = new Image ("file:minesweeper_images/images/num_0.gif");
			mine = new Image ("file:minesweeper_images/images/bomb_revealed.gif");

			flag = new Image ("file:minesweeper_images/images/bomb_flagged.png");
			question = new Image ("file:minesweeper_images/images/bomb_question.gif");
			mine_lose = new Image ("file:minesweeper_images/images/bomb_death.gif");
			mine_revealed = new Image ("file:minesweeper_images/images/bomb_revealed.gif");
			mine_wrong = new Image ("file:minesweeper_images/images/bomb_wrong.gif");

			ooh = new Image ("file:minesweeper_images/images/face_ooh.gif");
			lose = new Image ("file:minesweeper_images/images/face_dead.gif");
			smile = new Image ("file:minesweeper_images/images/face_smile.gif");
			win = new Image ("file:minesweeper_images/images/face_win.gif");

			imgwidth = nil.getWidth();
			imgheight = nil.getHeight();
		}

		public Image getUnrevealed() {
			return unrevealed;
		}

		public void setUnrevealed(Image unrevealed) {
			this.unrevealed = unrevealed;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (!model.isRevealed(i, j))
						cells[i][j].setImage(unrevealed);
		}

		public Image getOne() {
			return one;
		}

		public void setOne(Image one) {
			this.one = one;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.getStateAt(i, j) == MinesweeperModel.Cell.ONE)
						cells[i][j].setImage(one);
		}

		public Image getTwo() {
			return two;
		}

		public void setTwo(Image two) {
			this.two = two;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.getStateAt(i, j) == MinesweeperModel.Cell.TWO)
						cells[i][j].setImage(two);
		}

		public Image getThree() {
			return three;
		}

		public void setThree(Image three) {
			this.three = three;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.getStateAt(i, j) == MinesweeperModel.Cell.THREE)
						cells[i][j].setImage(three);
		}

		public Image getFour() {
			return four;
		}

		public void setFour(Image four) {
			this.four = four;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.getStateAt(i, j) == MinesweeperModel.Cell.FOUR)
						cells[i][j].setImage(four);
		}

		public Image getFive() {
			return five;
		}

		public void setFive(Image five) {
			this.five = five;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.getStateAt(i, j) == MinesweeperModel.Cell.FIVE)
						cells[i][j].setImage(five);
		}

		public Image getSix() {
			return six;
		}

		public void setSix(Image six) {
			this.six = six;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.getStateAt(i, j) == MinesweeperModel.Cell.SIX)
						cells[i][j].setImage(six);
		}

		public Image getSeven() {
			return seven;
		}

		public void setSeven(Image seven) {
			this.seven = seven;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.getStateAt(i, j) == MinesweeperModel.Cell.SEVEN)
						cells[i][j].setImage(seven);
		}

		public Image getEight() {
			return eight;
		}

		public void setEight(Image eight) {
			this.eight = eight;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.getStateAt(i, j) == MinesweeperModel.Cell.EIGHT)
						cells[i][j].setImage(eight);
		}

		public Image getNil() {
			return nil;
		}

		public void setNil(Image nil) {
			this.nil = nil;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.getStateAt(i, j) == MinesweeperModel.Cell.BLANK)
						cells[i][j].setImage(nil);
		}

		public Image getMine() {
			return mine;
		}

		public void setMine(Image mine) {
			this.mine = mine;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.isMine(i, j))
						cells[i][j].setImage(mine);
		}

		public Image getFlag() {
			return flag;
		}

		public void setFlag(Image flag) {
			this.flag = flag;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.isFlagged(i, j))
						cells[i][j].setImage(flag);
		}

		public Image getQuestion() {
			return question;
		}

		public void setQuestion(Image question) {
			this.question = question;
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < cells[i].length; j++)
					if (model.isQuestionMarked(i, j))
						cells[i][j].setImage(question);
		}

		public Image getMine_lose() {
			return mine_lose;
		}

		public void setMine_lose(Image mine_lose) {
			this.mine_lose = mine_lose;
		}

		public Image getMine_revealed() {
			return mine_revealed;
		}

		public void setMine_revealed(Image mine_revealed) {
			this.mine_revealed = mine_revealed;
		}

		public Image getMine_wrong() {
			return mine_wrong;
		}

		public void setMine_wrong(Image mine_wrong) {
			this.mine_wrong = mine_wrong;
		}

		public Image getOoh() {
			return ooh;
		}

		public void setOoh(Image ooh) {
			this.ooh = ooh;
		}

		public Image getLose() {
			return lose;
		}

		public void setLose(Image lose) {
			this.lose = lose;
		}

		public Image getSmile() {
			return smile;
		}

		public void setSmile(Image smile) {
			this.smile = smile;
		}

		public Image getWin() {
			return win;
		}

		public void setWin(Image win) {
			this.win = win;
		}

		public void setModel (MinesweeperModel mm)
		{
			this.model = mm;
			numflags.setText("Number of flags: " + mm.getFlags());
			resetCells ();
		}

		public void resetCells() {
			getChildren().remove(0, getChildren().size());
			cells = new ImageView[model.getNumRows()][model.getNumCols()];
			for (int i = 0; i < model.getNumRows(); i++)
			{
				for (int j = 0; j < model.getNumCols(); j++)
				{
					ImageView imgvw = new ImageView ();
					if (!model.isRevealed(i, j))
					{
						if (model.isQuestionMarked(i, j))
						{
							imgvw.setImage(question);
							numflags.setText("Number of flags: " + model.getFlags());
						}
						else if (model.isFlagged(i, j))
						{
							imgvw.setImage(flag);
							numflags.setText("Number of flags: " + model.getFlags());
						}
						else
							imgvw.setImage(unrevealed);
					}
					else
					{
						switch (model.getStateAt(i, j))
						{
						case MinesweeperModel.Cell.BLANK:
							imgvw.setImage(nil);
							break;
						case MinesweeperModel.Cell.ONE:
							imgvw.setImage(one);
							break;
						case MinesweeperModel.Cell.TWO:
							imgvw.setImage(two);
							break;
						case MinesweeperModel.Cell.THREE:
							imgvw.setImage(three);
							break;
						case MinesweeperModel.Cell.FOUR:
							imgvw.setImage(four);
							break;
						case MinesweeperModel.Cell.FIVE:
							imgvw.setImage(five);
							break;
						case MinesweeperModel.Cell.SIX:
							imgvw.setImage(six);
							break;
						case MinesweeperModel.Cell.SEVEN:
							imgvw.setImage(seven);
							break;
						case MinesweeperModel.Cell.EIGHT:
							imgvw.setImage(eight);
							break;
						case MinesweeperModel.Cell.MINE:
							imgvw.setImage(mine_lose);
							break;
						}
					}
					imgvw.setX(imgvw.getImage().getWidth() * j);
					imgvw.setY(imgvw.getImage().getHeight() * i);

					getChildren().add(imgvw);
					cells[i][j] = imgvw;

				}
			}
		}

		public ImageView cellAt (int row, int col)
		{
			return cells[row][col];
		}

		public double col_to_x (int col)
		{
			return imgwidth * col;
		}

		public double row_to_y (int row)
		{
			return imgheight * row;
		}

		public int x_to_col (double x)
		{
			return (int) (x / imgwidth);
		}

		public int y_to_row (double y)
		{
			return (int) (y / imgheight);
		}	
	}

	private int getNum ()
	{
		return num;
	}

	private void setNum (int it)
	{
		num = it;
	}

}
