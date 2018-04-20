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
import android.util.Log;

import com.st.util.DebugUtility;

/**
 * Class containing the NFC TypeV standard commands
 */
public abstract class NFCCommandV {

    static final String TAG = "NFCVCommand";

    // NFC Type V and ISO 15693 commands
    protected static final byte TYPE5_CMD_INVENTORY = (byte) 0x01;    // Inventory
    protected static final byte TYPE5_CMD_STAY_QUIET = (byte) 0x02;    // Stay quiet
    protected static final byte TYPE5_CMD_READ_SINGLE_BLOCK = (byte) 0x20;    // Read single block
    protected static final byte TYPE5_CMD_WRITE_SINGLE_BLOCK = (byte) 0x21;    // Write single block
    protected static final byte TYPE5_CMD_LOCK_BLOCK = (byte) 0x22;    // Lock block
    protected static final byte TYPE5_CMD_READ_MULTIPLE_BLOCK = (byte) 0x23;    // Read multiple blocks
    protected static final byte TYPE5_CMD_WRITE_MULTIPLE_BLOCK = (byte) 0x24;    // Write multiple blocks
    protected static final byte TYPE5_CMD_SELECT = (byte) 0x25;    // Select
    protected static final byte TYPE5_CMD_RESET_TO_READY = (byte) 0x26;    // Reset to ready
    protected static final byte TYPE5_CMD_WRITE_AFI = (byte) 0x27;    // Write AFI
    protected static final byte TYPE5_CMD_LOCK_AFI = (byte) 0x28;    // Lock AFI
    protected static final byte TYPE5_CMD_WRITE_DSFID = (byte) 0x29;    // Write DSFID
    protected static final byte TYPE5_CMD_LOCK_DSFID = (byte) 0x2A;    // Lock DSFID
    protected static final byte TYPE5_CMD_GET_SYSTEM_INFO = (byte) 0x2B;    // Get system information
    protected static final byte TYPE5_CMD_GET_MULTIPLE_BLOCK_SECURITY_STATUS = (byte) 0x2C;    // Get multiple block security status

    // Commands from ISO 15693 - Amendment 4
    protected static final byte TYPE5_CMD_EXTENTED_READ_SINGLE_BLOCK = (byte) 0x30;     // Extended Read Single Block
    protected static final byte TYPE5_CMD_EXTENTED_WRITE_SINGLE_BLOCK = (byte) 0x31;     // Extended Write Single Block
    protected static final byte TYPE5_CMD_EXTENTED_LOCK_BLOCK = (byte) 0x32;     // Extended Lock block
    protected static final byte TYPE5_CMD_EXTENTED_READ_MULTIPLE_BLOCK = (byte) 0x33;     // Extended Read Multiple Block
    protected static final byte TYPE5_CMD_EXTENTED_WRITE_MULTIPLE_BLOCK = (byte) 0x34;     // Extended Write Multiple Block
    protected static final byte TYPE5_CMD_AUTHENTICATE = (byte) 0x35;     // Authenticate
    protected static final byte TYPE5_CMD_KEY_UPDATE = (byte) 0x36;     // Key update
    protected static final byte TYPE5_CMD_AUTHCOMM_CRYPTO_FORMAT_INDICATOR = (byte) 0x37;     // AuthComm crypto format indicator
    protected static final byte TYPE5_CMD_SECURECOMM_CRYPTO_FORMAT_INDICATOR = (byte) 0x38;     // SecureComm crypto format indicator
    protected static final byte TYPE5_CMD_CHALLENGE = (byte) 0x39;     // Challenge
    protected static final byte TYPE5_CMD_READ_BUFFER = (byte) 0x3A;     // Read buffer
    protected static final byte TYPE5_CMD_EXTENTED_GET_SYSTEM_INFORMATION = (byte) 0x3B;     // Extended get system information
    protected static final byte TYPE5_CMD_EXTENTED_GET_MULTI_BLOCK_SEC_STATUS = (byte) 0x3C;     // Extended get multiple block security status


    public NFCCommandV() {
        // TODO Auto-generated constructor stub
    }

