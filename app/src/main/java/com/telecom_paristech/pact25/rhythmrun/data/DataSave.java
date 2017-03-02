package com.example.raphaelattali.rythmrun.data;

import android.media.Image;

import com.example.raphaelattali.rythmrun.interfaces.data.DataSaveInterface;

/**
 * Created by ClemSurfaceBook on 30/01/2017.
 */

public class DataSave implements DataSaveInterface {
    @Override
    public void imageDataEncoder(String pictureName) {

    }

    @Override
    public void intDataEncoder(int dataNumber) {

    }

    @Override
    public Image imageDataDecoder(String fileName) {
        return null;
    }

    @Override
    public int intDataDecoder(String numberName) {
        return 0;
    }

    @Override
    public void setDataMax(int dataMax) {

    }

    @Override
    public void tempoDataSave(String songPath, double freq) {
        // TempoDataBase.addSongAndTempo(songPath, freq);
        //TODO : résoudre le problème avec addSongAndTempo -> souci de fonction déclarée static
    }

    @Override
    public double tempoData(String songPath) {
        return 0;
    }
}
