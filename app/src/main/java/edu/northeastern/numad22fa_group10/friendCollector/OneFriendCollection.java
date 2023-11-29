package edu.northeastern.numad22fa_group10.friendCollector;

public class OneFriendCollection {
    private String id;
    private String username;
    private String birthday;

    public OneFriendCollection(){

    }
    public OneFriendCollection(String id, String username, String birthday) {
        this.id = id;
        this.username = username;
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
