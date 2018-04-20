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

import com.st.util.GenErrorAppReport;

public interface stnfcTagGenHandler {

    //void decode();
    public void closeConnection();

    //public int requestATS();

    public int requestCCSelect();


    public int requestSysSelect();


    public int requestCCReadLength();

    public int requestSysReadLength();


    public int requestCCRead(int size, byte [] buffer);

    public int requestSysRead(int size, byte [] buffer);


    public int readNdeflength() ;
    public int readNdefBinary(byte [] ndefbuffer);

    public GenErrorAppReport FormatNDEF(NFCTag currentTag, int nbNdefFiles);
    public GenErrorAppReport NDEFLockWrite(NFCTag currentTag, byte[] _password128bitslong, byte[] DEFAULT_PASSWORD);
    public GenErrorAppReport NDEFUnLockWrite(NFCTag currentTag, byte[] _password128bitslong, byte[] DEFAULT_PASSWORD);
    public GenErrorAppReport NDEFLockRead(NFCTag currentTag, byte[] _password128bitslong, byte[] _modificationpassword128bitslong , byte[] DEFAULT_PASSWORD);
    public GenErrorAppReport NDEFUnLockRead(NFCTag currentTag,  byte[] _modificationpassword128bitslong , byte[] DEFAULT_PASSWORD);
    public GenErrorAppReport ToggleGPO(NFCTag currentTag, boolean HZState);
    public GenErrorAppReport EraseNDEF(NFCTag currentTag);
    public GenErrorAppReport SetupCounter(NFCTag currentTag,int setupCounter );
    public GenErrorAppReport SelectCommand();

    //public boolean m24srupdateBinary(byte [] binary);
    public boolean updateBinary(byte [] binary);
    //public boolean m24srupdateBinarywithPassword(byte [] binary, byte [] password);
    public boolean updateBinarywithPassword(byte [] binary, byte [] password);

//    public int selectNdef(int NdefFileID);
    public GenErrorAppReport selectNdef(int NdefFileID);
    // Protection and lock management

    public boolean isNDEFWriteUnLocked();
    public boolean isNDEFReadUnLocked();
    public boolean isNDEFReadUnLocked(byte [] password);






}
