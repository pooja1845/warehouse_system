package com.wms;

import com.wms.controllers.LoginController;
import com.wms.views.LoginView;

import javax.swing.*;

public class MainApplication {

    public static void main(String[] args) {
        // Ensure GUI runs on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set FlatLaf look and feel
                com.formdev.flatlaf.FlatLightLaf.setup();
                UIManager.put("Button.arc", 8);
                UIManager.put("Component.arc", 8);
                UIManager.put("ProgressBar.arc", 8);
                UIManager.put("TextComponent.arc", 8);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController(loginView);

            loginView.setVisible(true);
        });
    }
}
