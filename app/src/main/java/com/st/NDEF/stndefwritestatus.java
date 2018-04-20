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

public enum stndefwritestatus  {
    WRITE_STATUS_OK,
    WRITE_STATUS_ONGOING,
    WRITE_STATUS_ERR_TAG_LOST,
    WRITE_STATUS_ERR_IO,
    WRITE_STATUS_ERR_PASSWORD_REQUIRED,
    WRITE_STATUS_ERR_WRONG_PASSWORD,
    WRITE_STATUS_ERR_MALFORMED_STRUCTURE,
    WRITE_STATUS_ERR_READ_ONLY_TAG,
    WRITE_STATUS_ERR_LOCKED_TAG_NOT_SUPPORTED,
    WRITE_STATUS_ERR_NOT_SUPPORTED
}
//- APT 06/09/2013