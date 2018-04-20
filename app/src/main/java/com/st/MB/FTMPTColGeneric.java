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

import java.util.Hashtable;
import java.util.LinkedHashMap;

import com.st.MB.FTMHeaderBuilder.MBFct;
import com.st.MB.FTMHeaderBuilder.MBcmd;
import com.st.nfcv.NFCCommandVExtended;
import com.st.nfcv.SysFileLRHandler;


import android.util.Log;

public abstract class FTMPTColGeneric {
    static final String TAG = "FTMPTColGeneric";
    static final boolean DBG = false;

    public static  enum ProtocolStatus {
        MB_PTL_Request,
        MB_PTL_Answer,
        MB_PTL_Acknowledge,
        MB_PTL_Finished,
        MB_PTL_End,
        MB_PTL_Dummy
        }

    public static enum ProtocolStateMachine {
        MB_PTL_SM_Current, MB_PTL_SM_Next, MB_PTL_SM_Previous, MB_PTL_SM_End
    };

    protected LinkedHashMap<Byte, ProtocolStatus> mHTProtocolStepsDescr = new LinkedHashMap<Byte, ProtocolStatus>() ;

    //private Hashtable<Byte, byte[]> mHTPayloadReceived = new Hashtable<Byte, byte[]>() ;
    protected LinkedHashMap<Integer, byte[]> mHTPayloadReceived = new LinkedHashMap<Integer, byte[]>() ;
    protected LinkedHashMap<Integer, Integer> mHTPayloadReceivedSize = new LinkedHashMap<Integer, Integer>() ;
    protected int totalPayloadReceived = 0;
    public int getTotalPayloadReceived() {
        return totalPayloadReceived;
    }


    protected LinkedHashMap<Integer, Integer> mHTCmdPayload = new LinkedHashMap<Integer, Integer>() ;

    protected FTMHeaderBuilder mMBProtocolBuilder;
    protected FTMHeaderBuilder mMBProtocolBuilderReceived;

    protected int mMaxChunckForTranceiveData = 256;

    protected byte mProtocolStep = 0;

    // Error flags
    byte MB_ERROR_PROTOCOL_NO_ERROR = 0x00;
    byte MB_ERROR_PROTOCOL_CRC_ERROR = 0x2A;
    byte MB_ERROR_PROTOCOL_LOST_FUNCTION = 0x2B;
    byte MB_ERROR_PROTOCOL_CHAINING_ERROR = 0x2C;


    protected byte mGeneralErrorCode = MB_ERROR_PROTOCOL_NO_ERROR;
    protected boolean mErrorProtocol = false;

    protected MBFct mMBProtocolFunction = MBFct.MB_FCT_DUMMY;

    //private     byte[] cmd_header = null;
    protected     byte[] cmd_payload;

    //protected     byte[] response_payload;


   // private int cmd_chunk_nb;
    // private int cmd_total_chunck;

    public FTMPTColGeneric() {
        // TODO Auto-generated constructor stub
    }
    public FTMPTColGeneric(MBFct Fct) {
        // TODO Auto-generated constructor stub
        mMBProtocolFunction = Fct;
    }

    public byte getMBProtocolError() {

        if (mGeneralErrorCode != 0) return mGeneralErrorCode;
        if (mMBProtocolBuilderReceived != null && mMBProtocolBuilderReceived != null)
            return (byte) (mMBProtocolBuilder.mErrorHeaderCode | mMBProtocolBuilderReceived.mErrorHeaderCode);

        if (mMBProtocolBuilder != null && mMBProtocolBuilder.mErrorHeaderCode != 0) return (byte) (mMBProtocolBuilder.mErrorHeaderCode);
        if (mMBProtocolBuilderReceived != null && mMBProtocolBuilderReceived.mErrorHeaderCode != 0) return (byte) (mMBProtocolBuilderReceived.mErrorHeaderCode);
        return mGeneralErrorCode;
    }


    public byte[] getPayload() {
        // return all payload
        byte[] data = null;
        int size = mHTPayloadReceived.size();
        int estimatedsize = 0;
        for (Integer key : mHTPayloadReceived.keySet()) {

        //for (int i =1;i<=size;i++) {
            byte[] dd = mHTPayloadReceived.get(key);
            estimatedsize = estimatedsize + dd.length;
        }
        data = new byte[estimatedsize];
        estimatedsize = 0;
        byte[] chunk = null;
        for (Integer key : mHTPayloadReceived.keySet()) {
        //for (int i =0;i<size;i++) {
            chunk = mHTPayloadReceived.get(key);
            System.arraycopy(chunk, 0, data, estimatedsize, chunk.length);
            estimatedsize = estimatedsize + chunk.length;
        }

        return data;

    }

