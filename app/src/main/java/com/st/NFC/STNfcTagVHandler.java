/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1207 $
  * Revision of last commit    :  $Rev: 1207 $
  * Date of last commit     :  $Date: 2015-10-02 17:29:12 +0200 (Fri, 02 Oct 2015) $
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
package com.st.NFC;




import com.st.nfcv.Helper;
import com.st.nfcv.NFCCommandVExtended;
import com.st.nfcv.NFCCommandVLR;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcRegisterHandler;
import com.st.nfcv.stnfcccLRhandler;
import com.st.nfcv.stnfcm24LRBasicOperation;
import com.st.util.GenErrorAppReport;

import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;

/**
 * @author MMY
 *
 */
public class STNfcTagVHandler implements stnfcTagGenHandler {


    private String TAG = "stnfcTagVHandler";
    private static final int MAX_NB_SUPPORTED_NDEF = 10;
    private static final int TAG_STATE_UNKNOW = 0xFF;


    protected int currentTagState;
    protected int _currentIDFile; // current Field ID get from corresponding TLVBlock

    protected NFCCommandVLR mTypeVTagOperation;
    protected Tag currentTag;
    protected String _modelName;

    /**
     *
     */
    public STNfcTagVHandler() {
        // TODO Auto-generated constructor stub
        currentTagState = TAG_STATE_UNKNOW;
        //currentTag = null;
        //isoDepCurrentTag = null;
        //_lasttranscieveAnswer = new Iso7816_4Err();
        _currentIDFile = 0;
        //m_m24ProtectionLockMgt = null;
        mTypeVTagOperation = null;
    }
    public STNfcTagVHandler(Tag tagToHandle, String modelName)
    {
        currentTagState = TAG_STATE_UNKNOW;
        //currentTag = tagToHandle;
        //_lasttranscieveAnswer = new Iso7816_4Err();
        currentTag = tagToHandle;
        _modelName = modelName;
        mTypeVTagOperation = new NFCCommandVLR(modelName);
    }

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#decode()
     */
/*    @Override
    public void decode() {
        // TODO Auto-generated method stub

    }*/

