/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1719 $
  * Revision of last commit    :  $Rev: 1719 $
  * Date of last commit     :  $Date: 2016-03-04 15:42:31 +0100 (Fri, 04 Mar 2016) $
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



import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import com.st.NFC.stnfchelper;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;




public class stnfcndefhandler {

    //+ APT 06/09/2013
    final int MAX_SUPPORTED_NDEF_MESSAGE_NB = 1;
    //- APT 06/09/2013
    final int MAX_SUPPORTED_NDEF_RECORD_NB = 10;

    private String TAG = this.getClass().getName();

    // NDEF record object
    // One or more NDEF record may be encapsulated in NDEF message
    // Actually a single NDEF record is supported in NDEF message.
    // TBD : Enhance Stnfcndefhandler to support more NDEF record.

    public class stndefrecord
    {

        public boolean     mMBFlag;          // set in the first record
        public boolean     mMEFlag;          // set in the last record
        public boolean     mCFFlag;          // set either in the first record chunck or in the middle record chunck.
        public boolean     mSRFlag;         // set if mpayloadlength field is a single octet.
        public boolean     mILFlag;         // set if mIDLength (ID_Length) field is present in the header as a single octets
        public tnf           mTNF;            // indicates the structure of the value of the mtype field
        public int       mtypelength;    // specifies the length in octets of the type field
        public int        mpayloadlength; // PayloadL3<<24 + PayLoadL2<<16 + PayloadL2<<8 + PayloadL1
        public int         mIDLength;        // 8-bit integer specifying the length is octets of the ID field
        public byte[]     mtype;            // type field that must follow the structure, encoding and formant implied by mTNF
        public byte[]     mid;            // Identifier in the form of a URI reference [RFC 3986] - middle, final record mustn't not have this field
        public byte[]     mpayload;        // application payload field  - opaque to NDEF.

        protected stndefrecstatus status;
        protected boolean     mMBTransparent;          // encoding free

        stndefrecord()
        {
            mMBFlag = false;
            mMEFlag = false;
            mCFFlag = false;
            mSRFlag = false;
            mILFlag = false;
            mTNF = tnf.empty;
            mtypelength = 0;
            mpayloadlength = 0;
            mIDLength = 0;
            mtype = null;
            mid = null;
            mpayload = null;
            status = stndefrecstatus.STATE_NDEFREC_UNSET;
            mMBTransparent = false;

        } // End of stndefrecord default constructor


        public void setmMBTransparent(boolean mMBTransparent) {
            this.mMBTransparent = mMBTransparent;
        }


        public void setmMBFlag(boolean mMBFlag) {
            this.mMBFlag = mMBFlag;
        }

        public void setmMEFlag(boolean mMEFlag) {
            this.mMEFlag = mMEFlag;
        }

        public void setmCFFlag(boolean mCFFlag) {
            this.mCFFlag = mCFFlag;
        }

        public void setmSRFlag(boolean mSRFlag) {
            this.mSRFlag = mSRFlag;
        }

        public void setmILFlag(boolean mILFlag) {
            this.mILFlag = mILFlag;
        }

        public void setmTNF(tnf mTNF) {
            this.mTNF = mTNF;
        }

        public void setMtypelength(int mtypelength) {
            this.mtypelength = mtypelength;
        }

        public void setMpayloadlength(int mpayloadlength) {
            this.mpayloadlength = mpayloadlength;
        }

        public void setmIDLength(int mIDLength) {
            this.mIDLength = mIDLength;
        }

        public void setMtype(byte[] mtype) {

            if (mtype == null) this.mtype = null;
            this.mtype = new byte [mtype.length];
            System.arraycopy(mtype, 0, this.mtype, 0, mtype.length);
        }

        public void setMid(byte[] mid) {

            if (mid == null) this.mid = null;
            this.mid = new byte [mid.length];
            System.arraycopy(mid, 0, this.mid, 0, mid.length);

        }

        public void setMpayload(byte[] mpayload) {
            if (mpayload == null) this.mpayload = null;
            this.mpayload = new byte [mpayload.length];
            System.arraycopy(mpayload, 0, this.mpayload, 0, mpayload.length);
        }

        public void setStatus(stndefrecstatus status) {
            this.status = status;
        }

