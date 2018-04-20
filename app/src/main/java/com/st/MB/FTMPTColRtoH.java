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

import com.st.MB.FTMHeaderBuilder.MBFct;
import com.st.MB.FTMHeaderBuilder.MBcmd;
import com.st.nfcv.Helper;
import com.st.nfcv.NFCCommandVExtended;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.NFCCommandVExtended.TypeVcmd;

import android.util.Log;

public class FTMPTColRtoH extends FTMPTColGeneric {
    static final boolean DBG = false;
    static final String TAG = "FTMPTColGeneric";

    public FTMPTColRtoH() {
        // TODO Auto-generated constructor stub
        super();
    }

    public FTMPTColRtoH(MBFct mbFctSimple) {
        // TODO Auto-generated constructor stub
        super(mbFctSimple);
    }

    // ====================================================================================
    public void initProtocol(int max_tranceive_data_available) {
        mProtocolStep = 0;
        if (mMBProtocolFunction == MBFct.MB_FCT_DUMMY) {
            mMBProtocolFunction = MBFct.MB_FCT_SIMPLE;
        }

        this.mMaxChunckForTranceiveData = (max_tranceive_data_available > this.mMaxChunckForTranceiveData
                ? this.mMaxChunckForTranceiveData : max_tranceive_data_available);

        mMBProtocolBuilder = new FTMHeaderBuilder(this.mMaxChunckForTranceiveData);
        this.mHTProtocolStepsDescr.put((byte) 0, ProtocolStatus.MB_PTL_Request);
        this.mHTProtocolStepsDescr.put((byte) 1, ProtocolStatus.MB_PTL_Answer);
        this.mHTProtocolStepsDescr.put((byte) 2, ProtocolStatus.MB_PTL_End);

        cmdPayloadIndex = 0;
    }

    public byte[] processRequest(SysFileLRHandler sysHDL, NFCCommandVExtended cmd_description, ProtocolStateMachine sm) {
        byte[] cmd = null;
        if (mProtocolStep == 0) {
            if (cmdPayloadIndex == 0) {
                // first iteration
                //Log.v(this.getClass().getName(), "processRequest : computeCmdPayload");
                int cmdlength = cmd_description.getCmdLength(TypeVcmd.TypeVcmd_MBWrite, sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested());
                computeCmdPayload(cmdlength,this.cmd_payload);
                cmd = mMBProtocolBuilder.cretateSimpleMessage(m_payloadChunckSize,this.cmd_payload, this.mMBProtocolFunction, MBcmd.MB_CMD_Command,this.mGeneralErrorCode);
                if (mHTCmdPayload.size() > 1) {
                    // need to chain cmd
                    // store header for next iteration = done in the mMBProtocolBuilder.cretateSimpleMessage method .....
                    if (DBG)
                        Log.d(TAG, "processRequest : computeCmdPayload with mHBChaningField content: " + cmdPayloadIndex +
                                "[" +  cmd.length + "]");
                    cmdPayloadIndex++;
                } else {
                    // no cmd mHBChaningField Go forward in protocol
                    if (DBG)
                        Log.d(TAG, "processRequest : computeCmdPayload with no mHBChaningField content  = Step++" );
                    mProtocolStep++;
                }
            } else {
                if (mHTCmdPayload.size() > 1 && cmdPayloadIndex < mHTCmdPayload.size()) {
                    // need to chain cmd
                    int offset = cmdPayloadIndex+1;
                    byte[] cur_payload = getCurrentCmdPayload();
                    cmd = mMBProtocolBuilder.setMessageChainingPayload(mMBProtocolBuilder.mHBCommandHeader, cur_payload,
                            offset);
                    if (cmdPayloadIndex < mHTCmdPayload.size()) {
                        if (DBG)
                            Log.d(TAG, "processRequest : computeCmdPayload with mHBChaningField content: " + offset +
                                    "[" +  cmd.length + "]");
                        cmdPayloadIndex++;
                        if (cmdPayloadIndex == mHTCmdPayload.size()) mProtocolStep++; // the end of data
                    } else {
                        if (DBG)
                            Log.d(TAG, "processRequest : computeCmdPayload with mHBChaningField content: " + offset +
                                    "[" +  cmd.length + "]" + " = Step++");
                        mProtocolStep++;
                    }
                } else {
                    // no cmd mHBChaningField Go forward in protocol
                    if (DBG)
                        Log.d(TAG, "processRequest : computeCmdPayload without mHBChaningField content: " + cmdPayloadIndex +
                                "[" +  "cmd.length=0 !!" + "]" + " = Step++");
                    mProtocolStep++;
                }

            }
        }
        return cmd;
    }