    public NFCCommandVLR getTypeVTagOperation() {
        return mTypeVTagOperation;
    }

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#closeConnection()
     */
    @Override
    public void closeConnection() {
        // TODO Auto-generated method stub
        try {

            if (currentTag != null) {
                NfcV nfcvTag = NfcV.get(currentTag);
                nfcvTag.close();
            }
        } catch (Exception ex) {
            Log.e(TAG, "closeConnection errorOccured " + ex.toString());

        }

    }





    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#requestSysSelect()
     */
    @Override
    public int requestSysSelect() {
        // TODO Auto-generated method stub
        return 1;
    }
    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#requestSysReadLength()
     */
    @Override
    public int requestSysReadLength() {
        // TODO Auto-generated method stub
        return 1;
    }
    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#requestSysRead(int, byte[])
     */
    @Override
    public int requestSysRead(int size, byte[] buffer) {
        // TODO Auto-generated method stub
        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler)(tt.getSYSHandler());
        byte[] GetSystemInfoAnswer = null;
        GetSystemInfoAnswer = this.mTypeVTagOperation.SendGetSystemInfoCommandCustom(tt.getTag(), sysHDL);
        if (GetSystemInfoAnswer.length <= 2) {
            // issue in cmd - returned only error info
            Log.v(this.getClass().getName(), "cmd requestSysRead: " + Helper.ConvertHexByteArrayToString(GetSystemInfoAnswer));
            return 0;
        }
        else {
            // decode answer
            boolean ret_decode = false;
            ret_decode = sysHDL.decodeBasicInfo(GetSystemInfoAnswer, sysHDL);
            if (ret_decode) {
                GetSystemInfoAnswer = this.mTypeVTagOperation.SendGetSystemInfoCommandCustom(tt.getTag(), sysHDL);
                ret_decode = sysHDL.decode(GetSystemInfoAnswer);
            }

            // Additional info to be retrieve concerning max data that can be given to the tranceive cmd
            sysHDL.setMaxTransceiveLength(this.mTypeVTagOperation.readMaxTransceiveCmd(tt.getTag()));

            // Additional cmd for ST25DV
            if (sysHDL.getProductName().contains("ST25DV") && ret_decode == true) {
                // Process Extended GetSys info
                GetSystemInfoAnswer = null;
                NFCCommandVExtended LRcmdExtended = new NFCCommandVExtended(tt.getModel());
                int cpt = 0;
                while ((GetSystemInfoAnswer == null || GetSystemInfoAnswer[0] == 1) && cpt < 10) {
                    GetSystemInfoAnswer = LRcmdExtended.SendGetSystemInfoCommandExtended(tt.getTag(), sysHDL, (byte) 0x3F);
                    cpt++;
                }
                if (GetSystemInfoAnswer.length <= 2 || cpt == 10) {
                    // issue in cmd - returned only error info
                    Log.e(this.getClass().getName(), "cmd error SendGetSystemInfoCommandExtended: " + Helper.ConvertHexByteArrayToString(GetSystemInfoAnswer));
                } else {
                    // Update info corresponding to Extended system info cmd
                    Log.v(this.getClass().getName(), "cmd succeed SendGetSystemInfoCommandExtended: " + Helper.ConvertHexByteArrayToString(GetSystemInfoAnswer));
                    ret_decode = sysHDL.decodeExtended(GetSystemInfoAnswer, (byte) 0x3F,tt);
                }
                // Decode System registers
                if (sysHDL.decodeRegisterState(tt.getTag()) == true) {
                    Log.v(this.getClass().getName(), "decodeRegisterState process succeed...");
                } else {
                    Log.e(this.getClass().getName(), "decodeRegisterState process failled...");
                }
                // Patch to use cmd on two bytes addresse
                //sysHDL.setBasedOnTwoBytesAddress(true);
            }

            if (ret_decode) return 1;
            else return 0;
        }
    }


    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#requestCCSelect()
     */
    @Override
    public int requestCCSelect() {
        // TODO Auto-generated method stub
          byte[] resultBlock0 = null;
       int cpt = 0;

       NFCTag tt = NFCApplication.getApplication().getCurrentTag();
       SysFileLRHandler sysHDL = (SysFileLRHandler)(tt.getSYSHandler());
       int startAddress = sysHDL.getZoneAddress(tt.getCurrentValideTLVBlokID());
       byte[] stAdr =  Helper.ConvertIntTo2bytesHexaFormatBis(startAddress);
       while ((resultBlock0 == null || resultBlock0[0] == 1) && cpt <10)
        {
            resultBlock0 = this.mTypeVTagOperation.SendReadSingleBlockCommand(this.currentTag, stAdr, sysHDL.isBasedOnTwoBytesAddress(),sysHDL.isUidRequested());
            cpt ++;
           //Used for DEBUG : Log.v("CPT ", " CPT Read Block 0 ===> " + String.valueOf(cpt));
        }

        //NDEF format : 4th first bytes of NDEF header
        //CC0 = E1h  =  NDEF message is present
        //CC1 = bit7-6 : Major version
        //         bit5-4 : Minor version
        //         bit3-2 : Read access (00:free access)
        //         bit1-0 : Write access (00:free access / 10:write need password / 11:no write access)
        //CC2 = Memory size of data field (CC2 *8)
        //CC3 = bit7-3 : rfu
        //         bit2 : IC memory exceed 2040 bytes
        //         bit1 : rfu
        //         bit0 : 1=support Multiple read / read single only
       if (cpt >=10 || resultBlock0[0] == 1) {
           if (resultBlock0.length >= 2)return resultBlock0[1];
           else return 0;
       }
       else {
           return 1;
       }

    }

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#requestCCReadLength()
     */
    @Override
    public int requestCCReadLength() {
        // TODO Auto-generated method stub
              byte[] resultBlock0 = null;
           int cpt = 0;

           NFCTag tt = NFCApplication.getApplication().getCurrentTag();
           SysFileLRHandler sysHDL = (SysFileLRHandler)(tt.getSYSHandler());
        int startAddress = sysHDL.getZoneAddress(tt.getCurrentValideTLVBlokID());
        byte[] stAdr =  Helper.ConvertIntTo2bytesHexaFormatBis(startAddress);

           while ((resultBlock0 == null || resultBlock0[0] == 1) && cpt <10)
            {
                resultBlock0 = this.mTypeVTagOperation.SendReadSingleBlockCommand(this.currentTag, stAdr, sysHDL.isBasedOnTwoBytesAddress(),sysHDL.isUidRequested());
                cpt ++;
               //Used for DEBUG : Log.v("CPT ", " CPT Read Block 0 ===> " + String.valueOf(cpt));
            }

            //NDEF format : 4th first bytes of NDEF header
            //CC0 = E1h  =  NDEF message is present
            //CC1 = bit7-6 : Major version
            //         bit5-4 : Minor version
            //         bit3-2 : Read access (00:free access)
            //         bit1-0 : Write access (00:free access / 10:write need password / 11:no write access)
            //CC2 = Memory size of data field (CC2 *8)
            //CC3 = bit7-3 : rfu
            //         bit2 : IC memory exceed 2040 bytes
            //         bit1 : rfu
            //         bit0 : 1=support Multiple read / read single only
           if (cpt >=10 || resultBlock0[0] == 1) return 0;
           else
           {
                 if(resultBlock0[0]==(byte)0x00 && (resultBlock0[1]==(byte)0xE1 || resultBlock0[1]==(byte)0xE2))
                 {
                     if (resultBlock0[3] == 0x00) {
                         return 8;

                     } else
                         return 4; // return resultBlock0[3];
                 } else {
                     return -1;
                 }

           }


    }


    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#requestCCRead(int, byte[])
     */
    @Override
    public int requestCCRead(int size, byte[] buffer) {
        // TODO Auto-generated method stub
           byte[] resultBlock0 = null;

        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler)(tt.getSYSHandler());
        int startAddress = sysHDL.getZoneAddress(tt.getCurrentValideTLVBlokID());
        byte[] stAdr =  Helper.ConvertIntTo2bytesHexaFormatBis(startAddress);

        int cpt = 0;
        byte[] bytAddress = new byte[2];
        bytAddress = Helper.ConvertIntTo2bytesHexaFormat(size);
        while ((resultBlock0 == null || resultBlock0[0] == 1) && cpt <10)        {
            //resultBlock1 = NFCCommand.SendReadSingleBlockCommand(dataDevice.getCurrentTag(), new byte[]{0x00,0x01}, dataDevice);
        //resultBlock0 = NFCCommandV.Send_several_ReadSingleBlockCommands(this.currentTag,new byte[]{0x00,0x00}, bytAddress, sysHDL.isBasedOnTwoBytesAddress(),sysHDL.isUidRequested());
        resultBlock0 = this.mTypeVTagOperation.Send_several_ReadSingleBlockCommands(this.currentTag,stAdr, bytAddress, sysHDL.isBasedOnTwoBytesAddress(),sysHDL.isUidRequested());
        cpt ++;
           //Used for DEBUG : Log.v("CPT ", " CPT Read Block 0 ===> " + String.valueOf(cpt));
        }

        if (cpt >=10 || resultBlock0[0] == 1) return 0; else {
            System.arraycopy(resultBlock0, 1, buffer, 0, size);
            return 1;
        }

    }



    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#readNdeflength()
     */
    @Override
    public int readNdeflength() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#readNdefBinary(byte[])
     */
    @Override
    public int readNdefBinary(byte[] ndefbuffer) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#updateBinary(byte[])
     */
    @Override
    public boolean updateBinary(byte[] binary) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        boolean ret = false;
        int canWrite = 0;
        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (tt.getSYSHandler());
        stnfcccLRhandler cchandler = (stnfcccLRhandler) tt.getCCHandler();

        //int nbBlock = sysHDL.getZoneSize(tt.getCurrentValideTLVBlokID());
        int nbBlock = 0;

        int blocks = Helper.ConvertStringToInt(sysHDL.getBlockSize().replace(" ", ""));

        int memSizeInBytes = sysHDL.getZoneSize(tt.getCurrentValideTLVBlokID());
        String sTemp = sysHDL.getBlockSize();
        int nbBytesInBlock = 3;
        if (sTemp != null) {
            sTemp = sTemp.replace(" ", "");
            nbBytesInBlock = Helper.ConvertStringToInt(sTemp);
        } else {

        }
        nbBlock = memSizeInBytes/(nbBytesInBlock+1);
        blocks = nbBytesInBlock;
        int binaryLength = binary.length;
        byte[] ccByteArray = cchandler.CreateCCFileByteArray(sysHDL.isMultipleReadSupported(),
                sysHDL.isMemoryExceed2048bytesSize(),
                nbBlock,
                blocks);
        byte[] TLByteArray = cchandler.CreateTLFileByteArray(binaryLength);
        byte[] TLTerminatorByteArray = cchandler.CreateTLTerminatorFileByteArray();
