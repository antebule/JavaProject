package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User extends Table {
    @Entity(type = "INTEGER", size = 32, primary = true)
    int id;

    @Entity(type="VARCHAR", size=50, isnull = false)
    String firstname;

    @Entity(type = "VARCHAR", size=50, isnull = false)
    String lastname;

    @Entity(type="VARCHAR", size = 150, isnull = false)
    String email;

    @Entity(type = "VARCHAR", size = 256, isnull = false)
    String password;

    @Entity(type = "ENUM", size = 2, isnull = false)
    String role;

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static User login(String email, String password) throws Exception{
        String sql = "SELECT id FROM User WHERE email=? AND password=?";
        PreparedStatement query = Database.CONNECTION.prepareStatement(sql);
        query.setString(1, email);
        query.setString(2, password);
        ResultSet rs = query.executeQuery();

        if(rs.next()){
            return (User) User.get(User.class, rs.getInt(1));
        } else {
            return null;
        }
    }
}
