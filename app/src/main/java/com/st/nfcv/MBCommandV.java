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

import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Trace;
import android.util.Log;

import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.util.DebugUtility;


public class MBCommandV extends NFCCommandVExtended {


    static final String TAG = "MBCommandV";

    private int mMaxTransceiveBufferAvailableSize;
    public byte[] mBlockAnswer = null;


    public MBCommandV(int maxBufferLength) {
        super("");
        this.mMaxTransceiveBufferAvailableSize = maxBufferLength;
    }


    public byte[] getBlockAnswer() {
        return mBlockAnswer;
    }

    public byte[] getMBConfig(Tag myTag, SysFileLRHandler sysFileHnd, boolean staticReg) {


        byte[] response = new byte[]{(byte) 0xAA};
        byte[] frame;


        if (sysFileHnd.isUidRequested()) {
            frame = new byte[]{(byte) 0x22,  TYPE5_ST_CMD_READ_DYN_CFG, (byte) 0x02,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xFF};
            frame[11] = (byte) 0x0D;

        } else {

            frame = new byte[]{(byte) 0x02,  TYPE5_ST_CMD_READ_DYN_CFG, (byte) 0x02, (byte) 0xFF};
            frame[3] = (byte) 0x0D;
        }

        if (staticReg == true) {
            frame[1] =  TYPE5_ST_CMD_READ_STATIC_CFG;
        } else {
            frame[1] =  TYPE5_ST_CMD_READ_DYN_CFG;
        }

        int counterLoop = 1;
        //NfcV nfcvTag = NfcV.get(myTag);
        NfcV nfcvTag = NFCApplication.getApplication().getCurrentTag().getNfcvTagMB();
        if (nfcvTag != null) {
        while ((response == null || response[0] == (byte) 0xAA) && counterLoop > 0) {
            counterLoop--;

            if (sysFileHnd.isUidRequested()) {
                frame[2] = myTag.getId()[0];
                frame[3] = myTag.getId()[1];
                frame[4] = myTag.getId()[2];
                frame[5] = myTag.getId()[3];
                frame[6] = myTag.getId()[4];
                frame[7] = myTag.getId()[5];
                frame[8] = myTag.getId()[6];
                frame[9] = myTag.getId()[7];
            }
            try {
                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> getMBConfig() request: " + Helper.ConvertHexByteArrayToString(frame));

                response = nfcvTag.transceive(frame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "getMBConfig response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00) {
                    return response;
                }
            } catch (Exception e) {
                Log.e(TAG, "getMBConfig " + e.getMessage());
            }
        }}
        return null;
    }


