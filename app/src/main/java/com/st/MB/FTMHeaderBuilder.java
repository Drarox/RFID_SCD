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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.util.Log;

public class FTMHeaderBuilder {



    public static  enum MBFct {
        MB_FCT_SIMPLE,
        MB_FCT_SIMPLE_CHAINED,
        MB_FCT_FILE_UPLOAD,
        MB_FCT_DUMMY,
        MB_FCT_SIMPLE_From_HOST,
        MB_FCT_SIMPLE_CHAINED_From_HOST,
        MB_FCT_FW_UPLOAD,
        MB_FCT_FWU_PWD,
        MB_FCT_IMAGE_DOWNLOAD,
        MB_FCT_IMAGE_UPLOAD,
        MB_FCT_Xfers_UPLOAD,
        MB_FCT_CHRONO
        }

    public static enum MBcmd {
        MB_CMD_Command,
        MB_CMD_Response,
        MB_CMD_Acknowledge,
        MB_CMD_DUMMY,
        }
    public static enum MBerror {
        MB_ERR_NOERROR,
        MB_ERR_SIMPLE,
        MB_ERR_UNKNOWN_FUNCTION,
        MB_ERR_DUMMY,
        }

    protected int mMBMaxPayload = 240;
    protected int mMBMaxHeaderSimple = 5;
    protected int mMBMaxHeaderChained = 13;

    public int getMBMaxPayload() {
        return mMBMaxPayload;
    }


    static byte[] toBytes(int i)
    {
          byte[] result = new byte[4];

          result[0] = (byte) (i >> 24);
          result[1] = (byte) (i >> 16);
          result[2] = (byte) (i >> 8);
          result[3] = (byte) (i /*>> 0*/);

          return result;
        }

    class MyMap<K,V> extends HashMap<K, V> {

        Map<V,K> reverseMap = new HashMap<V,K>();

        @Override
        public V put(K key, V value) {
            // TODO Auto-generated method stub
            reverseMap.put(value, key);
            return super.put(key, value);
        }

        public K getKey(V value){
            return reverseMap.get(value);
        }
    }

    private MyMap<FTMHeaderBuilder.MBFct, Byte> ProtocolFctDescr = new MyMap<FTMHeaderBuilder.MBFct, Byte>() {
        {
            put(MBFct.MB_FCT_DUMMY, (byte) 0x00);
            put(MBFct.MB_FCT_SIMPLE, (byte) 0x01);              // Simple cmd from Reader to Host requiring on simple answer R/A/Ack
            put(MBFct.MB_FCT_SIMPLE_CHAINED, (byte) 0x02);      // Simple cmd from Reader to Host requiring on Chaining answer R/A/Ack
            put(MBFct.MB_FCT_FILE_UPLOAD, (byte) 0x03);      // Simple cmd from Reader to Host Starting FW upload R/A/Ack
            put(MBFct.MB_FCT_FW_UPLOAD, (byte) 0x04);      //  FW upload R/A/Ack
            put(MBFct.MB_FCT_SIMPLE_From_HOST, (byte) 0x05);      // Simple cmd from Reader to Host Starting FW upload R/A/Ack
            put(MBFct.MB_FCT_SIMPLE_CHAINED_From_HOST, (byte) 0x06);      // Simple cmd from Reader to Host Starting FW upload R/A/Ack
            put(MBFct.MB_FCT_FWU_PWD, (byte) 0x08);      // Simple cmd from Reader to Host Starting FW upload R/A/Ack
            put(MBFct.MB_FCT_IMAGE_DOWNLOAD, (byte) 0x09);      // Simple cmd from Reader to Host Starting Image upload R/A/Ack
            put(MBFct.MB_FCT_IMAGE_UPLOAD, (byte) 0x07);      // Simple cmd from Reader to Host Starting Image upload R/A/Ack
            put(MBFct.MB_FCT_Xfers_UPLOAD, (byte) 0x0A);      // Host to Reader Starting data XFers upload R/A/Ack
            put(MBFct.MB_FCT_CHRONO, (byte) 0x0B);      // Host to Reader Starting data XFers Chrono
        }
    };
    private MyMap<FTMHeaderBuilder.MBcmd, Byte> ProtocolCmdDescr = new MyMap<FTMHeaderBuilder.MBcmd, Byte>() {
        {
            put(MBcmd.MB_CMD_Command, (byte) 0x00);
            put(MBcmd.MB_CMD_Response, (byte) 0x01);
            put(MBcmd.MB_CMD_Acknowledge, (byte) 0x02);
            put(MBcmd.MB_CMD_DUMMY, (byte) 0xAA);
        }
    };

