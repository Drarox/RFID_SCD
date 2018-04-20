/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1823 $
  * Revision of last commit    :  $Rev: 1823 $
  * Date of last commit     :  $Date: 2016-03-31 19:43:31 +0200 (Thu, 31 Mar 2016) $
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
//import android.util.Log;
import android.util.Log;

import com.st.util.DebugUtility;

public class NFCCommandVLR extends NFCCommandV {
    String _modelName = null;
    static final String TAG = "NFCVCommand";

    // STMicroelectronics custom commands
    protected static final byte TYPE5_ST_CMD_READ_EH_CONFIG = (byte) 0xA0;     // Read Energy Harvesting config
    protected static final byte TYPE5_ST_CMD_WRITE_EH_CONFIG = (byte) 0xA1;     // Write Energy Harvesting config
    protected static final byte TYPE5_ST_CMD_SET_RESET_EH_CFG = (byte) 0xA2;     // Set Reset Energy Harvesting config
    protected static final byte TYPE5_ST_CMD_CHECK_EH_ENABLE = (byte) 0xA3;     // Check Energy Harvesting enable status
    protected static final byte TYPE5_ST_CMD_WRITE_DOC_CONFIG = (byte) 0xA4;     // Write doc config
    protected static final byte TYPE5_ST_CMD_WRITE_PASSWORD = (byte) 0xB1;     // Write password
    protected static final byte TYPE5_ST_CMD_LOCK_SECTOR = (byte) 0xB2;     // Lock sector
    protected static final byte TYPE5_ST_CMD_PRESENT_PASSWORD = (byte) 0xB3;     // Present password

    public NFCCommandVLR(String _modelName) {
        super();
        this._modelName = _modelName;
    }


    //***********************************************************************/
    //* the function send an WriteSingle command (0x0A 0x21) || (0x02 0x21)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  DataToWrite : {0x04 0x14 0xFF 0xB2}
    //* the function will write {0x04 0x14 0xFF 0xB2} at the address 0002
    //***********************************************************************/
    //public static byte[] SendPresentPasswordCommand (Tag myTag, boolean isUidRequested, byte PasswordNumber, byte[] PasswordData)
    public byte[] SendPresentPasswordCommand(Tag myTag, boolean isAddressModeNeeded, byte PasswordNumber, byte[] PasswordData) {
        byte[] response = new byte[]{(byte) 0xFF};
        byte[] PresentPasswordFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            byte[] PresentPasswordFrame_block1 = new byte[]{(byte) 0x22, TYPE5_ST_CMD_PRESENT_PASSWORD, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            PresentPasswordFrame = new byte[PresentPasswordFrame_block1.length + 1 + PasswordData.length];
            // cmd
            System.arraycopy(PresentPasswordFrame_block1, 0, PresentPasswordFrame, 0, PresentPasswordFrame_block1.length);
            // pwd number
            PresentPasswordFrame[11] = PasswordNumber;

            // Password
            System.arraycopy(PasswordData, 0, PresentPasswordFrame, PresentPasswordFrame_block1.length + 1, PasswordData.length);

            //PresentPasswordFrame = new byte[]{(byte) 0x22,  TYPE5_ST_CMD_PRESENT_PASSWORD, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, PasswordNumber, PasswordData[0], PasswordData[1], PasswordData[2], PasswordData[3]};
        } else {
            byte[] PresentPasswordFrame_block1 = new byte[]{(byte) 0x02, TYPE5_ST_CMD_PRESENT_PASSWORD, (byte) 0x02};
            PresentPasswordFrame = new byte[PresentPasswordFrame_block1.length + 1 + PasswordData.length];
            // cmd
            System.arraycopy(PresentPasswordFrame_block1, 0, PresentPasswordFrame, 0, PresentPasswordFrame_block1.length);
            // pwd number
            PresentPasswordFrame[3] = PasswordNumber;
            // Password
            System.arraycopy(PasswordData, 0, PresentPasswordFrame, PresentPasswordFrame_block1.length + 1, PasswordData.length);
            //PresentPasswordFrame = new byte[]{(byte) 0x02,  TYPE5_ST_CMD_PRESENT_PASSWORD, (byte) 0x02, PasswordNumber, PasswordData[0], PasswordData[1], PasswordData[2], PasswordData[3]};
        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    PresentPasswordFrame[3] = myTag.getId()[0];
                    PresentPasswordFrame[4] = myTag.getId()[1];
                    PresentPasswordFrame[5] = myTag.getId()[2];
                    PresentPasswordFrame[6] = myTag.getId()[3];
                    PresentPasswordFrame[7] = myTag.getId()[4];
                    PresentPasswordFrame[8] = myTag.getId()[5];
                    PresentPasswordFrame[9] = myTag.getId()[6];
                    PresentPasswordFrame[10] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendPresentPasswordCommand() request: " + Helper.ConvertHexByteArrayToString(PresentPasswordFrame));

                response = nfcvTag.transceive(PresentPasswordFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendPresentPasswordCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i("*******", "**SUCCESS** Write Data " + DataToWrite[0] +" "+ DataToWrite[1] +" "+ DataToWrite[2] +" "+ DataToWrite[3] + " at address " +  (byte)StartAddress[0] +" "+ (byte)StartAddress[1]);
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e(TAG, "Send password command  " + errorOccured);
                if (errorOccured == 2) {
                    Log.e("Exception","Exception " + e.getMessage());
                    Log.e("WRITE", "**ERROR SEND PASSWORD**");
                    return response;
                }
            }
        }
        return response;
    }

