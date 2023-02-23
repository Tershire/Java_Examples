import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

/**
 * jordle.
 * @author Wonhee Lee
 * @version v0.0.1
 */
public class Jordle extends Application {

    // Field ------------------------------------------------------------------------------
    static final int SCENE_SIZE_X = 360;
    static final int SCENE_SIZE_Y = (int) (SCENE_SIZE_X * 1.618);

    static final int NUM_ROW = 6;
    static final int NUM_COL = 5;

    private Label status;
    private Stage instStage = new Stage();
    private GridPane paneMain;
    private String word;
    private Text[][] cells;

    private int i = 0;
    private int j = 0;

    // Start ------------------------------------------------------------------------------
    @Override
    public void start(Stage primaryStage) {
        // First Word ---------------------------------------------------------------------
        generateRandomWord();

        // Primary Stage ------------------------------------------------------------------
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(4, 4, 4, 4));

        // Scene
        Scene scene = new Scene(pane, SCENE_SIZE_X, SCENE_SIZE_Y);

        // Stage
        primaryStage.setTitle("Jordle");
        primaryStage.setScene(scene);
        primaryStage.show();

        // TOP: Header ////////////////////////////////////////////////////////////////////
        VBox paneHeader = new VBox();
        Font font1 = Font.font("Cambria",
                               FontWeight.BOLD, FontPosture.REGULAR, 36);
        Font font2 = Font.font("Times New Roman",
                                FontWeight.BOLD, FontPosture.ITALIC, 18);

        Label title = new Label("JORDLE");
        title.setFont(font1);
        title.setTextFill(Color.BLACK);

        Label subTitle = new Label("Java Wordle from Georgia Tech");
        subTitle.setFont(font2);
        subTitle.setTextFill(Color.INDIGO);

        paneHeader.getChildren().add(title);
        paneHeader.getChildren().add(subTitle);

        // settings
        paneHeader.setPadding(new Insets(5, 2, 20, 2));
        paneHeader.setStyle("-fx-background-color: IVORY");

        // TOP: Status ////////////////////////////////////////////////////////////////////
        StackPane paneStatus = new StackPane();
        status = new Label("Guess the Word");
        status.setFont(font2);
        status.setTextFill(Color.DARKBLUE);

        paneStatus.getChildren().add(status);

        // settings
        paneStatus.setPadding(new Insets(25, 2, 20, 2));
        paneStatus.setStyle("-fx-background-color: IVORY");
        StackPane .setAlignment(status, Pos.CENTER);
        BorderPane.setAlignment(paneStatus, Pos.CENTER);

        // CENTER: Main Game //////////////////////////////////////////////////////////////
        paneMain = new GridPane();
        cells = new Text[NUM_ROW][NUM_COL];

        // initial cell settings
        for (int p = 0; p < NUM_ROW; p++) {
            for (int q = 0; q < NUM_COL; q++) {
                cells[p][q] = new Text(" ");
            }
        }

        for (int p = 0; p < NUM_ROW; p++) {
            for (int q = 0; q < NUM_COL; q++) {
                Rectangle tile = new Rectangle(0, 0, 25, 25);
                tile.setStyle("-fx-fill: TRANSPARENT; -fx-stroke: BLACK");
                StackPane paneBlock = new StackPane();
                paneBlock.getChildren().add(tile);
                paneBlock.getChildren().add(cells[p][q]);
                paneMain.add(paneBlock, q, p);
            }
        }

        // settings
        paneMain.setAlignment(Pos.CENTER);
        paneMain.setPadding(new Insets(10, 10, 10, 10));
        paneMain.setHgap(5.5);
        paneMain.setVgap(5.5);

        // Event
        pane.setOnKeyPressed(
            new EventHandler<KeyEvent>() {
                public void handle(KeyEvent e) {
                    keyInputManager(e);
                }
            }
        );
        pane.requestFocus();

        // LEFT: Utilities ////////////////////////////////////////////////////////////////
        VBox boxUtil = new VBox();
        Button btnInst = new Button("Instructions");
        boxUtil.getChildren().add(btnInst);

        // RIGHT: Game Control ////////////////////////////////////////////////////////////
        VBox boxCtrl = new VBox();
        Button btnReset = new Button("RESTART");
        boxCtrl.getChildren().add(btnReset);

        // Event
        btnReset.setOnAction(e -> {
            resetCells();
            generateRandomWord();
            status.setText("Guess the Word");
            }
        );

