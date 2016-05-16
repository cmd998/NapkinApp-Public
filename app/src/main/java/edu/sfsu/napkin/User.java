package edu.sfsu.napkin;

import java.util.ArrayList;

public class User {

    private ArrayList<String> allergies;

    public User() {
        allergies = new ArrayList<String>();
    }

    public User(String allergyList) {
        String[] temp = allergyList.split(",");

        allergies = new ArrayList<String>();
        for (int i = 0; i < temp.length; i++)
            allergies.add(temp[i]);
    }

    public User(ArrayList<String> allergies) {
        // Copy allergy list
        this.allergies = new ArrayList<String>();
        for (int i = 0; i < allergies.size(); i++)
            this.allergies.add(allergies.get(i));
    }

    public ArrayList<String> getAllergies() {
        return allergies;
    }
}
