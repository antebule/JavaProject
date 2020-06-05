package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Main;
import model.Database;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.ResourceBundle;

public class Admin implements Initializable {
    @FXML
    TextField firstname;

    @FXML
    TextField lastname;

    @FXML
    TextField email;

    @FXML
    PasswordField password;

    @FXML
    RadioButton userRadioBtn;

    @FXML
    RadioButton adminRadioBtn;

    @FXML
    Button addUserBtn;

    @FXML
    TableView<User> usersTableView;

    @FXML
    TableColumn<User, String> firstNameTblCol;

    @FXML
    TableColumn<User, String> lastNameTblCol;

    @FXML
    TableColumn<User, String> emailTblCol;

    @FXML
    TableColumn<User, String> roleTblCol;

    Collection<User> users;

    {
        try {
            // storing all existing users from database
            users = (Collection<User>) User.list(User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addUserToDatabase(ActionEvent e) throws Exception{
        if(this.firstname.getText().equals("") || this.lastname.getText().equals("") || this.email.getText().equals("") || this.password.getText().equals("")){
            System.out.println("Popunite sva polja za dodavanje novog korisnika");
        } else {
            // creating new user with data from input fields
            User user = new User();
            user.setFirstname(this.firstname.getText());
            user.setLastname(this.lastname.getText());
            user.setEmail(this.email.getText());
            user.setPassword(this.password.getText());
            if(adminRadioBtn.isSelected()){
                user.setRole("admin");
            } else {
                user.setRole("user");
            }

            // adding user to database
            user.save();

            // adding user to TableView
            users.add(user);

            // refreshing the table after adding user
            this.usersTableView.getSelectionModel().clearSelection();
            this.usersTableView.getItems().clear();
            this.usersTableView.getItems().addAll(users);

            // deleting data from input fields after adding user to database
            this.firstname.setText("");
            this.lastname.setText("");
            this.email.setText("");
            this.password.setText("");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.firstNameTblCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        this.lastNameTblCol.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        this.emailTblCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        this.roleTblCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        try {
            // filling table with existing users
            this.usersTableView.getItems().setAll(users);
        } catch (Exception e) {
            System.out.println("We failed to get any data");
        }
    }

    public void goToHome(ActionEvent ev) throws IOException {
        Main.showWindow(
                getClass(),
                "../view/Home.fxml",
                "Welcome to Home page", 600, 350
        );
    }

    public void deleteUser(ActionEvent ev) throws Exception {
        if(this.usersTableView.getSelectionModel().getSelectedItem() != null){
            // getting selected user
            User deletingUser = this.usersTableView.getSelectionModel().getSelectedItem();

            if(deletingUser.getId() == Login.loggedInUser.getId()){
                System.out.println("You're about to delete yourself");
                // yet to implement in profile section
            } else {
                // deleting all todos and categories created by selected user
                String sql = "DELETE FROM Todo WHERE userId = ?";
                PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
                query.setInt(1, deletingUser.getId());
                query.executeUpdate();

                String sql2 = "DELETE FROM Category WHERE userId = ?";
                PreparedStatement query2 = Database.CONNECTION.prepareStatement(sql2);
                query2.setInt(1, deletingUser.getId());
                query2.executeUpdate();

                // deleting selected user from database
                deletingUser.delete();

                // removing user from collection of existing users
                users.remove(deletingUser);

                // refreshing the table after deleting user
                this.usersTableView.getSelectionModel().clearSelection();
                this.usersTableView.getItems().clear();
                this.usersTableView.getItems().addAll(users);
            }
        } else {
            System.out.println("Select user to delete it.");
        }
    }

    public void editUser() throws IOException {
        if(this.usersTableView.getSelectionModel().getSelectedItem() != null){
            Profile.userEdit = this.usersTableView.getSelectionModel().getSelectedItem();
            Main.showWindow(
                    getClass(),
                    "../view/Profile.fxml",
                    "Your Profile", 600, 300
            );
        }
    }
}
