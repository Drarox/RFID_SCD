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

package com.st.nfcv;

import android.util.Log;

import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.STNfcTagVHandler;

/**
 * Created on 5/12/16.
 */
public class BasicOperation {

    private String TAG = "BasicOperation";
    protected int mMaxTransceiveBufferAvailableSize;

    public byte[] MBBlockAnswer;
    byte[] ReadMultipleBlockAnswer = null;


    public byte[] getReadMultipleBlockAnswer() {
        return ReadMultipleBlockAnswer;
    }

    public byte[] getMBBlockAnswer() {
        return MBBlockAnswer;
    }


    public BasicOperation(int max_transceive_buffer) {
        // TODO Auto-generated constructor stub
        this.mMaxTransceiveBufferAvailableSize = max_transceive_buffer;
    }

    public int m24LRReadBasicOp(byte[] addressStart, byte[] numberOfBlockToRead, String mMemorySize) {
        Log.d(TAG, " m24LRReadBasicOp");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " m24LRReadBasicOp Action");
            //
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            cpt = 0;
            ReadMultipleBlockAnswer = null;
            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            if (sysHDL.isMultipleReadSupported() == false
                    || Helper.Convert2bytesHexaFormatToInt(numberOfBlockToRead) <= 1) // ex:
            // LRIS2K
            {
                while ((ReadMultipleBlockAnswer == null || ReadMultipleBlockAnswer[0] == 1) && cpt <= 10) {
                    // Used for DEBUG : Log.i("ScanRead", "Dans le several read
                    // single block le cpt est %s -----> " +
                    // String.valueOf(cpt));
                    ReadMultipleBlockAnswer = mtagHDL.getTypeVTagOperation().Send_several_ReadSingleBlockCommands_NbBlocks(
                            currentTag.getTag(), addressStart, numberOfBlockToRead, sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested());

/*                    ReadMultipleBlockAnswer = NFCCommandV.Send_several_ReadSingleBlockCommands_NbBlocks(
                            currentTag.getTag(), addressStart, numberOfBlockToRead, sysHDL.isBasedOnTwoBytesAddress(),sysHDL.isUidRequested());*/
                    cpt++;
                }
                cpt = 0;
            } else if (Helper.Convert2bytesHexaFormatToInt(numberOfBlockToRead) < 32) {
                while ((ReadMultipleBlockAnswer == null || ReadMultipleBlockAnswer[0] == 1) && cpt <= 10) {
                    // Used for DEBUG : Log.i("ScanRead", "Dan le read MULTIPLE
                    // 1 le cpt est %s -----> " + String.valueOf(cpt));
                    ReadMultipleBlockAnswer = mtagHDL.getTypeVTagOperation().SendReadMultipleBlockCommandCustom(currentTag.getTag(),
                            addressStart, numberOfBlockToRead[1], sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested());

/*                    ReadMultipleBlockAnswer = NFCCommandV.SendReadMultipleBlockCommandCustom(currentTag.getTag(),
                            addressStart, numberOfBlockToRead[1], sysHDL.isBasedOnTwoBytesAddress(),sysHDL.isUidRequested());*/
                    cpt++;
                }
                cpt = 0;
            } else {
                while ((ReadMultipleBlockAnswer == null || ReadMultipleBlockAnswer[0] == 1) && cpt <= 10) {
                    // Used for DEBUG : Log.i("ScanRead", "Dans le read MULTIPLE
                    // 2 le cpt est %s  -----> " + String.valueOf(cpt));
                    ReadMultipleBlockAnswer = mtagHDL.getTypeVTagOperation().SendReadMultipleBlockCommandCustom2(currentTag.getTag(),
                            addressStart, numberOfBlockToRead, sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(),
                            mMemorySize);
/*                    ReadMultipleBlockAnswer = NFCCommandV.SendReadMultipleBlockCommandCustom2(currentTag.getTag(),
                            addressStart, numberOfBlockToRead, sysHDL.isBasedOnTwoBytesAddress(),sysHDL.isUidRequested(),
                            mMemorySize);*/
                    cpt++;
                }
                cpt = 0;
            }

            if (ReadMultipleBlockAnswer == null) {
                returncd = currentTag.reportActionStatus("ERROR Read Basic Op (No tag answer) ", -1);
            } else {
                returncd = currentTag.reportActionStatus("Read Basic Op Sucessfull ", 0x00);
                // finish();
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }

