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

import com.st.NDEF.NDEFMailMessage;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NFC.NFCApplication;
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

public class NDEFMailFragment extends NDEFSimplifiedMessageFragment implements OnItemSelectedListener {
    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFMailMessage _ndefMsg = null;

    // Used to send mail from NDEF editor for Demo
    private String _mailrecipients;
    public String get_mailrecipients() {
        return _mailrecipients;
    }

    private String _mailsubject;
    public String get_mailsubject() {
        return _mailsubject;
    }

    private String _mailtext;
    public String get_mailtext() {
        return _mailtext;
    }
    // end additional mail items storage

    public static NDEFMailFragment newInstance(NDEFMailMessage msg, boolean readOnly) {
        NDEFMailFragment fragment = new NDEFMailFragment();
        fragment.setNDEFMailMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFMailFragment newInstance(boolean readOnly) {
        NDEFMailFragment fragment = new NDEFMailFragment();
        fragment.setNDEFMailMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFMailFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFMailMsg (NDEFMailMessage msg) { _ndefMsg = msg; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_mail, container, false);


        // Deactivate the view(s) in case of ReadOnly invocation
        if (isReadOnly()) {
            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            txtView = (EditText) _curFragmentView.findViewById(R.id.subjectEtId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            txtView = (EditText) _curFragmentView.findViewById(R.id.mailmsgEtId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
        } else {
            // Set the OnItemSelectedListener for the spinner
            if (NFCApplication.getApplication().isEnableSalonFeature()) {
                EditText txtView = (EditText) _curFragmentView.findViewById(R.id.mailmsgEtId);
                _mailtext = "Dear " + NFCApplication.getApplication().getmNFCApp_customername();
                _mailtext = _mailtext + "\n";
                _mailtext = _mailtext + NFCApplication.getApplication().getMailSalonHeader();
                _mailtext = _mailtext + "\n";
                _mailtext = _mailtext + NFCApplication.getApplication().getmNFCApp_customertextinformation();
                _mailtext = _mailtext + "\n";
                _mailtext = _mailtext + "\n";
                _mailtext = _mailtext + NFCApplication.getApplication().getMailSalonFooter();

                txtView.setText(_mailtext);

                txtView = (EditText) _curFragmentView.findViewById(R.id.subjectEtId);
                _mailsubject = NFCApplication.getApplication().getMailSalonSubject();
                txtView.setText(_mailsubject);


                txtView = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
                _mailrecipients = NFCApplication.getApplication().getmNFCApp_customermail();
                txtView.setText(_mailrecipients);

            } else {
                EditText txtView = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
                _mailrecipients = "";
                txtView.setText(_mailrecipients);
                txtView.setEnabled(true);
                //txtView.setRawInputType(InputType.TYPE_NULL);
                txtView.setFocusableInTouchMode(true);

                txtView = (EditText) _curFragmentView.findViewById(R.id.subjectEtId);
                _mailsubject = "";
                txtView.setText(_mailsubject);
                txtView.setEnabled(true);
                //txtView.setRawInputType(InputType.TYPE_NULL);
                txtView.setFocusableInTouchMode(true);

                txtView = (EditText) _curFragmentView.findViewById(R.id.mailmsgEtId);
                _mailtext = "";
                txtView.setText(_mailtext);
                txtView.setEnabled(true);
                //txtView.setRawInputType(InputType.TYPE_NULL);
                txtView.setFocusableInTouchMode(true);


            }

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
        NDEFMailMessage ndefMessage = null;

 
         EditText contactEd = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
        String contact = contactEd.getText().toString();
        _mailrecipients = contact;
         EditText subjectEd = (EditText) _curFragmentView.findViewById(R.id.subjectEtId);
        String subject = subjectEd.getText().toString();
        _mailsubject = subject;
         EditText messagesEd = (EditText) _curFragmentView.findViewById(R.id.mailmsgEtId);
        String message = messagesEd.getText().toString();
        _mailtext = message;

        if (!contact.isEmpty()) {
            ndefMessage = new NDEFMailMessage(contact, subject,message);
        }

        return ndefMessage;
    }

    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {
    // Update ID in spinner

    // Update URI in TextView
        EditText contactview = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
        contactview.setText(((NDEFMailMessage)ndefMsg).get_mContact());
        _mailrecipients = ((NDEFMailMessage)ndefMsg).get_mContact();
        EditText subjectview = (EditText) _curFragmentView.findViewById(R.id.subjectEtId);
        subjectview.setText(((NDEFMailMessage)ndefMsg).get_mSubject());
        _mailsubject = ((NDEFMailMessage)ndefMsg).get_mSubject();
        EditText messageview = (EditText) _curFragmentView.findViewById(R.id.mailmsgEtId);
        messageview.setText(((NDEFMailMessage)ndefMsg).get_mMessage());
        _mailtext = ((NDEFMailMessage)ndefMsg).get_mMessage();

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
