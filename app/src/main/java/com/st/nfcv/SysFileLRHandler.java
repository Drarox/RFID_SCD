/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1257 $
  * Revision of last commit    :  $Rev: 1257 $
  * Date of last commit     :  $Date: 2015-10-22 16:02:56 +0200 (Thu, 22 Oct 2015) $ 
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

import com.st.NFC.NFCTag;
import com.st.NFC.SysFileGenHandler;

import android.nfc.Tag;
import android.util.Log;

public class SysFileLRHandler implements SysFileGenHandler {

    private String uid;
    private String techno;
    private String manufacturer;
    private String productName;
    private String dsfid;
    private String afi;
    private String memorySize;
    private String blockSize;

    static final String TAG = "SysFileLRHandler";

    private int mconverterMemSize = 0;

    private int mMaxTransceiveLength = 0;


    public int getMaxTransceiveLength() {
        //return mMaxTransceiveLength-1-4; // Work Around .....
        return mMaxTransceiveLength; // Work Around .....
    }

    public void setMaxTransceiveLength(int transceiveLength) {
        //return mMaxTransceiveLength-1-4; // Work Around .....
        mMaxTransceiveLength = transceiveLength;
    }

    private String icReference;
    private boolean basedOnTwoBytesAddress;
    private boolean MultipleReadSupported;
    private boolean MemoryExceed2048bytesSize;
    private boolean AddressModeNeeded;

    private int mMemSize = 0;
    protected int mLength;
    private byte[] mCmdList;


    private int mNbMemoryZone = 0;


    public stnfcRegisterHandler mST25DVRegister;

    public SysFileLRHandler() {
        uid = null;
        techno = null;
        manufacturer = null;
        productName = null;
        dsfid = null;
        afi = null;
        memorySize = null;
        mMemSize = 0;
        blockSize = null;
        icReference = null;
        basedOnTwoBytesAddress = false;
        MultipleReadSupported = false;
        MemoryExceed2048bytesSize = false;
        AddressModeNeeded = false;
        mLength = 0;
        mconverterMemSize = 0;
        mST25DVRegister = null;
        mMaxTransceiveLength = 0;

    }

    public byte getProductCode() {
        return Helper.ConvertStringToHexByte(icReference);
    }

    public int getNbMemoryZone() {
        int ret = 1;
        if (mST25DVRegister != null) {
            ret = mST25DVRegister.getNbMemoryZone(getProductCode());
        }
        return ret;
    }

    public int getZoneAddress(int zone) {
        int ret = 0;

        if (zone > getNbMemoryZone()) {
            Log.e(TAG, "Invalid zone nbr!");
            return 0;
        }

        if (mST25DVRegister != null) {
            ret = mST25DVRegister.getZoneAddress(zone);
        }
        return ret;
    }

    /**
     * Get the memory size of a zone (in Bytes)
     *
     * @param zone
     * @return
     */
    public int getZoneSize(int zone) {
        int ret = 0;

        if (zone > getNbMemoryZone()) {
            Log.e(TAG, "Invalid zone nbr!");
            return 0;
        }

        // Default behavior
        ret = mMemSize;

        // ST25DV specific behavior
        if (mST25DVRegister != null) {
            // inputs are blocks
            ret = mST25DVRegister.getZoneSize(zone, mMemSize/(this.getNbOfBytesPerBlock()+1)) * (this.getNbOfBytesPerBlock() + 1);
        }

        return ret;
    }

    public int getMconverterMemSize() {
        int endZone;
        //int blockSize;
        int availableNDEFSize;
        if (mST25DVRegister != null) {
            endZone = (int) (mST25DVRegister.getKnownRegisterValue(stnfcRegisterHandler.ST25DVRegisterTable.Reg_End1) & 0xFF);
            //blockSize = Helper.ConvertStringToInt(this.getBlockSize().trim());
            // get the block size
            String sTemp;
            int iTemp;
            sTemp = this.blockSize;
            iTemp = 0;
            sTemp = com.st.nfcv.Helper.StringForceDigit(sTemp, 4);
            if (sTemp != null) {
                iTemp = com.st.nfcv.Helper.ConvertStringToInt(sTemp);
                iTemp++;
            }
            availableNDEFSize = ((endZone + 1) * 8) * iTemp;
            return availableNDEFSize;
        }

        return mconverterMemSize;
    }


