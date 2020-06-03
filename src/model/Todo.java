package model;

public class Todo extends Table {

    @Entity(type = "INTEGER", size = 32, primary = true)
    int id;

    @Entity(type = "VARCHAR", size = 50, isnull = false)
    String title;

    @Entity(type = "ENUM", size = 3, isnull = false)
    String type;

    @Entity(type="LONGTEXT", size = 1000, isnull = false)
    String content;

    public void setContent(String content) {
        this.content = content;
    }

    @ForeignKey(table = "User", attribute = "id")
    @Entity(type = "INTEGER", size = 32, isnull = false)
    int userId;

    @ForeignKey(table = "Category", attribute = "id")
    @Entity(type = "INTEGER", size = 32)
    int category;

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
