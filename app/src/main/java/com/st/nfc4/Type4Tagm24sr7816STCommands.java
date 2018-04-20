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

public interface Type4Tagm24sr7816STCommands {

     // Update Binary Command
      static final byte[] m24srNCFForumupdateBinary = new byte [] {
                                                                (byte) 0x00,(byte) 0xD6, // CAS , INS
                                                                (byte) 0x00,(byte) 0x02, // P1  , P2 - Offset in the SelectedFile (here 2)
                                                                (byte) 0x00              // LC - Number of byte of data

                                                              };

         // Update Binary Command for GPO in Sys File
      static final byte[] ST25TANCFForumupdateBinaryGPOConfig = new byte [] {
                                                                (byte) 0x00,(byte) 0xD6, // CAS , INS
                                                                (byte) 0x00,(byte) 0x02, // P1  , P2 - Offset in the SelectedFile (here 2)
                                                                (byte) 0x01 , // LC - Number of byte of data
                                                                (byte) 0x00

                                                              };

     // Update Binary Command
      static final byte[] m24srNCFForumupdateBinarySize = new byte [] {
                                                                (byte) 0x00,(byte) 0xD6, // CAS , INS
                                                                (byte) 0x00,(byte) 0x00, // P1  , P2 - Offset in the SelectedFile (here 0)
                                                                (byte) 0x02,             // LC - Number of byte of data
                                                                (byte) 0x00,(byte) 0x00, // DATA_1 : SIZE MSB, DATA_2 : SIZE LSB
                                                              };


         // Verify command - See desciption from M24SRXX data sheet.
         // Use to verify and check NDEF Password status.

          static final byte[] m24sr7816verifycmd = new byte []{
                                                                       (byte) 0x00,(byte) 0x20, // CAS , INS
                                                                       (byte) 0x00,(byte) 0x00, // P1  , P2
                                                                       (byte) 0x00               // Lc
                                                                      };
            // Change Reference Data Command
            // Use to update Read or Write password linked to the NDEF file previously selected.
              static final byte[] m24sr7816chgRefDatacmd = new byte[]{
                                                                             (byte) 0x00,(byte) 0x24, // CAS , INS
                                                                             (byte) 0x00,(byte) 0x00, // P1  , P2
                                                                             (byte) 0x10,
                                                                         };


    // Enable Verification Requirement command
    // use to activate Read or Write password protection
      static final byte[] m24sr7816enableVerifReqCmd = new byte[]{
                                                                     (byte) 0x00,(byte) 0x28, // CAS , INS
                                                                     (byte) 0x00,(byte) 0x00, // P1  , P2
                                                                 };

    // Disable Verification Requirement command
    // use to de-activate Read or Write password protection
      static final byte[] m24sr7816disableVerifReqCmd = new byte[]{
                                                                     (byte) 0x00,(byte) 0x26, // CAS , INS
                                                                     (byte) 0x00,(byte) 0x00, // P1  , P2
                                                                 };

     // ST Proprietary command set

     // ExtendedReadBinary command
     // use to read binary from Offset in NDEF File to Offset+Le
      static final byte[] m24sr7816STExtReadBinarycmd = new byte[]{
                                                                     (byte) 0xA2,(byte) 0xB0, // CAS , INS
                                                                     (byte) 0x00,(byte) 0x00, // P1  , P2
                                                                     (byte) 0x00                 // Le
                                                                 };

     // EnablePermanentState Command
     // Use to configures the NDEF file to be Read or Write Only
      static final byte[] m24sr7816STEnablePermState = new byte[]{
                                                                    (byte) 0xA2,(byte) 0x28, // CAS , INS
                                                                    (byte) 0x00,(byte) 0x00 // P1  , P2
                                                                 };

    // DisablePermanentState Command
    // Use to disable the NDEF file  Read or Write Only protection
     static final byte[] m24sr7816STDisablePermState = new byte[]{
                                                                    (byte) 0xA2,(byte) 0x26, // CAS , INS
                                                                    (byte) 0x00,(byte) 0x00 // P1  , P2
                                                                };

    // SendInterrupt command
    // On receiving the SendInterrupt command the M24SR generates a negative pulse on the GPO pins
    // /!\ System file shall be selected by issuing the System Select command before
     static final byte[] m24sr7816STSendInterrupt = new byte[]{
                                                                    (byte) 0xA2,(byte) 0xD6, // CAS , INS
                                                                    (byte) 0x00,(byte) 0x01, // P1  , P2
                                                                    (byte) 0x00
                                                                };
    // StateControl command
    // use to drive GPO pins to low or to HZ
     static final byte[] m24sr7816STStateCtrlcmd = new byte[]{
                                                                    (byte) 0xA2,(byte) 0xD6, // CAS , INS
                                                                    (byte) 0x00,(byte) 0x1F, // P1  , P2
                                                                    (byte) 0x01,(byte) 0x00  // LC, DATA_1
                                                                };


}
