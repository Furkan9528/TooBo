package com.example.weatherapp.User;


public class User {

    public String fullName, city, email;
    public User(){
    }
    public User(String fullName, String city, String email){
        this.fullName = fullName;
        this.city = city;
        this.email = email;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
