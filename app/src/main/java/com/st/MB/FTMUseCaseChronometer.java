package com.st.MB;

import java.util.ArrayList;
import java.util.List;


public class FTMUseCaseChronometer extends FTMUseCaseGen  implements MBTransferListener{
    private List<MBTransferListenerFWU> listeners = new ArrayList<MBTransferListenerFWU>();

    protected FTMPTColRtoHReqWoResp sp;
    protected byte[] mdata;

    public FTMUseCaseChronometer(FTMHeaderBuilder.MBFct fct, byte[] data ) {
        // TODO Auto-generated constructor stub
        setupTaskAndProtocol(fct);
        mdata = data;
    }


    public FTMUseCaseChronometer(byte[] data  ) {
        // TODO Auto-generated constructor stub
        mdata = data;
        setupTaskAndProtocol(FTMHeaderBuilder.MBFct.MB_FCT_CHRONO);
    }

    private void setupTaskAndProtocol(FTMHeaderBuilder.MBFct fct) {
        sp = new FTMPTColRtoHReqWoResp(fct);

        // ========================
        sp.setCmdPayload(mdata);
        sp.setCheckProtocolDataEXchangeParameters(false, 0);

        stask = new FTMTaskRequestResponse();
        stask.addListener(this);

        stask.setProtocol(sp);

        stask.mNextTransferTask = null;

    }

    public void execute() {
        mStartTime_ms = System.currentTimeMillis();
        stask.execute();

    }


}