    public int m24LRWriteBasicOp(byte[] addressStart, byte[] DataToWrite) {
        Log.d(TAG, " m24LRWriteBasicOp");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " m24LRWriteBasicOp Action");
            //
            byte[] WriteSingleBlockAnswer = null;
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            cpt = 0;
            while ((WriteSingleBlockAnswer == null || WriteSingleBlockAnswer[0] == 1) && cpt <= 10) {
                WriteSingleBlockAnswer = mtagHDL.getTypeVTagOperation().SendWriteSingleBlockCommand(currentTag.getTag(), addressStart, DataToWrite,
                        sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(), sysHDL.getManufacturer());
/*                WriteSingleBlockAnswer = NFCCommandV.SendWriteSingleBlockCommand(currentTag.getTag(), addressStart, DataToWrite,
                        sysHDL.isBasedOnTwoBytesAddress(),sysHDL.isUidRequested(),sysHDL.getManufacturer());*/
                cpt++;
            }

            if (WriteSingleBlockAnswer == null || WriteSingleBlockAnswer[0] == -1) {
                returncd = currentTag.reportActionStatus("ERROR Write (No tag answer) ", -1);
            } else if (WriteSingleBlockAnswer[0] == 1 && WriteSingleBlockAnswer[1] == (byte) 0x0F) {
                returncd = currentTag.reportActionStatus("ERROR Write:error with no information given ", 0x0F);
            } else if (WriteSingleBlockAnswer[0] == 1 && WriteSingleBlockAnswer[1] == (byte) 0x10) {
                returncd = currentTag.reportActionStatus("ERROR Write:the specified block is not available ", 0x10);
            } else if (WriteSingleBlockAnswer[0] == 1 && WriteSingleBlockAnswer[1] == (byte) 0x12) {
                returncd = currentTag.reportActionStatus("ERROR Write:the specified block is locked and its contents cannot be changed ", 0x12);
            } else if (WriteSingleBlockAnswer[0] == 1 && WriteSingleBlockAnswer[1] == (byte) 0x13) {
                returncd = currentTag.reportActionStatus("ERROR Write:the specified block was not successfully programmed ", 0x13);
            } else if (WriteSingleBlockAnswer[0] == (byte) 0x00) {
                returncd = currentTag.reportActionStatus("Write Sucessfull ", 0x00);
            } else {
                returncd = currentTag.reportActionStatus("Write ERROR ", -1);
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }


    public int m24LRWriteMemoryBasicOp(byte[] addressStart, byte[] bufferFile, int blocksToWrite) {
        Log.d(TAG, " m24LRWriteBasicOp");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " m24LRWriteBasicOp Action");
            //
            byte[] WriteSingleBlockAnswer = null;
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            byte[] dataToWrite = new byte[4];
            int ResultWriteAnswer = 0;
            for (int iAddressStart = 0; iAddressStart < blocksToWrite; iAddressStart++) {
                addressStart = Helper.ConvertIntTo2bytesHexaFormat(iAddressStart);
                dataToWrite[0] = bufferFile[iAddressStart * 4];
                dataToWrite[1] = bufferFile[iAddressStart * 4 + 1];
                dataToWrite[2] = bufferFile[iAddressStart * 4 + 2];
                dataToWrite[3] = bufferFile[iAddressStart * 4 + 3];
                cpt = 0;
                WriteSingleBlockAnswer = null;
                while ((WriteSingleBlockAnswer == null || WriteSingleBlockAnswer[0] == 1) && cpt <= 10) {
                    WriteSingleBlockAnswer = mtagHDL.getTypeVTagOperation().SendWriteSingleBlockCommand(currentTag.getTag(), addressStart, dataToWrite,
                            sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(), sysHDL.getManufacturer());
                    cpt++;
                }
                if (WriteSingleBlockAnswer[0] != (byte) 0x00) {
                    ResultWriteAnswer = ResultWriteAnswer + 1;
                    WriteSingleBlockAnswer[0] = (byte) 0xE1;
                    //return null;
                    break;
                }
            }
            if (ResultWriteAnswer > 0)
                WriteSingleBlockAnswer[0] = (byte) 0xFF;
            else
                WriteSingleBlockAnswer[0] = (byte) 0x00;


