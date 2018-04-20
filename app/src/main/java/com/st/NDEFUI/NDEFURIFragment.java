/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1985 $
  * Revision of last commit    :  $Rev: 1985 $
  * Date of last commit     :  $Date: 2016-04-19 16:22:13 +0200 (Tue, 19 Apr 2016) $ 
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

import java.util.Collection;

import com.st.demo.R;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFURIMessage;
import com.st.NDEF.NDEFURIMessage.NDEFURIIDCode;
import com.st.NFC.NFCApplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class NDEFURIFragment extends NDEFSimplifiedMessageFragment implements OnItemSelectedListener {
    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFURIMessage _ndefMsg = null;
    boolean mBrowserStarted = false;

    public static NDEFURIFragment newInstance(NDEFURIMessage newMsg, boolean readOnly) {
        NDEFURIFragment fragment = new NDEFURIFragment();
        fragment.setNDEFURIMsg(newMsg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFURIFragment newInstance(boolean readOnly) {
        NDEFURIFragment fragment = new NDEFURIFragment();
        fragment.setNDEFURIMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFURIFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFURIMsg (NDEFURIMessage msg) { _ndefMsg = msg; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_uri, container, false);

        // Configure the spinner for URI codes
        // - get the spinner
        Spinner curSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentIDId);
        // - build the applicable list
        Collection<String> spinnerList = NDEFURIMessage.getSupportedURICodesList();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(NFCApplication.getContext(), R.layout.spinner_text_view);
        spinnerAdapter.addAll(spinnerList);
        // - set the spinner list
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_text_view);
        curSpinner.setAdapter(spinnerAdapter);

        // Deactivate the view(s) in case of ReadOnly invocation
        if (isReadOnly()) {
            curSpinner.setEnabled(false);
            curSpinner.setClickable(false);
            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentTextViewId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);

        } else {
            // Set the OnItemSelectedListener for the spinner
            curSpinner.setOnItemSelectedListener(this);
        }

        // Set some cosmetics
        //((TextView)curSpinner.findViewById(R.id.SpinnerTxtView)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimensionPixelSize(R.dimen.ndef_frag_spinner_max_width));

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
        NDEFURIMessage ndefMessage = null;

        Spinner curSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentIDId);
        NDEFURIIDCode uriID = NDEFURIMessage.getURICodeFromStr((String)curSpinner.getSelectedItem());

        EditText uriView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentTextViewId);
        String text = uriView.getText().toString();

        if (!text.isEmpty() || (uriID == NDEFURIIDCode.NDEF_RTD_URI_ID_NO_PREFIX)) {
            ndefMessage = new NDEFURIMessage(uriID, text);
        }

        return ndefMessage;
    }

    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {
        // Update ID in spinner
        if (_curFragmentView != null)
        {
            Spinner curSpinner = (Spinner) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentIDId);
            NDEFURIIDCode uriID = ((NDEFURIMessage)ndefMsg).getURIID();
            curSpinner.setSelection(NDEFURIMessage.getURICodePositionInList(uriID));

            // Update URI in TextView
            EditText uriView = (EditText) _curFragmentView.findViewById(R.id.NDEFRTDURIFragmentTextViewId);
            uriView.setText(((NDEFURIMessage)ndefMsg).getURI());

            // additional info used for CES Salon improvement
            NFCApplication currentApp = NFCApplication.getApplication() ;
            currentApp.setmNFCApp_customertextinformation(curSpinner.getSelectedItem().toString() + ((NDEFURIMessage)ndefMsg).getURI());

            if (currentApp.isEnableDemoFeature() && isReadOnly() && mBrowserStarted == false && currentApp.getCurrentTag().getModel().contains("M24LR")){
                mBrowserStarted = true;
                startBrowser(curSpinner.getSelectedItem().toString() + ((NDEFURIMessage)ndefMsg).getURI());
            }


        }
    }

    // Implementation of the AdapterView.OnItemSelectedListener interface, for Spinner change behavior
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // Nothing to do
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void startBrowser(String url) {
        //String url = "http://www.stackoverflow.com";
        String localurl = url;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            localurl = "http://www." + url;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(localurl));
        startActivity(i);
    }
    
}
