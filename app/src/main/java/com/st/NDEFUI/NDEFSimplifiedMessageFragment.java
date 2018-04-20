/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1719 $
  * Revision of last commit    :  $Rev: 1719 $
  * Date of last commit     :  $Date: 2016-03-04 15:42:31 +0100 (Fri, 04 Mar 2016) $ 
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


package com.st.NDEFUI;

import com.st.NDEF.NDEFSimplifiedMessage;

import android.content.Intent;
import android.support.v4.app.Fragment;


public abstract class NDEFSimplifiedMessageFragment extends Fragment {
    /**
     * Defines
     */
    protected boolean _readOnly = true;
    protected boolean _storeRcs = false;

    /**
     * Attributes
     */

    public boolean is_storeRcs() {
        return _storeRcs;
    }
    public void set_storeRcs(boolean _storeRcs) {
        this._storeRcs = _storeRcs;
    }
    /**
     * Methods
     */
    protected final void setReadOnly(boolean readOnly) { _readOnly = readOnly; }
    public final boolean isReadOnly() { return _readOnly; }
    // Function for "Read": msg received from a tag
    public abstract void onMessageChanged(NDEFSimplifiedMessage ndefMsg);
    // Function for "Write": msg to send to a tag
    public abstract NDEFSimplifiedMessage getNDEFSimplifiedMessage();

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