    //***********************************************************************/
    //* the function send an WriteSingle command (0x0A 0x21) || (0x02 0x21)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  DataToWrite : {0x04 0x14 0xFF 0xB2}
    //* the function will write {0x04 0x14 0xFF 0xB2} at the address 0002
    //***********************************************************************/
    //public static byte[] SendWritePasswordCommand (Tag myTag, boolean isUidRequested, byte PasswordNumber, byte[] PasswordData)
    public byte[] SendWritePasswordCommand(Tag myTag, boolean isAddressModeNeeded, byte PasswordNumber, byte[] PasswordData) {
        byte[] response = new byte[]{(byte) 0xFF};
        byte[] PresentPasswordFrame;


        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            byte[] PresentPasswordFrame_block1 = new byte[]{(byte) 0x22, TYPE5_ST_CMD_WRITE_PASSWORD, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            PresentPasswordFrame = new byte[PresentPasswordFrame_block1.length + 1 + PasswordData.length];
            // cmd
            System.arraycopy(PresentPasswordFrame_block1, 0, PresentPasswordFrame, 0, PresentPasswordFrame_block1.length);
            // pwd number
            PresentPasswordFrame[11] = PasswordNumber;

            // Password
            System.arraycopy(PasswordData, 0, PresentPasswordFrame, PresentPasswordFrame_block1.length + 1, PasswordData.length);

//                 PresentPasswordFrame = new byte[]{(byte) 0x22,  TYPE5_ST_CMD_WRITE_PASSWORD, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//                         PasswordNumber, PasswordData[0], PasswordData[1], PasswordData[2], PasswordData[3]};
        } else {
            byte[] PresentPasswordFrame_block1 = new byte[]{(byte) 0x02, TYPE5_ST_CMD_WRITE_PASSWORD, (byte) 0x02};
            PresentPasswordFrame = new byte[PresentPasswordFrame_block1.length + 1 + PasswordData.length];
            // cmd
            System.arraycopy(PresentPasswordFrame_block1, 0, PresentPasswordFrame, 0, PresentPasswordFrame_block1.length);
            // pwd number
            PresentPasswordFrame[3] = PasswordNumber;
            // Password
            System.arraycopy(PasswordData, 0, PresentPasswordFrame, PresentPasswordFrame_block1.length + 1, PasswordData.length);

//                 PresentPasswordFrame = new byte[]{(byte) 0x02,  TYPE5_ST_CMD_WRITE_PASSWORD, (byte) 0x02,
//                         PasswordNumber, PasswordData[0], PasswordData[1], PasswordData[2], PasswordData[3]};
        }

        // Add Password number + Password according to length

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    PresentPasswordFrame[3] = myTag.getId()[0];
                    PresentPasswordFrame[4] = myTag.getId()[1];
                    PresentPasswordFrame[5] = myTag.getId()[2];
                    PresentPasswordFrame[6] = myTag.getId()[3];
                    PresentPasswordFrame[7] = myTag.getId()[4];
                    PresentPasswordFrame[8] = myTag.getId()[5];
                    PresentPasswordFrame[9] = myTag.getId()[6];
                    PresentPasswordFrame[10] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendWritePasswordCommand() request: " + Helper.ConvertHexByteArrayToString(PresentPasswordFrame));

                response = nfcvTag.transceive(PresentPasswordFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendWritePasswordCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i("*******", "**SUCCESS** Write Data " + DataToWrite[0] +" "+ DataToWrite[1] +" "+ DataToWrite[2] +" "+ DataToWrite[3] + " at address " +  (byte)StartAddress[0] +" "+ (byte)StartAddress[1]);
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e(TAG, "Send write password command  " + errorOccured);
                if (errorOccured == 2) {
                    Log.e("Exception","Exception " + e.getMessage());
                    Log.e("WRITE", "**ERROR SEND WRITE PASSWORD**");
                    return response;
                }
            }
        }
        return response;
    }

    //***********************************************************************/
    //* the function send an WriteSingle command (0x0A 0x21) || (0x02 0x21)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  DataToWrite : {0x04 0x14 0xFF 0xB2}
    //* the function will write {0x04 0x14 0xFF 0xB2} at the address 0002
    //***********************************************************************/
