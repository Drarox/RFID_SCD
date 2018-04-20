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

package com.st.NFC;



import java.util.Hashtable;

import com.st.NFC.NFCTag.NfcTagTypes;


import android.util.Log;


public class stnfccchandler implements CCFileGenHandler {

    final static int MAX_FILES = 8;
    protected short mcclength;
    protected short    mccmappingver;
    protected short mmaxbytesread;
    protected short mmaxbyteswritten;


    public class TLVBlock {

        protected short mtfield;
        protected short mlfield;
        protected int mfieldId;
        protected int mndeffilelength;
        protected short mreadeaccess;
        protected short mwriteaccess;
        protected short mextreadaccess;
        protected short mextwriteaccess;
        protected stnfcProtectionLockStates mextreadstate;
        protected stnfcProtectionLockStates mextwritestate;

        public TLVBlock() {
            mtfield = 0;
            mlfield = 0;
            mfieldId = 0;
            mndeffilelength = 0;
            mreadeaccess = 0;
            mwriteaccess = 0;
            mextreadaccess = 0;
            mextwriteaccess = 0;
            mextreadstate = stnfcProtectionLockStates.NDEF_LOCK_NO;
            mextwritestate = stnfcProtectionLockStates.NDEF_LOCK_NO;
        }

    };

    private Hashtable<stnfcProtectionLockStates, String> NdefTLockStateDescr = new Hashtable<stnfcProtectionLockStates, String>() {
        {
            put(stnfcProtectionLockStates.NDEF_LOCK_NO, "NDEF no lock");
            put(stnfcProtectionLockStates.NDEF_LOCK_SOFT, "NDEF soft lock");
            put(stnfcProtectionLockStates.NDEF_LOCK_PERMANENT, "NDEF permanent lock");
        }
    };

    protected short _mnbTLVBlocks; // only one TLV blocks per CC file as only one NDEF File
    protected TLVBlock _mTLVBlockArray[];

    // TLV control part
    // To be updated as we may have several TLV blocks in a same NDEF File
//    protected short mtfield;
//    protected short mlfield;
//    protected int mfieldId;
//    protected int mndeffilelength;
//    protected short mreadeaccess;
//    protected short mWriteAccess;



    public stnfccchandler()
    {
        mcclength = 0;
        mccmappingver = 0;
        mmaxbytesread = 0;
        mmaxbyteswritten = 0;

        _mnbTLVBlocks = 1;
        _mTLVBlockArray = new TLVBlock[1];

        _mTLVBlockArray[0].mtfield = 0;
        _mTLVBlockArray[0].mlfield = 0;
        _mTLVBlockArray[0].mfieldId = 0;
        _mTLVBlockArray[0].mndeffilelength = 0;
        _mTLVBlockArray[0].mreadeaccess = 0;
        _mTLVBlockArray[0].mwriteaccess = 0;

        _mTLVBlockArray[0].mextreadaccess = 0;
        _mTLVBlockArray[0].mextwriteaccess = 0;
        _mTLVBlockArray[0].mextreadstate = stnfcProtectionLockStates.NDEF_LOCK_NO;
        _mTLVBlockArray[0].mextwritestate = stnfcProtectionLockStates.NDEF_LOCK_NO;

    }

    public stnfccchandler(byte[] buffer)
    {
        int TLVBlockSize = 8;
        int CCFileHeaderSize = 7;

        short bufferlength = (short) buffer.length;
        if (bufferlength == 0  || bufferlength < CCFileHeaderSize) {
            mcclength = 0;
            mccmappingver = 0;
            mmaxbytesread = 0;
            mmaxbyteswritten = 0;

            _mnbTLVBlocks = 1;
            _mTLVBlockArray = new TLVBlock[1];

            _mTLVBlockArray[0].mtfield = 0;
            _mTLVBlockArray[0].mlfield = 0;
            _mTLVBlockArray[0].mfieldId = 0;
            _mTLVBlockArray[0].mndeffilelength = 0;
            _mTLVBlockArray[0].mreadeaccess = 0;
            _mTLVBlockArray[0].mwriteaccess = 0;

            _mTLVBlockArray[0].mextreadaccess = 0;
            _mTLVBlockArray[0].mextwriteaccess = 0;
            _mTLVBlockArray[0].mextreadstate = stnfcProtectionLockStates.NDEF_LOCK_NO;
            _mTLVBlockArray[0].mextwritestate = stnfcProtectionLockStates.NDEF_LOCK_NO;

        } else {

            mcclength = (short) ((buffer[0]<<8 &0xFF) + (buffer[1]&0xFF));
            mccmappingver = (short)(buffer[2]&0xFF);
            mmaxbytesread = (short) ((((short)buffer[3])<<8 &0xFF) + (((short)buffer[4])&0xFF));
            mmaxbyteswritten = (short) ((((short)buffer[5])<<8 &0xFF) + (((short)buffer[6])&0xFF));

            _mnbTLVBlocks = (short)( (buffer.length - CCFileHeaderSize) / TLVBlockSize);

            // NB TLV block can be confirmed from SYS files in case of ST product
            _mTLVBlockArray = new TLVBlock[_mnbTLVBlocks];

            for(int i=0; i< _mnbTLVBlocks;i++)
            {
                 _mTLVBlockArray[i] = new TLVBlock();
                _mTLVBlockArray[i].mtfield = (short)buffer[7+i*TLVBlockSize];
                _mTLVBlockArray[i].mlfield = (short)buffer[8+i*TLVBlockSize];
                _mTLVBlockArray[i].mfieldId = (int)((buffer[9+i*TLVBlockSize]&0xFF)<<8)+ (int)(buffer[10+i*TLVBlockSize]&0xFF);
                _mTLVBlockArray[i].mndeffilelength = (int)((buffer[11+i*TLVBlockSize]&0xFF)<<8) + (int)(buffer[12+i*TLVBlockSize]&0xFF);
                _mTLVBlockArray[i].mreadeaccess = (short)buffer[13+i*TLVBlockSize];
                _mTLVBlockArray[i].mwriteaccess = (short)buffer[14+i*TLVBlockSize];

                _mTLVBlockArray[i].mextreadaccess = 0;
                _mTLVBlockArray[i].mextwriteaccess = 0;
                _mTLVBlockArray[i].mextreadstate = stnfcProtectionLockStates.NDEF_LOCK_NO;
                _mTLVBlockArray[i].mextwritestate = stnfcProtectionLockStates.NDEF_LOCK_NO;

            }
        }
    }


