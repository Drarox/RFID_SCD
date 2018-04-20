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

import java.util.ArrayList;
import java.util.List;

import com.st.MB.FTMHeaderBuilder.MBFct;
import com.st.MB.FTMHeaderBuilder.MBcmd;

public class FTMUseCaseHostRequest extends FTMUseCaseGen implements MBTransferListener, MBTransferListenerDataReceived{
    // measurement
    protected long mUCStartTime_ms = 0;

    // Associated Threads
    protected FTMTaskHostRequest stask;
    // protocole
    protected FTMPTColHtoR sp;


    private List<MBTransferListenerHostRequest> listeners = new ArrayList<MBTransferListenerHostRequest>();
    private List<MBTransferListenerDataReceived> listenersDataReceived = new ArrayList<MBTransferListenerDataReceived>();

    public FTMUseCaseHostRequest() {
        // TODO Auto-generated constructor stub
        sp = new FTMPTColHtoR();
        // ========================
        sp.setCmdPayload(null);
        sp.setCheckProtocolDataEXchangeParameters(false, 0);

        stask = new FTMTaskHostRequest();
        stask.addListener((MBTransferListener) this);
        stask.addListener((MBTransferListenerDataReceived) this);
        stask.setProtocol(sp);
        stask.mNextTransferTask = null;

    }

    public long getEllapsedTime() {
        long ret = 0;
        if (stask != null) {
            long end = stask.mEndTime_ms;
            ret = end - stask.mStartTime_ms;
        }
        return ret;

    }

    public long getTotalBytesProcessed() {
        long ret = 0;
        if (stask != null) {
            ret = stask.mBytesSent;
        }
        return ret;
    }

    public byte[] getPayloadReceived() {
        byte[] ret = null;
        ret = this.sp.getPayload();
        return ret;
    }

    @Override
    public void endOfTransfert(int error) {
        // TODO Auto-generated method stub
        notifySomethingHappened(error);
    }


    public void addListener(MBTransferListenerHostRequest listener) {
        listeners.add(listener);
    }
    public void addListener(MBTransferListenerDataReceived listener) {
        listenersDataReceived.add(listener);
    }

    // callback started at the end of the transfert
    void notifySomethingHappened(int err) {

        for (MBTransferListenerHostRequest listener : listeners) {
            if (this.sp.mMBProtocolBuilderReceived != null) {
                listener.hostRequestAvailable(err, getPayloadReceived(), this.sp.mMBProtocolBuilderReceived.mHBFunctionCode, this.sp.mMBProtocolBuilderReceived.mHBCommandResponseCode);
            } else {
                listener.hostRequestAvailable(this.sp.MB_ERROR_PROTOCOL_LOST_FUNCTION, null, MBFct.MB_FCT_DUMMY, MBcmd.MB_CMD_DUMMY);
            }
        }
    }

    @Override
    public void dataReceived(long size, long expectedSize, long currentChunck, long expectedChunck) {
        for (MBTransferListenerDataReceived listener : listenersDataReceived) {
                listener.dataReceived(size,expectedSize , currentChunck, expectedChunck);
            }
    }

    public void execute() {
        mUCStartTime_ms = System.currentTimeMillis();
        stask.execute();
    }

}
