import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Main extends Application {
    //stage to pass around to class that wish to add/remove stuff from it such as scenes or components
    static Stage primaryStage;

    //offsets used for window dragging
    private double xOffset = 0;
    private double yOffset = 0;

    //used for popups
    private static JFrame informFrame;

    //used for autolog in for developers
    public static boolean autoLoggedIn = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StraightShot");
        Main.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("IO/Logo.png")));

        //allow the primary stage to be movable
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
            primaryStage.setOpacity(0.8f);
        });

        root.setOnDragDone((event -> Main.primaryStage.setOpacity(1.0f)));
        root.setOnMouseReleased((event -> Main.primaryStage.setOpacity(1.0f)));

        primaryStage.show();

        //shutdown hook to remove tmp directory
        Runtime.getRuntime().addShutdownHook(new Thread(ViewController::clearLocalAttachments));
    }

    public static void startWorking(String message, int delay) {
        try {
            int width = 150;
            int height = 30;

            if (informFrame != null)
                informFrame.dispose();

            informFrame = new JFrame();
            informFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            informFrame.setTitle("Working in background");
            informFrame.setSize(width, height);
            informFrame.setUndecorated(true);
            informFrame.setBackground(new Color(226, 40, 102));

            JLabel consoleLabel = new JLabel();
            informFrame.setContentPane(consoleLabel);

            JLabel desc = new JLabel(message, SwingConstants.CENTER);
            desc.setForeground(Color.BLACK);
            desc.setFont(new Font("Segoe UI Black", Font.BOLD, 10));
            desc.setBounds(5, 5, width - 10, 20);

            consoleLabel.add(desc, SwingConstants.CENTER);
            consoleLabel.setBorder(new LineBorder(Color.BLACK,2,false));
            informFrame.setVisible(true);
            informFrame.setLocation((int) (primaryStage.getX() + primaryStage.getWidth() / 2.0 - informFrame.getWidth() / 2.0),(int) primaryStage.getY() + 20);

            informFrame.setAlwaysOnTop(true);
            informFrame.setResizable(false);
            informFrame.setIconImage(new ImageIcon("src/IO/Logo.png").getImage());

            //pass in a zero to stay until next call here
            if (delay != 0) {
                Timer t = new Timer(delay, null);
                t.addActionListener(e -> {
                    informFrame.setVisible(false);
                    informFrame.dispose();

                });
                t.setRepeats(false);
                t.start();
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