        // <+>
        btnReset.setFocusTraversable(false);        // this prevents focus not distracted

        // Stage: instruction XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        Pane paneInst = new Pane();
        Text inst = new Text(20, 50,  "The goal is to make the correct guess\n"
                                    + "of the 5 letter word.\n\n"
                                    + "You have 6 tries\n"
                                    + "So be prudent before hit Enter\n\n"
                                    + "< COLOR LEGEND >\n"
                                    + "GREEN :     existing letter in correct place\n"
                                    + "YELLOW:     existing letter in wrong   place\n"
                                    + "GREY  : non-existing letter\n\n\n\n"
                                    + "Ready? Go! Good Luck!\n");
        inst.setFont(Font.font("Courier New", 12));
        paneInst.getChildren().add(inst);

        // Event
        btnInst.setOnAction(e -> {
                instStage.show();
            }
        );

        // <+>
        btnInst.setFocusTraversable(false);         // this prevents focus not distracted

        // Scene
        Scene sceneInst = new Scene(paneInst, SCENE_SIZE_X, 450);

        // Stage
        instStage.setTitle("Instructions");
        instStage.setScene(sceneInst);

        ///////////////////////////////////////////////////////////////////////////////////

        // Pane ---------------------------------------------------------------------------
        pane.setTop(paneHeader);
        pane.setRight(boxCtrl);
        pane.setBottom(paneStatus);
        pane.setLeft(boxUtil);
        pane.setCenter(paneMain);
    }

    // Helpers ------------------------------------------------------------------------
    /**
     * checks character match and assign color as accordingly.
     * @return  number of correct characters
     */
    public int colorCells() {

        int numCorrects = 0;
        for (int q = 0; q < NUM_COL; q++) {

            // get the character from the string in cell
            char cellChar = cells[i][q].getText().charAt(0);

            // get the paneBlock of the grid pane to change background color
            Node panelBlock = paneMain.getChildren().get(NUM_COL * i + q);

            if (cellChar == (word.charAt(q))) {
                panelBlock.setStyle("-fx-background-color: LIGHTGREEN");
                numCorrects += 1;

            } else if (word.contains(String.valueOf(cellChar))) {
                panelBlock.setStyle("-fx-background-color: LIGHTYELLOW");

            } else {
                panelBlock.setStyle("-fx-background-color: LIGHTGREY");

            }

        }
        return numCorrects;

    }

    /**
     * reset cells to make them empty.
     */
    public void resetCells() {

        for (int p = 0; p < NUM_ROW; p++) {
            for (int q = 0; q < NUM_COL; q++) {

                Node panelBlock = paneMain.getChildren().get(NUM_COL * p + q);
                panelBlock.setStyle("-fx-background-color: TRANSPARENT");

                cells[p][q].setText(" ");

            }
        }

        i = 0;
        j = 0;

    }

    /**
     * generate a random word from the list of Words.java file.
     */
    public void generateRandomWord() {

        Random random = new Random();

        word = Words.list.get(random.nextInt(Words.list.size())).toUpperCase();
        System.out.println(word); // TEST

    }

    /**
     * controls the flow depending on the user key input.
     * @param e KeyEvent object
     */
    public void keyInputManager(KeyEvent e) {
        int numCorrects;

        if (i < NUM_ROW && i >= 0) {

            switch (e.getCode()) {
            case ENTER:
                if (j == NUM_COL) {
                    // evaluate & colorcode
                    numCorrects = colorCells();

                    j = 0;
                    i += 1;

                    if (numCorrects == NUM_COL) {
                        status.setText("Congratulations!");
                        i = -1;

                    } else if (i == NUM_ROW) {
                        status.setText("Not Good. Try Again.");

                    }

                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR,
                                            "Please Enter " + NUM_COL + " Letter Word");
                    error.setTitle("Number of Letters Error");
                    error.showAndWait();

                }
                break;

            case BACK_SPACE:
                if (j > 0) {
                    j--;
                    cells[i][j].setText(" ");

                }
                break;

            default:
                String s = e.getText().toUpperCase();
                if (j < NUM_COL) {
                    // constraint to alphabets
                    if (s.compareTo("A") >= 0 && s.compareTo("Z") <= 0) {
                        cells[i][j].setText(s);
                        j++;
                    }
                }
                break;

            }
        }
    }

    // Static Methods ---------------------------------------------------------------------
    /**
     * main.
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}