        public byte[] serialize()
        {
            byte [] serializedNDEFRBuffer = null;
            if (this.mMBTransparent == false) {
            // NDEF Header
            byte NDEFRHeader = (byte) 0x00;
            byte NDEFRTypeLength = (byte) 0x00;
            byte NDEFPayLoadL[] = {0x00,0x00,0x00,0x00};
            byte NDEFRIDLength =(byte) 0x00;

            int NDEFRSize = 0;



            if (isamessagebegin())
                NDEFRHeader = (byte) (NDEFRHeader | (byte)0x80);
            else
                NDEFRHeader = (byte) (NDEFRHeader & (byte)0x7F);

            if (isamessageend())
                NDEFRHeader = (byte) (NDEFRHeader | (byte)0x40);
            else
                NDEFRHeader = (byte) (NDEFRHeader & (byte)0xBF);

            if (isachunkedmessage())
                NDEFRHeader = (byte) (NDEFRHeader | (byte)0x20);
            else
                NDEFRHeader = (byte) (NDEFRHeader & (byte)0xDF);

            if (isashortrecord())
                NDEFRHeader = (byte) (NDEFRHeader | (byte)0x10);
            else
                NDEFRHeader = (byte) (NDEFRHeader & (byte)0xEF);

            if (isIDpresent())
                NDEFRHeader = (byte) (NDEFRHeader | (byte)0x08);
            else
                NDEFRHeader = (byte) (NDEFRHeader & (byte)0xF7);

            // TNF Handling
            // recordCtx&0x03

            NDEFRHeader = (byte)(NDEFRHeader & (byte)0xF8);

            switch (mTNF)
            {
            case empty:
                break;
            case wellknown:
                NDEFRHeader = (byte)(NDEFRHeader | (byte)0x01);
                break;
            case media:
                NDEFRHeader = (byte)(NDEFRHeader | (byte)0x02);
                break;
            case uri:
                NDEFRHeader = (byte)(NDEFRHeader | (byte)0x03);
                break;
            case external:
                NDEFRHeader = (byte)(NDEFRHeader | (byte)0x04);
                break;
            case unknow:
                NDEFRHeader = (byte)(NDEFRHeader | (byte)0x05);
                break;
            case unchanged:
                NDEFRHeader = (byte)(NDEFRHeader | (byte)0x06);
                break;
            case rfu:
                NDEFRHeader = (byte)(NDEFRHeader | (byte)0x07);
                status = stndefrecstatus.STATE_NDEFREC_TNF_RFU_USED;
                break;
            default:
                NDEFRHeader = (byte)(NDEFRHeader & (byte)0xFF);
                status = stndefrecstatus.STATE_NDEFREC_TNF_RFU_USED;
                break;
            }

            NDEFRSize = 1;

            //
            NDEFRTypeLength = (byte) (gettypelength()&0xFF);
            NDEFRSize++;

            // Payload length
            if (mSRFlag) // in case of Short Message
            {
                NDEFPayLoadL[3] = (byte) (mpayloadlength & (byte)0xFF);
                NDEFRSize++;
            }
            else
            {
                NDEFPayLoadL[0] = (byte) ((mpayloadlength & 0xFF000000)>>24);
                NDEFPayLoadL[1] = (byte) ((mpayloadlength & 0x00FF0000)>>16);
                NDEFPayLoadL[2] = (byte) ((mpayloadlength & 0x0000FF00)>>8);
                NDEFPayLoadL[3] = (byte) ((mpayloadlength & 0x000000FF));
                NDEFRSize+=4;
            }
            // Handle ID
            if (mILFlag)
            {
                NDEFRIDLength = (byte) getIDlength();
                NDEFRSize+=1;
            }


            if (mtype!=null) NDEFRSize+=mtype.length;
            if (mid!=null) NDEFRSize+=mid.length;
            if (mpayload!=null) NDEFRSize+=mpayload.length;


            serializedNDEFRBuffer = new byte[NDEFRSize];

            serializedNDEFRBuffer[0] = (byte)NDEFRHeader;
            serializedNDEFRBuffer[1] = (byte)NDEFRTypeLength;
            int serializedNDEFRBufferOffset = 0;
            if (this.mSRFlag)
            {
                serializedNDEFRBuffer[2] = NDEFPayLoadL[3];
                serializedNDEFRBufferOffset  = 3;
            }
            else
            {
                System.arraycopy(NDEFPayLoadL, 0, serializedNDEFRBuffer, 2, NDEFPayLoadL.length);
                serializedNDEFRBufferOffset  = 6;
            }
            if (mILFlag) {
                // Store ID length
                serializedNDEFRBuffer[serializedNDEFRBufferOffset] = (byte) (NDEFRIDLength & 0xFF);
                serializedNDEFRBufferOffset++;
            }

            if (mtype!= null)
            {
            System.arraycopy(mtype, 0, serializedNDEFRBuffer, serializedNDEFRBufferOffset, mtype.length);
            serializedNDEFRBufferOffset += mtype.length;
            }
            if (mid!= null)
            {
            System.arraycopy(mid, 0, serializedNDEFRBuffer, serializedNDEFRBufferOffset, mid.length);
            serializedNDEFRBufferOffset += mid.length;
            }
            if (mpayload!= null)
            System.arraycopy(mpayload, 0, serializedNDEFRBuffer, serializedNDEFRBufferOffset, mpayload.length);

            return serializedNDEFRBuffer;
            }
            else {

                int NDEFRSize = 0;
                if (mpayload!=null) NDEFRSize+=mpayload.length;
                serializedNDEFRBuffer = new byte[NDEFRSize];
                if (mpayload!= null)
                System.arraycopy(mpayload, 0, serializedNDEFRBuffer, 0, mpayload.length);

                return serializedNDEFRBuffer;

            }
        }

        protected void checkTypeLength()
        {
            // TO BE DONE
            // check typeLenght validity according to TNF
            // if not ok set record status to STATE_NDEFREC_TYPE_LEN_INVALID
        }

        protected boolean isamessagebegin()
        { return mMBFlag;}
        protected boolean isamessageend()
        { return mMEFlag;}
        protected boolean isachunkedmessage()
        { return mCFFlag;}
        protected boolean isashortrecord()
        { return mSRFlag;}
        protected boolean isIDpresent()
        { return mILFlag;}
        protected tnf gettnf()
        { return mTNF;}

        protected String getTNFtoString()
        {
            return mTNF.toString();
        }
        protected int gettypelength() {return mtypelength;}
        protected int getpayloadlength() { return mpayloadlength;}
        protected int getIDlength() { return mIDLength;}

        protected byte [] getID()
        { if (mILFlag)
            return mid;
        else
            return null;
        }