//     public static byte[] SendLockSectorCommand (Tag myTag, boolean isUidRequested, boolean isBasedOnTwoBytesAddress, byte[] SectorNumberAddress, byte LockSectorByte)
    public byte[] SendLockSectorCommand(Tag myTag, boolean isAddressModeNeeded, boolean isBasedOnTwoBytesAddress, byte[] SectorNumberAddress, byte LockSectorByte) {
        byte[] response = new byte[]{(byte) 0xFF};
        byte[] LockSectorFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            if (isBasedOnTwoBytesAddress)
                LockSectorFrame = new byte[]{(byte) 0x2A, TYPE5_ST_CMD_LOCK_SECTOR, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, SectorNumberAddress[1], SectorNumberAddress[0], LockSectorByte};
            else
                LockSectorFrame = new byte[]{(byte) 0x22, TYPE5_ST_CMD_LOCK_SECTOR, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, SectorNumberAddress[1], LockSectorByte};
        } else {
            if (isBasedOnTwoBytesAddress)
                LockSectorFrame = new byte[]{(byte) 0x0A, TYPE5_ST_CMD_LOCK_SECTOR, (byte) 0x02, SectorNumberAddress[1], SectorNumberAddress[0], LockSectorByte};
            else
                LockSectorFrame = new byte[]{(byte) 0x02, TYPE5_ST_CMD_LOCK_SECTOR, (byte) 0x02, SectorNumberAddress[1], LockSectorByte};
        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    LockSectorFrame[3] = myTag.getId()[0];
                    LockSectorFrame[4] = myTag.getId()[1];
                    LockSectorFrame[5] = myTag.getId()[2];
                    LockSectorFrame[6] = myTag.getId()[3];
                    LockSectorFrame[7] = myTag.getId()[4];
                    LockSectorFrame[8] = myTag.getId()[5];
                    LockSectorFrame[9] = myTag.getId()[6];
                    LockSectorFrame[10] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendLockSectorCommand() request: " + Helper.ConvertHexByteArrayToString(LockSectorFrame));

                response = nfcvTag.transceive(LockSectorFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendLockSectorCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i("*******", "**SUCCESS** Write Data " + DataToWrite[0] +" "+ DataToWrite[1] +" "+ DataToWrite[2] +" "+ DataToWrite[3] + " at address " +  (byte)StartAddress[0] +" "+ (byte)StartAddress[1]);
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e(TAG, "Lock sector command  " + errorOccured);
                if (errorOccured == 2) {
                    Log.e("Exception","Exception " + e.getMessage());
                    Log.e("WRITE", "**ERROR LOCK SECTOR**");
                    return response;
                }
            }
        }
        return response;
    }

    // EH cmd
    //***********************************************************************/
    //* the function send an ReadSingle command (0x0A 0x20) || (0x02 0x20)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/
