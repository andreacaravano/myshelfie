package it.polimi.ingsw.Client;

import it.polimi.ingsw.Common.BoardInterface;
import it.polimi.ingsw.Common.CommonGoalCardInterface;
import it.polimi.ingsw.Common.PersonalGoalCardInterface;
import it.polimi.ingsw.Common.ShelfInterface;
import it.polimi.ingsw.Utility.ClassicLock;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * This class define game's GUI
 */
public class GUI extends Application {
    /**
     * Game's login width resolution
     */
    private static final int loginRESX = 800;
    /**
     * Game's login height resolution
     */
    private static final int loginRESY = 400;
    /**
     * End game card's resolution
     */
    private static final double egcRES = 55;
    /**
     * Shelf cells' pointers
     */
    private static final ArrayList<ArrayList<ImageView>> shelfColumnsCells = new ArrayList<>();
    /**
     * Board cells' pointers
     */
    private static final ArrayList<ArrayList<CellDataModel>> boardRowsCells = new ArrayList<>();
    /**
     * Support array with board cell's position
     */
    private static final ArrayList<ArrayList<Integer>> positions = new ArrayList<ArrayList<Integer>>(9);
    /**
     * Attribute that indicate if is player's turn
     */
    private static final Text yourTurnText = new Text("Your turn");
    /**
     * Menu's lock
     */
    public static ClassicLock allowExitMenu = new ClassicLock(true);
    /**
     * Login's lock
     */
    public static ClassicLock allowLogin = new ClassicLock(true);
    /**
     * Move's lock
     */
    public static ClassicLock allowMove = new ClassicLock(true);
    /**
     * Scoreboard's lock
     */
    public static ClassicLock allowScoreboard = new ClassicLock(true);
    /**
     * Identifier's lock
     */
    public static ClassicLock allowIdentifier = new ClassicLock(true);
    /**
     * Game's main stage
     */
    private static Stage mainStage;
    /**
     * Main menu's scene
     */
    private static Scene mainMenuScene;
    /**
     * Login's scene
     */
    private static Scene loginScene;
    /**
     * Credits' scene
     */
    private static Scene creditsScene;
    /**
     * Game's scene
     */
    private static Scene gameScene;
    /**
     * Scoreboard's scene
     */
    private static Scene scoreboardScene;
    /**
     * Loading's scene
     */
    private static Scene loadingScene;
    /**
     * Second common goal card available points' ImageView attribute
     */
    private static ImageView secondCGCAvailablePointsImageView;
    /**
     * First common goal card available points' ImageView attribute
     */
    private static ImageView firstCGCAvailablePointsImageView;
    /**
     * First common goal card points' ImageView attribute
     */
    private static ImageView firstCGCPImageView;
    /**
     * Second common goal card points' ImageView attribute
     */
    private static ImageView secondCGCPImageView;
    /**
     * Personal goal card ImageView attribute
     */
    private static ImageView pgcImageView;
    /**
     * First common goal card's ImageView attribute
     */
    private static ImageView firstCGCImageView;
    /**
     * Second common goal card's ImageView attribute
     */
    private static ImageView secondCGCImageView;
    /**
     * ScoreBoard rootPane's attribute
     */
    private static AnchorPane sbRootPane;
    /**
     * Game rootPane's attribute
     */
    private static AnchorPane rootPane;
    /**
     * Player's nickname Text attribute
     */
    private static Text nicknameText;
    /**
     * Attribute that indicate the textArea to insert player's number
     */
    private static Text numPlayer;
    /**
     * TextArea used to insert number of players
     */
    private static TextArea numPlayerTextArea;
    /**
     * Selected cells' pointers attribute
     */
    private static ArrayList<CellDataModel> selectedCells = new ArrayList<>();
    /**
     * Attribute that allow or not shelf's columns selection
     */
    private static boolean isMyTurn = false;
    /**
     * Selected column's number
     */
    private static int selectedColumn = -1;
    /**
     * Selected cells' x positions
     */
    private static List<Integer> x = new ArrayList<>();
    /**
     * Selected cells' y positions
     */
    private static List<Integer> y = new ArrayList<>();
    /**
     * Game'ID
     */
    private static String gameID;
    /**
     * Game's number of players
     */
    private static String numOfPlayers;
    /**
     * Player's nickname
     */
    private static String nickname;
    /**
     * Valued to true if the client is joining a new match
     */
    private static boolean newMatch = false;
    /**
     * Game's width resolution
     */
    private final int GameRESX = 1024;
    /**
     * Game's height resolution
     */
    private final int GameRESY = 600;
    /**
     * Game's menu' width resolution
     */
    private final int mainMenuRESX = 1024;
    /**
     * Game's menu' height resolution
     */
    private final int mainMenuRESY = 768;
    /**
     * Border's dimension value
     */
    private final double borderDimension = 4;
    /**
     * Border's dimension
     */
    private final BorderWidths borderWidths = new BorderWidths(borderDimension);
    /**
     * Board's dimension
     */
    private final double boardRES = (double) this.GameRESX / 2;
    /**
     * Shelf's dimension
     */
    private final double shelfRES = boardRES * 3 / 5;
    /**
     * Common goal card's width resolution
     */
    private final double cgcRESX = boardRES * (1.5 / 5);
    /**
     * Common goal card's height resolution
     */
    private final double cgcRESY = (914 * this.cgcRESX) / 1384;
    /**
     * Personal goal card's resolution
     */
    private final double pgcRES = this.cgcRESX;
    /**
     * CGC points card's resolution
     */
    private final double cgcPointsRES = cgcRESX * 9 / 20;
    /**
     * Score card's resolution
     */
    private final double scRES = 48;
    /**
     * Shelf columns' pointers
     */
    private final ArrayList<VBox> shelfColumns = new ArrayList<>();
    /**
     * Shelf cells' dimension
     */
    private final int shelfCellDim = 36;
    /**
     * Board rows' pointers
     */
    private final ArrayList<HBox> boardRows = new ArrayList<>();
    /**
     * Board cells' dimension
     */
    private final int boardCellDim = 50;

    /**
     * This method builds end game card's GUI
     */
    public static void buildEndGameCard() {
        Image egcImage = new Image("gui/sc/end_game.jpg");
        ImageView egcImageView = new ImageView();
        Platform.runLater(() -> {
            egcImageView.setImage(egcImage);
            egcImageView.setFitHeight(egcRES);
            egcImageView.setPreserveRatio(true);
            egcImageView.setRotate(8);
            egcImageView.setLayoutX(417);
            egcImageView.setLayoutY(359);
            rootPane.getChildren().add(egcImageView);
        });
    }

    /**
     * This method cleans the insertion's attributes
     */
    public static void resetSelection() {
        selectedColumn = -1;
        x = new ArrayList<>();
        y = new ArrayList<>();
        selectedCells.stream().forEach(e -> e.getCell().setEffect(null));
        selectedCells = new ArrayList<>();
    }

    /**
     * Sets visibility of nickname/number of players text areas
     *
     * @param isVisible to turn visibility on
     */
    public static void setNewMatch(boolean isVisible) {
        if (isVisible) {
            Platform.runLater(() -> {
                numPlayer.setVisible(true);
                numPlayerTextArea.setVisible(true);
            });
            newMatch = isVisible;
        }
    }

