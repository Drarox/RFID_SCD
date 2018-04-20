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

package com.st.nfc4;

import java.io.IOException;

import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;

import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.IsoDep;
import android.util.Log;

public class Type4TagOperationBasicOp implements Type4TagIso7816Commands {

     private String TAG = "Type4TagOperationBasicOp";

        protected Tag currentTag;
        protected IsoDep isoDepCurrentTag;

        public Iso7816_4Err _lasttranscieveAnswer;

        public Type4TagOperationBasicOp()  {
            // TODO Auto-generated constructor stub
            currentTag = null;
            isoDepCurrentTag = null;
            _lasttranscieveAnswer = new Iso7816_4Err();

        }
        public Type4TagOperationBasicOp(Tag tagToHandle)  {
            // TODO Auto-generated constructor stub
            currentTag = null;
            isoDepCurrentTag = null;
            _lasttranscieveAnswer = new Iso7816_4Err();
            currentTag = tagToHandle;

        }

    public Iso7816_4Err getError() {return _lasttranscieveAnswer;}

    // request ATS
    public int requestATS()
    {
        byte[] transcieveAnswer = new byte[] { (byte) 0x01 };
        if (isoDepCurrentTag == null)
            isoDepCurrentTag = IsoDep.get(this.currentTag);
        if (isoDepCurrentTag == null)
        {
            return 0;
        }
        int cpt = 0;

        while (( transcieveAnswer[0] == 1 || transcieveAnswer[0] == (byte)0xAA) && cpt <= 1)
        {
            try {
                if (!isoDepCurrentTag.isConnected())
                {
                    isoDepCurrentTag.close();
                    isoDepCurrentTag.connect();
                }
                    //isoDepCurrentTag.setTimeout(20);
                transcieveAnswer = isoDepCurrentTag.transceive(NdefSelectAppliFrame);

                if (transcieveAnswer[0] == (byte) 0x90 && transcieveAnswer[1] == (byte) 0x00)
                    {
                        //isoDepCurrentTag.close();
                        return 1;
                    }
                    else
                    {
                        isoDepCurrentTag.close();
                        cpt++;
                        return 0;
                    }
            } catch (TagLostException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                throw new RuntimeException("fail", e);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                throw new RuntimeException("fail", e);
            }
        }
            return 0;
    } // End of ATS request

    public byte [] transcievecmd(byte [] cmd)
    {
        byte[] answer = null;
        _lasttranscieveAnswer.reset();
        try
        {
            if (isoDepCurrentTag == null)
                isoDepCurrentTag = IsoDep.get(this.currentTag);

            if (!isoDepCurrentTag.isConnected())
            {
                isoDepCurrentTag.connect();
                isoDepCurrentTag.setTimeout(20);
            }
            answer = isoDepCurrentTag.transceive(cmd);
        }
        catch (IOException io)
        {
            Log.e(TAG," Tranceive "+cmd.toString()+" IOexception");
            return null;
        }
        catch (IllegalStateException ise)
        {
            Log.e(TAG," Tranceive "+cmd.toString()+" IllegalStateException");
            return null;
        }
//        try {
//            //isoDepCurrentTag.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        return answer;
    }

    public int getMaxtranscievecmd(int maxTransceivecmd)
    {
        /* The maximum length of a normal IsoDep frame consists of:
         * CLA, INS, P1, P2, LC, LE + 255 payload bytes = 261 bytes
         * such a frame is supported. Extended length frames however
         * are not supported.
         */
        int readermaxtranceive = maxTransceivecmd;
            if (isoDepCurrentTag == null) isoDepCurrentTag = IsoDep.get(this.currentTag);
            readermaxtranceive = isoDepCurrentTag.getMaxTransceiveLength();


        return (maxTransceivecmd > readermaxtranceive?readermaxtranceive:maxTransceivecmd) ;
    }

    public void settranscievetimeout(int tout)
    {
            if (isoDepCurrentTag == null) isoDepCurrentTag = IsoDep.get(this.currentTag);
            isoDepCurrentTag.setTimeout(tout);

    }
    public int gettranscievetimeout()
    {
        int tout = 0;
        if (isoDepCurrentTag == null) isoDepCurrentTag = IsoDep.get(this.currentTag);
        tout = isoDepCurrentTag.getTimeout();
        return tout;
    }


