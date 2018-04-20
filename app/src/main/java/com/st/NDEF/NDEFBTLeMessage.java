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


import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;

import com.st.NFC.NFCApplication;
import com.st.NFC.stnfchelper;
import com.st.demo.R;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;


//Implements NFC Forum RTD URI NDEF message

public class NDEFBTLeMessage extends NDEFSimplifiedMessage {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    private String TAG = this.getClass().getName();

    public String mBtDeviceName;
    public byte[] mBtMacAddr;
    public byte   mBtMacAddrType;
    private byte[] mBtDeviceClass;
    private byte[] mBtUuidClassList;
    private byte mBtUuidClass;
    private byte[] mBtRoleList;
    private byte mBtRole;
    private byte[] mBtAppearenceData;
    private byte mBtApparence;

    private byte[] mBuffer;

     /**
     * Methods
     */
    // Constructors
    public NDEFBTLeMessage() {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_BTLE);
        // Default values
        mBtDeviceName = "";
        mBtMacAddr = null;
        mBtDeviceClass = null;
        mBtUuidClassList = null;
        mBtUuidClass = 0x00;

    }

    public NDEFBTLeMessage(String deviceName,
                           byte[] macAddr, byte[] deviceClass, byte[] uuidClass, byte uidClassList) {
        super(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_BTLE);
        mBtDeviceName = deviceName;
        mBtMacAddr = new byte[macAddr.length];
        System.arraycopy(macAddr, 0, mBtMacAddr, 0, mBtMacAddr.length);
        mBtDeviceClass = new byte[deviceClass.length];
        System.arraycopy(deviceClass, 0, mBtDeviceClass, 0, mBtDeviceClass.length);
        mBtUuidClassList = new byte[uuidClass.length];
        System.arraycopy(uuidClass, 0, mBtUuidClassList, 0, mBtUuidClassList.length);
        mBtUuidClass = uidClassList;
    }



    public String getBTDeviceName() {
        return mBtDeviceName;
    }

    public byte[] getBTDeviceMacAddr() {
        return mBtMacAddr;
    }

    public void setBTDeviceName(String name) {
        mBtDeviceName = name;
    }

    public void setBTDeviceMacAddr(byte[] macAddr) {
        mBtMacAddr = new byte[macAddr.length];
        System.arraycopy(macAddr, 0, mBtMacAddr, 0, macAddr.length);
    }

    public void setBTUuidClass(byte[] uuidClass) {
        mBtUuidClassList = uuidClass;
    }

    public void setBTDeviceClass(byte[] deviceClass) {
        mBtDeviceClass = deviceClass;
    }

    public void setBtUuidClassList(byte uuidClassList) {
        mBtUuidClass = uuidClassList;
    }

    public void setBTDeviceMacAddrType(byte type) {mBtMacAddrType = type;}
    public byte getBTDeviceMacAddrType() {return mBtMacAddrType;}

    public void setBTRoleList(byte[] roleList) {mBtRoleList = roleList;}
    public byte[] getBTRoleList() {return mBtRoleList ;}


    public void setBTAppearence(byte[] appearence) { mBtAppearenceData = appearence;}
    public byte[] getBTAppearence() { return mBtAppearenceData;}


    public static boolean isSimplifiedMessage(tnf mTnf, byte [] rtdType) {

        if ( (mTnf == tnf.media)
            &&
            (Arrays.equals(rtdType, NFCApplication.getContext().getString(R.string.mime_type_bt_le).getBytes())) )
            return true;

        return false;
    }


    private void parse (ByteBuffer payload) {

        try {
            payload.position(2);
            byte[] address = new byte[6];
            payload.get(address);
            // ByteBuffer.order(LITTLE_ENDIAN) doesn't work for
            // ByteBuffer.get(byte[]), so manually swap order
            // Do not display last byte (public or random in case of BTLE)
            for (int i = 0; i < 3; i++) {
                byte temp = address[i];
                address[i] = address[5 - i];
                address[5 - i] = temp;
            }

            mBtMacAddr = address.clone();
            mBtDeviceName = null;

            mBtMacAddrType = payload.get();

            while (payload.remaining() > 0) {
                byte[] nameBytes;

                int len = payload.get();
                int type = payload.get();
                switch (type) {
                    case 0x08:  // short local name
                        nameBytes = new byte[len - 1];
                        payload.get(nameBytes);
                        mBtDeviceName = new String(nameBytes, Charset.forName("UTF-8"));
                        break;
                    case 0x09:  // long local name
                        if (mBtDeviceName != null) break;  // prefer short name
                        nameBytes = new byte[len - 1];
                        payload.get(nameBytes);
                        mBtDeviceName = new String(nameBytes, Charset.forName("UTF-8"));
                        break;
                    case 0x0D: //Class of device - 3 Bytes with Service Class / Major Device Class / Minor Device Class
                        mBtDeviceClass = new byte[3];
                        payload.get(mBtDeviceClass);
                        break;
                    case 0x1C:
                        mBtRole = (byte) type;
                        mBtRoleList = new byte[len-1];
                        payload.get(mBtRoleList);
                        break;
                    case 0x19:
                        mBtApparence = (byte) type;
                        mBtAppearenceData = new byte[len -1];
                        payload.get(mBtAppearenceData);
                        break;
                    case 0x02: //16-bit un-complete Service Class UUID list
                        mBtUuidClass = (byte) 0x02;
                        mBtUuidClassList = new byte[len - 1];
                        payload.get(mBtUuidClassList);
                        break;
                    case 0x03://16-bit complete Service Class UUID list
                    case 0x04: //32-bit complete Service Class UUID list
                    case 0x05://64-bit un-complete Service Class UUID list
                    case 0x06://128-bit un-complete Service Class UUID list
                    case 0x07://256-bit un-complete Service Class UUID list
                        mBtUuidClass = (byte) type;
                        mBtUuidClassList = new byte[len - 1];
                        payload.get(mBtUuidClassList);
                        break;
                    default:
                        payload.position(payload.position() + len - 1);
                        break;
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "BT OOB: invalid BT address" + e.toString());
        } catch (BufferUnderflowException e) {
            Log.e(TAG, "BT OOB: payload shorter than expected" + e.toString());
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
            parse(payloadbuffer);
        }
    }

    private void serialize()
    {
        export2Bt();
    }

    private byte[] fillEirBuffer(byte[] input, byte id) {
        byte[] output = null;

        if ((input != null) && (input.length != 0)) {
            // eir data length = 1 + eir data local type name = 1
            output = new byte[1 + 1 + input.length];
            // size of payload
            output[0] = (byte) ((1 + input.length) & 0xFF);
            output[1] = (byte) id; // Eir Complete local name type
            System.arraycopy(input, 0, output, 2, input.length);
        }

        return output;
    }
    private void export2Bt() {
        mBuffer = null;
        byte[] macAddr = null;

        if (mBtMacAddr != null) {
            //We add mac addr type @ the end
            macAddr = new byte[mBtMacAddr.length + 1];
            for (int i = 0; i < mBtMacAddr.length; i++) {
                macAddr[i] = mBtMacAddr[mBtMacAddr.length - i - 1];
            }
            macAddr[mBtMacAddr.length] = mBtMacAddrType;
        }

        byte[] eirMacAddr = null;
        if (macAddr != null)
            eirMacAddr = fillEirBuffer(macAddr, (byte) 0x1B);

        byte[] eirDeviceName = null;
        if (mBtDeviceName != null)
            eirDeviceName = fillEirBuffer(mBtDeviceName.getBytes(), (byte) 0x09);

        byte[] eirDeviceClass = null;
        if (mBtDeviceClass != null)
            eirDeviceClass = fillEirBuffer(mBtDeviceClass, (byte) 0x0D);

        byte[] eirUuidServiceClass = null;
        if (mBtUuidClassList != null)
            eirUuidServiceClass = fillEirBuffer(mBtUuidClassList, (byte) mBtUuidClass);

        byte[] eirAppearenceData = null;
        if (mBtAppearenceData != null)
            eirAppearenceData = fillEirBuffer(mBtAppearenceData, (byte) 0x19);

        byte[] eirRoleList = null;
        if (mBtRoleList != null)
            eirRoleList = fillEirBuffer(mBtRoleList, (byte) 0x1C);


        //Build OOB payload : Optional Data length + BT device Addr + EIR Data LocalName + EIR Device class Data + EIR UUID Service class
        int size = ((eirMacAddr != null) ? eirMacAddr.length : 0) +
                ((eirDeviceName != null) ? eirDeviceName.length : 0) +
                ((eirDeviceClass != null) ? eirDeviceClass.length : 0) +
                ((eirUuidServiceClass != null) ? eirUuidServiceClass.length : 0) +
                ((eirAppearenceData!= null) ? eirAppearenceData.length : 0) +
                ((eirRoleList!= null) ? eirRoleList.length : 0);


        mBuffer = new byte[size];


        System.arraycopy(eirMacAddr, 0, mBuffer, 0, eirMacAddr.length);
        int offset = eirMacAddr.length;


        if (eirDeviceName != null) {
            System.arraycopy(eirDeviceName, 0, mBuffer, offset, eirDeviceName.length);
            offset += eirDeviceName.length;
        }

        if (eirDeviceClass != null) {
            System.arraycopy(eirDeviceClass, 0, mBuffer, offset, eirDeviceClass.length);
            offset += eirDeviceClass.length;
        }

        if (eirUuidServiceClass != null) {
            System.arraycopy(eirUuidServiceClass, 0, mBuffer, offset, eirUuidServiceClass.length);
            offset += eirUuidServiceClass.length;
        }

        if (eirAppearenceData != null) {
            System.arraycopy(eirAppearenceData, 0, mBuffer, offset, eirAppearenceData.length);
            offset += eirAppearenceData.length;
        }

        if (eirRoleList != null) {
            System.arraycopy(eirRoleList, 0, mBuffer, offset, eirRoleList.length);
            offset += eirRoleList.length;
        }
    }
    // Implementation of abstract method(s) from parent
    public NdefMessage getNDEFMessage() {
        NdefMessage msg = null;
        byte [] payload=null;

        // Check if there is a valid text
        if (mBtMacAddr == null) {
               Log.e(this.getClass().getName(), "Error in NDEF msg creation: No input BTLe");
        } else {

            export2Bt();

            // Build


            NdefRecord rtdBTLeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,"application/vnd.bluetooth.le.oob".getBytes(), new byte[0]/*id*/, mBuffer);
            // Create the msg to be returned
            NdefRecord[] records = new NdefRecord[] {rtdBTLeRecord};
            msg = new NdefMessage(records);
        }

        return msg;
    }


    public void updateSTNDEFMessage (stnfcndefhandler ndefHandler)
    {

        // update ST NDEF message object
        serialize();
        ndefHandler.setNdefBTLe(mBuffer);
    }

}