    //***********************************************************************/
    //* the function send an Inventory command (0x26 0x01 0x00)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //***********************************************************************/
    public byte[] SendInventoryCommand(Tag myTag) {
        byte[] UIDFrame;
        byte[] response;

        UIDFrame = new byte[]{(byte) 0x26, (byte) TYPE5_CMD_INVENTORY, (byte) 0x00};
        response = new byte[]{(byte) 0x01};

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                nfcvTag.close();
                nfcvTag.connect();
                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendInventoryCommand() request: " + Helper.ConvertHexByteArrayToString(UIDFrame));
                response = nfcvTag.transceive(UIDFrame);
                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendInventoryCommand response: " + Helper.ConvertHexByteArrayToString(response));
                nfcvTag.close();
                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC )
                {
                    //Used for DEBUG : Log.i("NFCCOmmand", "SENDED Frame : " + Helper.ConvertHexByteToString((byte) 0x26) + " " + Helper.ConvertHexByteToString((byte) 0x01) + " " + Helper.ConvertHexByteToString((byte) 0x00) );
                    errorOccured = 0;
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e("Polling**ERROR***", "SendInventoryCommand" + Integer.toString(errorOccured));
                if (errorOccured >= 2) {
                    Log.e("Exception", "Inventory Exception " + e.getMessage());
                    return response;
                }
            }
        }
        //Used for DEBUG : Log.i("NFCCOmmand", "Response " + Helper.ConvertHexByteToString((byte)response[0]));
        return response;
    }

    //public static byte[] SendGetSystemInfoCommandCustom (Tag myTag, SysFileLRHandler ma)
    public byte[] SendGetSystemInfoCommandCustom(Tag myTag, SysFileLRHandler ma) {

        boolean boolDeviceDetected = false;
        byte[] UIDFrame;
        byte[] response;

        if (ma.getIcReference() == null) {
            ma.setBasedOnTwoBytesAddress(false);
            boolDeviceDetected = false;
            ma.setTechno("");
            ma.setManufacturer("");

            // --- 1st Step : Inventory to detect 1 or 2 bytes address ---
            UIDFrame = new byte[]{(byte) 0x26, (byte) TYPE5_CMD_INVENTORY, (byte) 0x00};
            response = new byte[]{(byte) 0xAA};

            int errorOccured = 1;
            while (errorOccured != 0) {
                try {
                    NfcV nfcvTag = NfcV.get(myTag);
                    nfcvTag.close();
                    nfcvTag.connect();
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> Send Inventory cmd request: " + Helper.ConvertHexByteArrayToString(UIDFrame));
                    response = nfcvTag.transceive(UIDFrame);
                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "Send Inventory cmd response: " + Helper.ConvertHexByteArrayToString(response));

                    nfcvTag.close();
                    if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC )
                    {
                        //Used for DEBUG : Log.i("NFCCOmmand", "SENDED Frame : " + Helper.ConvertHexByteToString((byte) 0x26) + " " + Helper.ConvertHexByteToString((byte) 0x01) + " " + Helper.ConvertHexByteToString((byte) 0x00) );
                        errorOccured = 0;
                    }
                } catch (Exception e) {
                    errorOccured++;
                    Log.e("Polling**ERROR***", "Send Inventory cmd " + Integer.toString(errorOccured));
                    if (errorOccured >= 5) {
                        Log.e("Exception", "Inventory Exception " + e.getMessage());
                        return response;
                    }
                }
            }

            if (errorOccured == 0 && response[0] == (byte) 0x00) {
                // Inventory UID analysis
                if (response[9] == (byte) 0xE0) {
                    ma.setTechno("ISO 15693");
                    // New 2014 02 28
                    String uidToString = "";
                    byte[] uid = new byte[8];
                    // change uid format from byteArray to a String
                    for (int i = 1; i <= 8; i++) {
                        uid[i - 1] = response[10 - i];
                        uidToString += Helper.ConvertHexByteToString(uid[i - 1]);
                    }
                    ma.setUid(uidToString);
                    boolDeviceDetected = true;
                } else {
                    boolDeviceDetected = false;
                    ma.setManufacturer("Unknown techno");
                }
            } else {
                boolDeviceDetected = false;
                ma.setManufacturer("Unknown techno");
            }

            // Manage GetSystem info for 2B@ then 1B@ .... in order to get ICRef
            // INVENTORY HAS NOT DETECTED DEVICE ... TRYING WITH GET SYSTEM INFO ROUTINE
            int cpt = 0;
            response = new byte[]{(byte) 0xAA};
            byte[] GetSystemInfoFrame = new byte[2];

            // to know if tag's addresses are coded on 1 or 2 byte we consider 2
            // then we wait the response if it's not good we trying with 1
            ma.setBasedOnTwoBytesAddress(true);

            //1st flag=1 for 2 bytes address products

            //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
            if (ma.isUidRequested()) {
                GetSystemInfoFrame = new byte[]{(byte) 0x2A, (byte) TYPE5_CMD_GET_SYSTEM_INFO, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            } else {
                GetSystemInfoFrame = new byte[]{(byte) 0x0A, (byte) TYPE5_CMD_GET_SYSTEM_INFO};
            }

            while ((response == null || response[0] == (byte) 0xAA) && cpt < 1) {
                try {
                    NfcV nfcvTag = NfcV.get(myTag);
                    //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                    if (ma.isUidRequested()) {
                        GetSystemInfoFrame[2] = myTag.getId()[0];
                        GetSystemInfoFrame[3] = myTag.getId()[1];
                        GetSystemInfoFrame[4] = myTag.getId()[2];
                        GetSystemInfoFrame[5] = myTag.getId()[3];
                        GetSystemInfoFrame[6] = myTag.getId()[4];
                        GetSystemInfoFrame[7] = myTag.getId()[5];
                        GetSystemInfoFrame[8] = myTag.getId()[6];
                        GetSystemInfoFrame[9] = myTag.getId()[7];
                    }
                    nfcvTag.close();
                    nfcvTag.connect();

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> SendGetSystemInfoCommandCustom() request: " + Helper.ConvertHexByteArrayToString(GetSystemInfoFrame));

                    response = nfcvTag.transceive(GetSystemInfoFrame);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "SendGetSystemInfoCommandCustom response: " + Helper.ConvertHexByteArrayToString(response));

                    nfcvTag.close();
                    if (response[0] == (byte) 0x00) {
                        ma.setBasedOnTwoBytesAddress(true);    //1st (flag=1) = 2 add bytes (M24LR64 FREEDOM2)
                        return response;
                    }
                } catch (Exception e) {
                    Log.e("Exception", "SendGetSystemInfoCommandCustom " + e.getMessage());
                    cpt++;
                }
            }

            cpt = 0;
            response = new byte[]{(byte) 0xAA};

            //2nd flag=0 for 1 byte address products

            //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
            if (ma.isUidRequested()) {
                GetSystemInfoFrame = new byte[]{(byte) 0x22, (byte) TYPE5_CMD_GET_SYSTEM_INFO, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            } else {
                GetSystemInfoFrame = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_GET_SYSTEM_INFO};
            }

            while ((response == null || response[0] == (byte) 0xAA) && cpt < 1) {
                try {
                    NfcV nfcvTag = NfcV.get(myTag);
                    //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                    if (ma.isUidRequested()) {
                        GetSystemInfoFrame[2] = myTag.getId()[0];
                        GetSystemInfoFrame[3] = myTag.getId()[1];
                        GetSystemInfoFrame[4] = myTag.getId()[2];
                        GetSystemInfoFrame[5] = myTag.getId()[3];
                        GetSystemInfoFrame[6] = myTag.getId()[4];
                        GetSystemInfoFrame[7] = myTag.getId()[5];
                        GetSystemInfoFrame[8] = myTag.getId()[6];
                        GetSystemInfoFrame[9] = myTag.getId()[7];
                    }
                    nfcvTag.close();
                    nfcvTag.connect();

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "==> SendGetSystemInfoCommandCustom() request: " + Helper.ConvertHexByteArrayToString(GetSystemInfoFrame));

                    response = nfcvTag.transceive(GetSystemInfoFrame);

                    if (DebugUtility.printNfcCommands)
                        Log.v(TAG, "SendGetSystemInfoCommandCustom response: " + Helper.ConvertHexByteArrayToString(response));

                    nfcvTag.close();
                    if (response[0] == (byte) 0x00) {
                        ma.setBasedOnTwoBytesAddress(false);    //1st (flag=1) = 2 add bytes (M24LR64 FREEDOM2)
                        return response;
                    }
                } catch (Exception e) {
                    Log.e("Exception", "Get System Info Exception " + e.getMessage());
                    cpt++;
                }
            }

        } else {
            int cpt2 = 0;
            int cpt_2bytes = 0;

            response = new byte[]{(byte) 0xAA};
            byte[] GetSystemInfoFrame2bytesAddress;
            byte[] GetSystemInfoFrame1bytesAddress;

            //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
            if (ma.isUidRequested()) {
                GetSystemInfoFrame2bytesAddress = new byte[]{(byte) 0x2A, (byte) TYPE5_CMD_GET_SYSTEM_INFO, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                GetSystemInfoFrame1bytesAddress = new byte[]{(byte) 0x22, (byte) TYPE5_CMD_GET_SYSTEM_INFO, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            } else {
                GetSystemInfoFrame2bytesAddress = new byte[]{(byte) 0x0A, (byte) TYPE5_CMD_GET_SYSTEM_INFO};
                GetSystemInfoFrame1bytesAddress = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_GET_SYSTEM_INFO};
            }

            // INVENTORY HAS DETECTED DEVICE ... READING GET SYSTEM INFO
            while ((response == null || response[0] == (byte) 0xAA) && cpt2 < 1 && cpt_2bytes <= 5) {
                try {
                    cpt_2bytes++;
                    NfcV nfcvTag = NfcV.get(myTag);
                    //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                    if (ma.isUidRequested()) {
                        //2 bytes address
                        GetSystemInfoFrame2bytesAddress[2] = myTag.getId()[0];
                        GetSystemInfoFrame2bytesAddress[3] = myTag.getId()[1];
                        GetSystemInfoFrame2bytesAddress[4] = myTag.getId()[2];
                        GetSystemInfoFrame2bytesAddress[5] = myTag.getId()[3];
                        GetSystemInfoFrame2bytesAddress[6] = myTag.getId()[4];
                        GetSystemInfoFrame2bytesAddress[7] = myTag.getId()[5];
                        GetSystemInfoFrame2bytesAddress[8] = myTag.getId()[6];
                        GetSystemInfoFrame2bytesAddress[9] = myTag.getId()[7];
                        //1 bytes address
                        GetSystemInfoFrame1bytesAddress[2] = myTag.getId()[0];
                        GetSystemInfoFrame1bytesAddress[3] = myTag.getId()[1];
                        GetSystemInfoFrame1bytesAddress[4] = myTag.getId()[2];
                        GetSystemInfoFrame1bytesAddress[5] = myTag.getId()[3];
                        GetSystemInfoFrame1bytesAddress[6] = myTag.getId()[4];
                        GetSystemInfoFrame1bytesAddress[7] = myTag.getId()[5];
                        GetSystemInfoFrame1bytesAddress[8] = myTag.getId()[6];
                        GetSystemInfoFrame1bytesAddress[9] = myTag.getId()[7];
                    }
                    nfcvTag.close();
                    nfcvTag.connect();
                    if (ma.isBasedOnTwoBytesAddress() == true) {
                        if (DebugUtility.printNfcCommands)
                            Log.v(TAG, "==> SendGetSystemInfoCommandCustom() request: " + Helper.ConvertHexByteArrayToString(GetSystemInfoFrame2bytesAddress));

                        response = nfcvTag.transceive(GetSystemInfoFrame2bytesAddress);

                        if (DebugUtility.printNfcCommands)
                            Log.v(TAG, "SendGetSystemInfoCommandCustom response: " + Helper.ConvertHexByteArrayToString(response));
                    } else {
                        if (DebugUtility.printNfcCommands)
                            Log.v(TAG, "==> SendGetSystemInfoCommandCustom() request: " + Helper.ConvertHexByteArrayToString(GetSystemInfoFrame1bytesAddress));

                        response = nfcvTag.transceive(GetSystemInfoFrame1bytesAddress);

                        if (DebugUtility.printNfcCommands)
                            Log.v(TAG, "SendGetSystemInfoCommandCustom response: " + Helper.ConvertHexByteArrayToString(response));
                    }
                    //nfcvTag.close(); //-> deleted 20130717 too long time consuming
                    if (response[0] == (byte) 0x00) {
                        return response;
                    }
                } catch (Exception e) {
                    Log.e("Exception", "SendGetSystemInfoCommandCustom " + e.getMessage());
                    ma.setBasedOnTwoBytesAddress(false);
                    cpt2++;
                }
            }
        }
        //Used for DEBUG : Log.i("NFCCOmmand", "Response Get System Info " + Helper.ConvertHexByteArrayToString(response));
        return response;
    }

    //***********************************************************************/
    //* the function send an ReadSingle command (0x0A 0x20) || (0x02 0x20)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/
    //public static byte[] SendReadSingleBlockCommand (Tag myTag, byte[] StartAddress, boolean isBasedOnTwoBytesAddress, boolean isUidRequested)
    public byte[] SendReadSingleBlockCommand(Tag myTag, byte[] StartAddress, boolean isBasedOnTwoBytesAddress, boolean isAddressModeNeeded) {
        byte[] response = new byte[]{(byte) 0x0A};
        byte[] ReadSingleBlockFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            if (isBasedOnTwoBytesAddress)
                ReadSingleBlockFrame = new byte[]{(byte) 0x2A, (byte) TYPE5_CMD_READ_SINGLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], StartAddress[0]};
            else
            if (StartAddress.length == 2 && StartAddress[0] != 0 ) {
                ReadSingleBlockFrame = new byte[]{(byte) 0x22, (byte) TYPE5_CMD_EXTENTED_READ_SINGLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,StartAddress[1], StartAddress[0]};
            }else
                ReadSingleBlockFrame = new byte[]{(byte) 0x22, (byte) TYPE5_CMD_READ_SINGLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1]};
        } else {
            if (isBasedOnTwoBytesAddress)
                ReadSingleBlockFrame = new byte[]{(byte) 0x0A, (byte) TYPE5_CMD_READ_SINGLE_BLOCK, StartAddress[1], StartAddress[0]};
            else
            if (StartAddress.length == 2 && StartAddress[0] != 0 ) {
                ReadSingleBlockFrame = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_EXTENTED_READ_SINGLE_BLOCK, StartAddress[1], StartAddress[0]};
            }else
                ReadSingleBlockFrame = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_READ_SINGLE_BLOCK, StartAddress[1]};
        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    ReadSingleBlockFrame[2] = myTag.getId()[0];
                    ReadSingleBlockFrame[3] = myTag.getId()[1];
                    ReadSingleBlockFrame[4] = myTag.getId()[2];
                    ReadSingleBlockFrame[5] = myTag.getId()[3];
                    ReadSingleBlockFrame[6] = myTag.getId()[4];
                    ReadSingleBlockFrame[7] = myTag.getId()[5];
                    ReadSingleBlockFrame[8] = myTag.getId()[6];
                    ReadSingleBlockFrame[9] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendReadSingleBlockCommand() request: " + Helper.ConvertHexByteArrayToString(ReadSingleBlockFrame));

                response = nfcvTag.transceive(ReadSingleBlockFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendReadSingleBlockCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01)//response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i("NFCCOmmand", "SENDED Frame : " + Helper.ConvertHexByteArrayToString(ReadSingleBlockFrame));
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e("NFCCOmmand", "Response Read Single Block" + Helper.ConvertHexByteArrayToString(response));
                if (errorOccured == 2) {
                    Log.e("Exception", "Exception " + e.getMessage());
                    return response;
                }
            }
        }
        //Used for DEBUG : Log.i("NFCCOmmand", "Response Read Sigle Block" + Helper.ConvertHexByteArrayToString(response));
        return response;
    }


    //***********************************************************************/
    //* the function send an ReadSingle Custom command (0x0A 0x20) || (0x02 0x20)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/
    //public static byte[] SendReadMultipleBlockCommand (Tag myTag, byte[] StartAddress, byte NbOfBlockToRead,   boolean BasedOnTwoBytesAddress,boolean isUidRequested)
    public byte[] SendReadMultipleBlockCommand(Tag myTag, byte[] StartAddress, byte NbOfBlockToRead, boolean BasedOnTwoBytesAddress, boolean isAddressModeNeeded) {

        byte[] response = new byte[]{(byte) 0x01};
        byte[] ReadMultipleBlockFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            if (BasedOnTwoBytesAddress)
                ReadMultipleBlockFrame = new byte[]{(byte) 0x2A, (byte) TYPE5_CMD_READ_MULTIPLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], StartAddress[0], NbOfBlockToRead};
            else
                ReadMultipleBlockFrame = new byte[]{(byte) 0x22, (byte) TYPE5_CMD_READ_MULTIPLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], NbOfBlockToRead};
        } else {
            if (BasedOnTwoBytesAddress)
                ReadMultipleBlockFrame = new byte[]{(byte) 0x0A, (byte) TYPE5_CMD_READ_MULTIPLE_BLOCK, StartAddress[1], StartAddress[0], NbOfBlockToRead};
            else
                //ReadMultipleBlockFrame = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_READ_MULTIPLE_BLOCK, StartAddress[1], NbOfBlockToRead};
                if (StartAddress.length == 2 && StartAddress[0] != 0 ) {
                    ReadMultipleBlockFrame = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_EXTENTED_READ_MULTIPLE_BLOCK, StartAddress[1], StartAddress[0], NbOfBlockToRead};
                }else
                    ReadMultipleBlockFrame = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_READ_MULTIPLE_BLOCK, StartAddress[1], NbOfBlockToRead};
        }
        //Used for DEBUG : Log.i("NFCCOmmand", "SENDED Frame : " + Helper.ConvertHexByteArrayToString(ReadMultipleBlockFrame));


        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    ReadMultipleBlockFrame[2] = myTag.getId()[0];
                    ReadMultipleBlockFrame[3] = myTag.getId()[1];
                    ReadMultipleBlockFrame[4] = myTag.getId()[2];
                    ReadMultipleBlockFrame[5] = myTag.getId()[3];
                    ReadMultipleBlockFrame[6] = myTag.getId()[4];
                    ReadMultipleBlockFrame[7] = myTag.getId()[5];
                    ReadMultipleBlockFrame[8] = myTag.getId()[6];
                    ReadMultipleBlockFrame[9] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendReadMultipleBlockCommand() request: " + Helper.ConvertHexByteArrayToString(ReadMultipleBlockFrame));

                response = nfcvTag.transceive(ReadMultipleBlockFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendReadMultipleBlockCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01)//response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i("NFCCOmmand", "SENDED Frame : " + Helper.ConvertHexByteArrayToString(ReadMultipleBlockFrame));

                }
            } catch (Exception e) {
                errorOccured++;
                Log.e("NFCCOmmand", "SendReadMultipleBlockCommand errorOccured " + errorOccured);
                if (errorOccured == 3) {
                    Log.e("Exception", "Exception " + e.getMessage());
                    Log.e("NFCCOmmand", "Error when try to read from address  " + (byte) StartAddress[0] + " " + (byte) StartAddress[1]);
                    return response;
                }
            }
        }
        //Used for DEBUG : Log.i("NFCCOmmand", "Response Read Multiple Block" + Helper.ConvertHexByteArrayToString(response));
        return response;

    }

    //public static byte[] SendReadMultipleBlockCommandCustom_JPG (Tag myTag, byte[] StartAddress, byte NbOfBlockToRead,   boolean BasedOnTwoBytesAddress, boolean isUidRequested)
    public byte[] SendReadMultipleBlockCommandCustom_JPG(Tag myTag, byte[] StartAddress, byte NbOfBlockToRead, boolean BasedOnTwoBytesAddress, boolean isAddressModeNeeded) {
        long cpt = 0;
        boolean EndOfJpgFile = false;
        boolean checkCorrectAnswer = true;

        //int NbBytesToRead = (NbOfBlockToRead*4)+1;
        int NbBytesToRead = NbOfBlockToRead * 4;
        byte[] FinalResponse = new byte[NbBytesToRead + 1];

        for (int i = 0; i <= (NbOfBlockToRead * 4) - 4; i = i + 4) {
            byte[] temp = new byte[5];
            int incrementAddressStart0 = (StartAddress[0] + i / 256);                                //Most Important Byte
            int incrementAddressStart1 = (StartAddress[1] + i / 4) - (incrementAddressStart0 * 255);    //Less Important Byte

            temp = null;
            while (temp == null || temp[0] == 1 && cpt <= 2) {
                temp = SendReadSingleBlockCommand(myTag, new byte[]{(byte) incrementAddressStart0, (byte) incrementAddressStart1}, BasedOnTwoBytesAddress, isAddressModeNeeded);
                cpt++;
            }
            cpt = 0;

            if (temp[0] != 0x00)
                checkCorrectAnswer = false;

            if (i == 0) {
                for (int j = 0; j <= 4; j++) {
                    if (temp[0] == 0x00)
                        FinalResponse[j] = temp[j];
                    else
                        FinalResponse[j] = (byte) 0xFF;
                }
            } else {
                for (int j = 1; j <= 4; j++) {
                    if (temp[0] == 0x00)
                        FinalResponse[i + j] = temp[j];
                    else
                        FinalResponse[i + j] = (byte) 0xFF;
                }
            }

            //check JPG Start of Frame
            for (int j = 1; j <= 4; j++)
                if (FinalResponse[i + j] == (byte) 0xD9 && FinalResponse[i + j - 1] == (byte) 0xFF) {
                    i = (NbOfBlockToRead * 4) + 50;
                    EndOfJpgFile = true;
                    j = 15;
                }

        }
        if (EndOfJpgFile == false)
            FinalResponse[0] = (byte) 0xAE;
        if (checkCorrectAnswer == false)
            FinalResponse[0] = (byte) 0xAF;

        return FinalResponse;
    }

    //public static byte[] Send_several_ReadSingleBlockCommands (Tag myTag, byte[] StartAddress, byte[] bytNbBytesToRead,   boolean BasedOnTwoBytesAddress, boolean isUidRequested)
    public byte[] Send_several_ReadSingleBlockCommands(Tag myTag, byte[] StartAddress, byte[] bytNbBytesToRead, boolean BasedOnTwoBytesAddress, boolean isAddressModeNeeded) {
        long cpt = 0;
        boolean checkCorrectAnswer = true;

        int NbBytesToRead = Helper.Convert2bytesHexaFormatToInt(bytNbBytesToRead);
        int iNbOfBlockToRead = (NbBytesToRead / 4);
        byte[] FinalResponse = new byte[iNbOfBlockToRead * 4 + 1];

        byte[] bytAddress = new byte[2];

        //int intAddress = 0;
        int intAddress = Helper.Convert2bytesHexaFormatToInt(StartAddress);

        int index = 0;

        byte[] temp = new byte[5];

        //boucle for(int i=0;i<iNbOfBlockToRead; i++)
        do {
            bytAddress = Helper.ConvertIntTo2bytesHexaFormat(intAddress);

            temp = null;
            while (temp == null || temp[0] == 1 && cpt <= 5) {
                temp = SendReadSingleBlockCommand(myTag, new byte[]{(byte) bytAddress[0], (byte) bytAddress[1]}, BasedOnTwoBytesAddress, isAddressModeNeeded);
                cpt++;
            }
            cpt = 0;

            if (temp[0] != 0x00)
                checkCorrectAnswer = false;

            if (temp[0] == 0) {
                if (index == 0) {
                    for (int j = 0; j <= 4; j++)
                        FinalResponse[j] = temp[j];
                } else {
                    for (int j = 1; j <= 4; j++)
                        FinalResponse[(index * 4) + j] = temp[j];
                }
            } else {
                if (index == 0) {
                    for (int j = 0; j <= 4; j++)
                        FinalResponse[j] = (byte) 0xFF;
                } else {
                    for (int j = 1; j <= 4; j++)
                        FinalResponse[(index * 4) + j] = (byte) 0xFF;
                }
            }

            intAddress++;
            index++;

        } while (index < iNbOfBlockToRead);

        if (checkCorrectAnswer == false)
            FinalResponse[0] = (byte) 0xAF;

        return FinalResponse;
    }

    //public static byte[] Send_several_ReadSingleBlockCommands_NbBlocks (Tag myTag, byte[] StartAddress, byte[] bNbOfBlockToRead,   boolean BasedOnTwoBytesAddress, boolean isUidRequested)
    public byte[] Send_several_ReadSingleBlockCommands_NbBlocks(Tag myTag, byte[] StartAddress, byte[] bNbOfBlockToRead, boolean BasedOnTwoBytesAddress, boolean isAddressModeNeeded) {
        long cpt = 0;
        boolean checkCorrectAnswer = true;

        int iNbOfBlockToRead = Helper.Convert2bytesHexaFormatToInt(bNbOfBlockToRead);
        byte[] FinalResponse = new byte[iNbOfBlockToRead * 4 + 1];

        byte[] bytAddress = new byte[2];

        //int intAddress = 0;
        int intAddress = Helper.Convert2bytesHexaFormatToInt(StartAddress);

        int index = 0;

        byte[] temp = new byte[5];

        //boucle for(int i=0;i<iNbOfBlockToRead; i++)
        do {
            bytAddress = Helper.ConvertIntTo2bytesHexaFormat(intAddress);

            temp = null;
            while (temp == null || temp[0] == 1 && cpt <= 5) {
                temp = SendReadSingleBlockCommand(myTag, new byte[]{(byte) bytAddress[0], (byte) bytAddress[1]}, BasedOnTwoBytesAddress, isAddressModeNeeded);
                cpt++;
            }
            cpt = 0;

            if (temp[0] != 0x00)
                checkCorrectAnswer = false;

            if (temp[0] == 0) {
                if (index == 0) {
                    for (int j = 0; j <= 4; j++)
                        FinalResponse[j] = temp[j];
                } else {
                    for (int j = 1; j <= 4; j++)
                        FinalResponse[(index * 4) + j] = temp[j];
                }
            } else {
                if (index == 0) {
                    for (int j = 0; j <= 4; j++)
                        FinalResponse[j] = (byte) 0xFF;
                } else {
                    for (int j = 1; j <= 4; j++)
                        FinalResponse[(index * 4) + j] = (byte) 0xFF;
                }
            }

            intAddress++;
            index++;

        } while (index < iNbOfBlockToRead);

        if (checkCorrectAnswer == false)
            FinalResponse[0] = (byte) 0xAF;

        return FinalResponse;
    }

    //public static byte[] Send_several_ReadSingleBlockCommands_NbBlocks_JPG (Tag myTag, byte[] StartAddress, byte[] bNbOfBlockToRead,   boolean BasedOnTwoBytesAddress, boolean isUidRequested)
    public byte[] Send_several_ReadSingleBlockCommands_NbBlocks_JPG(Tag myTag, byte[] StartAddress, byte[] bNbOfBlockToRead, boolean BasedOnTwoBytesAddress, boolean isAddressModeNeeded) {
        long cpt = 0;
        boolean EndOfJpgFile = false;
        boolean checkCorrectAnswer = true;

        int iNbOfBlockToRead = Helper.Convert2bytesHexaFormatToInt(bNbOfBlockToRead);
        byte[] FinalResponse = new byte[iNbOfBlockToRead * 4 + 1];

        byte[] bytAddress = new byte[2];

        //int intAddress = 0;
        int intAddress = Helper.Convert2bytesHexaFormatToInt(StartAddress);

        int index = 0;

        byte[] temp = new byte[5];

        //boucle for(int i=0;i<iNbOfBlockToRead; i++)
        do {
            bytAddress = Helper.ConvertIntTo2bytesHexaFormat(intAddress);

            temp = null;
            while (temp == null || temp[0] == 1 && cpt <= 5) {
                temp = SendReadSingleBlockCommand(myTag, new byte[]{(byte) bytAddress[0], (byte) bytAddress[1]}, BasedOnTwoBytesAddress, isAddressModeNeeded);
                cpt++;
            }
            cpt = 0;

            if (temp[0] != 0x00)
                checkCorrectAnswer = false;

            if (temp[0] == 0) {
                if (index == 0) {
                    for (int j = 0; j <= 4; j++)
                        FinalResponse[j] = temp[j];
                } else {
                    for (int j = 1; j <= 4; j++)
                        FinalResponse[(index * 4) + j] = temp[j];
                }
            } else {
                if (index == 0) {
                    for (int j = 0; j <= 4; j++)
                        FinalResponse[j] = (byte) 0xFF;
                } else {
                    for (int j = 1; j <= 4; j++)
                        FinalResponse[(index * 4) + j] = (byte) 0xFF;
                }
            }

            //check JPG Start of Frame
            for (int j = 1; j <= 4; j++)
                if (FinalResponse[(index * 4) + j] == (byte) 0xD9 && FinalResponse[(index * 4) + j - 1] == (byte) 0xFF) {
                    index = iNbOfBlockToRead + 50;
                    EndOfJpgFile = true;
                    j = 15;
                }

            intAddress++;
            index++;

        } while (index < iNbOfBlockToRead);

        if (EndOfJpgFile == false)
            FinalResponse[0] = (byte) 0xAE;
        if (checkCorrectAnswer == false)
            FinalResponse[0] = (byte) 0xAF;

        return FinalResponse;
    }

    //***********************************************************************/
    //* the function send an ReadSingle Custom command (0x0A 0x20) || (0x02 0x20)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/

    //public static byte[] SendReadMultipleBlockCommandCustom2 (Tag myTag, byte[] StartAddress, byte[] bNbOfBlockToRead, boolean BasedOnTwoBytesAddress, boolean isUidRequested,String MemorySize)
    public byte[] SendReadMultipleBlockCommandCustom2(Tag myTag, byte[] StartAddress, byte[] bNbOfBlockToRead, boolean BasedOnTwoBytesAddress, boolean isAddressModeNeeded, String MemorySize) {

        boolean checkCorrectAnswer = true;

        int iNbOfBlockToRead = Helper.Convert2bytesHexaFormatToInt(bNbOfBlockToRead);
        int iNumberOfSectorToRead;
        int iStartAddress = Helper.Convert2bytesHexaFormatToInt(StartAddress);
        int iAddressStartRead = (iStartAddress / 32) * 32;
        if (iNbOfBlockToRead % 32 == 0) {
            iNumberOfSectorToRead = (iNbOfBlockToRead / 32);
        } else {
            iNumberOfSectorToRead = (iNbOfBlockToRead / 32) + 1;
        }
        byte[] bAddressStartRead = Helper.ConvertIntTo2bytesHexaFormat(iAddressStartRead);

        byte[] AllReadDatas = new byte[((iNumberOfSectorToRead * 128) + 1)];
        byte[] FinalResponse = new byte[(iNbOfBlockToRead * 4) + 1];

        String sMemorySize = MemorySize;
        sMemorySize = Helper.StringForceDigit(sMemorySize, 4);
        byte[] bLastMemoryAddress = Helper.ConvertStringToHexBytes(sMemorySize);

        //Loop needed for number of sector o read
        for (int i = 0; i < iNumberOfSectorToRead; i++) {
            byte[] temp = new byte[33];

            int incrementAddressStart0 = (bAddressStartRead[0] + i / 8);                                    //Most Important Byte
            int incrementAddressStart1 = (bAddressStartRead[1] + i * 32) - (incrementAddressStart0 * 256);    //Less Important Byte


            if (bAddressStartRead[0] < 0)
                incrementAddressStart0 = ((bAddressStartRead[0] + 256) + i / 8);

            if (bAddressStartRead[1] < 0)
                incrementAddressStart1 = ((bAddressStartRead[1] + 256) + i * 32) - (incrementAddressStart0 * 256);


            if (incrementAddressStart1 > bLastMemoryAddress[1] && incrementAddressStart0 > bLastMemoryAddress[0]) {


            } else {
                temp = null;
                //temp = SendReadMultipleBlockCommand (myTag, new byte[]{(byte)incrementAddressStart0,(byte)incrementAddressStart1},(byte)0x1F,ma);
                temp = SendReadMultipleBlockCommand(myTag, new byte[]{(byte) incrementAddressStart0, (byte) incrementAddressStart1}, (byte) 0x1F, BasedOnTwoBytesAddress, isAddressModeNeeded);

                if (temp[0] != 0x00)
                    checkCorrectAnswer = false;

                // if any error occurs during
                if (temp[0] == (byte) 0x01) {
                    return temp;
                } else {
                    // to construct a response with first byte = 0x00
                    try {
                        if (i == 0) {
                            for (int j = 0; j <= 128; j++) {
                                AllReadDatas[j] = temp[j];
                            }
                        } else {
                            for (int j = 1; j <= 128; j++) {
                                AllReadDatas[(i * 128) + j] = temp[j];
                            }
                        }
                    } catch (Exception e) {
                        // For debug .....
                        //Log.e("NFCCommand", Log.getStackTraceString(e.getCause().getCause()));
                        // index out of range occurred during the "SendReadMultipleBlockCommand" with a one byte or more returned without possibility to check error returned // RF cmd
                        // If based on attended returned values we go outside predefined buffers ==> means error...
                        temp[0] = (byte) 0x01;
                        return temp;
                    }
                }
            }
        }

        int iNbBlockToCopyInFinalReponse = Helper.Convert2bytesHexaFormatToInt(bNbOfBlockToRead);
        int iNumberOfBlockToIgnoreInAllReadData = 4 * (Helper.Convert2bytesHexaFormatToInt(StartAddress) % 32);

        for (int h = 1; h <= iNbBlockToCopyInFinalReponse * 4; h++) {
            FinalResponse[h] = AllReadDatas[h + iNumberOfBlockToIgnoreInAllReadData];
        }

        if (checkCorrectAnswer == true)
            FinalResponse[0] = AllReadDatas[0];
        else
            FinalResponse[0] = (byte) 0xAF;

        return FinalResponse;
    }

    //     public static byte[] SendReadMultipleBlockCommandCustom2_JPG (Tag myTag, byte[] StartAddress, byte[] bNbOfBlockToRead, boolean BasedOnTwoBytesAddress, boolean isUidRequested,String MemorySize)
    public byte[] SendReadMultipleBlockCommandCustom2_JPG(Tag myTag, byte[] StartAddress, byte[] bNbOfBlockToRead, boolean BasedOnTwoBytesAddress, boolean isAddressModeNeeded, String MemorySize) {
        boolean checkCorrectAnswer = true;
        boolean EndOfJpgFile = false;

        int iNbOfBlockToRead = Helper.Convert2bytesHexaFormatToInt(bNbOfBlockToRead);
        int iNumberOfSectorToRead = 0;
        int iNumberOfSectorToRead_New = 0;
        int iStartAddress = Helper.Convert2bytesHexaFormatToInt(StartAddress);
        int iAddressStartRead = (iStartAddress / 32) * 32;

        if (iNbOfBlockToRead % 32 == 0) {
            iNumberOfSectorToRead = (iNbOfBlockToRead / 32);
        } else {
            iNumberOfSectorToRead = (iNbOfBlockToRead / 32) + 1;
        }
        byte[] bAddressStartRead = Helper.ConvertIntTo2bytesHexaFormat(iAddressStartRead);

        byte[] AllReadDatas = new byte[((iNumberOfSectorToRead * 128) + 1)];
        byte[] FinalResponse = new byte[(iNbOfBlockToRead * 4) + 1];

        String sMemorySize = MemorySize;
        sMemorySize = Helper.StringForceDigit(sMemorySize, 4);
        byte[] bLastMemoryAddress = Helper.ConvertStringToHexBytes(sMemorySize);

        //Loop needed for number of sector o read
        for (int i = 0; i < iNumberOfSectorToRead; i++) {
            byte[] temp = new byte[33];

            int incrementAddressStart0 = (bAddressStartRead[0] + i / 8);                                    //Most Important Byte
            int incrementAddressStart1 = (bAddressStartRead[1] + i * 32) - (incrementAddressStart0 * 256);    //Less Important Byte


            if (bAddressStartRead[0] < 0)
                incrementAddressStart0 = ((bAddressStartRead[0] + 256) + i / 8);

            if (bAddressStartRead[1] < 0)
                incrementAddressStart1 = ((bAddressStartRead[1] + 256) + i * 32) - (incrementAddressStart0 * 256);


            if (incrementAddressStart1 > bLastMemoryAddress[1] && incrementAddressStart0 > bLastMemoryAddress[0]) {


            } else {
                temp = null;
                temp = SendReadMultipleBlockCommand(myTag, new byte[]{(byte) incrementAddressStart0, (byte) incrementAddressStart1}, (byte) 0x1F, BasedOnTwoBytesAddress, isAddressModeNeeded);

                if (temp[0] != 0x00)
                    checkCorrectAnswer = false;

                // if any error occurs during
                if (temp[0] == (byte) 0x01) {
                    return temp;
                } else {
                    // to construct a response with first byte = 0x00
                    if (i == 0) {
                        for (int j = 0; j <= 128; j++) {
                            AllReadDatas[j] = temp[j];
                        }
                    } else {
                        for (int j = 1; j <= 128; j++) {
                            AllReadDatas[(i * 128) + j] = temp[j];
                        }
                    }

                    //check JPG Start of Frame
                    for (int j = 1; j <= 128; j++)
                        if (AllReadDatas[(i * 128) + j] == (byte) 0xD9 && AllReadDatas[(i * 128) + j - 1] == (byte) 0xFF) {
                            iNumberOfSectorToRead_New = i + 1;
                            i = iNumberOfSectorToRead + 10;
                            EndOfJpgFile = true;
                            j = 200;
                        }
                }
            }
        }

        int iNbBlockToCopyInFinalReponse = iNumberOfSectorToRead_New * 32;
        int iNumberOfBlockToIgnoreInAllReadData = 4 * (Helper.Convert2bytesHexaFormatToInt(StartAddress) % 32);

        for (int h = 1; h <= iNbBlockToCopyInFinalReponse * 4; h++) {
            FinalResponse[h] = AllReadDatas[h + iNumberOfBlockToIgnoreInAllReadData];
        }

        if (EndOfJpgFile = false)
            FinalResponse[0] = (byte) 0xAE;
        else {
            if (checkCorrectAnswer == true)
                FinalResponse[0] = AllReadDatas[0];
            else
                FinalResponse[0] = (byte) 0xAF;
        }

        return FinalResponse;
    }


    //32 blocks max
    //Begining from Address 0x0000
