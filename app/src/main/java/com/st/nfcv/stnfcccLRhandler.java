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

package com.st.nfcv;



import java.util.ArrayList;
import java.util.List;

import com.st.NFC.CCFileGenHandler;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.STNfcTagHandler;
import com.st.NFC.STNfcTagVHandler;


public class stnfcccLRhandler implements CCFileGenHandler {

    public final static int TAG_TYPEV_CC_FILE_TLV_TYPE_NDEF = 3;
    public final static int TAG_TYPEV_CC_FILE_TLV_TYPE_PROPRIETARY = 0xFD;

    final static int MAX_FILES = 8;

    /*
 protected class ZoneCCF {
        protected short mCCFLength;
        protected short mMemorySize;

        protected byte mMagicNumber;
        protected byte mMappingVersion;
        protected byte mMajorVersion = 0;
        protected byte mMinorVersion = 0;
        protected byte mReadAccess;
        protected byte mWriteAccess;
        protected byte mByte2MemorySize;
        protected byte mnfctype5Tag;

        protected byte mbyte4RFU;
        protected byte mbyte5RFU;
        protected byte mByte6MemorySize;
        protected byte mByte7MemorySize;

        protected boolean mByte3MultipleRead = false;
        protected byte mByte3Type = 0;


        protected short _mnbTLVBlocks; // only one TLV blocks per CC file as only one NDEF File
        //protected TLVBlock _mTLVBlockArray[];

        protected List<TLVBlock> _mTLVBlockArray = new ArrayList<TLVBlock>();
        protected List<TLVBlockMemory> _mTLVBlockArrayMemory = new ArrayList<TLVBlockMemory>();
    }
*/

    protected short mCCFLength;
    protected short mMemorySize;

    protected byte mMagicNumber;
    protected byte mMappingVersion;
    protected byte mMajorVersion = 0;
    protected byte mMinorVersion = 0;
    protected byte mReadAccess;
    protected byte mWriteAccess;
    protected byte mByte2MemorySize;
    protected byte mnfctype5Tag;

    protected byte mbyte4RFU;
    protected byte mbyte5RFU;
    protected byte mByte6MemorySize;
    protected byte mByte7MemorySize;

    protected boolean mByte3MultipleRead = false;
    protected byte mByte3Type = 0;


    protected short _mnbTLVBlocks; // only one TLV blocks per CC file as only one NDEF File
    //protected TLVBlock _mTLVBlockArray[];

    protected List<TLVBlock> _mTLVBlockArray = new ArrayList<TLVBlock>();
    protected List<TLVBlockMemory> _mTLVBlockArrayMemory = new ArrayList<TLVBlockMemory>();


    private short mNbMemoryZone;
    //private ZoneCCF[] mZoneCCF = new ZoneCCF[MAX_FILES];

    public stnfcccLRhandler()
    {
        mCCFLength = 0;
        mMagicNumber = 0;
        mMappingVersion = 0;
        mReadAccess = 0;
        mWriteAccess = 0;

        mByte2MemorySize = 0;
        mnfctype5Tag = 0;

        mByte3MultipleRead = false;
        mByte3Type = 0;

        _mnbTLVBlocks = 1;
        _mTLVBlockArray.add(new TLVBlock());
        _mTLVBlockArrayMemory.add(new TLVBlockMemory());

        TLVBlock mtlv = _mTLVBlockArray.get(0);
        mtlv.mtfield = 0;
        mtlv.mlfield = 0;
        mtlv.mfieldId = 0;
        mtlv.mndeffilelength = 0;
        mtlv.mreadeaccess = 0;
        mtlv.mwriteaccess = 0;
    }



    public byte getMagicNumber() {
        return mMagicNumber;
    }


    public void setmMagicNumber(byte mMagicNumber) {
        this.mMagicNumber = mMagicNumber;
    }


    public short getCCFMemorySize() {
        //return mByte2MemorySize;
        return mMemorySize;
    }



    public byte getReadAccess() {
        return mReadAccess;
    }


    public byte getWriteAccess() {
        return mWriteAccess;
    }



    public class TLVBlock {