//     public static byte[] SendReadEHconfigCommand (Tag myTag, boolean isUidRequested)
    public byte[] SendReadEHconfigCommand(Tag myTag, boolean isAddressModeNeeded) {
        byte[] response = new byte[]{(byte) 0x0A};
        byte[] ReadEHconfigFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            ReadEHconfigFrame = new byte[]{(byte) 0x22, TYPE5_ST_CMD_READ_EH_CONFIG, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        } else {
            ReadEHconfigFrame = new byte[]{(byte) 0x02, TYPE5_ST_CMD_READ_EH_CONFIG, (byte) 0x02};
        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    ReadEHconfigFrame[3] = myTag.getId()[0];
                    ReadEHconfigFrame[4] = myTag.getId()[1];
                    ReadEHconfigFrame[5] = myTag.getId()[2];
                    ReadEHconfigFrame[6] = myTag.getId()[3];
                    ReadEHconfigFrame[7] = myTag.getId()[4];
                    ReadEHconfigFrame[8] = myTag.getId()[5];
                    ReadEHconfigFrame[9] = myTag.getId()[6];
                    ReadEHconfigFrame[10] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendReadEHconfigCommand() request: " + Helper.ConvertHexByteArrayToString(ReadEHconfigFrame));

                response = nfcvTag.transceive(ReadEHconfigFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendReadEHconfigCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i(TAG, "SENDED Frame : " + Helper.ConvertHexByteArrayToString(ReadSingleBlockFrame));
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e(TAG, "SendReadEHconfigCommand" + Helper.ConvertHexByteArrayToString(response));
                if (errorOccured == 2) {
                    Log.e("Exception","Exception " + e.getMessage());
                    return response;
                }
            }
        }
        //Used for DEBUG : Log.i(TAG, "Response Read Sigle Block" + Helper.ConvertHexByteArrayToString(response));
        return response;
    }

    //***********************************************************************/
    //* the function send an WriteSingle command (0x0A 0x21) || (0x02 0x21)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  DataToWrite : {0x04 0x14 0xFF 0xB2}
    //* the function will write {0x04 0x14 0xFF 0xB2} at the address 0002
    //***********************************************************************/
    //public static byte[] SendWriteEHconfigCommand (Tag myTag,  boolean isUidRequested, byte EHconfigByte)
    public byte[] SendWriteEHconfigCommand(Tag myTag, boolean isAddressModeNeeded, byte EHconfigByte) {
        byte[] response = new byte[]{(byte) 0xFF};
        byte[] WriteEHconfigFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            WriteEHconfigFrame = new byte[]{(byte) 0x22, TYPE5_ST_CMD_WRITE_EH_CONFIG, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, EHconfigByte};
        } else {
            WriteEHconfigFrame = new byte[]{(byte) 0x02, TYPE5_ST_CMD_WRITE_EH_CONFIG, (byte) 0x02, EHconfigByte};
        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    WriteEHconfigFrame[3] = myTag.getId()[0];
                    WriteEHconfigFrame[4] = myTag.getId()[1];
                    WriteEHconfigFrame[5] = myTag.getId()[2];
                    WriteEHconfigFrame[6] = myTag.getId()[3];
                    WriteEHconfigFrame[7] = myTag.getId()[4];
                    WriteEHconfigFrame[8] = myTag.getId()[5];
                    WriteEHconfigFrame[9] = myTag.getId()[6];
                    WriteEHconfigFrame[10] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendWriteEHconfigCommand() request: " + Helper.ConvertHexByteArrayToString(WriteEHconfigFrame));

                response = nfcvTag.transceive(WriteEHconfigFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendWriteEHconfigCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i("*******", "**SUCCESS** Write Data " + DataToWrite[0] +" "+ DataToWrite[1] +" "+ DataToWrite[2] +" "+ DataToWrite[3] + " at address " +  (byte)StartAddress[0] +" "+ (byte)StartAddress[1]);
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e(TAG, "Write EH config command  " + errorOccured);
                if (errorOccured == 2) {
                    Log.e("Exception","Exception " + e.getMessage());
                    Log.e("WRITE", "**ERROR WRITE EH CONFIG**");
                    return response;
                }
            }
        }
        return response;
    }

    //***********************************************************************/
    //* the function send an WriteSingle command (0x0A 0x21) || (0x02 0x21)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  DataToWrite : {0x04 0x14 0xFF 0xB2}
    //* the function will write {0x04 0x14 0xFF 0xB2} at the address 0002
    //***********************************************************************/
    // public static byte[] SendWriteD0configCommand (Tag myTag,  boolean isUidRequested, byte D0configByte)
    public byte[] SendWriteD0configCommand(Tag myTag, boolean isAddressModeNeeded, byte D0configByte) {
        byte[] response = new byte[]{(byte) 0xFF};
        byte[] WriteD0configFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            WriteD0configFrame = new byte[]{(byte) 0x22, TYPE5_ST_CMD_WRITE_DOC_CONFIG, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, D0configByte};
        } else {
            WriteD0configFrame = new byte[]{(byte) 0x02, TYPE5_ST_CMD_WRITE_DOC_CONFIG, (byte) 0x02, D0configByte};
        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    WriteD0configFrame[3] = myTag.getId()[0];
                    WriteD0configFrame[4] = myTag.getId()[1];
                    WriteD0configFrame[5] = myTag.getId()[2];
                    WriteD0configFrame[6] = myTag.getId()[3];
                    WriteD0configFrame[7] = myTag.getId()[4];
                    WriteD0configFrame[8] = myTag.getId()[5];
                    WriteD0configFrame[9] = myTag.getId()[6];
                    WriteD0configFrame[10] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendWriteD0configCommand() request: " + Helper.ConvertHexByteArrayToString(WriteD0configFrame));

                response = nfcvTag.transceive(WriteD0configFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendWriteD0configCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i("*******", "**SUCCESS** Write Data " + DataToWrite[0] +" "+ DataToWrite[1] +" "+ DataToWrite[2] +" "+ DataToWrite[3] + " at address " +  (byte)StartAddress[0] +" "+ (byte)StartAddress[1]);
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e(TAG, "Write DO config command  " + errorOccured);
                if (errorOccured == 2) {
                    Log.e("Exception","Exception " + e.getMessage());
                    Log.e("WRITE", "**ERROR WRITE DO CONFIG**");
                    return response;
                }
            }
        }
        return response;
    }

    //***********************************************************************/
    //* the function send an ReadSingle command (0x0A 0x20) || (0x02 0x20)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/
