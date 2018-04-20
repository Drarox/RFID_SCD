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
import java.util.Arrays;
import java.util.Locale;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;


public class NDEFAarMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    public String _mAar;
    /**
     * Methods
     */


    static final String TAG = "AAR - TEST";
    // Constructors
    public NDEFAarMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_AAR);
        _mAar = "";
    }

    public NDEFAarMessage(String aar) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_AAR);
        _mAar = aar;
    }

    // Accessors


    public String get_maar() {
        return _mAar;
    }

    public void set_maar(String _maar) {
        this._mAar = _maar;
    }

    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {
        boolean result = false;

        // Text message: TNF_WELL_KNOWN with RTD_TEXT (0x54)
        // Later may also consider MIME type "text/plain" ?
        if ( (mTnf == tnf.external) && (Arrays.equals(rtdType,"android.com:pkg".getBytes())) )
        {
            result = true;
        }
        return result;
    }

    // Implementation of abstract method(s) from NDEFSimplifiedMessage parent
    // Method to decode tag "Read from tag"
    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {

            byte [] payload = ndefHandler.getpayload(0);
            if (payload.length !=0)
            {
                _mAar = new String (ndefHandler.getpayload(0));
            }
            else
            {
                _mAar ="";
            }

        }
    }
    public void setNDEFMessage(tnf mTnf, byte [] rtdType, byte[] payload) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {

            if (payload.length !=0)
            {
                _mAar = new String (payload);
            }
            else
            {
                _mAar ="";
            }

        }
    }
    // Method for "Write to tag" operation (get data from UI or external app, and format it to NDEF message)
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;

            NdefRecord extRecord =  NdefRecord.createExternal("android.com","pkg",_mAar.getBytes());
            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {extRecord};
            msg = new NdefMessage(records);
        return msg;
    }

 
    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {
        // update ST NDEF message object
        ndefHandler.setNdefAar(_mAar);

    }
}
