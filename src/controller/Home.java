package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Main;
import model.Category;
import model.Database;
import model.Todo;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.ResourceBundle;

public class Home implements Initializable {
    public static String chosenTodoType;
    public static Todo editingTodo;

    @FXML
    RadioButton noteRadioButton;

    @FXML
    RadioButton listRadioButton;

    @FXML
    RadioButton taskRadioButton;

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
    Button addToFavoritesBtn;

    @FXML
    TextField categoryNameTxt;

    @FXML
    ComboBox optionsComboBox;

    public void add(ActionEvent ev) throws Exception{
        // cheching for selected radio button
        if(noteRadioButton.isSelected()){
            chosenTodoType = "note";
            Main.showWindow(
                    getClass(),
                    "../view/Note.fxml",
                    "Create your note", 600, 400
            );
        } else if(listRadioButton.isSelected()){
            chosenTodoType = "list";
            Main.showWindow(
                    getClass(),
                    "../view/List.fxml",
                    "Create new list", 630, 400
            );
        } else if(taskRadioButton.isSelected()){
            chosenTodoType = "task";
            Main.showWindow(
                    getClass(),
                    "../view/Note.fxml",
                    "Create task", 600, 400
            );
        } else{
            System.out.println("Select one radio button to add new todo");
        }
    }

    // storing all todos in collection
    Collection<Todo> allTodos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        optionsComboBox.getItems().addAll(
                "Profile",
                "Favorites",
                "Categories",
                "Logout"
        );
        if(Login.loggedInUser.getRole().equals("admin")){
            optionsComboBox.getItems().add(1, "Administration");
        }
        optionsComboBox.setPromptText(Login.loggedInUser.getFirstname() + " " + Login.loggedInUser.getLastname());
        optionsComboBox.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setOnMousePressed(e -> {
                if (!cell.isEmpty()) {
                    try {
                        optionClicked(cell.getItem());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            return cell ;
        });

        this.titleTblCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        this.typeTblCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        this.categoryTblCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        try {
            // getting all todos from database
            allTodos = (Collection<Todo>) Todo.list(Todo.class);

            // removes todos from other users
            allTodos.removeIf(todo -> todo.getUserId() != Login.loggedInUser.getId());

            // filling the table
            this.todosTableView.getItems().setAll(allTodos);

            // adding default category if it not exists
            Category defaultCategory = Category.getCategoryByName("Uncategorized");
            if(defaultCategory == null){
                Category.addNewCategory("Uncategorized");
            }
        } catch (Exception e) {
            System.out.println("Nismo uspjeli dohvatiti podatke");
        }
    }

    public void editTodo(ActionEvent ev) throws Exception{
        // checking for selected todo
        if(this.todosTableView.getSelectionModel().getSelectedItem() != null){
            editingTodo = this.todosTableView.getSelectionModel().getSelectedItem();
            chosenTodoType = editingTodo.getType();

            // opening appropriate window for different todo type
            if(chosenTodoType.equals("note") || chosenTodoType.equals("task")){
                Main.showWindow(
                        getClass(),
                        "../view/Note.fxml",
                        "Edit todo", 600, 400
                );
            } else if(chosenTodoType.equals("list")){
                Main.showWindow(
                        getClass(),
                        "../view/List.fxml",
                        "Edit todo", 630, 400
                );
            }
        }
    }

    public void deleteTodo(ActionEvent ev) throws Exception {
        // checking for selected todo
        if(this.todosTableView.getSelectionModel().getSelectedItem() != null){
            // getting selected todo
            Todo deletingTodo = this.todosTableView.getSelectionModel().getSelectedItem();

            // deleting from favorites
            String sql = "DELETE FROM Favorites WHERE todoId = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setInt(1, deletingTodo.getId());
            query.executeUpdate();

            // deleting from database
            deletingTodo.delete();

            // removing from collection
            allTodos.remove(deletingTodo);

            // refreshing the table
            this.todosTableView.getSelectionModel().clearSelection();
            this.todosTableView.getItems().clear();
            this.todosTableView.getItems().addAll(allTodos);
        } else {
            System.out.println("Select todo to delete it.");
        }
    }

    public void goToFavorites() throws IOException {
        Main.showWindow(
                getClass(),
                "../view/Favorites.fxml",
                "Favorites", 600, 400
        );
    }

    public void addToFavorites(ActionEvent ev) throws SQLException {
        // checking for selected todo
        if(this.todosTableView.getSelectionModel().getSelectedItem() != null){
            // getting selected todo from database
            String sql = "SELECT * FROM Favorites WHERE todoId = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setInt(1, this.todosTableView.getSelectionModel().getSelectedItem().getId());
            ResultSet rs = query.executeQuery();

            if(rs.next()){
                System.out.println("This todo already exist in favorites");
            } else {
                // adding selected todo to database if it doesn't already exist
                PreparedStatement stmt = Database.CONNECTION.prepareStatement("INSERT INTO Favorites (`id`, `todoId`, `userId`) VALUES (NULL, ?, ?)");
                stmt.setInt(1, this.todosTableView.getSelectionModel().getSelectedItem().getId());
                stmt.setInt(2, Login.loggedInUser.getId());
                stmt.executeUpdate();
            }
        } else {
            System.out.println("Select todo to add it to favorites.");
        }
    }

    public void goToAdministration() throws IOException {
        Main.showWindow(
                getClass(),
                "../view/Admin.fxml",
                "Welcome to Administration", 600, 400
        );
    }

    public void addNewCategory(ActionEvent ev) throws Exception {

        // checking input data
        if(this.categoryNameTxt.getText().equals("")){
            System.out.println("Input category name to add it.");
        } else if(Category.addNewCategory(this.categoryNameTxt.getText())){
                // clearing TextField after adding new category
                this.categoryNameTxt.setText("");
        }
    }

    public void goToCategories() throws IOException {
        Main.showWindow(
                getClass(),
                "../view/Categories.fxml",
                "Categories", 600, 400
        );
    }

    public void goToProfile() throws IOException {
        Main.showWindow(
                getClass(),
                "../view/Profile.fxml",
                "Your Profile", 600, 300
        );
    }

    public void optionClicked(String option) throws Exception {
        if(option.equals("Profile")){
            goToProfile();
        } else if(option.equals("Administration")){
            goToAdministration();
        } else if(option.equals("Favorites")){
            goToFavorites();
        } else if(option.equals("Categories")){
            goToCategories();
        } else if(option.equals("Logout")){
            logout();
        }
    }

    public void logout() throws IOException {
        Login.loggedInUser = null;
        Main.showWindow(
                getClass(),
                "../view/Login.fxml",
                "Login to system", 400, 215
        );
    }
}
