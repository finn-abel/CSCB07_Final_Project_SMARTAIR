package com.example.cscb07_final_project_smartair.DataObjects;

public class triageData {
    public boolean lips;
    public boolean chest;
    public boolean speak;
    public String guidance;
    public String decision;
    public Float pef;
    public boolean escalation;
    public int rescue_attempts;

    public triageData(){

    }

    public triageData(String decision, String guidance,
                      boolean speak, boolean lips, boolean chest,
                      Float pef, boolean escalation, int rescue_attempts) {
        this.decision = decision;
        this.guidance = guidance;
        this.speak = speak;
        this.lips = lips;
        this.chest = chest;
        this.pef = pef;
        this.escalation=escalation;
        this.rescue_attempts = rescue_attempts;
    }
}
