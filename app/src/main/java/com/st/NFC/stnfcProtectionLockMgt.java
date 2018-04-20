/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1170 $
  * Revision of last commit    :  $Rev: 1170 $
  * Date of last commit     :  $Date: 2015-09-23 16:35:26 +0200 (Wed, 23 Sep 2015) $ 
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
package com.st.NFC;

public abstract class stnfcProtectionLockMgt {

    protected static final int  PASSWORDLENGTH = 16;
    protected stnfcProtectionLockStates _mlastProtectionState;


    public void setlastProtectionstate(stnfcProtectionLockStates _mlastProtectionState) {
        this._mlastProtectionState = _mlastProtectionState;
    }

    public void stnfcProtectionLockMgt () {
        _mlastProtectionState = stnfcProtectionLockStates.NDEF_LOCK_NO;
    }

    public stnfcProtectionLockStates getlastProtectionstate() {
        return _mlastProtectionState;
    }

    public abstract boolean isNDEFReadUnLocked();


    public abstract boolean  isNDEFWriteUnLocked();


    public abstract boolean  isNDEFReadUnLocked(byte [] password);


    public abstract boolean isNDEFWriteunlock(byte [] password);


    // Change Password for the Read mode
    public abstract boolean m24srchgRefReadDatacmd(byte[] password);

    // Change Password for the Wite mode
    public abstract boolean m24srchgRefWriteDatacmd(byte[] password);



    public abstract boolean m24srenableReadVerifReqCmd();
    // Enable write Password
    public abstract boolean m24srenableWriteVerifReqCmd();



    // Enable Read Password
    public abstract boolean  m24sdisableReadVerifReqCmd();

    // Enable write Password
    public abstract boolean  m24sdisableWriteVerifReqCmd();


}
