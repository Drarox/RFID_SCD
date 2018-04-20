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


package com.st.NDEF;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;


//Implements NFC Forum RTD URI NDEF message



public class NDEFMailMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    private final byte  _ID = 0x06; // mailto - NDEFURIIDCode -
    private String _mContact;
    private String _mSubject;
    private String _mMessage;

    private Locale _Locale;
    boolean _Utf8Enc = true;

    /**
     * Methods
     */
    // Constructors
    public NDEFMailMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_MAIL);
        // Default values
         _mContact ="";
        _mSubject="";
        _mMessage="";
        _Locale = Locale.getDefault();
        _Utf8Enc = true;

    }

    public NDEFMailMessage(String contact, String subject, String message) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_MAIL);
        _mContact = contact;
        _mSubject=subject;
        _mMessage=message;
        _Locale = Locale.getDefault();
        _Utf8Enc = true;

    }

    // Accessors
    public String get_mContact() {
        return _mContact;
    }

    public void set_mContact(String contact) {
        this._mContact = contact;
    }

    public String get_mSubject() {
        return _mSubject;
    }

    public void set_mSubject(String subject) {
        this._mSubject = subject;
    }

    public String get_mMessage() {
        return _mMessage;
    }

    public void set_mMessage(String message) {
        this._mMessage = message;
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
        if (isSimplifiedMessage(mTnf, rtdType) && ( ndefHandler.getpayload(0)[0] == (byte) 0x06) ) {
            byte [] payload = ndefHandler.getpayload(0);
            // Mail Message
            _mContact = "";
            _mSubject = "";
            _mMessage = "";
            String tmpmessage;
            String message;
            // Patch for Compatibility of encoding/decoding
            _Utf8Enc = (payload[0]>>7 == 0)?true:false;
            //int langCodeLgth = payload[0] & 0x1F;
            //_Locale = Locale.getDefault();
            //int txtSize = payload.length - 1 - langCodeLgth;
            // Transform payload to String, using UTF-16 charset if initial data are encoded in UTF-8
            //byte [] txtPayload = new byte [txtSize];
            //System.arraycopy(payload, 1+langCodeLgth, txtPayload, 0, txtSize);
            if (_Utf8Enc) {
                message = new String(payload, Charset.forName("UTF-8"));
            } else {
                message = new String(payload, Charset.forName("UTF-16"));
            }

            // End ==> Patch for Compatibility of encoding/decoding

            //String message = new String(payload);
/*            try {
                message = new String(payload,"US-ASCII");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                message = new String(payload);
                e.printStackTrace();
            }*/
            String [] contactScissor=message.split("\\?",2);
            //String [] contactScissor=message.split("\\?");
            if (!contactScissor[0].isEmpty()) // else stay with an empty message
            {
                _mContact = contactScissor[0].substring(1); // remove first char
                if ((contactScissor.length>1) && (!contactScissor[1].isEmpty())) // with have subject &/| message
                {
                    if (contactScissor[1].matches("(subject=).*") || contactScissor[1].matches("subject=*"))
                    {
                        String [] subjectScissor=contactScissor[1].split("subject="); // remove split string
                        String [] messageScissor = subjectScissor[1].split("\\&body=",2);
                        if (!messageScissor[0].isEmpty()) {
                            _mSubject = messageScissor[0];
                            _mMessage = ((messageScissor.length>1) && (!messageScissor[1].isEmpty()))?messageScissor[1]:"";
                        }
                        else {
                            _mMessage = subjectScissor[0];
                        }
                    }
                    else
                    {
                        tmpmessage = contactScissor[1];
                        String [] subjectScissor=tmpmessage.split("&body=",2);
                        if ((subjectScissor.length>1) && (!subjectScissor[1].isEmpty())) {
                            _mMessage = subjectScissor[1];
                            tmpmessage = subjectScissor[0];
                            subjectScissor=tmpmessage.split("subject=",2);
                            if ((subjectScissor.length>1) && (!subjectScissor[1].isEmpty())) {
                                _mSubject = subjectScissor[1];
                            }
                            else
                                _mSubject = subjectScissor[0];
                        }
                        else
                            _mMessage = subjectScissor[0];
                    }
                }
            }
        }
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, byte [] apayload) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType) && ( apayload[0] == (byte) 0x06) ) {
            byte [] payload = apayload.clone();
            // Mail Message
            _mContact = "";
            _mSubject = "";
            _mMessage = "";

            //String message = new String(payload);
            String message;

            // Patch for Compatibility of encoding/decoding
            _Utf8Enc = (payload[0]>>7 == 0)?true:false;
            //int langCodeLgth = payload[0] & 0x1F;
            //_Locale = Locale.getDefault();
            //int txtSize = payload.length - 1 - langCodeLgth;
            // Transform payload to String, using UTF-16 charset if initial data are encoded in UTF-8
            //byte [] txtPayload = new byte [txtSize];
            //System.arraycopy(payload, 1+langCodeLgth, txtPayload, 0, txtSize);
            if (_Utf8Enc) {
                message = new String(payload, Charset.forName("UTF-8"));
            } else {
                message = new String(payload, Charset.forName("UTF-16"));
            }

/*            try {
                message = new String(payload,"US-ASCII");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                message = new String(payload);
                e.printStackTrace();
            }*/

            String [] contactScissor=message.split("\\?",2);
            //String [] contactScissor=message.split("\\?");
            if (!contactScissor[0].isEmpty()) // else stay with an empty message
            {
                _mContact = contactScissor[0].substring(1); // remove first char
                if ((contactScissor.length>1) && (!contactScissor[1].isEmpty())) // with have subject &/| message
                {
                    if (contactScissor[1].matches("(subject=).*"))
                    {
                        String [] subjectScissor=contactScissor[1].split("subject="); // remove split string
                        String [] messageScissor = subjectScissor[1].split("\\&body=",2);
                        if (!messageScissor[0].isEmpty()) {
                            _mSubject = messageScissor[0];
                            _mMessage = ((messageScissor.length>1) && (!messageScissor[1].isEmpty()))?messageScissor[1]:"";
                        }
                        else {
                            _mMessage = subjectScissor[0];
                        }
                    }
                    else
                    {
                        String [] subjectScissor=message.split("body=",2);
                        if ((subjectScissor.length>1) && (!subjectScissor[1].isEmpty())) {
                            _mMessage = subjectScissor[1];
                            }
                        else
                            _mMessage = subjectScissor[0];
                    }
                }
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

            String fullMessage = _mContact+"?subject="+_mSubject+"&body="+_mMessage;

            byte[] payload = new byte[1+fullMessage.getBytes(Charset.forName("US-ASCII")).length ]; //add 1 for the URI Prefix
            payload[0] = (byte)_ID; //prefixes with URI ID

            System.arraycopy(fullMessage.getBytes(Charset.forName("US-ASCII")), 0, payload, 1, fullMessage.getBytes(Charset.forName("US-ASCII")).length);  //appends URI to payload
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
        String fullMessage = _mContact+"?subject="+_mSubject+"&body="+_mMessage;
        ndefHandler.setNdefRTDURI((byte)_ID, fullMessage);
    }

}

