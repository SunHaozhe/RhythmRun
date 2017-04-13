package com.telecom_paristech.pact25.rhythmrun.interfaces.music;

import java.nio.ByteBuffer;

/**
 * Created by lucas on 11/04/17.
 */

public interface ByteBufferSupplier {
    ByteBuffer getNextBuffer();
    boolean songEnded();
    void setRatio(float ratio);
}
