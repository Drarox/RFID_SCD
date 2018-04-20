package com.st.nfcv;


public class stnfcm24LRBasicOperation extends BasicOperation {

    private String TAG = "stnfcm24LRBasicOperation";

    public stnfcm24LRBasicOperation(int max_transceive_buffer) {
        // TODO Auto-generated constructor stub
        super(max_transceive_buffer);
        this.mMaxTransceiveBufferAvailableSize = max_transceive_buffer;
    }
}

