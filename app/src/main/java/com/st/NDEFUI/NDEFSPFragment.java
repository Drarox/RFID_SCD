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

import java.util.Arrays;

import com.st.NDEF.NDEFMailMessage;
import com.st.NDEF.NDEFSPMessage;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFSmsMessage;
import com.st.NDEF.NDEFTextMessage;
import com.st.NDEF.NDEFURIMessage;
import com.st.NDEF.tnf;
import com.st.demo.R;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class NDEFSPFragment extends NDEFSimplifiedMessageFragment implements OnItemSelectedListener {
    /*
     * Attributes
     */

    private final int MAX_FRAME_LAYOUT = 3;

    private View _curFragmentView = null;
    NDEFSPMessage _ndefMsg = null;
    private String TAG = this.getClass().getName();
    private NDEFSimplifiedMessageFragment  _mSmartfragmentArray[] ;


    public static NDEFSPFragment newInstance(NDEFSPMessage msg, boolean readOnly) {
        NDEFSPFragment fragment = new NDEFSPFragment();
        fragment.setNDEFSPMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFSPFragment newInstance(boolean readOnly) {
        NDEFSPFragment fragment = new NDEFSPFragment();
        fragment.setNDEFSPMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFSPFragment() {
        // Required empty public constructor
        _mSmartfragmentArray  = new NDEFSimplifiedMessageFragment[MAX_FRAME_LAYOUT];
    }

    // Accessors
    public void setNDEFSPMsg (NDEFSPMessage msg) { _ndefMsg = msg; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_sp, container, false);


        // Deactivate the view(s) in case of ReadOnly invocation
        if (isReadOnly()) {

            if (_ndefMsg !=null) {
                updateSmartRecord(true);
            } else {
                Log.d(this.getClass().getName(),"updateSmartPRecord with a null _ndefMsg ....");

            }
            //  freeze fragment here.
            /*
            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            txtView = (EditText) _curFragmentView.findViewById(R.id.smsmsgEtId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
            */
        } else {

            // Set the OnItemSelectedListener for the spinner
            // need to generate IO Exception as no yet supported.
            throw new RuntimeException(" WRITE Mode for SP Fragment not yet implemented");
        }
        return _curFragmentView;
    }


    private void clearFragment()
    {
        NDEFSimplifiedMessageFragment tmpFragment = null;

        FragmentManager fragMng = getChildFragmentManager();

        tmpFragment = (NDEFTextFragment) fragMng.findFragmentByTag("NDEFSimpleMsgTextsp");
        if (tmpFragment!=null)
        {
            FragmentTransaction transaction = fragMng.beginTransaction();
            transaction.hide(tmpFragment);
            transaction.detach(tmpFragment);
            transaction.remove(tmpFragment);
            transaction.commitAllowingStateLoss();
            fragMng.executePendingTransactions();
            tmpFragment = null;
        }
        tmpFragment = (NDEFSimplifiedMessageFragment) fragMng.findFragmentByTag("NDEFSimpleMsgURIsp");
        if (tmpFragment!=null)
        {
            FragmentTransaction transaction = fragMng.beginTransaction();
            transaction.hide(tmpFragment);
            transaction.detach(tmpFragment);
            transaction.remove(tmpFragment);
            transaction.commitAllowingStateLoss();
            fragMng.executePendingTransactions();
            tmpFragment = null;
        }



    }

    private void updateSmartRecord(boolean isReadOnly)
    {
        NdefMessage ndefMessage = _ndefMsg.get_mNDEFRecordlist();
        NdefRecord [] ndefRecordList = ndefMessage.getRecords();
        int framelayoutID[] = {R.id.frSmartViewId1,R.id.frSmartViewId2,R.id.frSmartViewId3};
        int textID[] = {R.id.LabelTxtId1,R.id.LabelTxtId2,R.id.LabelTxtId3};
        String labelDes ="";

        int currentFrameLayoutID = 0;
        NDEFSimplifiedMessageFragment tmpFragment = null;
        String tmpFragmentTag = null;

        clearFragment();

        FragmentManager fragMng = getChildFragmentManager();

        for (int i=0; ((i<ndefRecordList.length) && (currentFrameLayoutID <MAX_FRAME_LAYOUT)) ;i++)
        {
            switch (ndefRecordList[i].getTnf())
            {

            case NdefRecord.TNF_WELL_KNOWN:
                {
                    if (Arrays.equals(ndefRecordList[i].getType(),"T".getBytes()))
                    {
                        // add a NDEF Text Fragment
                        Log.d(TAG,"Ndef Record type Text detected");
                        // - Text simplified NDEF message
                        // Check if there's an existing Text fragment in current activity/fragment
                        tmpFragment = (NDEFTextFragment) fragMng.findFragmentByTag("NDEFSimpleMsgTextsp");
                        tmpFragmentTag = "NDEFSimpleMsgTextsp";
                        if (tmpFragment!=null)
                        {
                            FragmentTransaction transaction = fragMng.beginTransaction();
                            transaction.hide(tmpFragment);
                            transaction.detach(tmpFragment);
                            transaction.remove(tmpFragment);
                            transaction.commitAllowingStateLoss();
                            fragMng.executePendingTransactions();
                            tmpFragment = null;
                        }
                        NDEFTextMessage ndefText = (NDEFTextMessage) new NDEFTextMessage() ;
                        ndefText.setNDEFMessage(tnf.wellknown,NdefRecord.RTD_TEXT,ndefRecordList[i].getPayload());
                        if (tmpFragment == null) {
                                tmpFragment = NDEFTextFragment.newInstance(ndefText ,isReadOnly);
                            }
                        labelDes = "Description :";
                    }

                    else if (Arrays.equals(ndefRecordList[i].getType(),"U".getBytes()))
                    {
                        // add a NDEF URI Fragmet
                        Log.d(TAG,"Ndef Record type URI detected");
                        tmpFragment = (NDEFSimplifiedMessageFragment) fragMng.findFragmentByTag("NDEFSimpleMsgURIsp");
                        tmpFragmentTag = "NDEFSimpleMsgURIsp";
                        if (tmpFragment!=null)
                        {
                            FragmentTransaction transaction = fragMng.beginTransaction();
                            transaction.hide(tmpFragment);
                            transaction.detach(tmpFragment);
                            transaction.remove(tmpFragment);
                            transaction.commitAllowingStateLoss();
                            fragMng.executePendingTransactions();
                            tmpFragment = null;
                        }
                        byte [] ndefRecordPayload = ndefRecordList[i].getPayload().clone();
                        // Check what kind of URI we have:
                        // Check Sms use case
                         if ( (ndefRecordPayload[0] == (byte) 0x00)
                                     && (new String(ndefRecordPayload).matches(".?(sms:).*")))
                         {
                             // Create a Sms URI Message with its associated fragment
                                NDEFSmsMessage ndefSmsUri = (NDEFSmsMessage) new NDEFSmsMessage() ;
                                ndefSmsUri.setNDEFMessage(tnf.wellknown, NdefRecord.RTD_URI,ndefRecordList[i].getPayload());
                                if (tmpFragment == null) {
                                        tmpFragment = NDEFSmsFragment.newInstance(ndefSmsUri ,isReadOnly);
                                    }
                         }
                         // test Mail URI
                         else if  ( ndefRecordPayload[0] == (byte) 0x06)
                         {
                             // Create a Mail URI Message with its associated fragment
                                NDEFMailMessage ndefMailUri = (NDEFMailMessage) new NDEFMailMessage() ;
                                ndefMailUri.setNDEFMessage(tnf.wellknown, NdefRecord.RTD_URI,ndefRecordList[i].getPayload());
                                if (tmpFragment == null) {
                                        tmpFragment = NDEFMailFragment.newInstance(ndefMailUri ,isReadOnly);
                                    }
                         }
                         else //  pure URI
                         {

                            NDEFURIMessage ndefUri = (NDEFURIMessage) new NDEFURIMessage() ;
                            ndefUri.setNDEFMessage(tnf.wellknown, NdefRecord.RTD_URI,ndefRecordList[i].getPayload());
                            if (tmpFragment == null) {
                                    tmpFragment = NDEFURIFragment.newInstance(ndefUri ,isReadOnly);
                                }
                         }
                        labelDes = "Content :";
                    }

                }
            default:
                // Ndef Record Not Handled
            }

            // Message type is identified. update the fragment manager
            if (tmpFragment != null) {
                FragmentTransaction transaction = fragMng.beginTransaction();
                // Hide current fragment, if there's one
                // If this is a new fragment, add it to the fragment manager; otherwise, just show it
                transaction.add(framelayoutID[currentFrameLayoutID], tmpFragment, tmpFragmentTag);
                    // transaction.commit();
                transaction.commitAllowingStateLoss();

                tmpFragment = null;
                TextView curTxtView = (TextView) _curFragmentView.findViewById(textID[currentFrameLayoutID]);
                curTxtView.setVisibility(View.VISIBLE);
                curTxtView.setText(labelDes);
                currentFrameLayoutID++;
                }
        }
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

        /*
         EditText contactEd = (EditText) _curFragmentView.findViewById(R.id.contactEtId);
        String contact = contactEd.getText().toString();
         EditText messagesEd = (EditText) _curFragmentView.findViewById(R.id.smsmsgEtId);
        String message = messagesEd.getText().toString();

        if (!contact.isEmpty()) {
            ndefMessage = new NDEFSmsMessage(contact,message);
        }
        */
        // not yet implemented !!
        throw new RuntimeException(" WRITE Mode for SP Fragment not yet implemented");



    }

    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {
                            setNDEFSPMsg((NDEFSPMessage)ndefMsg);
                            updateSmartRecord(true);

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