    public byte[] processResponse(SysFileLRHandler sysHDL, NFCCommandVExtended cmd_description, ProtocolStateMachine sm, MBcmd answer_acknowledge) {
        byte[] cmd = null;

            if (cmdPayloadIndex == 0) {
                // first iteration
                //Log.v(this.getClass().getName(), "processRequest : computeCmdPayload");
                int cmdlength = cmd_description.getCmdLength(TypeVcmd.TypeVcmd_MBWrite, sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested());
                computeCmdPayload(cmdlength,this.cmd_payload);
                cmd = mMBProtocolBuilder.cretateSimpleMessage(m_payloadChunckSize,this.cmd_payload, this.mMBProtocolFunction, MBcmd.MB_CMD_Acknowledge,this.mGeneralErrorCode);
                if (mHTCmdPayload.size() > 1) {
                    // need to chain cmd
                    // store header for next iteration = done in the mMBProtocolBuilder.cretateSimpleMessage method .....
                    if (DBG)
                        Log.d(TAG, "processResponse : computeCmdPayload with mHBChaningField content: " + cmdPayloadIndex +
                                "[" +  cmd.length + "]");
                    cmdPayloadIndex++;
                } else {
                    // no cmd mHBChaningField Go forward in protocol
                    if (DBG)
                        Log.d(TAG, "processResponse : computeCmdPayload with no mHBChaningField content  = Step++" );
                    mProtocolStep++;
                }
            } else {
                if (mHTCmdPayload.size() > 1 && cmdPayloadIndex < mHTCmdPayload.size()) {
                    // need to chain cmd
                    int offset = cmdPayloadIndex+1;
                    byte[] cur_payload = getCurrentCmdPayload();
                    cmd = mMBProtocolBuilder.setMessageChainingPayload(mMBProtocolBuilder.mHBCommandHeader, cur_payload,
                            offset);
                    if (cmdPayloadIndex < mHTCmdPayload.size()) {
                        if (DBG)
                            Log.d(TAG, "processResponse : computeCmdPayload with mHBChaningField content: " + offset +
                                    "[" +  cmd.length + "]");
                        cmdPayloadIndex++;
                        if (cmdPayloadIndex == mHTCmdPayload.size()) {
                            mProtocolStep++; // the end of data
                        }
                    } else {
                        if (DBG)
                            Log.d(TAG, "processResponse : computeCmdPayload with mHBChaningField content: " + offset +
                                    "[" +  cmd.length + "]" + " = Step++");
                        mProtocolStep++;
                    }
                } else {
                    // no cmd mHBChaningField Go forward in protocol
                    if (DBG)
                        Log.d(TAG, "processResponse : computeCmdPayload without mHBChaningField content: " + cmdPayloadIndex +
                                "[" +  "cmd.length=0 !!" + "]" + " = Step++");
                    mProtocolStep++;
                }

            }



        return cmd;
    }

