package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Main;
import model.Database;
import model.Todo;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Favorites implements Initializable {
    @FXML
    TableView<Todo> todosTableView;

    @FXML
    TableColumn<Todo, String> titleTblCol;

    @FXML
    TableColumn<Todo, String> typeTblCol;

    @FXML
    TableColumn<Todo, String> categoryTblCol;

    @FXML
    Button editBtn;

    @FXML
    Button deleteBtn;

    @FXML
    Button logoutBtn;

    @FXML
    Button backBtn;

    @FXML
    Label loggedUserLbl;

    ArrayList userFavorites = new ArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loggedUserLbl.setText(Login.loggedInUser.getFirstname() + " " + Login.loggedInUser.getLastname());

        this.titleTblCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        this.typeTblCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        this.categoryTblCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        try {
            // getting all favorites from database
            String sql = "SELECT * FROM Favorites WHERE userId = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setInt(1, Login.loggedInUser.getId());

            ResultSet rs = query.executeQuery();
            while(rs.next()){
                // storing favorites in array like todos
                Todo todo = (Todo) Todo.get(Todo.class, rs.getInt(2));
                userFavorites.add(todo);
            }

            // showing all favorites in table
            this.todosTableView.getItems().setAll(userFavorites);
        } catch (Exception e) {
            System.out.println("We failed to get any data");
        }
    }

    public void deleteTodo(ActionEvent ev) throws SQLException {
        // checking for selected item
        if(this.todosTableView.getSelectionModel().getSelectedItem() != null){
            // getting selected todo
            Todo deletingTodo = this.todosTableView.getSelectionModel().getSelectedItem();

            // deleting selected todo from favorites
            PreparedStatement stmt = Database.CONNECTION.prepareStatement("DELETE FROM Favorites WHERE todoId=?");
            stmt.setInt(1, deletingTodo.getId());
            stmt.executeUpdate();

            // removing selected todo from array
            userFavorites.remove(deletingTodo);

            // refreshing the table
            this.todosTableView.getSelectionModel().clearSelection();
            this.todosTableView.getItems().clear();
            this.todosTableView.getItems().addAll(userFavorites);
        }
    }

    public void editTodo(ActionEvent ev) throws IOException {
        // checking for selected todo
        if(this.todosTableView.getSelectionModel().getSelectedItem() != null){
            // storing selected todo
            Home.editingTodo = this.todosTableView.getSelectionModel().getSelectedItem();
            Home.chosenTodoType = Home.editingTodo.getType();

            // redirecting to proper editing view
            if(Home.chosenTodoType.equals("note") || Home.chosenTodoType.equals("task")){
                Main.showWindow(
                        getClass(),
                        "../view/Note.fxml",
                        "Edit todo", 600, 400
                );
            } else if(Home.chosenTodoType.equals("list")){
                Main.showWindow(
                        getClass(),
                        "../view/List.fxml",
                        "Edit todo", 630, 400
                );
            }
        }
    }

    public void goBack(ActionEvent ev) throws IOException {
        Main.showWindow(
                getClass(),
                "../view/Home.fxml",
                "Login to system", 600, 350
        );
    }

    public void logout(ActionEvent ev) throws IOException {
        Login.loggedInUser = null;

        Main.showWindow(
                getClass(),
                "../view/Login.fxml",
                "Login to system", 600, 215
        );
    }
}