     // accessors
     public short getcclength() {return mcclength;};
     public short getccmappingver() {return mccmappingver;}
     public short getmaxbytesread() {return mmaxbytesread;}
     public short getmaxbyteswritten() {return mmaxbyteswritten;}

    // TLV control part

     public short  getNbFile()
     {
         return _mnbTLVBlocks;
     }


     public short  getnbNDEFFile()
     {
         return _mnbTLVBlocks;
     }

     public short getnbTLVblocks()
     {
         return _mnbTLVBlocks;
     }

     // Backward compatibility - if ID is notre provided return request info from the first TLV

//     short     gettfield() {return _mTLVBlockArray[0].mtfield;}
//     short  getlfield() {return _mTLVBlockArray[0].mlfield;}
//     int    getfieldId(){return _mTLVBlockArray[0].mfieldId;}
//     int    getndeffilelength() {return _mTLVBlockArray[0].mndeffilelength;}
//     short  getreadaccess(){return _mTLVBlockArray[0].mreadeaccess;}
//     short  getwriteaccess(){return _mTLVBlockArray[0].mWriteAccess;}
//
//     public boolean isNDEFLOCKWrite() {return (_mTLVBlockArray[0].mWriteAccess == -128); }
//     public boolean isNDEFLOCKRead()  {return (_mTLVBlockArray[0].mreadeaccess == -128); }
//
//
//     public boolean isNDEFPermanentLOCKWrite() {return (_mTLVBlockArray[0].mWriteAccess == -1); }
//     public boolean isNDEFPermanentLOCKRead()  {return (_mTLVBlockArray[0].mreadeaccess == -2); }
//
//
//     public boolean isreadaccessenabled()
//     {
//         return (getreadaccess()==1)?true:false;
//     }
//
//     public boolean iswriteaccessenabled()
//     {
//         return (getwriteaccess()==1)?true:false;
//     }

     public short     gettfield(int TLVID ) {return  (_mTLVBlockArray[TLVID]!=null)?_mTLVBlockArray[TLVID].mtfield:0;}
     public short  getlfield(int TLVID ) {return (_mTLVBlockArray[TLVID]!=null)?_mTLVBlockArray[TLVID].mlfield:0;}
     public int    getfieldId(int TLVID ){return (_mTLVBlockArray[TLVID]!=null)?_mTLVBlockArray[TLVID].mfieldId:0;}
     public int    getndeffilelength(int TLVID ) {return (_mTLVBlockArray[TLVID]!=null)?_mTLVBlockArray[TLVID].mndeffilelength:0;}

     public short  getreadaccess(int TLVID ){return (_mTLVBlockArray[TLVID]!=null)?_mTLVBlockArray[TLVID].mreadeaccess:0;}
     public short  getwriteaccess(int TLVID ){return (_mTLVBlockArray[TLVID]!=null)?_mTLVBlockArray[TLVID].mwriteaccess:0;}
//     public short  getreadaccess(int TLVID ){return (short) ((_mTLVBlockArray[TLVID]!=null)?(_mTLVBlockArray[TLVID].mreadeaccess | _mTLVBlockArray[TLVID].mextreadaccess):0);}
//     public short  getwriteaccess(int TLVID ){return (short) ((_mTLVBlockArray[TLVID]!=null)?(_mTLVBlockArray[TLVID].mWriteAccess | _mTLVBlockArray[TLVID].mextwriteaccess):0);}

