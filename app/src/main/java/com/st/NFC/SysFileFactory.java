/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1207 $
  * Revision of last commit    :  $Rev: 1207 $
  * Date of last commit     :  $Date: 2015-10-02 17:29:12 +0200 (Fri, 02 Oct 2015) $ 
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

import com.st.nfcv.SysFileLRHandler;

public class SysFileFactory {

    public SysFileFactory() {
        // TODO Auto-generated constructor stub
    }

    public SysFileGenHandler getSysFileHandler(String tagHandlerType, byte[] sysFileBuff) {
        if (tagHandlerType == null) {
            return null;
        }
        if (tagHandlerType.equalsIgnoreCase("sysfileHandler")) {
            return new sysfileHandler(sysFileBuff);

        }
        if (tagHandlerType.equalsIgnoreCase("SysFileLRHandler")) {
            return new SysFileLRHandler(sysFileBuff);

        }

        return null;
    }

}
