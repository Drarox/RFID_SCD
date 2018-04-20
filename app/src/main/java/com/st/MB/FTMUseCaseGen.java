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

public abstract class FTMUseCaseGen implements MBTransferListener {
    // measurement
    protected long mStartTime_ms = 0;

    // Associated Threads
    protected FTMTaskRequestResponse stask;
    protected FTMTaskAcknowledge stask2;
    // protocole

    FTMPTColGeneric sp;

    public void setUCThreadingSleepTime(long threadingTime) {

        if (stask!=null) {
            stask.setThreadingTime(threadingTime);
/*            if (stask.mNextTransferTask != null) {
                stask.mNextTransferTask.setThreadingTime(threadingTime);
            }*/
        }

        if (stask2!=null) stask2.setThreadingTime(threadingTime);
    }


    public void setUCThreadingIterator(int m_threadingLoop) {

        if (stask!=null) stask.setThreadingLoop(m_threadingLoop);
        if (stask2!=null) stask2.setThreadingLoop(m_threadingLoop);
    }

    public void stopUCThreadingLoop(int m_threadingLoop) {

        if (stask!=null) stask.stopThreadingLoop(m_threadingLoop);
        if (stask2!=null) stask2.stopThreadingLoop(m_threadingLoop);
    }

    private List<MBTransferListenerFWU> listeners = new ArrayList<MBTransferListenerFWU>();

    public FTMUseCaseGen() {
        // TODO Auto-generated constructor stub
     }

    public long getEllapsedTime() {
        long ret = 0;
        if (stask != null && stask2 != null) {
            long end = (stask2.mEndTime_ms > stask.mEndTime_ms ? stask2.mEndTime_ms : stask.mEndTime_ms);
            ret = end - stask.mStartTime_ms;
        }
        return ret;

    }
    public long getTotalBytesProcessed() {
        long ret = 0;
        if (stask != null && stask2 != null) {
            ret = stask.mBytesSent + stask2.mBytesSent;
        }
        return ret;
    }

    public byte[] getPayloadReceived () {
        byte[] ret = null;
        if (this.sp != null) ret = this.sp.getPayload();
        return ret;
    }

    @Override
    public void endOfTransfert(int error) {
        // TODO Auto-generated method stub
        notifySomethingHappened(error);
    }


   public void addListener(MBTransferListenerFWU listener) {
        listeners.add(listener);
    }
    
    // callback started at the end of the transfert
    void notifySomethingHappened(int err){
        for(MBTransferListenerFWU listener : listeners){
            listener.endOfTransfer(err);
        }
    }
    
    public byte getErrorCode () {
        byte ret = 0;
        ret = this.stask.mProtocol.getMBProtocolError();
        return ret;
    }


    public abstract void execute();
}
