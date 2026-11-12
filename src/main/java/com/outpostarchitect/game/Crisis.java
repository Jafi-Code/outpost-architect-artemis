package com.outpostarchitect.game;

import java.util.List;

public class Crisis {
    public String id;
    public int triggerDay;
    public String title;
    public String description;
    public String characterName;
    public String characterImage;
    public String scienceContext;
    public List<Decision> choices;

    public Crisis(String id, int triggerDay, String title, String description, 
                  String characterName, String characterImage, String scienceContext, List<Decision> choices) {
        this.id = id;
        this.triggerDay = triggerDay;
        this.title = title;
        this.description = description;
        this.characterName = characterName;
        this.characterImage = characterImage;
        this.scienceContext = scienceContext;
        this.choices = choices;
    }
}