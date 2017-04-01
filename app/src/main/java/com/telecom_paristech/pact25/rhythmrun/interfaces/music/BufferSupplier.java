package com.telecom_paristech.pact25.rhythmrun.interfaces.music;

/**
 * Created by lucas on 31/03/17.
 */

public interface BufferSupplier {
    float[] getNextBuffer();
    boolean songEnded();
}
