package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by asus on 20/04/2017.
 */

public class HeartBeatCoach {
    int age;
    int gender;
    int frequencyAtRest;
    int frequencyAfterRun;

    //frequencyAtRest is the value of HeartBeat measured when the user is resting, before the run or hours after it
    //frequencyAfterRun is the value of frequency measured one minut after the runner stops running

    public HeartBeatCoach(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.age = Integer.parseInt(prefs.getString("age", "21"));
        this.gender = genderConstant(prefs.getString("gender", "M"));
        this.frequencyAtRest = 65;
    }

    public int genderConstant(String gender){
        if (gender.equals("M")){
            return 220;
        }
        return 226;
    }

    //Age and gender are to be setteled from the share preferences or the data basis:
    public int getAge()
    {
        return age;
    }

    public int getGender()
    {
        return gender;
    }

    //FrequencyAtRest should be returned by the chest strap when the runner is at rest (anytime, not during the run)
    public int getFrequencyAtRest() {
        return frequencyAtRest;
    }

    //FrequencyAfterRun should be returned by the chest strap about 1 minute after the runner decides to stop
    public int getFrequencyAfterRun() {
        return frequencyAfterRun;
    }

    //HeartBeatFrequency should be returned by the chest strap during the run
    public int getHeartBeatFrequency()
    {
        return 140;
    }


    //Calculate the maximum theorical value of Heartbeat
    public int getFCM()
    {
        return gender-age;

    }

    //Calculate the FC : critical frequency :

    public float getFC()
    {
        return (float) (0.75*getFCM());

    }

    //Indicates whether the frequency of heart beat is good or not
    public boolean goodHeartBeatFrequency()
    {
        if (getHeartBeatFrequency()<getFC())
        {
            return true;
        }
        else
        {
            return false;

        }
    }

    //Returns a message to the runner according to the accuracy of his run
    public String messageDuringRun()
    {
        if (goodHeartBeatFrequency())
        {
            return "Good job, you're running in an accurate way !";
        }

        else
        {
            return "High heartBeat. You'd rather run a bit lower to enhance your endurance! ";

        }

    }

    //Says whether the rhythm at rest is good or not :
    public String messageOneMinuteAfterRun()
    {
        if(getFC()-getFrequencyAfterRun()<20)
        {
            return "Still in the normal performance. Pay greater efforts to be an athlete. Good luck!";
        }
        else
        {
            if(getFC()-getFrequencyAfterRun()<30)
            {
                return "Good runner, congratulations! You're about to be an athlete.";
            }
            else{
                return "Congratulations! You're an athlete.";
            }
        }
    }


}