    /**
     * This method build score board's GUI
     *
     * @param scoreboard map received from the server
     */
    public static void buildScoreBoard(Map<String, Integer> scoreboard) {
        ArrayList<PlayerDataModel> players = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : scoreboard.entrySet()) {
            players.add(new PlayerDataModel(entry.getKey(), entry.getValue()));
        }
        //Build the table
        VBox scoreboardVbox = new VBox();
        Platform.runLater(() -> {
            scoreboardVbox.setMinSize(loginRESX - 150, loginRESY - 150);
            scoreboardVbox.setMaxSize(loginRESX - 150, loginRESY - 150);
            scoreboardVbox.setLayoutX(75);
            scoreboardVbox.setLayoutY(25);
            scoreboardVbox.setSpacing(20);
            scoreboardVbox.setAlignment(Pos.CENTER);
        });
        TableView sbTable = new TableView<PlayerDataModel>();
        TableColumn nicknameCol = new TableColumn<PlayerDataModel, String>("Nickname");
        Platform.runLater(() -> {
            nicknameCol.setCellValueFactory(new PropertyValueFactory<PlayerDataModel, String>("nickname"));
        });
        TableColumn pointsCol = new TableColumn<PlayerDataModel, Integer>("Points");
        Platform.runLater(() -> {
            pointsCol.setCellValueFactory(new PropertyValueFactory<PlayerDataModel, Integer>("points"));
            sbTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            sbTable.setEditable(false);
            sbTable.getColumns().addAll(nicknameCol, pointsCol);
        });
        //Add elements to the table
        for (int i = 0; i < players.size(); i++) {
            int finalI = i;
            Platform.runLater(() -> {
                sbTable.getItems().add(players.get(finalI));
            });
        }
        //Button
        Button gobackButton = new Button();
        Platform.runLater(() -> {
            gobackButton.setText("QUIT");
            gobackButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 20");
            gobackButton.setBackground(Background.EMPTY);
            gobackButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 20");
            gobackButton.setTextFill(Color.BLACK);
            gobackButton.setAlignment(Pos.CENTER_LEFT);
            scoreboardVbox.getChildren().addAll(sbTable, gobackButton);
            sbRootPane.getChildren().add(scoreboardVbox);
        });
        scoreboardButtonEvent(gobackButton);
    }

    /**
     * This method defines score board button's events
     *
     * @param gobackButton button
     */
    private static void scoreboardButtonEvent(Button gobackButton) {
        gobackButton.setOnMouseClicked(mouseEvent -> {
            allowScoreboard.unlock();
        });
        gobackButton.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                gobackButton.setTextFill(Color.BROWN);
            });
        });
        gobackButton.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                gobackButton.setTextFill(Color.BLACK);
            });
        });
    }

    /**
     * This method builds loading scene's GUI
     *
     * @param rootPane root AnchorPane
     */
    private static void buildLoading(AnchorPane rootPane) {
        VBox loadingVbox = new VBox();
        Text loadingText = new Text();
        ProgressIndicator indicator = new ProgressIndicator();
        Platform.runLater(() -> {
            loadingVbox.setAlignment(Pos.CENTER);
            loadingVbox.setSpacing(20);
            loadingVbox.setMinSize(loginRESX, loginRESY);
            loadingVbox.setMaxSize(loginRESX, loginRESY);
            loadingText.setText("Waiting for player/s to join");
            loadingText.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 30");
            indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            indicator.setMinSize(100, 100);
            loadingVbox.getChildren().addAll(loadingText, indicator);
            rootPane.getChildren().add(loadingVbox);
        });
    }

    /**
     * This method builds error messages
     *
     * @param errorText text to visualize
     */
    public static void errorMessage(String errorText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(errorText);
            alert.setResizable(true);
            alert.show();
        });
    }

    /**
     * This method builds error messages waiting for user confirmation
     *
     * @param errorText text to visualize
     */
    public static void errorMessageConfirm(String errorText) {
        RunnableFuture<Void> task = new FutureTask<>(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(errorText);
            alert.setResizable(true);
            alert.showAndWait();
        }, null);
        Platform.runLater(task);
        try {
            task.get();
        } catch (Exception ignored) {
        }
    }

    /**
     * This method builds information messages
     *
     * @param mexText text to visualize
     */
    public static void infoMessage(String mexText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(mexText);
            alert.setResizable(true);
            alert.show();
        });
    }

    /**
     * This method is used to update game's board
     *
     * @param board game's board
     * @throws Exception related to Model management
     */
    public static void updateBoard(BoardInterface board) throws Exception {
        String path = "gui/oc/";

        //First row
        for (int i = 0; i < 2; i++) {
            if (board.getCardImageFromSpace(0, positions.get(0).get(i)) != null) {
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        boardRowsCells.get(0).get(finalI).getCell().setImage(new Image(path.concat(board.getCardImageFromSpace(0, positions.get(0).get(finalI))).concat(".png")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else boardRowsCells.get(0).get(i).getCell().setImage(null);
        }
        //Second row
        for (int i = 0; i < 3; i++) {
            if (board.getCardImageFromSpace(1, positions.get(1).get(i)) != null) {
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        boardRowsCells.get(1).get(finalI).getCell().setImage(new Image(path.concat(board.getCardImageFromSpace(1, positions.get(1).get(finalI))).concat(".png")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else boardRowsCells.get(1).get(i).getCell().setImage(null);
        }
        //Third row
        for (int i = 0; i < 5; i++) {
            if (board.getCardImageFromSpace(2, positions.get(2).get(i)) != null) {
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        boardRowsCells.get(2).get(finalI).getCell().setImage(new Image(path.concat(board.getCardImageFromSpace(2, positions.get(2).get(finalI))).concat(".png")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else boardRowsCells.get(2).get(i).getCell().setImage(null);
        }
        //Fourth row
        for (int i = 0; i < 8; i++) {
            if (board.getCardImageFromSpace(3, positions.get(3).get(i)) != null) {
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        boardRowsCells.get(3).get(finalI).getCell().setImage(new Image(path.concat(board.getCardImageFromSpace(3, positions.get(3).get(finalI))).concat(".png")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else boardRowsCells.get(3).get(i).getCell().setImage(null);
        }
        //Fifth row
        for (int i = 0; i < 9; i++) {
            if (board.getCardImageFromSpace(4, positions.get(4).get(i)) != null) {
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        boardRowsCells.get(4).get(finalI).getCell().setImage(new Image(path.concat(board.getCardImageFromSpace(4, positions.get(4).get(finalI))).concat(".png")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else boardRowsCells.get(4).get(i).getCell().setImage(null);
        }
        //Sixth row
        for (int i = 0; i < 8; i++) {
            if (board.getCardImageFromSpace(5, positions.get(5).get(i)) != null) {
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        boardRowsCells.get(5).get(finalI).getCell().setImage(new Image(path.concat(board.getCardImageFromSpace(5, positions.get(5).get(finalI))).concat(".png")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else boardRowsCells.get(5).get(i).getCell().setImage(null);
        }
        //Seventh row
        for (int i = 0; i < 5; i++) {
            if (board.getCardImageFromSpace(6, positions.get(6).get(i)) != null) {
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        boardRowsCells.get(6).get(finalI).getCell().setImage(new Image(path.concat(board.getCardImageFromSpace(6, positions.get(6).get(finalI))).concat(".png")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else boardRowsCells.get(6).get(i).getCell().setImage(null);
        }
        //Eight row
        for (int i = 0; i < 3; i++) {
            if (board.getCardImageFromSpace(7, positions.get(7).get(i)) != null) {
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        boardRowsCells.get(7).get(finalI).getCell().setImage(new Image(path.concat(board.getCardImageFromSpace(7, positions.get(7).get(finalI))).concat(".png")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else boardRowsCells.get(7).get(i).getCell().setImage(null);
        }
        //Ninth row
        for (int i = 0; i < 2; i++) {
            if (board.getCardImageFromSpace(8, positions.get(8).get(i)) != null) {
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        boardRowsCells.get(8).get(finalI).getCell().setImage(new Image(path.concat(board.getCardImageFromSpace(8, positions.get(8).get(finalI))).concat(".png")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else boardRowsCells.get(8).get(i).getCell().setImage(null);
        }
    }

    /**
     * This method is used to update game's shelf
     *
     * @param shelf game's shelf
     */
    public static void updateShelf(ShelfInterface shelf) {
        String path = "gui/oc/";
        for (int i = 0; i < ShelfInterface.SHELF_DIM_Y; i++) {
            for (int j = 0; j < ShelfInterface.SHELF_DIM_X; j++) {
                int finalJ = j;
                int finalI = i;
                Platform.runLater(() -> {
                    try {
                        if (shelf.getCardImage(finalJ, finalI) != null)
                            shelfColumnsCells.get(finalI).get(finalJ).setImage(new Image(path.concat(shelf.getCardImage(finalJ, finalI)).concat(".png")));
                        else shelfColumnsCells.get(finalI).get(finalJ).setImage(null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    /**
     * This method is used to update personal goal cards
     *
     * @param pgCard game's personal goal card
     * @throws RemoteException related to server
     */
    public static void updatePersonalGoalCard(PersonalGoalCardInterface pgCard) throws RemoteException {
        String path = "gui/pgc/" + pgCard.getType() + ".png";
        Platform.runLater(() -> {
            pgcImageView.setImage(new Image(path));
        });
    }

    /**
     * This method is used to update common goal cards
     *
     * @param firstCGC  game's first common goal card
     * @param secondCGC game's second common goal card
     * @throws RemoteException related to server
     */
    public static void updateCommonGoalCards(CommonGoalCardInterface firstCGC, CommonGoalCardInterface secondCGC) throws RemoteException {
        String path1 = "gui/cgc/" + firstCGC.getType() + ".jpg";
        String path2 = "gui/cgc/" + secondCGC.getType() + ".jpg";
        if (firstCGC.getIncrements().size() > 0) {
            String pointsPath1 = "gui/sc/" + "scoring_" + firstCGC.getIncrements().pop().getValue() + ".jpg";
            Platform.runLater(() -> {
                firstCGCAvailablePointsImageView.setImage(new Image(pointsPath1));
            });
        } else if (firstCGC.getIncrements().size() == 0) {
            String pointsPath1 = "gui/sc/scoring_back_EMPTY.jpg";
            Platform.runLater(() -> {
                firstCGCAvailablePointsImageView.setImage(new Image(pointsPath1));
            });
        }
        if (secondCGC.getIncrements().size() > 0) {
            String pointsPath2 = "gui/sc/" + "scoring_" + secondCGC.getIncrements().pop().getValue() + ".jpg";
            Platform.runLater(() -> {
                secondCGCAvailablePointsImageView.setImage(new Image(pointsPath2));
            });
        } else if (secondCGC.getIncrements().size() == 0) {
            String pointsPath2 = "gui/sc/scoring_back_EMPTY.jpg";
            Platform.runLater(() -> {
                secondCGCAvailablePointsImageView.setImage(new Image(pointsPath2));
            });
        }
        Platform.runLater(() -> {
            firstCGCImageView.setImage(new Image(path1));
            secondCGCImageView.setImage(new Image(path2));
        });
    }

    /**
     * This method updates common goal cards score's cards
     *
     * @param scoreCards List with gained points
     * @throws RemoteException related to server
     */
    public static void updateCGCScoredPoints(ArrayList<Integer> scoreCards) throws RemoteException {
        if (scoreCards.size() > 0) {
            List<String> pointsPath = new ArrayList<>();
            for (Integer sc : scoreCards) {
                if (sc != 1)
                    pointsPath.add("gui/sc/" + "scoring_" + sc + ".jpg");
            }
            if (pointsPath.size() == 1) {
                firstCGCPImageView.setImage(new Image(pointsPath.get(0)));
            } else if (pointsPath.size() == 2) {
                firstCGCPImageView.setImage(new Image(pointsPath.get(0)));
                firstCGCPImageView.setImage(new Image(pointsPath.get(1)));
            }
        }
    }

    /**
     * This method changes game's current scene
     *
     * @param scene scene to set
     */
    public static void setScene(Scenes scene) {
        switch (scene) {
            case mainMenuScene: {
                Platform.runLater(() -> {
                    mainStage.setScene(mainMenuScene);
                });
                break;
            }
            case loginScene: {
                Platform.runLater(() -> {
                    mainStage.setScene(loginScene);
                });
                break;
            }
            case gameScene: {
                Platform.runLater(() -> {
                    mainStage.setScene(gameScene);
                });
                break;
            }
            case scoreboardScene: {
                Platform.runLater(() -> {
                    mainStage.setScene(scoreboardScene);
                });
                break;
            }
            case loadingScene: {
                Platform.runLater(() -> {
                    mainStage.setScene(loadingScene);
                });
                break;
            }
            case creditsScene: {
                Platform.runLater(() -> {
                    mainStage.setScene(creditsScene);
                });
                break;
            }
        }
    }

    /**
     * Getter method for the game's ID
     *
     * @return game's ID
     */
    public static String getGameID() {
        return gameID;
    }

    /**
     * Getter method for game's number of players
     *
     * @return game's number of players
     */
    public static String getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     * Getter method for player's nickname
     *
     * @return player's nickname
     */
    public static String getNickname() {
        return nickname;
    }

    /**
     * Getter method for selected column
     *
     * @return selected column
     */
    public static int getSelectedColumn() {
        return selectedColumn;
    }

    /**
     * Getter method for x selected cells coordinates
     *
     * @return x coordinates
     */
    public static List<Integer> getX() {
        return x;
    }

    /**
     * Getter method for x selected cells coordinates
     *
     * @return y coordinates
     */
    public static List<Integer> getY() {
        return y;
    }

    /**
     * Setter method for boolean attribute "myTurn"
     *
     * @param myTurn value to set
     */
    public static void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
        yourTurnText.setVisible(isMyTurn);
    }

    /**
     * Setter method for nickname's TextArea attribute
     *
     * @param text text to set
     */
    public static void setNicknameText(String text) {
        Platform.runLater(() -> {
            nicknameText.setText(text);
        });
    }

    /**
     * Launch GUI
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * This method instantiates game's GUI component
     *
     * @param stage game's stage
     */
    @Override
    public void start(Stage stage) {
        buildBoardPositions();
        mainStage = stage;
        Image background = new Image("gui/misc/sfondo_parquet.jpg");
        BackgroundImage bImg = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background bGround = new Background(bImg);
        stage.setOnCloseRequest(windowEvent -> {
                    stage.close();
                    System.exit(1);
                }
        );
        //MainmenuScene
        Group mainMenuRoot = new Group();
        AnchorPane mainMenuRootPane = new AnchorPane();
        Platform.runLater(() -> {
            mainMenuRootPane.setMinSize(this.mainMenuRESX, this.mainMenuRESY);
            mainMenuRoot.getChildren().add(mainMenuRootPane);
            mainMenuRootPane.setBackground(bGround);
        });
        mainMenuScene = new Scene(mainMenuRoot, this.mainMenuRESX, this.mainMenuRESY);
        buildMenu(mainMenuRootPane);
        //LoginScene
        Group loginSceneRoot = new Group();
        AnchorPane loginSceneRootPane = new AnchorPane();
        Platform.runLater(() -> {
            mainMenuScene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto+Slab&display=swap");
            loginSceneRootPane.setMinSize(loginRESX, loginRESY);
            loginSceneRoot.getChildren().add(loginSceneRootPane);
        });
        loginScene = new Scene(loginSceneRoot, loginRESX, loginRESY);
        Platform.runLater(() -> {
            loginScene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto+Slab&display=swap");
            loginSceneRootPane.setBackground(bGround);
        });
        buildLogin(loginSceneRootPane);
        //CreditsScene
        Group creditsSceneRoot = new Group();
        AnchorPane creditsSceneRootPane = new AnchorPane();
        Platform.runLater(() -> {
            creditsSceneRootPane.setMinSize(loginRESX, loginRESY);
            creditsSceneRoot.getChildren().add(creditsSceneRootPane);
        });
        creditsScene = new Scene(creditsSceneRoot, loginRESX, loginRESY);
        Platform.runLater(() -> {
            creditsScene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto+Slab&display=swap");
            creditsSceneRootPane.setBackground(bGround);
        });
        buildCredits(creditsSceneRootPane);
        //LoadingScene
        Group loadingSceneRoot = new Group();
        AnchorPane loadingSceneRootPane = new AnchorPane();
        Platform.runLater(() -> {
            loadingSceneRootPane.setMinSize(loginRESX, loginRESY);
            loadingSceneRoot.getChildren().add(loadingSceneRootPane);
        });
        loadingScene = new Scene(loadingSceneRoot, loginRESX, loginRESY);
        Platform.runLater(() -> {
            loadingScene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto+Slab&display=swap");
            loadingSceneRootPane.setBackground(bGround);
        });
        buildLoading(loadingSceneRootPane);
        //GameScene
        Group gameRoot = new Group();
        rootPane = new AnchorPane();
        Platform.runLater(() -> {
            rootPane.setMinSize(this.GameRESX, this.GameRESY);
            gameRoot.getChildren().add(rootPane);
        });
        buildBoard();
        buildShelf();
        buildCommonGoalCards();
        buildPersonalGoalCard();
        buildCommonGoalCardsPointCards();
        buildGameButtons();
        buildShelfGrid();
        buildBoardGrid();
        buildFirstCGCAvailablePoints();
        buildSecondCGCAvailablePoints();
        buildNickname();
        gameScene = new Scene(gameRoot, this.GameRESX, this.GameRESY);
        Platform.runLater(() -> {
            gameScene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto+Slab&display=swap");
            rootPane.setBackground(bGround);
        });
        //ScoreboardScene
        Group sbRoot = new Group();
        sbRootPane = new AnchorPane();
        Platform.runLater(() -> {
            sbRootPane.setMinSize(loginRESX, loginRESY);
        });
        scoreboardScene = new Scene(sbRoot, loginRESX, loginRESY);
        Platform.runLater(() -> {
            scoreboardScene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto+Slab&display=swap");
            sbRoot.getChildren().add(sbRootPane);
            sbRootPane.setBackground(bGround);
        });
        //mainStage
        Platform.runLater(() -> {
            mainStage.setTitle("My Shelfie - AM34");
            mainStage.setScene(mainMenuScene);
            mainStage.setResizable(false);
            mainStage.getIcons().add(new Image("gui/misc/Icon.png"));
            mainStage.show();
        });
    }

    /**
     * This method builds the board's GUI
     */
    private void buildBoard() {
        Image boardImage = new Image("gui/board/livingroom.png");
        ImageView boardImageView = new ImageView();
        Platform.runLater(() -> {
            boardImageView.setImage(boardImage);
            boardImageView.setFitHeight(this.boardRES);
            boardImageView.setFitWidth(this.boardRES);
            boardImageView.setPreserveRatio(true);
        });
        HBox boardImageContainer = new HBox(boardImageView);
        Platform.runLater(() -> {
            boardImageContainer.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, this.borderWidths)));
            rootPane.getChildren().add(boardImageContainer);
        });
    }

    /**
     * This method builds the shelf's GUI
     */
    private void buildShelf() {
        Image shelfImage = new Image("gui/board/bookshelf_orth.png");
        ImageView shelfImageView = new ImageView();
        Platform.runLater(() -> {
            shelfImageView.setImage(shelfImage);
            shelfImageView.setFitHeight(this.shelfRES);
            shelfImageView.setFitWidth(this.shelfRES);
            shelfImageView.setX(this.boardRES + 50);
            shelfImageView.setY(10);
            shelfImageView.setPreserveRatio(true);
            rootPane.getChildren().add(shelfImageView);
        });
    }

    /**
     * This method builds common goal cards' GUI
     */
    private void buildCommonGoalCards() {
        Label firstLabel = new Label("First Common Goal Card");
        Label secondLabel = new Label("Second Common Goal Card");
        Platform.runLater(() -> {
            firstLabel.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 12");
            firstLabel.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 12");
        });
        firstCGCImageView = new ImageView();
        secondCGCImageView = new ImageView();
        Platform.runLater(() -> {
            firstCGCImageView.setPreserveRatio(true);
            firstCGCImageView.setFitWidth(this.cgcRESX);
            secondCGCImageView.setPreserveRatio(true);
            secondCGCImageView.setFitWidth(this.cgcRESX);
        });
        HBox firstImageContainer = new HBox(firstCGCImageView);
        Platform.runLater(() -> {
            firstImageContainer.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, this.borderWidths)));
        });
        HBox secondImageContainer = new HBox(secondCGCImageView);
        Platform.runLater(() -> {
            secondImageContainer.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, this.borderWidths)));
        });
        VBox firstCGC = new VBox(firstLabel, firstImageContainer);
        VBox secondCGC = new VBox(secondLabel, secondImageContainer);
        VBox CGCVbox = new VBox();
        Platform.runLater(() -> {
            firstCGC.setSpacing(5);
            secondCGC.setSpacing(5);
            CGCVbox.setSpacing(5);
            CGCVbox.getChildren().addAll(firstCGC, secondCGC);
            CGCVbox.setLayoutX(this.boardRES + 50);
            CGCVbox.setLayoutY(this.shelfRES + 20);
            rootPane.getChildren().add(CGCVbox);
        });
    }

    /**
     * This method builds personal goal card's GUI
     */
    private void buildPersonalGoalCard() {
        Label pgcLabel = new Label("Personal Goal Card");
        pgcImageView = new ImageView();
        Platform.runLater(() -> {
            pgcLabel.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 12");
            pgcImageView.setFitWidth(this.pgcRES);
            pgcImageView.setPreserveRatio(true);
        });
        HBox pgcImageContainer = new HBox(pgcImageView);
        Platform.runLater(() -> {
            pgcImageContainer.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, this.borderWidths)));
        });
        VBox pgcVbox = new VBox();
        Platform.runLater(() -> {
            pgcVbox.getChildren().addAll(pgcLabel, pgcImageContainer);
            pgcVbox.setLayoutX(this.boardRES + this.cgcRESX + 70);
            pgcVbox.setLayoutY(this.shelfRES + 20);
            pgcVbox.setSpacing(5);
            rootPane.getChildren().add(pgcVbox);
        });
    }

    /**
     * This method builds cgc points cards' GUI
     */
    private void buildCommonGoalCardsPointCards() {
        HBox cmcPCHbox = new HBox();
        firstCGCPImageView = new ImageView();
        secondCGCPImageView = new ImageView();
        Platform.runLater(() -> {
            firstCGCPImageView.setImage(new Image("gui/sc/scoring_back_EMPTY.jpg"));
            secondCGCPImageView.setImage(new Image("gui/sc/scoring_back_EMPTY.jpg"));
            cmcPCHbox.setSpacing(10);
            firstCGCPImageView.setFitHeight(this.cgcPointsRES);
            firstCGCPImageView.setPreserveRatio(true);
            secondCGCPImageView.setFitHeight(this.cgcPointsRES);
            secondCGCPImageView.setPreserveRatio(true);
        });
        HBox CGCHbox = new HBox();
        Platform.runLater(() -> {
            CGCHbox.setAlignment(Pos.TOP_LEFT);
            CGCHbox.setSpacing(30);
        });
        Label firstLabel = new Label("Common Goal Cards points");
        Platform.runLater(() -> {
            firstLabel.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 12");
            CGCHbox.getChildren().addAll(firstCGCPImageView, secondCGCPImageView);
            cmcPCHbox.getChildren().addAll(firstLabel, CGCHbox);
            cmcPCHbox.setLayoutX(30);
            cmcPCHbox.setLayoutY(this.boardRES + 15);
            rootPane.getChildren().add(cmcPCHbox);
        });
    }

    /**
     * This method builds first CGC available points' GUI
     */
    private void buildFirstCGCAvailablePoints() {
        firstCGCAvailablePointsImageView = new ImageView();
        Platform.runLater(() -> {
            firstCGCAvailablePointsImageView.setFitWidth(this.scRES);
            firstCGCAvailablePointsImageView.setPreserveRatio(true);
            firstCGCAvailablePointsImageView.setLayoutX(655);
            firstCGCAvailablePointsImageView.setLayoutY(378);
            firstCGCAvailablePointsImageView.setRotate(-8);
            rootPane.getChildren().add(firstCGCAvailablePointsImageView);
        });
    }

    /**
     * This method builds second CGC available points' GUI
     */
    private void buildSecondCGCAvailablePoints() {
        secondCGCAvailablePointsImageView = new ImageView();
        Platform.runLater(() -> {
            secondCGCAvailablePointsImageView.setFitWidth(this.scRES);
            secondCGCAvailablePointsImageView.setPreserveRatio(true);
            secondCGCAvailablePointsImageView.setLayoutX(655);
            secondCGCAvailablePointsImageView.setLayoutY(515);
            secondCGCAvailablePointsImageView.setRotate(-8);
            rootPane.getChildren().add(secondCGCAvailablePointsImageView);
        });
    }

    /**
     * This method builds game button's GUI
     */
    private void buildGameButtons() {
        //Font buttonsFont = Font.font("Comic sans MS", FontWeight.BOLD, FontPosture.REGULAR, 20);
        Button exitButton = new Button();
        Platform.runLater(() -> {
            exitButton.setText("QUIT MATCH");
            exitButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 20");
            exitButton.setBackground(Background.EMPTY);
            exitButton.setTextFill(Color.BLACK);
            exitButton.setLayoutX(this.boardRES + 50 + this.shelfRES - 20);
            exitButton.setLayoutY(60);
            rootPane.getChildren().add(exitButton);
        });
        gameButtonsEvents(exitButton);
    }

    /**
     * This method builds nickname's GUI
     */
    private void buildNickname() {
        VBox nicknameVbox = new VBox();
        nicknameText = new Text();
        Platform.runLater(() -> {
            yourTurnText.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 20;");
            yourTurnText.setVisible(false);
            yourTurnText.setFill(Color.GREEN);
            nicknameVbox.setSpacing(5);
            nicknameVbox.setLayoutX(this.boardRES + 45 + this.shelfRES);
            nicknameVbox.setLayoutY(10);
            nicknameText.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 20");
            nicknameVbox.getChildren().addAll(nicknameText, yourTurnText);
            rootPane.getChildren().add(nicknameVbox);
        });
    }

    /**
     * This method builds shelf grid's GUI
     */
    private void buildShelfGrid() {
        double columnWidth = (this.shelfRES - 70) / 5 - 10;
        double columnHeight = this.shelfRES - 57;
        HBox shelfGridhbox = new HBox();
        Platform.runLater(() -> {
            shelfGridhbox.setLayoutX(this.boardRES + 86);
            shelfGridhbox.setLayoutY(30);
            shelfGridhbox.setMinWidth(this.shelfRES - 70);
            shelfGridhbox.setMinHeight(columnHeight);
            shelfGridhbox.setSpacing(11);
        });
        VBox shelfColumn0 = new VBox();
        Platform.runLater(() -> {
            shelfColumn0.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            shelfColumn0.setMinSize(columnWidth, columnHeight);
            shelfColumn0.setMaxSize(columnWidth, columnHeight);
            shelfColumn0.setAlignment(Pos.BOTTOM_CENTER);
            shelfColumn0.setSpacing(6.4);
        });
        shelfColumns.add(shelfColumn0);
        VBox shelfColumn1 = new VBox();
        Platform.runLater(() -> {
            shelfColumn1.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            shelfColumn1.setMinSize(columnWidth, columnHeight);
            shelfColumn1.setMaxSize(columnWidth, columnHeight);
            shelfColumn1.setAlignment(Pos.BOTTOM_CENTER);
            shelfColumn1.setSpacing(6.4);
        });
        shelfColumns.add(shelfColumn1);
        VBox shelfColumn2 = new VBox();
        Platform.runLater(() -> {
            shelfColumn2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            shelfColumn2.setMinSize(columnWidth, columnHeight);
            shelfColumn2.setMaxSize(columnWidth, columnHeight);
            shelfColumn2.setAlignment(Pos.BOTTOM_CENTER);
            shelfColumn2.setSpacing(6.4);
        });
        shelfColumns.add(shelfColumn2);
        VBox shelfColumn3 = new VBox();
        Platform.runLater(() -> {
            shelfColumn3.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            shelfColumn3.setMinSize(columnWidth, columnHeight);
            shelfColumn3.setMaxSize(columnWidth, columnHeight);
            shelfColumn3.setAlignment(Pos.BOTTOM_CENTER);
            shelfColumn3.setSpacing(6.4);
        });
        shelfColumns.add(shelfColumn3);
        VBox shelfColumn4 = new VBox();
        Platform.runLater(() -> {
            shelfColumn4.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            shelfColumn4.setMinSize(columnWidth, columnHeight);
            shelfColumn4.setMaxSize(columnWidth, columnHeight);
            shelfColumn4.setAlignment(Pos.BOTTOM_CENTER);
            shelfColumn4.setSpacing(6.4);
        });
        shelfColumns.add(shelfColumn4);
        Platform.runLater(() -> {
            shelfGridhbox.getChildren().addAll(shelfColumn0, shelfColumn1, shelfColumn2, shelfColumn3, shelfColumn4);
            rootPane.getChildren().add(shelfGridhbox);
        });
        shelfGridEvents(shelfColumn0, shelfColumn1, shelfColumn2, shelfColumn3, shelfColumn4);
        buildColumCells();
    }

    /**
     * This method builds board grid's GUI
     */
    private void buildBoardGrid() {
        double rowHeight = (this.boardRES - 47) / 9;
        HBox firstRowHbox = new HBox();
        Platform.runLater(() -> {
            firstRowHbox.setLayoutX(182);
            firstRowHbox.setLayoutY(29);
            firstRowHbox.setMinSize(100, rowHeight);
            firstRowHbox.setMaxSize(100, rowHeight);
            firstRowHbox.setSpacing(2);
        });
        boardRows.add(firstRowHbox);
        HBox secondRowHbox = new HBox();
        Platform.runLater(() -> {
            secondRowHbox.setLayoutX(181);
            secondRowHbox.setLayoutY(29 + rowHeight);
            secondRowHbox.setMinSize(151, rowHeight);
            secondRowHbox.setMaxSize(151, rowHeight);
            secondRowHbox.setSpacing(2);
        });
        boardRows.add(secondRowHbox);
        HBox thirdRowHbox = new HBox();
        Platform.runLater(() -> {
            thirdRowHbox.setLayoutX(129);
            thirdRowHbox.setLayoutY(29 + rowHeight * 2);
            thirdRowHbox.setMinSize(256, rowHeight);
            thirdRowHbox.setMaxSize(256, rowHeight);
            thirdRowHbox.setSpacing(2);
        });
        boardRows.add(thirdRowHbox);
        HBox fourthRowHbox = new HBox();
        Platform.runLater(() -> {
            fourthRowHbox.setLayoutX(77);
            fourthRowHbox.setLayoutY(29 + rowHeight * 3);
            fourthRowHbox.setMinSize(411, rowHeight);
            fourthRowHbox.setMaxSize(411, rowHeight);
            fourthRowHbox.setSpacing(2);
        });
        boardRows.add(fourthRowHbox);
        HBox fifthRowHbox = new HBox();
        Platform.runLater(() -> {
            fifthRowHbox.setLayoutX(25);
            fifthRowHbox.setLayoutY(29 + rowHeight * 4);
            fifthRowHbox.setMinSize(463, rowHeight);
            fifthRowHbox.setMaxSize(463, rowHeight);
            fifthRowHbox.setSpacing(2);
        });
        boardRows.add(fifthRowHbox);
        HBox sixthRowHbox = new HBox();
        Platform.runLater(() -> {
            sixthRowHbox.setLayoutX(25);
            sixthRowHbox.setLayoutY(29 + rowHeight * 5);
            sixthRowHbox.setMinSize(412, rowHeight);
            sixthRowHbox.setMaxSize(412, rowHeight);
            sixthRowHbox.setSpacing(2);
        });
        boardRows.add(sixthRowHbox);
        HBox seventhRowHbox = new HBox();
        Platform.runLater(() -> {
            seventhRowHbox.setLayoutX(129);
            seventhRowHbox.setLayoutY(29 + rowHeight * 6);
            seventhRowHbox.setMinSize(255, rowHeight);
            seventhRowHbox.setMaxSize(255, rowHeight);
            seventhRowHbox.setSpacing(2.3);
        });
        boardRows.add(seventhRowHbox);
        HBox eightRowHbox = new HBox();
        Platform.runLater(() -> {
            eightRowHbox.setLayoutX(181);
            eightRowHbox.setLayoutY(29 + rowHeight * 7);
            eightRowHbox.setMinSize(151, rowHeight);
            eightRowHbox.setMaxSize(151, rowHeight);
            eightRowHbox.setSpacing(2);
        });
        boardRows.add(eightRowHbox);
        HBox ninthRowHbox = new HBox();
        Platform.runLater(() -> {
            ninthRowHbox.setLayoutX(233);
            ninthRowHbox.setLayoutY(29 + rowHeight * 8);
            ninthRowHbox.setMinSize(98, rowHeight);
            ninthRowHbox.setMaxSize(98, rowHeight);
            ninthRowHbox.setSpacing(2);
        });
        boardRows.add(ninthRowHbox);
        Platform.runLater(() -> {
            rootPane.getChildren().addAll(firstRowHbox, secondRowHbox, thirdRowHbox, fourthRowHbox, fifthRowHbox, sixthRowHbox, seventhRowHbox, eightRowHbox, ninthRowHbox);
        });
        buildRowsCells();
    }

    /**
     * This method instantiates board cells' positions
     */
    private void buildBoardPositions() {
        ArrayList<Integer> pos = new ArrayList<>();
        for (int i = 3; i < 5; i++) {
            pos.add(i);
        }
        positions.add(pos);
        pos = new ArrayList<>();
        for (int i = 3; i < 6; i++) {
            pos.add(i);
        }
        positions.add(pos);
        pos = new ArrayList<>();
        for (int i = 2; i < 7; i++) {
            pos.add(i);
        }
        positions.add(pos);
        pos = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            pos.add(i);
        }
        positions.add(pos);
        pos = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            pos.add(i);
        }
        positions.add(pos);
        pos = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            pos.add(i);
        }
        positions.add(pos);
        pos = new ArrayList<>();
        for (int i = 2; i < 7; i++) {
            pos.add(i);
        }
        positions.add(pos);
        pos = new ArrayList<>();
        for (int i = 3; i < 6; i++) {
            pos.add(i);
        }
        positions.add(pos);
        pos = new ArrayList<>();
        for (int i = 4; i < 6; i++) {
            pos.add(i);
        }
        positions.add(pos);
    }

    /**
     * This method builds board's cells
     */
    private void buildRowsCells() {
        int nCells = 2;
        ArrayList<CellDataModel> cells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            switch (i) {
                case 0:
                case 8:
                    nCells = 2;
                    break;
                case 1:
                case 7:
                    nCells = 3;
                    break;
                case 2:
                case 6:
                    nCells = 5;
                    break;
                case 3:
                case 5:
                    nCells = 8;
                    break;
                case 4:
                    nCells = 9;
                    break;
            }
            for (int j = 0; j < nCells; j++) {
                ImageView cell = new ImageView();
                int finalI = i;
                Platform.runLater(() -> {
                    cell.setFitHeight(this.boardCellDim);
                    cell.setFitWidth(this.boardCellDim);
                    cell.setPreserveRatio(true);
                    this.boardRows.get(finalI).getChildren().add(cell);
                });
                CellDataModel model = new CellDataModel(i, positions.get(i).get(j), cell);
                boardCellEvents(model);
                cells.add(model);
            }
            boardRowsCells.add(cells);
            cells = new ArrayList<>();
        }
    }

    /**
     * This method builds shelf's cells
     */
    private void buildColumCells() {
        ArrayList<ImageView> cellsImageView = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                ImageView cell = new ImageView();
                int finalI = i;
                Platform.runLater(() -> {
                    cell.setFitHeight(this.shelfCellDim);
                    cell.setFitWidth(this.shelfCellDim);
                    cell.setPreserveRatio(true);
                    this.shelfColumns.get(finalI).getChildren().add(cell);
                });
                cellsImageView.add(cell);
            }
            shelfColumnsCells.add(cellsImageView);
            cellsImageView = new ArrayList<>();
        }
    }

    /**
     * This method defines board cell's events
     *
     * @param cell for which to define effects
     */
    private void boardCellEvents(CellDataModel cell) {
        int depth = (int) (this.boardRES - 47) / 9;
        DropShadow borderGlow = new DropShadow();
        Glow glow = new Glow();
        Platform.runLater(() -> {
            borderGlow.setOffsetX(0);
            borderGlow.setOffsetY(0);
            borderGlow.setColor(Color.DARKGREEN);
            borderGlow.setWidth(depth);
            borderGlow.setHeight(depth);
            glow.setInput(borderGlow);
            glow.setLevel(0.5);
        });
        cell.getCell().setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                if (isMyTurn) {
                    if (selectedCells.size() < 3 && !selectedCells.contains(cell) && cell.getCell().getImage() != null) {
                        selectedCells.add(cell);
                        Platform.runLater(() -> {
                            cell.getCell().setEffect(glow);
                        });
                    }
                } else {
                    errorMessage("Wait for your turn!");
                }
            } else if (mouseEvent.getButton() == MouseButton.SECONDARY || selectedCells.size() == 0) {
                if (isMyTurn) {
                    if (selectedCells.contains(cell)) {
                        selectedCells.remove(cell);
                        Platform.runLater(() -> {
                            cell.getCell().setEffect(null);
                        });
                    }
                } else {
                    errorMessage("Wait for your turn!");
                }
            }
        });
    }

    /**
     * This method defines shelf columns' events
     *
     * @param shelfColumn0 first column
     * @param shelfColumn1 second column
     * @param shelfColumn2 third column
     * @param shelfColumn3 fourth column
     * @param shelfColumn4 fifth column
     */
    private void shelfGridEvents(VBox shelfColumn0, VBox shelfColumn1, VBox shelfColumn2, VBox shelfColumn3, VBox shelfColumn4) {
        int depth = (int) (this.shelfRES - 70) / 5 - 10;
        DropShadow borderGlow = new DropShadow();
        Glow glow = new Glow();
        Platform.runLater(() -> {
            borderGlow.setOffsetX(0);
            borderGlow.setOffsetY(0);
            borderGlow.setColor(Color.DARKGREEN);
            borderGlow.setWidth(depth);
            borderGlow.setHeight(depth);
            glow.setInput(borderGlow);
            glow.setLevel(0.5);
        });
        //First column
        shelfColumn0.setOnMouseClicked(mouseEvent -> {
            //Platform.runLater(() -> {
            if (isMyTurn) {
                if (selectedCells.size() > 0) {
                    selectedColumn = 0;
                    for (int i = 0; i < selectedCells.size(); i++) {
                        x.add(i, selectedCells.get(i).getX());
                        y.add(i, selectedCells.get(i).getY());
                    }
                    allowMove.unlock();
                } else {
                    errorMessage("You must select at least one cell from the board!");
                }
            } else {
                errorMessage("Wait for your turn!");
            }
            //});
        });
        shelfColumn0.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn0.setEffect(glow);
            });
        });
        shelfColumn0.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn0.setEffect(null);
            });
        });
        //Second column
        shelfColumn1.setOnMouseClicked(mouseEvent -> {
            //Platform.runLater(() -> {
            if (isMyTurn) {
                if (selectedCells.size() > 0) {
                    selectedColumn = 1;
                    for (int i = 0; i < selectedCells.size(); i++) {
                        x.add(i, selectedCells.get(i).getX());
                        y.add(i, selectedCells.get(i).getY());
                    }
                    allowMove.unlock();
                } else {
                    errorMessage("You must select at least one cell from the board!");
                }
            } else {
                errorMessage("Wait for your turn!");
            }
            //});
        });
        shelfColumn1.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn1.setEffect(glow);
            });
        });
        shelfColumn1.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn1.setEffect(null);
            });
        });
        //Third column
        shelfColumn2.setOnMouseClicked(mouseEvent -> {
            //Platform.runLater(() -> {
            if (isMyTurn) {
                if (selectedCells.size() > 0) {
                    selectedColumn = 2;
                    for (int i = 0; i < selectedCells.size(); i++) {
                        x.add(i, selectedCells.get(i).getX());
                        y.add(i, selectedCells.get(i).getY());
                    }
                    allowMove.unlock();
                } else {
                    errorMessage("You must select at least one cell from the board!");
                }
            } else {
                errorMessage("Wait for your turn!");
            }
            //});
        });
        shelfColumn2.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn2.setEffect(glow);
            });
        });
        shelfColumn2.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn2.setEffect(null);
            });
        });
        //Fourth column
        shelfColumn3.setOnMouseClicked(mouseEvent -> {
            //Platform.runLater(() -> {
            if (isMyTurn) {
                if (selectedCells.size() > 0) {
                    selectedColumn = 3;
                    for (int i = 0; i < selectedCells.size(); i++) {
                        x.add(i, selectedCells.get(i).getX());
                        y.add(i, selectedCells.get(i).getY());
                    }
                    allowMove.unlock();
                } else {
                    errorMessage("You must select at least one cell from the board!");
                }
            } else {
                errorMessage("Wait for your turn!");
            }
            //});
        });
        shelfColumn3.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn3.setEffect(glow);
            });
        });
        shelfColumn3.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn3.setEffect(null);
            });
        });
        //Fifth column
        shelfColumn4.setOnMouseClicked(mouseEvent -> {
            //Platform.runLater(() -> {
            if (isMyTurn) {
                if (selectedCells.size() > 0) {
                    selectedColumn = 4;
                    for (int i = 0; i < selectedCells.size(); i++) {
                        x.add(i, selectedCells.get(i).getX());
                        y.add(i, selectedCells.get(i).getY());
                    }
                    allowMove.unlock();
                } else {
                    errorMessage("You must select at least one cell from the board!");
                }
            } else {
                errorMessage("Wait for your turn!");
            }
            //);
        });
        shelfColumn4.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn4.setEffect(glow);
            });
        });
        shelfColumn4.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                shelfColumn4.setEffect(null);
            });
        });
    }

    /**
     * This method defines game buttons' events
     *
     * @param exitButton button
     */
    private void gameButtonsEvents(Button exitButton) {
        exitButton.setOnMouseClicked(mouseEvent -> {
            System.exit(1);
        });
        exitButton.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                exitButton.setTextFill(Color.BROWN);
            });
        });
        exitButton.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                exitButton.setTextFill(Color.BLACK);
            });
        });
    }

    /**
     * This method builds game menu's GUI
     *
     * @param rootPane root AnchorPane
     */
    private void buildMenu(AnchorPane rootPane) {
        //BottomBanner
        Image bottomImage = new Image("gui/misc/banner.png");
        ImageView bottomImageView = new ImageView();
        Platform.runLater(() -> {
            bottomImageView.setImage(bottomImage);
            bottomImageView.setFitHeight(200);
            bottomImageView.setFitWidth(this.mainMenuRESX);
        });
        VBox bottomImageContainer = new VBox(bottomImageView);
        Platform.runLater(() -> {
            bottomImageContainer.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, this.borderWidths)));
            bottomImageContainer.setLayoutY(this.mainMenuRESY - 200);
            rootPane.getChildren().add(bottomImageContainer); //add to the main root
        });
        //LogoImage
        Image logoImage = new Image("gui/misc/Title.png");
        ImageView logoImageView = new ImageView();
        Platform.runLater(() -> {
            logoImageView.setImage(logoImage);
            logoImageView.setFitHeight(250);
            logoImageView.setFitWidth(700);
            logoImageView.setX(0);
            logoImageView.setY(0);
            rootPane.getChildren().add(logoImageView); //add to the main root
        });
        //PublisherImage
        HBox publisherHbox = new HBox();
        Label publisherLabel = new Label("A game by");
        Image publisherImage = new Image("gui/misc/Publisher.png");
        ImageView publisherImageView = new ImageView();
        Platform.runLater(() -> {
            publisherImageView.setImage(publisherImage);
            publisherLabel.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 20");
            publisherImageView.setFitHeight(150);
            publisherImageView.setFitWidth(150);
            publisherHbox.getChildren().addAll(publisherLabel, publisherImageView);
            publisherHbox.setLayoutX(this.mainMenuRESX - 250);
            publisherHbox.setLayoutY(10);
            rootPane.getChildren().add(publisherHbox); //add to the main root
        });
        //Buttons
        VBox buttonsVbox = new VBox();
        Button startGameButton = new Button();
        Platform.runLater(() -> {
            startGameButton.setText("Join or create a game");
            startGameButton.setTextFill(Color.BLACK);
            startGameButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 40");
            startGameButton.setAlignment(Pos.CENTER_LEFT);
            startGameButton.setBackground(Background.EMPTY);
        });
        Button creditsButton = new Button();
        Platform.runLater(() -> {
            creditsButton.setText("Credits");
            creditsButton.setTextFill(Color.BLACK);
            creditsButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 40");
            creditsButton.setAlignment(Pos.CENTER_LEFT);
            creditsButton.setBackground(Background.EMPTY);
        });
        Button closeGameButton = new Button();
        Platform.runLater(() -> {
            closeGameButton.setText("Close game");
            closeGameButton.setTextFill(Color.BLACK);
            closeGameButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 40");
            closeGameButton.setAlignment(Pos.CENTER_LEFT);
            closeGameButton.setBackground(Background.EMPTY);
        });
        Platform.runLater(() -> {
            buttonsVbox.getChildren().addAll(startGameButton, creditsButton, closeGameButton);
            buttonsVbox.setLayoutX(50);
            buttonsVbox.setLayoutY(230);
            rootPane.getChildren().add(buttonsVbox); //add to the main root
        });
        mainMenuButtonsEvents(startGameButton, creditsButton, closeGameButton);
    }

    /**
     * This method builds login's GUI
     *
     * @param rootPane root AnchorPane
     */
    private void buildLogin(AnchorPane rootPane) {
        //Game ID area
        HBox gameIDHbox = new HBox();
        Platform.runLater(() -> {
            gameIDHbox.setMaxWidth(loginRESX - 250);
            gameIDHbox.setMinWidth(loginRESX - 250);
            gameIDHbox.setSpacing(10);
            gameIDHbox.setLayoutX(150);
            gameIDHbox.setLayoutY(50);
        });
        TextArea gameIDTextArea = new TextArea();
        Platform.runLater(() -> {
            gameIDTextArea.setPrefColumnCount(15);
            gameIDTextArea.setMinWidth((double) loginRESX / 4);
            gameIDTextArea.setPrefHeight(0);
        });
        Label connectLabel = new Label("Game Identifier: ");
        Platform.runLater(() -> {
            connectLabel.setTextFill(Color.BLACK);
            connectLabel.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 25");
        });
        Button confirmIDButton = new Button();
        Platform.runLater(() -> {
            confirmIDButton.setText("Confirm ID");
            confirmIDButton.setBackground(Background.EMPTY);
            confirmIDButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 15");
            confirmIDButton.setTextFill(Color.BLACK);
            confirmIDButton.setLayoutX(120 + (double) loginRESX / 4);
            confirmIDButton.setLayoutY(50);
        });
        //Connect or start button
        Button connectOrStartButton = new Button();
        Platform.runLater(() -> {
            connectOrStartButton.setText("CONNECT / START A NEW GAME");
            connectOrStartButton.setBackground(Background.EMPTY);
            connectOrStartButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 20");
            connectOrStartButton.setTextFill(Color.BLACK);
            connectOrStartButton.setAlignment(Pos.CENTER);
            connectOrStartButton.setDisable(true);
            gameIDHbox.getChildren().addAll(connectLabel, gameIDTextArea, confirmIDButton);
        });
        //Player's nickname area
        HBox playerNickHbox = new HBox();
        Platform.runLater(() -> {
            playerNickHbox.setMinWidth(loginRESX - 250);
            playerNickHbox.setMaxWidth(loginRESX - 250);
            playerNickHbox.setLayoutX(209);
            playerNickHbox.setLayoutY(100);
            playerNickHbox.setSpacing(10);
        });
        TextArea playerNickTextArea = new TextArea();
        Platform.runLater(() -> {
            playerNickTextArea.setPrefColumnCount(15);
            playerNickTextArea.setMinWidth((double) loginRESX / 4);
            playerNickTextArea.setPrefHeight(0);
            playerNickTextArea.setDisable(true);
        });
        Label nicknameLabel = new Label("Nickname: ");
        Platform.runLater(() -> {
            nicknameLabel.setTextFill(Color.BLACK);
            nicknameLabel.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 25");
            playerNickHbox.getChildren().addAll(nicknameLabel, playerNickTextArea);
        });
        //Number of player for the new game
        HBox numPlayerHbox = new HBox();
        Platform.runLater(() -> {
            numPlayerHbox.setMinWidth(loginRESX - 250);
            numPlayerHbox.setMaxWidth(loginRESX - 250);
            numPlayerHbox.setSpacing(10);
            numPlayerHbox.setLayoutX(120);
            numPlayerHbox.setLayoutY(150);
        });
        numPlayer = new Text();
        Platform.runLater(() -> {
            numPlayer.setText("Number of players:");
            numPlayer.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 25");
            numPlayer.setTextAlignment(TextAlignment.LEFT);
            numPlayer.setVisible(false);
        });
        numPlayerTextArea = new TextArea();
        Platform.runLater(() -> {
            numPlayerTextArea.setPrefColumnCount(15);
            numPlayerTextArea.setMinWidth((double) loginRESX / 4);
            numPlayerTextArea.setPrefHeight(0);
            numPlayerTextArea.setVisible(false);
            numPlayerHbox.getChildren().addAll(numPlayer, numPlayerTextArea);
        });
        //Go back
        Button gobackButton = new Button();
        Platform.runLater(() -> {
            gobackButton.setText("GO BACK");
            gobackButton.setBackground(Background.EMPTY);
            gobackButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 20");
            gobackButton.setTextFill(Color.BLACK);
            gobackButton.setAlignment(Pos.CENTER);
        });
        VBox buttonVbox = new VBox(connectOrStartButton, gobackButton);
        Platform.runLater(() -> {
            buttonVbox.setMinWidth(loginRESX);
            buttonVbox.setSpacing(10);
            buttonVbox.setAlignment(Pos.CENTER);
            buttonVbox.setLayoutY(200);
        });
        Platform.runLater(() -> rootPane.getChildren().addAll(gameIDHbox, numPlayerHbox, playerNickHbox, buttonVbox));
        loginButtonEvent(connectOrStartButton, gobackButton, confirmIDButton, gameIDTextArea, playerNickTextArea, numPlayer);
    }

    /**
     * This method builds credits' GUI
     *
     * @param rootPane root AnchorPane
     */
    private void buildCredits(AnchorPane rootPane) {
        VBox creditsVbox = new VBox();
        Text creditsText = new Text();
        Button creditsButton = new Button();
        Platform.runLater(() -> {
            creditsText.setText(String.format("MyShelfie: a game by Cranio Creations%n%n" +
                    "AM34 Group - Accademic Year: 22/23%n%n" +
                    "Caravano Andrea %n Cancelliere Biagio %n Cavallo Alessandro %n Chiavacci Allegra"));
            creditsText.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 25");
            creditsText.setTextAlignment(TextAlignment.CENTER);
            creditsButton.setText("QUIT");
            creditsButton.setBackground(Background.EMPTY);
            creditsButton.setStyle("-fx-font-family: 'Roboto Slab', serif; -fx-font-size: 30");
            creditsButton.setTextFill(Color.BLACK);
            creditsButton.setAlignment(Pos.CENTER_LEFT);
            creditsVbox.setMinSize(loginRESX - 50, loginRESY - 50);
            creditsVbox.setMaxSize(loginRESX - 50, loginRESY - 50);
            creditsVbox.setLayoutX(50);
            creditsVbox.setLayoutX(50);
            creditsVbox.setAlignment(Pos.CENTER);
            creditsVbox.getChildren().addAll(creditsText, creditsButton);
            rootPane.getChildren().add(creditsVbox);
        });
        creditsButtonEvent(creditsButton);
    }

    private void creditsButtonEvent(Button creditsButton) {
        creditsButton.setOnMouseClicked(mouseEvent -> {
            setScene(Scenes.mainMenuScene);
        });
        creditsButton.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                creditsButton.setTextFill(Color.BROWN);
            });
        });
        creditsButton.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                creditsButton.setTextFill(Color.BLACK);
            });
        });
    }

    /**
     * This method defines login buttons' events
     *
     * @param connectOrStartButton button
     * @param gobackButton         button
     * @param gameIDTextArea       button
     * @param playerNickTextArea   button
     */
    private void loginButtonEvent(Button connectOrStartButton, Button gobackButton, Button confirmIDButton, TextArea gameIDTextArea, TextArea playerNickTextArea, Text numPlayer) {
        connectOrStartButton.setOnMouseClicked(mouseEvent -> {
            if (checkLoginInputs(playerNickTextArea.getText(), newMatch ? numPlayerTextArea.getText() : String.valueOf(4))) {
                Platform.runLater(() -> {
                    numOfPlayers = numPlayerTextArea.getText();
                    nickname = playerNickTextArea.getText();
                    allowLogin.unlock();
                });
            }
        });
        connectOrStartButton.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                connectOrStartButton.setTextFill(Color.BROWN);
            });
        });
        connectOrStartButton.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                connectOrStartButton.setTextFill(Color.BLACK);
            });
        });
        gobackButton.setOnMouseClicked(mouseEvent -> {
            Platform.runLater(() -> {
                playerNickTextArea.setText("");
                gameIDTextArea.setText("");
                numPlayerTextArea.setText("");
                numPlayerTextArea.setVisible(false);
                numPlayer.setVisible(false);
                gameIDTextArea.setDisable(false);
                connectOrStartButton.setDisable(true);
                playerNickTextArea.setDisable(true);
                confirmIDButton.setDisable(false);
                setNewMatch(false);
            });
            setScene(Scenes.mainMenuScene);
        });
        gobackButton.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                gobackButton.setTextFill(Color.BROWN);
            });
        });
        gobackButton.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                gobackButton.setTextFill(Color.BLACK);
            });
        });
        confirmIDButton.setOnMouseClicked(mouseEvent -> {
            if (checkLoginID(gameIDTextArea.getText())) {
                Platform.runLater(() -> {
                    gameID = gameIDTextArea.getText();
                    playerNickTextArea.setDisable(false);
                    connectOrStartButton.setDisable(false);
                    gameIDTextArea.setDisable(true);
                    confirmIDButton.setDisable(true);
                    allowIdentifier.unlock();
                });
            }
        });
        confirmIDButton.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                confirmIDButton.setTextFill(Color.BROWN);
            });
        });
        confirmIDButton.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                confirmIDButton.setTextFill(Color.BLACK);
            });
        });
    }

    /**
     * This method defines main menu buttons' events
     *
     * @param startGameButton button
     * @param creditsButton   button
     * @param closeGameButton button
     */
    private void mainMenuButtonsEvents(Button startGameButton, Button creditsButton, Button closeGameButton) {
        //StartGameButton events
        startGameButton.setOnMouseClicked(mouseEvent -> {
            setScene(Scenes.loginScene);
            allowExitMenu.unlock();
        });
        startGameButton.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                startGameButton.setTextFill(Color.BROWN);
            });
        });
        startGameButton.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                startGameButton.setTextFill(Color.BLACK);
            });
        });
        //CreditsButton events
        creditsButton.setOnMouseClicked(mouseEvent -> {
            Platform.runLater(() -> {
                setScene(Scenes.creditsScene);
            });
        });
        creditsButton.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                creditsButton.setTextFill(Color.BROWN);
            });
        });
        creditsButton.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                creditsButton.setTextFill(Color.BLACK);
            });
        });
        //CloseGameButtons
        closeGameButton.setOnMouseClicked(mouseEvent -> {
            mainStage.close();
            System.exit(1);
        });
        closeGameButton.setOnMouseMoved(mouseEvent -> {
            Platform.runLater(() -> {
                closeGameButton.setTextFill(Color.BROWN);
            });
        });
        closeGameButton.setOnMouseExited(mouseEvent -> {
            Platform.runLater(() -> {
                closeGameButton.setTextFill(Color.BLACK);
            });
        });
    }

    /**
     * This method makes login input's checks
     *
     * @param nickname     player's nickname
     * @param numOfPlayers game's number of players
     * @return true if OK, false if KO
     */
    private boolean checkLoginInputs(String nickname, String numOfPlayers) {
        int checkPlayers = 0;
        try {
            checkPlayers = Integer.parseInt(numOfPlayers);
            if (checkPlayers < 2 || checkPlayers > 4) {
                errorMessage("The number of players must be between 4 and 2 included");
                return false;
            }
        } catch (NumberFormatException e) {
            errorMessage("You have not inserted a number! Retry");
            return false;
        }
        if (nickname == null || !nickname.matches("^[a-zA-Z0-9_]+$") || nickname.length() < 3) {
            errorMessage("Note that both the game identifier and your nickname must be an alphanumeric string, of 3 or more letters");
            return false;
        }
        return true;
    }

    /**
     * This method makes identifier input's checks
     *
     * @param gameID game's identifier
     * @return true if OK, false if KO
     */
    private boolean checkLoginID(String gameID) {
        if (gameID == null || !gameID.matches("^[a-zA-Z0-9_]+$") || gameID.length() < 3) {
            errorMessage("Note that both the game identifier and your nickname must be an alphanumeric string, of 3 or more letters");
            return false;
        }
        return true;
    }
}