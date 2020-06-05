package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.Main;
import model.Database;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
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

    @FXML
    Label roleLbl;

    @FXML
    RadioButton userRadioBtn;

    @FXML
    RadioButton adminRadioBtn;

    public static User userEdit = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Login.loggedInUser;
        if(userEdit != null){
            user = userEdit;
            roleLbl.setVisible(true);
            adminRadioBtn.setVisible(true);
            userRadioBtn.setVisible(true);
            if(user.getRole().equals("admin")){
                adminRadioBtn.setSelected(true);
            } else {
                userRadioBtn.setSelected(true);
            }
        }
        firstnameTxt.setText(user.getFirstname());
        lastnameTxt.setText(user.getLastname());
        emailTxt.setText(user.getEmail());
        passwordTxt.setText(user.getPassword());
    }

    public void goToHome() throws IOException {
        userEdit = null;
        Main.showWindow(
                getClass(),
                "../view/Home.fxml",
                "Welcome to Home page", 600, 350
        );
    }

    public void logout() throws IOException {
        userEdit = null;
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
            User user = Login.loggedInUser;
            if(userEdit != null){
                user = userEdit;
                if(adminRadioBtn.isSelected()){
                    user.setRole("admin");
                } else {
                    user.setRole("user");
                }
            }

            user.setFirstname(firstnameTxt.getText());
            user.setLastname(lastnameTxt.getText());
            user.setEmail(emailTxt.getText());
            user.setPassword(passwordTxt.getText());
            user.update();
        }
    }

    public void deleteAccount() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting account");
        if(userEdit != null){
            alert.setHeaderText("Warning! You are about to delete user account!");
            alert.setContentText("1) If you choose 'Delete' you will permanently delete all of selected user todos together with favorites and categories! \n 2) You can choose 'Cancel' to cancel the process and nothing will happen.");
        } else {
            alert.setHeaderText("Warning! You are about to delete your account!");
            alert.setContentText("1) If you choose 'Delete' you will permanently delete all of your todos together with your favorites and categories! \n 2) You can choose 'Cancel' to cancel the process and nothing will happen.");
        }

        ButtonType buttonTypeDelete = new ButtonType("Delete");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeDelete, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == buttonTypeDelete){
            User deletingUser = Login.loggedInUser;

            if(userEdit != null){
                deletingUser = userEdit;
            }

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
            if(userEdit == null){
                Login.loggedInUser = null;
                Main.showWindow(
                        getClass(),
                        "../view/Login.fxml",
                        "Login to system", 400, 215
                );
            } else{
                Main.showWindow(
                        getClass(),
                        "../view/Admin.fxml",
                        "Welcome to Administration", 600, 400
                );
            }
            userEdit = null;
        }
    }
}
