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

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;


//Implements NFC Forum RTD URI NDEF message



public class NDEFSmsMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    private final byte  _ID = 0x00; // Empty - NDEFURIIDCode -
    private String _mContact;
    private String _mMessage;
    /**
     * Methods
     */
    // Constructors
    public NDEFSmsMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_SMS);
        // Default values
         _mContact ="";
         _mMessage="";
    }

    public NDEFSmsMessage(String contact, String message) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_SMS);
        _mContact = contact;
        _mMessage=message;
    }

    // Accessors
    public String get_mContact() {
        return _mContact;
    }

    public void set_mContact(String _mContact) {
        this._mContact = _mContact;
    }

    public String get_mMessage() {
        return _mMessage;
    }

    public void set_mMessage(String _mMessage) {
        this._mMessage = _mMessage;
    }

    public byte get_ID() {
        return _ID;
    }

    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {
        boolean result = false;

        if ((mTnf == tnf.wellknown)
                && (Arrays.equals(rtdType, NdefRecord.RTD_URI))) // parialy  true. need to check also the first byte of the payload = 0x06
            result = true;
        return result;
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType) && ( ndefHandler.getpayload(0)[0] == (byte) 0x00) &&((new String(ndefHandler.getpayload(0))).matches(".?(sms:).*"))) {
            byte [] payload = new byte[ndefHandler.getpayload(0).length-5];
            System.arraycopy(ndefHandler.getpayload(0), 5, payload, 0, ndefHandler.getpayload(0).length-5);
            // need to remove 0x00 first char

            // Mail Message
            _mContact = "";
            _mMessage = "";

            String message = new String(payload);
            //String [] contactScissor=message.split("?",2);
            String [] contactScissormsg=message.split("(\\?body=)");
            if (!contactScissormsg[0].isEmpty()) // else stay with an empty message
            {
                _mContact = contactScissormsg[0]; // remove first byte 0x00 + "sms:"
                if ((contactScissormsg.length>1) && (!contactScissormsg[1].isEmpty())) // with have message
                {
                    _mMessage = contactScissormsg[1];
                }
                else
                {
                    _mMessage ="";
                }
            }
            else
            {
                _mMessage ="";
                _mContact = "";

            }
        }
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, byte [] apayload) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if ((apayload==null) || (apayload.length<5))
            return;
        if (isSimplifiedMessage(mTnf, rtdType) && ( apayload[0] == (byte) 0x00) &&((new String(apayload)).matches(".?(sms:).*"))) {
            byte [] payload = new byte[apayload.length-5];
            System.arraycopy(apayload, 5, payload, 0, apayload.length-5);
            // need to remove 0x00 first char

            // Mail Message
            _mContact = "";
            _mMessage = "";

            String message = new String(payload);
            //String [] contactScissor=message.split("?",2);
            String [] contactScissormsg=message.split("(\\?body=)");
            if (!contactScissormsg[0].isEmpty()) // else stay with an empty message
            {
                _mContact = contactScissormsg[0]; // remove first byte 0x00 + "sms:"
                if ((contactScissormsg.length>1) && (!contactScissormsg[1].isEmpty())) // with have message
                {
                    _mMessage = contactScissormsg[1];
                }
                else
                {
                    _mMessage ="";
                }
            }
            else
            {
                _mMessage ="";
                _mContact = "";

            }
        }
    }

    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;

        // Check if there is a valid text
        if (_mContact.isEmpty() ) {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input Contact");
        } else {
            String fullMessage = _mContact+"?body="+_mMessage;

            String smsString ="sms:";
            byte[] payload = new byte[1+smsString.getBytes(Charset.forName("US-ASCII")).length +fullMessage.getBytes(Charset.forName("US-ASCII")).length ];
            payload[0] = (byte)_ID; //prefixes with URI ID
            System.arraycopy(smsString.getBytes(Charset.forName("US-ASCII")), 0, payload, 1, smsString.getBytes(Charset.forName("US-ASCII")).length);
            System.arraycopy(fullMessage.getBytes(Charset.forName("US-ASCII")), 0, payload, 1+smsString.getBytes(Charset.forName("US-ASCII")).length, fullMessage.getBytes(Charset.forName("US-ASCII")).length);
            NdefRecord rtdUriRecord = new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);

            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {rtdUriRecord};
            msg = new NdefMessage(records);
        }

        return msg;
    }

    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {
        String fullMessage = "sms:"+_mContact+"?body="+_mMessage;
        ndefHandler.setNdefRTDURI((byte)_ID, fullMessage);
    }

}

