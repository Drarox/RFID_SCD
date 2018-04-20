
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

import com.st.NDEF.NDEFAarMessage;
import com.st.NDEF.NDEFMultipleRecordMessage;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFSmsMessage;
import com.st.NDEF.NDEFTextMessage;
import com.st.NDEF.NDEFURIMessage;
import com.st.NDEF.NDEFVCardMessage;
import com.st.NDEF.stnfcndefhandler;
import com.st.NFC.NFCApplication;
import com.st.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NDEFMultipleRecordFragment extends NDEFSimplifiedMessageFragment {
    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFMultipleRecordMessage _ndefMsg = null;
    NDEFMultipleRecordMessage _ndefMsgUI = null;


    public static NDEFMultipleRecordFragment newInstance(NDEFMultipleRecordMessage newMsg, boolean readOnly) {
        NDEFMultipleRecordFragment fragment = new NDEFMultipleRecordFragment();
        fragment.setNDEFMsg(newMsg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFMultipleRecordFragment newInstance(boolean readOnly) {
        NDEFMultipleRecordFragment fragment = new NDEFMultipleRecordFragment();
        fragment.setNDEFMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFMultipleRecordFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFMsg (NDEFMultipleRecordMessage msg) {
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
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_multiplerecord, container, false);

        // Deactivate the view(s) in case of ReadOnly invocation
        if (isReadOnly()) {
        }
        // No need for "else" case: default behavior is for "Write" use case

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
    // Function for "Write": msg to send to a tag
    public NDEFSimplifiedMessage getNDEFSimplifiedMessage() {
        return _ndefMsgUI;
    }

    // Function for "Read": msg received from a tag
    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {
        _ndefMsgUI = (NDEFMultipleRecordMessage) ndefMsg;
        // Identify the record
        stnfcndefhandler ndefHDL = _ndefMsgUI.getNdefHandler();
        NdefMessage mess = ndefHDL.getNdefMessage();
        TextView tv = (TextView) _curFragmentView.findViewById(R.id.NDEFSimpleTextFragmentMultipleRecordViewId);

        ListView maListViewPerso;
        maListViewPerso = (ListView) _curFragmentView.findViewById(R.id.listviewrecords);
        NdefRecord[] ndefRecords = mess.getRecords();
        if (ndefRecords == null) {
            tv.setText("Multiple records message detected : " + " 0 record !");
            return;
        } else {
            tv.setText("Multiple records message detected : " + ndefRecords.length);
            updateRecordsListView(ndefRecords,maListViewPerso);
        }


    }

    private void updateRecordsListView(NdefRecord[]ndefRecords, ListView maListViewPerso) {
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        int recordCount = 0;
        listItem.clear();
        for (NdefRecord record : ndefRecords) {
            short tnf = record.getTnf();
            String type = new String(record.getType());
            //tv.append("\n");
            if (tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(type.getBytes(), NdefRecord.RTD_URI)) {
                String url = new String(record.getPayload());
                map = new HashMap<String, String>();
                map.put("title", recordCount + " - TNF_WELL_KNOWN");
                map.put("description", "RTD_URI");
                map.put("img", String.valueOf(R.drawable.internet));
                listItem.add(map);
                //tv.append("URI: " + url);

                NDEFURIMessage newMsg = new NDEFURIMessage();
                newMsg.setNDEFMessage(com.st.NDEF.tnf.wellknown,NdefRecord.RTD_URI,record.getPayload());
                NFCApplication.getApplication().storeNDEFMessageRecord(newMsg);
            }
            if (tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(type.getBytes(), NdefRecord.RTD_TEXT)) {
                String Text = new String(record.getPayload());
                //tv.append("TEXT: " + Text);
                map = new HashMap<String, String>();
                map.put("title", recordCount + " - TNF_WELL_KNOWN");
                map.put("description", "RTD_TEXT");
                map.put("img", String.valueOf(R.drawable.text));
                listItem.add(map);

                NDEFTextMessage newMsg = new NDEFTextMessage();
                newMsg.setNDEFMessage(com.st.NDEF.tnf.wellknown,NdefRecord.RTD_TEXT,record.getPayload());
                NFCApplication.getApplication().storeNDEFMessageRecord(newMsg);

            }

            if (tnf == NdefRecord.TNF_EXTERNAL_TYPE ) {
                //tv.append("EXTERNAL_TYPE : ");
                String payload = record.toMimeType();
                if (Arrays.equals(type.getBytes(),"android.com:pkg".getBytes())) {
                    //tv.append("EXTERNAL_TYPE : AAR");
                    map = new HashMap<String, String>();
                    map.put("title", recordCount + " - TNF_EXTERNAL_TYPE");
                    map.put("description", "AAR");
                    map.put("img", String.valueOf(R.drawable.androidapplication));
                    listItem.add(map);

                    NDEFAarMessage newMsg = new NDEFAarMessage();
                    newMsg.setNDEFMessage(com.st.NDEF.tnf.external,type.getBytes(),record.getPayload());
                    NFCApplication.getApplication().storeNDEFMessageRecord(newMsg);
                } else {
                    //tv.append("EXTERNAL_TYPE : XXX");
                }
            }

            if (tnf == NdefRecord.TNF_ABSOLUTE_URI ) {
                //tv.append("ABSOLUTE_URI : ");
            }
            if (tnf == NdefRecord.TNF_EMPTY ) {
                //tv.append("EMPTY : ");
            }
            if (tnf == NdefRecord.TNF_MIME_MEDIA ) {
                //tv.append("MIME_MEDIA : ");
                String payload = new String(record.getPayload());
                if (payload.startsWith("sms:")) {
                    //tv.append("MIME_MEDIA : sms");
                    map = new HashMap<String, String>();
                    map.put("title", recordCount + " - TNF_MIME_MEDIA");
                    map.put("description", "sms");
                    map.put("img", String.valueOf(R.drawable.sms));
                    listItem.add(map);

                    NDEFSmsMessage newMsg = new NDEFSmsMessage();
                    newMsg.setNDEFMessage(com.st.NDEF.tnf.media,type.getBytes(),record.getPayload());
                    NFCApplication.getApplication().storeNDEFMessageRecord(newMsg);

                } else if (Arrays.equals(type.getBytes(),"text/x-vCard".getBytes())) {
                    //tv.append("MIME_MEDIA : VCard");
                    map = new HashMap<String, String>();
                    map.put("title", recordCount + " - TNF_MIME_MEDIA");
                    map.put("description", "x-vCard");
                    map.put("img", String.valueOf(R.drawable.vcardpicture));
                    listItem.add(map);

                    NDEFVCardMessage newMsg = new NDEFVCardMessage();
                    newMsg.setNDEFMessage(com.st.NDEF.tnf.media,type.getBytes(),record.getPayload());
                    NFCApplication.getApplication().storeNDEFMessageRecord(newMsg);

                } else {
                    //tv.append("MIME_MEDIA : XXX");
                }
            }

            if (tnf == NdefRecord.TNF_UNKNOWN ) {
                //tv.append("UNKNOWN : ");
            }

            recordCount++;
        }
        SimpleAdapter mSchedule = new SimpleAdapter(this.getActivity().getBaseContext(), listItem, R.layout.rowlayout_record,
                new String[] {"img", "title", "description"}, new int[] {R.id.img, R.id.title, R.id.description});
        maListViewPerso.setAdapter(mSchedule);
        //mSchedule.notifyDataSetChanged();
    }

 public void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
 }
 
}
