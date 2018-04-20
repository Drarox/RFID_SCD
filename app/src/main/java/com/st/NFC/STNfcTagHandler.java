/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1504 $
  * Revision of last commit    :  $Rev: 1504 $
  * Date of last commit     :  $Date: 2016-01-04 13:59:27 +0100 (Mon, 04 Jan 2016) $ 
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




import com.st.nfc4.Iso7816_4Err;
import com.st.nfc4.Type4TagOperationM24SR;
import com.st.util.GenErrorAppReport;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

//public class STNfcTagHandler extends Type4TagOperationM24SR implements Type4Tagm24sr7816STCommands,Iso7816_4APDU {
    public class STNfcTagHandler  implements stnfcTagGenHandler {



    private String TAG = "STNfcTagHandler";
    private static final int MAX_NB_SUPPORTED_NDEF = 10;
    private static final int TAG_STATE_UNKNOW = 0xFF;
    //private static final int TAG_STATE_ATS = 0x01;


    protected int currentTagState;
    protected int _currentIDFile; // current Field ID get from corresponding TLVBlock

    protected Type4TagOperationM24SR m_Type4TagOperationM24SR;

    public STNfcTagHandler()
    {
        currentTagState = TAG_STATE_UNKNOW;
        //currentTag = null;
        //isoDepCurrentTag = null;
        //_lasttranscieveAnswer = new Iso7816_4Err();
        _currentIDFile = 0;
        //m_m24ProtectionLockMgt = null;
        m_Type4TagOperationM24SR = null;
    }

    public STNfcTagHandler(Tag tagToHandle)
    {
        currentTagState = TAG_STATE_UNKNOW;
        //currentTag = tagToHandle;
        //_lasttranscieveAnswer = new Iso7816_4Err();
        m_Type4TagOperationM24SR = new Type4TagOperationM24SR(tagToHandle);
    }

    /*
     * From basic operations
     */
    public stnfcTag_Err getError() {
        return (stnfcTag_Err)(m_Type4TagOperationM24SR.getError());
    }

    // Gen
    public void closeConnection()
    {
        m_Type4TagOperationM24SR.closeConnection();
    }


    // Gen
    public int requestCCSelect()
    {
        return m_Type4TagOperationM24SR.requestCCSelect();
    }

    // Gen
    public int requestSysSelect()
    {
        return m_Type4TagOperationM24SR.requestSysSelect();
    }

    // Gen
    public int requestCCReadLength()
    {
        return m_Type4TagOperationM24SR.requestCCReadLength();
    }
    // Gen
    public int requestSysReadLength()
    {
        return m_Type4TagOperationM24SR.requestSysReadLength();
    }

    public int requestCCRead(int size, byte [] buffer)
    {
        return m_Type4TagOperationM24SR.requestCCRead(size, buffer);
    }
    public int requestSysRead(int size, byte [] buffer)
    {
        return m_Type4TagOperationM24SR.requestSysRead(size, buffer);
    }

    public int readNdeflength() // similar to requestCCReadLength
    {
        return m_Type4TagOperationM24SR.readNdeflength();
    }
    public int readNdefBinary(byte [] ndefbuffer)
    {
        return m_Type4TagOperationM24SR.readNdefBinary(ndefbuffer);
    }


    /*
     * Local methods
     */
    public int getCurrentNDEFID()
    {
        return     _currentIDFile;
    }

    public void setCurrentNDEFID(int ID)
    {
        _currentIDFile = ID;
    }


    //Erase NDEF File
    private boolean m24srEraseNdef(int NDEFID)
    {
        return updateBinarySize(0);
    }

    // Format TAG with Nb NDEF Files gives in parameters
    private boolean m24srFormatNdef(int nbNdefFiles)
    {
            return m_Type4TagOperationM24SR.m24srFormatNdef(nbNdefFiles);

    }

    // Write file Management

    // Update size

    private boolean updateBinarySize(int size)
    {
        return m_Type4TagOperationM24SR.m24srupdateBinarySize(size);
    }
    //m24srNCFForumupdateBinarySize

    // Update binary without size. Start at the Offset 2

    public boolean updateBinary(byte [] binary)
    {
        return m_Type4TagOperationM24SR.m24srupdateBinary(binary);
    }


    // ======================================
    public boolean updateBinarywithPassword(byte [] binary, byte [] password)
    {
        return m_Type4TagOperationM24SR.m24srupdateBinarywithPassword(binary,password);

    }




    // Protection and lock management

    public boolean isNDEFReadUnLocked()
    {
        //return m24srVerifycmd(null,true);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().isNDEFReadUnLocked();
    }

    // ==================
    public boolean isNDEFWriteUnLocked()
    {
        //return m24srVerifycmd(null,false);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().isNDEFWriteUnLocked();
    }

    public boolean isNDEFReadUnLocked(byte [] password)
    {
        //return m24srVerifycmd(password,true);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().isNDEFReadUnLocked(password);
    }

    public boolean isNDEFWriteunlock(byte [] password)
    {
        //return m24srVerifycmd(password,false);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().isNDEFWriteunlock(password);
    }


    // Change Reference Data Command
    // Change Reference Data command prelaces the read or write password related to the NDEF file previously selected


    // Change Password for the READ mode
    private boolean m24srchgRefReadDatacmd(byte[] password)
    {
        //return m24srchgRefDatacmd(password, true);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().m24srchgRefReadDatacmd(password);
    }

    // Change Password for the Wite mode
    private boolean m24srchgRefWriteDatacmd(byte[] password)
    {
        //return m24srchgRefDatacmd(password, false);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().m24srchgRefWriteDatacmd(password);
    }






    // Enable Read Password
    private boolean m24srenableReadVerifReqCmd()
    {
        //return m24srenableVerifReqCmd(true);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().m24srenableReadVerifReqCmd();

    }

    // Enable write Password
    private boolean m24srenableWriteVerifReqCmd()
    {
        //return m24srenableVerifReqCmd(false);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().m24srenableWriteVerifReqCmd();
    }


    // Disable Verification Command
    // Deactivate the protection  by password of the currently selected NDEF file.

    // Enable Read Password
    private boolean m24sdisableReadVerifReqCmd()
    {
        //return m24srdiableVerifReqCmd(true);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().m24sdisableReadVerifReqCmd();
    }

    // Enable write Password
    private boolean m24sdisableWriteVerifReqCmd()
    {
        //return m24srdiableVerifReqCmd(false);
        return m_Type4TagOperationM24SR.get_mProtectionLockMgt().m24sdisableWriteVerifReqCmd();
    }


    // Enable Permanent State Command in read mode
    private boolean m24srSTEnableReadPermState()
    {
        //return m24srSTEnablePermState(true);
        return  m_Type4TagOperationM24SR.m24srSTEnableReadPermState();
    }

    // Enable Permanent State Command in Write mode
    private boolean m24srSTEnableWritePermState()
    {
        //return m24srSTEnablePermState(false);
        return m_Type4TagOperationM24SR.m24srSTEnableWritePermState();
    }

    // Enable Permanent State Command

    // Disable Permanent State Command in read mode
    private boolean m24srSTDisableReadPermState()
    {
        //return m24srSTDisablePermState(true);
        return m_Type4TagOperationM24SR.m24srSTDisableReadPermState();
    }

    // Disable Permanent State Command in Write mode
    private boolean m24srSTDisableWritePermState()
    {
        //return m24srSTDisablePermState(false);
        return m_Type4TagOperationM24SR.m24srSTDisableWritePermState();
    }

    // Disable Permanent State Command
    // Configure current NDEF file in ReadOnly or WriteOnly command.



    // SendInterrupt command
    // On receivin the SendInterrupt command, M24SR04 generates a negative pulse on the GPO pin.

    public boolean m24srSTSendInterrupt()
    {
        return m_Type4TagOperationM24SR.m24srSTSendInterrupt();
    }


    private boolean m24srSTStateCtrlcmd(boolean setStateHz)
    {
        return m_Type4TagOperationM24SR.m24srSTStateCtrlcmd(setStateHz);
    }



    // Return The ID list of the NDEF messages stored in Tag
/*    public int[] m24srgetNdefIDlist()
    {
        int [] IDList = new int[MAX_NB_SUPPORTED_NDEF];

        // Check current tag status (Connected or not connected)

        // Send ATS - Send Select NDEF Tag Application Command

        // Select and read CC File

        // Parse CC File and get the ID list of NDEF File

        return IDList;
    }*/

    //
/*    public int m24srSelectNDEFFile(int NDEFID)
    {

        return 0;
    }
*/





    private boolean srtag2kldisablecounter()
    {
        return m_Type4TagOperationM24SR.srtag2kldisablecounter();

    }

    private boolean srtag2klenableWritecounter()
    {
        return m_Type4TagOperationM24SR.srtag2klenableWritecounter();
    }

    private boolean srtag2klenableReadcounter()
    {
        return m_Type4TagOperationM24SR.srtag2klenableReadcounter();
    }

    private boolean SetupGPOConfig(int config)
    {
        return m_Type4TagOperationM24SR.SetupGPOConfig(config);
    }

/*    @Override
    public void decode() {
        // TODO Auto-generated method stub

    }*/

    // Architecture

    //public int selectNdef(int NdefFileID) {
    public GenErrorAppReport selectNdef(int NdefFileID) {

        try {

            if (m_Type4TagOperationM24SR.selectNdef(NdefFileID) == 1) {
                _currentIDFile = NdefFileID;
                return new GenErrorAppReport(this.getError().translate(), 1);
            } else {
                _currentIDFile = 0;
                return new GenErrorAppReport(this.getError().translate(), 0);
            }
        } catch (RuntimeException e) {
            _currentIDFile = 0;
            // e.printStackTrace();
            throw new RuntimeException("fail", e);
            //return new GenErrorAppReport(this.getError().translate(), 0);
        }

    }

//    public int requestATS()
    public GenErrorAppReport SelectCommand()
    {
        int ret = m_Type4TagOperationM24SR.requestATS();
        return new GenErrorAppReport(this.getError().translate(), ret);
    }

        @Override
        public GenErrorAppReport FormatNDEF(NFCTag currentTag, int nbNdefFiles)
        {

            // Check that the TAG is a M24SR
            if (!(currentTag.getModel().contains("24SR")) && !(currentTag.getModel().contains("SRTAG")))
            {
                return new GenErrorAppReport("Requested Erase Action not supported by the presented Tag", 0);
            }

            // TBD : need to ensure First that whole NDEF are not W|L locked

            // Put the  system in a Select Sys File Mode.
            if (currentTag.setInSelectSysFileState() != 1)
            {
                return new GenErrorAppReport("Can not put the tag in Sys selected File State", 0);
            }

            //STNfcTagHandler stTagHandler  = currentTag.getSTTagHandler();

            //Format Tag with expected File Number - currently we format on NDEF Files
            //if (!stTagHandler.m24srFormatNdef(nbNdefFiles))
            if (!m24srFormatNdef(nbNdefFiles))
            {
                return new GenErrorAppReport(getError().translate(),0);
            }

            return new GenErrorAppReport("Format Request Succeeded!",1);

        }


        public GenErrorAppReport NDEFLockWrite(NFCTag currentTag, byte[] _password128bitslong, byte[] DEFAULT_PASSWORD)
        {
            // Check that the TAG is a M24SR
            if (!(currentTag.getModel().contains("24SR")) && !(currentTag.getModel().contains("SRTAG")) &&
                    !(currentTag.getModel().contains("ST25TA")))
            {
                return new GenErrorAppReport("Requested Lock Write not supported by the presented Tag", 0);
            }

            // Check that the TAG is not already locked from CC file field
            stnfccchandler lcchandler = (stnfccchandler)(currentTag.getCCHandler());

            if (lcchandler.isNDEFLOCKWrite(currentTag.getCurrentValideTLVBlokID()))
            {
                return new GenErrorAppReport("Tag is alread Locked.\n If you want to change password please unlock it before", 0);
            }

            // Check that the TAG is not already locked from CC file field
            if (lcchandler.isNDEFPermanentLOCKWrite(currentTag.getCurrentValideTLVBlokID()))
            {
                return new GenErrorAppReport("Tag is Permanently Locked.\n Please Give Write Password before writing in", 0);
            }


            // put the  system in a Select  NDEF File.
            if (currentTag.setInSelectNDEFState(currentTag.getCurrentValideTLVBlokID()) != 1)
            {
                return new GenErrorAppReport("Can not put the tag in NDEF selected State", 0);
            }

            //NDEF is now selected. we need to verify lock status
            STNfcTagHandler stTagHandler  = (STNfcTagHandler) currentTag.getSTTagHandler();
            if (stTagHandler == null)
            {
                return new GenErrorAppReport("Can't retrieve NFC Tag Handler", 0); // Must not occurs
            }

            // At this step TAG must answer OK (not locked)
            if (!stTagHandler.isNDEFWriteUnLocked())
            {
                return new GenErrorAppReport("Tag is alread Locked.\n If you want to change password please unlock it before", 0);
            }

            // Need to send the Write Password (Last entered write password - we use the default one)

            if (!stTagHandler.isNDEFWriteunlock(DEFAULT_PASSWORD))
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(), 0);
            }

            // Enable Verification Write
            if (!m24srenableWriteVerifReqCmd())
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }

            // Enable Verification is set. Now Change Password

            if (!m24srchgRefWriteDatacmd(_password128bitslong))
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }

            return new GenErrorAppReport("Success Tag is now Locked in write", 1);


        }

        public GenErrorAppReport NDEFUnLockWrite(NFCTag currentTag, byte[] _password128bitslong, byte[] DEFAULT_PASSWORD)
        {

            // Check that the TAG is a M24SR
            if (!(currentTag.getModel().contains("24SR")) && !(currentTag.getModel().contains("SRTAG")) &&
                    !(currentTag.getModel().contains("ST25TA")))
            {
                return new GenErrorAppReport("Requested Lock Write not supported by the presented Tag", 0);
            }

            // Check that the TAG is locked from CC file field
            stnfccchandler lcchandler = (stnfccchandler)(currentTag.getCCHandler());
            if (!lcchandler.isNDEFLOCKWrite(currentTag.getCurrentValideTLVBlokID()))
            {
                return new GenErrorAppReport("Tag is already UnLocked.\n No Need to unlock it", 0);
            }

            // Check that the TAG is not already locked from CC file field
            if (lcchandler.isNDEFPermanentLOCKWrite(currentTag.getCurrentValideTLVBlokID()))
            {
                return new GenErrorAppReport("Tag is Permanently Locked.\n Can not remove lock", 0);
            }



            // put the  system in a Select  NDEF File.
            if (currentTag.setInSelectNDEFState(currentTag.getCurrentValideTLVBlokID()) != 1)
            {
                return new GenErrorAppReport("Can not put the tag in NDEF selected State", 0);
            }

            //NDEF is now selected. we need to verify lock status
            STNfcTagHandler stTagHandler  = (STNfcTagHandler) currentTag.getSTTagHandler();
            if (stTagHandler == null)
            {
                return new GenErrorAppReport("Can't retrieve NFC Tag Handler", 0); // Must not occurs
            }

            // At this step TAG must answer TAG REQUIRED Password
            if (stTagHandler.isNDEFWriteUnLocked())
            {
                return new GenErrorAppReport("Tag is not locked Locked.\n If you want to change password please unlock it before", 0);
            }
            //else if ( (stTagHandler._lasttranscieveAnswer.getSW1() != 0x63) &&  (stTagHandler._lasttranscieveAnswer.getSW2() != 0x00) )
            else if ( (stTagHandler.getError().getSW1() != 0x63) &&  (stTagHandler.getError().getSW2() != 0x00) )
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(), 0);
            }
            // Else TAG required a Write password.

            // Need to send the entered Write Password (Last entered write password - we use the default one)
            if (!stTagHandler.isNDEFWriteunlock(_password128bitslong))
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(), 0);
            }

            // Enable Verification is set. Now Change Write Password to the Default one (Not mandatory)

            if (!stTagHandler.m24srchgRefWriteDatacmd(DEFAULT_PASSWORD))
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }

            // Disable Verification Write
            if (!stTagHandler.m24sdisableWriteVerifReqCmd())
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }
            //FBE
            lcchandler.resetextwriteaccessenabled(currentTag.getCurrentValideTLVBlokID());
            return new GenErrorAppReport("Success Tag is now unLocked in write", 1);

        }

     // Lock Read - NDEFLockRead
        public GenErrorAppReport NDEFLockRead(NFCTag currentTag, byte[] _password128bitslong, byte[] _modificationpassword128bitslong , byte[] DEFAULT_PASSWORD)
        {

            // Check that the TAG is a M24SR
            if (!(currentTag.getModel().contains("24SR")) && !(currentTag.getModel().contains("SRTAG")) &&
                    !(currentTag.getModel().contains("ST25TA")))
            {
                return new GenErrorAppReport("Requested Lock Read not supported by the presented Tag", 0);
            }

            stnfccchandler lcchandler = (stnfccchandler)(currentTag.getCCHandler());

            // Check that the TAG is not already locked from CC file field
            if (lcchandler.isNDEFLOCKRead(currentTag.getCurrentValideTLVBlokID()))
            {
                return new GenErrorAppReport("Tag is already Locked.\n If you want to change password please unlock it before", 0);
            }

            // Check that the TAG is not already locked from CC file field
            if (lcchandler.isNDEFPermanentLOCKRead(currentTag.getCurrentValideTLVBlokID()))
            {
                return new GenErrorAppReport("Tag is already Locked.\n", 0);
            }


            // Put the  system in a Select  NDEF File Mode.
            if (currentTag.setInSelectNDEFState(currentTag.getCurrentValideTLVBlokID()) != 1)
            {
                return new GenErrorAppReport("Can not put the tag in NDEF selected State", 0);
            }

            // NDEF is now selected. we need to verify lock status
            STNfcTagHandler stTagHandler  = (STNfcTagHandler) currentTag.getSTTagHandler();

            if (stTagHandler == null)
            {
                return new GenErrorAppReport("Can't retrieve NFC Tag Handler", 0); // Must not occurs
            }

            // At this step TAG must answer OK (not locked)
            if (!stTagHandler.isNDEFReadUnLocked())
            {
                return new GenErrorAppReport("Tag is alread Locked.\n If you want to change password please unlock it before", 0);
            }

            // Need to check the write Status.
              // if lock.. enter write password to be abble to lock in read mode
              // if not lock enter the default write password.
            // At this step TAG must answer OK (not locked)
            if (stTagHandler.isNDEFWriteUnLocked())
            {
                // Send default lock write
                if (!stTagHandler.isNDEFWriteunlock(DEFAULT_PASSWORD))
                {
                    return new GenErrorAppReport(stTagHandler.getError().translate(), 0);
                }
            } else
            {
                if (!stTagHandler.isNDEFWriteunlock(_modificationpassword128bitslong))
                {
                    return new GenErrorAppReport(stTagHandler.getError().translate(), 0);
                }
            }

            // Here we are supposed to lock in read mode

            // Enable Verification Read
            if (!stTagHandler.m24srenableReadVerifReqCmd())
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }

            // Enable Verification is set. Now Change Password
            if (!stTagHandler.m24srchgRefReadDatacmd(_password128bitslong))
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }

            currentTag.tagInvalidate = true;
            return new GenErrorAppReport("Success Tag is now Locked in read", 1);
            // invalidate NDEFHandler
            // currentTag.setndedHandler(null);

        }

     // Un Lock Read - NDEFUnLockRead
        public GenErrorAppReport NDEFUnLockRead(NFCTag currentTag,  byte[] _modificationpassword128bitslong , byte[] DEFAULT_PASSWORD)
        {

            // Check that the TAG is a M24SR
            if (!(currentTag.getModel().contains("24SR")) && !(currentTag.getModel().contains("SRTAG")) &&
                    !(currentTag.getModel().contains("ST25TA")))
            {
                return new GenErrorAppReport("Requested Lock Write not supported by the presented Tag", 0);
            }

            // Check that the TAG is locked from CC file field
            stnfccchandler lcchandler = (stnfccchandler)(currentTag.getCCHandler());

            if (!lcchandler.isNDEFLOCKRead(currentTag.getCurrentValideTLVBlokID()))
            {
                return new GenErrorAppReport("Tag is Not Locked.\n", 0);
            }

            // Put the  system in a Select  NDEF File Mode.
            if (currentTag.setInSelectNDEFState(currentTag.getCurrentValideTLVBlokID()) != 1)
            {
                return new GenErrorAppReport("Can not put the tag in NDEF selected State", 0);
            }

            // NDEF is now selected. we need to verify lock status
            STNfcTagHandler stTagHandler  = (STNfcTagHandler) currentTag.getSTTagHandler();

            if (stTagHandler == null)
            {
                return new GenErrorAppReport("Can't retrieve NFC Tag Handler", 0); // Must not occurs
            }

            // At this step TAG must answer TAG REQUIRED Password
            if (stTagHandler.isNDEFReadUnLocked())
            {
                return new GenErrorAppReport("Tag is not locked .\n If you want to change password please unlock it before", 0);
            }
            //else if ( (stTagHandler._lasttranscieveAnswer.getSW1() != 0x63) &&  (stTagHandler._lasttranscieveAnswer.getSW2() != 0x00) )
            // WARNING
            else if ( (stTagHandler.getError().getSW1() != 0x63) &&  (stTagHandler.getError().getSW2() != 0x00) )
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(), 0);
            }


            // Need to check the write Status.
              // if lock.. enter write password to be abble to lock in read mode
              // if not lock enter the defautl write password.
            // At this step TAG must answer OK (not locked)
            if (stTagHandler.isNDEFWriteUnLocked())
            {
                // Send default lock write
                if (!stTagHandler.isNDEFWriteunlock(DEFAULT_PASSWORD))
                {
                    return new GenErrorAppReport(stTagHandler.getError().translate(), 0);
                }
            } else
            {
                if (!stTagHandler.isNDEFWriteunlock(_modificationpassword128bitslong))
                {
                    return new GenErrorAppReport(stTagHandler.getError().translate(), 0);
                }
            }

            // Here we are supposed to unlock read mode

            // Enable Verification Read
            if (!stTagHandler.m24sdisableReadVerifReqCmd())
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }

            // Enable Verification is set. Now Change Write Password to the Default one (Not mandatory)

            if (!stTagHandler.m24srchgRefReadDatacmd(DEFAULT_PASSWORD))
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }

            lcchandler.resetextreadaccessenabled(currentTag.getCurrentValideTLVBlokID());
            currentTag.tagInvalidate = true;
            return new GenErrorAppReport("Success Tag is now un unLocked in read", 0);

        }

        public GenErrorAppReport ToggleGPO(NFCTag currentTag, boolean HZState)
        {


            // Check that the TAG is a M24SR
            if (!(currentTag.getModel().contains("24SR")) && !(currentTag.getModel().contains("SRTAG")) &&
                    !(currentTag.getModel().contains("ST25TA")))
            {
                return new GenErrorAppReport("Requested GPO feature not supported by the presented Tag", 0);
            }

            // Put the  system in a Select SYS File Mode.
            if (currentTag.setInSelectSysFileState() != 1)
            {
                return new GenErrorAppReport("Can not put the tag in SYS selected State", 0);
            }

            // SyS file  is now selected - we may check RF GPO config in SysFIle GPO
            STNfcTagHandler stTagHandler  = (STNfcTagHandler) currentTag.getSTTagHandler();

            //boolean HZState = _HZState;

            if (!stTagHandler.m24srSTStateCtrlcmd(HZState))
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }

            return new GenErrorAppReport("Set State Control Mode Request Succeeded!",1);
        }


        public GenErrorAppReport EraseNDEF(NFCTag currentTag)
        {

            // Check that the TAG is a M24SR
            if (!(currentTag.getModel().contains("24SR")) && !(currentTag.getModel().contains("SRTAG")) &&
                    !(currentTag.getModel().contains("ST25TA")))
            {
                return new GenErrorAppReport("Requested Erase Action not supported by the presented Tag", 0);
            }

            // Put the  system in a Select NDEF File Mode.
            if (currentTag.setInSelectNDEFState(currentTag.getCurrentValideTLVBlokID()) != 1)
            {
                return new GenErrorAppReport("Can not put the tag in NDEF selected State", 0);
            }

            STNfcTagHandler stTagHandler  = (STNfcTagHandler) currentTag.getSTTagHandler();

            // Only first NDEF File is erased - (improvement : loop over whole NDEF files)
            if (!stTagHandler.m24srEraseNdef(0))
            {
                return new GenErrorAppReport(stTagHandler.getError().translate(),0);
            }

            return new GenErrorAppReport("Set State Control Mode Request Succeeded!",1);

        }

        // SRTAG2KL specific
        public GenErrorAppReport SetupCounter(NFCTag currentTag,int setupCounter )
        {

            // Check that the TAG is a M24SR
            if (!(currentTag.getModel().contains("SRTAG2KL")) && !(currentTag.getModel().contains("ST25TA")))
            {
                return new GenErrorAppReport("Requested Counter Setup Action not supported by the presented Tag", 0);
            }

            // TBD : need to ensure First that whole NDEF are not W|L locked

            // Put the  system in a Select Sys File Mode.
            if (currentTag.setInSelectSysFileState() != 1)
            {
                return new GenErrorAppReport("Can not put the tag in Sys selected File State", 0);
            }

            STNfcTagHandler stTagHandler  = (STNfcTagHandler) currentTag.getSTTagHandler();

            switch (setupCounter)
            {
            case 0x00 : //Disable counter
                if (!stTagHandler.srtag2kldisablecounter())
                {
                    return new GenErrorAppReport(stTagHandler.getError().translate(),0);
                }
                break;
            case 0x01 : //Enable write counter
                if (!stTagHandler.srtag2klenableWritecounter())
                {
                    return new GenErrorAppReport(stTagHandler.getError().translate(),0);
                }
                break;
            case 0x02 : //Enable read counter
                if (!stTagHandler.srtag2klenableReadcounter())
                {
                    return new GenErrorAppReport(stTagHandler.getError().translate(),0);
                }
                break;
            default:
                return new GenErrorAppReport("Setup Counter config not supported!",1);

            }

            return new GenErrorAppReport("Setup Counter Succeeded!",1);

        }


        // SRTAG2KL specific
    public GenErrorAppReport SetupGPOConfig(NFCTag currentTag, int config) {

        // Check that the TAG is a M24SR
        if (!(currentTag.getModel().contains("SRTAG2KL")) && !(currentTag.getModel().contains("ST25TA"))) {
            return new GenErrorAppReport("Requested GPO Config  not supported by the presented Tag", 0);
        }

        // TBD : need to ensure First that whole NDEF are not W|L locked

        // Put the system in a Select Sys File Mode.
        if (currentTag.setInSelectSysFileState() != 1) {
            return new GenErrorAppReport("Can not put the tag in Sys selected File State", 0);
        }

        STNfcTagHandler stTagHandler = (STNfcTagHandler) currentTag.getSTTagHandler();

        if (!stTagHandler.SetupGPOConfig(config)) {
            return new GenErrorAppReport(stTagHandler.getError().translate(), 0);
        }

        return new GenErrorAppReport("Config GPO Succeeded!", 1);

    }


}


