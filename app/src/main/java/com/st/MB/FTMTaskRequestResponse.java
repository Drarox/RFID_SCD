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


import com.st.MB.FTMHeaderBuilder.MBcmd;
import com.st.MB.FTMPTColGeneric.ProtocolStateMachine;
import com.st.MB.FTMPTColGeneric.ProtocolStatus;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.nfcv.Helper;
import com.st.nfcv.MBCommandV;
import com.st.nfcv.NFCCommandVExtended;
import com.st.nfcv.SysFileLRHandler;
import com.st.util.DebugUtility;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Trace;
import android.util.Log;

public class FTMTaskRequestResponse extends FTMTaskGen {
    //genericMBTask stask2;
    FTMTaskGen mNextTransferTask;
//    long loop_before_going_out = 30; //1000000;
    static final boolean DBG = false;
    static final String TAG = "FTMTaskRequestResponse";
    static final String TAG_INSTRUMENTATION = "FTMTInstrumentation";

    protected boolean systemAndroidTraceMonitoring = true;
    int currentapiVersion = android.os.Build.VERSION.SDK_INT;

    public FTMTaskRequestResponse() {
        // TODO Auto-generated constructor stub
    }

    protected void logCmdDataWrittenToMB(byte[] data) {
        byte[] cmd;
        if (data.length > 13) {
            cmd = new byte[13];
            System.arraycopy(data, 0, cmd, 0, 13);
        } else {
            if (data.length > 5) {
                cmd = new byte[5];
                System.arraycopy(data, 0, cmd, 0, 5);
            } else {
                cmd = data;
            }
        }
        Log.v(this.getClass().getName(),
                "logCmdDataWrittenToMB cmd sent header: " + Helper.ConvertHexByteArrayToString(cmd));

    }

    //    class MBProcessTask extends genericMBTask {
    protected byte[] cmd = null;
    private int cpt = 0;
    protected byte MBConfigRegister;

    protected NFCCommandVExtended LRcmdExtended;
    protected SysFileLRHandler sysHDL = null;
    protected MBCommandV bop;

    boolean m_execution_result = true;

    // can use UI thread here
    @Override
    protected void onPreExecute() {
        if (DBG)
            Log.v(this.getClass().getName(), "onPreExecute started ... ");
        m_execution_result = false;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        if (currentTag != null) {
            if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                sysHDL = (SysFileLRHandler) currentTag.getSYSHandler();
                bop = new MBCommandV(sysHDL.getMaxTransceiveLength());
                LRcmdExtended = new NFCCommandVExtended(currentTag.getModel());
                m_execution_result = true;
            }
        }
        this.mStartTime_ms = System.currentTimeMillis();
        mBytesSent = 0;

        // New Implementation
        byte[] cmd = null;

        if (m_execution_result) {
            mProtocol.initProtocol(sysHDL.getMaxTransceiveLength());
        } else {
            // issue no need to go FW
            // m_background_Taskloop = false;
            m_execution_result = false;
            //mMBTaskExecutionError = this.MB_ERROR_TAG_SYS_PARAMETER;
            setMBTaskExecutionError(this.MB_ERROR_TAG_SYS_PARAMETER);

        }