        protected byte[] gettype()
        {
            if (mtypelength!=0)
                return mtype;
            else return null;
        }

        protected byte[] getpayload()
        {
            if (mpayloadlength != 0)
                return mpayload;
            else
                return null;
        }

        protected stndefrecstatus getStatus()
        {
            return status;
        }
    }

    protected  stndefrecord[] mstnfcndefrecord;     // Ndef Message is composed of one or more ndefRecords;
    protected  NdefMessage _mNdefMessage;           // SDK android NdefMessage
    protected  int mnbndefrecord;                    // nb records in the NDEF message
    protected  byte[] buffer;                        // buffer to store NDEF message
    protected  ndefError status;                    // store current object status

    public stnfcndefhandler()
    {
        mnbndefrecord = 0;
        buffer = null;
        mstnfcndefrecord = new stndefrecord[MAX_SUPPORTED_NDEF_RECORD_NB];
        status = ndefError.ERR_NDEF_UNKNOWN;
    }

    public stnfcndefhandler(byte[] rawnfcbuffer) {
        mstnfcndefrecord = new stndefrecord[MAX_SUPPORTED_NDEF_RECORD_NB];
        if (rawnfcbuffer == null || rawnfcbuffer.length == 0) {
            status = ndefError.ERR_NDEF_UNKNOWN;
            buffer = null;
            setNdefEmpty();
        } else

        {
            buffer = new byte[rawnfcbuffer.length];
            System.arraycopy(rawnfcbuffer, 0, buffer, 0, rawnfcbuffer.length);
            status = parseNdefBuffer((short) 0);
        }

    }
    public stnfcndefhandler(byte [] rawnfcbuffer,short offset)
    {
        mstnfcndefrecord = new stndefrecord[MAX_SUPPORTED_NDEF_RECORD_NB];
        if (rawnfcbuffer == null || rawnfcbuffer.length == 0){
            status = ndefError.ERR_NDEF_UNKNOWN;
            buffer = null;
            setNdefEmpty();
        } else
        {
            buffer = new byte[rawnfcbuffer.length];
            System.arraycopy(rawnfcbuffer, 0, buffer, 0, rawnfcbuffer.length);
            status = parseNdefBuffer((short)offset);
        }

    }

    public void setNdefEmpty()
    {
        setNdefEmpty(0);
    }


