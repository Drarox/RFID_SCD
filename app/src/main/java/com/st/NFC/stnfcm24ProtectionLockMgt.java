/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1170 $
  * Revision of last commit    :  $Rev: 1170 $
  * Date of last commit     :  $Date: 2015-09-23 16:35:26 +0200 (Wed, 23 Sep 2015) $ 
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

import com.st.nfc4.Iso7816_4APDU;
import com.st.nfc4.Type4TagOperationBasicOp;
import com.st.nfc4.Type4Tagm24sr7816STCommands;

import android.util.Log;

/**
 * @author MMY
 *
 */
public class stnfcm24ProtectionLockMgt extends stnfcProtectionLockMgt implements Iso7816_4APDU, Type4Tagm24sr7816STCommands {

    private String TAG = "stnfcm24ProtectionLockMgt";


    protected Type4TagOperationBasicOp _mType4TagOperationBasicOp;
    /**
     *
     */
    public stnfcm24ProtectionLockMgt(Type4TagOperationBasicOp TagOperationBasicOp) {
        // TODO Auto-generated constructor stub
        super();
        _mType4TagOperationBasicOp = TagOperationBasicOp;

    }



    @Override
    public boolean isNDEFReadUnLocked() {
        // TODO Auto-generated method stub
        boolean ret = true;
        _mlastProtectionState = stnfcProtectionLockStates.NDEF_LOCK_NO;
        ret =  m24srVerifycmd(null,true);
        stnfcTag_Err taglasterror = _mType4TagOperationBasicOp._lasttranscieveAnswer;
        if (taglasterror.getSW1() == (byte)0x90 && taglasterror.getSW2() == (byte)0x00) {
            _mlastProtectionState =stnfcProtectionLockStates.NDEF_LOCK_NO;
        }
        if (taglasterror.getSW1() == (byte)0x63 && taglasterror.getSW2() == (byte)0x00) {
            _mlastProtectionState =stnfcProtectionLockStates.NDEF_LOCK_SOFT;
        }
        if (taglasterror.getSW1() == (byte)0x69 && taglasterror.getSW2() == (byte)0x82) {
            _mlastProtectionState =stnfcProtectionLockStates.NDEF_LOCK_PERMANENT;
        }
        if (taglasterror.getSW1() == (byte)0x69 && taglasterror.getSW2() == (byte)0x84) {
            _mlastProtectionState =stnfcProtectionLockStates.NDEF_LOCK_PERMANENT;
        }
        return ret;
    }

    @Override
    public boolean isNDEFWriteUnLocked() {
        // TODO Auto-generated method stub
        boolean ret = true;
        _mlastProtectionState = stnfcProtectionLockStates.NDEF_LOCK_NO;
        ret =   m24srVerifycmd(null,false);
        stnfcTag_Err taglasterror = _mType4TagOperationBasicOp._lasttranscieveAnswer;
        if (taglasterror.getSW1() == (byte)0x90 && taglasterror.getSW2() == (byte)0x00) {
            _mlastProtectionState =stnfcProtectionLockStates.NDEF_LOCK_NO;
        }
        if (taglasterror.getSW1() == (byte)0x63 && taglasterror.getSW2() == (byte)0x00) {
            _mlastProtectionState =stnfcProtectionLockStates.NDEF_LOCK_SOFT;
        }
        if (taglasterror.getSW1() == (byte)0x69 && taglasterror.getSW2() == (byte)0x82) {
            _mlastProtectionState =stnfcProtectionLockStates.NDEF_LOCK_PERMANENT;
        }
        if (taglasterror.getSW1() == (byte)0x69 && taglasterror.getSW2() == (byte)0x84) {
            _mlastProtectionState =stnfcProtectionLockStates.NDEF_LOCK_PERMANENT;
        }
        return ret;
    }

    @Override
    public boolean isNDEFReadUnLocked(byte[] password) {
        // TODO Auto-generated method stub
        return m24srVerifycmd(password,true);
    }

    @Override
    public boolean isNDEFWriteunlock(byte[] password) {
        // TODO Auto-generated method stub
        return m24srVerifycmd(password,false);
    }

    private boolean m24srVerifycmd(byte[] password, boolean readmode)
    {

        byte[] cmd = null;
        boolean ret = false;

        _mType4TagOperationBasicOp._lasttranscieveAnswer.reset();

        if ((password != null) && ((password.length == 0) || (password.length != 16)))
        {
            Log.d(TAG,"m24srVerifycmd - password must be empty or equal to 0x10");
            return ret;
        }

         if (password!=null && (password.length==PASSWORDLENGTH))
         {
            cmd = new byte[m24sr7816verifycmd.length + PASSWORDLENGTH];
         }
         else
         {
             cmd = new byte[m24sr7816verifycmd.length];
         }

        System.arraycopy(m24sr7816verifycmd, 0, cmd, 0, m24sr7816verifycmd.length);

        if  ((password!=null)&&(password.length==PASSWORDLENGTH)) // fill password fields
        {
            cmd[m24sr7816verifycmd.length-1] = (byte)password.length;
            System.arraycopy(password,0,cmd,m24sr7816verifycmd.length,password.length);
        }

        // Configure Read or Write mode request

        if (readmode)
        {
            cmd[P2_INDEX] = (byte) 0x01;
        }
        else
        {
            cmd[P2_INDEX] = (byte) 0x02;
        }

        _mType4TagOperationBasicOp._lasttranscieveAnswer.set(_mType4TagOperationBasicOp.transcievecmd(cmd));

        if ((_mType4TagOperationBasicOp._lasttranscieveAnswer.getSW1() == (byte) 0x90)&&(_mType4TagOperationBasicOp._lasttranscieveAnswer.getSW2() == (byte)0x00))
        {
            ret = true;
        }
        else
        {
            Log.d(TAG,"m24srVerifycmd Failed " + _mType4TagOperationBasicOp._lasttranscieveAnswer.translate());
            ret = false;
        }
        return ret;
    }