        ProtocolStatus step = mProtocol.getCurrentProtocolStep();
        if (step == ProtocolStatus.MB_PTL_Request)
            cmd = mProtocol.processRequest(sysHDL, LRcmdExtended, ProtocolStateMachine.MB_PTL_SM_Current);
        if (cmd != null) {
            if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                if (bop.writeMBMsg(cmd) == 0) {
                    // ok
                    //logCmdDataWrittenToMB(cmd);
                    mBytesSent = mBytesSent + cmd.length;

                    if (DBG)
                        Log.d(TAG, "writeMBMsg: " + Helper.ConvertHexByteArrayToString(mProtocol.getMBGPRequestHeader()));

                } else {
                    // ko
                    byte[] answer = bop.getBlockAnswer();
                    if (answer != null)
                        this.setEndTaskNeeded(true);
                    m_execution_result = false;
                    //mMBTaskExecutionError = MB_ERROR_TAG_WRITE_FAILED;
                    setMBTaskExecutionError(this.MB_ERROR_TAG_WRITE_FAILED);

                }

            } else {
                m_execution_result = false;
                //mMBTaskExecutionError = this.MB_ERROR_TAG_SYS_PARAMETER;
                setMBTaskExecutionError(this.MB_ERROR_TAG_SYS_PARAMETER);
            }

        } else {
            // Need to go in a read from MB only
            // nothing to do .....
            // Just to process a message from Host.....
        }

    }


    // automatically done on worker thread (separate from UI thread)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        cpt = 0;
        int adjustSleepTime = 1;
        boolean writeIssueFlag;
        writeIssueFlag = false;
        // ..........................
        while (m_execution_result && cpt < mThreadingLoop && isEndTaskNeeded() == false) {
            synchronized (this) {

                if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                    Trace.beginSection("Pooling MBConfigRegister ");
                } else {
                    // do something for phones running an SDK before JELLY_BEAN
                    if (DebugUtility.printInstrumentation)
                        Log.v(TAG_INSTRUMENTATION, "Pooling_MBConfigRegister");
                }
                MBConfigRegister = getMBConfigRegister(bop);
                if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                    Trace.endSection();
                } else {
                    // do something for phones running an SDK before JELLY_BEAN
                    if (DebugUtility.printInstrumentation)
                        Log.v(TAG_INSTRUMENTATION, "End_Pooling_MBConfigRegister");
                }

                // Log.v(this.getClass().getName(), "getMBConfigRegister
                // process response : "
                // + Helper.ConvertHexByteToString(MBConfigRegister) + " cpt
                // = " + cpt);
            }
            // in case of read register issue.......
            // just try again ...
            if (MBConfigRegister == -1) {
                if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                    Trace.beginSection("MBConfigRegister ping ");
                } else {
                    // do something for phones running an SDK before JELLY_BEAN
                    if (DebugUtility.printInstrumentation)
                        Log.v(TAG_INSTRUMENTATION, "MBConfigRegister_ping");
                }

                try {
                    int loopCounter = 10;
                    boolean ping = true;
                    NFCApplication currentApp = NFCApplication.getApplication();
                    NFCTag currentTag = currentApp.getCurrentTag();
                    while ((ping = currentTag.pingTag()) != true && loopCounter > 0) {

                        try {
                            Thread.sleep(10);
                            loopCounter--;
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    Thread.sleep(mThreadWaitingTime);
                    cpt++;
                    Log.v(this.getClass().getName(), "doInBackground Read register failed..: "
                            + Helper.ConvertHexByteToString(MBConfigRegister) + "  cpt = " + cpt);

                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                    Trace.endSection();
                } else {
                    // do something for phones running an SDK before JELLY_BEAN
                    if (DebugUtility.printInstrumentation)
                        Log.v(TAG_INSTRUMENTATION, "End_MBConfigRegister_ping");
                }

            } else {
                boolean messavailable = isAHostMessageAvailable(MBConfigRegister);
                if (messavailable) {
                    // read length
                    byte mess_l = MBreadLength(bop);
                    int messlength = (mess_l & 0xFF) + 1;
                    if (messlength > 0) {
                        // read Message
                        if (bop.readMBMsg((byte) messlength) == 0) {
                            byte[] response = bop.getBlockAnswer();
                            if (DBG)
                                Log.d(TAG, "doInBackground message received: " + response.length);
                            mBytesSent = mBytesSent + response.length;
                            // New version
                            boolean decoding_err;
                            decoding_err = mProtocol.ProcessHostMessage(response);
                            long dataPayloadReceived = mProtocol.getTotalPayloadReceived();
                            long dataPayloadExpected = mProtocol.mMBProtocolBuilderReceived.mHBTotalChainingLenField;
                            long currentChunck = mProtocol.mMBProtocolBuilderReceived.mHBCurrentChunckField;
                            //long totalChunck = mProtocol.mMBProtocolBuilderReceived.mHBTotalChunckField;
                            long expectedChunck = mProtocol.mHTPayloadReceived.size();
                            if (decoding_err != true) {
                                // for notification
                                //currentChunck = -1;
                                if (DBG)
                                    Log.d(TAG, "doInBackground ProcessHostMessage issue mHBErrorCode Protocol= "
                                            + Helper.ConvertHexByteToString(mProtocol.getMBProtocolError()));
                            } else {
                                // do nothing
                            }
                            // inform listener about message received data
                            notifyDataPayloadReceived(dataPayloadReceived,dataPayloadExpected,currentChunck,expectedChunck);
                            // end of new implementation

                        }
                    } else {
                        // issue reading length
                        cpt++;
                        if (DBG)
                            Log.d(TAG, "doInBackground read length issue: " + messlength);
                    }
                } else {
                    boolean canwrite = isRFCanWrite(MBConfigRegister);
                    ProtocolStatus step = mProtocol.getCurrentProtocolStep();
                    // Patch to check if Host missed message / Interupt not handled
                    boolean hostMissMessage = isHostMissMessage(MBConfigRegister);
                    if (hostMissMessage == true) {
                        if (step == ProtocolStatus.MB_PTL_Request && writeIssueFlag == false) {
                            // notify among of data processed
                            if (mProtocol.cmdPayloadIndex > 0)
                                mProtocol.cmdPayloadIndex = mProtocol.cmdPayloadIndex - 1;
                            long datapayloadsent = mProtocol.cmdPayloadIndex * mProtocol.m_payloadChunckSize;
                            notifyDataPayloadSent(datapayloadsent);
                            writeIssueFlag = true;
                        }
                        if (DBG)
                            Log.d(TAG, "doInBackground Patch Host missed message : "
                                    + Helper.ConvertHexByteArrayToString(mProtocol.getMBGPRequestHeader()));
                        cpt++;
                        if (cpt >= mThreadingLoop) {
                            if (DBG)
                                Log.d(TAG, "doInBackground Patch Host missed mess issue : "
                                        + Helper.ConvertHexByteArrayToString(mProtocol.getMBGPRequestHeader()));
                            // mProtocol.getBackCurrentProtocolStep();
                            m_execution_result = false;
                            //mMBTaskExecutionError = MB_ERROR_TAG_WRITE_FAILED;
                            setMBTaskExecutionError(this.MB_ERROR_TAG_WRITE_FAILED);
                        }

                    }
                    if (canwrite == true) {

                        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                            Trace.beginSection("Cmd preparation ");
                        } else {
                            // do something for phones running an SDK before JELLY_BEAN
                            if (DebugUtility.printInstrumentation)
                                Log.v(TAG_INSTRUMENTATION, "Cmd_preparation ");
                        }


                        cmd = null;
                        if (step == ProtocolStatus.MB_PTL_Request) {
                            cmd = mProtocol.processRequest(sysHDL, LRcmdExtended,
                                    ProtocolStateMachine.MB_PTL_SM_Current);
                            // notify among of data processed
                            long datapayloadsent = mProtocol.cmdPayloadIndex * mProtocol.m_payloadChunckSize;

                            if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                                Trace.beginSection("callback datapayloadsent ");
                            } else {
                                // do something for phones running an SDK before JELLY_BEAN
                                if (DebugUtility.printInstrumentation)
                                    Log.v(TAG_INSTRUMENTATION, "callback_datapayloadsent ");
                            }
                            notifyDataPayloadSent(datapayloadsent);
                            if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                                Trace.endSection();
                            } else {
                                // do something for phones running an SDK before JELLY_BEAN
                                if (DebugUtility.printInstrumentation)
                                    Log.v(TAG_INSTRUMENTATION, "End_callback_datapayloadsent ");
                            }

                        }
                        if (cmd == null && step == ProtocolStatus.MB_PTL_Finished) {
                            m_execution_result = false;
                        }
                        if (cmd == null && (step == ProtocolStatus.MB_PTL_End || step == ProtocolStatus.MB_PTL_Acknowledge) && m_execution_result == true) {

                            if (step == ProtocolStatus.MB_PTL_Acknowledge)
                                cmd = mProtocol.processResponse(sysHDL, LRcmdExtended,
                                        ProtocolStateMachine.MB_PTL_SM_Current, MBcmd.MB_CMD_Response);
                            if (step == ProtocolStatus.MB_PTL_End)
                                cmd = mProtocol.processResponse(sysHDL, LRcmdExtended,
                                        ProtocolStateMachine.MB_PTL_SM_Current, MBcmd.MB_CMD_Acknowledge);
                        }

                        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                            Trace.endSection();
                        } else {
                            // do something for phones running an SDK before JELLY_BEAN
                            if (DebugUtility.printInstrumentation)
                                Log.v(TAG_INSTRUMENTATION, "End_Cmd_preparation ");
                        }

                        if (cmd != null) {
                            int writeResult = -1;
                            if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                                Trace.beginSection("writeMBMsg ");
                            } else {
                                // do something for phones running an SDK before JELLY_BEAN
                                if (DebugUtility.printInstrumentation)
                                    Log.v(TAG_INSTRUMENTATION, "writeMBMsg  ");
                            }

                            writeResult = bop.writeMBMsg(cmd);

                            if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2 && systemAndroidTraceMonitoring) {
                                Trace.endSection();
                            } else {
                                // do something for phones running an SDK before JELLY_BEAN
                                if (DebugUtility.printInstrumentation)
                                    Log.v(TAG_INSTRUMENTATION, "End_writeMBMsg  ");
                            }

                            if (writeResult == 0) {
                                //logCmdDataWrittenToMB(cmd);
                                mBytesSent = mBytesSent + cmd.length;
                                // ok
                                cpt = 0;
                                writeIssueFlag = false;
                                if (DBG)
                                    Log.d(TAG, "writeMBMsg: " + Helper.ConvertHexByteArrayToString(mProtocol.getMBGPRequestHeader()));

                                //Log.v(this.getClass().getName(),
                                //        "doInBackground message sent: " + cmd.length + "cpt = 0");
                                //if (cmd.length < this.mProtocol.mMBProtocolBuilder.mMBMaxHeaderChained)
                                //    Log.v(this.getClass().getName(),
                                //            "doInBackground message sent header: " + Helper.ConvertHexByteArrayToString(cmd));
/*                                if (DBG)
                                    Log.d(TAG, "doInBackground Sleep after write to enable host to read msg");
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException exception) {
                                    exception.printStackTrace();
                                }*/

                            } else {
                                if (writeResult == -1) {
                                    if (step == ProtocolStatus.MB_PTL_Request && writeIssueFlag == false) {
                                        // notify among of data processed
                                        if (mProtocol.cmdPayloadIndex > 0)
                                            mProtocol.cmdPayloadIndex = mProtocol.cmdPayloadIndex - 1;
                                        long datapayloadsent = mProtocol.cmdPayloadIndex * mProtocol.m_payloadChunckSize;
                                        notifyDataPayloadSent(datapayloadsent);
                                        writeIssueFlag = true;
                                    }
                                }
                                if (DBG)
                                    Log.d(TAG, "writeMBMsg issue: " + Helper.ConvertHexByteArrayToString(mProtocol.getMBGPRequestHeader()));
                                cpt++;
                                if (cpt >= mThreadingLoop) {
                                    if (DBG)
                                        Log.d(TAG, "writeMBMsg issue end PTCol: " + Helper.ConvertHexByteArrayToString(mProtocol.getMBGPRequestHeader()));
                                    // mProtocol.getBackCurrentProtocolStep();
                                    m_execution_result = false;
                                    //mMBTaskExecutionError = MB_ERROR_TAG_WRITE_FAILED;
                                    setMBTaskExecutionError(this.MB_ERROR_TAG_WRITE_FAILED);
                                }

                            }

                        } else {
                            // Need to go in a read from MB
                            cpt++;
                            if (DBG)
                                Log.d(TAG, "doInBackground read mode needed ... cmd == null and can write - sleep " + "  cpt = " + cpt + "Reg: " + Helper.ConvertHexByteToString(MBConfigRegister));
                            try {
                                Thread.sleep(mThreadWaitingTime);
                            } catch (InterruptedException exception) {
                                exception.printStackTrace();
                            }
                        }

                    } else {
                        if (cpt > 3) {

                            try {
                                if (DBG)
                                    Log.d(TAG, "doInBackground sleep mode needed - can't write - without cpt "
                                            + Helper.ConvertHexByteToString(MBConfigRegister) + "  cpt = " + cpt);
                                Thread.sleep(mThreadWaitingTime);
                            } catch (InterruptedException exception) {
                                exception.printStackTrace();
                            }
                        }
                        cpt++;
                        if (isHostMissMessage(MBConfigRegister)) {
                            cpt++;
                            if (DBG)
                                Log.d(TAG, "doInBackground H missed mess increment cpt: "
                                        + Helper.ConvertHexByteToString(MBConfigRegister) + "  cpt = " + cpt);
                        }

                    }


                }

            }
        }
        if (DBG)
            Log.d(TAG, "doInBackground ended : " + Helper.ConvertHexByteToString(MBConfigRegister) + "  cpt = " + cpt);

        return null;
    }

    protected byte getMBConfigRegister(MBCommandV mboperation) {
        byte ret = -1;
        try {
            if (mboperation.MBReadCfgBasicOp(false) == 0) {
                byte[] cfg = mboperation.getBlockAnswer();
                if (cfg.length >= 2) {
                    ret = cfg[1];
                }
                // ret = mboperation.getBlockAnswer()[1];
            } else {
                // ko
            }

        } catch (Exception ex) {
            ret = -1;
        }
        return ret;
    }

    protected boolean isRFCanWrite(byte mBConfigRegister) {
        boolean ret = true;
        if ((mBConfigRegister & 0x05) == 0x05 || (mBConfigRegister & 0x03) == 0X03
                || (mBConfigRegister & 0x01) == 0x00) {
            // no message
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    private boolean isHostMissMessage(byte MBConfigRegister) {
        boolean ret = false;
        if ((MBConfigRegister & 0x10) == 0) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    private boolean isAHostMessageAvailable(byte MBConfigRegister) {
        boolean ret = false;
        if ((MBConfigRegister & 0xC0) == 0) {
            // no message
        } else {
            if ((MBConfigRegister & 0xC0) == 0x40 && (MBConfigRegister & 0x02) == 0x02) {
                // I2C
                ret = true;
            } else if ((MBConfigRegister & 0xC0) == 0x80) {
                // RF
            } else {
                // issue .....??
            }
        }

        return ret;
    }

    private byte MBreadLength(MBCommandV mboperation) {
        byte ret = 0x00;
        try {
            if (mboperation.getMBMsgLength() == 0) {
                byte[] block = mboperation.getBlockAnswer();
                if (block.length >= 2) {
                    ret = block[1];
                }
                // ret = mboperation.getBlockAnswer()[1];
            } else {
                // ko
            }

        } catch (Exception ex) {
            Log.e(this.getClass().getName(), "MBreadLength Exception ...");

        }
        return ret;
    }

    // can use UI thread here
    @Override
    protected void onPostExecute(final Void unused) {
        if (DBG)
            Log.v(this.getClass().getName(), "onPostExecute started ... ");
        MBConfigRegister = getMBConfigRegister(bop);
        boolean canwrite = isRFCanWrite(MBConfigRegister);
        ProtocolStatus step = mProtocol.getCurrentProtocolStep();
        if (step != ProtocolStatus.MB_PTL_Finished && step !=null &&
                    (mProtocol.mMBProtocolFunction != FTMHeaderBuilder.MBFct.MB_FCT_SIMPLE || mProtocol.mMBProtocolFunction != FTMHeaderBuilder.MBFct.MB_FCT_DUMMY)) {
            Log.e(this.getClass().getName(), "onPostExecute Step1 protocol error ...");
            mProtocol.setProtocoltoTheEnd();
            mProtocol.setProtocolError((byte) 0x20);
            this.mEndTime_ms = System.currentTimeMillis();
            notifySomethingHappened(mProtocol.getMBProtocolError());

        } else {
            // check reported data
            // Prepare following steps
            if (mMBTaskExecutionError == 0 && this.mNextTransferTask != null) {
                // No error during protocol
                if (this.getProtocol().checkProtocolDataEXchange(this.mProtocol.checkdataExchange, this.getProtocol().lCRC)) {
                    mNextTransferTask.mProtocol.setProtocolError(this.mProtocol.MB_ERROR_PROTOCOL_NO_ERROR);
                } else {
                    mNextTransferTask.mProtocol.setProtocolError(this.mProtocol.MB_ERROR_PROTOCOL_CRC_ERROR);

                }
                mNextTransferTask.execute();
            } else {

                this.mEndTime_ms = System.currentTimeMillis();

                if (mProtocol.mMBProtocolBuilderReceived != null) {
                    notifySomethingHappened(mProtocol.getProtocolError() | mMBTaskExecutionError | mProtocol.mMBProtocolBuilderReceived.getHeaderError());
                } else {
                    notifySomethingHappened(mProtocol.getProtocolError() | mMBTaskExecutionError);
                }

            }
        }
        this.mEndTime_ms = System.currentTimeMillis();

    }
}