    private MyMap<FTMHeaderBuilder.MBerror, Byte> ProtocolErrDescr = new MyMap<FTMHeaderBuilder.MBerror, Byte>() {
        {
            put(MBerror.MB_ERR_NOERROR, (byte) 0x00);
            put(MBerror.MB_ERR_SIMPLE, (byte) 0x01);
            put(MBerror.MB_ERR_DUMMY, (byte) 0xAA);
            put(MBerror.MB_ERR_UNKNOWN_FUNCTION,(byte) 0x10);
        }
    };

    public FTMHeaderBuilder(int max_transceive_data) {
        // TODO Auto-generated constructor stub
        //this.mMBMaxPayload = (max_transceive_data > this.mMBMaxPayload ? this.mMBMaxPayload : max_transceive_data);
        this.mMBMaxPayload = (max_transceive_data );
    }

    public MBFct mHBFunctionCode;
    public MBcmd mHBCommandResponseCode;
    public MBerror mHBErrorCode;

    public byte mHBChaningField = 0;
    public int mHBTotalChainingLenField = 0;
    public int mHBCurrentChainingLenField = 0;
    public int mHBCurrentChunckField = 0;
    public int mHBTotalChunckField = 0;
    //public int current_current_chunck = 0;


    public byte mHBLenField;
    public byte[] payload = null;

    /**
     *
     */
    public byte[] mHBCommandHeader;
    public byte[] getHBCommandHeader() {
        return mHBCommandHeader;
    }


    /**
     *
     */
    public byte[] mHBResponseHeader;
    public byte[] getHBResponseHeader() {
        return mHBResponseHeader;
    }

    public byte getHeaderError() {
        return (byte) (mErrorHeaderCode | ProtocolErrDescr.get(mHBErrorCode));
    }

    byte mErrorHeaderCode = 0;

    byte MB_ERROR_HEADER_NO_ERROR = 0x00;
    byte MB_ERROR_HEADER_SIZE_ERROR = 0x01;
    byte MB_ERROR_HEADER_FONCTION_ERROR = 0x02;
    byte MB_ERROR_HEADER_COMMAND_ERROR = 0x03;
    byte MB_ERROR_HEADER_CHAINING_ERROR = 0x04;
    byte MB_ERROR_HEADER_UNKNOWN_ERROR = 0x05;
    byte MB_ERROR_HEADER_NOANSWER_ERROR = 0x06;
    // 0: no error
    // 1: size
    // ....


