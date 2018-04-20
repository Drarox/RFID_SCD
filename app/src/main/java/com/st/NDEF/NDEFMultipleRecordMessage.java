
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

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;

import com.st.NDEFUI.NDEFSimplifiedMessageFragment;


public class NDEFMultipleRecordMessage extends NDEFSimplifiedMessage {

    /**
     * Defines
     */

    /**
     * Attributes
     */
    private stnfcndefhandler _mndefHandler;
    //private NDEFGenCtrlTranspMessage _ndefMsg = null;
    /**
     * Methods
     */
    // Constructors
    public NDEFMultipleRecordMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MULTIPLE_RECORD);
        // Default values
        _mndefHandler = null;

    }

    public NDEFMultipleRecordMessage(stnfcndefhandler ndefHandler) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MULTIPLE_RECORD);
        _mndefHandler = ndefHandler;
    }



    // Accessors

    public void setNdefHandler(stnfcndefhandler ndefHandler)
    {
        _mndefHandler = ndefHandler;
    }

    public stnfcndefhandler getNdefHandler()
    {
        return _mndefHandler;
    }

    public  boolean isSimplifiedMessage() {
        if (_mndefHandler == null)
            return false;
        else
            return (_mndefHandler.getRecordNb()>1);
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (ndefHandler.getRecordNb()>1) {
            _mndefHandler = ndefHandler;
        }
        else
        {
            _mndefHandler = null;
        }
    }


    public void setNDEFMessage(tnf mTnf, byte [] rtdType, byte [] ndefpayload) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        stnfcndefhandler andefHandler = new stnfcndefhandler(ndefpayload);
        if ((andefHandler!=null)&&(andefHandler.getRecordNb()>1))
        {
            _mndefHandler = andefHandler;
        }
        else
            _mndefHandler = null;
    }

    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;
        NdefRecord record ;
        // Need to parse each records from _mndefHandler and build the NdefRecord array ..
        //
        // Now create the record with the given parameters
        if (_mndefHandler == null)
        {
            return null;
        }

        return _mndefHandler.getNdefMessage();
    }

    //following Text Record Type Definition - Text 1.0 2006-07-24
    // Update ndefHandler message with current Text
    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {
        // update ST NDEF message object
        _mndefHandler = ndefHandler;
    }

}
