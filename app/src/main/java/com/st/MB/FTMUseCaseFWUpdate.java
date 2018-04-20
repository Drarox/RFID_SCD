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
import com.st.util.crc;

import android.util.Log;

//public class MBFWUpdateUseCase  implements MBTransfertListener {
    public class FTMUseCaseFWUpdate  extends FTMUseCaseGen implements MBTransferListenerDataSent {


    // payload
    protected int FWsize = 0;
    protected long m_CRC = 0;
    protected byte[] mpl = null;

    protected FTMPTColRtoHFWUReqResp sp;
    protected FTMPTColRtoHFWUAck sp2;


/*    private MBGenericTaskRequestResponse stask;
    private MBGenericTaskAcknowledge stask2;

    private MBSimplePTColRtoHFWUReqResp sp;
    private MBSimplePTColRtoHFWUAck sp2;*/

    private List<MBFWUListenerDataSent> listenersFWUData = new ArrayList<MBFWUListenerDataSent>();

    public void addListenerFWUDataSent(MBFWUListenerDataSent listener) {
        listenersFWUData.add(listener);
    }

    // callback started at the end of the transfert

    @Override
    public void dataSent(long size) {
        // TODO Auto-generated method stub
        for (MBFWUListenerDataSent listeners : listenersFWUData) {
            listeners.FWUdataSent(size);
        }

    }

    public FTMUseCaseFWUpdate() {
        // TODO Auto-generated constructor stub
        if (this.FWsize == 0)
            FWsize = 300;
        mpl = new byte[FWsize];
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

        setupTaskAndProtocol(MBFct.MB_FCT_FILE_UPLOAD);

    }

    public FTMUseCaseFWUpdate(int size) {
        // TODO Auto-generated constructor stub
        FWsize = size;
        // showFileChooser();

        mpl = new byte[FWsize];
        int min = 0;
        int max = 255;
        Random r = new Random();
        for (int i =0;i<mpl.length;i++) mpl[i] = (byte) r.nextInt(max - min);
        //mpl[0] = 0x08;
        //mpl[FWsize - 1] = 0x09;
        try {
            m_CRC = crc.CRC(mpl);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(this.getClass().getName(), "CRC calculation issue ... ");
        }

        setupTaskAndProtocol(MBFct.MB_FCT_FILE_UPLOAD);
    }

    public FTMUseCaseFWUpdate(byte[] bufferFile, long m_CRC2,MBFct Fct) {
        // TODO Auto-generated constructor stub
        mpl = bufferFile;
        m_CRC = m_CRC2;
        setupTaskAndProtocol(Fct);
    }


    private void setupTaskAndProtocol(MBFct Fct) {

        sp = new FTMPTColRtoHFWUReqResp(Fct);
        sp2 = new FTMPTColRtoHFWUAck(Fct);
        // ================== Step 2
        sp2.setCmdPayload(null);
        sp2.setCheckProtocolDataEXchangeParameters(false, this.m_CRC);
        // ========================
        sp.setCmdPayload(mpl);
        sp.setCheckProtocolDataEXchangeParameters(true, this.m_CRC);

        stask = new FTMTaskRequestResponse();
        stask.addListener(this);
        stask.addListenerDataSent(this);
        stask2 = new FTMTaskAcknowledge();
        stask2.addListener(this);

        stask2.setProtocol(sp2);
        stask.setProtocol(sp);

        stask.mNextTransferTask = stask2;

    }

    public void execute() {
        // ================== Step 1 task
        // MBProcessTask stask = new MBProcessTask();

        mStartTime_ms = System.currentTimeMillis();
        stask.execute();
        // ================== Step 2 task2 configured in postExecute Step1
    }





}
