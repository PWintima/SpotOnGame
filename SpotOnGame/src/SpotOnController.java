/*

 Philip Awini
 1169595
 ESOF-2570 
 */

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SpotOnController  {

    @FXML
    private Label level,highScore,score;

    @FXML
    private Label CurrentLevelLabel,CurrrentScoreLabel,HighScoreLabel;


    @FXML
    private Circle GreenSpot1,GreenSpot2,GreenSpot3;

    

    @FXML
    private Circle Life1,Life2,Life3,Life4,Life5,Life6,Life7;

    


    @FXML
    private Circle RedSpot1,RedSpot2;


   

    @FXML
    private AnchorPane missedClickPane;

 
    
    private Timeline timeline;
    private MediaPlayer hitSoundPlayer;
    private MediaPlayer missSoundPlayer;
    private MediaPlayer disappearSoundPlayer;
    private int currentScore = 0;
    private int currentLevel = 1;
    private int lives = 7;
    private int highestScore;
    int hits = 0;
    
    private final int INITIAL_SPOT_SIZE = 60;
    private final int FINAL_SPOT_SIZE = 0;
    private final int SPOT_SPEED = 10000;
    private final int MISS_PENALTY = 15;

    private static final String SCORE_FILE = "highest_score.txt"; 
    
    @FXML
    void initialize() {
    	
    	timeline = new Timeline();
        

        // Initialize game elements
    	score.setText("0");
    	level.setText("1");
    	// Load the highest score from storage
        highestScore = loadHighestScore();
        highScore.setText(String.valueOf(highestScore));
    	
        // Load the hit sound file
        Media hitSound = new Media(getClass().getResource("Sounds/hit.mp3").toExternalForm());
        hitSoundPlayer = new MediaPlayer(hitSound);

        // Load the disappear sound file
        Media missSound = new Media(getClass().getResource("Sounds/miss.mp3").toExternalForm());
        missSoundPlayer = new MediaPlayer(missSound);
        
        Media dissapearSound = new Media(getClass().getResource("Sounds/miss.mp3").toExternalForm());
        disappearSoundPlayer = new MediaPlayer(dissapearSound);
        
        animateSpot(GreenSpot1);
        animateSpot(GreenSpot2);
        animateSpot(GreenSpot3);
        animateSpot(RedSpot1);
        animateSpot(RedSpot2);
    }
    
    @FXML
    void ClickedOnSpot(MouseEvent event) {// for handling the disappearance of the spots
        Circle circle = (Circle) event.getTarget();

        if (circle.getRadius() == INITIAL_SPOT_SIZE) {
            // Spot clicked 
            currentScore += 10 * currentLevel;
            hits++;
            score.setText(String.valueOf(currentScore));
            
            hitSoundPlayer.stop();
            hitSoundPlayer.play();

            // Check for level up
            if (hits % 10 == 0) {
                currentLevel++;
                level.setText(String.valueOf(currentLevel));
                
                // Check if life needs to be revealed
                switch (hits / 10) { 
                    case 1:
                        Life4.setVisible(true);
                        break;
                    case 2:
                        Life5.setVisible(true);
                        break;
                    case 3:
                        Life6.setVisible(true);
                        break;
                    case 4:
                        Life7.setVisible(true);
                        break;
                }
            }

            // Reappear the spot from a new random direction
            animateSpot(circle);

            // Update highest score
            if (currentScore > highestScore) {
                highestScore = currentScore;
                highScore.setText(String.valueOf(highestScore));
                saveHighestScore(highestScore);
            }

        } else {
            // Missed spot
            MissedSpot(event);
        }
    }

    
    private void animateSpot(Circle spot) {
        double startX = Math.random() * 600;
        double startY = Math.random() * 400;
        double endX = Math.random() * 600;
        double endY = Math.random() * 400;

        // Ensure end point is within bounds
        if (endX < 25) endX = 25;
        if (endX > 575) endX = 575;
        if (endY < 25) endY = 25;
        if (endY > 325) endY = 325;

        // Set initial size and position
        spot.setRadius(INITIAL_SPOT_SIZE);
        spot.setCenterX(startX);
        spot.setCenterY(startY);
        spot.setVisible(true);

        // Calculate duration based on level
        int animationDuration = SPOT_SPEED - (currentLevel * 100); // Increase speed by 100 milliseconds per level

        // Create animations
        PathTransition pathTransition = new PathTransition(Duration.millis(animationDuration), new Line(startX, startY, endX, endY), spot);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(animationDuration), spot);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.25);
        scaleTransition.setToY(0.25);

        ParallelTransition parallelTransition = new ParallelTransition(spot, pathTransition, scaleTransition);
        parallelTransition.setOnFinished(event -> {
            // Spot animation completed without being clicked
            if (spot.getRadius() == FINAL_SPOT_SIZE) {
                spot.setVisible(false);
                disappearSoundPlayer.stop();
                disappearSoundPlayer.play();
                lives--;
                updateLivesDisplay();
                animateSpot(new Circle());
                parallelTransition.play();
            }
        });

        // Start animations
        parallelTransition.play();
    }

    
   

    @FXML
    void MissedSpot(MouseEvent event) {
        if (event.getTarget() == missedClickPane) {
            // Decrease score for missing a spot
            currentScore -= MISS_PENALTY;
            score.setText(String.valueOf(currentScore));

            // Decrease lives
            lives--;
            updateLivesDisplay();
           
           

            // Play miss sound
            if (missSoundPlayer != null) {
                missSoundPlayer.stop();
                missSoundPlayer.play();
            }

            // Check if game should end
            if (lives < 0) {
                endGame();
            } 
            /*
         
             */
        }
    }

    
    private void updateLivesDisplay() {
    	
        switch (lives) {
         case 6:
             Life7.setVisible(false);
             break;
            
         case 5:
          Life6.setVisible(false);                
          break;
          
          case 4:
                Life5.setVisible(false);
                break;
                
            case 3:
                Life4.setVisible(false);                
                break;
                
            case 2:
             Life3.setVisible(false);
             break;
             
            case 1:
                Life2.setVisible(false);
                break;
                
            case 0:
                Life1.setVisible(false);                
                endGame();
                break;
        }
    }
 
    private void endGame() {
        // Stop timeline if running
        if (timeline != null) {
            timeline.stop();
        }

        // Hide all spots
        GreenSpot1.setVisible(false);
        GreenSpot2.setVisible(false);
        GreenSpot3.setVisible(false);
        RedSpot1.setVisible(false);
        RedSpot2.setVisible(false);

        // Create a dialog box for game over message and restart option
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Your final score is: " + currentScore + "\nYou reached level: " + currentLevel +
                    "\nHighest score: " + highestScore +
                    "\nNumber of spots clicked: " + hits +
                    "\nNumber of spots missed: " + (currentLevel * 10 - hits) +
                    "\n\nDo you want to restart the game?");

            // Add restart button
            ButtonType restartButtonType = new ButtonType("Restart");
            alert.getButtonTypes().setAll(restartButtonType, ButtonType.CANCEL);

            // Handle button actions
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == restartButtonType) {
                    restartGame();
                }
            });
        });
    }
    
    void updateLives(MouseEvent event) 
    {
        Circle circle = (Circle) event.getTarget();
        circle.setVisible(false);
     // Play disappear sound
        //lives--;
        updateLivesDisplay();

    }
    
    private void saveHighestScore(int highestScore) 
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORE_FILE))) 
        {
            writer.println(highestScore);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    private int loadHighestScore() 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) 
        {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line);
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
            // Handle the exception
        }
        return 0;
    }
    
    private void restartGame() {
        // Reset game parameters
        currentScore = 0;
        currentLevel = 1;
        lives = 3;
        hits = 0;

        // Update UI
        score.setText("0");
        level.setText("1");

        // Reset life circles visibility
        Life1.setVisible(true);
        Life2.setVisible(true);
        Life3.setVisible(true);
        Life4.setVisible(false);
        Life5.setVisible(false);
        Life6.setVisible(false);
        Life7.setVisible(false);

        // Restart animations
        animateSpot(GreenSpot1);
        animateSpot(GreenSpot2);
        animateSpot(GreenSpot3);
        animateSpot(RedSpot1);
        animateSpot(RedSpot2);
    }
}