    public void setNdefEmpty(int recordId)
    {
        status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
        this.mstnfcndefrecord[recordId] = new stndefrecord();

        this.mstnfcndefrecord[recordId].setmSRFlag(true);
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.empty);
        this.mstnfcndefrecord[recordId].setmIDLength(0);
        this.mstnfcndefrecord[recordId].setMtypelength(0);
        this.mstnfcndefrecord[recordId].setMpayloadlength(0);
        this.mstnfcndefrecord[recordId].setMpayload(null);
        buffer = null;
    }



    public void setNdefAar(String Aar)
    {
        setNdefAar( Aar,0);
    }

    public void setNdefAar(String Aar, int recordId)
    {
        final String exttype = "android.com:pkg";

        if (Aar.isEmpty())
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }
        // create a new record
        this.mstnfcndefrecord[recordId] = new stndefrecord();

        byte [] payload  = new byte[Aar.getBytes().length];
        System.arraycopy(Aar.getBytes(), 0, payload, 0, Aar.getBytes().length);
        // evaluate text payload size

        if ( payload.length > 255)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }

        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.external);


        this.mstnfcndefrecord[recordId].setMtypelength(exttype.length());

        byte[] textType = null;
        try {
            textType = exttype.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,"Unable to convert external type in byte UTF8");
            e.printStackTrace();
        }

        this.mstnfcndefrecord[recordId].setMtype(textType);
        this.mstnfcndefrecord[recordId].setmIDLength(0);

        byte [] data = new byte[payload.length];
        System.arraycopy(payload, 0, data, 0, payload.length);

        this.mstnfcndefrecord[recordId].setMpayloadlength(data.length);
        this.mstnfcndefrecord[recordId].setMpayload(data);

        this.mnbndefrecord = 1;

        buffer = serialize();


    }

    public void setNdefProprietaryExtm24srDiscoveryCtrlMsg(byte [] payload)
    {
        setNdefProprietaryExtm24srDiscoveryCtrlMsg(payload,0);
    }

    public void setNdefProprietaryExtm24srDiscoveryCtrlMsg(byte [] payload, int recordId)
    {

        final String exttype = "st.com:m24sr_discovery_democtrl";
        if ((payload == null) || (payload.length==0))
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }
        // create a new record
        this.mstnfcndefrecord[recordId] = new stndefrecord();

        // evaluate text payload size

        if ( payload.length > 255)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }

        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.external);


        this.mstnfcndefrecord[recordId].setMtypelength(exttype.length());

        byte[] textType = null;
        try {
            textType = exttype.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,"Unable to convert external type in byte UTF8");
            e.printStackTrace();
        }

        this.mstnfcndefrecord[recordId].setMtype(textType);
        this.mstnfcndefrecord[recordId].setmIDLength(0);

        byte [] data = new byte[payload.length];
        System.arraycopy(payload, 0, data, 0, payload.length);

        this.mstnfcndefrecord[recordId].setMpayloadlength(data.length);
        this.mstnfcndefrecord[recordId].setMpayload(data);

        this.mnbndefrecord = 1;

        buffer = serialize();

    }

    public void setNdefVCard(String VcardPayload)
    {
        setNdefVCard( VcardPayload,0);
    }

    public void setNdefVCard(String VcardPayload, int recordId )
    {

        // final String vcardtype = "text/vcard";
        final String vcardtype = "text/x-vCard";

        if ((VcardPayload == null) || (VcardPayload.length()==0))
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }
        // create a new record

        this.mstnfcndefrecord[recordId] = new stndefrecord();

        Charset utfEncoding =Charset.forName("UTF-8") ;

        // evaluate uri payload size
        int maxDedicatedVcardSize =  255;

        // evaluate text payload size

        if ( VcardPayload.length() > maxDedicatedVcardSize)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }
        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.media);


        this.mstnfcndefrecord[recordId].setMtypelength(vcardtype.getBytes().length);
        byte[] textType = null;
        try {
            textType = vcardtype.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,"Unable to convert Vcard type in byte UTF8");
            e.printStackTrace();
        }
        this.mstnfcndefrecord[recordId].setMtype(textType);
        this.mstnfcndefrecord[recordId].setmIDLength(0);

        byte [] data = new byte[VcardPayload.getBytes().length];
        System.arraycopy(VcardPayload.getBytes(), 0, data, 0, VcardPayload.getBytes().length);

        this.mstnfcndefrecord[recordId].setMpayloadlength(VcardPayload.getBytes().length);
        this.mstnfcndefrecord[recordId].setMpayload(data);

        this.mnbndefrecord = 1;

        buffer = serialize();

    }

    public void setNdefRTDSP(byte [] payload)
    {
        setNdefRTDSP(payload,0);
    }

    public void setNdefRTDSP(byte [] payload,int recordId )
    {

        if ((payload == null) || (payload.length ==0))
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }

        // create a new record

        this.mstnfcndefrecord[recordId] = new stndefrecord();


        int maxDedicatedpayloadSize =  255;

        if ( payload.length > maxDedicatedpayloadSize)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }
        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.wellknown);
        this.mstnfcndefrecord[recordId].setMtypelength(2);
        this.mstnfcndefrecord[recordId].setMtypelength("Sp".getBytes().length);
        this.mstnfcndefrecord[recordId].setMtype("Sp".getBytes());
        this.mstnfcndefrecord[recordId].setmIDLength(0);


        // Build the RTD Text buffer
        byte[] data = new byte[payload.length];

        // - payload
        System.arraycopy(payload, 0, data, 0, payload.length);

        this.mstnfcndefrecord[recordId].setMpayloadlength(data.length);
        this.mstnfcndefrecord[recordId].setMpayload(data);

        this.mnbndefrecord = 1;

        buffer = serialize();

    }

    public void setNdefRTDURI(byte _id, String uri)
    {
        setNdefRTDURI( _id,  uri,0);
    }

    public void setNdefRTDURI(byte _id, String uri, int recordId)
    {

        if ((uri == null) || (uri.length()==0))
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }

        // create a new record

        this.mstnfcndefrecord[recordId] = new stndefrecord();

        Charset utfEncoding =Charset.forName("UTF-8") ;

        // evaluate uri payload size
        int maxDedicatedUriSize =  255 - 1;

        if ( uri.getBytes().length > maxDedicatedUriSize)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }
        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.wellknown);
        this.mstnfcndefrecord[recordId].setMtypelength(1);
        byte [] uriType = {(byte)0x55};
        this.mstnfcndefrecord[recordId].setMtype(uriType);
        this.mstnfcndefrecord[recordId].setmIDLength(0);

        byte[] uriBytes = uri.getBytes(utfEncoding);
        // Build the RTD Text buffer
        byte[] data = new byte[1 +  uriBytes.length];

        data[0] = (byte) _id;
        // - payload
        System.arraycopy(uriBytes, 0, data, 1, uriBytes.length);

        this.mstnfcndefrecord[recordId].setMpayloadlength(data.length);
        this.mstnfcndefrecord[recordId].setMpayload(data);

        this.mnbndefrecord = 1;

        buffer = serialize();

    }

    public void setNdefBTHandover(byte [] payload)
    {
        setNdefBTHandover( payload,0);
    }

    public void setNdefBTHandover(byte [] payload, int recordId)
    {


        final String bttype = "application/vnd.bluetooth.ep.oob";

        if ((payload == null) || (payload.length==0))
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }
        // create a new record

        this.mstnfcndefrecord[recordId] = new stndefrecord();

        Charset utfEncoding =Charset.forName("UTF-8") ;

        // evaluate bt payload size
        int maxDedicatedShortMessage =  255;

        // evaluate text payload size

        if ( payload.length > maxDedicatedShortMessage)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }
        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.media);


        this.mstnfcndefrecord[recordId].setMtypelength(bttype.getBytes().length);
        byte[] textType = null;
        try {
            textType = bttype.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,"Unable to convert BT type in byte UTF8");
            e.printStackTrace();
        }
        this.mstnfcndefrecord[recordId].setMtype(textType);
        this.mstnfcndefrecord[recordId].setmIDLength(0);
        this.mstnfcndefrecord[recordId].setMpayloadlength(payload.length);
        this.mstnfcndefrecord[recordId].setMpayload(payload);

        this.mnbndefrecord = 1;

        buffer = serialize();

    }

    public void setNdefWiFiHandover(byte [] payload)
    {
        setNdefWiFiHandover( payload,0);
    }

    public void setNdefWiFiHandover(byte [] payload, int recordId)
    {


        final String wifiType = "application/vnd.wfa.wsc";

        if ((payload == null) || (payload.length==0))
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }
        // create a new record

        this.mstnfcndefrecord[recordId] = new stndefrecord();

        Charset utfEncoding =Charset.forName("UTF-8") ;

        // evaluate bt payload size
        int maxDedicatedShortMessage =  255;

        // evaluate text payload size

        if ( payload.length > maxDedicatedShortMessage)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }
        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.media);


        this.mstnfcndefrecord[recordId].setMtypelength(wifiType.getBytes().length);
        byte[] textType = null;
        try {
            textType = wifiType.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,"Unable to convert Wifi type in byte UTF8");
            e.printStackTrace();
        }
        this.mstnfcndefrecord[recordId].setMtype(textType);
        this.mstnfcndefrecord[recordId].setmIDLength(0);
        this.mstnfcndefrecord[recordId].setMpayloadlength(payload.length);
        this.mstnfcndefrecord[recordId].setMpayload(payload);

        this.mnbndefrecord = 1;

        buffer = serialize();

    }

    public void setNdefRTDText(String _Text)
    {
        setNdefRTDText( _Text,0);
    }

    public void setNdefRTDText(String _Text, int recordId )
    {
        boolean utf8Enc = true; // declared for futur use

        if ((_Text == null) || (_Text.length()==0))
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }

        // create a new record

        this.mstnfcndefrecord[recordId] = new stndefrecord();

        byte[] langBytes = Locale.getDefault().getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = utf8Enc ? Charset.forName("UTF-8") : Charset.forName("UTF-16");

        // evaluate text payload size
        int maxDedicatedTextSize =  255 - 1 - langBytes.length;

        if ( _Text.length() > maxDedicatedTextSize)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }


        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.wellknown);
        this.mstnfcndefrecord[recordId].setMtypelength(1);
        byte [] textType = {(byte)0x54};
        this.mstnfcndefrecord[recordId].setMtype(textType);
        this.mstnfcndefrecord[recordId].setmIDLength(0);

        byte[] textBytes = _Text.getBytes(utfEncoding);
        // Build the RTD Text buffer
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        // - Status byte
        int utfBit = utf8Enc ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        data[0] = (byte) status;
        // - ISO/IANA language code
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        // - payload
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        this.mstnfcndefrecord[recordId].setMpayloadlength(data.length);
        this.mstnfcndefrecord[recordId].setMpayload(data);

        this.mnbndefrecord = 1;

        buffer = serialize();
    }


    public ndefError parseNdefBufferIncludingOffset(short offset)
    {
        return parseNdefBuffer(offset);
    }

    public ndefError parseNdefBuffer(short offset)
    {
        short curIndexBuff = offset; // currentIndexBuffer in parsing. Start after NDEF size stored at the begining of the buffer

        byte recordCtx;
        int bufferLength;
        int recordId = 0;
        boolean ParseComplete = false;

        if (offset !=0 && offset !=2)
            return ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;

        if (buffer == null || buffer.length==0)
            return ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;

        while (!ParseComplete)
        {
        try {

        this.mstnfcndefrecord[recordId] = new stndefrecord();

        recordCtx = (byte) (buffer[curIndexBuff] &0xFF);
        byte tmp = (byte) (recordCtx & 0x80);

        //mstnfcndefrecord[recordId].mMBFlag =(recordCtx>>7==0)?false:true;
        //mstnfcndefrecord[recordId].mMBFlag =(tmp==0)?false:true;
        mstnfcndefrecord[recordId].mMBFlag =(tmp==0x80)?false:true;

        mstnfcndefrecord[recordId].mMEFlag =(((recordCtx>>6)&0x01)==0)?false:true;
        mstnfcndefrecord[recordId].mCFFlag =(((recordCtx>>5)&0x01)==0)?false:true;
        mstnfcndefrecord[recordId].mSRFlag =(((recordCtx>>4)&0x01)==0)?false:true;
        mstnfcndefrecord[recordId].mILFlag =(((recordCtx>>3)&0x01)==0)?false:true;


        switch (recordCtx&0x07)
        {
        case 0x00:
            mstnfcndefrecord[recordId].mTNF = tnf.empty;
            break;
        case 0x01:
            mstnfcndefrecord[recordId].mTNF = tnf.wellknown;
            break;
        case 0x02:
            mstnfcndefrecord[recordId].mTNF = tnf.media;
            break;
        case 0x03:
            mstnfcndefrecord[recordId].mTNF = tnf.uri;
            break;
        case 0x04:
            mstnfcndefrecord[recordId].mTNF = tnf.external;
            break;
        case 0x05:
            mstnfcndefrecord[recordId].mTNF = tnf.unknow;
            break;
        case 0x06:
            mstnfcndefrecord[recordId].mTNF = tnf.unchanged;
            break;
        case 0x07:
            mstnfcndefrecord[recordId].mTNF = tnf.rfu;
            mstnfcndefrecord[recordId].status = stndefrecstatus.STATE_NDEFREC_TNF_RFU_USED;
            break;
        default:
            mstnfcndefrecord[recordId].mTNF = tnf.rfu;
            mstnfcndefrecord[recordId].status = stndefrecstatus.STATE_NDEFREC_TNF_RFU_USED;
            break;
        }


        mstnfcndefrecord[recordId].mtypelength  =  (int)(buffer[++curIndexBuff]&0xFF);
        mstnfcndefrecord[recordId].checkTypeLength(); // - not yet implemented - fct call to check typelength validity.

        if (mstnfcndefrecord[recordId].mSRFlag)
        {
            if (curIndexBuff<=buffer.length)
            mstnfcndefrecord[recordId].mpayloadlength = (buffer[++curIndexBuff]&0xFF);
        }
        else
        {
            //mstnfcndefrecord[recordId].mpayloadlength = ((int)(buffer[++curIndexBuff]&0xFF))<<24 + ((int)(buffer[++curIndexBuff]&0xFF))<<16 + ((int)(buffer[++curIndexBuff]&0xFF))<<8 + ((int)(buffer[++curIndexBuff]&0xFF))  ;
            //mstnfcndefrecord[recordId].mpayloadlength  = (((int)buffer[++curIndexBuff]&0xFF)<<24) + (((int)buffer[++curIndexBuff]<<16)) + (((int)buffer[++curIndexBuff])<<8) + (((int)buffer[++curIndexBuff]));
            for (int i=0;i<4;i++)
            {
                mstnfcndefrecord[recordId].mpayloadlength<<=8;
                mstnfcndefrecord[recordId].mpayloadlength += (buffer[++curIndexBuff]&0xFF);

            }
        }

        if (mstnfcndefrecord[recordId].mILFlag)
            mstnfcndefrecord[recordId].mIDLength = (int)(buffer[++curIndexBuff]&0xFF);
        else
            mstnfcndefrecord[recordId].mIDLength = 0;

        if (mstnfcndefrecord[recordId].mtypelength != 0)
        {
            mstnfcndefrecord[recordId].mtype = new byte[mstnfcndefrecord[recordId].mtypelength];
            System.arraycopy(buffer, ++curIndexBuff, mstnfcndefrecord[recordId].mtype, 0, mstnfcndefrecord[recordId].mtypelength);
            curIndexBuff+=mstnfcndefrecord[recordId].mtypelength-1;
        }
        else
        {
            mstnfcndefrecord[recordId].mtype = null;
        }

        if (mstnfcndefrecord[recordId].mIDLength != 0)
        {
            mstnfcndefrecord[recordId].mid = new byte[mstnfcndefrecord[recordId].mIDLength];
            System.arraycopy(buffer, ++curIndexBuff, mstnfcndefrecord[recordId].mid, 0, mstnfcndefrecord[recordId].mIDLength);
            curIndexBuff+=mstnfcndefrecord[recordId].mIDLength-1;
        }
        else
        {
            mstnfcndefrecord[recordId].mid = null;
        }

        // FBE to check control allocation
        if (mstnfcndefrecord[recordId].mpayloadlength != 0 && mstnfcndefrecord[recordId].mpayloadlength < (buffer.length - curIndexBuff))
        {
            mstnfcndefrecord[recordId].mpayload = new byte[mstnfcndefrecord[recordId].mpayloadlength];
            System.arraycopy(buffer, ++curIndexBuff, mstnfcndefrecord[recordId].mpayload, 0, mstnfcndefrecord[recordId].mpayloadlength);
            curIndexBuff+=mstnfcndefrecord[recordId].mpayloadlength-1;
        }
        else
        {
            mstnfcndefrecord[recordId].mpayload = null;
        }
        } catch (Exception e){
            Log.e(this.getClass().getName(), "parseNdefBuffer error: malformed NDEF HEADER");
            mstnfcndefrecord[recordId].status = stndefrecstatus.STATE_NDEFREC_DATA_MISSED;
            status = ndefError.ERR_NDEF_HEADER_CORRUPTED;
            return ndefError.ERR_NDEF_HEADER_CORRUPTED;
        }
        bufferLength = buffer.length;    // to check buffer length in debug mode
        if (bufferLength != (curIndexBuff+1) && (recordId < (MAX_SUPPORTED_NDEF_RECORD_NB-1)))
        {
            mstnfcndefrecord[recordId].status = stndefrecstatus.STATE_NDEFREC_OK;
            // we may have some other Ndef Records following the current parsed record
            recordId++;
            curIndexBuff++;
            //mstnfcndefrecord[recordId].status = stndefrecstatus.STATE_NDEFREC_DATA_MISSED;
            //status = ndefError.ERR_NDEF_PARSING;
            //return ndefError.ERR_NDEF_PARSING;
        }
        else
        {
            ParseComplete = true;
            mstnfcndefrecord[recordId].status = stndefrecstatus.STATE_NDEFREC_OK;
            status = ndefError.ERR_NDEF_OK;;
            mnbndefrecord = recordId+1;
            return ndefError.ERR_NDEF_OK;
        }
        }
        if (ParseComplete)
        {
            ParseComplete = true;
            mstnfcndefrecord[recordId].status = stndefrecstatus.STATE_NDEFREC_OK;
            status = ndefError.ERR_NDEF_OK;;
            mnbndefrecord = recordId;
            return ndefError.ERR_NDEF_OK;
        }
        else
        {
            mstnfcndefrecord[recordId].status = stndefrecstatus.STATE_NDEFREC_DATA_MISSED;
            status = ndefError.ERR_NDEF_PARSING;
            return ndefError.ERR_NDEF_PARSING;
        }
    }


    public boolean isamessagebegin(int messageNB)
    {  return mstnfcndefrecord[messageNB].isamessagebegin();}

    public boolean isamessageend(int messageNB)
    {  return mstnfcndefrecord[messageNB].isamessageend();}

    public boolean isachunkedmessage(int messageNB)
    {  return mstnfcndefrecord[messageNB].isachunkedmessage();}

    public boolean isashortrecord(int messageNB)
    {  return mstnfcndefrecord[messageNB].isashortrecord();}

    public boolean isIDpresent(int messageNB)
    {  return mstnfcndefrecord[messageNB].isIDpresent();}

    public tnf gettnf(int messageNB)
    {  return mstnfcndefrecord[messageNB].gettnf();}

    public String getTNFtoString(int messageNB)
    {
         return mstnfcndefrecord[messageNB].getTNFtoString();
    }
    public int gettypelength(int messageNB)
    { return mstnfcndefrecord[messageNB].gettypelength();}

    public int getpayloadlength(int messageNB)
    {  return mstnfcndefrecord[messageNB].getpayloadlength();}

    public int getIDlength(int messageNB)
    {  return mstnfcndefrecord[messageNB].getIDlength();}

    public byte [] getID(int messageNB)
    {
        return mstnfcndefrecord[messageNB].getID();
    }

    public byte[] gettype(int messageNB)
    {
        return mstnfcndefrecord[messageNB].gettype();
    }

    public byte[] getpayload(int messageNB)
    {
        return mstnfcndefrecord[messageNB].getpayload();
    }


    public byte [] serialize()
    {
        byte[][] NDEFSerializedTab = new byte [MAX_SUPPORTED_NDEF_RECORD_NB][] ;
        byte[] NDEFSerializedbuffer = null;
        int NDEFSerializedBufferSize = 0;

        for (int i = 0;i<this.mnbndefrecord; i++)
        {
            NDEFSerializedTab[i] = mstnfcndefrecord[i].serialize();
            NDEFSerializedBufferSize += NDEFSerializedTab[i].length;
        }
        NDEFSerializedbuffer  = new byte[NDEFSerializedBufferSize];
        int NDEFSerializedbufferOffset = 0;
        for (int i = 0;i<this.mnbndefrecord; i++)
        {
            System.arraycopy(NDEFSerializedTab[i], 0, NDEFSerializedbuffer, NDEFSerializedbufferOffset, NDEFSerializedTab[i].length);
            NDEFSerializedbufferOffset+=NDEFSerializedTab[i].length;
        }
        return NDEFSerializedbuffer;
    }


    public String getPayloadtoHex(int messageNB)
    {
        String emptyString="Payload not available for the requested NDEF message "+ String.valueOf(messageNB);

        if (messageNB>this.mnbndefrecord)
            return emptyString;
        if (this.mstnfcndefrecord[messageNB].mpayload == null)
            return emptyString;
        return stnfchelper.bytArrayToHex(this.mstnfcndefrecord[messageNB].mpayload);
    }

    public byte []  getPayloadtoByte (int messageNB)
    {
        if (messageNB > this.mnbndefrecord)
        {
            return null;
        }
        return this.mstnfcndefrecord[messageNB].mpayload;

    }

    public ndefError getStatus()
    {
        return status;
    }

    public int getNLEN() {
        return buffer.length;
    }
    public int getFilesNb() {
        return MAX_SUPPORTED_NDEF_MESSAGE_NB;
    }

    // Wrong APU messageNB unused. To be removed.
    public int getRecordNb(int messageNB) {

        return (this.mnbndefrecord);
    }

    public int getRecordNb() {

        return (this.mnbndefrecord);
    }

    public NdefMessage getNdefMessage() {
        int nbrec = 0;
        NdefRecord[] ndefRecordArray = new NdefRecord[MAX_SUPPORTED_NDEF_RECORD_NB];
        short tnf = NdefRecord.TNF_UNKNOWN;
        // public NdefRecord (short tnf, byte[] type, byte[] id, byte[] payload)
        if ((mstnfcndefrecord == null) || (mstnfcndefrecord.length == 0))
            return null;
        else {
            for (int i = 0; i < mstnfcndefrecord.length; i++) {
                if (mstnfcndefrecord[i] != null) {
                    nbrec++;
                    switch (mstnfcndefrecord[i].gettnf()) {
                        case empty:
                            tnf = NdefRecord.TNF_EMPTY;
                            break;
                        case wellknown:
                            tnf = NdefRecord.TNF_WELL_KNOWN;
                            break;
                        case media:
                            tnf = NdefRecord.TNF_MIME_MEDIA;
                            break;
                        case uri:
                            tnf = NdefRecord.TNF_ABSOLUTE_URI;
                            break;
                        case external:
                            tnf = NdefRecord.TNF_EXTERNAL_TYPE;
                            break;
                        case unknow:
                            tnf = NdefRecord.TNF_UNKNOWN;
                            break;
                        case unchanged:
                            tnf = NdefRecord.TNF_UNCHANGED;
                            break;
                        case rfu:
                            tnf = NdefRecord.TNF_UNKNOWN;
                            break;
                        default:
                            tnf = NdefRecord.TNF_UNKNOWN;
                            break;
                    }
                    try {
                        ndefRecordArray[i] = new NdefRecord(tnf, mstnfcndefrecord[i].gettype(), mstnfcndefrecord[i].getID(), mstnfcndefrecord[i].getpayload());
                    } catch (Exception e) {
                        // need to understand why .....
                        tnf = NdefRecord.TNF_UNKNOWN;
                        ndefRecordArray[i] = new NdefRecord(tnf, mstnfcndefrecord[i].gettype(), mstnfcndefrecord[i].getID(), mstnfcndefrecord[i].getpayload());
                        Log.e(TAG,"Unable to create record - Create TNF_UNKNOWN for compatibility.....");
                        e.printStackTrace();
                    }
                }
            }

        }
        if (nbrec > 0 && nbrec < MAX_SUPPORTED_NDEF_RECORD_NB) {
            NdefRecord[] ndefRecordfound = new NdefRecord[nbrec];
            for (int ii = 0; ii < nbrec; ii++) ndefRecordfound[ii] = ndefRecordArray[ii];
            return new NdefMessage(ndefRecordfound);
        } else {
            return new NdefMessage(ndefRecordArray);
        }


    }

    public void setNdefProprietaryGenCtrlMsg(byte[] CandyPayload)
    {
        setNdefProprietaryGenCtrlMsg( CandyPayload,0);
    }
    public void setNdefProprietaryGenCtrlMsg(byte [] payload, int recordId)
    {

        final String exttype = "st.com:ctrl";
        if ((payload == null) || (payload.length==0))
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }
        // create a new record
        this.mstnfcndefrecord[recordId] = new stndefrecord();

        // evaluate text payload size

        if ( payload.length > 255)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }

        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.external);


        this.mstnfcndefrecord[recordId].setMtypelength(exttype.length());

        byte[] textType = null;
        try {
            textType = exttype.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,"Unable to convert external type in byte UTF8");
            e.printStackTrace();
        }

        this.mstnfcndefrecord[recordId].setMtype(textType);
        this.mstnfcndefrecord[recordId].setmIDLength(0);

        byte [] data = new byte[payload.length];
        System.arraycopy(payload, 0, data, 0, payload.length);

        this.mstnfcndefrecord[recordId].setMpayloadlength(data.length);
        this.mstnfcndefrecord[recordId].setMpayload(data);

        this.mnbndefrecord = 1;

        buffer = serialize();

    }
    public void setNdefProprietaryGenTransCtrlMsg(byte[] CandyPayload)
    {
        setNdefProprietaryCandyTranspCtrlMsg( CandyPayload,0);
    }
    public void setNdefProprietaryCandyTranspCtrlMsg(byte [] payload, int recordId)
    {

        //System.arraycopy(buffer, 0, payload, 0, payload.length);

/*        if (payload == null || payload.length == 0)
            status = ndefError.ERR_NDEF_UNKNOWN;
*/        if ((payload == null) || (payload.length==0))
        {
             // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }
            this.mstnfcndefrecord[recordId] = new stndefrecord();
            if ( payload.length > 255)
            {
                this.mstnfcndefrecord[recordId].setmSRFlag(false);
            }
            else // Short record
            {
                this.mstnfcndefrecord[recordId].setmSRFlag(true);
            }

            // SR use case
            this.mstnfcndefrecord[recordId].setmMBFlag(true);
            this.mstnfcndefrecord[recordId].setmMEFlag(true);
            this.mstnfcndefrecord[recordId].setmCFFlag(false);
            this.mstnfcndefrecord[recordId].setmILFlag(false);
            this.mstnfcndefrecord[recordId].setmTNF(tnf.external);

            final String exttype = "st.com:transp.ctrl";
            this.mstnfcndefrecord[recordId].setMtypelength(exttype.length());
            byte [] data = new byte[payload.length];
            System.arraycopy(payload, 0, data, 0, payload.length);

            this.mstnfcndefrecord[recordId].setMpayloadlength(data.length);
            this.mstnfcndefrecord[recordId].setMpayload(data);

            this.mnbndefrecord = 1;
            this.mstnfcndefrecord[recordId].setmMBTransparent(true);

        buffer = serialize();

    }

    public void setNdefBTLe(byte [] payload)
    {
        setNdefBTLe( payload,0);
    }

    public void setNdefBTLe(byte [] payload, int recordId)
    {


        final String bttype = "application/vnd.bluetooth.le.oob";

        if ((payload == null) || (payload.length==0))
        {
            // We may need to reset() this;
            status = ndefError.ERR_NDEF_UNINTIALIZED_BUFFER;
            return;
        }
        // create a new record

        this.mstnfcndefrecord[recordId] = new stndefrecord();

        Charset utfEncoding =Charset.forName("UTF-8") ;

        // evaluate bt payload size
        int maxDedicatedShortMessage =  255;

        // evaluate text payload size

        if ( payload.length > maxDedicatedShortMessage)
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(false);
        }
        else // Short record
        {
            this.mstnfcndefrecord[recordId].setmSRFlag(true);
        }
        // SR use case
        this.mstnfcndefrecord[recordId].setmMBFlag(true);
        this.mstnfcndefrecord[recordId].setmMEFlag(true);
        this.mstnfcndefrecord[recordId].setmCFFlag(false);
        this.mstnfcndefrecord[recordId].setmILFlag(false);
        this.mstnfcndefrecord[recordId].setmTNF(tnf.media);


        this.mstnfcndefrecord[recordId].setMtypelength(bttype.getBytes().length);
        byte[] textType = null;
        try {
            textType = bttype.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,"Unable to convert BTLe type in byte UTF8");
            e.printStackTrace();
        }
        this.mstnfcndefrecord[recordId].setMtype(textType);
        this.mstnfcndefrecord[recordId].setmIDLength(0);
        this.mstnfcndefrecord[recordId].setMpayloadlength(payload.length);
        this.mstnfcndefrecord[recordId].setMpayload(payload);

        this.mnbndefrecord = 1;

        buffer = serialize();

    }


}