    // Change Password for the Read mode
    @Override
    public boolean m24srchgRefReadDatacmd(byte[] password)
    {
        return m24srchgRefDatacmd(password, true);
    }

    // Change Password for the Wite mode
    @Override
    public boolean m24srchgRefWriteDatacmd(byte[] password)
    {
        return m24srchgRefDatacmd(password, false);
    }

    private boolean m24srchgRefDatacmd(byte[] password, boolean readmode)
    {
        // using command m24sr7816chgRefDatacmd
        byte[] cmd = null;

        if ((password == null) || (password.length != PASSWORDLENGTH))
        {
            Log.d(TAG,"m24srchgRefDatacmd - password must be equal to 0x10");
            return false;
        }

        cmd = new byte[m24sr7816chgRefDatacmd.length + PASSWORDLENGTH];


        System.arraycopy(m24sr7816chgRefDatacmd, 0, cmd, 0, m24sr7816chgRefDatacmd.length);
        System.arraycopy(password,0,cmd,m24sr7816chgRefDatacmd.length,PASSWORDLENGTH);

        // Configure Read or Write mode request
        if (readmode)
        {
            cmd[P2_INDEX] = (byte) 0x01;
        }
        else
        {
            cmd[P2_INDEX] = (byte) 0x02;
        }

        _mType4TagOperationBasicOp._lasttranscieveAnswer.set(_mType4TagOperationBasicOp.transcievecmd(cmd));

        if ((_mType4TagOperationBasicOp._lasttranscieveAnswer.getSW1() == (byte) 0x90)&&(_mType4TagOperationBasicOp._lasttranscieveAnswer.getSW2() == (byte)0x00))
        {
            return true;
        }
        else
        {
            Log.d(TAG,"m24sr7816chgRefDatacmd Failed " + _mType4TagOperationBasicOp._lasttranscieveAnswer.translate());
            return false;
        }
    }


    // Enable Read Password
    @Override
    public boolean m24srenableReadVerifReqCmd()
    {
        return m24srenableVerifReqCmd(true);

    }

    // Enable write Password
    @Override
    public boolean m24srenableWriteVerifReqCmd()
    {
        return m24srenableVerifReqCmd(false);
    }
    //Enable Verification Requirement Command
    // Activates the protection by password of the current selected NDEF File.
    // by a 128-bit password.

    private boolean m24srenableVerifReqCmd(boolean readmode)
    {
        // used command m24sr7816enableVerifReqCmd
        byte[] cmd = null;


        cmd = new byte[m24sr7816enableVerifReqCmd.length];


        System.arraycopy(m24sr7816enableVerifReqCmd, 0, cmd, 0, m24sr7816enableVerifReqCmd.length);

        // Configure Read or Write mode request
        if (readmode)
        {
            cmd[P2_INDEX] = (byte) 0x01;
        }
        else
        {
            cmd[P2_INDEX] = (byte) 0x02;
        }

        _mType4TagOperationBasicOp._lasttranscieveAnswer.set(_mType4TagOperationBasicOp.transcievecmd(cmd));

        if ((_mType4TagOperationBasicOp._lasttranscieveAnswer.getSW1() == (byte) 0x90)&&(_mType4TagOperationBasicOp._lasttranscieveAnswer.getSW2() == (byte)0x00))
        {
            return true;
        }
        else
        {
            Log.d(TAG,"m24sr7816enableVerifReqCmd Failed " + _mType4TagOperationBasicOp._lasttranscieveAnswer.translate());
            return false;
        }
    }


    // Enable Read Password
    @Override
    public boolean m24sdisableReadVerifReqCmd()
    {
        return m24srdiableVerifReqCmd(true);
    }

    // Enable write Password
    @Override
    public boolean m24sdisableWriteVerifReqCmd()
    {
        return m24srdiableVerifReqCmd(false);
    }

    // Disable Verification Command
    // Deactivate the protection  by password of the currently selected NDEF file.

    private boolean m24srdiableVerifReqCmd(boolean readmode)
    {
        // used command m24sr7816disableVerifReqCmd
        byte[] cmd = null;


        cmd = new byte[m24sr7816disableVerifReqCmd.length];


        System.arraycopy(m24sr7816disableVerifReqCmd, 0, cmd, 0, m24sr7816disableVerifReqCmd.length);

        // Configure Read or Write mode request
        if (readmode)
        {
            cmd[P2_INDEX] = (byte) 0x01;
        }
        else
        {
            cmd[P2_INDEX] = (byte) 0x02;
        }

        _mType4TagOperationBasicOp._lasttranscieveAnswer.set(_mType4TagOperationBasicOp.transcievecmd(cmd));

        if ((_mType4TagOperationBasicOp._lasttranscieveAnswer.getSW1() == (byte) 0x90)&&(_mType4TagOperationBasicOp._lasttranscieveAnswer.getSW2() == (byte)0x00))
        {
            return true;
        }
        else
        {
            Log.d(TAG,"m24sr7816disableVerifReqCmd Failed " + _mType4TagOperationBasicOp._lasttranscieveAnswer.translate());
            return false;
        }
    }


}
