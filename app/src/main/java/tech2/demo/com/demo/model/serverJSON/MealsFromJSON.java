package tech2.demo.com.demo.model.serverJSON;

import java.util.ArrayList;

import tech2.demo.com.demo.model.Meals;

/**
 * Created by Joel on 17-Feb-16.
 */
public class MealsFromJSON {
    String id;
    ArrayList<Meals> meals;

    public ArrayList<Meals> getMeals() {
        return meals;
    }

    public void setMeals(ArrayList<Meals> meals) {
        this.meals = meals;
    }
}
