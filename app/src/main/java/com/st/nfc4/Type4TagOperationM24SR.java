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

package com.st.nfc4;

import java.io.IOException;

import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.stnfcProtectionLockMgt;
import com.st.NFC.stnfcm24ProtectionLockMgt;

import android.content.Context;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Type4TagOperationM24SR extends Type4TagOperationBasicOp
        implements Type4Tagm24sr7816STCommands, Iso7816_4APDU {
    private String TAG = "Type4TagOperationM24SR";

    protected stnfcProtectionLockMgt _mProtectionLockMgt;

    public stnfcProtectionLockMgt get_mProtectionLockMgt() {
        return _mProtectionLockMgt;
    }

    public void set_mProtectionLockMgt(stnfcProtectionLockMgt _mProtectionLockMgt) {
        this._mProtectionLockMgt = _mProtectionLockMgt;
    }

    public Type4TagOperationM24SR() {
        super();
        // TODO Auto-generated constructor stub
        _mProtectionLockMgt = new stnfcm24ProtectionLockMgt((Type4TagOperationBasicOp) this);
    }

    public Type4TagOperationM24SR(Tag tagToHandle) {
        super(tagToHandle);
        // TODO Auto-generated constructor stub
        _mProtectionLockMgt = new stnfcm24ProtectionLockMgt((Type4TagOperationBasicOp) this);
    }

    public byte[] processcmd(byte[] cmd) {
        return this.transcievecmd(cmd);
    }

    // Format TAG with Nb NDEF Files gives in parameters
    public boolean m24srFormatNdef(int nbNdefFiles) {
        // Sys files is selected
        // NDEF files must be in unprotected mode before.
        SYSUpdatNbNdefFiles[5] = (byte) ((nbNdefFiles - 1) & 0xFF);
        _lasttranscieveAnswer.set(transcievecmd(SYSUpdatNbNdefFiles));

        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            return true;
        } else {
            Log.d(TAG, "m24srFormatNdef Failed -  Requested " + nbNdefFiles + "NDEF Files"
                    + _lasttranscieveAnswer.translate());
            return false;
        }

    }

    public boolean m24srupdateBinarySize(int size) {
        boolean ret = true;
        if ((size >= 0) && (size < 0xFFFF)) // Restricted to 246 bytes - ie 244
                                            // + 2 (Ndef Size)
                                            // 0 is supported to ensure data
                                            // coherency on NDEF File
        {
            byte[] cmd = null;

            _lasttranscieveAnswer.reset();

            cmd = new byte[m24srNCFForumupdateBinarySize.length];
            System.arraycopy(m24srNCFForumupdateBinarySize, 0, cmd, 0, m24srNCFForumupdateBinarySize.length);
            cmd[DATA1_INDEX] = (byte) (((int) (size & 0xFF00)) >> 8);
            cmd[DATA2_INDEX] = (byte) (((int) (size & 0xFF)));
            _lasttranscieveAnswer.set(transcievecmd(cmd));

            if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
                ret = true;
            } else {
                Log.d(TAG, "m24srupdateBinarySize Failed - size to write" + size + _lasttranscieveAnswer.translate());
                ret = false;
            }

        } else {
            Log.d("TAG", " Wong update binary size ");
            ret = false;
        }
        return ret;
    }
    // m24srNCFForumupdateBinarySize

    public boolean m24srupdateBinary(byte[] binary) {
        // Value of the M24SR
        if (!m24srupdateBinarySize(0)) {
            Log.d(TAG, "m24srupdateBinary - Fail to write Size");
            return false;
        }
        // write binary
        // Verify timeout
        // int to = this.gettranscievetimeout();
        // Patch for SSGxx phone ... need to update the default timeout of 309
        // to more.
        this.settranscievetimeout(1000);

        // Get the current tag and update the max byte authorized to be written
        // in APDU cmd
        int MaxBytesWrite = 244;
        NFCApplication currentApp = (NFCApplication) NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        if (currentTag != null) {
            MaxBytesWrite = currentTag.getmaxbyteswritten();
            MaxBytesWrite = getMaxtranscievecmd(MaxBytesWrite);
        }

        byte[] cmd = null;

        // if (binary.length > 244)
        if (true) {
            // need to split binary and write in in several chunk
            int remainBytesToWrite = binary.length;
            int offset = 0;

            while (remainBytesToWrite > 0) {

                int dataToWrite = (remainBytesToWrite > MaxBytesWrite) ? MaxBytesWrite : remainBytesToWrite;
                cmd = new byte[m24srNCFForumupdateBinary.length + dataToWrite];
                remainBytesToWrite = remainBytesToWrite - dataToWrite;
                System.arraycopy(m24srNCFForumupdateBinary, 0, cmd, 0, m24srNCFForumupdateBinary.length);
                // update offset
                cmd[3] = (byte) ((2 + offset) & 0xFF);
                cmd[2] = (byte) (((2 + offset) & 0xFF00) >> 8);

                cmd[LC_INDEX] = (byte) (dataToWrite & 0xFF);
                System.arraycopy(binary, offset, cmd, LC_INDEX + 1, dataToWrite);
                offset = offset + dataToWrite;
                _lasttranscieveAnswer.set(transcievecmd(cmd));
                if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90)
                        && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
                    Log.d(TAG, "m24srupdateBinaryPayload succeed" + binary.length + _lasttranscieveAnswer.translate());
                } else {
                    Log.d(TAG, "m24srupdateBinarywithPassword Failed - size to write" + binary.length
                            + _lasttranscieveAnswer.translate());
                    return false;
                }
                _lasttranscieveAnswer.reset();

            }

        }
        // write binary size
        return m24srupdateBinarySize(binary.length);
    }

    public boolean m24srupdateBinarywithPassword(byte[] binary, byte[] password) {
        // m24srNCFForumupdateBinary

        // Select NDEF File

        // Verify if Protection unlock is requested
        if (!_mProtectionLockMgt.isNDEFWriteUnLocked()) {
            // If password == null -> send error message Password is required
            if ((password != null) && ((password.length == 0) || (password.length != 16))) {
                Log.d(TAG, "m24srupdateBinarywithPassword - password must not be empty or password size equal to 0x10");
                return false;
            }
            if (!_mProtectionLockMgt.isNDEFWriteunlock(password)) {
                // Fail to unprotected file
                Log.d(TAG, "m24srupdateBinarywithPassword - Wrong Password");
                return false;
            }
        }
        //

        // write size to zero
        return m24srupdateBinary(binary);

    }

    // ST Proprietary command Set

    // Extended Read Binary
    // requests M24SR to read the memory filed and send back its value in R-APDU

    private boolean m24srSTExtReadBinarycmd(int offsetInBytes, int maxExpectedDataLenght, byte[] answer) {
        // Used Command m24sr7816STExtReadBinarycmd
        byte[] cmd = null;
        byte P1 = (byte) ((int) (offsetInBytes & 0xFF00) >> 8);
        byte P2 = (byte) (offsetInBytes & 0x00FF);
        byte Le = (byte) (maxExpectedDataLenght & 0x00FF);
        if (Le > 0xF6) {
            _lasttranscieveAnswer.reset();
            return false;
        }

        cmd = new byte[m24sr7816STExtReadBinarycmd.length];

        cmd[P1_INDEX] = P1;
        cmd[P2_INDEX] = P2;

        _lasttranscieveAnswer.initBuff(maxExpectedDataLenght);
        _lasttranscieveAnswer.set(transcievecmd(cmd));

        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            if (_lasttranscieveAnswer.hasAnswerData()) {
                answer = new byte[_lasttranscieveAnswer.getbuffAnswer().length];
                System.arraycopy(_lasttranscieveAnswer.getbuffAnswer(), 0, answer, 0,
                        _lasttranscieveAnswer.getbuffAnswer().length - 2);
            }
            return true;
        } else {
            Log.d(TAG, "m24sr7816disableVerifReqCmd Failed " + _lasttranscieveAnswer.translate());
            return false;
        }

    }

    // Enable Permanent State Command in read mode
    public boolean m24srSTEnableReadPermState() {
        return m24srSTEnablePermState(true);
    }

    // Enable Permanent State Command in Write mode
    public boolean m24srSTEnableWritePermState() {
        return m24srSTEnablePermState(false);
    }

    // Enable Permanent State Command
    // Configure current NDEF file in ReadOnly or WriteOnly command.

    private boolean m24srSTEnablePermState(boolean readmode) {

        byte[] cmd = null;

        // Used command m24sr7816STenablePermState
        cmd = new byte[m24sr7816STEnablePermState.length];

        System.arraycopy(m24sr7816STEnablePermState, 0, cmd, 0, m24sr7816STEnablePermState.length);

        // Configure Read or Write mode request
        if (readmode) {
            cmd[P2_INDEX] = (byte) 0x01;
        } else {
            cmd[P2_INDEX] = (byte) 0x02;
        }

        _lasttranscieveAnswer.set(transcievecmd(cmd));

        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            return true;
        } else {
            Log.d(TAG, "m24srSTenablePermState Failed " + _lasttranscieveAnswer.translate());
            return false;
        }
    }

    // Disable Permanent State Command in read mode
    public boolean m24srSTDisableReadPermState() {
        return m24srSTDisablePermState(true);
    }

    // Disable Permanent State Command in Write mode
    public boolean m24srSTDisableWritePermState() {
        return m24srSTDisablePermState(false);
    }

    // Disable Permanent State Command
    // Configure current NDEF file in ReadOnly or WriteOnly command.

    private boolean m24srSTDisablePermState(boolean readmode) {

        byte[] cmd = null;

        // Used command m24sr7816STDisablePermState
        cmd = new byte[m24sr7816STDisablePermState.length];

        System.arraycopy(m24sr7816STDisablePermState, 0, cmd, 0, m24sr7816STDisablePermState.length);

        // Configure Read or Write mode request
        if (readmode) {
            cmd[P2_INDEX] = (byte) 0x01;
        } else {
            cmd[P2_INDEX] = (byte) 0x02;
        }

        _lasttranscieveAnswer.set(transcievecmd(cmd));

        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            return true;
        } else {
            Log.d(TAG, "m24srSTDisablePermState Failed " + _lasttranscieveAnswer.translate());
            return false;
        }
    }

    public boolean m24srSTSendInterrupt() {
        byte cmd[] = null;

        // Used command m24sr7816STDisablePermState
        cmd = new byte[m24sr7816STSendInterrupt.length];

        // Used Command m24sr7816STSendInterrupt
        System.arraycopy(m24sr7816STSendInterrupt, 0, cmd, 0, m24sr7816STSendInterrupt.length);

        _lasttranscieveAnswer.set(transcievecmd(cmd));

        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            return true;
        } else {
            Log.d(TAG, "m24srSTSendInterrupt Failed " + _lasttranscieveAnswer.translate());
            return false;
        }

    }

    public boolean m24srSTStateCtrlcmd(boolean setStateHz) {
        byte[] cmd = null;

        cmd = new byte[m24sr7816STStateCtrlcmd.length];

        // Used Command m24sr7816STStateCtrlcmd
        System.arraycopy(m24sr7816STStateCtrlcmd, 0, cmd, 0, m24sr7816STStateCtrlcmd.length);

        if (setStateHz) // true is
        {
            cmd[DATA1_INDEX] = (byte) 0x01;
        } else {
            cmd[DATA1_INDEX] = (byte) 0x00;
        }

        _lasttranscieveAnswer.set(transcievecmd(cmd));

        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            return true;
        } else {
            Log.d(TAG, "m24srSTStateCtrlcmd Failed " + _lasttranscieveAnswer.translate());
            return false;
        }
    }

    public int selectNdef(int NdefFileID) {

        // ndefSelectcmd

        byte ndefSelectCmdWithID[] = new byte[ndefSelectcmd.length];
        byte[] transcieveAnswer = new byte[] { (byte) 0x00 };

        // build Ndef Select command
        System.arraycopy(ndefSelectcmd, 0, ndefSelectCmdWithID, 0, ndefSelectcmd.length);
        ndefSelectCmdWithID[5] = (byte) (NdefFileID >> 8);
        ndefSelectCmdWithID[6] = (byte) (NdefFileID & 0xFF);

        if (isoDepCurrentTag == null)
            isoDepCurrentTag = IsoDep.get(this.currentTag);

        try {
            if (!isoDepCurrentTag.isConnected()) {
                isoDepCurrentTag.connect();
                isoDepCurrentTag.setTimeout(20);
            }

            transcieveAnswer = isoDepCurrentTag.transceive(ndefSelectCmdWithID);

            if (transcieveAnswer.length == 2 && transcieveAnswer[0] == (byte) 0x90
                    && transcieveAnswer[1] == (byte) 0x00) {
                // isoDepCurrentTag.close();
                return 1;
            } else {
                isoDepCurrentTag.close();
                return 0;
            }
        } catch (IOException e) {
            // e.printStackTrace();
            throw new RuntimeException("fail", e);
        }

    }

    public boolean srtag2kldisablecounter() {
        // Sys files is selected
        SYSUpdatConfigCounter[5] = (byte) (0x00);
        _lasttranscieveAnswer.set(transcievecmd(SYSUpdatConfigCounter));
        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            return true;
        } else {
            Log.d(TAG, "srtag2kldisablecounter Failed -  Requested " + _lasttranscieveAnswer.translate());
            return false;
        }

    }

    public boolean srtag2klenableWritecounter() {
        // Sys files is selected
        SYSUpdatConfigCounter[5] = (byte) (0x03);
        _lasttranscieveAnswer.set(transcievecmd(SYSUpdatConfigCounter));
        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            return true;
        } else {
            Log.d(TAG, "srtag2klenableWritecounter Failed -  Requested " + _lasttranscieveAnswer.translate());
            return false;
        }
    }

    public boolean srtag2klenableReadcounter() {
        // Sys files is selected
        SYSUpdatConfigCounter[5] = (byte) (0x02);
        _lasttranscieveAnswer.set(transcievecmd(SYSUpdatConfigCounter));
        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            return true;
        } else {
            Log.d(TAG, "srtag2klenableReadcounter Failed -  Requested " + _lasttranscieveAnswer.translate());
            return false;
        }
    }

    public boolean SetupGPOConfig(int config) {
        // Sys files is selected
        byte GPOCfg = 0x00;
        switch (config) {
        case 1:
            GPOCfg = 0x10;
            break;
        case 2:
            GPOCfg = 0x20;
            break;
        case 3:
            GPOCfg = 0x30;
            break;
        case 4:
            GPOCfg = 0x40;
            break;
        case 5:
            GPOCfg = 0x50;
            break;
        case 6:
            GPOCfg = 0x60;
            break;
        case 7:
            GPOCfg = 0x70;
            break;
        default:
            GPOCfg = 0x00;
            break;

        }
        ST25TANCFForumupdateBinaryGPOConfig[5] = (byte) (GPOCfg);
        _lasttranscieveAnswer.set(transcievecmd(ST25TANCFForumupdateBinaryGPOConfig));
        if ((_lasttranscieveAnswer.getSW1() == (byte) 0x90) && (_lasttranscieveAnswer.getSW2() == (byte) 0x00)) {
            return true;
        } else {
            Log.d(TAG, "SetupGPOConfig Failed -  Requested " + _lasttranscieveAnswer.translate());
            return false;
        }
    }

}
