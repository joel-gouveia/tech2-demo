package tech2.demo.com.demo.model;

import java.util.List;


/**
 * Created by Joel on 27-Jan-16.
 */
public class Users {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String expectedCalories;
    private int numberMeals;
    private int permissions;
    private boolean active;
    private List<Meals> meals;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public List<Meals> getMeals() {
        return meals;
    }

    public void setMeals(List<Meals> meals) {
        this.meals = meals;
    }

    public String getExpectedCalories() {
        return expectedCalories;
    }

    public void setExpectedCalories(String expectedCalories) {
        this.expectedCalories = expectedCalories;
    }

    public int getNumberMeals() {
        return numberMeals;
    }

    public void setNumberMeals(int numberMeals) {
        this.numberMeals = numberMeals;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
