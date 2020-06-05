package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.Main;
import model.Database;
import model.User;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Register {
    @FXML
    TextField firstnameTxt;

    @FXML
    TextField lastnameTxt;

    @FXML
    TextField emailTxt;

    @FXML
    TextField passwordTxt;

    @FXML
    Label existingUserMsg;

    @FXML
    Label missingDataMsg;

    @FXML
    Label successMsg;

    public void backToLogin() throws IOException {
        Main.showWindow(
                getClass(),
                "../view/Login.fxml",
                "Login to system", 400, 215
        );
    }

    public void register() throws Exception {
        // resetting all messages before checking data
        existingUserMsg.setVisible(false);
        missingDataMsg.setVisible(false);
        successMsg.setVisible(false);

        // checking input fields
        if(firstnameTxt.getText().equals("") || lastnameTxt.getText().equals("") || emailTxt.getText().equals("") || passwordTxt.getText().equals("")){
            missingDataMsg.setVisible(true);
        } else {
            // checking database for already existing user
            String sql = "SELECT * FROM User WHERE firstname = ? AND lastname = ? AND email = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setString(1, firstnameTxt.getText());
            query.setString(2, lastnameTxt.getText());
            query.setString(3, emailTxt.getText());
            ResultSet rs = query.executeQuery();
            if(rs.next()){
                existingUserMsg.setVisible(true);
            } else {
                // creating new user
                User newUser = new User();
                newUser.setFirstname(firstnameTxt.getText());
                newUser.setLastname(lastnameTxt.getText());
                newUser.setEmail(emailTxt.getText());
                newUser.setPassword(passwordTxt.getText());
                newUser.setRole("user");
                newUser.save();

                // clearing Text fields
                firstnameTxt.setText("");
                lastnameTxt.setText("");
                emailTxt.setText("");
                passwordTxt.setText("");
                successMsg.setVisible(true);
            }
        }
    }
}
