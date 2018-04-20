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


import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.st.NFC.NFCApplication;
import com.st.NFC.stnfchelper;
import com.st.demo.R;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;


//Implements NFC Forum RTD URI NDEF message

enum NDEFDeviceClassCode {
    NDEF_RTD_CLASS_PRINTER,
    NDEF_RTD_CLASS_CAMERA,
    NDEF_RTD_CLASS_SMARTPHONE,
    NDEF_RTD_CLASS_HEADSET,
    NDEF_RTD_CLASS_UNDEF,
}

public class NDEFBTHandoverMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    private String TAG = this.getClass().getName();

    // record #0

    // Record #1
    private NDEFDeviceClassCode _deviceClassCode;
    private byte [] _macAddr;
    private String _macAddrStr;
    private byte [] _deviceClass;
    private String _deviceName;
    private byte _UUIDClassList;
    private byte [] _UUIDClassBuff;
    private byte [] _payload;

    private final static LinkedHashMap<String, NDEFDeviceClassCode> SupportedDeviceClassCodesList =
        new LinkedHashMap<String, NDEFDeviceClassCode>() {{
            // 0 0x00 N/A. No prepending is done, and the URI field contains the unabridged URI.
            put("", NDEFDeviceClassCode.NDEF_RTD_CLASS_UNDEF);
            //1 0x01 http://www.
            put("Printer", NDEFDeviceClassCode.NDEF_RTD_CLASS_PRINTER);
            //2 0x02 https://www.
            put("Camera", NDEFDeviceClassCode.NDEF_RTD_CLASS_CAMERA);
            //3 0x03 http://
            put("Smartphone", NDEFDeviceClassCode.NDEF_RTD_CLASS_SMARTPHONE);
            //4 0x04 https://
            put("Headset", NDEFDeviceClassCode.NDEF_RTD_CLASS_HEADSET);
        }};

    /**
     * Methods
     */
    // Constructors
    public NDEFBTHandoverMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_BTHANDOVER);
        // Default values
        _deviceClassCode = NDEFDeviceClassCode.NDEF_RTD_CLASS_UNDEF; // not used for now
        _macAddr = null;
        _deviceName = null;
        _deviceClass = null;
        _UUIDClassBuff = null;
        _UUIDClassList = 0x00;
        _payload = null;

    }

    public NDEFBTHandoverMessage(NDEFDeviceClassCode deviceClassCode ,
                                 byte [] macAddr,
                                 String deviceName) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_BTHANDOVER);
        _deviceClassCode = deviceClassCode;
        _macAddr = macAddr.clone();
        _deviceName = deviceName;
        _deviceClass = null;
        _UUIDClassBuff = null;
        _UUIDClassList = 0x00;
        _payload = null;

    }

    public NDEFBTHandoverMessage(
                                     String deviceName,
                                     String macAddr) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_BTHANDOVER);
        _deviceClassCode = NDEFDeviceClassCode.NDEF_RTD_CLASS_UNDEF;
        try {
            _macAddrStr = macAddr.replaceAll(" ", ":");
            _macAddr = stnfchelper.hexStringToByteArray2(macAddr.replaceAll(":", ""));
        } catch(IllegalArgumentException ex) {
            Log.e(TAG, "_macAddr: " + ex.toString());
/*            int len = macAddr.length();
            if (len %2 != 0) {
                String evenMacAdrStr = macAddr + " ";
                byte[] tmpMacAdrByte = stnfchelper.hexStringToByteArray(evenMacAdrStr.replaceAll(":", ""));
                _macAddr = new byte[tmpMacAdrByte.length];
                System.arraycopy(tmpMacAdrByte,0,_macAddr,0,_macAddr.length);
            }*/
        }
        _deviceName = deviceName;
        _deviceClass = null;
        _UUIDClassBuff = null;
        _UUIDClassList = 0x00;

    }


    // Accessors
    public NDEFDeviceClassCode get_deviceClassCode() {
        return _deviceClassCode;
    }

    public void set_deviceClassCode(NDEFDeviceClassCode _deviceClassCode) {
        this._deviceClassCode = _deviceClassCode;
    }

    public byte [] get_macAddr() {
        return _macAddr;
    }

    public String get_stringmacAddr() {
        if (_macAddr == null) return "";
        else
        return stnfchelper.bytArrayToHex(_macAddr);
    }

    public String get_string2macAddr() {
        if (_macAddr == null) return "";
        else
            return _macAddrStr;
    }


    public void set_macAddr(byte [] _macAddr) {
        if (_macAddr!=null) this._macAddr = _macAddr.clone();
    }

    public String get_deviceName() {
        return _deviceName;
    }

    public void set_deviceName(String _deviceName) {
        this._deviceName = _deviceName;
    }


    public static ArrayList<String>       getSupportedDeviceClassCodesList()                      { return new ArrayList<String>(Collections.synchronizedSet(SupportedDeviceClassCodesList.keySet())); }
    public static NDEFDeviceClassCode     getDeviceClassCodeFromStr(String codeStr)               { return SupportedDeviceClassCodesList.get(codeStr); }
    public static int                     getURICodePositionInList(NDEFDeviceClassCode deviceClassCode)   { return deviceClassCode.ordinal(); }

    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {

        if ( (mTnf == tnf.media)
            &&
            (Arrays.equals(rtdType, NFCApplication.getContext().getString(R.string.mime_type_bt_handover).getBytes())) )
            return true;

        return false;
    }


       private void parseBtOob(ByteBuffer payload) {

            try {
                payload.position(2);
                byte[] address = new byte[6];
                payload.get(address);
                // ByteBuffer.order(LITTLE_ENDIAN) doesn't work for
                // ByteBuffer.get(byte[]), so manually swap order
                for (int i = 0; i < 3; i++) {
                    byte temp = address[i];
                    address[i] = address[5 - i];
                    address[5 - i] = temp;
                }
                _macAddr=address.clone();
                _deviceName = null;
                while (payload.remaining() > 0) {
                    byte[] nameBytes;

                    int len = payload.get();
                    int type = payload.get();
                    switch (type) {
                        case 0x08:  // short local name
                            nameBytes = new byte[len - 1];
                            payload.get(nameBytes);
                            _deviceName = new String(nameBytes, Charset.forName("UTF-8"));
                            break;
                        case 0x09:  // long local name
                            if (_deviceName != null) break;  // prefer short name
                            nameBytes = new byte[len - 1];
                            payload.get(nameBytes);
                            _deviceName = new String(nameBytes, Charset.forName("UTF-8"));
                            break;
                        case 0x0D: //Class of device - 3 Bytes with Service Class / Major Device Class / Minor Device Class
                            _deviceClass = new byte[3];
                            payload.get(_deviceClass);
                            break;
                        case 0x02: //16-bit un-complete Service Class UUID list
                            _UUIDClassList = (byte) 0x02;
                            _UUIDClassBuff = new byte[len - 1];
                            payload.get(_UUIDClassBuff);
                            break;
                        case 0x03: //16-bit complete Service Class UUID list
                            _UUIDClassList = (byte) 0x03;
                            _UUIDClassBuff = new byte[len - 1];
                            payload.get(_UUIDClassBuff);
                            break;
                        case 0x04: //32-bit un-complete Service Class UUID list
                            _UUIDClassList = (byte) 0x04;
                            _UUIDClassBuff = new byte[len - 1];
                            payload.get(_UUIDClassBuff);
                            break;
                        case 0x05: //32-bit complete Service Class UUID list
                            _UUIDClassList = (byte) 0x05;
                            _UUIDClassBuff = new byte[len - 1];
                            payload.get(_UUIDClassBuff);
                            break;
                        case 0x06: //128-bit un-complete Service Class UUID list
                            _UUIDClassList = (byte) 0x06;
                            _UUIDClassBuff = new byte[len - 1];
                            payload.get(_UUIDClassBuff);
                            break;
                        case 0x07: //128-bit complete Service Class UUID list
                            _UUIDClassList = (byte) 0x07;
                            _UUIDClassBuff = new byte[len - 1];
                            payload.get(_UUIDClassBuff);
                            break;
                        default:
                            payload.position(payload.position() + len - 1);
                            break;
                    }
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "BT OOB: invalid BT address");
            } catch (BufferUnderflowException e) {
                Log.e(TAG, "BT OOB: payload shorter than expected");
            }
        }



    public void setNDEFMessage(tnf mTnf, byte [] rtdType, stnfcndefhandler ndefHandler) {

        // workaround as stnfcndefhandler doesn't yet support multiple record.
          // Record #0  - Handover Select Record is managed in static way
          // Record #1  - BlueTooth Secure Simple Pairing record is managed by stnfcdefhandler

        // If the NDEF message type is recognized, decode the data as per the applicable spec...
        if (isSimplifiedMessage(mTnf, rtdType)) {
            byte [] payload = ndefHandler.getpayload(0); // OOB data Length / Mac Addr / Device Type / Data Type / Local Name
            ByteBuffer payloadbuffer = ByteBuffer.allocate(payload.length /*+ 8*/);
            payloadbuffer.put(payload);
            parseBtOob(payloadbuffer);
        }
    }

    private void serialize()
    {
        _payload=null;
        int oobOptionalDataLength = 2;
        byte [] macAddr = null;
        macAddr = new byte[_macAddr.length];
        // Switch MAC address order
        for (int i=0; i<_macAddr.length;i++)
        {
            macAddr[i] = _macAddr[_macAddr.length-i-1];
        }


        int EIRdatalengthSize = 1;
        int EIR_dataLocalTypeNameSize = 1;

        // EIR DATA NAME
        byte [] deviceNameEir = null;
        if ((_deviceName!=null) && (_deviceName.length() != 0))
        {
            deviceNameEir = new byte [ EIRdatalengthSize + EIR_dataLocalTypeNameSize + _deviceName.getBytes().length ];
            deviceNameEir[0] = (byte) ((EIR_dataLocalTypeNameSize + _deviceName.getBytes().length)&0xFF);
            deviceNameEir[1] = (byte) (0x09); // Eir Complete local name type
            System.arraycopy(_deviceName.getBytes(), 0, deviceNameEir,2, _deviceName.getBytes().length);
        }

        // EIR DATA DEVICE CLASS
        byte [] deviceClassEir = null;

        if ((_deviceClass!=null) && (_deviceClass.length != 0))
        {
            deviceClassEir = deviceClassEir = new byte [EIRdatalengthSize + EIR_dataLocalTypeNameSize + _deviceClass.length];
            deviceClassEir[0] = (byte) ((EIR_dataLocalTypeNameSize + _deviceClass.length)&0xFF);
            deviceClassEir[1] = (byte) (0x0D); // Eir Complete local name type
            System.arraycopy(_deviceClass, 0, deviceClassEir,2, _deviceClass.length);
        }


        byte [] UUIDServiceClassEir = null;
        // EIR UUID SERVICE CLASS
        if ((_UUIDClassBuff!=null) && (_UUIDClassBuff.length != 0))
        {
            UUIDServiceClassEir = new byte [EIRdatalengthSize + EIR_dataLocalTypeNameSize +  _UUIDClassBuff.length];
            UUIDServiceClassEir[0] = (byte) ((EIR_dataLocalTypeNameSize + _UUIDClassBuff.length)&0xFF);
            UUIDServiceClassEir[1] = (byte) (_UUIDClassList); // Eir Complete local name type
            System.arraycopy(_UUIDClassBuff, 0, UUIDServiceClassEir,2, _UUIDClassBuff.length);
        }

        //Build OOB payload : Optional Data length + BT device Addr + EIR Data LocalName + EIR Device class Data + EIR UUID Service class

        int payloadPartialSize = ((macAddr!=null)?macAddr.length:0) +((deviceNameEir!=null)?deviceNameEir.length:0)
                + ((deviceClassEir!=null)?deviceClassEir.length:0) + ((UUIDServiceClassEir!=null)?UUIDServiceClassEir.length:0);

        _payload = new byte[oobOptionalDataLength + payloadPartialSize];

        _payload[1] =(byte)     (((_payload.length /*- oobOptionalDataLength*/)&0xFF00)>>8);
        _payload[0] = (byte)    ((_payload.length /*- oobOptionalDataLength*/)&0xFF);

        System.arraycopy(macAddr,0,_payload,2,macAddr.length);
        int currentOffset=2;
        if (deviceNameEir != null)
        {
            System.arraycopy(deviceNameEir,0,_payload,currentOffset+macAddr.length,deviceNameEir.length);
            currentOffset = currentOffset +macAddr.length;
        }

        if (deviceClassEir != null)
        {
            System.arraycopy(deviceClassEir,0,_payload,currentOffset+deviceNameEir.length,deviceClassEir.length);
            currentOffset = currentOffset +deviceNameEir.length;
        }
        if (UUIDServiceClassEir != null)
        {
            System.arraycopy(UUIDServiceClassEir,0,_payload,2+macAddr.length+deviceNameEir.length+deviceClassEir.length,UUIDServiceClassEir.length);
        }


    }

    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;
        byte [] payload=null;

        // Check if there is a valid text
        if (_macAddr == null) {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input URI");
        } else {

            // Payload Struct : - Simplified Tag Format for a Single Bluetooth Carrier
            // OOB Data Length
            // Device Address
            // BT locale Name
            // Class of device - optional
            // Service Class UID - optional
            int oobOptionalDataLength = 2;
            byte [] macAddr = null;
            macAddr = new byte[_macAddr.length];
            // Switch MAC address order
            for (int i=0; i<_macAddr.length;i++)
            {
                macAddr[i] = _macAddr[_macAddr.length-i-1];
            }


            int EIRdatalengthSize = 1;
            int EIR_dataLocalTypeNameSize = 1;

            // EIR DATA NAME
            byte [] deviceNameEir = null;

            if ((_deviceName!=null) && (_deviceName.length() != 0))
            {
             deviceNameEir = new byte [ EIRdatalengthSize + EIR_dataLocalTypeNameSize + _deviceName.getBytes().length ];
             deviceNameEir[0] = (byte) ((EIR_dataLocalTypeNameSize + _deviceName.getBytes().length)&0xFF);
             deviceNameEir[1] = (byte) (0x09); // Eir Complete local name type
             System.arraycopy(_deviceName.getBytes(), 0, deviceNameEir,2, _deviceName.getBytes().length);
            }

            // EIR DATA DEVICE CLASS
            byte [] deviceClassEir = null;

            if ((_deviceClass!=null) && (_deviceClass.length != 0))
            {
                deviceClassEir = new byte [EIRdatalengthSize + EIR_dataLocalTypeNameSize + _deviceClass.length];
                deviceClassEir[0] = (byte) ((EIR_dataLocalTypeNameSize + _deviceClass.length)&0xFF);
                deviceClassEir[1] = (byte) (0x0D); // Eir Complete local name type
                System.arraycopy(_deviceClass, 0, deviceClassEir,2, _deviceClass.length);
            }


            byte [] UUIDServiceClassEir = null;
            // EIR UUID SERVICE CLASS
            if ((_UUIDClassBuff!=null) && (_UUIDClassBuff.length != 0))
            {
                UUIDServiceClassEir = new byte [EIRdatalengthSize + EIR_dataLocalTypeNameSize +  _UUIDClassBuff.length];
                UUIDServiceClassEir[0] = (byte) ((EIR_dataLocalTypeNameSize + _UUIDClassBuff.length)&0xFF);
                UUIDServiceClassEir[1] = (byte) (_UUIDClassList); // Eir Complete local name type
                System.arraycopy(_UUIDClassBuff, 0, UUIDServiceClassEir,2, _UUIDClassBuff.length);
            }


            //Build OOB payload : Optional Data length + BT device Addr + EIR Data LocalName + EIR Device class Data + EIR UUID Service class

            int payloadPartialSize = ((macAddr!=null)?macAddr.length:0) +((deviceNameEir!=null)?deviceNameEir.length:0)
                                    + ((deviceClassEir!=null)?deviceClassEir.length:0) + ((UUIDServiceClassEir!=null)?UUIDServiceClassEir.length:0);
            payload = new byte[oobOptionalDataLength + payloadPartialSize];

            payload[1] =(byte)     (((payload.length /*+ oobOptionalDataLength*/)&0xFF00)>>8);
            payload[0] = (byte) ((payload.length  /*+ oobOptionalDataLength*/)&0xFF);
            System.arraycopy(macAddr,0,payload,2,macAddr.length);
            int currentOffset=2;
            if (deviceNameEir != null)
            {
                System.arraycopy(deviceNameEir,0,payload,currentOffset+macAddr.length,deviceNameEir.length);
                currentOffset = currentOffset +macAddr.length;
            }

            if (deviceClassEir != null)
            {
                System.arraycopy(deviceClassEir,0,payload,currentOffset+deviceNameEir.length,deviceClassEir.length);
                currentOffset = currentOffset +deviceNameEir.length;
            }
            if (UUIDServiceClassEir != null)
            {
                System.arraycopy(UUIDServiceClassEir,0,payload,2+macAddr.length+deviceNameEir.length+deviceClassEir.length,UUIDServiceClassEir.length);
            }

            // Build


            NdefRecord rtdBTHandoverRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,"application/vnd.bluetooth.ep.oob".getBytes(), new byte[0]/*id*/, payload);
            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {rtdBTHandoverRecord};
            msg = new NdefMessage(records);
        }

        return msg;
    }

    public void setDeviceClass(byte [] deviceClass)
    {
        _deviceClass = deviceClass.clone();
    }

    public void setServiceClass(byte serviceClassCoding ,byte [] ServiceClass)
    {
        _UUIDClassBuff = ServiceClass.clone();
        _UUIDClassList = serviceClassCoding;
    }

    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {

        // update ST NDEF message object
        serialize();
        ndefHandler.setNdefBTHandover(_payload);
    }

}