//     public static byte[] SendCheckEHenableCommand (Tag myTag,  boolean isUidRequested)
    public byte[] SendCheckEHenableCommand(Tag myTag, boolean isAddressModeNeeded) {
        byte[] response = new byte[]{(byte) 0x0A};
        byte[] ReadEHconfigFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            ReadEHconfigFrame = new byte[]{(byte) 0x22, TYPE5_ST_CMD_CHECK_EH_ENABLE, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        } else {
            ReadEHconfigFrame = new byte[]{(byte) 0x02, TYPE5_ST_CMD_CHECK_EH_ENABLE, (byte) 0x02};
        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    ReadEHconfigFrame[3] = myTag.getId()[0];
                    ReadEHconfigFrame[4] = myTag.getId()[1];
                    ReadEHconfigFrame[5] = myTag.getId()[2];
                    ReadEHconfigFrame[6] = myTag.getId()[3];
                    ReadEHconfigFrame[7] = myTag.getId()[4];
                    ReadEHconfigFrame[8] = myTag.getId()[5];
                    ReadEHconfigFrame[9] = myTag.getId()[6];
                    ReadEHconfigFrame[10] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendCheckEHenableCommand() request: " + Helper.ConvertHexByteArrayToString(ReadEHconfigFrame));

                response = nfcvTag.transceive(ReadEHconfigFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendCheckEHenableCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i(TAG, "SENDED Frame : " + Helper.ConvertHexByteArrayToString(ReadSingleBlockFrame));
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e(TAG, "SendCheckEHenableCommand " + Helper.ConvertHexByteArrayToString(response));
                if (errorOccured == 2) {
                    Log.e("Exception","Exception " + e.getMessage());
                    return response;
                }
            }
        }
        //Used for DEBUG : Log.i(TAG, "Response Read Sigle Block" + Helper.ConvertHexByteArrayToString(response));
        return response;
    }

    //***********************************************************************/
    //* the function send an ReadSingle command (0x0A 0x20) || (0x02 0x20)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/
    // public static byte[] SendResetEHenableCommand (Tag myTag,  boolean isUidRequested)
    public byte[] SendResetEHenableCommand(Tag myTag, boolean isAddressModeNeeded) {
        byte[] response = new byte[]{(byte) 0x0A};
        byte[] ResetEHenableFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            ResetEHenableFrame = new byte[]{(byte) 0x22, TYPE5_ST_CMD_SET_RESET_EH_CFG, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        } else {
            ResetEHenableFrame = new byte[]{(byte) 0x02, TYPE5_ST_CMD_SET_RESET_EH_CFG, (byte) 0x02, (byte) 0x00};
        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    ResetEHenableFrame[3] = myTag.getId()[0];
                    ResetEHenableFrame[4] = myTag.getId()[1];
                    ResetEHenableFrame[5] = myTag.getId()[2];
                    ResetEHenableFrame[6] = myTag.getId()[3];
                    ResetEHenableFrame[7] = myTag.getId()[4];
                    ResetEHenableFrame[8] = myTag.getId()[5];
                    ResetEHenableFrame[9] = myTag.getId()[6];
                    ResetEHenableFrame[10] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendResetEHenableCommand() request: " + Helper.ConvertHexByteArrayToString(ResetEHenableFrame));

                response = nfcvTag.transceive(ResetEHenableFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendResetEHenableCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i(TAG, "SENDED Frame : " + Helper.ConvertHexByteArrayToString(ReadSingleBlockFrame));
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e(TAG, "SendResetEHenableCommand " + Helper.ConvertHexByteArrayToString(response));
                if (errorOccured == 2) {
                    Log.e("Exception","Exception " + e.getMessage());
                    return response;
                }
            }
        }
        //Used for DEBUG : Log.i(TAG, "Response Read Sigle Block" + Helper.ConvertHexByteArrayToString(response));
        return response;
    }

    //***********************************************************************/
    //* the function send an ReadSingle command (0x0A 0x20) || (0x02 0x20)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/
    //public static byte[] SendSetEHenableCommand (Tag myTag,  boolean isUidRequested)
    public byte[] SendSetEHenableCommand(Tag myTag, boolean isAddressModeNeeded) {
        byte[] response = new byte[]{(byte) 0x0A};
        byte[] SetEHenableFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            SetEHenableFrame = new byte[]{(byte) 0x22, TYPE5_ST_CMD_SET_RESET_EH_CFG, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01};
        } else {
            SetEHenableFrame = new byte[]{(byte) 0x02, TYPE5_ST_CMD_SET_RESET_EH_CFG, (byte) 0x02, (byte) 0x01};
        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    SetEHenableFrame[3] = myTag.getId()[0];
                    SetEHenableFrame[4] = myTag.getId()[1];
                    SetEHenableFrame[5] = myTag.getId()[2];
                    SetEHenableFrame[6] = myTag.getId()[3];
                    SetEHenableFrame[7] = myTag.getId()[4];
                    SetEHenableFrame[8] = myTag.getId()[5];
                    SetEHenableFrame[9] = myTag.getId()[6];
                    SetEHenableFrame[10] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendSetEHenableCommand() request: " + Helper.ConvertHexByteArrayToString(SetEHenableFrame));

                response = nfcvTag.transceive(SetEHenableFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendSetEHenableCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i(TAG, "SENDED Frame : " + Helper.ConvertHexByteArrayToString(ReadSingleBlockFrame));
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e(TAG, "SendSetEHenableCommand " + Helper.ConvertHexByteArrayToString(response));
                if (errorOccured == 2) {
                    Log.e("Exception","Exception " + e.getMessage());
                    return response;
                }
            }
        }
        //Used for DEBUG : Log.i(TAG, "Response Read Sigle Block" + Helper.ConvertHexByteArrayToString(response));
        return response;
    }
    // End EH cmd


}