//     public static byte[] Send_several_ReadMultipleBlockCommands (Tag myTag, byte[] bytNbBytesToRead,   boolean BasedOnTwoBytesAddress,boolean isUidRequested)
    public byte[] Send_several_ReadMultipleBlockCommands(Tag myTag, byte[] bytNbBytesToRead, boolean BasedOnTwoBytesAddress, boolean isAddressModeNeeded) {
        long cpt = 0;
        boolean checkCorrectAnswer = true;

        int NbBytesToRead = Helper.Convert2bytesHexaFormatToInt(bytNbBytesToRead);
        int iNumberOfSectorToRead;
        int iResteOfBytes;
        int iResteOfRows;

        if (NbBytesToRead % 128 == 0)
            iNumberOfSectorToRead = (NbBytesToRead / 128);
        else
            iNumberOfSectorToRead = (NbBytesToRead / 128) + 1;
        iResteOfBytes = NbBytesToRead % 128;
        iResteOfRows = iResteOfBytes / 4;
        if (iResteOfBytes % 4 > 0)
            iResteOfRows += 1;

        //String sMemorySize = ma.getMemorySize();
        //sMemorySize = Helper.StringForceDigit(sMemorySize,4);
        //byte[] bLastMemoryAddress = Helper.ConvertStringToHexBytes(sMemorySize);

        byte[] bytAddress = new byte[2];
        int intAddress = 0;
        int index = 0;

        byte[] FinalResponse = new byte[(iNumberOfSectorToRead * 128) + 1];
        byte[] temp = new byte[128 + 1];

        //Loop needed for number of sector o read
        do {
            bytAddress = Helper.ConvertIntTo2bytesHexaFormat(intAddress);

            temp = null;
            byte byteNbRowsToRead;
            if (index == iNumberOfSectorToRead - 1 && iResteOfRows > 0)
                byteNbRowsToRead = (byte) iResteOfRows;
            else
                byteNbRowsToRead = (byte) 0x20;

            temp = null;
            while (temp == null || temp[0] == 1 && cpt <= 5) {
                temp = SendReadMultipleBlockCommand(myTag, bytAddress, (byte) (byteNbRowsToRead - 1), BasedOnTwoBytesAddress, isAddressModeNeeded);
                cpt++;
            }
            cpt = 0;

            intAddress += 0x20;

            if (temp[0] != 0x00)
                checkCorrectAnswer = false;

            // to construct a response with first byte = 0x00
            if (temp[0] == 0) {
                if (index == 0) {
                    for (int j = 0; j <= byteNbRowsToRead * 4; j++)
                        FinalResponse[j] = temp[j];
                } else {
                    for (int j = 1; j <= byteNbRowsToRead * 4; j++)
                        FinalResponse[(index * 128) + j] = temp[j];
                }
            } else {
                if (index == 0) {
                    for (int j = 0; j <= byteNbRowsToRead * 4; j++)
                        FinalResponse[j] = (byte) 0xFF;
                } else {
                    for (int j = 1; j <= byteNbRowsToRead * 4; j++)
                        FinalResponse[(index * 128) + j] = (byte) 0xFF;
                }
            }

            index++;

        } while (index < iNumberOfSectorToRead);

        if (checkCorrectAnswer == false)
            FinalResponse[0] = (byte) 0xAF;

        return FinalResponse;
    }


    //***********************************************************************/
    //* the function send an ReadMultiple command (0x0A 0x23) || (0x02 0x23)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/
