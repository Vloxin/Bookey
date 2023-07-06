package com.example.bookey.Objects;

import androidx.annotation.NonNull;

public class Client {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String type;

    public Client(String firstName, String lastName, String email, String username, String password, String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public Client() {
        // empty constructor
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail(){
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    // tostring
    @NonNull
    @Override
    public String toString() {
        return "Client{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username=" + username +
                ", password=" + password +
                ", type=" + type +
                '}';
    }
}