    public FTMHeaderBuilder(byte[] buffer) {
        // TODO Auto-generated constructor stub
        mErrorHeaderCode = MB_ERROR_HEADER_NO_ERROR;
        mHBFunctionCode = MBFct.MB_FCT_DUMMY;
        if (buffer.length >= mMBMaxHeaderSimple) {
            // seems to be a correct protocol
            boolean found = false;
            mHBFunctionCode = MBFct.MB_FCT_DUMMY;
            if (ProtocolFctDescr.containsValue(buffer[0]) == true) {
                mHBFunctionCode = ProtocolFctDescr.getKey(buffer[0]);

            } else {
                mErrorHeaderCode = MB_ERROR_HEADER_FONCTION_ERROR;
            }
            mHBCommandResponseCode = MBcmd.MB_CMD_DUMMY;
            if (ProtocolCmdDescr.containsValue(buffer[1]) == true) {
                mHBCommandResponseCode = ProtocolCmdDescr.getKey(buffer[1]);

            }else {
                mErrorHeaderCode = MB_ERROR_HEADER_COMMAND_ERROR;
            }
            mHBErrorCode = MBerror.MB_ERR_DUMMY;
            if (ProtocolErrDescr.containsValue(buffer[2]) == true) {
                mHBErrorCode = ProtocolErrDescr.getKey(buffer[2]);

            } else {
                mErrorHeaderCode = MB_ERROR_HEADER_UNKNOWN_ERROR;
            }

            mHBChaningField =     buffer[3];

            if (mHBChaningField == 0x01) {
                if (buffer.length >= mMBMaxHeaderChained) {
                    // mHBChaningField message
                    // full length
                    mHBTotalChainingLenField = ((int)buffer [4] & 0xFF) << 24 | ((int)buffer [5] & 0xFF) << 16 | ((int)buffer [6] & 0xFF) << 8 |  ((int)buffer [7] & 0xFF);

                    // Total number of chuck
                    mHBTotalChunckField = ((int)buffer [8] & 0xFF) << 8 | ((int)buffer [9] & 0xFF);

                    // nb chuck
                    mHBCurrentChunckField = ((int)buffer [10] & 0xFF) << 8 | ((int)buffer [11] & 0xFF);
                    // length
                    mHBCurrentChainingLenField = (int)(buffer[12]& 0xFF);

                    mHBLenField = (byte) mHBCurrentChainingLenField;
                    int data_len = mHBLenField & 0xFF;
                    if (data_len > 0 && data_len <256 && (buffer.length == (data_len+ mMBMaxHeaderChained))) {
                        payload = new byte[data_len];
                        System.arraycopy(buffer, mMBMaxHeaderChained, payload, 0, data_len);

                    }else {
                        mErrorHeaderCode = MB_ERROR_HEADER_SIZE_ERROR;
                    }
                    // Store Header
                    mHBResponseHeader = new byte[mMBMaxHeaderChained];
                    System.arraycopy(buffer, 0, mHBResponseHeader, 0, mMBMaxHeaderChained);
                    Log.v(this.getClass().getName(), "Message info = "+
                            "  Chaining : " + mHBChaningField +
                            "  mHBTotalChainingLenField : " + mHBTotalChainingLenField +
                            "  mHBTotalChunckField : " + mHBTotalChunckField +
                            "  mHBCurrentChunckField : " + mHBCurrentChunckField +
                            "  mHBCurrentChainingLenField : " + mHBCurrentChainingLenField);


                } else {
                    // Decoding error
                    mErrorHeaderCode = MB_ERROR_HEADER_SIZE_ERROR;
                }


            } else {
                // NO CHAINING
                mHBLenField = buffer[4];
                int data_len = mHBLenField & 0xFF;

                if (data_len > 0 && data_len <256 && (buffer.length == (data_len+ mMBMaxHeaderSimple))) {
                    payload = new byte[data_len];
                    System.arraycopy(buffer, mMBMaxHeaderSimple, payload, 0, data_len);

                } else {
                    // Decoding error
                    if (mHBLenField == 0) {
                        Log.v(this.getClass().getName(), "Received transaction without data ....");
                    } else {
                        mErrorHeaderCode = MB_ERROR_HEADER_SIZE_ERROR;
                    }
                }
                // Store Header
                mHBResponseHeader = new byte[mMBMaxHeaderSimple];
                System.arraycopy(buffer, 0, mHBResponseHeader, 0, mMBMaxHeaderSimple);
                Log.v(this.getClass().getName(), "Message info = "+
                        "  mHBFunctionCode : " + this.mHBFunctionCode +
                        "  Cmd : " + this.mHBCommandResponseCode +
                        "  mHBErrorCode : " + this.mHBErrorCode +
                        "  Chaining : " + mHBChaningField +
                        "  mHBTotalChainingLenField : " + mHBTotalChainingLenField +
                        "  mHBTotalChunckField : " + mHBTotalChunckField +
                        "  mHBCurrentChunckField : " + mHBCurrentChunckField +
                        "  mHBCurrentChainingLenField : " + mHBCurrentChainingLenField);
            }


        } else {
            // not a correct protocol = specify
            mHBFunctionCode = MBFct.MB_FCT_DUMMY;
            mHBCommandResponseCode = MBcmd.MB_CMD_DUMMY;
            mHBErrorCode = MBerror.MB_ERR_DUMMY;
            mHBChaningField = 0;
            mHBLenField = 0;
            // Store Header
            mHBResponseHeader = null;
            mErrorHeaderCode = MB_ERROR_HEADER_UNKNOWN_ERROR;
        }
    }


    public byte[] cretateSimpleMessage(int maxDataChunck, byte[] payload, MBFct fct, MBcmd cmd, byte error_code){
        int payloadlength;
        byte[] buffer = null;
        if (payload != null ) {
            //if (payload.length < (getMBMaxPayload()- this.mMBMaxHeaderSimple)) {
            if (payload.length < (maxDataChunck)) {
                // no mHBChaningField
                payloadlength = payload.length;
                byte[] header = createProtocolHeader(maxDataChunck,payloadlength, fct,  cmd,error_code);

                mHBCommandHeader = new byte[header.length];
                System.arraycopy(header, 0, mHBCommandHeader, 0, header.length);

                buffer = new byte[payload.length+ header.length];
                System.arraycopy(header, 0, buffer, 0, header.length);
                System.arraycopy(payload, 0, buffer, header.length, payload.length);

            } else {
                // mHBChaningField
                payloadlength = payload.length;
                byte[] header = createProtocolHeader(maxDataChunck,payloadlength, fct,  cmd, error_code);

                mHBCommandHeader = new byte[header.length];
                System.arraycopy(header, 0, mHBCommandHeader, 0, header.length);

                buffer = new byte[maxDataChunck + header.length];
                System.arraycopy(header, 0, buffer, 0, header.length);
                System.arraycopy(payload, 0, buffer, header.length, maxDataChunck);

            }

        } else {
            // No Payload
            payloadlength = 0;
            byte[] header = createProtocolHeader(maxDataChunck,payloadlength, fct,  cmd,error_code);
            mHBCommandHeader = new byte[header.length];
            System.arraycopy(header, 0, mHBCommandHeader, 0, header.length);

            buffer = new byte[payloadlength+ header.length];
            System.arraycopy(header, 0, buffer, 0, header.length);

        }

        return buffer;
    }


