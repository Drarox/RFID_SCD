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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
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

import com.st.NDEF.NDEFBTLeMessage;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NFC.NFCApplication;
import com.st.NFC.stnfchelper;
import com.st.demo.R;

import java.util.ArrayList;
import java.util.Set;

public class NDEFBTLeFragment extends NDEFSimplifiedMessageFragment implements OnItemSelectedListener {
    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFBTLeMessage _ndefMsg = null;
    private BluetoothAdapter _mBluetoothAdapter = null;
    private ArrayAdapter<String> _mbtArrayAdapter;
    Set<BluetoothDevice> _mpairedDevices = null;

    private ArrayList<String> _mDevicelistName;
    private ArrayList<String> _mDevicelistMacAddr;


    private String TAG = this.getClass().getName();


    public static NDEFBTLeFragment newInstance(NDEFBTLeMessage msg, boolean readOnly) {
        NDEFBTLeFragment fragment = new NDEFBTLeFragment();
        fragment.setNDEFMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFBTLeFragment newInstance(boolean readOnly) {
        NDEFBTLeFragment fragment = new NDEFBTLeFragment();
        fragment.setNDEFMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFBTLeFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFMsg (NDEFBTLeMessage msg) { _ndefMsg = msg; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_btle, container, false);

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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE ||
                                    device.getType() == BluetoothDevice.DEVICE_TYPE_DUAL) {
                                _mDevicelistName.add(device.getName());
                                _mDevicelistMacAddr.add(device.getAddress());
                            }
                        }
                    }

                // add a new device
                _mDevicelistName.add("New BT Le Device");
                _mDevicelistMacAddr.add("AA:BB:CC:EE::FF");

                _mbtArrayAdapter= new ArrayAdapter<String>(NFCApplication.getContext(),R.layout.list_item ,_mDevicelistName);

                ListView alistView = (ListView) _curFragmentView.findViewById(R.id.listView_bounded_device);
                alistView.setAdapter(_mbtArrayAdapter);

                // Fill EditText with local BT adapter name and Mac Addr

                alistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                      @Override
                      public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
                        final String item = (String) parent.getItemAtPosition(position);
                        EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTLeFragmentDeviceNameId);
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

            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTLeFragmentDeviceNameId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            ListView alistView = (ListView) _curFragmentView.findViewById(R.id.listView_bounded_device);
            alistView.setVisibility(View.GONE);

        } else {
            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTLeFragmentDeviceNameId);
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
         onMessageChanged (_ndefMsg);
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
        NDEFBTLeMessage ndefMessage = null;


     EditText deviceNameView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTLeFragmentDeviceNameId);
     String deviceName = deviceNameView.getText().toString();
        EditText macAddrView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
        String macAddr = macAddrView.getText().toString();

        if (!deviceName.isEmpty() && !macAddr.isEmpty() ) {
            ndefMessage = new NDEFBTLeMessage();
            ndefMessage.setBTDeviceName(deviceName);
            ndefMessage.setBTDeviceMacAddr(stnfchelper.hexStringToByteArray(macAddr.replaceAll(":", "")));

            ndefMessage.setBTDeviceMacAddrType((byte) 0x00); //public type

            int btdeviceIndex = _mDevicelistMacAddr.indexOf(macAddr);
            if (btdeviceIndex!=-1)
            {
                // try to find the device class of the select bt device
                if((_mBluetoothAdapter.getName()!=null ) && (_mBluetoothAdapter.getName().equals(deviceName) ))// local device is selected
                {
                    byte[] roleList = {(byte) 0x00};//Role peripheric only
                    ndefMessage.setBTRoleList(roleList);
                    ndefMessage.setBTDeviceMacAddrType((byte) 0x00); //public type

                }
                else
                {
                    BluetoothDevice remoteDevice =  _mBluetoothAdapter.getRemoteDevice(macAddr);
                    if (remoteDevice != null)
                    {
                        BluetoothClass deviceClass = remoteDevice.getBluetoothClass();

                        if (deviceClass !=null)
                        {
                            if (deviceClass.getDeviceClass() != 0) {
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
    EditText deviceNameView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTLeFragmentDeviceNameId);
    deviceNameView.setText(((NDEFBTLeMessage)ndefMsg).getBTDeviceName());

    EditText macAddrView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
    macAddrView.setText(stnfchelper.bytArrayToHex(((NDEFBTLeMessage)ndefMsg).getBTDeviceMacAddr()));

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
  


