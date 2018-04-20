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
package com.st.nfc4;

public interface Iso7816_4APDU {

 static final int P1_INDEX = 2; // Position of P1 field in 7816-4
    // Request Command
 static final int P2_INDEX = 3; // Position of P2 field in 7816-4
    // Request Command
 static final int LC_INDEX = 4; // Position of P2 field in 7816-4
    // Request Command
 static final int DATA1_INDEX = 5; // Position of P2 field in 7816-4
        // Request Comma
 static final int DATA2_INDEX = 6; // Position of P2 field in 7816-4
        // Request Comma

}