        protected short mtfield;
        protected short mlfield;
        protected int mfieldId;
        protected int mndeffilelength;
        protected short mreadeaccess;
        protected short mwriteaccess;


        public TLVBlock() {
            mtfield = 0;
            mlfield = 0;
            mfieldId = 0;
            mndeffilelength = 0;
            mreadeaccess = 0;
            mwriteaccess = 0;

        }

    };
    public class TLVBlockMemory {

        protected short startAdr;
        protected short offset;
        protected short length;


        public TLVBlockMemory() {
            startAdr = 0;
            offset = 0;
            length = 0;
        }


        public short getLength() {
            return length;
        }
        public short getOffset() {
            return offset;
        }
        public short getStartAdr() {
            return startAdr;
        }

    };


    public void setNbMemoryZone(short mNbMemoryZone) {
        this.mNbMemoryZone = mNbMemoryZone;
    }


    @Override
    public short getNbFile() {
        // TODO Auto-generated method stub
        return mNbMemoryZone;
    }

     public ArrayList<Integer>  getnbNDEFFile()
     {
         // need to parse and count only one with NDEF
         ArrayList<Integer> arrlist = new ArrayList<Integer>();
//         List<short> listTLVBlock = new ArrayList<short>();
         int iterator = 0;
         if (_mTLVBlockArray != null) {
             for (TLVBlock src : _mTLVBlockArray) {
                    if( src.mtfield == 0x03) {
                        //NDEF detected
                        // assign to arraylists here
                        arrlist.add(iterator);
                        iterator ++;
                    }
                }

         }

         return arrlist;
     }


    public int getTLVblockLengthInfo (int index) {
        if (_mTLVBlockArray.size()> index ) return _mTLVBlockArray.get(index).mndeffilelength;
        else
            return 0;
    }

    public int getTLVLengthSizeInfo (int index) {
        if (_mTLVBlockArray.size()> index )  return _mTLVBlockArray.get(index).mlfield;
        else
            return 0;
    }

    public boolean isANDEFMessage(short iterator) {
        boolean ret = false;
        if (_mTLVBlockArrayMemory.size()> iterator ) {
            TLVBlockMemory mem = _mTLVBlockArrayMemory.get(iterator);
            TLVBlock mtlv = _mTLVBlockArray.get(iterator);
            if (mtlv.mtfield == 0x03) ret = true;
            // do not forget RW/ACCess

        }
        else {

        }
        return ret;
    }

    public boolean setTLVBlockMemory(short stadr,short offset, short length ,short id) {
        TLVBlockMemory mtlv = null;
        TLVBlock mtlvinf = _mTLVBlockArray.get(id);
        boolean ret = true;
        if (_mTLVBlockArrayMemory.size()> id)
            mtlv = _mTLVBlockArrayMemory.get(id);
        if (mtlv != null) {
        // TLV block already created
            mtlv.startAdr = stadr;
            mtlv.offset = (short) (offset + mtlvinf.mlfield + 1);
            mtlv.length = length;

        } else {
        // add TLV block
            mtlv = new TLVBlockMemory();
            mtlv.startAdr = stadr;
            // Offset in memory + T + L of TLV
            mtlv.offset = (short) (offset + mtlvinf.mlfield+1);
            mtlv.length = length;
            _mTLVBlockArrayMemory.add(id, mtlv);
        }

        return ret;
    }


    public TLVBlockMemory getTLVBlockMemoryInfo (int index) {
        if (_mTLVBlockArrayMemory.size()> index )  return _mTLVBlockArrayMemory.get(index);
        else
            return null;
    }