    public SysFileLRHandler(byte[] GetSystemInfoResponse) {

        decode(GetSystemInfoResponse);
    }


    public boolean decodeRegisterState(Tag myTag) {
        boolean ret = false;
        if (mST25DVRegister == null) {
            mST25DVRegister = new stnfcRegisterHandler();
            ret = mST25DVRegister.readAllSystemRegister(myTag, this);
        } else {
            ret = true;
        }
        return ret;
    }


    public boolean decodeBasicInfo(byte[] data, SysFileLRHandler ma) {
        boolean ret = true;
        int dataLength = data.length;
        if (dataLength >= 12) {

            String uidToString = "";
            byte[] uid = new byte[8];
            // change uid format from byteArray to a String
            for (int i = 1; i <= 8; i++) {
                uid[i - 1] = data[10 - i];
                uidToString += Helper.ConvertHexByteToString(uid[i - 1]);
            }

            //***** TECHNO ******
            ma.setUid(uidToString);
            if (uid[0] == (byte) 0xE0)
                ma.setTechno("ISO 15693");
            else if (uid[0] == (byte) 0xD0)
                ma.setTechno("ISO 14443");
            else
                ma.setTechno("Unknown techno");

            //***** MANUFACTURER ****
            if (uid[1] == (byte) 0x02)
                ma.setManufacturer("STMicroelectronics");
            else if (uid[1] == (byte) 0x04)
                ma.setManufacturer("NXP");
            else if (uid[1] == (byte) 0x07)
                ma.setManufacturer("Texas Instruments");
            else if (uid[1] == (byte) 0x01) //MOTOROLA (updated 20140228)
                ma.setManufacturer("Motorola");
            else if (uid[1] == (byte) 0x03) //HITASHI (updated 20140228)
                ma.setManufacturer("Hitachi");
            else if (uid[1] == (byte) 0x04) //NXP SEMICONDUCTORS
                ma.setManufacturer("NXP");
            else if (uid[1] == (byte) 0x05) //INFINEON TECHNOLOGIES (updated 20140228)
                ma.setManufacturer("Infineon");
            else if (uid[1] == (byte) 0x06) //CYLINC (updated 20140228)
                ma.setManufacturer("Cylinc");
            else if (uid[1] == (byte) 0x07) //TEXAS INSTRUMENTS TAG-IT
                ma.setManufacturer("Texas Instruments");
            else if (uid[1] == (byte) 0x08) //FUJITSU LIMITED (updated 20140228)
                ma.setManufacturer("Fujitsu");
            else if (uid[1] == (byte) 0x09) //MATSUSHITA ELECTRIC INDUSTRIAL (updated 20140228)
                ma.setManufacturer("Matsushita");
            else if (uid[1] == (byte) 0x0A) //NEC (updated 20140228)
                ma.setManufacturer("NEC");
            else if (uid[1] == (byte) 0x0B) //OKI ELECTRIC (updated 20140228)
                ma.setManufacturer("Oki");
            else if (uid[1] == (byte) 0x0C) //TOSHIBA (updated 20140228)
                ma.setManufacturer("Toshiba");
            else if (uid[1] == (byte) 0x0D) //MITSUBISHI ELECTRIC (updated 20140228)
                ma.setManufacturer("Mitsubishi");
            else if (uid[1] == (byte) 0x0E) //SAMSUNG ELECTRONICS (updated 20140228)
                ma.setManufacturer("Samsung");
            else if (uid[1] == (byte) 0x0F) //HUYNDAI ELECTRONICS (updated 20140228)
                ma.setManufacturer("Hyundai");
            else if (uid[1] == (byte) 0x10) //LG SEMICONDUCTORS (updated 20140228)
                ma.setManufacturer("LG");
            else
                ma.setManufacturer("Unknown manufacturer");

            // Retrieve the ICRef from response
            byte ICrefProcessing = 0;
            byte lICRefUID = uid[2];
            byte lICRefSystemResponse = getSystemInfoICRef(data);
            setIcReference(Helper.ConvertHexByteToString(lICRefSystemResponse));

            if (uid[1] == (byte) 0x02) {

                if (lICRefUID != lICRefSystemResponse) {
                    ICrefProcessing = lICRefSystemResponse;
                } else {
                    ICrefProcessing = lICRefUID;
                }

                //**** PRODUCT NAME *****
                if (ICrefProcessing >= (byte) 0x04 && ICrefProcessing <= (byte) 0x07) {
                    ma.setProductName("LRI512");
                    ma.setMultipleReadSupported(false);
                    ma.setMemoryExceed2048bytesSize(false);
                } else if (ICrefProcessing >= (byte) 0x14 && ICrefProcessing <= (byte) 0x17) {
                    ma.setProductName("LRI64");
                    ma.setMultipleReadSupported(false);
                    ma.setMemoryExceed2048bytesSize(false);
                } else if (ICrefProcessing >= (byte) 0x20 && ICrefProcessing <= (byte) 0x23) {
                    ma.setProductName("LRI2K");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(false);
                } else if (ICrefProcessing >= (byte) 0x28 && ICrefProcessing <= (byte) 0x2B) {
                    ma.setProductName("LRIS2K");
                    ma.setMultipleReadSupported(false);
                    ma.setMemoryExceed2048bytesSize(false);
                } else if (ICrefProcessing >= (byte) 0x2C && ICrefProcessing <= (byte) 0x2F) {
                    ma.setProductName("M24LR64");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                } else if (ICrefProcessing >= (byte) 0x40 && ICrefProcessing <= (byte) 0x43) {
                    ma.setProductName("LRI1K");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(false);
                } else if (ICrefProcessing >= (byte) 0x44 && ICrefProcessing <= (byte) 0x47) {
                    ma.setProductName("LRIS64K");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                    ma.setBasedOnTwoBytesAddress(true);
                    if (ma.isBasedOnTwoBytesAddress() == false)
                        return false;
                } else if (ICrefProcessing >= (byte) 0x48 && ICrefProcessing <= (byte) 0x4B) {
                    ma.setProductName("M24LR01E");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(false);
                } else if (ICrefProcessing >= (byte) 0x4C && ICrefProcessing <= (byte) 0x4F) {
                    ma.setProductName("M24LR16E");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                    ma.setBasedOnTwoBytesAddress(true);
                    if (ma.isBasedOnTwoBytesAddress() == false)
                        return false;
                } else if (ICrefProcessing >= (byte) 0x50 && ICrefProcessing <= (byte) 0x53) {
                    ma.setProductName("M24LR02E");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(false);
                } else if (ICrefProcessing >= (byte) 0x54 && ICrefProcessing <= (byte) 0x57) {
                    ma.setProductName("M24LR32E");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                    ma.setBasedOnTwoBytesAddress(true);
                    if (ma.isBasedOnTwoBytesAddress() == false)
                        return false;
                } else if (ICrefProcessing >= (byte) 0x58 && ICrefProcessing <= (byte) 0x5B) {
                    ma.setProductName("M24LR04E");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                } else if (ICrefProcessing >= (byte) 0x5C && ICrefProcessing <= (byte) 0x5F) {
                    ma.setProductName("M24LR64E");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                    ma.setBasedOnTwoBytesAddress(true);
                    if (ma.isBasedOnTwoBytesAddress() == false)
                        return false;
                } else if (ICrefProcessing >= (byte) 0x60 && ICrefProcessing <= (byte) 0x63) {
                    ma.setProductName("M24LR08E");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                } else if (ICrefProcessing >= (byte) 0x64 && ICrefProcessing <= (byte) 0x67) {
                    ma.setProductName("M24LR128E");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                    ma.setBasedOnTwoBytesAddress(true);
                    if (ma.isBasedOnTwoBytesAddress() == false)
                        return false;
                } else if (ICrefProcessing >= (byte) 0x6C && ICrefProcessing <= (byte) 0x6F) {
                    ma.setProductName("M24LR256E");
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                    ma.setBasedOnTwoBytesAddress(true);
                    if (ma.isBasedOnTwoBytesAddress() == false)
                        return false;
                } else if (ICrefProcessing >= (byte) 0xF8 && ICrefProcessing <= (byte) 0xFB) {
                    ma.setProductName("detected product");
                    ma.setBasedOnTwoBytesAddress(true);
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                } else if (ICrefProcessing == 0x26) {
                    ma.setProductName("ST25DV64E");
                    ma.setBasedOnTwoBytesAddress(false);
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                } /* else if (ICrefProcessing == 0x25) {
                    ma.setProductName("ST25DV16E");
                    ma.setBasedOnTwoBytesAddress(false);
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                } */ else if (ICrefProcessing == 0x24) {
                    ma.setProductName("ST25DV04k");
                    ma.setBasedOnTwoBytesAddress(false);
                    ma.setMultipleReadSupported(true);
                    ma.setMemoryExceed2048bytesSize(true);
                }   else {
                    ma.setProductName("Unknown product");
                    ma.setBasedOnTwoBytesAddress(false);
                    ma.setMultipleReadSupported(false);
                    ma.setMemoryExceed2048bytesSize(false);
                }

            } else {
                ma.setProductName("Unknown product");
                ma.setBasedOnTwoBytesAddress(false);
                ma.setMultipleReadSupported(false);
                ma.setMemoryExceed2048bytesSize(false);

            }
        }
        return ret;
    }

