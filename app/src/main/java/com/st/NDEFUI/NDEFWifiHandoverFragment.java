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
import java.util.List;

import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFWifiHandoverMessage;
import com.st.NFC.NFCApplication;
import com.st.demo.R;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NDEFWifiHandoverFragment extends NDEFSimplifiedMessageFragment implements OnItemSelectedListener {
    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFWifiHandoverMessage _ndefMsg = null;
    private WifiManager _mWifiManager = null;
    private List<WifiConfiguration> _maccesspointlist = null;


    private ArrayAdapter<String> _mSSIDArrayAdapter;
    private ArrayAdapter<String> _mspinnerAdapter;
    private String _mpassword;
    private boolean _mshowpassword;

    private byte[] _payload;


    //private ArrayList<String> _mDevicelistName;
    //private ArrayList<String> _mDevicelistMacAddr;


    private String TAG = this.getClass().getName();

    private void readWepConfig(WifiConfiguration config) {
        Log.d("WifiPreference", "SSID " + config.SSID);
        Log.d("WifiPreference", "PASSWORD " + config.preSharedKey);
        Log.d("WifiPreference", "ALLOWED ALGORITHMS -------------");
        Log.d("WifiPreference", "LEAP " + config.allowedAuthAlgorithms.get(AuthAlgorithm.LEAP));
        Log.d("WifiPreference", "OPEN " + config.allowedAuthAlgorithms.get(AuthAlgorithm.OPEN));
        Log.d("WifiPreference", "SHARED " + config.allowedAuthAlgorithms.get(AuthAlgorithm.SHARED));
        Log.d("WifiPreference", "GROUP CIPHERS--------------------");
        Log.d("WifiPreference", "CCMP " + config.allowedGroupCiphers.get(GroupCipher.CCMP));
        Log.d("WifiPreference", "TKIP " + config.allowedGroupCiphers.get(GroupCipher.TKIP));
        Log.d("WifiPreference", "WEP104 " + config.allowedGroupCiphers.get(GroupCipher.WEP104));
        Log.d("WifiPreference", "WEP40  " + config.allowedGroupCiphers.get(GroupCipher.WEP40));
        Log.d("WifiPreference", "KEYMGMT -------------------------");
        Log.d("WifiPreference", "IEEE8021X " + config.allowedKeyManagement.get(KeyMgmt.IEEE8021X));
        Log.d("WifiPreference", "NONE " + config.allowedKeyManagement.get(KeyMgmt.NONE));
        Log.d("WifiPreference", "WPA_EAP " + config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
        Log.d("WifiPreference", "WPA_PSK " + config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));
        Log.d("WifiPreference", "PairWiseCipher-------------------");
        Log.d("WifiPreference", "CCMP " + config.allowedPairwiseCiphers.get(PairwiseCipher.CCMP));
        Log.d("WifiPreference", "NONE " + config.allowedPairwiseCiphers.get(PairwiseCipher.NONE));
        Log.d("WifiPreference", "TKIP " + config.allowedPairwiseCiphers.get(PairwiseCipher.TKIP));
        Log.d("WifiPreference", "Protocols-------------------------");
        Log.d("WifiPreference", "RSN " + config.allowedProtocols.get(Protocol.RSN));
        Log.d("WifiPreference", "WPA " + config.allowedProtocols.get(Protocol.WPA));
        Log.d("WifiPreference", "WEP Key Strings--------------------");
        String[] wepKeys = config.wepKeys;
        Log.d("WifiPreference", "WEP KEY 0 " + wepKeys[0]);
        Log.d("WifiPreference", "WEP KEY 1 " + wepKeys[1]);
        Log.d("WifiPreference", "WEP KEY 2 " + wepKeys[2]);
        Log.d("WifiPreference", "WEP KEY 3 " + wepKeys[3]);
    }


    public static NDEFWifiHandoverFragment newInstance(NDEFWifiHandoverMessage msg, boolean readOnly) {
        NDEFWifiHandoverFragment fragment = new NDEFWifiHandoverFragment();
        fragment.setNDEFMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFWifiHandoverFragment newInstance(boolean readOnly) {
        NDEFWifiHandoverFragment fragment = new NDEFWifiHandoverFragment();
        fragment.setNDEFMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFWifiHandoverFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFMsg(NDEFWifiHandoverMessage msg) {
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
        Spinner encrTypeList;
        Spinner authTypeList;
        EditText netKeyTxt;

        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_wifihandover, container, false);
        Spinner SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFWifiSSIDSpinnerId);
        _mspinnerAdapter = new ArrayAdapter<String>(NFCApplication.getContext(), R.layout.spinner_text_view);

        if (!isReadOnly()) {
            _mWifiManager = (WifiManager) NFCApplication.getContext().getSystemService(Context.WIFI_SERVICE);


            if (!_mWifiManager.isWifiEnabled()) {
                Toast toast = Toast.makeText(NFCApplication.getContext(), "Enable Wifi fisrt", Toast.LENGTH_LONG);
                toast.show();
            } else {

                _maccesspointlist = _mWifiManager.getConfiguredNetworks();

                // Configure the spinner for SSID Names
                // - get the SSID spinner


                // - build the applicable list
                List<String> SSIDspinnerList = new ArrayList<String>();
                for (int i = 0; i < _maccesspointlist.size(); i++) {

                    SSIDspinnerList.add(_maccesspointlist.get(i).SSID.replaceAll("\"", ""));
                    //SSID = SSID.replaceAll("\"","");
                    // log configuration
                    readWepConfig(_maccesspointlist.get(i));
                }
                //SSIDspinnerList.add("Custom...");
                _mspinnerAdapter.setDropDownViewResource(R.layout.spinner_text_view);
                _mspinnerAdapter.addAll(SSIDspinnerList);
                SSIDSpinner.setAdapter(_mspinnerAdapter);

                SSIDSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        //readWepConfig(_maccesspointlist.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
            }
            SSIDSpinner.setOnItemSelectedListener(this);

            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFWidiTHandoverFragmentSSIDId);
            txtView.setEnabled(false);
            txtView.setText("");
            txtView.setVisibility(View.INVISIBLE);

            SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFWifiSSIDSpinnerId);
            SSIDSpinner.setFocusable(true);
            SSIDSpinner.setEnabled(true);
            SSIDSpinner.setClickable(true);


            authTypeList = (Spinner) _curFragmentView.findViewById(R.id.authTypeList);
            authTypeList.setFocusable(true);
            authTypeList.setEnabled(true);
            authTypeList.setClickable(true);

            encrTypeList = (Spinner) _curFragmentView.findViewById(R.id.encrTypeList);
            encrTypeList.setFocusable(true);
            encrTypeList.setEnabled(true);
            encrTypeList.setClickable(true);

            netKeyTxt = (EditText) _curFragmentView.findViewById(R.id.netKeyTxt);
            netKeyTxt.setFocusable(true);



        /*
        txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
        txtView.setText(_mDevicelistMacAddr.get(0));
        txtView.setEnabled(true);
        */

        } else {
        /*
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(NFCApplication.getContext(), R.layout.spinner_text_view);
        spinnerAdapter.addAll(spinnerList);
        // - set the spinner list
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_text_view);
        SSIDSpinner.setAdapter(spinnerAdapter);




                ListView alistView = (ListView) _curFragmentView.findViewById(R.id.listView_bounded_device);
                alistView.setAdapter(_mbtArrayAdapter);

                // Fill EditText with local BT adapter name and Mac Addr

                alistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                      @Override
                      public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
                        final String item = (String) parent.getItemAtPosition(position);
                        EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTHandoverFragmentDeviceNameId);
                        txtView.setEnabled(true);
                        txtView.setText(_mDevicelistName.get(position));
                        txtView.setRawInputType(InputType.TYPE_NULL);
                        txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
                        txtView.setText(_mDevicelistMacAddr.get(position));
                      }

                    });
                }
                */
            // Deactivate the view(s) in case of ReadOnly invocation
            Log.d(TAG, "WIFI Fragment READ Mode");
            SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFWifiSSIDSpinnerId);
            SSIDSpinner.setFocusable(false);
            SSIDSpinner.setEnabled(false);
            SSIDSpinner.setClickable(false);

            authTypeList = (Spinner) _curFragmentView.findViewById(R.id.authTypeList);
            authTypeList.setFocusable(false);
            authTypeList.setEnabled(false);
            authTypeList.setClickable(false);

            encrTypeList = (Spinner) _curFragmentView.findViewById(R.id.encrTypeList);
            encrTypeList.setFocusable(false);
            encrTypeList.setEnabled(false);
            encrTypeList.setClickable(false);

            netKeyTxt = (EditText) _curFragmentView.findViewById(R.id.netKeyTxt);
            netKeyTxt.setFocusable(false);



            /*
            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDBTHandoverFragmentDeviceNameId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentMacAddrId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            ListView alistView = (ListView) _curFragmentView.findViewById(R.id.listView_bounded_device);
            alistView.setVisibility(View.GONE);
            */
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
        NDEFWifiHandoverMessage ndefMessage = null;


        Spinner SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFWifiSSIDSpinnerId);
        String SSID;
        if (SSIDSpinner.getSelectedItem() != null) {
            SSID = SSIDSpinner.getSelectedItem().toString();
        } else {
            SSID = "NotDefined";
        }

        Spinner authTypeList = (Spinner) _curFragmentView.findViewById(R.id.authTypeList);
        int authType = authTypeList.getSelectedItemPosition();

        Spinner encrTypeList = (Spinner) _curFragmentView.findViewById(R.id.encrTypeList);
        int encrType = encrTypeList.getSelectedItemPosition();

        EditText eTPassword = (EditText) _curFragmentView.findViewById(R.id.netKeyTxt);
        String password;
        if (eTPassword.getText() != null) {
            password = eTPassword.getText().toString();
        } else {
            password = "NotDefined";
        }

        ndefMessage = new NDEFWifiHandoverMessage(SSID,
                authType,
                encrType,
                password);
        return ndefMessage;
    }

    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {

        Spinner SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFWifiSSIDSpinnerId);

        String SSID = ((((NDEFWifiHandoverMessage) ndefMsg).getSSID()));
        //SSID = SSID.replaceAll("\"","");
        Integer s = 0;
        s = Integer.valueOf(_mspinnerAdapter.getPosition(SSID));

        if (s < 1) {
            _mspinnerAdapter.add(((NDEFWifiHandoverMessage) ndefMsg).getSSID());
            SSIDSpinner.setEnabled(false);
            SSIDSpinner.setClickable(false);
            SSIDSpinner.setAdapter(_mspinnerAdapter);
            SSIDSpinner.setEnabled(true);
            SSIDSpinner.setClickable(true);
        }
        s = Integer.valueOf(_mspinnerAdapter.getPosition(((NDEFWifiHandoverMessage) ndefMsg).getSSID()));
        SSIDSpinner.setSelection(s);
        String aSSID = SSIDSpinner.getSelectedItem().toString();

        Spinner authTypeList = (Spinner) _curFragmentView.findViewById(R.id.authTypeList);
        if (((NDEFWifiHandoverMessage) ndefMsg).getAuthType() < 2)
            authTypeList.setSelection(((NDEFWifiHandoverMessage) ndefMsg).getAuthType());
        else
            authTypeList.setSelection(0);


        Spinner encrTypeList = (Spinner) _curFragmentView.findViewById(R.id.encrTypeList);
        if (((NDEFWifiHandoverMessage) ndefMsg).getEncrType() < 3)
            encrTypeList.setSelection(((NDEFWifiHandoverMessage) ndefMsg).getEncrType());
        else
            encrTypeList.setSelection(0);


        EditText eTPassword = (EditText) _curFragmentView.findViewById(R.id.netKeyTxt);
        if (((NDEFWifiHandoverMessage) ndefMsg).getEncrKey() != null)
            eTPassword.setText(((NDEFWifiHandoverMessage) ndefMsg).getEncrKey());
        else
            eTPassword.setText("");
    }

    // Implementation of the AdapterView.OnItemSelectedListener interface, for Spinner change behavior
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Spinner SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFWifiSSIDSpinnerId);

        // readWepConfig(_maccesspointlist.get(pos));
        populateWifiExportedField(_maccesspointlist.get(pos));
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void populateWifiExportedField(WifiConfiguration config) {
        Spinner SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFWifiSSIDSpinnerId);

        if ((config.preSharedKey == null)
                        && (config.allowedKeyManagement.get(KeyMgmt.NONE) == true)
                        && (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK) == false)) {
            // we have an open network
            SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.authTypeList);
            SSIDSpinner.setSelection(0);
            SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.encrTypeList);
            SSIDSpinner.setSelection(0);
        } else if ((config.preSharedKey != null)
                        && (config.allowedKeyManagement.get(KeyMgmt.NONE) == false)
                        && (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK) == true)
                ) {
            // we have a WPA/WPA2 PSK
            SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.authTypeList);
            SSIDSpinner.setSelection(1);
            SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.encrTypeList);
            SSIDSpinner.setSelection(2);
        } else {
            // we have a WPA/WPA2 PSK
            SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.authTypeList);
            SSIDSpinner.setSelection(0);
            SSIDSpinner = (Spinner) _curFragmentView.findViewById(R.id.encrTypeList);
            SSIDSpinner.setSelection(0);
            //Toast alarm = Toast.makeText(getActivity(), "Wifi Configuration Not yet supported", Toast.LENGTH_LONG);
            //alarm.show();
        }
    }


}




