package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class LockRequestCommand implements Command {
    private final String command = "LOCK_REQUEST";
    @JsonRequired
    private String username;
    @JsonRequired
    private String secret;
    //change
    private String id;

    // public LockRequestCommand(String username, String secret) {
    //     this.setUsername(username);
    //     this.setSecret(secret);
    // }

    // change
    public LockRequestCommand(String username, String secret,String id) {
        this.setUsername(username);
        this.setSecret(secret);
        this.setId(id);
    }

    @Override
    public boolean equals(Object obj) {
        // return obj instanceof LockRequestCommand &&
        //     username.equals(((LockRequestCommand) obj).getUsername()) &&
        //     secret.equals(((LockRequestCommand) obj).getSecret());

        //change
        return obj instanceof LockRequestCommand &&
            username.equals(((LockRequestCommand) obj).getUsername()) &&
            secret.equals(((LockRequestCommand) obj).getSecret()) &&
            id.equals(((LockRequestCommand) obj).getId());
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //change
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
