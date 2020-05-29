package controller;

import javafx.scene.control.PasswordField;
import main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;

public class Login {
    public static User loggedInUser;

    @FXML
    Button loginBtn;

    @FXML
    TextField emailTxt;

    @FXML
    PasswordField passwordTxt;

    @FXML
    Label errorLbl;

    @FXML
    Label successLbl;

    @FXML
    public void login(ActionEvent ev){
        // getting data from TextFields
        String email = this.emailTxt.getText();
        String password = this.passwordTxt.getText();

        // checking data
        if (email.equals("") || password.equals("")){
            errorLbl.setVisible(true);
            successLbl.setVisible(false);
        } else {
            errorLbl.setVisible(false);
            successLbl.setVisible(true);

            try {
                // storing logged user
                loggedInUser = User.login(email, password);

                if(loggedInUser != null){
                    Main.showWindow(
                            getClass(),
                            "../view/Home.fxml",
                            "Welcome to Home page", 600, 350
                    );
                } else {
                    // showing proper message
                    errorLbl.setText("Enter correct user data");
                    errorLbl.setVisible(true);
                    successLbl.setVisible(false);
                }
            } catch (Exception e) {
                System.out.println("An error occurred " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