    public void addPayload(byte[] payload,int chunck) {
        int size = mHTPayloadReceived.size();
        if (size == 0) totalPayloadReceived = 0;
        if (DBG)
            Log.d(TAG, "mHTPayloadReceived index :" + size +"["+ payload.length +"]");
        mHTPayloadReceived.put( size, payload);
        mHTPayloadReceivedSize.put( chunck, payload.length); totalPayloadReceived = totalPayloadReceived + payload.length;

    }

    public boolean checkChainingReceivedHeader(byte[] header) {
        boolean ret = true;
        int size = mHTPayloadReceived.size();
        int chunk = this.mMBProtocolBuilderReceived.getChainingChunckNumber(header);
        if (size != (chunk)) {
            ret = false;
            if (DBG)
                Log.d(TAG, "checkChainingReceivedProcess mHBChaningField issue Actual Payload= :" + size +
                        " Chunck = " + "["+ chunk +"]");
        }

        return ret;
    }


    public ProtocolStatus getCurrentProtocolStep() {
        if (mProtocolStep < mHTProtocolStepsDescr.size()){
            return mHTProtocolStepsDescr.get(mProtocolStep);
        } else {
            //return ProtocolStatus.MB_PTL_Dummy;
            // We are in that case when an error occured during protocol....... TBC
            return mHTProtocolStepsDescr.get(mHTProtocolStepsDescr.size()-1);
        }
    }

    public boolean isProtocolEndedRequired() {
        boolean ret = false;
        int ptl = mHTProtocolStepsDescr.size();
        if (mProtocolStep < ptl){
            byte keyval = (byte) ((byte) ((byte) mHTProtocolStepsDescr.size() & 0xFF)-1);
            ProtocolStatus st = mHTProtocolStepsDescr.get(keyval);
            if (st == ProtocolStatus.MB_PTL_End) {
                ret = true;
            }
        } else {
            return false;
        }
        return ret;
    }
    public boolean setProtocoltoTheEnd() {
        boolean ret = false;
        if (isProtocolEndedRequired()){
            mProtocolStep = (byte) ((byte) mHTProtocolStepsDescr.size() -1);
            ret = true;
        } else {
        }
        return ret;
    }


    // Define list of error
    // ==> 10x Tasks error
    // ==> xx  Protocol builder errors
    // ==> 5x  CRC
    public byte getProtocolError() {
        // TODO Auto-generated method stub
        return mGeneralErrorCode;
    }
    public void setProtocolError(byte err) {
        if (err != 0 ) {
            mErrorProtocol = true;
            mGeneralErrorCode = err;
        }

/*        if (mMBProtocolBuilderReceived != null)
            mMBProtocolBuilderReceived.mErrorHeaderCode = (byte) (mMBProtocolBuilderReceived.mErrorHeaderCode | err);
        mMBProtocolBuilder.mErrorHeaderCode = (byte) (mMBProtocolBuilder.mErrorHeaderCode | err);*/
    }

    public void getBackCurrentProtocolStep() {
        // TODO Auto-generated method stub
        int size = mHTPayloadReceived.size();
        if (mProtocolStep > 0 && mProtocolStep < size) mProtocolStep--;
    }

    protected int cmdPayloadIndex = 0;

    private void addCmdPayload(int value) {
        int size = mHTCmdPayload.size();
        mHTCmdPayload.put(size, value);

    }

    public void setCmdPayload(byte[] cmd_payload) {
        this.cmd_payload = cmd_payload;
        cmdPayloadIndex = 0;
    }

    int m_payloadChunckSize = 0;


