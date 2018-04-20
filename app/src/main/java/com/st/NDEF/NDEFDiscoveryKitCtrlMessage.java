/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1616 $
  * Revision of last commit    :  $Rev: 1616 $
  * Date of last commit     :  $Date: 2016-02-03 19:03:03 +0100 (Wed, 03 Feb 2016) $
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
import java.util.Arrays;

import com.st.NFC.NFCApplication;
import com.st.demo.R;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;



public class NDEFDiscoveryKitCtrlMessage extends NDEFSimplifiedMessage {

    /**
     * Defines
     */

    final static String TAG = "NDEFDiscoveryKitCtrlMessage";

    public enum ledblinkspeed{
        OFF,
        LOW,
        MEDIUM,
        HIGH
    };

    public final static int FIXEDNBLED = 4;
    public final static int FIXEDNBTOKENS = 8;

    /**
     * Attributes
     */
    private boolean [] _ledInitialState;
    private boolean [] _ledBlinkState;
    private ledblinkspeed _ledBlinkSpeed;

    public class ctrlTockens
    {
        public final static int FIXEDTOKENSIZE = 20; // (M24SRDiscovery Kit limitation)

        private String _label;
        private String _value;         // value is adapted to fit to FIXEDTOKENSIZE (M24SRDiscovery Kit limitation)
        private short _fontColor;    // from 0x0000 to 0xFFFF
        private short _backGrColor;

        public ctrlTockens()
        {

        }

        public String get_label() {
            return _label;
        }
        public void set_label(String _label) {
            this._label = _label;
        }
        public String get_value() {
            return _value;
        }
        public void set_value(String _value) {
            this._value = _value;
        }
        public short get_fontColor() {
            return _fontColor;
        }
        public void set_fontColor(short _fontColor) {
            this._fontColor = _fontColor;
        }
        public short get_backGrColor() {
            return _backGrColor;
        }
        public void set_backGrColor(short _backGrColor) {
            this._backGrColor = _backGrColor;
        }

    }

    private ctrlTockens [] ctrlTockensArray;


