/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1504 $
  * Revision of last commit    :  $Rev: 1504 $
  * Date of last commit     :  $Date: 2016-01-04 13:59:27 +0100 (Mon, 04 Jan 2016) $ 
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

package com.st.nfc4;


// Following ISO/IEC 7816-4:2005(E)

public class Iso7816_4ErrLabel {

    public Iso7816_4Err _err;
    public String       _mean;

    Iso7816_4ErrLabel(byte SW1, byte SW2, String meaning)
    {
        _err = new Iso7816_4Err(SW1,SW2);
        _mean = meaning;
    }

    static final Iso7816_4ErrLabel [] _Iso7816_4ErrMeans = {
            new Iso7816_4ErrLabel((byte)0x90,(byte)0x00,"Command Completed"),
            new Iso7816_4ErrLabel((byte)0x62,(byte)0x80,"File overflow (Le error)"),
            new Iso7816_4ErrLabel((byte)0x62,(byte)0x82,"End of file or record reached before reading Le bytes"),
            new Iso7816_4ErrLabel((byte)0x63,(byte)0x00,"A password is required"),
            new Iso7816_4ErrLabel((byte)0x63,(byte)0xCF,"The password transmitted is incorrect"),
            new Iso7816_4ErrLabel((byte)0x65,(byte)0x81,"Unsuccessful updating"),
            new Iso7816_4ErrLabel((byte)0x67,(byte)0x00,"Wrong length"),
            new Iso7816_4ErrLabel((byte)0x69,(byte)0x85,"Condition of use not satisfied - (e.g. no NDEF file Was selected"),
            new Iso7816_4ErrLabel((byte)0x69,(byte)0x81,"Command Incompatible with file structure"),
            new Iso7816_4ErrLabel((byte)0x69,(byte)0x82,"Security status not satisfied"),
            new Iso7816_4ErrLabel((byte)0x69,(byte)0x84,"Reference data not usable"),
            new Iso7816_4ErrLabel((byte)0x6A,(byte)0x80,"Incorrect Parameter in cmd data field"),
            new Iso7816_4ErrLabel((byte)0x6A,(byte)0x82,"File or Application Not found"),
            new Iso7816_4ErrLabel((byte)0x6A,(byte)0x86,"Incorrect Parameter P1-P2"),
            new Iso7816_4ErrLabel((byte)0x6E,(byte)0x00,"Class not supported"),
            new Iso7816_4ErrLabel((byte)0x6D,(byte)0x00,"INS field not supported"),
            new Iso7816_4ErrLabel((byte)0x00,(byte)0x01,"Tag Unreacheable"), // Android Specific
            new Iso7816_4ErrLabel((byte)0x00,(byte)0x00,"unsupported Error")
            };


}
