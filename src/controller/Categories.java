package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.Main;
import model.Category;
import model.Todo;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;

public class Categories implements Initializable {
    @FXML
    TextField categoryNameTxt;

    @FXML
    ListView<Category> categoryListView;
    ObservableList<Category> listItems = FXCollections.observableArrayList();

    Collection<Category> categories;

    public void deleteCategory(ActionEvent ev) throws Exception {
        if (this.categoryListView.getSelectionModel().getSelectedItem() != null) {
            Category chosenCategory = this.categoryListView.getSelectionModel().getSelectedItem();
            if (chosenCategory.getName().equals("Uncategorized")) {
                System.out.println("You can't delete default category.");
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog with Custom Actions");
                alert.setHeaderText("Warning! You are about to delete category which will affect all todos with chosen category!!!");
                alert.setContentText("1) If you choose 'Delete' you will delete all todos with chosen category \n 2) If you choose 'Set Uncategorized' all todos with chosen category will become Uncategorized");

                ButtonType buttonTypeDelete = new ButtonType("Delete");
                ButtonType buttonTypeUncategorized = new ButtonType("Set Uncategoized");
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeDelete, buttonTypeUncategorized, buttonTypeCancel);
                Optional<ButtonType> result = alert.showAndWait();

                // checking selected button type
                if (result.get() == buttonTypeDelete) {
                    // ... user chose "Delete"

                    // getting all todos from database
                    Collection<Todo> allTodos = (Collection<Todo>) Todo.list(Todo.class);

                    // removing todos from other users
                    allTodos.removeIf(todo -> todo.getUserId() != Login.loggedInUser.getId());

                    Iterator<Todo> iterator = allTodos.iterator();

                    // deleting todos with chosen category
                    while (iterator.hasNext()) {
                        Todo deletingTodo = iterator.next();
                        if (deletingTodo.getCategory() == chosenCategory.getId()) {
                            deletingTodo.delete();
                        }
                    }

                    // removing category from list
                    this.listItems.remove(chosenCategory);
                    this.categoryListView.refresh();
                    chosenCategory.delete();
                } else if (result.get() == buttonTypeUncategorized) {
                    // ... user chose "Set Uncategorized"

                    // getting all todos from database
                    Collection<Todo> allTodos = (Collection<Todo>) Todo.list(Todo.class);

                    // removing todos from other users
                    allTodos.removeIf(todo -> todo.getUserId() != Login.loggedInUser.getId());

                    Iterator<Todo> iterator = allTodos.iterator();

                    // changing existing todos with deleting category to default category
                    while (iterator.hasNext()) {
                        Todo updatingTodo = iterator.next();
                        if (updatingTodo.getCategory() == chosenCategory.getId()) {
                            Category defaultCategory = Category.getCategoryByName("Uncategorized");
                            updatingTodo.setCategory(defaultCategory.getId());
                            updatingTodo.update();
                        }
                    }

                    this.listItems.remove(chosenCategory);
                    this.categoryListView.refresh();
                    chosenCategory.delete();
                } else {
                    // ... user chose CANCEL or closed the dialog
                    System.out.println("Deleting category canceled");
                }
            }
        } else{
            System.out.println("Select category to delete it.");
        }
    }

    public void addNewCategory(ActionEvent ev) throws Exception {
        if(Category.addNewCategory(this.categoryNameTxt.getText())){

            // getting added category from database and adding it to list
            Category addedCategory = Category.getCategoryByName(this.categoryNameTxt.getText());
            listItems.add(addedCategory);

            // clearing TextField after adding new category
            this.categoryNameTxt.setText("");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getCategories();
    }

    public void getCategories(){
        try {
            categories = (Collection<Category>) Category.list(Category.class);

            categories.removeIf(category -> category.getUserId() != Login.loggedInUser.getId());

            for(Category category : categories){
                this.listItems.add(category);
            }

            this.categoryListView.setItems(listItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToHome(ActionEvent ev) throws IOException {
        Main.showWindow(
                getClass(),
                "../view/Home.fxml",
                "Welcome to Home page", 600, 350
        );
    }

    public void logout(ActionEvent ev) throws IOException {
        Login.loggedInUser = null;

        Main.showWindow(
                getClass(),
                "../view/Login.fxml",
                "Login to system", 400, 215
        );
    }
}