    public int requestCCSelect()
    {
        byte[] transcieveAnswer = new byte[] { (byte) 0x01 };
        if (isoDepCurrentTag == null)
            isoDepCurrentTag = IsoDep.get(this.currentTag);
        int cpt = 0;

        while (( transcieveAnswer[0] == 1 || transcieveAnswer[0] == (byte)0xAA) && cpt <= 1)
        {
            try {
                if (!isoDepCurrentTag.isConnected())
                {
                    isoDepCurrentTag.connect();
                    isoDepCurrentTag.setTimeout(20);
                }
                transcieveAnswer = isoDepCurrentTag.transceive(CCSelect);
                if ((transcieveAnswer[0] == (byte) 0x90 ) && ( transcieveAnswer[1] == (byte) 0x00))
                {
                    // isoDepCurrentTag.close();
                    return 1;
                }
                else
                {
                    cpt++;
                    isoDepCurrentTag.close();
                    return 0;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException("fail", e);
            }
        }
            return 0;
    } // End of requestCCSelect




    public int requestSysSelect()
    {

        byte[] transcieveAnswer = new byte[] { (byte) 0x01 };
        if (isoDepCurrentTag == null)
            isoDepCurrentTag = IsoDep.get(this.currentTag);
        int cpt = 0;

        while (( transcieveAnswer[0] == 1 || transcieveAnswer[0] == (byte)0xAA) && cpt <= 1)
        {
            try {
                if (!isoDepCurrentTag.isConnected())
                {
                    isoDepCurrentTag.connect();
                    isoDepCurrentTag.setTimeout(20);
                }
                transcieveAnswer = isoDepCurrentTag.transceive(SYSSelect);

                if (transcieveAnswer[0] == (byte) 0x90 && transcieveAnswer[1] == (byte) 0x00)
                    {
                        //isoDepCurrentTag.close();
                        return 1;
                    }
                    else
                    {
                        cpt++;
                        isoDepCurrentTag.close();
                        return 0;
                    }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("fail", e);
            }
        }
            return 0;
    } // End of requestSysSelect

    public int requestCCReadLength()
    {
        byte[] transcieveAnswer = new byte[] { (byte) 0x01 };
        if (isoDepCurrentTag == null)
            isoDepCurrentTag = IsoDep.get(this.currentTag);
        int cpt = 0;

        int CCLength = 0;

        while (( transcieveAnswer[0] == 1 || transcieveAnswer[0] == (byte)0xAA) && cpt <= 1)
        {
            try {
                if (!isoDepCurrentTag.isConnected())
                {
                    isoDepCurrentTag.connect();
                    isoDepCurrentTag.setTimeout(20);
                }
                transcieveAnswer = isoDepCurrentTag.transceive(CCReadLength);

                if (transcieveAnswer[2] == (byte) 0x90 && transcieveAnswer[3] == (byte) 0x00)
                    {

                        CCLength = (int)((transcieveAnswer[0] & 0xFF)<<8) + (int)(transcieveAnswer[1]&0xFF);
                        // isoDepCurrentTag.close();
                        return CCLength;
                    }
                    else
                    {
                        cpt++;
                        isoDepCurrentTag.close();
                        return 0;
                    }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException("fail", e);
            }
        }
            return 0;
    } // End of CCReadLength request

    public int requestSysReadLength()
    {
        byte[] transcieveAnswer = new byte[] { (byte) 0x01 };
        if (isoDepCurrentTag == null)
            isoDepCurrentTag = IsoDep.get(this.currentTag);
        int cpt = 0;

        int CCLength = 0;

        while (( transcieveAnswer[0] == 1 || transcieveAnswer[0] == (byte)0xAA) && cpt <= 1)
        {
            try {
                if (!isoDepCurrentTag.isConnected())
                {
                    isoDepCurrentTag.connect();
                    isoDepCurrentTag.setTimeout(20);
                }
                transcieveAnswer = isoDepCurrentTag.transceive(SYSReadLength);

                if (transcieveAnswer[2] == (byte) 0x90 && transcieveAnswer[3] == (byte) 0x00)
                    {

                        CCLength = (int)((transcieveAnswer[0] & 0xFF)<<8) + (int)(transcieveAnswer[1]&0xFF);
                        //isoDepCurrentTag.close();
                        return CCLength;
                    }
                    else
                    {
                        cpt++;
                        isoDepCurrentTag.close();
                        return 0;
                    }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException("fail", e);
            }
        }
            return 0;
    } // End of SYSReadLength request

    // old method. Read Size restricted to 255 byte
    private int requestSingleReadBinary(int size, byte [] buffer)
    {

        byte[] transcieveAnswer = new byte[] { (byte) 0x00 };
        // transcieveAnswer[size] = 0x01;
        //transcieveAnswer[size+1] = (byte) 0xAA;

        if (isoDepCurrentTag == null)
            isoDepCurrentTag = IsoDep.get(this.currentTag);
        int cpt = 0;

        //size = 0;

        byte[] readcmd = new byte[readBinary.length];

        System.arraycopy(readBinary, 0, readcmd, 0, readBinary.length);
        readcmd[4] = (byte) (size & 0xFF);

        //while (( transcieveAnswer[size] == 1 || transcieveAnswer[size+1] == (byte)0xAA) && cpt <= 1)
        {
            try {
                if (!isoDepCurrentTag.isConnected())
                {
                    isoDepCurrentTag.connect();
                    isoDepCurrentTag.setTimeout(20);
                }

                transcieveAnswer = isoDepCurrentTag.transceive(readcmd);

                if (transcieveAnswer[size] == (byte) 0x90 && transcieveAnswer[size+1] == (byte) 0x00)
                    {
                        System.arraycopy(transcieveAnswer, 0, buffer, 0, size);
                        //isoDepCurrentTag.close();
                        return 1;
                    }
                    else
                    {
                        cpt++;
                        isoDepCurrentTag.close();
                        return 0;
                    }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException("fail", e);
            }
        }
            //return 0;
    } // End

    /**
     * @param size
     * @param buffer
     * @return
     */
    private int requestReadBinary(int size, byte [] buffer)
    {

        byte[] transcieveAnswer = new byte[] { (byte) 0x00 };
        int readoffset = 0;

        if (isoDepCurrentTag == null)
            isoDepCurrentTag = IsoDep.get(this.currentTag);
        int cpt = 0;

        //size = 0;

        byte[] readcmd = new byte[readBinary.length];

        // need to split command.
        // max size per readbinary : 244 bytes.
        //keep offset in the file (from last read data)

        // Get the current tag and Max read cmd size defined in CC file
        int maxDataChunk = 244; // fixed value store in CC file
        NFCApplication currentApp = (NFCApplication) NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        if (currentTag != null) {
            maxDataChunk = currentTag.getmaxbytesread();
        }




        int currentoffset = 0; // start from Addr 0
        int remainDataToRead = size;
//        int maxDataChunk = 244; // fixed value store in CC file
        int dataChunktoRead = 0;

        System.arraycopy(readBinary, 0, readcmd, 0, readBinary.length);
        while (remainDataToRead >0)
        {
            dataChunktoRead = (remainDataToRead>maxDataChunk)?maxDataChunk:remainDataToRead;
            remainDataToRead = remainDataToRead-dataChunktoRead;
            readcmd[2] = (byte) ((currentoffset & 0xFF00)>>8);
            readcmd[3] = (byte) (currentoffset & 0xFF);
            readcmd[4] = (byte) (dataChunktoRead & 0xFF);
            try {
                if (!isoDepCurrentTag.isConnected())
                {
                    isoDepCurrentTag.connect();
                    isoDepCurrentTag.setTimeout(20);
                }

                transcieveAnswer = isoDepCurrentTag.transceive(readcmd);

                if (transcieveAnswer[dataChunktoRead] == (byte) 0x90 )
                    //if (transcieveAnswer[dataChunktoRead] == (byte) 0x90 && ((size!=36)?(transcieveAnswer[dataChunktoRead+1] == (byte) 0x00):true))
                // if (transcieveAnswer[dataChunktoRead] == (byte) 0x90 && (transcieveAnswer[dataChunktoRead+1] == (byte) 0x00))
                    {
                        System.arraycopy(transcieveAnswer, 0, buffer, currentoffset, dataChunktoRead);
                    }
                    else
                    {
                        isoDepCurrentTag.close();
                        return 0; // error Exit
                    }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException("fail", e);
            }

            currentoffset = currentoffset+dataChunktoRead;
        }
//        try {
//            // isoDepCurrentTag.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    return 1;
    } // End of CCReadLength request


    public int requestCCRead(int size, byte [] buffer)
    {
        return this.requestReadBinary(size,buffer);
    }

    public int requestSysRead(int size, byte [] buffer)
    {
        return this.requestReadBinary(size,buffer);
    }



    public int readNdeflength() // similar to requestCCReadLength
    {

        //ndefSelectcmd
        byte[] transcieveAnswer = new byte[] { (byte) 0x01 };
        if (isoDepCurrentTag == null)
            isoDepCurrentTag = IsoDep.get(this.currentTag);
        int cpt = 0;

        int ndefLength = 0;

        while (( transcieveAnswer[0] == 1 || transcieveAnswer[0] == (byte)0xAA) && cpt <= 1)
        {
            try {
                if (!isoDepCurrentTag.isConnected())
                {
                    isoDepCurrentTag.connect();
                    isoDepCurrentTag.setTimeout(20);
                }
                transcieveAnswer = isoDepCurrentTag.transceive(ndefreadlengthcmd);

                if (transcieveAnswer[2] == (byte) 0x90 && transcieveAnswer[3] == (byte) 0x00)
                    {

                        ndefLength = (int)((transcieveAnswer[0] & 0xFF)<<8) + (int)(transcieveAnswer[1]&0xFF);
                        //isoDepCurrentTag.close();
                        return ndefLength;
                    }
                    else
                    {
                        cpt++;
                        ndefLength = 0;
                        isoDepCurrentTag.close();
                        return 0;
                    }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException("fail", e);
            }
        }
            return 0;
    } // End Ndef Read Length

    public int readNdefBinary(byte [] ndefbuffer)
    {
        return this.requestReadBinary(ndefbuffer.length,ndefbuffer);
    }

    public void closeConnection()
    {
        if (isoDepCurrentTag.isConnected())
        {
            try {
                isoDepCurrentTag.close();
                isoDepCurrentTag = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException("fail", e);
            }
        }
    }




}
