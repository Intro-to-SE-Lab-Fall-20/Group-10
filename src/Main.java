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
import java.awt.event.*;

public class Main extends Application {
    //stage to pass around to class that wish to add/remove stuff from it such as scenes or components
    static Stage primaryStage;

    //used for inform movement
    private static int xMouse;
    private static int yMouse;

    //offsets used for window dragging
    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StraightShot");
        this.primaryStage = primaryStage;
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
    }


    public static void startWorking() {
        try {
            String message = "Processing request";
            String title = "Working in background";
            int width = 150;
            int height = 100;

            JFrame informFrame = new JFrame();
            informFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            informFrame.setTitle(title);
            informFrame.setSize(width, height);
            informFrame.setUndecorated(true);
            informFrame.setBackground(new Color(52,70,84));

            JLabel consoleLabel = new JLabel();
            informFrame.setContentPane(consoleLabel);

            informFrame.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int x = e.getXOnScreen();
                    int y = e.getYOnScreen();

                    if (informFrame != null && informFrame.isFocused()) {
                        informFrame.setLocation(x - xMouse, y - yMouse);
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    xMouse = e.getX();
                    yMouse = e.getY();
                }
            });

            JLabel desc = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");

            desc.setHorizontalAlignment(JLabel.CENTER);
            desc.setVerticalAlignment(JLabel.CENTER);

            desc.setForeground(new Color(252, 251, 227));

            desc.setFont(new Font("Segoe UI Black", Font.BOLD, 12));

            desc.setBounds(15, 10, width - 20, 40);

            JLabel dismiss = new JLabel("Dismiss");

            dismiss.setHorizontalAlignment(JLabel.CENTER);
            dismiss.setVerticalAlignment(JLabel.CENTER);

            dismiss.setForeground(new Color(252, 251, 227));

            dismiss.setFont(new Font("Segoe UI Black", Font.BOLD, 15));

            dismiss.setBounds(10, height - 70, width - 20, height - 10);

            dismiss.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    informFrame.dispose();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    dismiss.setForeground(new Color(223,85,83));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    dismiss.setForeground(new Color(252, 251, 227));
                }
            });

            consoleLabel.add(desc);
            consoleLabel.add(dismiss);
            consoleLabel.setBorder(new LineBorder(new Color(26, 32, 51),2,false));
            informFrame.setVisible(true);
            informFrame.setLocationRelativeTo(null);

            informFrame.setAlwaysOnTop(true);
            informFrame.setResizable(false);
            informFrame.setIconImage(new ImageIcon("src/IO/Logo.png").getImage());

            Timer t = new Timer(3000, null);
            t.addActionListener(e -> {
                informFrame.setVisible(false);
                informFrame.dispose();

            });
            t.setRepeats(false);
            t.start();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
