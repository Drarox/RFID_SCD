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

import android.os.AsyncTask;

public class FTMTaskGen extends AsyncTask<Void, Void, Void> {

    byte MB_ERROR_TAG_NO_ERROR = 0x00;
    byte MB_ERROR_TAG_SYS_PARAMETER = 0x50;
    byte MB_ERROR_TAG_WRITE_FAILED = 0x60;

    private List<MBTransferListener> listeners = new ArrayList<MBTransferListener>();

    protected long mThreadWaitingTime;
    protected int mThreadingLoop;
    byte mMBTaskExecutionError = MB_ERROR_TAG_NO_ERROR;

    private boolean mEndTaskNeeded = false;

    // measurement
    protected long mStartTime_ms;
    protected long mEndTime_ms;
    protected long mBytesSent;

    protected FTMPTColGeneric mProtocol;

    public FTMTaskGen() {
        // TODO Auto-generated constructor stub
        mEndTime_ms = 0;
        mStartTime_ms = 0;
        mBytesSent = 0;
        mThreadWaitingTime = 10;
        mThreadingLoop = 50;
    }


    // callback started at the end of the transfert
    //void notifySomethingHappened(boolean err){
    void notifySomethingHappened(int err) {
        for (MBTransferListener listener : listeners) {
            listener.endOfTransfert(err);
        }
    }

    public void addListener(MBTransferListener listener) {
        listeners.add(listener);
    }


    // for data progression
    public List<MBTransferListenerDataSent> listenersData = new ArrayList<MBTransferListenerDataSent>();
    public List<MBTransferListenerDataReceived> listenersDataReceive = new ArrayList<MBTransferListenerDataReceived>();

    public void addListenerDataSent(MBTransferListenerDataSent listener) {
        listenersData.add(listener);
    }
    public void addListener(MBTransferListenerDataReceived listener) {
        listenersDataReceive.add(listener);
    }

    // callback started at the end of the transfert
    void notifyDataPayloadSent(long size) {
        for (MBTransferListenerDataSent listeners : listenersData) {
            listeners.dataSent(size);
        }
    }
    // callback started when data received
    public void notifyDataPayloadReceived(long size, long expectedSize, long currentChunck, long expectedChunck) {
        for (MBTransferListenerDataReceived listeners : listenersDataReceive) {
            listeners.dataReceived(size,expectedSize,currentChunck,expectedChunck);
        }
    }

    public void setThreadingLoop(int mThreadingLoop) {
        this.mThreadingLoop = mThreadingLoop;
    }

    public void setThreadingTime(long threadingSleepTime) {
        this.mThreadWaitingTime = threadingSleepTime;
    }

    public void stopThreadingLoop(int m_Threading_loop) {
        this.mThreadingLoop = 0;
    }

    public void setMBTaskExecutionError(byte executionError) {
        mMBTaskExecutionError = executionError;

    }


    public FTMPTColGeneric getProtocol() {
        return mProtocol;
    }

    public void setProtocol(FTMPTColGeneric mProtocol) {
        this.mProtocol = mProtocol;
    }

    public boolean isEndTaskNeeded() {
        return mEndTaskNeeded;
    }

    public void setEndTaskNeeded(boolean mEndTaskNeeded) {
        this.mEndTaskNeeded = mEndTaskNeeded;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        return null;
    }

}