    public byte[] setMBConfig(Tag myTag, SysFileLRHandler sysFileHnd, boolean staticReg, boolean enableMB) {

        byte[] response = new byte[]{(byte) 0xAA};
        byte[] frame;


        if (sysFileHnd.isUidRequested()) {

            frame = new byte[]{(byte) 0x22, (byte) 0xA1, (byte) 0x02,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xFF,
                    (byte) 0xFF};
            // Pointer address to be updated
            // default MB to be set
            frame[11] = (byte) 0x0D;
            // Define cmd value
            // Default Dynamic register vlue

            // Register Value
            frame[12] = (byte) ((enableMB == true) ? 0x01 : 0x00);


        } else {

            frame = new byte[]{(byte) 0x02, (byte) 0xA1, (byte) 0x02, (byte) 0xFF, (byte) 0xFF};
            // Pointer address to be updated
            // default MB to be set
            frame[3] = (byte) 0x0D;
            frame[4] = (byte) ((enableMB == true) ? 0x01 : 0x00);

        }

        if (staticReg == true) {
            frame[1] =  TYPE5_ST_CMD_WRITE_STATIC_CFG;
        } else {
            frame[1] =  TYPE5_ST_CMD_WRITE_DYN_CFG;
        }


        int counterLoop = 5;

        // INVENTORY HAS DETECTED DEVICE ... READING GET SYSTEM INFO
        //NfcV nfcvTag = NfcV.get(myTag);
        NfcV nfcvTag = NFCApplication.getApplication().getCurrentTag().getNfcvTagMB();

        // while ((response == null || response[0] == 1 || response[0] ==
        // (byte)0xAA) && cpt <= 1 && cpt_2bytes <= 2)
        if (nfcvTag != null) {
            while ((response == null || response[0] == (byte) 0xAA) && counterLoop > 0) {

                counterLoop--;
                if (sysFileHnd.isUidRequested()) {
                    // 2 bytes address
                    // 1 bytes address
                    frame[2] = myTag.getId()[0];
                    frame[3] = myTag.getId()[1];
                    frame[4] = myTag.getId()[2];
                    frame[5] = myTag.getId()[3];
                    frame[6] = myTag.getId()[4];
                    frame[7] = myTag.getId()[5];
                    frame[8] = myTag.getId()[6];
                    frame[9] = myTag.getId()[7];
                }

                try {

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> setMBConfig() request: " + Helper.ConvertHexByteArrayToString(frame));

                    response = nfcvTag.transceive(frame);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "setMBConfig response: " + Helper.ConvertHexByteArrayToString(response));

                    if (response[0] == (byte) 0x00) {
                        return response;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "setMBConfig " + e.getMessage());
                }
            }
        }
        return null;
    }



        public byte[] writeMBMsg (Tag myTag, SysFileLRHandler sysFileHnd,byte[] msg){


            byte[] response = new byte[]{(byte) 0xAA};
            byte[] frame;

            typeVcmdDefinition Vcmd = HashTypeVMBcmdDescr.get(TypeVcmd.TypeVcmd_MBWrite);

            if (sysFileHnd.isUidRequested()) {
                frame = Vcmd.getCmdAdrMode1B();
                frame[11] = (byte) ((msg.length - 1) & 0xFF);

            } else {
                frame = Vcmd.getCmd1B();
                frame[3] = (byte) ((msg.length - 1) & 0xFF);
            }

            int counterLoop = 5;

            //NfcV nfcvTag = NfcV.get(myTag);
            NfcV nfcvTag = NFCApplication.getApplication().getCurrentTag().getNfcvTagMB();
            if (nfcvTag != null) {
                while ((response == null || response[0] == (byte) 0xAA) && counterLoop > 0) {
                    counterLoop--;

                    if (sysFileHnd.isUidRequested()) {
                        addTagIdToCmd(frame, myTag.getId());
                    }

                    int dataToWrite = msg.length;


                    byte[] fullcmd = new byte[dataToWrite + frame.length];
                    System.arraycopy(frame, 0, fullcmd, 0, frame.length);
                    System.arraycopy(msg, 0, fullcmd, frame.length, msg.length);
                    try {
                        if (DebugUtility.printNfcCommands)
                            Log.v(TAG, "==> writeMBMsg request: " + Helper.ConvertHexByteArrayToString(fullcmd));

                        response = nfcvTag.transceive(fullcmd);

                        if (DebugUtility.printNfcCommands)
                            Log.v(TAG, "writeMBMsg response: " + Helper.ConvertHexByteArrayToString(response));

                        return response;

                    } catch (Exception e) {
                        Log.e(TAG, "Error writeMBMsg: " + e.getMessage());
                        return null;
                    }
                }
            }
            return null;
        }


        public byte[] getMBMsgLength (Tag myTag, SysFileLRHandler sysFileHnd){


            byte[] response = new byte[]{(byte) 0xAA};
            typeVcmdDefinition Vcmd = HashTypeVMBcmdDescr.get(TypeVcmd.TypeVcmd_MBReadLength);


            byte[] frame;

            if (sysFileHnd.isUidRequested()) {
                frame = Vcmd.getCmdAdrMode1B();

            } else {
                frame = Vcmd.getCmd1B();
            }

            int loopCounter = 5;

            // INVENTORY HAS DETECTED DEVICE ... READING GET SYSTEM INFO

            //NfcV nfcvTag = NfcV.get(myTag);
            NfcV nfcvTag = NFCApplication.getApplication().getCurrentTag().getNfcvTagMB();
            if (nfcvTag != null) {
                while ((response == null || response[0] == (byte) 0xAA) && loopCounter > 0) {

                    loopCounter--;
                    if (sysFileHnd.isUidRequested()) {

                        // 1 bytes address
                        frame[2] = myTag.getId()[0];
                        frame[3] = myTag.getId()[1];
                        frame[4] = myTag.getId()[2];
                        frame[5] = myTag.getId()[3];
                        frame[6] = myTag.getId()[4];
                        frame[7] = myTag.getId()[5];
                        frame[8] = myTag.getId()[6];
                        frame[9] = myTag.getId()[7];
                    }

                    try {

                        if (DebugUtility.printNfcCommands)
                            Log.v(TAG, "==> getMBMsgLength request: " + Helper.ConvertHexByteArrayToString(frame));

                        response = nfcvTag.transceive(frame);

                        if (DebugUtility.printNfcCommands)
                            Log.v(TAG, "getMBMsgLength response: " + Helper.ConvertHexByteArrayToString(response));


                        if (response[0] == (byte) 0x00) {
                            return response;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error getMBMsgLength: " + e.getMessage());
                    }
                }
            }
            return null;
        }


    public byte[] readMBMsg(Tag myTag, SysFileLRHandler sysFileHnd, byte offset, byte msgLength) {
        // Patch cpt = 5 cpt = 0 workaround ......


        byte[] response = new byte[]{(byte) 0xAA};
        typeVcmdDefinition Vcmd = HashTypeVMBcmdDescr.get(TypeVcmd.TypeVcmd_MBRead);
        byte[] frame;

        if (sysFileHnd.isUidRequested()) {
            frame = Vcmd.getCmdAdrMode1B();
            // Pointer address to be updated
            // default MB to be set
            frame[11] = (byte) ((offset) & 0xFF);
            frame[12] = (byte) ((msgLength - 1) & 0xFF);

            // Define cmd value

        } else {
            frame = Vcmd.getCmd1B();
            // Pointer address to be updated
            // default MB to be set
            frame[3] = (byte) ((offset) & 0xFF);
            frame[4] = (byte) ((msgLength - 1) & 0xFF);

        }


        NfcV nfcvTag = NFCApplication.getApplication().getCurrentTag().getNfcvTagMB();
        //NfcV nfcvTag = NfcV.get(myTag);
        if (nfcvTag != null) {
            if (nfcvTag.getMaxTransceiveLength() < msgLength) {
                Log.v(TAG, "No way to much data to transceive");
                return null;
            }
            if (sysFileHnd.isUidRequested()) {
                // 1 bytes address
                frame[2] = myTag.getId()[0];
                frame[3] = myTag.getId()[1];
                frame[4] = myTag.getId()[2];
                frame[5] = myTag.getId()[3];
                frame[6] = myTag.getId()[4];
                frame[7] = myTag.getId()[5];
                frame[8] = myTag.getId()[6];
                frame[9] = myTag.getId()[7];
            }

            if (nfcvTag.isConnected()) {
                try {

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> readMBMsg request: " + Helper.ConvertHexByteArrayToString(frame));

                    response = nfcvTag.transceive(frame);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "readMBMsg response: " + Helper.ConvertHexByteArrayToString(response));

                    return response;

                } catch (Exception e) {
                    Log.e(TAG, "Error readMBMsg: " + e.getMessage());
                }
            }
        }


        return null;

    }

    public int configureMB(boolean staticReg, boolean enableMB) {


        int ret = 0;
        boolean ping = true;


        int loopCounter = 5;

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


        if (ping == true) {

            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());


            mBlockAnswer = null;
            loopCounter = 10;
            while ((mBlockAnswer == null || mBlockAnswer[0] == 1) && loopCounter > 0) {
                mBlockAnswer = setMBConfig(currentTag.getTag(), sysHDL, staticReg, enableMB);
                loopCounter--;
            }

            if (mBlockAnswer == null || mBlockAnswer[0] == -1) {
                ret = currentTag.reportActionStatus("ConfigureMB " + (staticReg == true ? "static ":"dynamic " + " ERROR read (No tag answer) "), -1);
            } else if ((mBlockAnswer[0] & 0x01) == 0) {
                ret = currentTag.reportActionStatus("ConfigureMB " + (staticReg == true ? "static ":"dynamic ") + " succeeded", 0);
            }else if ((mBlockAnswer[0] & 0x01) == 1 && mBlockAnswer.length >=2) {
                ret = currentTag.reportActionStatusTransparent("ConfigureMB " + (staticReg == true ? "static ":"dynamic ") + " failed", mBlockAnswer[1]);
            } else {
                ret = currentTag.reportActionStatusTransparent("ConfigureMB " + (staticReg == true ? "static ":"dynamic ") + " failed with unknown error", -1);
            }
        } else {
            ret = currentTag.reportActionStatusTransparent("Tag not on the field...", -1);
        }
        return ret;
    }

    public int MBReadCfgBasicOp(boolean staticReg) {
        int ret = 0;
        boolean ping = true;

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long loopCounter = 1;

        if (ping) {

            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            mBlockAnswer = null;
            loopCounter = 5;

            while ((mBlockAnswer == null || mBlockAnswer[0] == 1) && loopCounter > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Trace.beginSection("MBReadCfgBasicOp ");
                }

                mBlockAnswer = getMBConfig(currentTag.getTag(), sysHDL, staticReg);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Trace.endSection();
                }


                loopCounter--;
            }

            if (mBlockAnswer == null || mBlockAnswer[0] == -1) {
                ret = currentTag.reportActionStatusTransparent("MBReadCfgBasicOp ERROR read (No tag answer) ", -1);
            } else if ((mBlockAnswer[0] & 0x01) == 0) {
                ret = currentTag.reportActionStatusTransparent("MBReadCfgBasicOp Command answered ok ", 0);
            }else if ((mBlockAnswer[0] & 0x01) == 1 && mBlockAnswer.length >=2) {
                ret = currentTag.reportActionStatusTransparent("MBReadCfgBasicOp ERROR : ", mBlockAnswer[1]);
            } else {
                ret = currentTag.reportActionStatusTransparent("MBReadCfgBasicOp Error unknown ", -1);
            }
            //
        } else {
            ret = currentTag.reportActionStatusTransparent("Tag not on the field...", -1);
        }
        return ret;
    }


    public int writeMBMsg(byte[] message) {

        int ret = 0;
        boolean ping = true;

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long loopCounter = 5;

/*        while ((ping = currentTag.pingTag()) != true && loopCounter > 0) {

            try {
                Thread.sleep(10);
                loopCounter--;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/


        if (ping) {

            SysFileLRHandler sysHnd = (SysFileLRHandler) (currentTag.getSYSHandler());

            mBlockAnswer = null;
            loopCounter = 5;

            while ((mBlockAnswer == null || mBlockAnswer[0] == 1) && loopCounter > 0) {
                mBlockAnswer = writeMBMsg(currentTag.getTag(), sysHnd, message);
                loopCounter--;
            }

            if (mBlockAnswer == null || mBlockAnswer[0] == -1) {
                ret = currentTag.reportActionStatusTransparent("writeMBMsg ERROR write (No tag answer) ", -1);
            } else if ((mBlockAnswer[0] & 0x01) == 0) {
                ret = currentTag.reportActionStatusTransparent("writeMBMsg command succeeded", 0);
            }else if ((mBlockAnswer[0] & 0x01) == 1 && mBlockAnswer.length >=2) {
                ret = currentTag.reportActionStatusTransparent("writeMBMsg ERROR : ", mBlockAnswer[1]);
            } else {
                ret = currentTag.reportActionStatusTransparent("writeMBMsg Error unknown ", -1);
            }
            //
        } else {
            ret = currentTag.reportActionStatusTransparent("writeMBMsg Tag not on the field... or Message > 256", -1);
        }
        return ret;
    }

    public int getMBMsgLength() {

        int ret = 0;
        boolean ping = true;

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        long loopCounter = 5;


        while ((ping = currentTag.pingTag()) != true && loopCounter > 0) {

            try {
                Thread.sleep(10);
                loopCounter--;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.e(TAG,
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ping) {
            Log.d(TAG, " getMBMsgLength Action");
            //
            SysFileLRHandler sysHnd = (SysFileLRHandler) (currentTag.getSYSHandler());

            mBlockAnswer = null;
            loopCounter = 5;
            while ((mBlockAnswer == null || mBlockAnswer[0] == 1) && loopCounter > 0) {
                mBlockAnswer = getMBMsgLength(currentTag.getTag(), sysHnd);
                loopCounter--;
            }

            if (mBlockAnswer == null || mBlockAnswer[0] == -1) {
                ret = currentTag.reportActionStatusTransparent("getMBMsgLength ERROR read (No tag answer) ", -1);
            } else if ((mBlockAnswer[0] & 0x01) == 0) {
                ret = currentTag.reportActionStatusTransparent("getMBMsgLength Command answered ok ", 0);
            } else if ((mBlockAnswer[0] & 0x01) == 1 && mBlockAnswer.length >=2) {
                ret = currentTag.reportActionStatusTransparent("getMBMsgLength ERROR read : ", mBlockAnswer[1]);
            } else {
                ret = currentTag.reportActionStatusTransparent("getMBMsgLength Error unknown ", -1);
            }
            //
        } else {
            ret = currentTag.reportActionStatusTransparent("getMBMsgLength Tag not on the field...", -1);
        }
        return ret;
    }

    public int readMBMsg(byte msgLength) {

        int ret = 0;
        boolean ping = true;

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        long loopCounter = 5;

        while ((ping = currentTag.pingTag()) != true && loopCounter > 0) {

            try {
                Thread.sleep(10);
                loopCounter--;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (ping) {
            SysFileLRHandler sysHnd = (SysFileLRHandler) (currentTag.getSYSHandler());
            mBlockAnswer = null;

            int maxTransceiveLength = sysHnd.getMaxTransceiveLength();

            int lengthToRead = (int) (msgLength & 0xFF);

            if (lengthToRead > maxTransceiveLength) {

                if (lengthToRead - maxTransceiveLength > 1) {
                } else {
                    maxTransceiveLength--;
                }

                byte[] datachunk1;
                //datachunk1 = readMBMsg(currentTag.getTag(), sysHnd, (byte) 0x00, (byte) ((byte) (maxTransceiveLength - 2) & 0xFF));
                datachunk1 = readMBMsg(currentTag.getTag(), sysHnd, (byte) 0x00, (byte) ((byte) (maxTransceiveLength) & 0xFF));

                byte[] datachunk2;
                //datachunk2 = readMBMsg(currentTag.getTag(), sysHnd, (byte) ((byte) (maxTransceiveLength - 2) & 0xFF), (byte) ((lengthToRead - (maxTransceiveLength - 2)) & 0xFF));
                datachunk2 = readMBMsg(currentTag.getTag(), sysHnd, (byte) ((byte) (maxTransceiveLength) & 0xFF), (byte) ((lengthToRead - (maxTransceiveLength)) & 0xFF));

                if (datachunk1 != null && datachunk2 != null) {
                    if (((datachunk1[0] & 0x01) == 0) && (datachunk2[0] & 0x01) == 0) {
                        // data has been retreived .... Create the output buffer
                        mBlockAnswer = new byte[lengthToRead + 1];
                        System.arraycopy(datachunk1, 0, mBlockAnswer, 0, datachunk1.length);
                        System.arraycopy(datachunk2, 1, mBlockAnswer, datachunk1.length, datachunk2.length-1);

                    } else {
                        // An issue occured
                        mBlockAnswer = new byte[2];
                        mBlockAnswer[0] = (byte) (datachunk1[0] | datachunk2[0]);
                        mBlockAnswer[1] = (byte) (datachunk1[1] | datachunk2[1]);
                    }

                } else {
                    mBlockAnswer = new byte[2];
                    // An issue occured
                    if (datachunk1 != null) {
                        mBlockAnswer[0] = (byte) (datachunk1[0] );
                        mBlockAnswer[1] = (byte) (datachunk1[1] );
                    }
                    if (datachunk2 != null) {
                        mBlockAnswer[0] = (byte) (mBlockAnswer[0] | datachunk2[0]);
                        mBlockAnswer[1] = (byte) (mBlockAnswer[1] | datachunk2[1]);
                    }

                }

            } else {
                loopCounter = 10;
                while ((mBlockAnswer == null || mBlockAnswer[0] == 1) && loopCounter > 0) {
                    mBlockAnswer = readMBMsg(currentTag.getTag(), sysHnd, (byte) 0x00, msgLength);
                    loopCounter--;
                }

            }

            if (mBlockAnswer == null || mBlockAnswer[0] == -1) {
                ret = currentTag.reportActionStatusTransparent("readMBMsg ERROR read (No tag answer) ", -1);
            } else if ((mBlockAnswer[0] & 0x01) == 0) {
                ret = currentTag.reportActionStatusTransparent("readMBMsg Command answered ok ", 0);
            }else if ((mBlockAnswer[0] & 0x01) == 1 && mBlockAnswer.length >=2) {
                ret = currentTag.reportActionStatusTransparent("readMBMsg ERROR read : ", mBlockAnswer[1]);
            } else {
                ret = currentTag.reportActionStatusTransparent("readMBMsg Error unknown ", -1);
            }
            //
        } else {
            ret = currentTag.reportActionStatusTransparent("readMBMsg Tag not on the field...", -1);
        }

        return ret;
    }

    private void addTagIdToCmd(byte[] cmd, byte[] tagId) {
        cmd[2] = tagId[0];
        cmd[3] = tagId[1];
        cmd[4] = tagId[2];
        cmd[5] = tagId[3];
        cmd[6] = tagId[4];
        cmd[7] = tagId[5];
        cmd[8] = tagId[6];
        cmd[9] = tagId[7];

    }

}
