/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1708 $
  * Revision of last commit    :  $Rev: 1708 $
  * Date of last commit     :  $Date: 2016-02-28 17:44:48 +0100 (Sun, 28 Feb 2016) $
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

package com.st.nfcv;

import java.util.Hashtable;

import com.st.NFC.NFCTag;
import com.st.nfcv.stnfcRegisterHandler.ST25DVRegisterTable;
import com.st.util.DebugUtility;

import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;


//public class NFCCommandVExtended extends NFCCommandVLR {
public class NFCCommandVExtended extends NFCCommandV {

    // GPO
    protected static final byte  TYPE5_ST_CMD_MANAGE_GPO_CFG  = (byte) 0xA9;

    protected static final byte  TYPE5_ST_CMD_READ_STATIC_CFG   = (byte) 0xA0;
    protected static final byte  TYPE5_ST_CMD_WRITE_STATIC_CFG  = (byte) 0xA1;

    // IC1
//     protected static final byte  TYPE5_ST_CMD_READ_DYN_CFG      = (byte) 0xAE;
//     protected static final byte  TYPE5_ST_CMD_WRITE_DYN_CFG     = (byte) 0xAF;
//     public static final String CMD_VERSION_BUILD = "IC1.1";
    // IC2 / FPGA
    protected static final byte  TYPE5_ST_CMD_READ_DYN_CFG      = (byte) 0xAD;
    protected static final byte  TYPE5_ST_CMD_WRITE_DYN_CFG     = (byte) 0xAE;
    public static final String CMD_VERSION_BUILD = "IC1.2";

    static final String TAG = "NFCCommandVExtended";


    public NFCCommandVExtended(String _modelName) {
        //super(_modelName);
        // TODO Auto-generated constructor stub
    }

    public byte[] SendGetSystemInfoCommandExtended(Tag myTag, SysFileLRHandler sysFileHnd, byte infoParamField) {


        int counterLoop = 0;

        byte[] response = new byte[]{(byte) 0xAA};
        byte[] frame2BytesAddr;
        byte[] frame1ByteAddr;

        if (sysFileHnd.isUidRequested()) {
            frame2BytesAddr = new byte[]{(byte) 0x2A, (byte) TYPE5_CMD_EXTENTED_GET_SYSTEM_INFORMATION, (byte) 0xFF, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            frame1ByteAddr = new byte[]{(byte) 0x22, (byte) TYPE5_CMD_EXTENTED_GET_SYSTEM_INFORMATION, (byte) 0xFF, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        } else {
            frame2BytesAddr = new byte[]{(byte) 0x0A, (byte) TYPE5_CMD_EXTENTED_GET_SYSTEM_INFORMATION, (byte) 0xFF,};
            frame1ByteAddr = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_EXTENTED_GET_SYSTEM_INFORMATION, (byte) 0xFF,};
        }

        frame2BytesAddr[2] = (byte) (infoParamField & 0xFF);
        frame1ByteAddr[2] = (byte) (infoParamField & 0xFF);

        // INVENTORY HAS DETECTED DEVICE ... READING GET SYSTEM INFO

        while ((response == null || response[0] == (byte) 0xAA) && counterLoop <= 5) {
            try {
                counterLoop++;
                NfcV nfcvTag = NfcV.get(myTag);
                // Addressed mode added for MODAT531 phones needing Address
                // mode (2015-07-21)
                if (sysFileHnd.isUidRequested()) {
                    // 2 bytes address
                    frame2BytesAddr[2] = myTag.getId()[0];
                    frame2BytesAddr[3] = myTag.getId()[1];
                    frame2BytesAddr[4] = myTag.getId()[2];
                    frame2BytesAddr[5] = myTag.getId()[3];
                    frame2BytesAddr[6] = myTag.getId()[4];
                    frame2BytesAddr[7] = myTag.getId()[5];
                    frame2BytesAddr[8] = myTag.getId()[6];
                    frame2BytesAddr[9] = myTag.getId()[7];
                    // 1 bytes address
                    frame1ByteAddr[2] = myTag.getId()[0];
                    frame1ByteAddr[3] = myTag.getId()[1];
                    frame1ByteAddr[4] = myTag.getId()[2];
                    frame1ByteAddr[5] = myTag.getId()[3];
                    frame1ByteAddr[6] = myTag.getId()[4];
                    frame1ByteAddr[7] = myTag.getId()[5];
                    frame1ByteAddr[8] = myTag.getId()[6];
                    frame1ByteAddr[9] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();
                if (sysFileHnd.isBasedOnTwoBytesAddress() == true) {
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> SendGetSystemInfoCommandExtended() request: " + Helper.ConvertHexByteArrayToString(frame2BytesAddr));

                    response = nfcvTag.transceive(frame2BytesAddr);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "SendGetSystemInfoCommandExtended response: " + Helper.ConvertHexByteArrayToString(response));
                } else {
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> SendGetSystemInfoCommandExtended() request: " + Helper.ConvertHexByteArrayToString(frame1ByteAddr));

                    response = nfcvTag.transceive(frame1ByteAddr);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "SendGetSystemInfoCommandExtended response: " + Helper.ConvertHexByteArrayToString(response));
                }
                // consuming
                if (response[0] == (byte) 0x00) {
                    return response;
                }
            } catch (Exception e) {
                Log.e("Exception","SendGetSystemInfoCommandExtended " + e.getMessage());
                //ma.setBasedOnTwoBytesAddress(false);
            }
        }

        // Used for DEBUG : Log.i("NFCCOmmand", "Response Get System Info " +
        // Helper.ConvertHexByteArrayToString(response));
        return response;
    }


