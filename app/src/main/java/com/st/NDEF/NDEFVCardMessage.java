/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1616 $
  * Revision of last commit    :  $Rev: 1616 $
  * Date of last commit     :  $Date: 2016-02-03 19:03:03 +0100 (Wed, 03 Feb 2016) $
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

import com.st.NFC.NFCApplication;
import com.st.demo.R;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;


public class NDEFVCardMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    public VcardHandler _VCardHandler;
    /**
     * Methods
     */


    static final String TAG = "VCARD - TEST";
    // Constructors
    public NDEFVCardMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_VCARD);
        _VCardHandler = new VcardHandler();
    }

    public NDEFVCardMessage(String mText, Locale mLocale, boolean mEncodingInUTF8) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_VCARD);

    }

    // Accessors
    public VcardHandler getVCardHandler()  { return _VCardHandler;}
    public void  setVCardHandler(VcardHandler _aVCardHandler)  { _VCardHandler = _aVCardHandler; }

    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {
        boolean result = false;

        // Text message: TNF_WELL_KNOWN with RTD_TEXT (0x54)
        // Later may also consider MIME type "text/plain" ?
        if ((mTnf == tnf.media)
            && (
                    (Arrays.equals(rtdType, NFCApplication.getContext().getString(R.string.mime_type_text_vcard).getBytes()))
                    || (Arrays.equals(rtdType, NFCApplication.getContext().getString(R.string.mime_type_text_vcard_deprecated1).getBytes()))
                    || (Arrays.equals(rtdType, NFCApplication.getContext().getString(R.string.mime_type_text_vcard_deprecated2).getBytes()))
                    )
                ) {
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
                String Vcard = new String(payload);
                VCardParser _VCardParser = new VCardParser(Vcard);
                _VCardParser.parse(_VCardHandler);
            }
            else
            {
                // Need to notified that we don't have a
            }

        }
    }
    public void setNDEFMessage(tnf mTnf, byte [] rtdType, byte[] payload) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {

            if (payload.length !=0)
            {
                String Vcard = new String(payload);
                VCardParser _VCardParser = new VCardParser(Vcard);
                _VCardParser.parse(_VCardHandler);
            }
            else
            {
                // Need to notified that we don't have a
            }

        }
    }
    // Method for "Write to tag" operation (get data from UI or external app, and format it to NDEF message)
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;

        // Check if there is a valid Name
        if (this._VCardHandler.getName() == null) {
               Log.e(this.getClass().getName(), "Error in VCARD NDEF msg creation: No input Name");
        } else {
            export2VCard();
            byte[] payload = this._VCardHandler.getVcard().getBytes(Charset.forName("US-ASCII"));
            // NdefRecord rtdUriRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(), new byte[0], payload);

            // x-vCard Obsolete but required to get interroperability.

            NdefRecord rtdUriRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/x-vCard".getBytes(), new byte[0], payload);
            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {rtdUriRecord};
            msg = new NdefMessage(records);
        }

        return msg;
    }

    public void export2VCard()
    {

        Context context = NFCApplication.getContext();
        String returnStr = "\r\n";
        String vcardString = "";
        // _VCardHandler._Vcard ="BEGIN:VCARD\nVERSION:2.1\n";
        vcardString ="BEGIN:VCARD"+returnStr+"VERSION:2.1"+returnStr;
        if (context.getResources().getString(R.string.name_hint) != _VCardHandler.getName())
        {
            vcardString =  vcardString + "N:;"+ _VCardHandler.getName()+";;;"+returnStr;
            vcardString =  vcardString + "FN:"+ _VCardHandler.getName()+returnStr;
        }
        else
        {
            // FN field is mandatory .. return empty VCard
            _VCardHandler.setVcard("");
            return;
        }
        if (context.getResources().getString(R.string.email_hint) != _VCardHandler.getEmail())
        {
            vcardString =  vcardString + "EMAIL;WORK:"+_VCardHandler.getEmail()+returnStr;
        }

        if (context.getResources().getString(R.string.SPAddr_hint) != _VCardHandler.getSPAddr())
        {
            vcardString =  vcardString + "ADR:"+_VCardHandler.getSPAddr()+";;;;;;;"+returnStr;
        }
        if (context.getResources().getString(R.string.number_hint) != _VCardHandler.getNumber())
        {
            vcardString = vcardString + "TEL;CELL:"+_VCardHandler.getNumber()+returnStr;
        }
           if ((_VCardHandler.getWebSite()!=null)&&(!_VCardHandler.getWebSite().isEmpty()))
        {
            vcardString = vcardString + "URL:"+_VCardHandler.getWebSite()+returnStr;
        }
           // added for tests purpose of multiple URL for VCard
           if ((_VCardHandler.getWebSite()!=null)&&(!_VCardHandler.getWebSite().isEmpty()))
        {
            vcardString = vcardString + "X-URL:"+_VCardHandler.getWebSite()+returnStr;
        }

        if ((_VCardHandler.getPhoto()!=null)&&(!_VCardHandler.getPhoto().isEmpty()))
        {
            vcardString = vcardString + "PHOTO;JPEG;ENCODING=BASE64:"+_VCardHandler.getPhoto()+returnStr;
        }

        vcardString = vcardString + "END:VCARD";

        Log.d(TAG, "Contact VCARD Formatted : \n" + vcardString);
        _VCardHandler.setVcard(vcardString);


    byte[] payload = _VCardHandler.getVcard().getBytes(Charset.forName("US-ASCII"));
    // byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
    //payload[0] = 0x01;                                         //prefixes http://www. to the URI
    // System.arraycopy(uriField, 0, payload, 1, payload.length);  //appends URI to payload

    // nfcRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/x-vcard".getBytes(), new byte[0], payload);

    Log.d(TAG, "Ndef Record Ready : \n");
    }

    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {
        // update ST NDEF message object
        export2VCard();
        ndefHandler.setNdefVCard(_VCardHandler._Vcard);

    }
}
