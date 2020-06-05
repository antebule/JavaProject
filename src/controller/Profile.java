package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import main.Main;
import model.Database;
import model.Todo;
import model.User;
import sun.rmi.runtime.Log;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class Profile implements Initializable {
    @FXML
    TextField firstnameTxt;

    @FXML
    TextField lastnameTxt;

    @FXML
    TextField emailTxt;

    @FXML
    TextField passwordTxt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        firstnameTxt.setText(Login.loggedInUser.getFirstname());
        lastnameTxt.setText(Login.loggedInUser.getLastname());
        emailTxt.setText(Login.loggedInUser.getEmail());
        passwordTxt.setText(Login.loggedInUser.getPassword());
    }

    public void goToHome() throws IOException {
        Main.showWindow(
                getClass(),
                "../view/Home.fxml",
                "Welcome to Home page", 600, 350
        );
    }

    public void logout() throws IOException {
        Login.loggedInUser = null;
        Main.showWindow(
                getClass(),
                "../view/Login.fxml",
                "Login to system", 400, 215
        );
    }

    public void saveChanges() throws Exception {
        if(firstnameTxt.getText().equals("") || lastnameTxt.getText().equals("") || emailTxt.getText().equals("") || passwordTxt.getText().equals("")){
            System.out.println("Fill all input fields to save changes");
        } else {
            Login.loggedInUser.setFirstname(firstnameTxt.getText());
            Login.loggedInUser.setLastname(lastnameTxt.getText());
            Login.loggedInUser.setEmail(emailTxt.getText());
            Login.loggedInUser.setPassword(passwordTxt.getText());
            Login.loggedInUser.update();
        }
    }

    public void deleteAccount() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting account");
        alert.setHeaderText("Warning! You are about to delete your account!");
        alert.setContentText("1) If you choose 'Delete' you will permanently delete all of your todos together with your favorites and categories! \n 2) You can choose 'Cancel' to cancel the process and nothing will happen.");

        ButtonType buttonTypeDelete = new ButtonType("Delete");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeDelete, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == buttonTypeDelete){
            User deletingUser = Login.loggedInUser;

            // deleting all favorites of deleting user
            String sql = "DELETE FROM Favorites WHERE userId = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setInt(1, deletingUser.getId());
            query.executeUpdate();

            // deleting all todos of deleting user
            String sql3 = "DELETE FROM Todo WHERE userId = ?";
            PreparedStatement query3 = Database.CONNECTION.prepareStatement(sql3);
            query3.setInt(1, deletingUser.getId());
            query3.executeUpdate();

            // deleting all categories of deleting user
            String sql2 = "DELETE FROM Category WHERE userId = ?";
            PreparedStatement query2 = Database.CONNECTION.prepareStatement(sql2);
            query2.setInt(1, deletingUser.getId());
            query2.executeUpdate();

            // deleting user and logging out
            deletingUser.delete();
            Login.loggedInUser = null;
            Main.showWindow(
                    getClass(),
                    "../view/Login.fxml",
                    "Login to system", 400, 215
            );
        }
    }
}
