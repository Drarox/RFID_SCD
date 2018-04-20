/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1708 $
  * Revision of last commit    :  $Rev: 1708 $
  * Date of last commit     :  $Date: 2016-02-28 17:44:48 +0100 (Sun, 28 Feb 2016) $ 
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

public enum NDEFSimplifiedMessageType {
    NDEF_SIMPLE_MSG_TYPE_EMPTY, // Must always be present at the beginning of the list
    NDEF_SIMPLE_MSG_TYPE_TEXT,
    NDEF_SIMPLE_MSG_TYPE_VCARD,
    NDEF_SIMPLE_MSG_TYPE_URI,
    NDEF_SIMPLE_MSG_TYPE_BTHANDOVER,
    NDEF_SIMPLE_MSG_TYPE_BTLE,
    NDEF_SIMPLE_MSG_TYPE_WIFIHANDOVER,
    NDEF_SIMPLE_MSG_TYPE_EXT_M24SRDISCOCTRL,
    NDEF_SIMPLE_MSG_TYPE_EXT_GENCTRL,
    NDEF_SIMPLE_MSG_TYPE_EXT_TRANSP_GENCTRL,
    NDEF_SIMPLE_MSG_TYPE_MAIL,
    NDEF_SIMPLE_MSG_TYPE_SMS,
    NDEF_SIMPLE_MSG_TYPE_AAR,
    NDEF_SIMPLE_MSG_TYPE_SP,
    NDEF_SIMPLE_MULTIPLE_RECORD,// Smart poster

    //NDEF_SIMPLE_MSG_TYPE_SMART_POSTER,
    //NDEF_SIMPLE_MSG_TYPE_TEL_NB,
    //NDEF_SIMPLE_MSG_TYPE_SMS,
    //NDEF_SIMPLE_MSG_TYPE_MAIL,

    // MIME ?
    //NDEF_SIMPLE_MSG_TYPE_WIFI_PAIR,
    //NDEF_SIMPLE_MSG_TYPE_PROPRIETARY,
    // Geo Loc ?
    // AAR ?
}


