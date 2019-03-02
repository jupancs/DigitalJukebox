package edu.arizona.cs;

import java.util.ArrayList;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<String> songList;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getSongList() {
        return songList;
    }

    public User() {
    }

    public User(String firstName, String lastName, String email, ArrayList<String> songList) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.songList = songList;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSongList(ArrayList<String> songList) {
        this.songList = songList;
    }
}
