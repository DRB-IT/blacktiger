package dk.drb.blacktiger.util;

import dk.apaq.peers.media.SoundManager;


public class SilentSoundManager implements SoundManager {

    @Override
    public void open() {
        
    }

    @Override
    public void close() {
        
    }

    @Override
    public byte[] readData() {
        return new byte[0];
    }

    @Override
    public int writeData(byte[] bytes, int offset, int length) {
        return length;
    }

}
