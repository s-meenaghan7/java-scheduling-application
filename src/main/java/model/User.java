package model;

/**
 This class defines User objects.
 @author Sean
 */
public class User {

    private final int id;
    private final String username;
    private final String password;

    public static User user;

    public User(int id, String username, String password) {
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.username + " (ID: " + this.id + ")";
    }

}