    public boolean setTLVLRhandler(byte[] buffer,short id) {
        TLVBlock mtlv = null;
        boolean ret = true;
        if (buffer[0] != 0x03 && buffer[0] != 0xFD && buffer[0] != 0xFE) return false;
        if (_mTLVBlockArray.size()> id)
            mtlv = _mTLVBlockArray.get(id);
        if (mtlv != null) {
        // TLV block already created
        } else {
        // add TLV block
            mtlv = new TLVBlock();
            _mTLVBlockArray.add(id, mtlv);
        }

        // update TLV block
        if (buffer != null && buffer[0] == 0x03) {// NDEF
        //    if (resultBlock0 != null && resultBlock0[1] == 0xFD) // Proprietary
        //  if (resultBlock0 != null && resultBlock0[1] == 0xFE) // Last TLV
            mtlv.mtfield = buffer[0];
        }
        // Length
        if (buffer != null && buffer[1] != -1)  { // Length on 1 byte
            mtlv.mlfield = (short) 1;
            //(buffer[1]&0xFF)
            //mtlv.mndeffilelength = buffer[1];
            mtlv.mndeffilelength =     (short) (buffer[1]&0xFF);
        }
        else {
            // length on 3 bytes
            // to be revisited
            mtlv.mlfield = (short) 3;
            mtlv.mndeffilelength = ((buffer[2] & 0xFF) << 8) + (buffer[3] & 0xFF);
            //mtlv.mndeffilelength = (short) (buffer[2] + buffer[3]);

        }

        // End of data detected .... stop
        if (buffer[0] == 0xFE) {
            mtlv.mlfield = (short) 0;
            mtlv.mndeffilelength = 0;

            ret = false;
        }

        mtlv.mreadeaccess = this.mReadAccess;
        mtlv.mwriteaccess = this.mWriteAccess;

        return ret;
    }



    public stnfcccLRhandler(byte[] buffer)
    {
        int CCFileHeaderSize = 4;
        int TLVBlockSize = 8;


        short bufferlength = (short) buffer.length;
        if (bufferlength == 0  || bufferlength < CCFileHeaderSize) {
            mCCFLength = 0;
            mMagicNumber = 0;
            mMappingVersion = 0;
            mByte2MemorySize = 0;
            mnfctype5Tag          = 0;
            mReadAccess = 0;
            mWriteAccess = 0;
            mByte3MultipleRead = false;
            mByte3Type = 0;

        } else {
            if (bufferlength > 3) {
                mMagicNumber = buffer[0];
                mMappingVersion = (byte) (buffer[1] & 0xF0);
                // need to add access conditions
                //CC1 = bit7-6 : Major version
                //         bit5-4 : Minor version
                //         bit3-2 : Read access (00:free access)
                //         bit1-0 : Write access (00:free access / 10:write need password / 11:no write access)
                mMajorVersion = (byte) ((mMappingVersion & 0xC0) >> 6);
                mMinorVersion = (byte) ((mMappingVersion & 0x30) >> 4);

                mReadAccess = (byte) ((buffer[1] & 0x0C) );
                mWriteAccess = (byte) ((buffer[1] & 0x03));

                mByte2MemorySize = buffer[2];
                mMemorySize = (short) (mByte2MemorySize * 8);
                // To be fisplayed in CC file output .....
                 if((buffer[3] & (byte)0x01) == (byte)0x01)
                     mByte3MultipleRead = true;
                 else
                     mByte3MultipleRead = false;

                mByte3Type = (byte) (buffer[3] & (byte)0xFE);


                if (buffer[2]  == 0x00) {
                    CCFileHeaderSize = 8;
                    mCCFLength = (short) CCFileHeaderSize;
                    // CC file on 8 bytes
                    // decode following info
                    if (bufferlength == CCFileHeaderSize) {
                        // To be revisited .......Not true just for information --- not valid
                        mByte6MemorySize = buffer[6];
                        mByte7MemorySize = buffer[7];
                        mMemorySize = (short) (((short)((mByte6MemorySize) << 8) + mByte7MemorySize) * 8);
                    }
                    //_mnbTLVBlocks = (short)( (buffer.length - CCFileHeaderSize) / TLVBlockSize);
                    _mnbTLVBlocks = 1;

                    // NB TLV block can be confirmed from SYS files in case of ST product
                    //_mTLVBlockArray.add(new TLVBlock()); //= new TLVBlock[_mnbTLVBlocks];

                    for(int i=0; i< _mnbTLVBlocks;i++)
                    {
                        //_mTLVBlockArray[i] = new TLVBlock();
                        _mTLVBlockArray.add(new TLVBlock());
                    }

                } else {
                    CCFileHeaderSize = 4;
                    mCCFLength = (short) CCFileHeaderSize;
                    //_mnbTLVBlocks = (short)( (buffer.length - CCFileHeaderSize) / TLVBlockSize);
                    _mnbTLVBlocks = 1;
                    for(int i=0; i< _mnbTLVBlocks;i++)
                    {
                        //_mTLVBlockArray[i] = new TLVBlock();
                        _mTLVBlockArray.add(new TLVBlock());
                    }

                    // NB TLV block can be confirmed from SYS files in case of ST product
                    //_mTLVBlockArray = new TLVBlock[_mnbTLVBlocks];

                }

            } else {
                // decoding issue .... not enough byte to decode CC file
            }

            // decode the CC file
        }


    }

