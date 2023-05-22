package com.team4.terminal_reentry.setup;

import java.util.List;
import java.util.Map;

public class NPC {
    private String firstName;
    private String lastName;
    private String nationality;
    private String pronoun;
    private String locationAtTimeOfMurder;
    private String activityAtTimeOfMurder;
    private String opinionOfVictim;
    private String otherTestimony;
    private boolean isMurderer;
    private Map<String, String> answers;

    NPC (String firstName, String lastName, String nationality, String pronoun, boolean isMurderer, Map<String, String> answers) {
        this.nationality = nationality;
        this.pronoun = pronoun;
        setName(firstName, lastName);
        setMurderer(isMurderer);
        setAnswers(answers);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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