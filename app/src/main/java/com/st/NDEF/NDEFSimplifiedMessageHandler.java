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

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import com.st.NFC.NFCApplication;
import com.st.demo.R;




public class NDEFSimplifiedMessageHandler {
    /**
     * Defines
     */
    //Some generic defines


    /**
     * Attributes
     */
    private final static Hashtable<String, NDEFSimplifiedMessageType> SupportedNDEFSimpleMsgList =
        new Hashtable<String, NDEFSimplifiedMessageType>() {{
            // NDEF_SIMPLE_MSG_TYPE_EMPTY,
            put(
                NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_empty),
                NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_EMPTY
            );
            // NDEF_SIMPLE_MSG_TYPE_TEXT
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_txt),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_TEXT
                );
            // NDEF_SIMPLE_MSG_TYPE_URI
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_bt_handover),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_BTHANDOVER
                );
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_bt_le),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_BTLE
            );
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_wifi_handover),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_WIFIHANDOVER
                );
            // NDEF_SIMPLE_MSG_TYPE_SMART_POSTER
            /*put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_smartposter),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_SMART_POSTER
                );*/
            // NDEF_SIMPLE_MSG_TYPE_TEL_NB
            /*put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_telnb),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_TEL_NB
                );*/
            // NDEF_SIMPLE_MSG_TYPE_SMS
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_sms),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_SMS
                );
            // NDEF_SIMPLE_MSG_TYPE_MAIL
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_mail),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_MAIL
                );
            // NDEF_SIMPLE_MSG_TYPE_VCARD
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_vcard),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_VCARD
                );
            // NDEF_SIMPLE_MSG_TYPE_BT_PAIR
            /*put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_btpair),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_BT_PAIR
                );*/
            // NDEF_SIMPLE_MSG_TYPE_WIFI_PAIR
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_wifipair),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_WIFIHANDOVER
                );
            // NDEF_SIMPLE_MSG_TYPE_URI
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_uri),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_URI
                );
            // NDEF_SIMPLE_MSG_TYPE_EXT_M24SRDISCOCTRL
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_m24srDiscoveryCtrl),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_EXT_M24SRDISCOCTRL
                );
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_aar),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_AAR
                );
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_mail),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_MAIL
                );
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_sp),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_SP
                );
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_Candy),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_EXT_GENCTRL
                );
            put(
                    NFCApplication.getContext().getResources().getString(R.string.mnf_frag_NDEF_rec_type_TranspCandy),
                    NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_EXT_TRANSP_GENCTRL
                );
        }};
    private NDEFSimplifiedMessage _curMessage = null;
    private stnfcndefhandler _ndefHandler = null;

    /**
     * Methods
     */
    // Constructor
    public NDEFSimplifiedMessageHandler(stnfcndefhandler mNdefHandler) {
        _ndefHandler = mNdefHandler;

        if (_ndefHandler != null) {
            parseNdefMsg();
        }
    }

    // Accessors
    public NDEFSimplifiedMessage            getNDEFSimplifiedMessage()                              { return _curMessage; }
    public static Collection<String>        getSupportedSimpleMsgStrList()                          { return Collections.list(SupportedNDEFSimpleMsgList.keys()); }
    public static NDEFSimplifiedMessageType getMsgTypeFromStr(String msgStr)                        { return SupportedNDEFSimpleMsgList.get(msgStr); }
    public static String                    getStrFromMsgType(NDEFSimplifiedMessageType msgType)    {
        String resStr = null;
        if (SupportedNDEFSimpleMsgList.containsValue(msgType)) {
            for (Map.Entry<String, NDEFSimplifiedMessageType> entry: SupportedNDEFSimpleMsgList.entrySet()) {
                if (msgType == entry.getValue()) {
                    resStr = (String) entry.getKey();
                    break;
                }
            }
        }

        return resStr;
    }
    public static int                       getMsgPositionInList(NDEFSimplifiedMessageType msgType) { return msgType.ordinal(); }

    // Private methods
    void parseNdefMsg() {
        // Identify the type of the Simplified NDEF message, as per TNF and RTD type
        // (1st record of the NDEF message determines it)
        // and call the right object constructor
        // TODO: for the moment, only 1 NDEF file supported in this version; to be improved...
        int unused = 0;

        // - Check first if NDEF message is a multiple NDEF Record message
        if (_ndefHandler.getRecordNb()>1)
        {
            _curMessage = new NDEFMultipleRecordMessage();
            _curMessage.setNDEFMessage(null,null, _ndefHandler);
        }

        // - Text message
        else if (NDEFTextMessage.isSimplifiedMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0))) {
            _curMessage = new NDEFTextMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }
        // - Sms message
        else if (
                (NDEFSmsMessage.isSimplifiedMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0)))
                        && ( _ndefHandler.getpayload(0)[0] == (byte) 0x00)
                        && ((new String(_ndefHandler.getpayload(0))).matches(".?(sms:).*") || (new String(_ndefHandler.getpayload(0))).matches(".?(Sms:).*"))
                )
        {
            _curMessage = new NDEFSmsMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }
        // - Mail message
        else if  ((NDEFMailMessage.isSimplifiedMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0)))
                && ( _ndefHandler.getpayload(0)[0] == (byte) 0x06)
                ) {
            _curMessage = new NDEFMailMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }        // - Uri message
        else if (
                (NDEFURIMessage.isSimplifiedMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0)))
                ) {
            _curMessage = new NDEFURIMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }

        // - VCard message
        else if (NDEFVCardMessage.isSimplifiedMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0))) {
            _curMessage = new NDEFVCardMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }
        // - Discovery Kit Control  message
        else if (NDEFDiscoveryKitCtrlMessage.isSimplifiedMessage(_ndefHandler.gettnf(0),_ndefHandler.gettype(0))) {
            _curMessage = new NDEFDiscoveryKitCtrlMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }
        // - BT Pairing message
        else if (NDEFBTHandoverMessage.isSimplifiedMessage(_ndefHandler.gettnf(0),_ndefHandler.gettype(0))) {
            _curMessage = new NDEFBTHandoverMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        } // - BT LE
        else if (NDEFBTLeMessage.isSimplifiedMessage(_ndefHandler.gettnf(0),_ndefHandler.gettype(0))) {
            _curMessage = new NDEFBTLeMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }
        // - AAR
        else if (NDEFAarMessage.isSimplifiedMessage(_ndefHandler.gettnf(0),_ndefHandler.gettype(0))) {
            _curMessage = new NDEFAarMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }
        // - SP
        else if (NDEFSPMessage.isSimplifiedMessage(_ndefHandler.gettnf(0),_ndefHandler.gettype(0))) {
            _curMessage = new NDEFSPMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }
        // Wifi
        else if (NDEFWifiHandoverMessage.isSimplifiedMessage(_ndefHandler.gettnf(0),_ndefHandler.gettype(0))) {
            _curMessage = new NDEFWifiHandoverMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }
        else if (NDEFGenCtrlMessage.isSimplifiedMessage(_ndefHandler.gettnf(0),_ndefHandler.gettype(0))) {
            _curMessage = new NDEFGenCtrlMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }
        else if (NDEFGenCtrlTranspMessage.isSimplifiedMessage(_ndefHandler.gettnf(0),_ndefHandler.gettype(0))) {
            _curMessage = new NDEFGenCtrlTranspMessage();
            _curMessage.setNDEFMessage(_ndefHandler.gettnf(0), _ndefHandler.gettype(0), _ndefHandler);
        }


    }
}
