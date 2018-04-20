/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1616 $
  * Revision of last commit    :  $Rev: 1616 $
  * Date of last commit     :  $Date: 2016-02-03 19:03:03 +0100 (Wed, 03 Feb 2016) $
  *
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2015 STMicroelectronics</center></h2>
  *
  * Licensed under ST MYLIBERTY SOFTWARE LICENSE AGREEMENT (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/myliberty
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
  * AND SPECIFICALLY DISCLAIMING THE IMPLIED WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
*/
package com.st.MB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.st.MB.FTMHeaderBuilder.MBFct;
import com.st.MB.FTMHeaderBuilder.MBcmd;
import com.st.util.crc;

import android.util.Log;

public class FTMUseCaseHostAnswerAcknowledge extends FTMUseCaseGen implements MBTransferListener {
    // payload
    protected int Datasize = 0;
    protected long m_CRC = 0;
    protected byte[] mpl = null;

    private boolean fullInit = false;
    // Associated Threads
    protected FTMTaskRequestResponse stask;
    // protocole
    //protected MBSimplePTColHtoR sp;

    public FTMUseCaseHostAnswerAcknowledge() {
        // TODO Auto-generated constructor stub
    }
    public FTMUseCaseHostAnswerAcknowledge(int size) {
        // TODO Auto-generated constructor stub
        this.Datasize = size;
        if (this.Datasize == 0)
            Datasize = 16;
        mpl = new byte[Datasize];
        int min = 0;
        int max = 255;
        Random r = new Random();
        for (int i =0;i<mpl.length;i++) mpl[i] = (byte) r.nextInt(max - min);
        try {
            m_CRC = crc.CRC(mpl);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(this.getClass().getName(), "CRC calculation issue ... ");
        }
        setupTaskAndProtocol();

    }
    public FTMUseCaseHostAnswerAcknowledge(MBFct fct, byte[] payload) {
        fullInit = true;
        // TODO Auto-generated constructor stub
        this.Datasize = payload.length;
        if (this.Datasize == 0)
            Datasize = 16;
        mpl = new byte[Datasize];
        System.arraycopy(payload,0,mpl,0,Datasize);
        try {
            m_CRC = crc.CRC(mpl);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(this.getClass().getName(), "CRC calculation issue ... ");
        }
        sp = new FTMPTcolHtoR_AAF(fct);
        setupTaskAndProtocol();
    }

    private void setupTaskAndProtocol() {
        if (fullInit == false) {
            // patch for older tests and tested features
            if (this.Datasize == 16) sp = new FTMPTcolHtoR_AAF(MBFct.MB_FCT_SIMPLE_From_HOST);
            if (this.Datasize == 512) sp = new FTMPTcolHtoR_AAF(MBFct.MB_FCT_SIMPLE_CHAINED_From_HOST);
        }
        // ========================
        sp.setCmdPayload(mpl);
        sp.setCheckProtocolDataEXchangeParameters(false, this.m_CRC);

        stask = new FTMTaskRequestResponse();
        stask.addListener(this);
        stask.setProtocol(sp);

        stask.mNextTransferTask = null;

    }


    @Override
    public void execute() {
        // TODO Auto-generated method stub
        // ================== Step 1 task
        // MBProcessTask stask = new MBProcessTask();

        mStartTime_ms = System.currentTimeMillis();
        stask.execute();
        // ================== Step 2 task2 configured in postExecute Step1

    }
    @Override
    public void endOfTransfert(int error) {
        // TODO Auto-generated method stub
        notifySomethingHappened(error);
    }


    private List<MBTransferListenerHostAcknowledge> listeners = new ArrayList<MBTransferListenerHostAcknowledge>();

    public void addListener(MBTransferListenerHostAcknowledge listener) {
        listeners.add(listener);
    }

    // callback started at the end of the transfert
    void notifySomethingHappened(int err) {

        for (MBTransferListenerHostAcknowledge listener : listeners) {
            if (this.sp.mMBProtocolBuilderReceived != null) {
                listener.hostAcknowledgeAvailable(err, getPayloadReceived(), this.sp.mMBProtocolBuilderReceived.mHBFunctionCode, this.sp.mMBProtocolBuilderReceived.mHBCommandResponseCode);
            } else {
                listener.hostAcknowledgeAvailable(this.sp.MB_ERROR_PROTOCOL_LOST_FUNCTION, null, MBFct.MB_FCT_DUMMY, MBcmd.MB_CMD_DUMMY);
            }
        }
    }


}