            if (WriteSingleBlockAnswer == null || WriteSingleBlockAnswer[0] == -1) {
                returncd = currentTag.reportActionStatus("ERROR Write (No tag answer) ", -1);
            } else if (WriteSingleBlockAnswer[0] == 1 && WriteSingleBlockAnswer[1] == (byte) 0x0F) {
                returncd = currentTag.reportActionStatus("ERROR Write:error with no information given ", 0x0F);
            } else if (WriteSingleBlockAnswer[0] == 1 && WriteSingleBlockAnswer[1] == (byte) 0x10) {
                returncd = currentTag.reportActionStatus("ERROR Write:the specified block is not available ", 0x10);
            } else if (WriteSingleBlockAnswer[0] == 1 && WriteSingleBlockAnswer[1] == (byte) 0x12) {
                returncd = currentTag.reportActionStatus("ERROR Write:the specified block is locked and its contents cannot be changed ", 0x12);
            } else if (WriteSingleBlockAnswer[0] == 1 && WriteSingleBlockAnswer[1] == (byte) 0x13) {
                returncd = currentTag.reportActionStatus("ERROR Write:the specified block was not successfully programmed ", 0x13);
            } else if (WriteSingleBlockAnswer[0] == (byte) 0x00) {
                returncd = currentTag.reportActionStatus("Write Sucessfull ", 0x00);
            } else {
                returncd = currentTag.reportActionStatus("Write ERROR ", -1);
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }

    public int m24LRWriteNDEFBasicOp(byte[] addressStart, byte[] CCbuffer, byte[] TLBuffer, byte[] PayloadBuffer, byte[] TerminatorBuffer) {
        Log.d(TAG, " m24LRWriteNDEFBasicOp");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " m24LRWriteNDEFBasicOp Action");
            //
            byte[] WriteSingleBlockAnswer = null;
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            byte[] dataToWrite = new byte[4];
            int ResultWriteAnswer = 0;
            byte[] WriteStatus;

