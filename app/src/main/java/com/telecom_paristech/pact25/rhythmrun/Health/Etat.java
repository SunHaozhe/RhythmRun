package com.telecom_paristech.pact25.rhythmrun.Health;

import com.telecom_paristech.pact25.rhythmrun.interfaces.Health.EtatReductInterface;

/**
 * Created by ClemSurfaceBook on 30/01/2017.
 */

public class Etat implements EtatReductInterface{
    private boolean fatigue;
    private boolean extenue;


    public boolean isFatigue() {
        return fatigue;
    }

    public void setFatigue(boolean fatigue) {
        this.fatigue = fatigue;
    }

    public boolean isExtenue() {
        return extenue;
    }

    public void setExtenue(boolean extenue) {
        this.extenue = extenue;
    }
}