/*

        if (cchandler.getWriteAccess() != 0x00 || !tt.getSTTagHandler().isNDEFWriteUnLocked()) {

        }
*/

        canWrite = 1;

        if (canWrite == 1) {
            stnfcm24LRBasicOperation bop = new stnfcm24LRBasicOperation(sysHDL.getMaxTransceiveLength());
            //byte[] addressStart = Helper.ConvertIntTo2bytesHexaFormat(0x00);
            int startAddress = sysHDL.getZoneAddress(tt.getCurrentValideTLVBlokID());
            byte[] stAdr =  Helper.ConvertIntTo2bytesHexaFormatBis(startAddress);

            if (bop.m24LRWriteNDEFBasicOp(stAdr, ccByteArray, TLByteArray, binary,
                    TLTerminatorByteArray) == 0) {
                // ok
                ret = true;
            } else {
                // write error
                ret = false;
            }
        }
        // ======================
        return ret;
    }

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#updateBinarywithPassword(byte[], byte[])
     */
    @Override
    public boolean updateBinarywithPassword(byte[] binary, byte[] password) {
        // TODO Auto-generated method stub
        boolean ret = false;
        int canWrite = 0;
        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (tt.getSYSHandler());
        stnfcccLRhandler cchandler = (stnfcccLRhandler) tt.getCCHandler();

        int nbBlock  = 0;
        int memSizeInBytes = sysHDL.getZoneSize(tt.getCurrentValideTLVBlokID());
        String sTemp = sysHDL.getBlockSize();
        int nbBytesInBlock = 3;
        if (sTemp != null) {
            sTemp = sTemp.replace(" ", "");
            nbBytesInBlock = Helper.ConvertStringToInt(sTemp);
        } else {

        }
        nbBlock = memSizeInBytes/(nbBytesInBlock+1);

        //int mem = sysHDL.getMconverterMemSize();
        int blocks = Helper.ConvertStringToInt(sysHDL.getBlockSize().replace(" ", ""));
        int binaryLength = binary.length;
        byte[] ccByteArray = cchandler.CreateCCFileByteArray(sysHDL.isMultipleReadSupported(),
                sysHDL.isMemoryExceed2048bytesSize(),
                nbBlock,
                blocks);
        byte[] TLByteArray = cchandler.CreateTLFileByteArray(binaryLength);
        byte[] TLTerminatorByteArray = cchandler.CreateTLTerminatorFileByteArray();

/*        if (cchandler.getWriteAccess() != 0x00 || !tt.getSTTagHandler().isNDEFWriteUnLocked()) {

        }*/

        // ========== present pwd
        // get the pwd for the first zone
        if (sysHDL.mST25DVRegister != null) {
            canWrite = 0;
            stnfcRegisterHandler.ST25DVRegisterTable reg = sysHDL.mST25DVRegister.getZSSentry(tt.getCurrentValideTLVBlokID());
            int pwdNumber = sysHDL.mST25DVRegister.getPasswordNumber(reg);
            if (pwdNumber > 0) {
                // present pwd
                GenErrorAppReport err;
                err = ((STNfcTagVHandler) (tt.getSTTagHandler())).presentPassword((byte) pwdNumber, password);
                if (err.m_err_value != 0) {
                    // error
                    // returncd = tt.reportActionStatus(err.m_err_text, err.m_err_value);
                    canWrite = 0;
                } else {
                    // Can write
                    canWrite = 1;
                }
            } else {
                // no password needed
                canWrite = 1;
            }
        } else {
            // Default = write permission
            canWrite = 1;
        }

        if (canWrite == 1) {
            stnfcm24LRBasicOperation bop = new stnfcm24LRBasicOperation(sysHDL.getMaxTransceiveLength());
            //byte[] addressStart = Helper.ConvertIntTo2bytesHexaFormat(0x00);
            int startAddress = sysHDL.getZoneAddress(tt.getCurrentValideTLVBlokID());
            byte[] stAdr =  Helper.ConvertIntTo2bytesHexaFormatBis(startAddress);
            if (bop.m24LRWriteNDEFBasicOp(stAdr, ccByteArray, TLByteArray, binary,
                    TLTerminatorByteArray) == 0) {
                // ok
                ret = true;
            } else {
                // write error
                ret = false;
            }
        }


        // ======================
        return ret;
    }

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#selectNdef(int)
     */
