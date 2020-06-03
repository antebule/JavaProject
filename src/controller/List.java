package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.Main;
import model.Category;
import model.Database;
import model.Todo;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class List implements Initializable {
    @FXML
    TextField titleText;

    @FXML
    TextField itemText;

    @FXML
    TextField categoryNameTxt;

    @FXML
    Button addToListBtn;

    @FXML
    Button cancelBtn;

    @FXML
    Button removeItemBtn;

    @FXML
    ListView<String> listItems;
    ObservableList<String> items = FXCollections.observableArrayList();

    @FXML
    ChoiceBox<String> categoryChoiceBox;

    // string for storing list in database
    String contentToStore = "";

    ObservableList<String> choiceItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // getting existing categories and showing them like choice items
            String sql = "SELECT * FROM Category WHERE userId = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setInt(1, Login.loggedInUser.getId());
            ResultSet rs = query.executeQuery();

            while(rs.next()){
                choiceItems.add(rs.getString(2));
            }

            // showing existing categories
            categoryChoiceBox.setItems(choiceItems);
            categoryChoiceBox.setValue("Uncategorized");

            // checking for editing action in Home
            if(Home.editingTodo != null) {
                // getting editing todo from database
                String sql2 = "SELECT * FROM Todo WHERE title = ? AND type = ? AND category = ? AND userId = ?";
                PreparedStatement query2 = Database.CONNECTION.prepareStatement(sql2);
                query2.setString(1, Home.editingTodo.getTitle());
                query2.setString(2, Home.editingTodo.getType());
                query2.setInt(3, Home.editingTodo.getCategory());
                query2.setInt(4, Login.loggedInUser.getId());
                ResultSet rs2 = query2.executeQuery();

                // showing already existing data from editing todo
                if(rs2.next()){
                    titleText.setText(rs2.getString(2));
                    String[] stringItems =  rs2.getString(3).split(", ");
                    for(String item : stringItems){
                        items.add(item);
                    }
                }

                Category editingCategory = (Category) Category.get(Category.class, rs2.getInt(6));
                categoryChoiceBox.setValue(editingCategory.getName());
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

//        listItems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listItems.setItems(items);
    }

    public void addToList(ActionEvent actionEvent) {
        // checking for some data to add to list
        if(!itemText.getText().equals("")){
            // adding input data to list
            items.add(itemText.getText());

            // clearing TextBox after storing
            itemText.setText("");

            // storing added item in string format for database storing
            contentToStore = String.join(", ", items);
        }
    }

    public void addListToDatabase(ActionEvent actionEvent) throws Exception {
        // checking for input data
        if(titleText.getText().equals("") && contentToStore.equals("")){
            System.out.println("Input some data to save it.");
        } else {
            // getting selected category from database
            String sql = "SELECT * FROM Category WHERE name = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setString(1, categoryChoiceBox.getValue());
            ResultSet rs = query.executeQuery();

            Todo todo;
            // making new todo if edit action is not active
            if(Home.editingTodo != null){
                todo = Home.editingTodo;
            } else {
                todo = new Todo();
            }

            // setting all data
            todo.setTitle(titleText.getText());
            todo.setType("list");
            todo.setUserId(Login.loggedInUser.getId());
            if(rs.next()){
                todo.setCategory(rs.getInt(1));
            }
            // storing list elements to database in string format
            ArrayList temp = new ArrayList(listItems.getItems());
            String content = String.join(", ", temp);
            todo.setContent(content);

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

    public void removeItem(ActionEvent ev){
        // checking for selected item
        if(listItems.getSelectionModel().getSelectedItem() != null){
            // removing selected item and refreshing the list
            items.remove(listItems.getSelectionModel().getSelectedItem());
            listItems.refresh();
        } else {
            System.out.println("Select item to remove it.");
        }
    }

    public void cancel(ActionEvent ev) throws IOException {
        // resetting editing action if active
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