//     public static byte[] SendReadMultipleBlockCommandCustom (Tag myTag, byte[] StartAddress, byte NbOfBlockToRead,  boolean isBasedOnTwoBytesAddress,boolean isUidRequested)
    public byte[] SendReadMultipleBlockCommandCustom(Tag myTag, byte[] StartAddress, byte NbOfBlockToRead, boolean isBasedOnTwoBytesAddress, boolean isAddressModeNeeded) {
        byte[] response = new byte[]{(byte) 0x01};
        byte[] ReadMultipleBlockFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            if (isBasedOnTwoBytesAddress)
                ReadMultipleBlockFrame = new byte[]{(byte) 0x2A, (byte) TYPE5_CMD_READ_MULTIPLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], StartAddress[0], NbOfBlockToRead};
            else
                ReadMultipleBlockFrame = new byte[]{(byte) 0x22, (byte) TYPE5_CMD_READ_MULTIPLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], NbOfBlockToRead};
        } else {
            if (isBasedOnTwoBytesAddress)
                ReadMultipleBlockFrame = new byte[]{(byte) 0x0A, (byte) TYPE5_CMD_READ_MULTIPLE_BLOCK, StartAddress[1], StartAddress[0], NbOfBlockToRead};
            else
                ReadMultipleBlockFrame = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_READ_MULTIPLE_BLOCK, StartAddress[1], NbOfBlockToRead};
        }
        //Used for DEBUG : Log.i("NFCCOmmand", "SENDED Frame : " + Helper.ConvertHexByteArrayToString(ReadMultipleBlockFrame));

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    ReadMultipleBlockFrame[2] = myTag.getId()[0];
                    ReadMultipleBlockFrame[3] = myTag.getId()[1];
                    ReadMultipleBlockFrame[4] = myTag.getId()[2];
                    ReadMultipleBlockFrame[5] = myTag.getId()[3];
                    ReadMultipleBlockFrame[6] = myTag.getId()[4];
                    ReadMultipleBlockFrame[7] = myTag.getId()[5];
                    ReadMultipleBlockFrame[8] = myTag.getId()[6];
                    ReadMultipleBlockFrame[9] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendReadMultipleBlockCommandCustom() request: " + Helper.ConvertHexByteArrayToString(ReadMultipleBlockFrame));

                response = nfcvTag.transceive(ReadMultipleBlockFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendReadMultipleBlockCommandCustom response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01)//response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i("NFCCOmmand", "SENDED Frame : " + Helper.ConvertHexByteArrayToString(ReadMultipleBlockFrame));

                }
            } catch (Exception e) {
                errorOccured++;
                Log.e("NFCCOmmand", "SendReadMultipleBlockCommand errorOccured " + errorOccured);
                if (errorOccured == 3) {
                    Log.e("Exception", "Exception " + e.getMessage());
                    Log.e("NFCCOmmand", "Error when try to read from address  " + (byte) StartAddress[0] + " " + (byte) StartAddress[1]);
                    return response;
                }
            }
        }
        //Used for DEBUG : Log.i("NFCCOmmand", "Response Read Multiple Block" + Helper.ConvertHexByteArrayToString(response));
        return response;
    }


    //***********************************************************************/
    //* the function send an WriteSingle command (0x0A 0x21) || (0x02 0x21)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  DataToWrite : {0x04 0x14 0xFF 0xB2}
    //* the function will write {0x04 0x14 0xFF 0xB2} at the address 0002
    //***********************************************************************/
    //public static byte[] SendWriteSingleBlockCommand (Tag myTag, byte[] StartAddress, byte[] DataToWrite, boolean isBasedOnTwoBytesAddress,boolean isUidRequested, String Manufacturer)
    public byte[] SendWriteSingleBlockCommand(Tag myTag, byte[] StartAddress, byte[] DataToWrite, boolean isBasedOnTwoBytesAddress, boolean isAddressModeNeeded, String Manufacturer) {
        byte[] response = new byte[]{(byte) 0xFF};
        byte[] WriteSingleBlockFrame;

        //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
        if (isAddressModeNeeded) {
            if (isBasedOnTwoBytesAddress) {
                if (Manufacturer == "Texas Instruments")
                    WriteSingleBlockFrame = new byte[]{(byte) 0x6A, (byte) TYPE5_CMD_WRITE_SINGLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], StartAddress[0], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
                else
                    WriteSingleBlockFrame = new byte[]{(byte) 0x2A, (byte) TYPE5_CMD_WRITE_SINGLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], StartAddress[0], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
            } else {
                if (Manufacturer == "Texas Instruments")
                    WriteSingleBlockFrame = new byte[]{(byte) 0x62, (byte) TYPE5_CMD_WRITE_SINGLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
                else {
                    if (StartAddress.length == 2 && StartAddress[0] > 0 ) {
                        WriteSingleBlockFrame = new byte[]{(byte) 0x22, (byte) TYPE5_CMD_EXTENTED_WRITE_SINGLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], StartAddress[0], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
                    }else
                    WriteSingleBlockFrame = new byte[]{(byte) 0x22, (byte) TYPE5_CMD_WRITE_SINGLE_BLOCK, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, StartAddress[1], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
                }
            }
        } else {
            if (isBasedOnTwoBytesAddress) {
                if (Manufacturer == "Texas Instruments")
                    WriteSingleBlockFrame = new byte[]{(byte) 0x4A, (byte) TYPE5_CMD_WRITE_SINGLE_BLOCK, StartAddress[1], StartAddress[0], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
                else
                    WriteSingleBlockFrame = new byte[]{(byte) 0x0A, (byte) TYPE5_CMD_WRITE_SINGLE_BLOCK, StartAddress[1], StartAddress[0], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
            } else {
                if (Manufacturer == "Texas Instruments")
                    WriteSingleBlockFrame = new byte[]{(byte) 0x42, (byte) TYPE5_CMD_WRITE_SINGLE_BLOCK, StartAddress[1], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
                else {
                    if (StartAddress.length == 2 && StartAddress[0] > 0 ) {
                        WriteSingleBlockFrame = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_EXTENTED_WRITE_SINGLE_BLOCK, StartAddress[1], StartAddress[0], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
                    }else
                    WriteSingleBlockFrame = new byte[]{(byte) 0x02, (byte) TYPE5_CMD_WRITE_SINGLE_BLOCK, StartAddress[1], DataToWrite[0], DataToWrite[1], DataToWrite[2], DataToWrite[3]};
                }
            }

        }

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                //Addressed mode added for MODAT531 phones needing Address mode (2015-07-21)
                if (isAddressModeNeeded) {
                    WriteSingleBlockFrame[2] = myTag.getId()[0];
                    WriteSingleBlockFrame[3] = myTag.getId()[1];
                    WriteSingleBlockFrame[4] = myTag.getId()[2];
                    WriteSingleBlockFrame[5] = myTag.getId()[3];
                    WriteSingleBlockFrame[6] = myTag.getId()[4];
                    WriteSingleBlockFrame[7] = myTag.getId()[5];
                    WriteSingleBlockFrame[8] = myTag.getId()[6];
                    WriteSingleBlockFrame[9] = myTag.getId()[7];
                }
                nfcvTag.close();
                nfcvTag.connect();

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "==> SendWriteSingleBlockCommand() request: " + Helper.ConvertHexByteArrayToString(WriteSingleBlockFrame));

                response = nfcvTag.transceive(WriteSingleBlockFrame);

                if (DebugUtility.printNfcCommands)
                    Log.v(TAG, "SendWriteSingleBlockCommand response: " + Helper.ConvertHexByteArrayToString(response));

                if (response[0] == (byte) 0x00 || response[0] == (byte) 0x01) //response 01 = error sent back by tag (new Android 4.2.2) or BC
                {
                    errorOccured = 0;
                    //Used for DEBUG : Log.i("*******", "**SUCCESS** Write Data " + DataToWrite[0] +" "+ DataToWrite[1] +" "+ DataToWrite[2] +" "+ DataToWrite[3] + " at address " +  (byte)StartAddress[0] +" "+ (byte)StartAddress[1]);
                }
            } catch (Exception e) {
                errorOccured++;
                Log.e("NFCCOmmand", "SendWriteSingleBlockCommand  " + errorOccured);
                if (errorOccured == 2) {
                    Log.e("Exception", "Exception " + e.getMessage());
                    Log.e("WRITE", "**ERROR WRITE SINGLE** at address " + Helper.ConvertHexByteArrayToString(StartAddress));
                    return response;
                }
            }
        }
        return response;
    }

    //***********************************************************************/
    //* the function send an Write command (0x0A 0x21) || (0x02 0x21)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  DataToWrite : {0x04 0x14 0xFF 0xB2}
    //* the function will write {0x04 0x14 0xFF 0xB2} at the address 0002
    //***********************************************************************/
    //public static byte[] SendWriteMultipleBlockCommand (Tag myTag, byte[] StartAddress, byte[] DataToWrite, boolean BasedOnTwoBytesAddress,boolean isUidRequested,String Manufacturer)
    public byte[] SendWriteMultipleBlockCommand(Tag myTag, byte[] StartAddress, byte[] DataToWrite, boolean BasedOnTwoBytesAddress, boolean isAddressModeNeeded, String Manufacturer) {
        byte[] response = new byte[]{(byte) 0x01};
        long cpt = 0;

        int NBByteToWrite = DataToWrite.length;
        while (NBByteToWrite % 4 != 0)
            NBByteToWrite++;

        byte[] fullByteArrayToWrite = new byte[NBByteToWrite];
        for (int j = 0; j < NBByteToWrite; j++) {
            if (j < DataToWrite.length) {
                fullByteArrayToWrite[j] = DataToWrite[j];
            } else {
                fullByteArrayToWrite[j] = (byte) 0xFF;
            }
        }


        int intAddress2Write = Helper.Convert2bytesHexaFormatToInt(StartAddress);
        byte[] bytAddress = Helper.ConvertIntTo2bytesHexaFormat(intAddress2Write);

        for (int i = 0; i < NBByteToWrite; i = i + 4) {


            //int incrementAddressStart0 = (StartAddress[0]+i/256)  ;                                //Most Important Byte
            //int incrementAddressStart1 = (StartAddress[1]+i/4) - (incrementAddressStart0*255);    //Less Important Byte

            int incrementAddressStart0 = bytAddress[0];
            int incrementAddressStart1 = bytAddress[1];
            intAddress2Write++;
            bytAddress = Helper.ConvertIntTo2bytesHexaFormat(intAddress2Write);

            response[0] = (byte) 0x01;

            while ((response[0] == (byte) 0x01) && cpt <= 2) {
                response = SendWriteSingleBlockCommand(myTag, new byte[]{(byte) incrementAddressStart0, (byte) incrementAddressStart1}, new byte[]{(byte) fullByteArrayToWrite[i], (byte) fullByteArrayToWrite[i + 1], (byte) fullByteArrayToWrite[i + 2], (byte) fullByteArrayToWrite[i + 3]}, BasedOnTwoBytesAddress, isAddressModeNeeded, Manufacturer);
                cpt++;
            }
            if (response[0] == (byte) 0x01)
                return response;
            cpt = 0;
        }
        return response;
    }

    public int readMaxTransceiveCmd(Tag myTag) {
        int ret = 0;
        try {
            NfcV nfcvTag = NfcV.get(myTag);
            nfcvTag.close();
            nfcvTag.connect();
            ret = nfcvTag.getMaxTransceiveLength();
        } catch (Exception e) {
            Log.e("Exception", "Exception " + e.getMessage());
            //ma.setBasedOnTwoBytesAddress(false);
        }
        return ret;
    }
}