    // =============================================== cmd object declaration

    public class typeVcmdDefinition {
        public byte[][] cmdAdrMode;
        public byte[][] cmd;

        public typeVcmdDefinition(byte[] ca1ba, byte[] ca2ba, byte[] c1ba, byte[] c2ba) {
            super();
            cmdAdrMode = new byte[2][];
            cmd = new byte[2][];
            // 1 byte adr
            this.cmdAdrMode[0] = ca1ba;
            // 2 byte adr
            this.cmdAdrMode[1] = ca2ba;
            this.cmd[0] = c1ba;
            this.cmd[1] = c2ba;
        }

        public byte[] getCmdAdrMode1B() {
            return cmdAdrMode[0].clone();
        }

        public byte[] getCmdAdrMode2B() {
            return cmdAdrMode[1].clone();
        }

        public byte[] getCmd1B() {
            return cmd[0].clone();
        }

        public byte[] getCmd2B() {
            return cmd[1].clone();
        }

        public int getCmd1BLength(boolean adrmode) {
            int ret = 0;
            if (adrmode) {
                ret = cmdAdrMode[0].length;
            } else {
                ret = cmd[0].length;
            }
            return ret;
        }

        public int getCmd2BLength(boolean adrmode) {
            int ret = 0;
            if (adrmode) {
                ret = cmdAdrMode[1].length;
            } else {
                ret = cmd[1].length;
            }
            return ret;
        }
    }


