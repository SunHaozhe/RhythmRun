package com.telecom_paristech.pact25.rhythmrun.Android_activities;

/**
 * Created by asus on 20/04/2017.
 */

public class HeartBeatCoach {
    int age;
    int gender;

    public HeartBeatCoach(int age, int gender)
    {
        this.age = age;
        this.gender = gender;
    }

    public int getAge()
    {
        return 20;
    }

    public int getGender()
    {
        return 220;
    }

    //Calculate the maximum value of Heartbeat

    public int getFCM()
    {
        return gender-age;

    }

    //Calculate the FC : critical frequency :

    public float getFC()
    {
        return (float) (0.75*getFCM());

    }


}
