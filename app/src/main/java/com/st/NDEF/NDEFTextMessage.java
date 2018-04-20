/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1710 $
  * Revision of last commit    :  $Rev: 1710 $
  * Date of last commit     :  $Date: 2016-02-28 18:02:32 +0100 (Sun, 28 Feb 2016) $
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

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;


public class NDEFTextMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    private String _Text;
    private Locale _Locale;
    boolean _Utf8Enc = true;

    /**
     * Methods
     */
    // Constructors
    public NDEFTextMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_TEXT);
        // Default values
        _Text = null;
        _Locale = Locale.getDefault();
        _Utf8Enc = true;
    }

    public NDEFTextMessage(String mText, Locale mLocale, boolean mEncodingInUTF8) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_TEXT);
        _Text = mText;
        _Locale = mLocale;
        _Utf8Enc = mEncodingInUTF8;
    }



    // Accessors
    public String  getText()                 { return _Text; }
    public void    setText(String mText)     { _Text = mText; }
    public Locale  getLocale()               { return _Locale; }
    public void    setLocale(Locale mLocale) { _Locale = mLocale; }
    public boolean isUTF8Encoding()          { return _Utf8Enc; }
    public void    setUTF8Encoding()         { _Utf8Enc = true; }
    public void    setUTF16Encoding()        { _Utf8Enc = false; }

    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {
        boolean result = false;

        // Text message: TNF_WELL_KNOWN with RTD_TEXT (0x54)
        // Later may also consider MIME type "text/plain" ?
        if ((mTnf == tnf.wellknown)
            && (Arrays.equals(rtdType, NdefRecord.RTD_TEXT))) {
            result = true;
        }
        return result;
    }

    private String NFCForumNDEFStringDecode(byte[] payload) {
        String text;
        try
     {
             //byte[] payload = record.getPayload();
         text = new String(payload);
             /*
          * payload[0] contains the "Status Byte Encodings" field, per the
          * NFC Forum "Text Record Type Definition" section 3.2.1.
          *
          * bit7 is the Text Encoding Field.
          *
          * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
          * The text is encoded in UTF16
          *
          * Bit_6 is reserved for future use and must be set to zero.
          *
          * Bits 5 to 0 are the length of the IANA language code.
          */

              //Get the Text Encoding
             String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";

             //Get the Language Code
             int languageCodeLength = payload[0] & 0077;
             String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

             //Get the Text
             text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

            //return new TextRecord(text, languageCode);
             return text;
     }
     catch(Exception e)
     {
             //throw new RuntimeException("Record Parsing Failure!!");
         return new String("Decoding text issue ..");

     }
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {
            byte [] payload = ndefHandler.getpayload(0);
            _Utf8Enc = (payload[0]>>7 == 0)?true:false;
            // TODO: get Locale from payload
            int langCodeLgth = payload[0] & 0x1F;
            _Locale = Locale.getDefault();
            // Text size
            int txtSize = payload.length - 1 - langCodeLgth;
            try {
                if (txtSize <= 0) {
                    // issue decoding with Lang code ... Do basic decode
                    txtSize = payload.length;
                    byte[] txtPayload = new byte[txtSize];
                    System.arraycopy(payload, 0, txtPayload, 0, txtSize);
                    if (_Utf8Enc) {
                        _Text = new String(txtPayload, Charset.forName("UTF-8"));
                    } else {
                        _Text = new String(txtPayload, Charset.forName("UTF-16"));
                    }
                } else {
                    // Transform payload to String, using UTF-16 charset if
                    // initial data are encoded in UTF-8
                    byte[] txtPayload = new byte[txtSize];
                    System.arraycopy(payload, 1 + langCodeLgth, txtPayload, 0, txtSize);
                    if (_Utf8Enc) {
                        _Text = new String(txtPayload, Charset.forName("UTF-8"));
                    } else {
                        _Text = new String(txtPayload, Charset.forName("UTF-16"));
                    }
                }
            } catch (Exception e) {
                // issue decoding with Lang code ... Do basic decode
                txtSize = payload.length;
                byte[] txtPayload = new byte[txtSize];
                System.arraycopy(payload, 0, txtPayload, 0, txtSize);
                if (_Utf8Enc) {
                    _Text = new String(txtPayload, Charset.forName("UTF-8"));
                } else {
                    _Text = new String(txtPayload, Charset.forName("UTF-16"));
                }

            }

        }
    }


    public void setNDEFMessage(tnf mTnf, byte[] rtdType, byte[] ndefpayload) {
        // If the NDEF message type is recognized, decode the data as per the
        // applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {
            byte[] payload = ndefpayload.clone();
            _Utf8Enc = (payload[0] >> 7 == 0) ? true : false;
            // TODO: get Locale from payload
            int langCodeLgth = payload[0] & 0x1F;
            _Locale = Locale.getDefault();
            // Text size
            int txtSize = payload.length - 1 - langCodeLgth;
            try {
                if (txtSize <= 0) {
                    // issue decoding with Lang code ... Do basic decode
                    txtSize = payload.length;
                    byte[] txtPayload = new byte[txtSize];
                    System.arraycopy(payload, 0, txtPayload, 0, txtSize);
                    if (_Utf8Enc) {
                        _Text = new String(txtPayload, Charset.forName("UTF-8"));
                    } else {
                        _Text = new String(txtPayload, Charset.forName("UTF-16"));
                    }
                } else {
                    // Transform payload to String, using UTF-16 charset if
                    // initial data are encoded in UTF-8
                    byte[] txtPayload = new byte[txtSize];
                    System.arraycopy(payload, 1 + langCodeLgth, txtPayload, 0, txtSize);
                    if (_Utf8Enc) {
                        _Text = new String(txtPayload, Charset.forName("UTF-8"));
                    } else {
                        _Text = new String(txtPayload, Charset.forName("UTF-16"));
                    }
                }
            } catch (Exception e) {
                // issue decoding with Lang code ... Do basic decode
                txtSize = payload.length;
                byte[] txtPayload = new byte[txtSize];
                System.arraycopy(payload, 0, txtPayload, 0, txtSize);
                if (_Utf8Enc) {
                    _Text = new String(txtPayload, Charset.forName("UTF-8"));
                } else {
                    _Text = new String(txtPayload, Charset.forName("UTF-16"));
                }

            }
        }
    }

    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;

        // Check if there is a valid text
        if (_Text == null) {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input Text");
        } else {
            // As per RTD Text spec, RTD content is:
            // 1 byte: status byte:
            //        |        7         |  6  |  5   4   3   2   1   0 |
            //        | 0: Text in UTF-8 | RFU | IANA lang code length  |
            //        | 1: Text in UTF16 | (0) |                        |
            // n bytes: ISO/IANA language code.
            //          Examples: “fi?, “en-US?, “fr-CA?, “jp?. Encoding is US-ASCII.
            // m bytes: The actual text. Encoding is the one of status byte

            // Implementation of the "TNF_WELL_KNOWN with RTD_TEXT" use case,
            // as advised in "NFC Basics" developers guide, on http://developer.android.com
            // (https://developer.android.com/guide/topics/connectivity/nfc/nfc.html)
            // Prepare language and its encoding, as per the input parameters
            byte[] langBytes = _Locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
            Charset utfEncoding = _Utf8Enc ? Charset.forName("UTF-8") : Charset.forName("UTF-16");

            // Turn the text as per the encoding
            // TODO: RTD_Text spec says:
                // Control characters (0x00-0x1F in UTF-8) should be removed prior to display, except for newline,
                // line feed (0x0D, 0x0A) and tab (0x08) characters. Markup MUST NOT be embedded (please use
                // the “text/xhtml? or other suitable MIME types). The Text record should be considered to be equal
                // to the MIME type “text/plain; format=fixed?.
                // Line breaks in the text MUST be represented using the CRLF (so-called DOS convention, the
                // sequence 0x0D,0x0A in UTF-8). The device may deal with the tab character as it wishes.
                // White space other than newline and tab SHOULD be collapsed, i.e., multiple space characters are
                // to be considered a single space character.
                // To be applied !!!
            byte[] textBytes = _Text.getBytes(utfEncoding);

            // Build the RTD Text buffer
            byte[] data = new byte[1 + langBytes.length + textBytes.length];
            // - Status byte
            int utfBit = _Utf8Enc ? 0 : (1 << 7);
            char status = (char) (utfBit + langBytes.length);
            data[0] = (byte) status;
            // - ISO/IANA language code
            System.arraycopy(langBytes, 0, data, 1, langBytes.length);
            // - payload
            System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

            // Now create the record with the given parameters
            NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
            NdefRecord[] records = new NdefRecord[] {record};

            // Create the msg to be returned
            msg = new NdefMessage(records);
        }

        return msg;
    }

    //following Text Record Type Definition - Text 1.0 2006-07-24
    // Update ndefHandler message with current Text
    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {
        // update ST NDEF message object
        ndefHandler.setNdefRTDText(_Text);

    }
}