    /**
     * Methods
     */
    // Constructors
    public NDEFDiscoveryKitCtrlMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_EXT_M24SRDISCOCTRL);
        // Default values
        _ledInitialState = new boolean[FIXEDNBLED];
        for (int i=0; i<FIXEDNBLED;i++) _ledInitialState[i] = false;
        _ledBlinkState = new boolean[FIXEDNBLED];
        for (int i=0; i<FIXEDNBLED;i++) _ledBlinkState[i] = false;
        _ledBlinkSpeed = ledblinkspeed.OFF;
        ctrlTockensArray = new ctrlTockens[FIXEDNBTOKENS];
        for (int i=0;i<this.getFixednbtokens();i++)
        {
            ctrlTockensArray[i] = new ctrlTockens();
        }
    }

    public boolean[] get_ledInitialState() {
        return _ledInitialState;
    }

    public boolean get_ledInitialState(int ID) {
        return _ledInitialState[ID];
    }



    public void set_ledInitialState(boolean[] _ledInitialState) {
        this._ledInitialState = _ledInitialState;
    }

    public void set_ledInitialState(int ID, boolean state) {
        this._ledInitialState[ID] = state;
    }


    public boolean[] get_ledBlinkState() {
        return _ledBlinkState;
    }

    public boolean get_ledBlinkState(int ID) {
        return _ledBlinkState[ID];
    }



    public void set_ledBlinkState(boolean[] _ledBlinkState) {
        this._ledBlinkState = _ledBlinkState;
    }

    public void set_ledBlinkState(int ID, boolean state) {
        this._ledBlinkState[ID] = state;
    }


    public ledblinkspeed get_ledBlinkSpeed() {
        return _ledBlinkSpeed;
    }


    public void set_ledBlinkSpeed(ledblinkspeed _ledBlinkSpeed) {
        this._ledBlinkSpeed = _ledBlinkSpeed;
    }


    public ctrlTockens[] getCtrlTockensArray() {
        return ctrlTockensArray;
    }

    public ctrlTockens getCtrlTocken(int ID) {
        if (ID < FIXEDNBTOKENS)
            return ctrlTockensArray[ID];
        else
            return null;
    }


    public void setCtrlTockensArray(ctrlTockens[] ctrlTockensArray) {
        this.ctrlTockensArray = ctrlTockensArray;
    }

    public void setCtrlTockensArray(int ID, String label, String value, short bckColor,short fontColor) {
        this.ctrlTockensArray[ID].set_backGrColor(bckColor);
        this.ctrlTockensArray[ID].set_fontColor(fontColor);
        this.ctrlTockensArray[ID].set_label(label);
        this.ctrlTockensArray[ID].set_value(value);
    }

    public void setCtrlTocken(ctrlTockens[] ctrlTockensArray) {
        this.ctrlTockensArray = ctrlTockensArray;
    }


    public static int getFixednbled() {
        return FIXEDNBLED;
    }

    public static int getFixednbtokens() {
        return FIXEDNBTOKENS;
    }

    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {
        return ( (mTnf == tnf.external) && (Arrays.equals(rtdType,"st.com:m24sr_discovery_democtrl".getBytes())));
    }

    public void parseAndSetNDEFMessage(byte [] payload)
    {
        if ((payload != null) || (payload.length==0))
        {
            // record Led State
                for (int i = 0; i<FIXEDNBLED; i++)
                {
                    if (payload[i]== (byte)0x01)
                    {
                        _ledInitialState[i] = true;
                        _ledBlinkState[i] = false;
                    }
                    else if (payload[i]== (byte)0x02)
                    {
                        _ledInitialState[i] = false;
                        _ledBlinkState[i] = true;
                    }
                    else if (payload[i]== (byte)0x03)
                    {
                        _ledInitialState[i] = true;
                        _ledBlinkState[i] = true;
                    }
                    else //  if (payload[i]== (byte)0x00) or all other cases
                    {
                        _ledInitialState[i] = false;
                        _ledBlinkState[i] = false;
                    }
                }
                // record Blink speed
                if (payload[FIXEDNBLED] == (byte) 0x01)
                {
                    _ledBlinkSpeed = ledblinkspeed.LOW;
                }
                else if (payload[FIXEDNBLED] == (byte) 0x02)
                {
                    _ledBlinkSpeed = ledblinkspeed.MEDIUM;
                }
                else if (payload[FIXEDNBLED] == (byte) 0x03)
                {
                    _ledBlinkSpeed = ledblinkspeed.HIGH;
                }
                else // if (payload[FIXEDNBLED] == (byte) 0x00) or other values
                {
                    _ledBlinkSpeed = ledblinkspeed.OFF;
                }

                // record Tokens
                int currentoffset = 5;
                for(int i=0; i<FIXEDNBTOKENS ; i++)
                {
                    ctrlTockensArray[(int)payload[currentoffset]-1].set_backGrColor((short) (payload[currentoffset+1]<<8+ payload[currentoffset+2] ));
                    ctrlTockensArray[(int)payload[currentoffset]-1].set_fontColor((short) (payload[currentoffset+3]<<8+ payload[currentoffset+4] ));
                    byte []assciitab = new byte[20];
                    System.arraycopy( payload, currentoffset+5, assciitab, 0, 20);
                    String asciiString =  new String(assciitab);
                    String[] parts = asciiString.split(":");

                    ctrlTockensArray[(int)payload[currentoffset]-1].set_label(parts[0]);
                    if (parts.length>1)
                        ctrlTockensArray[(int)payload[currentoffset]-1].set_value(parts[1]);
                    currentoffset = currentoffset + 5 +ctrlTockens.FIXEDTOKENSIZE;
                }
            }
            else
            {
                Log.d(TAG,"Can't set NDEF message from payload");
            }
    }

    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {
        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {
            byte [] payload = ndefHandler.getpayload(0);

            // record Led State
            for (int i = 0; i<FIXEDNBLED; i++)
            {
                if (payload[i]== (byte)0x01)
                {
                    _ledInitialState[i] = true;
                    _ledBlinkState[i] = false;
                }
                else if (payload[i]== (byte)0x02)
                {
                    _ledInitialState[i] = false;
                    _ledBlinkState[i] = true;
                }
                else if (payload[i]== (byte)0x03)
                {
                    _ledInitialState[i] = true;
                    _ledBlinkState[i] = true;
                }
                else //  if (payload[i]== (byte)0x00) or all other cases
                {
                    _ledInitialState[i] = false;
                    _ledBlinkState[i] = false;
                }
            }
            // record Blink speed
            if (payload[FIXEDNBLED] == (byte) 0x01)
            {
                _ledBlinkSpeed = ledblinkspeed.LOW;
            }
            else if (payload[FIXEDNBLED] == (byte) 0x02)
            {
                _ledBlinkSpeed = ledblinkspeed.MEDIUM;
            }
            else if (payload[FIXEDNBLED] == (byte) 0x03)
            {
                _ledBlinkSpeed = ledblinkspeed.HIGH;
            }
            else // if (payload[FIXEDNBLED] == (byte) 0x00) or other values
            {
                _ledBlinkSpeed = ledblinkspeed.OFF;
            }

            // record Tokens
            int currentoffset = 5;
            for(int i=0; i<FIXEDNBTOKENS ; i++)
            {
                ctrlTockensArray[(int)payload[currentoffset]-1].set_backGrColor((short) (payload[currentoffset+1]<<8+ payload[currentoffset+2] ));
                ctrlTockensArray[(int)payload[currentoffset]-1].set_fontColor((short) (payload[currentoffset+3]<<8+ payload[currentoffset+4] ));
                byte []assciitab = new byte[20];
                System.arraycopy( payload, currentoffset+5, assciitab, 0, 20);
                String asciiString =  new String(assciitab);
                String[] parts = asciiString.split(":");

                ctrlTockensArray[(int)payload[currentoffset]-1].set_label(parts[0]);
                if (parts.length>1)
                    ctrlTockensArray[(int)payload[currentoffset]-1].set_value(parts[1]);
                currentoffset = currentoffset + 5 +ctrlTockens.FIXEDTOKENSIZE;
            }
        }
        else
        {
            Log.d(TAG,"Can't set NDEF message from ST ndefHandler object");
        }
    }

    public byte[] serializeNDEFMessage()
    {
        byte [] payload = new byte[FIXEDNBLED+1+FIXEDNBTOKENS*(5+ctrlTockens.FIXEDTOKENSIZE)];

        for (int i=0; i<FIXEDNBLED; i++)
        {
            payload[i] = (_ledInitialState[i]==true)?(byte)0x01:(byte)0x00;
            payload[i] += (_ledBlinkState[i]==true)?(byte)0x02:(byte)0x00;
        }

        switch (_ledBlinkSpeed){
            case LOW:{
                payload[FIXEDNBLED]=(byte)0x01;
                break;
            }
            case MEDIUM:{
                payload[FIXEDNBLED]=(byte)0x02;
                break;
            }
            case HIGH:{
                payload[FIXEDNBLED]=(byte)0x03;
                break;
            }
            default:{
                payload[FIXEDNBLED]=(byte)0x00;
            }
        }
        // Now treat Tokens and values.
        int currentoffset = FIXEDNBLED;
        for (int i = 0;i<FIXEDNBTOKENS;i++)
        {
            payload[currentoffset+1] = (byte)(i+1); // store current LCD ID
            payload[currentoffset+2] = (byte) (((short)ctrlTockensArray[i].get_backGrColor()&0xFF00)>>8);
            payload[currentoffset+3] = (byte) (ctrlTockensArray[i].get_backGrColor()&0xFF);
            payload[currentoffset+4] = (byte) (((ctrlTockensArray[i].get_fontColor()&0xFF00)>>8));
            payload[currentoffset+5] = (byte) (ctrlTockensArray[i].get_fontColor()&0xFF);

            String assciiString = ctrlTockensArray[i].get_label();
            String separator="";
            if (!assciiString.isEmpty())
            {
                separator+=":";
            }
            if (!ctrlTockensArray[i].get_label().isEmpty())
                assciiString+=separator+ctrlTockensArray[i].get_value();

            if (assciiString.length()>20)
                assciiString = assciiString.substring(0, 20);
            else if (assciiString.length()<20)
            {
                while (assciiString.length() != 20)
                assciiString += " ";
            }

            byte[] bytesarray = null;
            try {
                bytesarray = assciiString.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            if (bytesarray != null)
            {
                System.arraycopy(bytesarray, 0, payload, currentoffset+6, bytesarray.length);
            }
            else
            {
                Log.d("TAG","Serial Token "+i+" error");
            }
            currentoffset+= 5+ctrlTockens.FIXEDTOKENSIZE;
        }

        return payload;
    }


    public void defaultValue()
    {

        set_ledBlinkSpeed(NDEFDiscoveryKitCtrlMessage.ledblinkspeed.HIGH);
        set_ledBlinkState(0, true);
        set_ledBlinkState(1, true);
        set_ledBlinkState(2, true);
        set_ledBlinkState(3, true);
        set_ledInitialState(0, true);
        set_ledInitialState(1, true);
        set_ledInitialState(2, true);
        set_ledInitialState(3, true);

        String [] labelsArray={
                    NFCApplication.getContext().getResources().getString(R.string.labelline1),
                    NFCApplication.getContext().getResources().getString(R.string.labelline2),
                    NFCApplication.getContext().getResources().getString(R.string.labelline3),
                    NFCApplication.getContext().getResources().getString(R.string.labelline4),
                    NFCApplication.getContext().getResources().getString(R.string.labelline5),
                    NFCApplication.getContext().getResources().getString(R.string.labelline6),
                    NFCApplication.getContext().getResources().getString(R.string.labelline7),
                    NFCApplication.getContext().getResources().getString(R.string.labelline8)
                };

        for (int i=0;i<getFixednbtokens();i++)
        {
            ctrlTockensArray[i].set_label(labelsArray[i]);
            ctrlTockensArray[i].set_backGrColor((short)0XFFFF);
            ctrlTockensArray[i].set_fontColor((short)0x0000);
        }


    }
    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;
        byte [] payload = this.serializeNDEFMessage();

        // Check if there is a valid text
        if (payload == null) {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input M24Discovery Ctrl Message");
        } else {
            NdefRecord extRecord =  NdefRecord.createExternal("st.com","m24sr_discovery_democtrl",payload);

            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {extRecord};
            msg = new NdefMessage(records);
        }
        return msg;
    }

    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage(String NDEFTextType) {
        NdefMessage msg = null;
        byte [] payload = this.serializeNDEFMessage();

        // Check if there is a valid text
        if (payload == null) {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input M24Discovery Ctrl Message");
        } else {
            NdefRecord extRecord =  NdefRecord.createExternal("st.com",NDEFTextType,payload);
            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {extRecord};
            msg = new NdefMessage(records);
        }
        return msg;
    }

    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {
        // update ST NDEF message object
        ndefHandler.setNdefProprietaryExtm24srDiscoveryCtrlMsg(this.serializeNDEFMessage());

    }

}
