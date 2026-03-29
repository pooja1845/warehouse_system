package com.wms.controllers;

import com.wms.models.User;
import com.wms.utils.DatabaseConnection;
import com.wms.views.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        
        this.view.addLoginListener(new LoginListener());
        this.view.addExitListener(e -> System.exit(0));
    }

    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            String password = view.getPassword();

            if(username.isEmpty() || password.isEmpty()){
                view.displayErrorMessage("Please enter both username and password.");
                return;
            }

            try {
                Connection conn = DatabaseConnection.getConnection();
                if(conn == null) {
                    view.displayErrorMessage("Failed to connect to the database.");
                    return;
                }

                String query = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);

                ResultSet rs = pst.executeQuery();

                if(rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                    );

                    if("Admin".equalsIgnoreCase(user.getRole())) {
                        view.displaySuccessMessage("Login Successful");
                        view.dispose(); // Close login window
                        // TODO: Open Main Dashboard View and Controller here
                        MainFrameController main = new MainFrameController();
                        main.show();
                    } else {
                        view.displayErrorMessage("Only Admins can log into this system.");
                    }
                } else {
                    view.displayErrorMessage("Invalid username or password.");
                }

                rs.close();
                pst.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                view.displayErrorMessage("An error occurred during login.");
            }
        }
    }
}