     public short  getextreadaccess(int TLVID ){return (_mTLVBlockArray[TLVID]!=null)?_mTLVBlockArray[TLVID].mextreadaccess:0;}
     public short  getextwriteaccess(int TLVID ){return (_mTLVBlockArray[TLVID]!=null)?_mTLVBlockArray[TLVID].mextwriteaccess:0;}

//     public boolean isNDEFLOCKWrite(int TLVID ) {return (_mTLVBlockArray[TLVID]!=null)?(_mTLVBlockArray[TLVID].mWriteAccess == -128):false; }
//     public boolean isNDEFLOCKRead(int TLVID )  {return (_mTLVBlockArray[TLVID]!=null)?(_mTLVBlockArray[TLVID].mreadeaccess == -128):false; }

     public boolean isNDEFLOCKWrite(int TLVID ) {return (_mTLVBlockArray[TLVID]!=null)?(_mTLVBlockArray[TLVID].mwriteaccess == -128 || _mTLVBlockArray[TLVID].mextwriteaccess == -128):false; }
     public boolean isNDEFLOCKRead(int TLVID )  {return (_mTLVBlockArray[TLVID]!=null)?(_mTLVBlockArray[TLVID].mreadeaccess == -128 || _mTLVBlockArray[TLVID].mextreadaccess == -128):false; }

    @Override
    public boolean isAPwdAvailableForNDEFLockRead(int zone) {
        if (_mTLVBlockArray[zone].mextreadstate == stnfcProtectionLockStates.NDEF_LOCK_PERMANENT) {
            return false;
        } else
            return true;
    }

    public boolean isNDEFPermanentLOCKWrite(int TLVID) {
        boolean ret = false;
        if (_mTLVBlockArray[TLVID].mextwritestate == stnfcProtectionLockStates.NDEF_LOCK_PERMANENT) {
            return true;
        } else {
            if (_mTLVBlockArray[TLVID].mextwritestate == stnfcProtectionLockStates.NDEF_LOCK_SOFT) {
            return false;
            }
        }
        return (_mTLVBlockArray[TLVID] != null) ? (_mTLVBlockArray[TLVID].mwriteaccess == -1) : false;
    }


    public boolean isNDEFPermanentLOCKRead(int TLVID) {
        boolean ret = false;
        if (_mTLVBlockArray == null) return ret;
        if (TLVID >= _mTLVBlockArray.length) return ret;
        if (_mTLVBlockArray[TLVID].mextreadstate == stnfcProtectionLockStates.NDEF_LOCK_PERMANENT) {
            return true;
        } else {
            if (_mTLVBlockArray[TLVID].mextreadstate == stnfcProtectionLockStates.NDEF_LOCK_SOFT) {
            return false;
            }
        }
        return (_mTLVBlockArray[TLVID] != null) ? (_mTLVBlockArray[TLVID].mreadeaccess == -2) : false;
    }

    public String getNDEFRWLockState(int TLVID, boolean mode) {
        if (mode)
            return NdefTLockStateDescr.get(_mTLVBlockArray[TLVID].mextreadstate);
        else
            return NdefTLockStateDescr.get(_mTLVBlockArray[TLVID].mextwritestate);

    }



     public void setextwriteaccessenabled(int TLVID, boolean locked )
     {
         if (locked)
             _mTLVBlockArray[TLVID].mextwriteaccess = -128;
         else
             _mTLVBlockArray[TLVID].mextwriteaccess = 0;

     }
     public void setextreadaccessenabled(int TLVID, boolean locked )
     {
         if (locked)
             _mTLVBlockArray[TLVID].mextreadaccess = -128;
         else
             _mTLVBlockArray[TLVID].mextreadaccess = 0;

     }
     public void resetextwriteaccessenabled(int TLVID )
     {
             _mTLVBlockArray[TLVID].mextwriteaccess = 0;
             resetWlockState(TLVID);
     }

     public void resetextreadaccessenabled(int TLVID)
     {
             _mTLVBlockArray[TLVID].mextreadaccess = 0;
             resetRlockState(TLVID);
     }


     private void setWlockState (int TLVID,stnfcProtectionLockStates LockStates){
         _mTLVBlockArray[TLVID].mextwritestate = LockStates;
     }
     private void setRlockState (int TLVID,stnfcProtectionLockStates LockStates){
         _mTLVBlockArray[TLVID].mextreadstate = LockStates;
     }
     private void resetWlockState (int TLVID){
         _mTLVBlockArray[TLVID].mextwritestate = stnfcProtectionLockStates.NDEF_LOCK_NO;
     }
     private void resetRlockState (int TLVID){
         _mTLVBlockArray[TLVID].mextreadstate = stnfcProtectionLockStates.NDEF_LOCK_NO;
     }
     public void setProtectionWLockState(int TLVID, boolean locked, stnfcProtectionLockStates LockStates)
     {
         setextwriteaccessenabled(TLVID,locked);
         setWlockState(TLVID,LockStates);

     }
     public void setProtectionRLockState(int TLVID, boolean locked, stnfcProtectionLockStates LockStates)
     {
         setextreadaccessenabled(TLVID,locked);
         setRlockState(TLVID,LockStates);
     }

    @Override
    public int getCCLength() {
        // TODO Auto-generated method stub
        return this.mcclength;
    }

    @Override
    public byte getCCVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

}
