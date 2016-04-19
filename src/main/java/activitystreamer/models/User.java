package activitystreamer.models;

public class User {
    private String username;
    private String secret;

    public User(String username, String secret) throws Exception {
        if (username == null || secret == null) {
            throw new Exception("Username and secret must not be null.");
        }
        this.username = username;
        this.secret = secret;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User && username.equals(((User) o).username);
    }

    public String getUsername() {
        return username;
    }

    public String getSecret() {
        return secret;
    }
}
