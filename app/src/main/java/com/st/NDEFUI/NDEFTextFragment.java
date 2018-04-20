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

import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFTextMessage;
import com.st.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NDEFTextFragment extends NDEFSimplifiedMessageFragment {
    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFTextMessage _ndefMsg = null;

    public static NDEFTextFragment newInstance(NDEFTextMessage msg, boolean readOnly) {
        NDEFTextFragment fragment = new NDEFTextFragment();
        fragment.setNDEFTextMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFTextFragment newInstance(boolean readOnly) {
        NDEFTextFragment fragment = new NDEFTextFragment();
        fragment.setNDEFTextMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFTextFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFTextMsg (NDEFTextMessage msg) {
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
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_text, container, false);

        // Deactivate the view(s) in case of ReadOnly invocation
        if (isReadOnly()) {
            EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFSimpleTextFragmentTextViewId);
            txtView.setEnabled(false);
            txtView.setRawInputType(InputType.TYPE_NULL);
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
    // Function for "Write": msg to send to a tag
    public NDEFSimplifiedMessage getNDEFSimplifiedMessage() {
        NDEFTextMessage ndefMessage = null;

        EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFSimpleTextFragmentTextViewId);
        String text = txtView.getText().toString();

        if (!text.isEmpty()) {
            ndefMessage = new NDEFTextMessage();
            ndefMessage.setText(text);
        }

        return ndefMessage;
    }

    // Function for "Read": msg received from a tag
    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {
        EditText txtView = (EditText) _curFragmentView.findViewById(R.id.NDEFSimpleTextFragmentTextViewId);
        txtView.setText(((NDEFTextMessage) ndefMsg).getText());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
}
