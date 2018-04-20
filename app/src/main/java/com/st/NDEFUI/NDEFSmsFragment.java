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

import java.util.Collection;

import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFSmsMessage;
import com.st.demo.R;

import android.app.Activity;
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

public class NDEFSmsFragment extends NDEFSimplifiedMessageFragment implements OnItemSelectedListener {
    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFSmsMessage _ndefMsg = null;

    public static NDEFSmsFragment newInstance(NDEFSmsMessage msg, boolean readOnly) {
        NDEFSmsFragment fragment = new NDEFSmsFragment();
        fragment.setNDEFSmsMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFSmsFragment newInstance(boolean readOnly) {
        NDEFSmsFragment fragment = new NDEFSmsFragment();
        fragment.setNDEFSmsMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFSmsFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFSmsMsg (NDEFSmsMessage msg) { _ndefMsg = msg; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_sms, container, false);


        // Deactivate the view(s) in case of ReadOnly invocation
        if (isReadOnly()) {
            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            txtView = (EditText) _curFragmentView.findViewById(R.id.smsmsgEtId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
        } else {
            // Set the OnItemSelectedListener for the spinner
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
        NDEFSmsMessage ndefMessage = null;


         EditText contactEd = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
        String contact = contactEd.getText().toString();
         EditText messagesEd = (EditText) _curFragmentView.findViewById(R.id.smsmsgEtId);
        String message = messagesEd.getText().toString();

        if (!contact.isEmpty()) {
            ndefMessage = new NDEFSmsMessage(contact,message);
        }

        return ndefMessage;
    }

    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {
    // Update ID in spinner

    // Update URI in TextView
        EditText contactview = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
        contactview.setText(((NDEFSmsMessage)ndefMsg).get_mContact());
        EditText messageview = (EditText) _curFragmentView.findViewById(R.id.smsmsgEtId);
        messageview.setText(((NDEFSmsMessage)ndefMsg).get_mMessage());
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
