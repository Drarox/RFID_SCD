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


package com.st.NDEF;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;


public class NDEFSPMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */

    private NdefMessage _mNDEFRecordlist; // list of NDEFRecord mapped into NDEFMessage
    private byte [] _mpayload;
    private String TAG = this.getClass().getName();

    /**
     * Methods
     */
    // Constructors
    public NDEFSPMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_SP);
        // Default values
        _mNDEFRecordlist = null;
        _mpayload = null;
    }

    public NDEFSPMessage(byte [] payload) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_SP);
        _mpayload = payload.clone();
        parseSPPayload(payload);
    }

    private void parseSPPayload(byte [] payload)
    {
        // SP message is composed as following -
        // SP NDEF header already consumed
        // Payload = list of Ndef record : Flags byte, Type length, payload length, ID length, Type, ID, Payload
        try {
            _mNDEFRecordlist = new NdefMessage(payload);
        } catch (FormatException e) {
            Log.e(TAG," Cannot parse the payload to retrieve records");
            _mNDEFRecordlist = null;
            e.printStackTrace();
        }
    }

    // Accessors

    public NdefMessage get_mNDEFRecordlist() {
        return _mNDEFRecordlist;
    }

    public void set_mNDEFRecordlist(NdefMessage _mNDEFRecordlist) {
        this._mNDEFRecordlist = _mNDEFRecordlist;
    }

    public byte[] get_mpayload() {
        return _mpayload;
    }

    public void set_mpayload(byte[] _mpayload) {
        this._mpayload = _mpayload;
    }


    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {
        boolean result = false;
         if ((mTnf == tnf.wellknown)
                    && (Arrays.equals(rtdType, NdefRecord.RTD_SMART_POSTER)))
            result = true;
        return result;
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {
            byte [] payload = ndefHandler.getpayload(0).clone();
            parseSPPayload(payload);
        }
    }

    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;

        // Check if there is a valid text
        if ((_mNDEFRecordlist == null) || (_mpayload == null))
        {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input Smart Poster message");
        } else {
            NdefRecord rtdSPRecord = new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_SMART_POSTER, new byte[0], _mpayload);
            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {rtdSPRecord};
            msg = new NdefMessage(records);
        }

        return msg;
    }

    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {
        ndefHandler.setNdefRTDSP(_mpayload);
    }

}
