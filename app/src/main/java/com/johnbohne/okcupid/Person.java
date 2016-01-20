package com.johnbohne.okcupid;

/**
 * Created by john on 1/15/16.
 */
public class Person {
    private String userName;
    private String imageURL;
    private int age;
    private String city;
    private String state;
    private int percentage;

    public Person(String userName, String imageURL, int age, String city, String state, int percentage) {
        this.userName = userName;
        this.imageURL = imageURL;
        this.age = age;
        this.city = city;
        this.state = state;
        this.percentage = percentage;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPercentage() {
        return percentage;
    }
    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }


}
