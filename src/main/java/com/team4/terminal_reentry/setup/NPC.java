package com.team4.terminal_reentry.setup;

import java.util.List;

class NPC {
    private String name;
    boolean isMurderer;
    private List<String> answers;

    NPC (String name, boolean isMurderer, List<String> answers) {
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

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}