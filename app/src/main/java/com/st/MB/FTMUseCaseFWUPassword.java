package com.st.MB;

import java.util.ArrayList;
import java.util.List;


public class FTMUseCaseFWUPassword extends FTMUseCaseGen  implements MBTransferListener{
    private List<MBTransferListenerFWU> listeners = new ArrayList<MBTransferListenerFWU>();

    protected FTMPTColRtoHFWUReqResp sp;
    protected byte[] mPwd;

    public FTMUseCaseFWUPassword(FTMHeaderBuilder.MBFct fct, byte[] pwd ) {
        // TODO Auto-generated constructor stub
        setupTaskAndProtocol(fct);
        mPwd = pwd;
    }


    public FTMUseCaseFWUPassword(byte[] pwd  ) {
        // TODO Auto-generated constructor stub
        mPwd = pwd;
        setupTaskAndProtocol(FTMHeaderBuilder.MBFct.MB_FCT_FWU_PWD);
    }

    private void setupTaskAndProtocol(FTMHeaderBuilder.MBFct fct) {
        sp = new FTMPTColRtoHFWUReqResp(fct);

        // ========================
        sp.setCmdPayload(mPwd);
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