    public boolean decode(byte[] GetSystemInfoResponse) {
        mLength = GetSystemInfoResponse.length;

        if (GetSystemInfoResponse.length >= 11) {

            String uidToString = "";
            byte[] uid = new byte[8];
            // change uid format from byteArray to a String
            for (int i = 1; i <= 8; i++) {
                uid[i - 1] = GetSystemInfoResponse[10 - i];
                uidToString += Helper.ConvertHexByteToString(uid[i - 1]);
            }

            //***** TECHNO ******
            setUid(uidToString);
            if (uid[0] == (byte) 0xE0)
                setTechno("ISO 15693");
            else if (uid[0] == (byte) 0xD0)
                setTechno("ISO 14443");
            else
                setTechno("Unknown techno");


            // Retrieve the ICRef from response
            byte ICrefProcessing = 0;
            byte lICRefUID = uid[2];
            byte lICRefSystemResponse = getSystemInfoICRef(GetSystemInfoResponse);

            if (uid[1] == (byte) 0x02) {
                ICrefProcessing =lICRefSystemResponse;

                if (this.productName.contains("ST25DV")) {
                    setDsfid(Helper.ConvertHexByteToString(GetSystemInfoResponse[10]));
                    //*** AFI ***
                    setAfi(Helper.ConvertHexByteToString(GetSystemInfoResponse[11]));
                    //*** IC REFERENCE ***
                    setIcReference(Helper.ConvertHexByteToString(GetSystemInfoResponse[12]));

                    //*** MEMORY SIZE ***
                    String temp = new String();
                    int memesize = 0x7ff;
                    byte blocksize = 3;
                    if (ICrefProcessing == 0x26) {
                        // memesize = 0x7ff;
                        temp += 2047;
                        //temp += Helper.ConvertHexByteToString((byte) 0xFF);
                        //mMemSize = (((byte) 0x07 & 0xFF) << 8) + ((int) 0xFF & 0xFF);
                        mMemSize = 8192;
                        setMemorySize(temp);

                    } /* else if (ICrefProcessing == 0x25 || ICrefProcessing == 0x05) {
                        //memesize = 0x7f;
                        //temp += Helper.ConvertHexByteToString((byte) 0x7F);
                        temp += 511;
                        //mMemSize = 0x7F & 0xFF;
                        mMemSize = 2048;
                        setMemorySize(temp);
                    }*/ else if (ICrefProcessing == 0x24) {
                        //memesize = 0x7f;
                        temp += 127;
                        mMemSize = 512;
                        setMemorySize(temp);
                    } else {
                        //memesize = 0;
                        temp += Helper.ConvertHexByteToString((byte) 0x00);
                        mMemSize = 0x00 & 0xFF;
                        setMemorySize(temp);
                    }

                    //*** BLOCK SIZE ***
                    setBlockSize(Helper.ConvertHexByteToString(blocksize));


                } else {
                    // LR compatibility .....
                    // ======================================================
                    //*** DSFID ***
                    setDsfid(Helper.ConvertHexByteToString(GetSystemInfoResponse[10]));

                    //*** AFI ***
                    setAfi(Helper.ConvertHexByteToString(GetSystemInfoResponse[11]));

                    //*** MEMORY SIZE ***
                    if (isBasedOnTwoBytesAddress()) {
                        String temp = new String();
                        temp += Helper.ConvertHexByteToString(GetSystemInfoResponse[13]);
                        //temp.trim();
                        temp += Helper.ConvertHexByteToString(GetSystemInfoResponse[12]);
                        temp.trim();
                        // Memory map bytes per blocks
                        int iTemp = 0;
                        String sTemp = null;
                        sTemp = temp;
                        sTemp = com.st.nfcv.Helper.StringForceDigit(sTemp, 4);
                        if (sTemp != null) {
                            iTemp = com.st.nfcv.Helper.ConvertStringToInt(sTemp);
                        }
                        setMemorySize(String.valueOf(iTemp));
                        //*** BLOCK SIZE ***
                        setBlockSize(Helper.ConvertHexByteToString(GetSystemInfoResponse[14]));
                        int blockSize =  GetSystemInfoResponse[14];
                        // =========================
                        int nbblocks = iTemp++;
                        mMemSize = (nbblocks + 1) * (blockSize + 1);


                    } else {
                        int nbblocks = GetSystemInfoResponse[12];
                        //*** BLOCK SIZE ***
                        if (GetSystemInfoResponse.length >= 14) {
                            setBlockSize(Helper.ConvertHexByteToString(GetSystemInfoResponse[13]));
                            int blockSize =  GetSystemInfoResponse[13];

                            mMemSize = (nbblocks + 1) * (blockSize + 1);
                            setMemorySize(Integer.toString(nbblocks));
                        }
                    }

                    //*** IC REFERENCE ***
                    if (isBasedOnTwoBytesAddress())

                        if (GetSystemInfoResponse.length >= 16) setIcReference(Helper.ConvertHexByteToString(GetSystemInfoResponse[15]));
                    else
                        if (GetSystemInfoResponse.length >= 15) setIcReference(Helper.ConvertHexByteToString(GetSystemInfoResponse[14]));

                }
                computeMemSize();
            } else {
                setProductName("Unknown product");
                setBasedOnTwoBytesAddress(false);
                setMultipleReadSupported(false);
                setMemoryExceed2048bytesSize(false);
                systemInfoSetParam(GetSystemInfoResponse);
                mconverterMemSize = ((int)GetSystemInfoResponse[12] + 1) * ((int)GetSystemInfoResponse[13] + 1);
                mMemSize = mconverterMemSize;
            }
            return true;
        }

        // in case of Inventory OK and Get System Info HS
        else if (getTechno() == "ISO 15693") {
            setProductName("Unknown product");
            setBasedOnTwoBytesAddress(false);
            setMultipleReadSupported(false);
            setMemoryExceed2048bytesSize(false);
            setAfi("00 ");
            setDsfid("00 ");
            setMemorySize("003F ");        //changed 22-10-2014
            setBlockSize("03 ");
            setIcReference("00 ");
            //computeMemSize();
            mconverterMemSize = 64 * 4; // 3F * 4
            mMemSize = mconverterMemSize;
            return true;
        }

        //if the tag has returned an error code
        else {
            //computeMemSize();
            return false;
        }

    }

