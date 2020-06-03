package model;

import controller.Login;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Category extends Table {
    @Entity(type = "INTEGER", size = 32, primary = true)
    int id;

    @Entity(type = "VARCHAR", size = 50, isnull = false)
    String name;

    @ForeignKey(table="user", attribute = "id")
    @Entity(type="INTEGER", size = 32, isnull = false)
    int userId;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static boolean addNewCategory(String categoryName) throws Exception {
        if(categoryName.equals("")){
            System.out.println("Input category name to add it.");
            return false;
        } else {
            // checking for category with existing name
            String sql = "SELECT * FROM Category WHERE name = ? AND userId = ?";
            PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
            query.setString(1, categoryName);
            query.setInt(2, Login.loggedInUser.getId());
            ResultSet rs = query.executeQuery();
            if(rs.next()){
                System.out.println("Entered category name already exist!");
                return false;
            } else {
                // creating new category and setting category name
                Category newCategory = new Category();
                newCategory.setName(categoryName);
                newCategory.setUserId(Login.loggedInUser.getId());

                // storing new category to database;
                newCategory.save();
                System.out.println("New category added");
                return true;
            }
        }
    }

    public static Category getCategoryByName(String categoryName) throws Exception {
        String sql = "SELECT * FROM Category WHERE name = ? AND userId = ?";
        PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
        query.setString(1, categoryName);
        query.setInt(2, Login.loggedInUser.getId());
        ResultSet rs = query.executeQuery();
        if(rs.next()){
            Category category = (Category) Category.get(Category.class, (int) rs.getObject(1));
            return category;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
