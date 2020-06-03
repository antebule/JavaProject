package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import main.Main;
import model.Category;
import model.Database;
import model.Todo;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class Note implements Initializable {
    @FXML
    TextField titleText;

    @FXML
    TextArea noteTextArea;

    @FXML
    TextField categoryNameTxt;

    @FXML
    ChoiceBox<String> categoryChoiceBox;

    @FXML
    Button saveBtn;

    @FXML
    Button cancelBtn;

    ObservableList<String> choiceItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // getting categories to show them like choice items
            String sql = "SELECT * FROM Category WHERE userId = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setInt(1, Login.loggedInUser.getId());
            ResultSet rs = query.executeQuery();

            while(rs.next()){
                choiceItems.add(rs.getString(2));
            }

            // setting existing categories
            categoryChoiceBox.setItems(choiceItems);
            // setting default category value
            categoryChoiceBox.setValue("Uncategorized");

            // checking for edit action in Home
            if(Home.editingTodo != null) {
                // getting editing todo from database to show it's data
                String sql2 = "SELECT * FROM Todo WHERE title = ? AND type = ? AND category = ? AND userId = ?";
                PreparedStatement query2 = Database.CONNECTION.prepareStatement(sql2);
                query2.setString(1, Home.editingTodo.getTitle());
                query2.setString(2, Home.editingTodo.getType());
                query2.setInt(3, Home.editingTodo.getCategory());
                query2.setInt(4, Login.loggedInUser.getId());
                ResultSet rs2 = query2.executeQuery();

                // showing data from editing todo
                if(rs2.next()){
                    titleText.setText(rs2.getString(2));
                    noteTextArea.setText(rs2.getString(3));
                }

                // getting and setting already chosen category for editing todo
//                String sql3 = "SELECT * FROM Category WHERE id = ? AND userId = ?";
//                PreparedStatement query3 = Database.CONNECTION.prepareStatement(sql3);
//                query3.setInt(1, Home.editingTodo.getCategory());
//                query3.setInt(2, Login.loggedInUser.getId());
//                ResultSet rs3 = query3.executeQuery();
//                if(rs3.next()){
//                    categoryChoiceBox.setValue(rs3.getString(2));
//                }
                // getting and setting already chosen category for editing todo
                Category editingCategory = (Category) Category.get(Category.class, rs2.getInt(6));
                categoryChoiceBox.setValue(editingCategory.getName());
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void saveNoteToDatabase(ActionEvent ev) throws Exception{
        // checking for entered data
        if(titleText.getText().equals("") && noteTextArea.getText().equals("")){
            System.out.println("Input some data to save it.");
        } else{
            // getting category from chosen category name
            String sql = "SELECT * FROM Category WHERE name = ? AND userId = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setString(1, categoryChoiceBox.getValue());
            query.setInt(2, Login.loggedInUser.getId());
            ResultSet rs = query.executeQuery();

            Todo todo;
            // checking for editing todo, if not than it's new todo
            if(Home.editingTodo != null){
                todo = Home.editingTodo;
            } else {
                todo = new Todo();
            }

            // setting all data
            todo.setTitle(titleText.getText());
            if(Home.chosenTodoType != null){
                todo.setType(Home.chosenTodoType);
            } else {
                todo.setType("note");
            }
            todo.setContent(noteTextArea.getText());
            todo.setUserId(Login.loggedInUser.getId());

            if(rs.next()){
                // setting category id by chosen category name
                todo.setCategory(rs.getInt(1));
            }

            if(Home.editingTodo != null){
                todo.update();
            } else {
                todo.save();
            }

            Main.showWindow(
                    getClass(),
                    "../view/Home.fxml",
                    "Welcome to Home page", 600, 350
            );

            // resetting editing action
            Home.editingTodo = null;
            Home.chosenTodoType = null;
        }
    }

    public void cancel(ActionEvent ev) throws IOException {
        Home.editingTodo = null;
        Home.chosenTodoType = null;
        Main.showWindow(
                getClass(),
                "../view/Home.fxml",
                "Welcome to Home page", 600, 350
        );
    }

    public void addNewCategory(ActionEvent ev) throws Exception {
        if(Category.addNewCategory(this.categoryNameTxt.getText())){
            // adding new category to choiceBox
            choiceItems.add(this.categoryNameTxt.getText());

            // clearing TextField after adding new category
            this.categoryNameTxt.setText("");
        }
    }
}
