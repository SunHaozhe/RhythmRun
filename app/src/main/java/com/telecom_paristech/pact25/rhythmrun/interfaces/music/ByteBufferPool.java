package com.telecom_paristech.pact25.rhythmrun.interfaces.music;

import java.nio.ByteBuffer;

/**
 * Created by lucas on 11/04/17.
 */

public interface ByteBufferPool { //pour retourner les buffers, pour ne pas en allouer constamment
    void returnBuffer(ByteBuffer buffer);
}
