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


import java.util.Arrays;




import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;



public class NDEFGenCtrlMessage extends NDEFSimplifiedMessage {

    /**
     * Defines
     */

    final static String TAG = "NDEFGenCtrlMessage";


    /**
     * Attributes
     */
    byte[] _myarrayofBytes = null;
    int _mynbbytes = 10;


    /**
     * Methods
     */
    // Constructors
    public NDEFGenCtrlMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_EXT_GENCTRL);
        // Default values
        _myarrayofBytes = new byte[_mynbbytes];
    }



    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {
        return ( (mTnf == tnf.external) && (Arrays.equals(rtdType,"st.com:ctrl".getBytes())));
    }

    public void parseAndSetNDEFMessage(byte[] payload) {
        if ((payload != null) || (payload.length == 0)) {
            if (payload.length == _myarrayofBytes.length)
                System.arraycopy(payload, 0, _myarrayofBytes, 0, payload.length);
            else {
                _myarrayofBytes = new byte[payload.length];
                System.arraycopy(payload, 0, _myarrayofBytes, 0, payload.length);
            }
        } else {
            Log.d(TAG, "Can't set NDEF message from payload");
        }
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {
            byte [] payload = ndefHandler.getpayload(0);

            if (payload.length == _myarrayofBytes.length)
                System.arraycopy(payload, 0, _myarrayofBytes, 0, payload.length);
            else {
                _myarrayofBytes = new byte[payload.length];
                System.arraycopy(payload, 0, _myarrayofBytes, 0, payload.length);
            }


        }
        else
        {
            Log.d(TAG,"Can't set NDEF message from ST ndefHandler object");
        }
    }

    public byte[] serializeNDEFMessage()
    {
        byte [] payload = new byte[_myarrayofBytes.length];
        for (int i=0;i<_myarrayofBytes.length;i++) payload[i] = _myarrayofBytes[i];
        return payload;
    }



    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;
        byte [] payload = this.serializeNDEFMessage();

        // Check if there is a valid text
        if (payload == null) {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input M24Discovery Ctrl Message");
        } else {
            NdefRecord extRecord =  NdefRecord.createExternal("candy.com","ctrl",payload);

            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {extRecord};
            msg = new NdefMessage(records);
        }
        return msg;
    }

    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage(String NDEFTextType) {
        NdefMessage msg = null;
        byte [] payload = this.serializeNDEFMessage();

        // Check if there is a valid text
        if (payload == null) {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input Candy  Ctrl Message");
        } else {
            NdefRecord extRecord =  NdefRecord.createExternal("candy.com",NDEFTextType,payload);
            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {extRecord};
            msg = new NdefMessage(records);
        }
        return msg;
    }

    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {
        // update ST NDEF message object
        ndefHandler.setNdefProprietaryGenCtrlMsg(this.serializeNDEFMessage());

    }

}
