//this is some code I wrote for my person program called Cyder which is not a public repository yet
//kind of uneasy about making it public since i'm pretty proud of it but whatever it's here now
//todo comment this class @NathanCheshire

package com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DragLabel extends JLabel {
    private int restoreX;
    private int restoreY;
    private int width;
    private int height;
    private static JFrame effectFrame;

    private int xMouse;
    private int yMouse;

    //todo consolidate theme colors in a util resource
    private Color themeColor = new Color(26, 32, 51);

    ImageIcon mini1 = new ImageIcon("src\\com\\io\\pictures\\minimize1.png");
    ImageIcon mini2 = new ImageIcon("src\\com\\io\\pictures\\minimize2.png");

    ImageIcon close1 = new ImageIcon("src\\com\\io\\pictures\\Close1.png");
    ImageIcon close2 = new ImageIcon("src\\com\\io\\pictures\\Close2.png");

    private JButton close;
    private JButton minimize;

    public DragLabel(int w, int h, JFrame effectFrame) {
        this.width = w;
        this.height = h;

        new JLabel();
        setSize(width,height);
        setOpaque(true);
        setBackground(themeColor);
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();

                if (effectFrame != null && effectFrame.isFocused()) {
                    effectFrame.setLocation(x - xMouse, y - yMouse);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                xMouse = e.getX();
                yMouse = e.getY();
            }
        });

        effectFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeiconified(WindowEvent e) {
                effectFrame.setLocation(restoreX,restoreY);
                effectFrame.setVisible(true);
                effectFrame.requestFocus();
            }
        });

        close = new JButton("");

        close.setToolTipText("Close");

        close.addActionListener(e -> closeAnimation(effectFrame));

        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                close.setIcon(close2);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                close.setIcon(close1);
            }
        });

        close.setBounds(width - 26, 0, 22, 20);
        close.setIcon(close1);
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setFocusPainted(false);
        add(close);

        minimize = new JButton("");
        minimize.setToolTipText("Minimize");
        minimize.addActionListener(e -> {
            restoreX = effectFrame.getX();
            restoreY = effectFrame.getY();
            minimizeAnimation(effectFrame);
        });

        minimize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                minimize.setIcon(mini2);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimize.setIcon(mini1);
            }
        });

        minimize.setBounds(width - 52, 0, 22, 20);
        minimize.setIcon(mini1);
        minimize.setContentAreaFilled(false);
        minimize.setBorderPainted(false);
        minimize.setFocusPainted(false);
        add(minimize);
    }

    public int getRestoreX() {
        return this.restoreX;
    }

    public int getRestoreY() {
        return this.restoreY;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setColor(Color c) {this.setBackground(c);}

    public JFrame getEffectFrame() {
        return this.effectFrame;
    }

    public void closeAnimation(JFrame frame) {
        try {
            if (frame != null && frame.isVisible()) {
                Point point = frame.getLocationOnScreen();
                int x = (int) point.getX();
                int y = (int) point.getY();

                for (int i = y; i >= 0 - frame.getHeight(); i -= 15) {
                    Thread.sleep(1);
                    frame.setLocation(x, i);
                }

                frame.dispose();
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void minimizeAnimation(JFrame frame) {
        Point point = frame.getLocationOnScreen();
        int x = (int) point.getX();
        int y = (int) point.getY();

        try {
            for (int i = y; i <= Toolkit.getDefaultToolkit().getScreenSize().height; i += 15) {
                Thread.sleep(1);
                frame.setLocation(x, i);
            }

            frame.setState(JFrame.ICONIFIED);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
