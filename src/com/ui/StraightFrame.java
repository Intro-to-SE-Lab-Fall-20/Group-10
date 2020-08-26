//todo comment this code @NathanCheshire

package com.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StraightFrame extends JFrame {
    private int width;
    private int height;
    private ImageIcon background;
    private DragLabel dl;
    private JLabel titleLabel;

    private Color themeColor = new Color(26, 32, 51);

    public StraightFrame(int width, int height, ImageIcon background) {
        this.width = width;
        this.height = height;
        this.background = background;
        setSize(new Dimension(width, height));

        setResizable(false);
        setUndecorated(true);
        setIconImage(new ImageIcon("src\\com\\io\\pictures\\StraightIcon.png").getImage());

        JLabel parentLabel = new JLabel();
        parentLabel.setBorder(new LineBorder(themeColor, 5, false));
        parentLabel.setIcon(background);
        setContentPane(parentLabel);

        dl = new DragLabel(width, 30, this);
        dl.setBounds(0, 0, width, 30);
        parentLabel.add(dl);
    }

    public StraightFrame(int width, int height) {
        BufferedImage im = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = im.createGraphics();
        g.setPaint(new Color(238,238,238));
        g.fillRect(0,0,1,1);

        this.width = width;
        this.height = height;
        this.background = new ImageIcon(im);
        setSize(new Dimension(width, height));

        setResizable(false);
        setUndecorated(true);
        setIconImage(new ImageIcon("src\\com\\io\\pictures\\StraightIcon.png").getImage());

        JLabel parentLabel = new JLabel();
        parentLabel.setBorder(new LineBorder(themeColor, 5, false));
        parentLabel.setIcon(background);
        setContentPane(parentLabel);

        dl = new DragLabel(width, 30, this);
        dl.setBounds(0, 0, width, 30);
        parentLabel.add(dl);
    }

    @Override
    public void setTitle(String title) {
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        titleLabel.setForeground(new Color(252, 251, 227)); //todo make this a util var
        titleLabel.setBounds(5,2,((int) Math.ceil(14 * title.length())),25);
        dl.add(titleLabel);
    }
}