    public boolean ProcessHostMessage(byte[] response) {
        boolean ret = true;
        // MBProtocolHeaderBuilder mess_response;
        if (response[0] == 0) {
            // correct answer retrieved ......
            byte[] resp = new byte[response.length - 1];
            System.arraycopy(response, 1, resp, 0, resp.length);
            mMBProtocolBuilderReceived = new FTMHeaderBuilder(resp);
            if (mMBProtocolBuilderReceived.mErrorHeaderCode != 0) {
                setProtocolError(mMBProtocolBuilderReceived.mErrorHeaderCode);
                if (DBG)
                    Log.d(TAG, "ProcessHostMessage Response received Header failed :" + mMBProtocolBuilderReceived.mErrorHeaderCode + "  "
                            + "= Step to end");
                //mProtocolStep = 2; // Force end of protocol..........
                mProtocolStep = (byte) (this.mHTProtocolStepsDescr.size() - 1);
                ret = false;
            } else {
/*                if (mMBProtocolBuilderReceived.mHBFunctionCode == this.mMBProtocolFunction
                        && mMBProtocolBuilderReceived.mErrorHeaderCode == 0) {*/
                    // if CMD or RESPONSE
                    if (mMBProtocolBuilderReceived.mHBCommandResponseCode == FTMHeaderBuilder.MBcmd.MB_CMD_Response
                            || mMBProtocolBuilderReceived.mHBCommandResponseCode == FTMHeaderBuilder.MBcmd.MB_CMD_Acknowledge
                            || mMBProtocolBuilderReceived.mHBCommandResponseCode == FTMHeaderBuilder.MBcmd.MB_CMD_Command) {

                        if (mMBProtocolBuilderReceived.mHBErrorCode == FTMHeaderBuilder.MBerror.MB_ERR_NOERROR) {
                            if (DBG)
                                Log.d(TAG, "ProcessHostMessage Header:" + Helper.ConvertHexByteArrayToString(mMBProtocolBuilderReceived.mHBResponseHeader));
                            if (mMBProtocolBuilderReceived.payload != null) {
                                addPayload(mMBProtocolBuilderReceived.payload, mMBProtocolBuilderReceived.mHBCurrentChunckField);
                                if (mMBProtocolBuilderReceived.mHBChaningField == 1) {
                                    // stay at same level of protocol until
                                    // mHBChaningField
                                    // end
                                    if (checkChainingReceivedHeader(mMBProtocolBuilderReceived.getHBResponseHeader())) {
                                        // correct mHBChaningField info
                                        if (mMBProtocolBuilderReceived.isAChainingMessage(
                                                mMBProtocolBuilderReceived.getHBResponseHeader()) == true) {
                                            // mHBChaningField continuing
                                            if (DBG)
                                                Log.d(TAG, "ProcessHostMessage mHBChaningField: loop");
                                            if (mMBProtocolBuilderReceived.mHBChaningField == 0 ) {
                                                // mHBChaningField ended.....
                                                // move forward in protocol.....
                                                mProtocolStep++;
                                                if (DBG)
                                                    Log.d(TAG, "ProcessHostMessage mHBChaningField: ended = Step++");
                                            }
                                        } else {
                                            // mHBChaningField end
                                            if (DBG)
                                                Log.d(TAG, "ProcessHostMessage mHBChaningField: No = Step++");
                                            mProtocolStep++;
                                        }
                                    } else {
                                        // lost chunks or bad chunk number
                                        // Manage how to go forward in the error protocol
                                        // Force end of protocol..........
                                        // mProtocolStep = (byte) (this.mHTProtocolStepsDescr.size()-1);
                                        ret = false;
                                        if (DBG)
                                            Log.d(TAG, "ProcessHostMessage mHBChaningField issue: ended = Step ended");
                                        setProtocolError(MB_ERROR_PROTOCOL_CHAINING_ERROR);
                                    }

                                } else {
                                    mProtocolStep++;
                                    if (DBG)
                                        Log.d(TAG, "ProcessHostMessage Response received with data = Step++");
                                }
                                // Manage how to go forward in the protocol
                            } else {
                                // Manage how to go forward in the protocol
                                mProtocolStep++;
                                if (DBG)
                                    Log.d(TAG, "ProcessHostMessage message received with no data = Step++");

                            }

                        } else {
                            // Manage how to go forward in the error protocol
                            //mProtocolStep = 2; // Force end of protocol..........
                            mProtocolStep = (byte) (this.mHTProtocolStepsDescr.size() - 1);
                            ret = false;
                            if (DBG)
                                Log.e(TAG, "ProcessHostMessage Response received with protocol error = Step++");
                            setProtocolError(mMBProtocolBuilderReceived.mErrorHeaderCode);
                        }

                    } else {
                        if (DBG)
                            Log.d(TAG, "ProcessHostMessage Response received : other than Response/Acknowledge!! = Step = same");
                        ret = false;
                    }

//                }
            }

        } else {
            // issue ......
            ret = false;
            // go to the end of Protocol
            if (DBG)
                Log.d(TAG, "ProcessHostMessage Response received Response issue [0] != 0");
        }

        return ret;
    }


    public boolean checkProtocolDataEXchange(boolean ck, long CRC) {
        boolean ret = false;
        byte[] updateCRCFrame = new byte[4];

        if (ck) {
            updateCRCFrame[3] = (byte) ((CRC & 0x000000FF));
            updateCRCFrame[2] = (byte) ((CRC & 0x0000FF00) >> 8);
            updateCRCFrame[1] = (byte) ((CRC & 0x00FF0000) >> 16);
            updateCRCFrame[0] = (byte) ((CRC & 0xFF000000) >> 24);

            byte[] crcreceived = this.getPayload();
            if (crcreceived.length == 4) {
                if (crcreceived[0] == updateCRCFrame[0] && crcreceived[1] == updateCRCFrame[1]
                        && crcreceived[2] == updateCRCFrame[2] && crcreceived[3] == updateCRCFrame[3]) {
                    ret = true;
                } else {
                    // error code to manage
                    this.setProtocolError(MB_ERROR_PROTOCOL_CRC_ERROR);
                }

            }
        } else {
            ret = true;
        }

        return ret;

    }
}