    private typeVcmdDefinition MBWriteCmds = new typeVcmdDefinition(MBWriteAdrModeCmd1bytesAddress, MBWriteAdrModeCmd2bytesAddress, MBWriteCmd1bytesAddress, MBWriteCmd2bytesAddress);
    private static final byte[] MBWriteAdrModeCmd1bytesAddress = new byte[]{(byte) 0x22, (byte) 0xAA, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    private static final byte[] MBWriteAdrModeCmd2bytesAddress = new byte[]{(byte) 0x2A, (byte) 0xAA, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};
    private static final byte[] MBWriteCmd1bytesAddress = new byte[]{(byte) 0x02, (byte) 0xAA, (byte) 0x02, (byte) 0x00};
    private static final byte[] MBWriteCmd2bytesAddress = new byte[]{(byte) 0x0A, (byte) 0xAA, (byte) 0x02, (byte) 0x00};


    private static final byte[] MBReadLengthAdrModeCmd1bytesAddress = new byte[]{(byte) 0x22, (byte) 0xAB, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    private static final byte[] MBReadLengthAdrModeCmd2bytesAddress = new byte[]{(byte) 0x2A, (byte) 0xAB, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    private static final byte[] MBReadLengthCmd1bytesAddress = new byte[]{(byte) 0x02, (byte) 0xAB, (byte) 0x02};
    private static final byte[] MBReadLengthCmd2bytesAddress = new byte[]{(byte) 0x0A, (byte) 0xAB, (byte) 0x02};
    public typeVcmdDefinition MBReadLengthCmds = new typeVcmdDefinition(MBReadLengthAdrModeCmd1bytesAddress, MBReadLengthAdrModeCmd2bytesAddress, MBReadLengthCmd1bytesAddress, MBReadLengthCmd2bytesAddress);

    private typeVcmdDefinition MBReadCmds = new typeVcmdDefinition(MBReadAdrModeCmd1bytesAddress, MBReadAdrModeCmd2bytesAddress, MBReadCmd1bytesAddress, MBReadCmd2bytesAddress);
    private static final byte[] MBReadAdrModeCmd1bytesAddress = new byte[]{(byte) 0x22, (byte) 0xAC, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00,
            (byte) 0xFF};
    private static final byte[] MBReadAdrModeCmd2bytesAddress = new byte[]{(byte) 0x2A, (byte) 0xAC, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00,
            (byte) 0xFF};
    private static final byte[] MBReadCmd1bytesAddress = new byte[]{(byte) 0x02, (byte) 0xAC, (byte) 0x02, (byte) 0x00, (byte) 0xFF};
    private static final byte[] MBReadCmd2bytesAddress = new byte[]{(byte) 0x0A, (byte) 0xAC, (byte) 0x02, (byte) 0x00, (byte) 0xFF};

    public static enum TypeVcmd {
        TypeVcmd_MBReadLength,
        TypeVcmd_MBRead,
        TypeVcmd_MBWrite,
    }

    protected Hashtable<TypeVcmd, typeVcmdDefinition> HashTypeVMBcmdDescr = new Hashtable<TypeVcmd, typeVcmdDefinition>() {
        {
            put(TypeVcmd.TypeVcmd_MBReadLength, MBReadLengthCmds);
            put(TypeVcmd.TypeVcmd_MBRead, MBReadCmds);
            put(TypeVcmd.TypeVcmd_MBWrite, MBWriteCmds);

        }
    };

    public int getCmdLength(TypeVcmd cmdType, boolean basedOnTwoBytesAddress, boolean addressModeNeeded) {
        // TODO Auto-generated method stub
        int ret = 0;
        if (HashTypeVMBcmdDescr.containsKey(cmdType)) {
            typeVcmdDefinition cmd = HashTypeVMBcmdDescr.get(cmdType);
            if (basedOnTwoBytesAddress) {
                ret = cmd.getCmd2BLength(addressModeNeeded);
            } else {
                ret = cmd.getCmd1BLength(addressModeNeeded);
            }


        }
        return ret;
    }



    public byte[] readSystemRegister(Tag myTag, SysFileLRHandler sysFileHnd, ST25DVRegisterTable target, boolean staticReg) {


        byte[] response = new byte[]{(byte) 0xAA};
        byte[] frame2BytesAddr;
        byte[] frame1ByteAddr;
        Byte register = -1;

        if (sysFileHnd.mST25DVRegister == null) return response;
        if (sysFileHnd.mST25DVRegister.isAKnownRegister(target)) {
            register = sysFileHnd.mST25DVRegister.getKnownRegister(target);
        } else {
            return response;
        }
        if (staticReg == false){
            if (!sysFileHnd.mST25DVRegister.isADynamicRegister(target)) {
                return response;
            }
        }

        if (sysFileHnd.isUidRequested()) {
            frame2BytesAddr = new byte[]{(byte) 0x2A,  TYPE5_ST_CMD_READ_STATIC_CFG, (byte) 0x02,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xFF};
            frame1ByteAddr = new byte[]{(byte) 0x22,  TYPE5_ST_CMD_READ_STATIC_CFG, (byte) 0x02,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xFF};
            // Pointer address to be updated
            // default MB to be set
            frame1ByteAddr[11] = (byte) (register);
            frame2BytesAddr[11] = (byte) (register);
            // Define cmd value
            // Default Dynamic register vlue
            //frame1ByteAddr[1] =  TYPE5_ST_CMD_READ_STATIC_CFG;
            //frame2BytesAddr[1] =  TYPE5_ST_CMD_READ_STATIC_CFG;

            frame1ByteAddr[1] = (byte) ((staticReg == true) ?  TYPE5_ST_CMD_READ_STATIC_CFG :  TYPE5_ST_CMD_READ_DYN_CFG);
            frame2BytesAddr[1] = (byte) ((staticReg == true) ?  TYPE5_ST_CMD_READ_STATIC_CFG :  TYPE5_ST_CMD_READ_DYN_CFG);


        } else {
            frame2BytesAddr = new byte[]{(byte) 0x0A,  TYPE5_ST_CMD_READ_STATIC_CFG, (byte) 0x02, (byte) 0xFF};
            frame1ByteAddr = new byte[]{(byte) 0x02,  TYPE5_ST_CMD_READ_STATIC_CFG, (byte) 0x02, (byte) 0xFF};
            // Pointer address to be updated
            // default MB to be set
            frame1ByteAddr[3] = (byte) (register);
            frame2BytesAddr[3] = (byte) (register);
            // Define cmd value
            // Default Dynamic register vlue
            //frame1ByteAddr[1] =  TYPE5_ST_CMD_READ_STATIC_CFG;
            //frame2BytesAddr[1] =  TYPE5_ST_CMD_READ_STATIC_CFG;
            frame1ByteAddr[1] = (byte) ((staticReg == true) ?  TYPE5_ST_CMD_READ_STATIC_CFG :  TYPE5_ST_CMD_READ_DYN_CFG);
            frame2BytesAddr[1] = (byte) ((staticReg == true) ?  TYPE5_ST_CMD_READ_STATIC_CFG :  TYPE5_ST_CMD_READ_DYN_CFG);
        }

        int counterLoop = 0;

        // INVENTORY HAS DETECTED DEVICE ... READING GET SYSTEM INFO

        // while ((response == null || response[0] == 1 || response[0] ==
        // (byte)0xAA) && cpt <= 1 && cpt_2bytes <= 2)
        while ((response == null || response[0] == (byte) 0xAA) && counterLoop <= 5) {
            try {
                counterLoop++;
                NfcV nfcvTag = NfcV.get(myTag);
                // Addressed mode added for MODAT531 phones needing Address
                // mode (2015-07-21)
                if (sysFileHnd.isUidRequested()) {
                    // 2 bytes address
                    frame2BytesAddr[2] = myTag.getId()[0];
                    frame2BytesAddr[3] = myTag.getId()[1];
                    frame2BytesAddr[4] = myTag.getId()[2];
                    frame2BytesAddr[5] = myTag.getId()[3];
                    frame2BytesAddr[6] = myTag.getId()[4];
                    frame2BytesAddr[7] = myTag.getId()[5];
                    frame2BytesAddr[8] = myTag.getId()[6];
                    frame2BytesAddr[9] = myTag.getId()[7];
                    // 1 bytes address
                    frame1ByteAddr[2] = myTag.getId()[0];
                    frame1ByteAddr[3] = myTag.getId()[1];
                    frame1ByteAddr[4] = myTag.getId()[2];
                    frame1ByteAddr[5] = myTag.getId()[3];
                    frame1ByteAddr[6] = myTag.getId()[4];
                    frame1ByteAddr[7] = myTag.getId()[5];
                    frame1ByteAddr[8] = myTag.getId()[6];
                    frame1ByteAddr[9] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();
                if (sysFileHnd.isBasedOnTwoBytesAddress() == true) {
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> readSystemRegister() request: " + Helper.ConvertHexByteArrayToString(frame2BytesAddr));

                    response = nfcvTag.transceive(frame2BytesAddr);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "readSystemRegister response: " + Helper.ConvertHexByteArrayToString(response));

                } else {
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> readSystemRegister() request: " + Helper.ConvertHexByteArrayToString(frame1ByteAddr));

                    response = nfcvTag.transceive(frame1ByteAddr);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "readSystemRegister response: " + Helper.ConvertHexByteArrayToString(response));
                }
                // nfcvTag.close(); //-> deleted 20130717 too long time
                // consuming
                if (response[0] == (byte) 0x00) {
                    return response;
                }
            } catch (Exception e) {
                Log.e("Exception","Exception " + e.getMessage());
                //ma.setBasedOnTwoBytesAddress(false);
            }
        }


        // Used for DEBUG : Log.i("NFCCOmmand", "Response Get System Info "
        // + Helper.ConvertHexByteArrayToString(response));
        return response;
    }


    public byte[] writeSystemRegister(Tag myTag, SysFileLRHandler sysFileHnd, ST25DVRegisterTable target, byte value, boolean staticReg) {

        byte[]  response = new byte[]{(byte) 0xAA};
        byte[]  frame2BytesAddr;
        byte[]  frame1ByteAddr;
        //boolean staticReg = true;
        byte    register = -1;

/*        if (DVRegisterFctDescr.containsKey(target)) {
            register = DVRegisterFctDescr.get(target);
        } else {
            return response;
        }*/
        if (sysFileHnd.mST25DVRegister == null) return response;
        if (sysFileHnd.mST25DVRegister.isAKnownRegister(target)) {
            register = sysFileHnd.mST25DVRegister.getKnownRegister(target);
        } else {
            return response;
        }

        if (staticReg == false){
            if (!sysFileHnd.mST25DVRegister.isADynamicRegister(target)) {
                return response;
            }
        }

        if (sysFileHnd.isUidRequested()) {
            frame2BytesAddr = new byte[]{(byte) 0x2A,  TYPE5_ST_CMD_WRITE_STATIC_CFG, (byte) 0x02,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xFF,
                    (byte) 0xFF};
            frame1ByteAddr = new byte[]{(byte) 0x22,  TYPE5_ST_CMD_WRITE_STATIC_CFG, (byte) 0x02,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xFF,
                    (byte) 0xFF};
            // Pointer address to be updated
            // default MB to be set
            frame1ByteAddr[11] = (byte) (register);
            frame2BytesAddr[11] = (byte) (register);
            // Define cmd value
            // Default Dynamic register vlue
            frame1ByteAddr[1] = (byte) ((staticReg == true) ?  TYPE5_ST_CMD_WRITE_STATIC_CFG :  TYPE5_ST_CMD_WRITE_DYN_CFG);
            frame2BytesAddr[1] = (byte) ((staticReg == true) ?  TYPE5_ST_CMD_WRITE_STATIC_CFG :  TYPE5_ST_CMD_WRITE_DYN_CFG);
            // Register Value
            frame1ByteAddr[12] = (byte) (value);
            frame2BytesAddr[12] = (byte) (value);


        } else {
            frame2BytesAddr = new byte[]{(byte) 0x0A,  TYPE5_ST_CMD_WRITE_STATIC_CFG, (byte) 0x02, (byte) 0xFF, (byte) 0xFF};
            frame1ByteAddr = new byte[]{(byte) 0x02,  TYPE5_ST_CMD_WRITE_STATIC_CFG, (byte) 0x02, (byte) 0xFF, (byte) 0xFF};
            // Pointer address to be updated
            // default MB to be set
            frame1ByteAddr[3] = (byte) (register);
            frame2BytesAddr[3] = (byte) (register);
            // Define cmd value
            // Default Dynamic register vlue
            frame1ByteAddr[1] = (byte) ((staticReg == true) ?  TYPE5_ST_CMD_WRITE_STATIC_CFG :  TYPE5_ST_CMD_WRITE_DYN_CFG);
            frame2BytesAddr[1] = (byte) ((staticReg == true) ?  TYPE5_ST_CMD_WRITE_STATIC_CFG :  TYPE5_ST_CMD_WRITE_DYN_CFG);

            frame1ByteAddr[4] = (byte) (value);
            frame2BytesAddr[4] = (byte) (value);
        }

        int counterLoop = 0;


        while ((response == null || response[0] == (byte) 0xAA) && counterLoop <= 5) {
            try {
                counterLoop++;
                NfcV nfcvTag = NfcV.get(myTag);
                // Addressed mode added for MODAT531 phones needing Address
                // mode (2015-07-21)
                if (sysFileHnd.isUidRequested()) {
                    // 2 bytes address
                    frame2BytesAddr[2] = myTag.getId()[0];
                    frame2BytesAddr[3] = myTag.getId()[1];
                    frame2BytesAddr[4] = myTag.getId()[2];
                    frame2BytesAddr[5] = myTag.getId()[3];
                    frame2BytesAddr[6] = myTag.getId()[4];
                    frame2BytesAddr[7] = myTag.getId()[5];
                    frame2BytesAddr[8] = myTag.getId()[6];
                    frame2BytesAddr[9] = myTag.getId()[7];
                    // 1 bytes address
                    frame1ByteAddr[2] = myTag.getId()[0];
                    frame1ByteAddr[3] = myTag.getId()[1];
                    frame1ByteAddr[4] = myTag.getId()[2];
                    frame1ByteAddr[5] = myTag.getId()[3];
                    frame1ByteAddr[6] = myTag.getId()[4];
                    frame1ByteAddr[7] = myTag.getId()[5];
                    frame1ByteAddr[8] = myTag.getId()[6];
                    frame1ByteAddr[9] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();
                if (sysFileHnd.isBasedOnTwoBytesAddress() == true) {
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> writeSystemRegister() request: " + Helper.ConvertHexByteArrayToString(frame2BytesAddr));

                    response = nfcvTag.transceive(frame2BytesAddr);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "writeSystemRegister response: " + Helper.ConvertHexByteArrayToString(response));
                } else {
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> writeSystemRegister() request: " + Helper.ConvertHexByteArrayToString(frame1ByteAddr));

                    response = nfcvTag.transceive(frame1ByteAddr);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "writeSystemRegister response: " + Helper.ConvertHexByteArrayToString(response));
                }
                // nfcvTag.close(); //-> deleted 20130717 too long time
                // consuming
                if (response[0] == (byte) 0x00) {
                    return response;
                }
            } catch (Exception e) {
                Log.e("Exception","Exception " + e.getMessage());
                //ma.setBasedOnTwoBytesAddress(false);
            }


        }
        // Used for DEBUG : Log.i("NFCCOmmand", "Response Get System Info "
        // + Helper.ConvertHexByteArrayToString(response));
        return response;
    }

    public byte[] setGPOConfig(Tag myTag, SysFileLRHandler sysFileHnd, byte gpoValue) {
        byte[] response = new byte[]{(byte) 0xAA};
        byte[] frame2BytesAddr;
        byte[] frame1ByteAddr;

        if (sysFileHnd.isUidRequested()) {
            frame2BytesAddr = new byte[]{(byte) 0x2A,  TYPE5_ST_CMD_MANAGE_GPO_CFG, (byte) 0x02,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xFF};
            frame1ByteAddr = new byte[]{(byte) 0x22,  TYPE5_ST_CMD_MANAGE_GPO_CFG, (byte) 0x02,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xFF};
            // Pointer address to be updated
            // default MB to be set
            frame1ByteAddr[11] = (byte) (gpoValue);
            frame2BytesAddr[11] = (byte) (gpoValue);

        } else {
            frame2BytesAddr = new byte[]{(byte) 0x0A,  TYPE5_ST_CMD_MANAGE_GPO_CFG, (byte) 0x02, (byte) 0xFF};
            frame1ByteAddr = new byte[]{(byte) 0x02,  TYPE5_ST_CMD_MANAGE_GPO_CFG, (byte) 0x02, (byte) 0xFF};
            // Pointer address to be updated
            // default MB to be set
            frame1ByteAddr[3] = (byte) (gpoValue);
            frame2BytesAddr[3] = (byte) (gpoValue);
        }

        int counterLoop = 0;
        while ((response == null || response[0] == (byte) 0xAA) && counterLoop <= 5) {
            try {
                counterLoop++;
                NfcV nfcvTag = NfcV.get(myTag);
                // Addressed mode added for MODAT531 phones needing Address
                // mode (2015-07-21)
                if (sysFileHnd.isUidRequested()) {
                    // 2 bytes address
                    frame2BytesAddr[2] = myTag.getId()[0];
                    frame2BytesAddr[3] = myTag.getId()[1];
                    frame2BytesAddr[4] = myTag.getId()[2];
                    frame2BytesAddr[5] = myTag.getId()[3];
                    frame2BytesAddr[6] = myTag.getId()[4];
                    frame2BytesAddr[7] = myTag.getId()[5];
                    frame2BytesAddr[8] = myTag.getId()[6];
                    frame2BytesAddr[9] = myTag.getId()[7];
                    // 1 bytes address
                    frame1ByteAddr[2] = myTag.getId()[0];
                    frame1ByteAddr[3] = myTag.getId()[1];
                    frame1ByteAddr[4] = myTag.getId()[2];
                    frame1ByteAddr[5] = myTag.getId()[3];
                    frame1ByteAddr[6] = myTag.getId()[4];
                    frame1ByteAddr[7] = myTag.getId()[5];
                    frame1ByteAddr[8] = myTag.getId()[6];
                    frame1ByteAddr[9] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();
                if (sysFileHnd.isBasedOnTwoBytesAddress() == true) {
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> setGPOConfig() request: " + Helper.ConvertHexByteArrayToString(frame2BytesAddr));

                    response = nfcvTag.transceive(frame2BytesAddr);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "setGPOConfig response: " + Helper.ConvertHexByteArrayToString(response));

                } else {
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> setGPOConfig() request: " + Helper.ConvertHexByteArrayToString(frame1ByteAddr));

                    response = nfcvTag.transceive(frame1ByteAddr);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "setGPOConfig response: " + Helper.ConvertHexByteArrayToString(response));
                }
                // nfcvTag.close(); //-> deleted 20130717 too long time
                // consuming
                if (response[0] == (byte) 0x00) {
                    return response;
                }
            } catch (Exception e) {
                Log.e("Exception","Exception " + e.getMessage());
                //ma.setBasedOnTwoBytesAddress(false);
            }
        }


        // Used for DEBUG : Log.i("NFCCOmmand", "Response Get System Info "
        // + Helper.ConvertHexByteArrayToString(response));
        return response;
    }

}