/*    @Override
    public int selectNdef(int NdefFileID) {
        // TODO Auto-generated method stub
        return 0;
    }*/

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#isNDEFWriteUnLocked()
     */
    @Override
    public boolean isNDEFWriteUnLocked() {
        // TODO Auto-generated method stub
        // Check Register to know if Writable
        boolean ret = true;
        boolean ret1 = true;
        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (tt.getSYSHandler());
        if (sysHDL.mST25DVRegister != null) {
            stnfcRegisterHandler.ST25DVRegisterTable reg = sysHDL.mST25DVRegister.getZSSentry(tt.getCurrentValideTLVBlokID());
            ret1 =  sysHDL.mST25DVRegister.isWriteUnLocked(reg);
        }
        ret = ret1;
        return ret;
    }

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#isNDEFReadUnLocked()
     */
    @Override
    public boolean isNDEFReadUnLocked() {
        // TODO Auto-generated method stub
        boolean ret = true;

        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (tt.getSYSHandler());
        if (sysHDL.mST25DVRegister != null) {
            stnfcRegisterHandler.ST25DVRegisterTable reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ1SS;
            switch (tt.getCurrentValideTLVBlokID()){
                case 0:
                    break;
                case 1:
                    reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ2SS;
                    break;
                case 2:
                    reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ3SS;
                    break;
                case 3:
                    reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ4SS;
                    break;
                default:
                    break;
            }
            ret =  sysHDL.mST25DVRegister.isReadUnLocked(reg);
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see com.st.NFC.stnfcTagGenHandler#isNDEFReadUnLocked(byte[])
     */
    public boolean presentReadPasswordDone[] = {false,false,false,false};

    @Override
    public boolean isNDEFReadUnLocked(byte[] password) {
        // TODO Auto-generated method stub
        boolean ret = true;
        boolean canRead = false;

        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (tt.getSYSHandler());
        if (isNDEFReadUnLocked()) {
            ret = true;
        } else {
            // need the pwd
            if (sysHDL.mST25DVRegister != null) {
                canRead = false;
                stnfcRegisterHandler.ST25DVRegisterTable reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ1SS;
                switch (tt.getCurrentValideTLVBlokID()){
                    case 0:
                        break;
                    case 1:
                        reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ2SS;
                        break;
                    case 2:
                        reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ3SS;
                        break;
                    case 3:
                        reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ4SS;
                        break;
                    default:
                        break;
                }
                int pwdNumber = sysHDL.mST25DVRegister.getPasswordNumber(reg);
                if (pwdNumber > 0) {
                    // present pwd
                    GenErrorAppReport err;
                    err = ((STNfcTagVHandler) (tt.getSTTagHandler())).presentPassword((byte) pwdNumber, password);
                    if (err.m_err_value != 0) {
                        // error
                        tt.reportActionStatus(err.m_err_text, err.m_err_value);
                        canRead = false;
                    } else {
                        // Can write
                        canRead = true;
                    }
                } else {
                    // no password needed
                    canRead = true;
                }
            } else {
                // Default = write permission
                canRead = true;
            }
            ret = canRead;
        }
        if (canRead) presentReadPasswordDone[tt.getCurrentValideTLVBlokID()] = true;
        else presentReadPasswordDone[tt.getCurrentValideTLVBlokID()] = false;
        return ret;
    }

    @Override
    public GenErrorAppReport selectNdef(int NdefFileID){

        return new GenErrorAppReport();
    }
    @Override
    public GenErrorAppReport FormatNDEF(NFCTag currentTag, int nbNdefFiles) {

        return new GenErrorAppReport();
    }

    @Override
    public GenErrorAppReport NDEFLockWrite(NFCTag currentTag, byte[] _password128bitslong, byte[] DEFAULT_PASSWORD) {

        return new GenErrorAppReport();
    }

    public GenErrorAppReport NDEFUnLockWrite(NFCTag currentTag, byte[] _password128bitslong, byte[] DEFAULT_PASSWORD) {

        return new GenErrorAppReport();
    }

    public GenErrorAppReport NDEFLockRead(NFCTag currentTag, byte[] _password128bitslong, byte[] _modificationpassword128bitslong , byte[] DEFAULT_PASSWORD){

        return new GenErrorAppReport();
    }
    public GenErrorAppReport NDEFUnLockRead(NFCTag currentTag,  byte[] _modificationpassword128bitslong , byte[] DEFAULT_PASSWORD){
        // Check that the TAG is a M24SR
        if (!(currentTag.getModel().contains("DV")) )
        {
            return new GenErrorAppReport("Requested Lock Write not supported by the presented Tag", 0);
        }

        // Check that the TAG is locked from CC file field

        // Put the  system in a Select  NDEF File Mode.
        if (currentTag.setInSelectNDEFState(currentTag.getCurrentValideTLVBlokID()) != 1)
        {
            return new GenErrorAppReport("Can not put the tag in NDEF selected State", 0);
        }

        // NDEF is now selected. we need to verify lock status
        STNfcTagVHandler stTagHandler  = (STNfcTagVHandler) currentTag.getSTTagHandler();

        if (stTagHandler == null)
        {
            return new GenErrorAppReport("Can't retrieve NFC Tag Handler", 0); // Must not occurs
        }

        // At this step TAG must answer TAG REQUIRED Password
        if (stTagHandler.isNDEFReadUnLocked(_modificationpassword128bitslong))
        {
            return new GenErrorAppReport("Tag is not locked .\n If you want to change password please unlock it before", 0);
        }


        currentTag.tagInvalidate = true;
        return new GenErrorAppReport("Success Tag is now unLocked in read", 0);

     }



    public GenErrorAppReport ToggleGPO(NFCTag currentTag, boolean HZState){

        return new GenErrorAppReport();
    }
    public GenErrorAppReport EraseNDEF(NFCTag currentTag){

        return new GenErrorAppReport();
    }
    public GenErrorAppReport SetupCounter(NFCTag currentTag,int setupCounter ){

        return new GenErrorAppReport();
    }
    public GenErrorAppReport SelectCommand(){
        GenErrorAppReport errrpt;
        errrpt = new GenErrorAppReport("TypeV no need of Select Command ", (byte) 0x01);
        return errrpt;
        //return new GenErrorAppReport();
    }

  // additional methods for LR
    public GenErrorAppReport presentPassword(byte PasswordNumber, byte PasswordData[]) {
        // TODO Auto-generated method stub
        byte[] PresentPasswordCommandAnswer = null;
        GenErrorAppReport errrpt;

        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (tt.getSYSHandler());

        int cpt = 0;

        PresentPasswordCommandAnswer = null;
        while ((PresentPasswordCommandAnswer == null || PresentPasswordCommandAnswer[0] == 1) && cpt <= 10) {
            PresentPasswordCommandAnswer = this.mTypeVTagOperation.SendPresentPasswordCommand(this.currentTag,
                    sysHDL.isUidRequested(), PasswordNumber, PasswordData);
/*            PresentPasswordCommandAnswer = NFCCommandV.SendPresentPasswordCommand(this.currentTag,
                    sysHDL.isUidRequested(), PasswordNumber, PasswordData);*/
            cpt++;
        }
        if (PresentPasswordCommandAnswer == null) {
            // Toast.makeText(getApplicationContext(), "ERROR Present Password
            // (No tag answer) ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("ERROR Present Password (No tag answer) ", -1);
        } else if (PresentPasswordCommandAnswer[0] == (byte) 0x01) {
            // Toast.makeText(getApplicationContext(), "ERROR Present Password
            // ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("ERROR Present Password ", (byte) 0x01);
        } else if (PresentPasswordCommandAnswer[0] == (byte) 0xFF) {
            // Toast.makeText(getApplicationContext(), "ERROR Present Password
            // ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("ERROR Present Password ", (byte) 0xFF);
        } else if (PresentPasswordCommandAnswer[0] == (byte) 0x00) {
            // Toast.makeText(getApplicationContext(), "Present Password
            // Sucessfull ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("Present Password Sucessfull ", (byte) 0x00);
        } else {
            // Toast.makeText(getApplicationContext(), "Present Password ERROR
            // ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("Present Password ERROR ", -1);
        }

        return errrpt;
    }

    public GenErrorAppReport writePassword(byte PasswordNumber, byte PasswordData[]) {
        // TODO Auto-generated method stub
        byte[] WritePasswordCommandAnswer = null;
        GenErrorAppReport errrpt;

        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (tt.getSYSHandler());

        int cpt = 0;

        WritePasswordCommandAnswer = null;
        while ((WritePasswordCommandAnswer == null || WritePasswordCommandAnswer[0] == 1) && cpt <= 10) {

            WritePasswordCommandAnswer = this.mTypeVTagOperation.SendWritePasswordCommand(this.currentTag,sysHDL.isUidRequested(), PasswordNumber, PasswordData);
            //WritePasswordCommandAnswer = NFCCommandV.SendWritePasswordCommand(this.currentTag,sysHDL.isUidRequested(), PasswordNumber, PasswordData);
            cpt++;
        }
        if (WritePasswordCommandAnswer == null) {
            // Toast.makeText(getApplicationContext(), "ERROR Present Password
            // (No tag answer) ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("ERROR Write Password (No tag answer) ", -1);
        } else if (WritePasswordCommandAnswer[0] == (byte) 0x00) {
            // Toast.makeText(getApplicationContext(), "Present Password
            // Sucessfull ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("Write Password Sucessfull ", (byte) 0x00);
        }else if (WritePasswordCommandAnswer[0] == (byte) 0x01 && WritePasswordCommandAnswer.length >= 2) {
            // Toast.makeText(getApplicationContext(), "ERROR Present Password
            // ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("ERROR Write Password ", WritePasswordCommandAnswer[1]);
        } else if (WritePasswordCommandAnswer[0] == (byte) 0xFF) {
            // Toast.makeText(getApplicationContext(), "ERROR Present Password
            // ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("ERROR Write Password ", (byte) 0xFF);
        }  else {
            // Toast.makeText(getApplicationContext(), "Present Password ERROR
            // ", Toast.LENGTH_SHORT).show();
            errrpt = new GenErrorAppReport("Write Password ERROR ", -1);
        }

        return errrpt;
    }

    public GenErrorAppReport lockSector(byte[] SectorNumberAddress, byte LockSectorByte) {
        byte[] LockSectorCommandAnswer = null;
        GenErrorAppReport errrpt;

        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (tt.getSYSHandler());

        int cpt = 0;

        LockSectorCommandAnswer = null;
        while ((LockSectorCommandAnswer == null || LockSectorCommandAnswer[0] == 1) && cpt <= 10)
        {
            LockSectorCommandAnswer = this.mTypeVTagOperation.SendLockSectorCommand(this.currentTag, sysHDL.isUidRequested(), sysHDL.isBasedOnTwoBytesAddress(),SectorNumberAddress, LockSectorByte);
//            LockSectorCommandAnswer = NFCCommandV.SendLockSectorCommand(this.currentTag, sysHDL.isUidRequested(), sysHDL.isBasedOnTwoBytesAddress(),SectorNumberAddress, LockSectorByte);
            cpt++;
        }

        if (LockSectorCommandAnswer==null)
        {
            errrpt = new GenErrorAppReport("ERROR Present Password (No tag answer) ", -1);
        }else if(LockSectorCommandAnswer[0]==(byte)0x00)
        {
            errrpt = new GenErrorAppReport("Lock Sector Sucessfull ", 0);
            //finish();
        }
        else if(LockSectorCommandAnswer[0]==(byte)0x01 && LockSectorCommandAnswer.length >= 2)
        {
            errrpt = new GenErrorAppReport("ERROR Lock Sector ", LockSectorCommandAnswer[1]);
        }
        else if(LockSectorCommandAnswer[0]==(byte)0xFA)
        {
            errrpt = new GenErrorAppReport("ERROR : your RF product is not M24LRxx family ", 0xFA);
        }
        else if(LockSectorCommandAnswer[0]==(byte)0xFB)
        {
            errrpt = new GenErrorAppReport("ERROR : sector number is not compliant with your RF product ", 0xFB);
        }
        else if(LockSectorCommandAnswer[0]==(byte)0xFF)
        {
            errrpt = new GenErrorAppReport("ERROR Lock Sector ", 0xFF);
        }

        else
        {
            errrpt = new GenErrorAppReport("Lock Sector ERROR ", -1);
        }

        return errrpt;
    }




}