    public byte[] setMessageChainingPayload(byte [] header, byte[] payload, int nb) {
        byte[] buffer = null;
        // length
        header [12] = (byte) payload.length;
        // nb chunck
        int nbchunck = nb;
        setChainingChunckNumber(header,nbchunck);
        buffer = new byte[header.length+ payload.length];
        System.arraycopy(header, 0, buffer, 0, header.length);
        System.arraycopy(payload, 0, buffer, header.length, payload.length);
        return buffer;

    }


    public boolean isAChainingMessage(byte [] header) {
        boolean ret = false;
        if (this.mHBChaningField == 0x01) {
            int chuncknb = getChainingChunckNumber(header);
            int totalchuncknb = getChainingTotalChunckNumber(header);
            Log.v(this.getClass().getName(), "isAChainingMessage Chuncks: "+ chuncknb + "/" +  totalchuncknb);
            if (chuncknb >= totalchuncknb ) {
                // end of mHBChaningField
                ret = false;
                this.mHBChaningField = 0;
            } else {
                ret = true;
            }
        } else {
            ret = false;
        }
        return ret;
    }


    public void setSimpleFrameError(byte [] header, MBerror err) {
        header [2] = ProtocolErrDescr.get(err);
    }
    public void setSimpleFrameCmd(byte [] header, MBcmd cmd) {
        header [1] = ProtocolCmdDescr.get(cmd);
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    private byte[] createProtocolHeader(int maxDataChunck, int payloadlength, MBFct fct, MBcmd cmd, byte error_code){
        //byte[] header;
        //if (payloadlength < (getMBMaxPayload()-this.mMBMaxHeaderSimple)) {
        if (payloadlength < (maxDataChunck)) {
            mHBCommandHeader = new byte[mMBMaxHeaderSimple];
            mHBCommandHeader[0] = ProtocolFctDescr.get(fct);
            mHBCommandHeader[1] = ProtocolCmdDescr.get(cmd);
            mHBCommandHeader[2] = error_code;
            //mHBCommandHeader [2] = ProtocolErrDescr.get(MBerror.MB_ERR_NOERROR);
            mHBCommandHeader[3] = 0; //No Chaining
            mHBCommandHeader[4] = (byte) payloadlength; //No Chaining

        } else {
            // mHBChaningField
            mHBCommandHeader = new byte[mMBMaxHeaderChained];
            mHBCommandHeader[0] = ProtocolFctDescr.get(fct);
            mHBCommandHeader[1] = ProtocolCmdDescr.get(cmd);
            mHBCommandHeader[2] = error_code;
            //mHBCommandHeader [2] = ProtocolErrDescr.get(MBerror.MB_ERR_NOERROR);
            mHBCommandHeader[3] = 1; //Chaining
            // full length
            byte[] tmp = intToByteArray(payloadlength);
            for (int i = 0; i < 4; i++) {
                mHBCommandHeader[4+i] = tmp[i];
            }
            // Total number of chuck
            int toalchuncks = payloadlength/(maxDataChunck);
            int restofdivide = payloadlength % (maxDataChunck);
            if (restofdivide != 0) toalchuncks = toalchuncks +1;

            mHBCommandHeader[8] = (byte) (((int) (toalchuncks & 0xFF00)) >> 8);
            mHBCommandHeader[9] = (byte) (((int) (toalchuncks & 0xFF)));
            // nb chunck
            int nbchunck = 1;
            setChainingChunckNumber(mHBCommandHeader,nbchunck);
            // length
            mHBCommandHeader[12] = (byte) (maxDataChunck);

            //header [11] = (byte) (((int) (nbchunck & 0xFF00)) >> 8);;
            //header [12] = (byte) (((int) (nbchunck & 0xFF)));

        }

        return mHBCommandHeader;
    }


    private boolean setChainingChunckNumber(byte[] header, int nb) {
        boolean ret = false;
        if (header.length >= 12) {
            header[10] = (byte) (((int) (nb & 0xFF00)) >> 8);
            header[11] = (byte) (((int) (nb & 0xFF)));
            ret = true;
        } else {

        }
        return ret;
    }

    protected int getChainingChunckNumber(byte[] header) {
        int ret = 0;
        if (header.length >= 12) {
            ret =  (((int) ((int)header[10] & 0xFF)) << 8) | ((int)header[11] & 0xFF);
        } else {
            ret = -1;
        }
        return ret;

    }
    protected int getChainingTotalChunckNumber(byte[] header) {
        int ret = 0;
        if (header.length >= 12) {
            ret =  (((int) ((int)header [8] & 0xFF)) << 8) | ((int)header[9] & 0xFF);
        } else {
            ret = -1;
        }
        return ret;
    }

}
