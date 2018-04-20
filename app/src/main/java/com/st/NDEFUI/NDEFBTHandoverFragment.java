/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1672 $
  * Revision of last commit    :  $Rev: 1672 $
  * Date of last commit     :  $Date: 2016-02-18 17:11:40 +0100 (Thu, 18 Feb 2016) $ 
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


package com.st.NDEFUI;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import com.st.NDEF.NDEFBTHandoverMessage;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NFC.NFCApplication;
import com.st.demo.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class NDEFBTHandoverFragment extends NDEFSimplifiedMessageFragment implements OnItemSelectedListener {
    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFBTHandoverMessage _ndefMsg = null;
    private BluetoothAdapter _mBluetoothAdapter = null;
    private ArrayAdapter<String> _mbtArrayAdapter;
    Set<BluetoothDevice> _mpairedDevices = null;

    private ArrayList<String> _mDevicelistName;
    private ArrayList<String> _mDevicelistMacAddr;


    private String TAG = this.getClass().getName();


    public static NDEFBTHandoverFragment newInstance(NDEFBTHandoverMessage msg, boolean readOnly) {
        NDEFBTHandoverFragment fragment = new NDEFBTHandoverFragment();
        fragment.setNDEFMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFBTHandoverFragment newInstance(boolean readOnly) {
        NDEFBTHandoverFragment fragment = new NDEFBTHandoverFragment();
        fragment.setNDEFMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFBTHandoverFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFMsg(NDEFBTHandoverMessage msg) {
        _ndefMsg = msg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_bthandover, container, false);

        _mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();                    //Get BT Adapter
        Set<BluetoothDevice> _mpairedDevices = _mBluetoothAdapter.getBondedDevices(); //Get Already paired devices from the current mobile

        _mDevicelistName = new ArrayList<String>();
        _mDevicelistMacAddr = new ArrayList<String>();
        _mDevicelistName.add(_mBluetoothAdapter.getName());
        _mDevicelistMacAddr.add(_mBluetoothAdapter.getAddress());

        // If there are paired devices
        if (_mpairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : _mpairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                _mDevicelistName.add(device.getName());
                _mDevicelistMacAddr.add(device.getAddress());
            }

            // add a new device
            _mDevicelistName.add("New BT Device");
            _mDevicelistMacAddr.add("00:11:22:33:44:55");

            _mbtArrayAdapter = new ArrayAdapter<String>(NFCApplication.getContext(), R.layout.list_item, _mDevicelistName);

            ListView alistView = (ListView) _curFragmentView.findViewById(R.id.listView_bounded_device);
            alistView.setAdapter(_mbtArrayAdapter);

            // Fill EditText with local BT adapter name and Mac Addr

            alistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    final String item = (String) parent.getItemAtPosition(position);
                    EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTHandoverFragmentDeviceNameId);
                    //txtView.setEnabled(true);
                    txtView.setText(_mDevicelistName.get(position));
                    //txtView.setRawInputType(InputType.TYPE_NULL);
                    txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
                    txtView.setText(_mDevicelistMacAddr.get(position));
                }

            });
        }
        // Deactivate the view(s) in case of ReadOnly invocation
        if (isReadOnly()) {

            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTHandoverFragmentDeviceNameId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            ListView alistView = (ListView) _curFragmentView.findViewById(R.id.listView_bounded_device);
            alistView.setVisibility(View.GONE);

        } else {
            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTHandoverFragmentDeviceNameId);
            txtView.setEnabled(true);
            txtView.setText(_mDevicelistName.get(0));
            txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
            txtView.setText(_mDevicelistMacAddr.get(0));
            txtView.setEnabled(true);
        }

        return _curFragmentView;
    }

    @Override
    public void onStart() {
        Log.v(this.getClass().getName(), "onStart Fragment");
        super.onStart();

        if (_ndefMsg != null) {
            // Fill in the layout with the current message
            onMessageChanged(_ndefMsg);
            _ndefMsg = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // Implementation of abstract method(s) from parent
    public NDEFSimplifiedMessage getNDEFSimplifiedMessage() {
        NDEFBTHandoverMessage ndefMessage = null;


        EditText deviceNameView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTHandoverFragmentDeviceNameId);
        String deviceName = deviceNameView.getText().toString();

        EditText macAddrView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
        String macAddr = macAddrView.getText().toString();

        if (!deviceName.isEmpty() && !macAddr.isEmpty()) {
            ndefMessage = new NDEFBTHandoverMessage(deviceName, macAddr);
            int btdeviceIndex = _mDevicelistMacAddr.indexOf(macAddr);
            if (btdeviceIndex != -1) {
                // try to find the device class of the select bt device
                if ((_mBluetoothAdapter.getName() != null) && (_mBluetoothAdapter.getName().equals(deviceName)))// local device is selected
                {
                    // no API to get Class and Service Class -> hardcoding
                    byte[] serviceClass = {(byte) 0x0C, (byte) 0x02, (byte) 0x40}; // Device class / major class / minor class little endian coding
                    ndefMessage.setDeviceClass(serviceClass);
                    byte uiid = (byte) 0x03; // uiid Service class 16-bit complete.
                    byte[] uiidlist = {(byte) 0x1E, (byte) 0x11, (byte) 0x0B, (byte) 0x11}; // HFP A2DP litlle endian coding
                    ndefMessage.setServiceClass(uiid, uiidlist);
                } else {
                    BluetoothDevice remoteDevice = _mBluetoothAdapter.getRemoteDevice(macAddr);
                    if (remoteDevice != null) {
                        BluetoothClass deviceClass = remoteDevice.getBluetoothClass();

                        if (deviceClass != null) {
                            if (deviceClass.getDeviceClass() != 0) {
                                int Cod = deviceClass.hashCode();
                                byte[] buff = {(byte) (Cod & 0xFF), (byte) ((Cod & 0xFF00) >> 8), (byte) ((Cod & 0xFF0000) >> 16)};
                                ndefMessage.setDeviceClass(buff);
                                ParcelUuid[] uiids = remoteDevice.getUuids();
                                if (uiids != null) {
                                    // only handle a 16 bit class uuid - full list -
                                    byte[] uiidlist = new byte[uiids.length * 2];
                                    for (int i = 0; i < uiids.length; i++) {
                                        long value = (uiids[i].getUuid().getMostSignificantBits() & 0x0000FFFF00000000L) >>> 32;
                                        uiidlist[2 * i + 1] = (byte) ((value & 0xFF00) >> 8);
                                        uiidlist[2 * i] = (byte) (value & 0xFF);
                                    }
                                    byte uiid = (byte) 0x03; // uiid Service class 16-bit complete.
                                    ndefMessage.setServiceClass(uiid, uiidlist);
                                }
                            }

                        }
                    }

                }
                // try to find the device UUID of the selected bt device
            }

        }

        return ndefMessage;
    }

    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {

        // Update Device Name in TextView
        EditText deviceNameView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTHandoverFragmentDeviceNameId);
        deviceNameView.setText(((NDEFBTHandoverMessage) ndefMsg).get_deviceName());

        EditText macAddrView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
        macAddrView.setText(((NDEFBTHandoverMessage) ndefMsg).get_stringmacAddr());
    }

    // Implementation of the AdapterView.OnItemSelectedListener interface, for Spinner change behavior
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // Nothing to do
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
  