    protected void computeCmdPayload(int cmd_size, byte[] cmd_payload) {
        // Create Hashtable for Payload chuncks ....
        int MaxChunckofData = 0;
        int nbchuncks = 0;
        if (cmd_payload == null) {
            MaxChunckofData = mMBProtocolBuilder.getMBMaxPayload() - this.mMBProtocolBuilder.mMBMaxHeaderSimple - cmd_size;
            this.cmd_payload = null;
            cmdPayloadIndex = 0;
            m_payloadChunckSize = MaxChunckofData;

        } else {
            if (cmd_payload.length > (mMBProtocolBuilder.getMBMaxPayload()
                    - this.mMBProtocolBuilder.mMBMaxHeaderSimple - cmd_size)) {
                // need mHBChaningField = max data = Header + payload
                MaxChunckofData = mMBProtocolBuilder.getMBMaxPayload() - this.mMBProtocolBuilder.mMBMaxHeaderChained - cmd_size;
            } else {
                // no mHBChaningField
                MaxChunckofData = mMBProtocolBuilder.getMBMaxPayload() - this.mMBProtocolBuilder.mMBMaxHeaderSimple - cmd_size;
            }
            m_payloadChunckSize = MaxChunckofData;
            if (cmd_payload != null) {
                nbchuncks = cmd_payload.length / MaxChunckofData;
                int restof = cmd_payload.length % MaxChunckofData;
                if (restof != 0)
                    nbchuncks++;
                int ref = 0;
                for (int it = 0; it < nbchuncks; it++) {
                    addCmdPayload(ref * MaxChunckofData);
                }

            }
        }
        if (DBG) Log.v(this.getClass().getName(), "computeCmdPayload information..: " +
                "Nb Chuncks :" + nbchuncks + "  Chunk size = " + MaxChunckofData);

    }

    protected byte[] getCurrentCmdPayload() {
        byte[] data;
        int datasize;
        int datachunk = cmd_payload.length - cmdPayloadIndex*m_payloadChunckSize ;
        if (datachunk < 0) {
            datasize = cmd_payload.length - (cmdPayloadIndex-1)*m_payloadChunckSize;
            data = new byte[datasize];
            System.arraycopy(cmd_payload, (cmdPayloadIndex-1)*m_payloadChunckSize, data, 0, datasize);
            // Only the end to process
            // from (cmdPayloadIndex-1)*MBProtocolBuilder.mMBMaxPayload to cmd_payload.length;
            if (DBG)
                Log.d(TAG, "getCurrentCmdPayload information..: " +
                        "Payload Chunck max size :" + m_payloadChunckSize + "index :" + cmdPayloadIndex + "datasize :" + datasize + "  From : " + cmdPayloadIndex*m_payloadChunckSize);
        } else {
            if (cmdPayloadIndex*m_payloadChunckSize  + m_payloadChunckSize <  cmd_payload.length) {
             // Get the current MBProtocolBuilder.mMBMaxPayload data
                datasize = m_payloadChunckSize;
                data = new byte[datasize];
                System.arraycopy(cmd_payload, cmdPayloadIndex*m_payloadChunckSize, data, 0, datasize);

            } else {
                // get only the rest of buffer
                datasize = cmd_payload.length - cmdPayloadIndex*m_payloadChunckSize;
                data = new byte[datasize];
                System.arraycopy(cmd_payload, cmdPayloadIndex*m_payloadChunckSize, data, 0, datasize);

            }
            if (DBG)
                Log.d(TAG, "getCurrentCmdPayload information..: " +
                        "Payload Chunck max size :" + m_payloadChunckSize + "index :" + cmdPayloadIndex + "datasize :" + datasize + "  From : " + cmdPayloadIndex*m_payloadChunckSize);
        }
        return data;
    }

    public byte[] getMBGPRequestHeader() {
        return this.mMBProtocolBuilder.mHBCommandHeader;
    }
    public byte[] getMBGPReceivedHeader() {
        return this.mMBProtocolBuilderReceived.mHBCommandHeader;
    }

    protected boolean checkdataExchange = false;
    public  long lCRC = 0L;
    public   void  setCheckProtocolDataEXchangeParameters(boolean ck, long crc) {
        checkdataExchange = ck;
        lCRC = crc;
    }
    // ====================================================================================
    public abstract  void initProtocol(int max_tranceive_data_available) ;

    public abstract byte[] processRequest(SysFileLRHandler sysHDL, NFCCommandVExtended cmd_description, ProtocolStateMachine sm);

    public abstract byte[] processResponse(SysFileLRHandler sysHDL, NFCCommandVExtended cmd_description, ProtocolStateMachine sm, MBcmd answer_acknowledge) ;

    public abstract  boolean ProcessHostMessage(byte [] response) ;
        // TODO Auto-generated method stub
    public abstract  boolean checkProtocolDataEXchange(boolean ck, long CRC) ;
    // ====================================================================================


}