    public byte getMinorVersion() {
        return mMinorVersion;
    }
    public byte getMajorVersion() {
        return mMajorVersion;
    }


    @Override
    public int getCCLength() {
        // TODO Auto-generated method stub
        return this.mCCFLength;
    }

    @Override
    public byte getCCVersion() {
        // TODO Auto-generated method stub
        return 0;
    }



    @Override
    public boolean isNDEFPermanentLOCKRead(int currentValideTLVBlokID) {
        // TODO Auto-generated method stub
        int pwd = -1;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
        if (sysHDL.mST25DVRegister != null) {
            stnfcRegisterHandler.ST25DVRegisterTable reg = sysHDL.mST25DVRegister.getZSSentry(currentValideTLVBlokID);
            pwd = sysHDL.mST25DVRegister.getPasswordNumber(reg);
        }
        if (isNDEFLOCKRead(currentValideTLVBlokID) && pwd == 0) return true;
        return false;
    }

    @Override
    public boolean isNDEFLOCKRead(int currentValideTLVBlokID) {
        // TODO Auto-generated method stub
        boolean ret = false;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        // ret1 represent the product handling and default handling
        boolean ndefLocked = false;

        // by default, ret2 handle present pwd behaviour
        boolean ret2 = false;

        if (currentTag.getModel().contains("ST25DV")) {
            STNfcTagVHandler myTagHDL = (STNfcTagVHandler)currentTag.getSTTagHandler();
            ret2 = myTagHDL.presentReadPasswordDone[currentValideTLVBlokID];

            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
            if (sysHDL.mST25DVRegister != null) {
                stnfcRegisterHandler.ST25DVRegisterTable reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ1SS;
                switch (currentValideTLVBlokID){
                    case 0:
                        break;
                    case 1:
                        reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ2SS;
                        break;
                    case 2:
                        reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ3SS;
                        break;
                    case 3:
                        reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ4SS;
                        break;
                    default:
                        break;
                }
                ndefLocked = !sysHDL.mST25DVRegister.isReadUnLocked(reg);
            }

            if (ret2 == true)
                ret = false;
            else
                ret = ndefLocked;
        }

        return ret;
    }

    @Override
    public boolean isAPwdAvailableForNDEFLockRead(int zone) {
        boolean ret = true;
        int pwd = -1;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
        if (sysHDL.mST25DVRegister != null) {
            stnfcRegisterHandler.ST25DVRegisterTable reg = sysHDL.mST25DVRegister.getZSSentry(zone);
            pwd = sysHDL.mST25DVRegister.getPasswordNumber(reg);
        }
        if (pwd == 0) return false;

        return ret;
    }

    public short  gettfield(int TLVID ) {return  (_mTLVBlockArray.get(TLVID)!=null)?_mTLVBlockArray.get(TLVID).mtfield:0;}
     public short  getlfield(int TLVID ) {return (_mTLVBlockArray.get(TLVID)!=null)?_mTLVBlockArray.get(TLVID).mlfield:0;}
     public int    getfieldId(int TLVID ){return (_mTLVBlockArray.get(TLVID)!=null)?_mTLVBlockArray.get(TLVID).mfieldId:0;}
     public int    getndeffilelength(int TLVID ) {return (_mTLVBlockArray.get(TLVID)!=null)?_mTLVBlockArray.get(TLVID).mndeffilelength:0;}

