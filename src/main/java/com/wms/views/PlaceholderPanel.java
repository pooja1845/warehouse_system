package com.wms.views;

import javax.swing.*;
import java.awt.*;

public class PlaceholderPanel extends JPanel {
    public PlaceholderPanel(String title) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        
        add(lblTitle, BorderLayout.CENTER);
    }
}
