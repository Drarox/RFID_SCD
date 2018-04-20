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




public class NDEFURIMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */
     // Implements NFC Forum RTD URI NDEF message

    public enum NDEFURIIDCode {
        // 0 0x00 N/A. No prepending is done, and the URI field contains the unabridged URI.
        NDEF_RTD_URI_ID_NO_PREFIX,
        //1 0x01 http://www.
        NDEF_RTD_URI_ID_HTTP_WWW,
        //2 0x02 https://www.
        NDEF_RTD_URI_ID_HTTPS_WWW,
        //3 0x03 http://
        NDEF_RTD_URI_ID_HTTP,
        //4 0x04 https://
        NDEF_RTD_URI_ID_HTTPS,
        //5 0x05 tel:
        NDEF_RTD_URI_ID_TEL,
        //6 0x06 mailto:
        NDEF_RTD_URI_ID_MAILTO,
        //7 0x07 ftp://anonymous:anonymous@
        NDEF_RTD_URI_ID_FTP_ANONYMOUS,
        //8 0x08 ftp://ftp.
        NDEF_RTD_URI_ID_FTP_FTP,
        //9 0x09 ftps://
        NDEF_RTD_URI_ID_FTPS,
        //10 0x0A sftp://
        NDEF_RTD_URI_ID_SFTP,
        //11 0x0B smb://
        NDEF_RTD_URI_ID_SMB,
        //12 0x0C nfs://
        NDEF_RTD_URI_ID_NFS,
        //13 0x0D ftp://
        NDEF_RTD_URI_ID_FTP,
        //14 0x0E dav://
        NDEF_RTD_URI_ID_DAV,
        //15 0x0F news:
        NDEF_RTD_URI_ID_NEWS,
        //16 0x10 telnet://
        NDEF_RTD_URI_ID_TELNET,
        //17 0x11 imap:
        NDEF_RTD_URI_ID_IMAP,
        //18 0x12 rtsp://
        NDEF_RTD_URI_ID_RTSP,
        //19 0x13 urn:
        NDEF_RTD_URI_ID_URN,
        //20 0x14 pop:
        NDEF_RTD_URI_ID_POP,
        //21 0x15 sip:
        NDEF_RTD_URI_ID_SIP,
        //22 0x16 sips:
        NDEF_RTD_URI_ID_SIPS,
        //23 0x17 tftp:
        NDEF_RTD_URI_ID_TFTP,
        //24 0x18 btspp://
        NDEF_RTD_URI_ID_BTSPP,
        //25 0x19 btl2cap://
        NDEF_RTD_URI_ID_BTL2CAP,
        //26 0x1A btgoep://
        NDEF_RTD_URI_ID_BTGOEP,
        //27 0x1B tcpobex://
        NDEF_RTD_URI_ID_TCP_OBEX,
        //28 0x1C irdaobex://
        NDEF_RTD_URI_ID_IRDA_OBEX,
        //29 0x1D file://
        NDEF_RTD_URI_ID_FILE,
        //30 0x1E urn:epc:id:
        NDEF_RTD_URI_ID_URN_EPC_ID,
        //31 0x1F urn:epc:tag:
        NDEF_RTD_URI_ID_URN_EPC_TAG,
        //32 0x20 urn:epc:pat:
        NDEF_RTD_URI_ID_URN_EPC_PAT,
        //33 0x21 urn:epc:raw:
        NDEF_RTD_URI_ID_URN_EPC_RAW,
        //34 0x22 urn:epc:
        NDEF_RTD_URI_ID_URN_EPC,
        //35 0x23 urn:nfc:
        NDEF_RTD_URI_ID_URN_NFC
    }
    /**
     * Attributes
     */
    private NDEFURIIDCode _ID;
    private String _URI;
    private final static LinkedHashMap<String, NDEFURIIDCode> SupportedURICodesList =
        new LinkedHashMap<String, NDEFURIIDCode>() {{
            // 0 0x00 N/A. No prepending is done, and the URI field contains the unabridged URI.
            put("", NDEFURIIDCode.NDEF_RTD_URI_ID_NO_PREFIX);
            //1 0x01 http://www.
            put("http://www.", NDEFURIIDCode.NDEF_RTD_URI_ID_HTTP_WWW);
            //2 0x02 https://www.
            put("https://www.", NDEFURIIDCode.NDEF_RTD_URI_ID_HTTPS_WWW);
            //3 0x03 http://
            put("http://", NDEFURIIDCode.NDEF_RTD_URI_ID_HTTP);
            //4 0x04 https://
            put("https://", NDEFURIIDCode.NDEF_RTD_URI_ID_HTTPS);
            //5 0x05 tel:
            put("tel:", NDEFURIIDCode.NDEF_RTD_URI_ID_TEL);
            //6 0x06 mailto:
            put("mailto:", NDEFURIIDCode.NDEF_RTD_URI_ID_MAILTO);
            //7 0x07 ftp://anonymous:anonymous@
            put("ftp://anonymous:anonymous@", NDEFURIIDCode.NDEF_RTD_URI_ID_FTP_ANONYMOUS);
            //8 0x08 ftp://ftp.
            put("ftp://ftp.", NDEFURIIDCode.NDEF_RTD_URI_ID_FTP_FTP);
            //9 0x09 ftps://
            put("ftps://", NDEFURIIDCode.NDEF_RTD_URI_ID_FTPS);
            //10 0x0A sftp://
            put("sftp://", NDEFURIIDCode.NDEF_RTD_URI_ID_SFTP);
            //11 0x0B smb://
            put("smb://", NDEFURIIDCode.NDEF_RTD_URI_ID_SMB);
            //12 0x0C nfs://
            put("nfs://", NDEFURIIDCode.NDEF_RTD_URI_ID_NFS);
            //13 0x0D ftp://
            put("ftp://", NDEFURIIDCode.NDEF_RTD_URI_ID_FTP);
            //14 0x0E dav://
            put("dav://", NDEFURIIDCode.NDEF_RTD_URI_ID_DAV);
            //15 0x0F news:
            put("news:", NDEFURIIDCode.NDEF_RTD_URI_ID_NEWS);
            //16 0x10 telnet://
            put("telnet://", NDEFURIIDCode.NDEF_RTD_URI_ID_TELNET);
            //17 0x11 imap:
            put("imap:", NDEFURIIDCode.NDEF_RTD_URI_ID_IMAP);
            //18 0x12 rtsp://
            put("rtsp://", NDEFURIIDCode.NDEF_RTD_URI_ID_RTSP);
            //19 0x13 urn:
            put("urn:", NDEFURIIDCode.NDEF_RTD_URI_ID_URN);
            //20 0x14 pop:
            put("pop:", NDEFURIIDCode.NDEF_RTD_URI_ID_POP);
            //21 0x15 sip:
            put("sip:", NDEFURIIDCode.NDEF_RTD_URI_ID_SIP);
            //22 0x16 sips:
            put("sips:", NDEFURIIDCode.NDEF_RTD_URI_ID_SIPS);
            //23 0x17 tftp:
            put("tftp:", NDEFURIIDCode.NDEF_RTD_URI_ID_TFTP);
            //24 0x18 btspp://
            put("btspp://", NDEFURIIDCode.NDEF_RTD_URI_ID_BTSPP);
            //25 0x19 btl2cap://
            put("btl2cap://", NDEFURIIDCode.NDEF_RTD_URI_ID_BTL2CAP);
            //26 0x1A btgoep://
            put("btgoep://", NDEFURIIDCode.NDEF_RTD_URI_ID_BTGOEP);
            //27 0x1B tcpobex://
            put("tcpobex://", NDEFURIIDCode.NDEF_RTD_URI_ID_TCP_OBEX);
            //28 0x1C irdaobex://
            put("irdaobex://", NDEFURIIDCode.NDEF_RTD_URI_ID_IRDA_OBEX);
            //29 0x1D file://
            put("file://", NDEFURIIDCode.NDEF_RTD_URI_ID_FILE);
            //30 0x1E urn:epc:id:
            put("urn:epc:id:", NDEFURIIDCode.NDEF_RTD_URI_ID_URN_EPC_ID);
            //31 0x1F urn:epc:tag:
            put("urn:epc:tag:", NDEFURIIDCode.NDEF_RTD_URI_ID_URN_EPC_TAG);
            //32 0x20 urn:epc:pat:
            put("urn:epc:pat:", NDEFURIIDCode.NDEF_RTD_URI_ID_URN_EPC_PAT);
            //33 0x21 urn:epc:raw:
            put("urn:epc:raw:", NDEFURIIDCode.NDEF_RTD_URI_ID_URN_EPC_RAW);
            //34 0x22 urn:epc:
            put("urn:epc:", NDEFURIIDCode.NDEF_RTD_URI_ID_URN_EPC);
            //35 0x23 urn:nfc:
            put("urn:nfc:", NDEFURIIDCode.NDEF_RTD_URI_ID_URN_NFC);
        }};

    /**
     * Methods
     */
    // Constructors
    public NDEFURIMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_URI);
        // Default values
        _ID = NDEFURIIDCode.NDEF_RTD_URI_ID_NO_PREFIX;
        _URI = null;
    }
    public NDEFURIMessage(NDEFURIIDCode uriID, String uri) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_URI);
        _ID = uriID;
        _URI = uri;
    }

    // Accessors
    public NDEFURIIDCode getURIID()                    { return _ID; }
    public void          setURIID(NDEFURIIDCode uriID) { _ID = uriID; }
    public String        getURI()                      { return _URI; }
    public void          setURI(String uri)            { _URI = uri; }

    public static ArrayList<String> getSupportedURICodesList()                      { return new ArrayList<String>(Collections.synchronizedSet(SupportedURICodesList.keySet())); }
    public static NDEFURIIDCode     getURICodeFromStr(String codeStr)               { return SupportedURICodesList.get(codeStr); }
    public static int               getURICodePositionInList(NDEFURIIDCode uriCode) { return uriCode.ordinal(); }

    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {
        boolean result = false;
        // if ((mTnf == tnf.wellknown) && ((rtdType!=null) && (rtdType.length!=0) && (rtdType[0]!=(byte) 0x06))) // mail is a specific use case
         if ((mTnf == tnf.wellknown)
                    && (Arrays.equals(rtdType, NdefRecord.RTD_URI)))
            result = true;
        return result;
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {
            byte [] payload = ndefHandler.getpayload(0);
            _ID = NDEFURIIDCode.values()[payload[0]];
            // URI size
            int uriSize = payload.length - 1;
            // Transform payload to String, using UTF-16 charset if initial data are encoded in UTF-8
            byte [] uriPayload = new byte [uriSize];
            System.arraycopy(payload, 1, uriPayload, 0, uriSize);
            _URI = new String(uriPayload, Charset.forName("UTF-8"));
        }
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, byte [] ndefRecordPayload) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {
            byte [] payload = ndefRecordPayload.clone();
            _ID = NDEFURIIDCode.values()[payload[0]];
            // URI size
            int uriSize = payload.length - 1;
            // Transform payload to String, using UTF-16 charset if initial data are encoded in UTF-8
            byte [] uriPayload = new byte [uriSize];
            System.arraycopy(payload, 1, uriPayload, 0, uriSize);
            _URI = new String(uriPayload, Charset.forName("UTF-8"));
        }
    }


    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;

        // Check if there is a valid text
        if (_URI == null) {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input URI");
        } else {
            // As per RTD Text spec, RTD content is:
            // 1 byte: URI identifier code (see above SupportedURICodesList)
            // n bytes: URI in UTF-8

            // Implementation of the "TNF_WELL_KNOWN with RTD_URI" use case ("Creating the NdefRecord manually"),
            // as advised in "NFC Basics" developers guide, on http://developer.android.com
            // (https://developer.android.com/guide/topics/connectivity/nfc/nfc.html)
            // Prepare language and its encoding, as per the input parameters
            byte[] uriField = _URI.getBytes(Charset.forName("US-ASCII"));
            byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
            payload[0] = (byte) _ID.ordinal();                           //prefixes with URI ID
            System.arraycopy(uriField, 0, payload, 1, uriField.length);  //appends URI to payload
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
        ndefHandler.setNdefRTDURI((byte)getURICodePositionInList(_ID), _URI);
    }

}
