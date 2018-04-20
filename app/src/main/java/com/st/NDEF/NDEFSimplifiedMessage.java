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

import android.nfc.NdefMessage;


public abstract class NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    private NDEFSimplifiedMessageType _Type;

    /**
     * Methods
     */
    protected NDEFSimplifiedMessage (NDEFSimplifiedMessageType mType) { _Type = mType; }
    public final NDEFSimplifiedMessageType getType() { return _Type; }
    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) { return false; }
    // Method for "Write to tag" operation (get data from UI or external app, and format it to NDEF message)
    public abstract NdefMessage getNDEFMessage();
    // Method to decode tag "Read from tag"
    public abstract void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler);

    public abstract void updateSTNDEFMessage(stnfcndefhandler ndefHandler);
}
