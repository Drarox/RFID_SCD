/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1267 $
  * Revision of last commit    :  $Rev: 1267 $
  * Date of last commit     :  $Date: 2015-10-26 15:38:27 +0100 (Mon, 26 Oct 2015) $ 
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

public interface Type4TagIso7816Commands {
    /*
     * private static final byte[] selectAppliFrame = new byte[] { (byte) 0x00,
     * (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x10, (byte) 0xF0, (byte)
     * 0x02, (byte) 0x42, (byte) 0x4D, (byte) 0x50, (byte) 0x5F, (byte) 0x58,
     * (byte) 0x58, (byte) 0x4F, (byte) 0x5F, (byte) 0x76, (byte) 0x30, (byte)
     * 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
     */
    public static final byte[] NdefSelectAppliFrame = new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00,
            (byte) 0x07, (byte) 0xD2, (byte) 0x76, (byte) 0x00, (byte) 0x00, (byte) 0x85, (byte) 0x01, (byte) 0x01 };
    public static final byte[] CCSelect = new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x0C, (byte) 0x02,
            (byte) 0xE1, (byte) 0x03 };
    public static final byte[] CCReadLength = new byte[] { (byte) 0x00, (byte) 0xB0, (byte) 0x00, (byte) 0x00,
            (byte) 0x02 };

    public static final byte[] SYSSelect = new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x0C,
            (byte) 0x02, (byte) 0xE1, (byte) 0x01 };

    public static byte[] SYSUpdatNbNdefFiles = new byte[] { (byte) 0x00, (byte) 0xD6, // CAS
                                                                                    // ,
                                                                                    // INS
            (byte) 0x00, (byte) 0x07, // P1 , P2 - Offset in the SelectedFile
                                        // (here 0)
            (byte) 0x01, // LC - Number of byte of data
            (byte) 0x00 // DATA_1 : SIZE MSB, DATA_2 : SIZE LSB
    };

    public static byte[] SYSUpdatConfigCounter = new byte[] { (byte) 0x00, (byte) 0xD6, // CAS
                                                                                        // ,
                                                                                        // INS
            (byte) 0x00, (byte) 0x03, // P1 , P2 - Offset in the SelectedFile
                                        // (here 0)
            (byte) 0x01, // LC - Number of byte of data
            (byte) 0x00 // DATA_1 : SIZE MSB, DATA_2 : SIZE LSB
    };

    public static final byte[] SYSReadLength = new byte[] { (byte) 0x00, (byte) 0xB0, (byte) 0x00, (byte) 0x00,
            (byte) 0x02 };

    public static final byte[] readBinary = new byte[] { (byte) 0x00, (byte) 0xB0, (byte) 0x00, (byte) 0x00,
            (byte) 0x00 };

    public static final byte[] ndefSelectcmd = new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x0C,
            (byte) 0x02, (byte) 0x00, (byte) 0x00 // Ndef File ID to select
    };
    public static final byte[] ndefreadlengthcmd = new byte[] { (byte) 0x00, (byte) 0xB0, (byte) 0x00, (byte) 0x00,
            (byte) 0x02 };

}
