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


import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.st.NFC.NFCApplication;
import com.st.demo.R;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;


//Implements NFC Forum RTD URI NDEF message


public class NDEFWifiHandoverMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    private String TAG = this.getClass().getName();
    // public CWifiCredential _mCredential;

    // CWifiCredential class declaration to store Wifi credential in read or compose mode.

    // public class CWifiCredential {

        public int         _mNetworkIndex;
        public String     _mSSID;
        public int           _mNetAuthType;
        public int          _mNetEncrType;
        public String     _mEncrKey;
        public String     _mMacAddr;
        public int         _KeySharable;

        //public void CWifiCredential(){};

    //};



    private byte [] _payload;

    public int getAuthType() {return _mNetAuthType ;}
    public int getEncrType() { return _mNetEncrType;}
    public String getEncrKey() {return _mEncrKey;}

    /**
     * Methods
     */
    // Constructors
    public NDEFWifiHandoverMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_WIFIHANDOVER);

        //_mCredential = new CWifiCredential();
        /*_mCredential.*/_mNetworkIndex = 1;
        /*_mCredential.*/_mSSID ="";
        /*_mCredential.*/_mNetAuthType = 0;
        /*_mCredential.*/_mNetEncrType = 0;
        /*_mCredential.*/_mEncrKey = "";
        /*_mCredential.*/_mMacAddr = "";
        /*_mCredential.*/_KeySharable = 0;
        _payload = null;

    }

    public NDEFWifiHandoverMessage(String SSID,
                                     int authType,
                                     int encrType,
                                     String password) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_WIFIHANDOVER);
        // _mCredential = new CWifiCredential();
        /*_mCredential.*/_mNetworkIndex = 1;
        /*_mCredential.*/_mSSID =SSID;
        /*_mCredential.*/_mNetAuthType = authType;
        /*_mCredential.*/_mNetEncrType = encrType;
        /*_mCredential.*/_mEncrKey = password;
        /*_mCredential.*/_mMacAddr = "";
        /*_mCredential.*/_KeySharable = 0;
        _payload = null;

    }



    // Accessors


    public String getSSID() {
        return     /*_mCredential.*/_mSSID;
    }

    public void set_mSSID(String _mSSID) {
        /*_mCredential.*/_mSSID  = _mSSID;
    }



    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {

        if ( (mTnf == tnf.media)
            &&
            (Arrays.equals(rtdType, NFCApplication.getContext().getString(R.string.mime_type_wifi_handover).getBytes())) )
            return true;

        return false;
    }


    private void parseWifiHandoverMessage(ByteBuffer payload) {

        byte [] version;
        byte [] indexNet;
        byte [] SSID;
        byte [] authNet;
        byte [] encryptNet;
        byte [] networkKey;
        byte [] macAddr;
        byte [] vendorExtension;

        int temp = 0;

        int credentialSize = 0;
        payload.position(0);
        try {
            while (payload.remaining() > 0) {

                if((byte)payload.get() != (byte) 0x10)
                {
                    Log.d(TAG,"Missing ID Attribute");
                }

                byte type = (byte) payload.get();
                int len = (int)((payload.get()&0xFF)<<8) + (int)(payload.get()&0xFF) ;

                switch (type) {
                    case (byte) 0x4A:  //Version ID
                        version = new byte[len];
                        payload.get(version);

                        break;
                    case (byte) 0x0E:  // Credential
                        credentialSize = len;
                        break;
                    case (byte) 0x26: // Index Attribute
                        indexNet = new byte[len];
                        payload.get(indexNet);
                        break;
                    case (byte) 0x45: // SSID Attributes
                        SSID = new byte[len];
                        payload.get(SSID);
                    try {
                        _mSSID = new String(SSID, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                        break;
                    case (byte) 0x03: //Auth Attribute
                        authNet = new byte[len];
                        payload.get(authNet);
                        temp = 0;
                        for (int i = 0; i<len;i++)
                        {
                            temp = temp <<8;
                            temp = temp + (authNet[i]&0xFF);
                        }
                        this._mNetAuthType = temp;
                        break;
                    case (byte)  0x0F: //Encryp Attribute
                        encryptNet = new byte[len];
                        payload.get(encryptNet);
                        temp = 0;
                        for (int i = 0; i<len;i++)
                        {
                            temp = temp <<8;
                            temp = temp + (encryptNet[i]&0xFF);
                        }
                        this._mNetEncrType = temp;
                        break;
                    case (byte) 0x27: //Network key attribute
                        networkKey = new byte[len];
                        payload.get(networkKey);
                    try {
                        _mEncrKey = new String(networkKey, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                        break;
                    case (byte) 0x20: // Mac Address Attribute
                        macAddr = new byte[len];
                        payload.get(macAddr);
                        break;
                    case (byte) 0x49: //Vendor Extension attribute - we may have severa vendor extension
                        vendorExtension = new byte[len];
                        payload.get(vendorExtension);
                        break;
                    default:
                        payload.position(payload.position() + len - 1);
                        break;
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Wifi : invalid Wifi parameter");
        } catch (BufferUnderflowException e) {
            Log.e(TAG, "Wifi: payload shorter than expected");
        }
    }


    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {

        // workaround as stnfcndefhandler doesn't yet support multiple record.
          // Record #0  - Handover Select Record is managed in static way
          // Record #1  - BlueTooth Secure Simple Pairing record is managed by stnfcdefhandler

        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {
            byte [] payload = ndefHandler.getpayload(0);
            ByteBuffer payloadbuffer = ByteBuffer.allocate(payload.length);
            payloadbuffer.put(payload);
            parseWifiHandoverMessage(payloadbuffer);

        }
    }


    private void serialize()
    {
        _payload=null;

        byte [] version;
        byte [] defaultversion = {(byte)0x10,(byte)0x4A,(byte)0x00,(byte)0x01,(byte)0x10};
        byte [] credential;
        byte [] defaultcredential = {(byte)0x10,(byte)0x0E,(byte)0x00,(byte)0x00}; // to update once credential is built

        byte [] indexNet;
        byte [] defaultindexNet = {(byte)0x10,(byte)0x26,(byte)0x00,(byte)0x01,(byte)0x01};
        byte [] SSID;
        byte [] AttribIDSSID = {(byte)0x10,(byte)0x45};


        byte [] authNet;
        byte [] defaultauthNet = {(byte)0x10,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00}; // open Config : 0x00 / WPAPSK : 0x01
        byte [] encryptNet;
        byte [] defaultencryptNet = {(byte)0x10,(byte)0x0F,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00};
        byte [] networkKey = null;
        byte [] defaultnetworkKey = {(byte)0x10,(byte)0x27,(byte)0x00,(byte)0x00};
        byte [] macAddr;
        byte [] defaultMacAddr = {(byte)0x10,(byte)0x20,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};
        byte [] vendorExtension;
        byte [] defaultvendorExtension1 = {(byte)0x10,(byte)0x49,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x37,(byte)0x2A,(byte)0x02,(byte)0x01,(byte)0x01};
        byte [] defaultvendorExtension2 = {(byte)0x10,(byte)0x49,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x37,(byte)0x2A,(byte)0x00,(byte)0x01,(byte)0x20};

        int IDAttribLength             = 2;
        int sizeParameterLength     = 2;
        int CredentialTokenLength     = 0;
        int PasswordKeyTokenLength     = 0;
        int versionLength             = 5;
        int credentialLength         = 4;
        int netIndexLength             = 5;
        int netauthNetLength         = 6;
        int encryptNetLength        = 6;
        int defaultMacAddrLength    = 10;

        // Version
        version = new byte[versionLength];
        System.arraycopy(defaultversion, 0, version,0, versionLength);

        // Credential
        credential = new byte[credentialLength];
        System.arraycopy(defaultcredential, 0, credential,0, credentialLength);

        // Network Index
        indexNet = new byte[netIndexLength];
        System.arraycopy(defaultindexNet, 0, indexNet,0, netIndexLength);
        CredentialTokenLength += indexNet.length;
        // SSID
        SSID = new byte[IDAttribLength + sizeParameterLength + /*_mCredential.*/_mSSID.getBytes().length];
        System.arraycopy(AttribIDSSID, 0, SSID,0, AttribIDSSID.length);
        SSID[2] = (byte) ((/*_mCredential.*/_mSSID.getBytes().length& 0xFF00)>>8);
        SSID[3] = (byte) ((/*_mCredential.*/_mSSID.getBytes().length& 0xFF));
        System.arraycopy(/*_mCredential.*/_mSSID.getBytes(), 0, SSID,4, /*_mCredential.*/_mSSID.getBytes().length);
        CredentialTokenLength += SSID.length;
        // authNet
        authNet = new byte[netauthNetLength];
        System.arraycopy(defaultauthNet, 0, authNet,0, authNet.length);
        authNet[5] =  (byte)(/*_mCredential.*/_mNetAuthType & 0xFF);
        CredentialTokenLength += authNet.length;
        // encryptNet
        encryptNet = new byte[encryptNetLength];
        System.arraycopy(defaultencryptNet, 0, encryptNet,0, encryptNet.length);
        encryptNet[5] =  (byte)(/*_mCredential.*/_mNetEncrType & 0xFF);
        CredentialTokenLength += encryptNet.length;
        //networkKey
        if ((/*_mCredential.*/_mEncrKey != null) && (!/*_mCredential.*/_mEncrKey.isEmpty()))
        {
            networkKey = new byte[IDAttribLength + sizeParameterLength + /*_mCredential.*/_mEncrKey.getBytes().length];
            System.arraycopy(defaultnetworkKey, 0, networkKey,0, defaultnetworkKey.length);
            networkKey[2] = (byte) ((/*_mCredential.*/_mEncrKey.getBytes().length& 0xFF00)>>8);
            networkKey[3] = (byte) ((/*_mCredential.*/_mEncrKey.getBytes().length& 0xFF));
            System.arraycopy(/*_mCredential.*/_mEncrKey.getBytes(), 0, networkKey,4,/*_mCredential.*/_mEncrKey.getBytes().length);
            CredentialTokenLength += networkKey.length;
        }

        // macAddr
        macAddr = new byte[defaultMacAddrLength];
        System.arraycopy(defaultMacAddr, 0, macAddr,0,defaultMacAddrLength);
        CredentialTokenLength += macAddr.length;
        //Vendor Extension
        vendorExtension = new byte[defaultvendorExtension1.length+defaultvendorExtension2.length];
        System.arraycopy(defaultvendorExtension1, 0, vendorExtension,0,defaultvendorExtension1.length);
        System.arraycopy(defaultvendorExtension2, 0, vendorExtension,defaultvendorExtension1.length,defaultvendorExtension2.length);
        CredentialTokenLength += vendorExtension.length;

        credential[2] = (byte) ((CredentialTokenLength& 0xFF00)>>8); ;
        credential[3] = (byte) ((CredentialTokenLength& 0xFF));

        int payloadLenght = CredentialTokenLength + version.length + credential.length;
        _payload = new byte [payloadLenght];
        int offset = 0;

        System.arraycopy(version, 0, _payload,0,version.length);
        offset += version.length;

        System.arraycopy(credential, 0, _payload,offset,credential.length);
        offset += credential.length;

        System.arraycopy(indexNet, 0, _payload,offset,indexNet.length);
        offset += indexNet.length;

        System.arraycopy(SSID, 0, _payload,offset,SSID.length);
        offset += SSID.length;

        System.arraycopy(authNet, 0, _payload,offset,authNet.length);
        offset += authNet.length;

        System.arraycopy(encryptNet, 0, _payload,offset,encryptNet.length);
        offset += encryptNet.length;

        if (networkKey!=null)
        {
            System.arraycopy(networkKey, 0, _payload,offset,networkKey.length);
            offset += networkKey.length;
        }
        System.arraycopy(macAddr, 0, _payload,offset,macAddr.length);
        offset += macAddr.length;

        System.arraycopy(vendorExtension, 0, _payload,offset,vendorExtension.length);
        offset += vendorExtension.length;

    }


    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;
        serialize();
        NdefRecord rtdWifiHandoverRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,"application/vnd.wfa.wsc".getBytes(), new byte[0]/*id*/, _payload);
        // Create the msg to be returned
        NdefRecord[] records = new NdefRecord[] {rtdWifiHandoverRecord};
        msg = new NdefMessage(records);
        return msg; /*application/vnd.wfa.wsc*/
    }


    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {

        // update ST NDEF message object
        serialize();
        ndefHandler.setNdefWiFiHandover(_payload);
    }

}
