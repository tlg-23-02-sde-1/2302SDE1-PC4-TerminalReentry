package com.team4.terminal_reentry.setup;

import java.util.List;
import java.util.Map;

public class NPC {
    private String name;
    private String nationality;
    private String pronoun;
    private String locationAtTimeOfMurder;
    private String activityAtTimeOfMurder;
    private String opinionOfVictim;
    private String otherTestimony;
    private boolean isMurderer;
    private Map<String, String> answers;

    NPC (String name, String nationality, String pronoun, boolean isMurderer, Map<String, String> answers) {
        this.nationality = nationality;
        this.pronoun = pronoun;
        setName(name);
        setMurderer(isMurderer);
        setAnswers(answers);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMurderer() {
        return isMurderer;
    }

    public void setMurderer(boolean murderer) {
        isMurderer = murderer;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }
}