    private byte getSystemInfoICRef(byte[] GetSystemInfoResponse) {
        byte ret;
        ret = 0;
        byte infofieldanswer = GetSystemInfoResponse[1];
        byte ret_infoflagprocess = 0;
        ret_infoflagprocess = (byte) (infofieldanswer & 0x08);
        if (ret_infoflagprocess == 8) { // ICRef
            ret = GetSystemInfoResponse[GetSystemInfoResponse.length - 1];
        }
        return ret;
    }
    private byte systemInfoSetParam(byte[] GetSystemInfoResponse) {
        byte ret;
        ret = 0;
        byte infofieldanswer = GetSystemInfoResponse[1];
        int currentindex = 10;
        byte ret_infoflagprocess = 0;
        ret_infoflagprocess = (byte) (infofieldanswer & 0x01);// DSFID
        if (ret_infoflagprocess == 1 && currentindex<GetSystemInfoResponse.length) { // DSFID
            setDsfid(Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex]));
            currentindex++;
        } else {
            setDsfid("00 ");
        }

        ret_infoflagprocess = (byte) (infofieldanswer & 0x02);
        if (ret_infoflagprocess == 2 && currentindex<GetSystemInfoResponse.length) { // AFI
            //setAfi("00 ");
            setAfi(Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex]));
            currentindex++;
        } else {
            setAfi("00 ");
        }

        // Memory size.......... between curentIndex and Length-1
        ret_infoflagprocess = (byte) (infofieldanswer & 0x04);
        if (ret_infoflagprocess == 4 && currentindex<GetSystemInfoResponse.length) { // Meme size present
            int nbBytesForMemoryInfo = (GetSystemInfoResponse.length - 1) - currentindex;
            if (nbBytesForMemoryInfo > 0) {
                if (nbBytesForMemoryInfo == 1 ) {
                    // ??
                    setBlockSize("03 ");
                    setMemorySize(Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex]));
                }
                if (nbBytesForMemoryInfo == 2 && (currentindex+1) <GetSystemInfoResponse.length) {
                    // block size + meme size on 1 byte each
                    //setMemorySize("FF ");
                    setMemorySize(Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex]));
                    //setBlockSize("03 ");
                    setBlockSize(Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex + 1]));
                    //setIcReference("00 ");

                }
                if (nbBytesForMemoryInfo == 3 && (currentindex+2) <GetSystemInfoResponse.length) {
                    // block size + meme size on 2 bytes
                    String temp = new String();
                    temp += Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex + 1]);
                    //temp.trim();
                    temp += Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex]);
                    temp.trim();
                    // Memory map bytes per blocks
                    int iTemp = 0;
                    String sTemp = null;
                    sTemp = temp;
                    sTemp = com.st.nfcv.Helper.StringForceDigit(sTemp, 4);
                    if (sTemp != null) {
                        iTemp = com.st.nfcv.Helper.ConvertStringToInt(sTemp);
                    }
                    setMemorySize(String.valueOf(iTemp));
                    //*** BLOCK SIZE ***
                    setBlockSize(Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex+2]));
                }

            } else {
                setMemorySize("3F ");
                setBlockSize("03 ");
            }

        }

        ret_infoflagprocess = (byte) (infofieldanswer & 0x08);
        if (ret_infoflagprocess == 8) { // ICRef
            setIcReference(Helper.ConvertHexByteToString(GetSystemInfoResponse[GetSystemInfoResponse.length - 1]));
            currentindex++;
        } else {
            setIcReference("00 ");
        }
        return ret;
    }

    public boolean decodeExtended(byte[] GetSystemInfoResponse, byte infoparamfield, NFCTag cTag) {
        boolean ret = true;
        String uidToString = "";
        byte[] uid = new byte[8];

        // change uid format from byteArray to a String
        for (int i = 1; i <= 8; i++) {
            uid[i - 1] = GetSystemInfoResponse[10 - i];
            uidToString += Helper.ConvertHexByteToString(uid[i - 1]);
        }
        byte infofieldanswer = GetSystemInfoResponse[1];
        int currentindex = 10;
        int offset = 0;
        byte ret_infoflagprocess = 0;
        ret_infoflagprocess = (byte) (infofieldanswer & 0x01);// DSFID
        if (ret_infoflagprocess == 1) { // DSFID
            setDsfid(Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex]));
            currentindex++;

        }
        ret_infoflagprocess = (byte) (infofieldanswer & 0x02);
        if (ret_infoflagprocess == 2) { // AFI
            setDsfid(Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex]));
            currentindex++;

        }
        ret_infoflagprocess = (byte) (infofieldanswer & 0x04);
        int nbblocks = 0;
        if (ret_infoflagprocess == 4) { // Meme size present

            byte byte3 = GetSystemInfoResponse[currentindex - offset];
            byte byte2 = GetSystemInfoResponse[currentindex + 1 - offset];
            byte byte1 = GetSystemInfoResponse[currentindex + 2 - offset];
            nbblocks = ((byte2 & 0x0F) << 8) + (byte3 & 0xFF);
            setMemorySize(Integer.toString(nbblocks));

            //setBlockSize("03 ");
            setBlockSize(Helper.ConvertHexByteToString((byte) (byte1 & 0x7F)));
            //setIcReference("00 ");
            mMemSize = (nbblocks+1) * (getNbOfBytesPerBlock()+1);
            int memoryNumberOfBlocks = getMemoryNbOfBlocks();
            int bytesPerBlock = getNbOfBytesPerBlock();
            //mMemSize = (memoryNumberOfBlocks) * (bytesPerBlock+1);
            mconverterMemSize = mMemSize;
            currentindex = currentindex + 3;
        }

        ret_infoflagprocess = (byte) (infofieldanswer & 0x08);
        byte icRef = 0;
        if (ret_infoflagprocess == 8) { // ICRef
            icRef = GetSystemInfoResponse[currentindex - offset];
            this.setIcReference(Helper.ConvertHexByteToString(GetSystemInfoResponse[currentindex - offset]));
            currentindex++;

        }
        // In case we need to update/complete Product information found according to uid info and new way of managinf ICref and UID
        cTag.updateProductInformation(uid[2], nbblocks,icRef);

        ret_infoflagprocess = (byte) (infofieldanswer & 0x10);
        if (ret_infoflagprocess == 10) { // MOI
            // WARNING --- setBasedOnTwoBytesAddress(true);
            setBasedOnTwoBytesAddress(false);
            setMemoryExceed2048bytesSize(true);
            currentindex++;

        }
        ret_infoflagprocess = (byte) (infofieldanswer & 0x20);
        if (ret_infoflagprocess == 32) { // CmdList on 4 bytes
            mCmdList = new byte[4];
            mCmdList[0] = GetSystemInfoResponse[currentindex - offset];
            currentindex++;
            mCmdList[1] = GetSystemInfoResponse[currentindex - offset];
            currentindex++;
            mCmdList[2] = GetSystemInfoResponse[currentindex - offset];
            currentindex++;
            mCmdList[3] = GetSystemInfoResponse[currentindex - offset];
            currentindex++;
            // need to know decoding way of doing
        }

        return ret;
    }

    private void computeMemSize() {
        int blocksize = 0;
        int nbblocks = 0;
        String memsTemp = getBlockSize();
        if (memsTemp != null) {
            memsTemp = com.st.nfcv.Helper.StringForceDigit(memsTemp, 4);
            blocksize = com.st.nfcv.Helper.ConvertStringToInt(memsTemp);
            blocksize++;
            memsTemp = getMemorySize();
            if (memsTemp != null) {
                memsTemp = com.st.nfcv.Helper.StringForceDigit(memsTemp, 4);
                nbblocks = Integer.valueOf(memsTemp);
                nbblocks++;

            }

        }

        mconverterMemSize = blocksize * nbblocks;

    }

    public int getMemoryNbOfBlocks() {

        int nbBlock = 0;
        int memSizeInBytes = this.mMemSize;
        String sTemp = this.getBlockSize();
        int nbBytesInBlock = 3;
        if (sTemp != null) {
            sTemp = sTemp.replace(" ", "");
            nbBytesInBlock = Helper.ConvertStringToInt(sTemp);
        } else {

        }

        nbBlock = memSizeInBytes / (nbBytesInBlock + 1);
        return nbBlock;
    }

    public int getNbOfBytesPerBlock() {

        String sTemp = this.getBlockSize();
        int nbBytesInBlock = 3;
        if (sTemp != null) {
            sTemp = sTemp.replace(" ", "");
            nbBytesInBlock = Helper.ConvertStringToInt(sTemp);
        } else {

        }
        return nbBytesInBlock;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }
    public byte getProductRefInUid() {
        byte productRefInUid = -1;
        if (this.uid !=null && this.uid.getBytes().length >= 3) {
            String uidStr = this.getUid();
            String [] strArray =  uidStr.split(" ");
            productRefInUid = Helper.ConvertStringToHexByte(strArray[2]);
        }
        return productRefInUid;
    }
    public String getUid() {
        return uid;
    }

    public void setTechno(String techno) {
        this.techno = techno;
    }

    public String getTechno() {
        return techno;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setDsfid(String dsfid) {
        this.dsfid = dsfid;
    }

    public String getDsfid() {
        return dsfid;
    }

    public void setAfi(String afi) {
        this.afi = afi;
    }

    public String getAfi() {
        return afi;
    }

    public void setMemorySize(String memorySize) {
        this.memorySize = memorySize;
    }

    public String getMemorySize() {
        return this.memorySize;
    }

    public int getMemorySizeInt() {
        return this.mMemSize;
    }

    public void setIcReference(String icReference) {
        this.icReference = icReference;
    }

    public String getIcReference() {
        return icReference;
    }

    public void setBasedOnTwoBytesAddress(boolean basedOnTwoBytesAddress) {
        this.basedOnTwoBytesAddress = basedOnTwoBytesAddress;
    }

    public boolean isBasedOnTwoBytesAddress() {
        return basedOnTwoBytesAddress;
    }

    public void setMultipleReadSupported(boolean MultipleReadSupported) {
        this.MultipleReadSupported = MultipleReadSupported;
    }

    public boolean isMultipleReadSupported() {
        return MultipleReadSupported;
    }

    public void setMemoryExceed2048bytesSize(boolean MemoryExceed2048bytesSize) {
        this.MemoryExceed2048bytesSize = MemoryExceed2048bytesSize;
    }

    public boolean isMemoryExceed2048bytesSize() {
        return MemoryExceed2048bytesSize;
    }

    public void setAddressModeNeeded(boolean AddressModeNeeded) {
        this.AddressModeNeeded = AddressModeNeeded;
    }

    public boolean isUidRequested() {
        return AddressModeNeeded;
    }

    public void setBlockSize(String blockSize) {
        this.blockSize = blockSize;
    }

    public String getBlockSize() {
        return blockSize;
    }


    @Override
    public int getSYSLength() {
        // TODO Auto-generated method stub
        return this.mLength;
    }


    @Override
    public byte getProductVersion() {
        // TODO Auto-generated method stub
        return 0;
    }


}
