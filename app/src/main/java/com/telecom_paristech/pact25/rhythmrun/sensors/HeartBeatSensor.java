package com.telecom_paristech.pact25.rhythmrun.sensors;

import com.telecom_paristech.pact25.rhythmrun.interfaces.sensors.HeartBeatSensorInterface;

/**
 * Created by ClemSurfaceBook on 26/01/2017.
 */

public class HeartBeatSensor implements HeartBeatSensorInterface {

    @Override
    public void connect() {

    }

    @Override
    public float getInstantHeartbeat() {
        return 0;
    }
}