            if (PayloadBuffer != null) {

                //1st : store TLF 4 bytes begining + write 0x00 to length byte(s)
                int lenTLV = TLBuffer.length;
                int offsetregardingTL = 0;

                if (lenTLV <= 2) {
                    // Lenth on 1 byte + T = 2 bytes
                    offsetregardingTL = 2;

                } else {
                    // length on 3 byte + T = 4 bytes
                    offsetregardingTL = 0;
                }
                byte[] TLV2write = new byte[4];
                byte[] TLV2write_zeroLength = new byte[4];
                for (int i = 0; i < lenTLV; i++)
                    TLV2write[i] = TLBuffer[i];
                if (TLV2write[1] == -1) {
                    TLV2write_zeroLength[0] = TLV2write[0];
                    TLV2write_zeroLength[1] = (byte) 0xFF;
                    TLV2write_zeroLength[2] = 0x00;
                    TLV2write_zeroLength[3] = 0x00;
                } else {
                    // For writing reason: write a block 4 bytes, we get 4 bytes
                    // for TL but start writing Data
                    TLV2write_zeroLength[0] = TLV2write[0];
                    TLV2write_zeroLength[1] = 0x00;
                    // For the Zero length
                    TLV2write_zeroLength[2] = PayloadBuffer[0];
                    TLV2write_zeroLength[3] = PayloadBuffer[1];
                    // for the real end writing regarding operation
                    TLV2write[2] = PayloadBuffer[0];
                    TLV2write[3] = PayloadBuffer[1];
                }
                cpt = 0;
                int startBlockAdress = Helper.Convert2bytesHexaFormatToInt(addressStart);
                int writeBlockAdress = startBlockAdress;
                WriteStatus = null;
                while ((WriteStatus == null || WriteStatus[0] == 1) && cpt < 10) {
                    //Used for DEBUG : Log.i("NDEFWrite", "Dan le WRITE MULTIPLE le cpt est %s -----> " + String.valueOf(cpt));
                    //WriteStatus = NFCCommand.SendWriteMultipleBlockCommand(ma.getCurrentTag(), new byte[]{0x00,0x01}, TLV2write_zeroLength, dataDevice);
                    if (CCbuffer.length <= 4) {
                        writeBlockAdress = startBlockAdress + 1;
                        byte[] sAdr = Helper.ConvertIntTo2bytesHexaFormatBis(writeBlockAdress);
                        // new byte[]{0x00, 0x01}
                        WriteStatus = mtagHDL.getTypeVTagOperation().SendWriteMultipleBlockCommand(currentTag.getTag(), sAdr,
                                TLV2write_zeroLength,
                                sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(), sysHDL.getManufacturer());
                    } else { // CC file on 8 bytes ==> 2 blocks
                        writeBlockAdress = startBlockAdress + 2;
                        byte[] sAdr = Helper.ConvertIntTo2bytesHexaFormatBis(writeBlockAdress);
                        //new byte[]{0x00, 0x02}
                        WriteStatus = mtagHDL.getTypeVTagOperation().SendWriteMultipleBlockCommand(currentTag.getTag(),sAdr ,
                                TLV2write_zeroLength,
                                sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(), sysHDL.getManufacturer());

                    }
                    cpt++;
                }

                if (WriteStatus[0] == 0x00) {
                    //2nd Write CCfield if no write error
                    byte[] CCfield2write = new byte[CCbuffer.length];
                    for (int i = 0; i < CCbuffer.length; i++)
                        CCfield2write[i] = CCbuffer[i];
                    cpt = 0;
                    WriteStatus = null;
                    while ((WriteStatus == null || WriteStatus[0] == 1) && cpt < 10) {
                        //Used for DEBUG : Log.i("NDEFWrite", "Dan le WRITE MULTIPLE le cpt est %s -----> " + String.valueOf(cpt));
                        //WriteStatus = NFCCommand.SendWriteMultipleBlockCommand(ma.getCurrentTag(), new byte[]{0x00,0x00}, CCfield2write, dataDevice);
                        writeBlockAdress = startBlockAdress;
                        byte[] sAdr = Helper.ConvertIntTo2bytesHexaFormatBis(writeBlockAdress);
                        // new byte[]{0x00, 0x00}
                        WriteStatus = mtagHDL.getTypeVTagOperation().SendWriteMultipleBlockCommand(currentTag.getTag(), sAdr,
                                CCfield2write,
                                sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(), sysHDL.getManufacturer());
                        cpt++;
                    }

                    if (WriteStatus[0] == 0x00) {
                        //3rd write rest of the NDEF message if no previous errors
                        byte[] restNDEFmsg2write = new byte[PayloadBuffer.length - offsetregardingTL + TerminatorBuffer.length];
                        for (int i = 0; i < (PayloadBuffer.length - offsetregardingTL); i++)
                            restNDEFmsg2write[i] = PayloadBuffer[i + offsetregardingTL];
                        for (int i = 0; i < TerminatorBuffer.length; i++)
                            restNDEFmsg2write[PayloadBuffer.length - offsetregardingTL + i] = TerminatorBuffer[i];

                        cpt = 0;
                        WriteStatus = null;
                        while ((WriteStatus == null || WriteStatus[0] == 1) && cpt < 10) {
                            //Used for DEBUG : Log.i("NDEFWrite", "Dan le WRITE MULTIPLE le cpt est %s -----> " + String.valueOf(cpt));
                            //WriteStatus = NFCCommand.SendWriteMultipleBlockCommand(ma.getCurrentTag(), new byte[]{0x00,0x02}, restNDEFmsg2write, dataDevice);
                            if (CCbuffer.length <= 4) {
                                writeBlockAdress = startBlockAdress + 2;
                                byte[] sAdr = Helper.ConvertIntTo2bytesHexaFormatBis(writeBlockAdress);
                                // new byte[]{0x00, 0x02}
                                WriteStatus = mtagHDL.getTypeVTagOperation().SendWriteMultipleBlockCommand(currentTag.getTag(), sAdr,
                                        restNDEFmsg2write,
                                        sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(), sysHDL.getManufacturer());
                            }else {
                                writeBlockAdress = startBlockAdress + 3;
                                byte[] sAdr = Helper.ConvertIntTo2bytesHexaFormatBis(writeBlockAdress);
                                //new byte[]{0x00, 0x03}
                                WriteStatus = mtagHDL.getTypeVTagOperation().SendWriteMultipleBlockCommand(currentTag.getTag(), sAdr,
                                        restNDEFmsg2write,
                                        sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(), sysHDL.getManufacturer());
                            }
                            cpt++;
                        }
                        if (WriteStatus[0] == 0x00) {
                            //4rth write store TLF 4 bytes begining with length byte(s)
                            cpt = 0;
                            WriteStatus = null;
                            while ((WriteStatus == null || WriteStatus[0] == 1) && cpt < 10) {
                                //Used for DEBUG : Log.i("NDEFWrite", "Dan le WRITE MULTIPLE le cpt est %s -----> " + String.valueOf(cpt));
                                //WriteStatus = NFCCommand.SendWriteMultipleBlockCommand(ma.getCurrentTag(), new byte[]{0x00,0x01}, TLV2write, dataDevice);
                                if (CCbuffer.length <= 4) {
                                    writeBlockAdress = startBlockAdress + 1;
                                    byte[] sAdr = Helper.ConvertIntTo2bytesHexaFormatBis(writeBlockAdress);
                                    // new byte[]{0x00, 0x01}
                                    WriteStatus = mtagHDL.getTypeVTagOperation().SendWriteMultipleBlockCommand(currentTag.getTag(),sAdr ,
                                            TLV2write,
                                            sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(), sysHDL.getManufacturer());
                                } else {
                                    writeBlockAdress = startBlockAdress + 2;
                                    byte[] sAdr = Helper.ConvertIntTo2bytesHexaFormatBis(writeBlockAdress);
                                    //new byte[]{0x00, 0x02}
                                    WriteStatus = mtagHDL.getTypeVTagOperation().SendWriteMultipleBlockCommand(currentTag.getTag(), sAdr,
                                            TLV2write,
                                            sysHDL.isBasedOnTwoBytesAddress(), sysHDL.isUidRequested(), sysHDL.getManufacturer());
                                }
                                cpt++;
                            }
                        }
                    }
                }
            } else {
                WriteStatus = new byte[]{(byte) 0x02};//error code for message too long for memory
            }