     public short  getreadaccess(int TLVID ){return (_mTLVBlockArray.get(TLVID)!=null)?_mTLVBlockArray.get(TLVID).mreadeaccess:0;}
     public short  getwriteaccess(int TLVID ){return (_mTLVBlockArray.get(TLVID)!=null)?_mTLVBlockArray.get(TLVID).mwriteaccess:0;}


    public byte[] CreateCCFileByteArray(boolean isMultipleReadSupported, boolean isMemoryExceed2048bytesSize,
            int nbBlocks, int blockSize) {
        String strCCtobeWritten = "";
        byte[] ConvertedString;
        // CC0
        strCCtobeWritten = strCCtobeWritten + "E1";
        // CC1
        strCCtobeWritten = strCCtobeWritten + "40";
        // CC2
        byte CC2 = (byte) 0x00;
        if (((nbBlocks)/2) > 255) // memory size = nb
            // blocks *4 == nb
            // bytes
            CC2 = (byte) 0x00; // CC2
        else
            CC2 = (byte) ((nbBlocks) / 2); // CC2
        strCCtobeWritten = strCCtobeWritten +  Helper.ConvertHexByteToString(CC2);

        // CC3
        byte CC3 = (byte) 0x00;
        if (isMultipleReadSupported)
            CC3 |= (byte) 0x01; // bit0= 1:support MultipleReadBlocks 0:no
        if (isMemoryExceed2048bytesSize)
            CC3 |= (byte) 0x04; // bit2= 1:Memory exceed 2048 bytes 0:no

        strCCtobeWritten = strCCtobeWritten + Helper.ConvertHexByteToString(CC3);

        if (CC2 == 0) {
            // CC4
            byte CC4 = (byte) 0x00;
            strCCtobeWritten = strCCtobeWritten + Helper.ConvertHexByteToString(CC4);
            // CC5
            byte CC5 = (byte) 0x00;
            strCCtobeWritten = strCCtobeWritten + Helper.ConvertHexByteToString(CC5);
            // Coding the size .................
            // CC6
            int ccSize = ((nbBlocks) / 2);
            byte CC6 = (byte) (ccSize  >> 8);
            strCCtobeWritten = strCCtobeWritten + Helper.ConvertHexByteToString(CC6);
            // CC7
            byte CC7 = (byte) (ccSize  & 0xFF);
            strCCtobeWritten = strCCtobeWritten + Helper.ConvertHexByteToString(CC7);

        } else {
        }

        strCCtobeWritten = strCCtobeWritten.replace(" ", "");
        ConvertedString = Helper.ConvertStringToHexBytesArray(strCCtobeWritten);
        return ConvertedString;
    }

    public byte[] CreateTLFileByteArray(int iNDEFlen) {
        String strTLtobeWritten = "";
        String strNDEFlen = "";
        byte[] ConvertedString;

        if (iNDEFlen < 0xFF) // 1 byte length
            strNDEFlen = Helper.ConvertHexByteToString((byte) iNDEFlen);
        else
            strNDEFlen = "FF" + Helper.ConvertHexByteArrayToString(Helper.ConvertIntTo2bytesHexaFormat(iNDEFlen)); // 4
                                                                                                                    // byte
                                                                                                                    // length
                                                                                                                    // //FF
                                                                                                                    // +
                                                                                                                    // 2
                                                                                                                    // byte
                                                                                                                    // length
        strTLtobeWritten = strNDEFlen + strTLtobeWritten;

        strTLtobeWritten = "03" + strTLtobeWritten; // RTD_TEXT

        ConvertedString = Helper.ConvertStringToHexBytesArray(strTLtobeWritten);
        return ConvertedString;

    }

    public byte[] CreateTLTerminatorFileByteArray() {
        String strNDEFTerminatortobeWritten = "FE";
        byte[] ConvertedString;
        ConvertedString = Helper.ConvertStringToHexBytesArray(strNDEFTerminatortobeWritten);
        return ConvertedString;
    }

}