            if (WriteStatus == null || WriteStatus[0] == -1) {
                returncd = currentTag.reportActionStatus("ERROR Write (No tag answer) ", -1);
            } else if (WriteStatus[0] == 1 && WriteStatus[1] == (byte) 0x0F) {
                returncd = currentTag.reportActionStatus("ERROR Write:error with no information given ", 0x0F);
            } else if (WriteStatus[0] == 1 && WriteStatus[1] == (byte) 0x10) {
                returncd = currentTag.reportActionStatus("ERROR Write:the specified block is not available ", 0x10);
            } else if (WriteStatus[0] == 1 && WriteStatus[1] == (byte) 0x12) {
                returncd = currentTag.reportActionStatus("ERROR Write:the specified block is locked and its contents cannot be changed ", 0x12);
            } else if (WriteStatus[0] == 1 && WriteStatus[1] == (byte) 0x13) {
                returncd = currentTag.reportActionStatus("ERROR Write:the specified block was not successfully programmed ", 0x13);
            } else if (WriteStatus[0] == (byte) 0x00) {
                returncd = currentTag.reportActionStatus("Write Sucessfull ", 0x00);
            } else {
                returncd = currentTag.reportActionStatus("Write ERROR ", -1);
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }


    public int readMaxTransceiveCmd() {
        Log.d(TAG, " readMaxTransceiveCmd");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;
        returncd = currentTag.reportActionStatusTransparent("readMaxTransceiveCmd ERROR read (No tag answer) ", -1);
        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " readMaxTransceiveCmd Action");
            //
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());
            returncd = mtagHDL.getTypeVTagOperation().readMaxTransceiveCmd(currentTag.getTag());

        }else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;

    }


    public int writeRegister(stnfcRegisterHandler.ST25DVRegisterTable target, byte value, boolean staticReg) {
        Log.d(TAG, " writeRegister");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;
        returncd = currentTag.reportActionStatusTransparent("writeRegister ERROR write (No tag answer) ", -1);
        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        if (ret) {
            Log.d(TAG, " writeRegister Action");
            //
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            NFCCommandVExtended LRcmdExtended = new NFCCommandVExtended(currentTag.getModel());
            MBBlockAnswer = null;
            cpt = 0;
            while ((MBBlockAnswer == null || MBBlockAnswer[0] == 1) && cpt <= 10) {
                MBBlockAnswer = LRcmdExtended.writeSystemRegister(currentTag.getTag(), sysHDL, target, value, staticReg);
                cpt++;
            }

            if (MBBlockAnswer == null || MBBlockAnswer[0] == -1) {
                returncd = currentTag.reportActionStatusTransparent("writeRegister ERROR write (No tag answer) ", -1);
            } else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x01) {
                returncd = currentTag.reportActionStatusTransparent("Command is not supported. ", 0x01);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x02) {
                returncd = currentTag.reportActionStatusTransparent("Command is not recognized (format error). ", 0x02);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x03) {
                returncd = currentTag.reportActionStatusTransparent("writeRegister ERROR read:Invalid command ", 0x03);
            } else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x0F) {
                returncd = currentTag.reportActionStatusTransparent("Error with no information given. ", 0x0F);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x10) {
                returncd = currentTag.reportActionStatusTransparent("The specified block is not available. ", 0x10);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x11) {
                returncd = currentTag.reportActionStatusTransparent("The specified block is already locked and thus cannot be locked again. ", 0x11);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x12) {
                returncd = currentTag.reportActionStatusTransparent("The specified block is locked and its contents cannot be changed. ", 0x12);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x13) {
                returncd = currentTag.reportActionStatusTransparent("The specified block was not successfully programmed. ", 0x13);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x14) {
                returncd = currentTag.reportActionStatusTransparent("The specified block was not successfully locked. ", 0x14);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x15) {
                returncd = currentTag.reportActionStatusTransparent("The specified block is protected. ", 0x15);
            }else if ((MBBlockAnswer[0] & 0x01) == 0) {
                returncd = currentTag.reportActionStatusTransparent("writeRegister command succeeded", 0);
            } else {
                returncd = currentTag.reportActionStatusTransparent("writeRegister Error ... ", -1);
            }


            //
        } else {
            returncd = currentTag.reportActionStatusTransparent("writeRegister Tag not on the field...", -1);
        }
        return returncd;
    }

    public int readRegister(stnfcRegisterHandler.ST25DVRegisterTable target, boolean staticReg) {
        //Log.d(TAG, " readRegister");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;
        returncd = currentTag.reportActionStatusTransparent("readRegister ERROR read (No tag answer) ", -1);
        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            //Log.d(TAG, " readRegister Action");
            //
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            NFCCommandVExtended LRcmdExtended = new NFCCommandVExtended(currentTag.getModel());
            MBBlockAnswer = null;
            cpt = 0;
            while ((MBBlockAnswer == null || MBBlockAnswer[0] == 1) && cpt <= 10) {
                MBBlockAnswer = LRcmdExtended.readSystemRegister(currentTag.getTag(), sysHDL, target, staticReg);
                cpt++;
            }

            if (MBBlockAnswer == null || MBBlockAnswer[0] == -1) {
                returncd = currentTag.reportActionStatusTransparent("readRegister ERROR read (No tag answer) ", -1);
            } else if ((MBBlockAnswer[0] & 0x01) == 0) {
                returncd = currentTag.reportActionStatusTransparent("readRegister answered ok ", 0);
            } else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x01) {
                returncd = currentTag.reportActionStatusTransparent("Command is not supported. ", 0x01);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x02) {
                returncd = currentTag.reportActionStatusTransparent("Command is not recognized (format error). ", 0x02);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x03) {
                returncd = currentTag.reportActionStatusTransparent("readRegister ERROR read:Invalid command ", 0x03);
            } else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x0F) {
                returncd = currentTag.reportActionStatusTransparent("Error with no information given. ", 0x0F);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x10) {
                returncd = currentTag.reportActionStatusTransparent("The specified block is not available. ", 0x10);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x11) {
                returncd = currentTag.reportActionStatusTransparent("The specified block is already locked and thus cannot be locked again. ", 0x11);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x12) {
                returncd = currentTag.reportActionStatusTransparent("The specified block is locked and its contents cannot be changed. ", 0x12);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x13) {
                returncd = currentTag.reportActionStatusTransparent("The specified block was not successfully programmed. ", 0x13);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x14) {
                returncd = currentTag.reportActionStatusTransparent("The specified block was not successfully locked. ", 0x14);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x15) {
                returncd = currentTag.reportActionStatusTransparent("The specified block is protected. ", 0x15);
            } else {
                returncd = currentTag.reportActionStatusTransparent("readRegister Error ... ", -1);
            }
            //
        } else {
            returncd = currentTag.reportActionStatusTransparent("Tag not on the field...", -1);
        }
        return returncd;
    }
}
