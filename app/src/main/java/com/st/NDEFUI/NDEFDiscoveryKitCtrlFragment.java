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

import com.st.NDEF.NDEFDiscoveryKitCtrlMessage;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFDiscoveryKitCtrlMessage.ledblinkspeed;
import com.st.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NDEFDiscoveryKitCtrlFragment extends NDEFSimplifiedMessageFragment implements OnItemSelectedListener {
    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFDiscoveryKitCtrlMessage _ndefMsg = null;
    private String TAG = this.getClass().getName();

    public final static int NB_LED = NDEFDiscoveryKitCtrlMessage.FIXEDNBLED;
    public final static int NB_TOKEN = NDEFDiscoveryKitCtrlMessage.FIXEDNBTOKENS;

    public static NDEFDiscoveryKitCtrlFragment newInstance(NDEFDiscoveryKitCtrlMessage msg, boolean readOnly) {
        NDEFDiscoveryKitCtrlFragment fragment = new NDEFDiscoveryKitCtrlFragment();
        fragment.setNDEFDiscoveryKitCtrlMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFDiscoveryKitCtrlFragment newInstance(boolean readOnly) {
        NDEFDiscoveryKitCtrlFragment fragment = new NDEFDiscoveryKitCtrlFragment();
        fragment.setNDEFDiscoveryKitCtrlMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFDiscoveryKitCtrlFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFDiscoveryKitCtrlMsg(NDEFDiscoveryKitCtrlMessage msg) { _ndefMsg = msg; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void activateWidget(boolean state)
    {
        CheckBox rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led1);
        rbutton.setEnabled(state);
        rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led2);
        rbutton.setEnabled(state);
        rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led3);
        rbutton.setEnabled(state);
        rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led4);
        rbutton.setEnabled(state);

        ToggleButton tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled1);
        tbutton.setEnabled(state);
        tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled2);
        tbutton.setEnabled(state);
        tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled3);
        tbutton.setEnabled(state);
        tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled4);
        tbutton.setEnabled(state);

        SeekBar seekBar = (SeekBar) _curFragmentView.findViewById(R.id.blinkseekbar1);
        seekBar.setEnabled(state);

        EditText editText = (EditText) _curFragmentView.findViewById(R.id.edit_line1);
        editText.setEnabled(state);
        if (state == false ) editText.setRawInputType(InputType.TYPE_NULL);
        else editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText = (EditText) _curFragmentView.findViewById(R.id.edit_line2);
        editText.setEnabled(state);
        if (state == false ) editText.setRawInputType(InputType.TYPE_NULL);
        else editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText = (EditText) _curFragmentView.findViewById(R.id.edit_line3);
        editText.setEnabled(state);
        if (state == false ) editText.setRawInputType(InputType.TYPE_NULL);
        else editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText = (EditText) _curFragmentView.findViewById(R.id.edit_line4);
        editText.setEnabled(state);
        if (state == false ) editText.setRawInputType(InputType.TYPE_NULL);
        else editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText = (EditText) _curFragmentView.findViewById(R.id.edit_line5);
        editText.setEnabled(state);
        if (state == false ) editText.setRawInputType(InputType.TYPE_NULL);
        else editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText = (EditText) _curFragmentView.findViewById(R.id.edit_line6);
        editText.setEnabled(state);
        if (state == false ) editText.setRawInputType(InputType.TYPE_NULL);
        else editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText = (EditText) _curFragmentView.findViewById(R.id.edit_line7);
        editText.setEnabled(state);
        if (state == false ) editText.setRawInputType(InputType.TYPE_NULL);
        else editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText = (EditText) _curFragmentView.findViewById(R.id.edit_line8);
        editText.setEnabled(state);
        if (state == false ) editText.setRawInputType(InputType.TYPE_NULL);
        else editText.setRawInputType(InputType.TYPE_CLASS_TEXT);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_discoverykitctrl, container, false);

        // Deactivate the view(s) in case of ReadOnly invocation
        if (isReadOnly()) {
            activateWidget(false);
        } else {
            activateWidget(true);
        }
        return _curFragmentView;
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart Fragment");
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
        NDEFDiscoveryKitCtrlMessage ndefMessage = null;


    ndefMessage = new NDEFDiscoveryKitCtrlMessage();

    // Get Let Status

    CheckBox  rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led1);
    ToggleButton tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled1);

    ndefMessage.set_ledInitialState(0, rbutton.isChecked());
    ndefMessage.set_ledBlinkState(0, tbutton.isChecked());

    rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led2);
    tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled2);
    ndefMessage.set_ledInitialState(1, rbutton.isChecked());
    ndefMessage.set_ledBlinkState(1, tbutton.isChecked());

    rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led3);
    tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled3);
    ndefMessage.set_ledInitialState(2, rbutton.isChecked());
    ndefMessage.set_ledBlinkState(2, tbutton.isChecked());

    rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led4);
    tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled4);
    ndefMessage.set_ledInitialState(3, rbutton.isChecked());
    ndefMessage.set_ledBlinkState(3, tbutton.isChecked());

    // Blink state
    SeekBar seekbar = (SeekBar) _curFragmentView.findViewById(R.id.blinkseekbar1);
    int value = seekbar.getProgress();
    switch (value) {
        case 3 :
            ndefMessage.set_ledBlinkSpeed(NDEFDiscoveryKitCtrlMessage.ledblinkspeed.HIGH);
            break;
        case 2 :
            ndefMessage.set_ledBlinkSpeed(NDEFDiscoveryKitCtrlMessage.ledblinkspeed.MEDIUM);
            break;
        case 0 :
            ndefMessage.set_ledBlinkSpeed(NDEFDiscoveryKitCtrlMessage.ledblinkspeed.OFF);
            break;
        default:
            ndefMessage.set_ledBlinkSpeed(NDEFDiscoveryKitCtrlMessage.ledblinkspeed.LOW);
    }

    //Now retrieve lines values

    //Line 1
    EditText editText = (EditText) _curFragmentView.findViewById(R.id.edit_line1);
    TextView textView = (TextView) _curFragmentView.findViewById(R.id.tvline1);
    String removedeparator = textView.getText().toString().split(":")[0];
    ndefMessage.setCtrlTockensArray(0,removedeparator,editText.getText().toString(),(short)0xFFFF,(short)0x0000);

    //Line 2
    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line2);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline2);
    removedeparator = textView.getText().toString().split(":")[0];
    ndefMessage.setCtrlTockensArray(1,removedeparator,editText.getText().toString(),(short)0xFFFF,(short)0x0000);

    //Line 3
    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line3);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline3);
    removedeparator = textView.getText().toString().split(":")[0];
    ndefMessage.setCtrlTockensArray(2,removedeparator,editText.getText().toString(),(short)0xFFFF,(short)0x0000);


    //Line 4
    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line4);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline4);
    removedeparator = textView.getText().toString().split(":")[0];
    ndefMessage.setCtrlTockensArray(3,removedeparator,editText.getText().toString(),(short)0xFFFF,(short)0x0000);

    //Line 5
    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line5);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline5);
    removedeparator = textView.getText().toString().split(":")[0];
    ndefMessage.setCtrlTockensArray(4,removedeparator,editText.getText().toString(),(short)0xFFFF,(short)0x0000);

    //Line 6
    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line6);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline6);
    removedeparator = textView.getText().toString().split(":")[0];
    ndefMessage.setCtrlTockensArray(5,removedeparator,editText.getText().toString(),(short)0xFFFF,(short)0x0000);

    //Line 7
    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line7);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline7);
    removedeparator = textView.getText().toString().split(":")[0];
    ndefMessage.setCtrlTockensArray(6,removedeparator,editText.getText().toString(),(short)0xFFFF,(short)0x0000);

    //Line 8
    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line8);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline8);
    removedeparator = textView.getText().toString().split(":")[0];
    ndefMessage.setCtrlTockensArray(7,removedeparator,editText.getText().toString(),(short)0xFFFF,(short)0x0000);

    return ndefMessage;
    }

    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {

     // Set Led States

    CheckBox  rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led1);
    ToggleButton tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled1);

    rbutton.setChecked(((NDEFDiscoveryKitCtrlMessage)ndefMsg).get_ledInitialState(0));
    tbutton.setChecked(((NDEFDiscoveryKitCtrlMessage)ndefMsg).get_ledBlinkState(0));

    rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led2);
    tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled2);

    rbutton.setChecked(((NDEFDiscoveryKitCtrlMessage)ndefMsg).get_ledInitialState(1));
    tbutton.setChecked(((NDEFDiscoveryKitCtrlMessage)ndefMsg).get_ledBlinkState(1));

    rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led3);
    tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled3);

    rbutton.setChecked(((NDEFDiscoveryKitCtrlMessage)ndefMsg).get_ledInitialState(2));
    tbutton.setChecked(((NDEFDiscoveryKitCtrlMessage)ndefMsg).get_ledBlinkState(2));

    rbutton = (CheckBox ) _curFragmentView.findViewById(R.id.radio_led4);
    tbutton = (ToggleButton) _curFragmentView.findViewById(R.id.togglebuttonled4);

    rbutton.setChecked(((NDEFDiscoveryKitCtrlMessage)ndefMsg).get_ledInitialState(3));
    tbutton.setChecked(((NDEFDiscoveryKitCtrlMessage)ndefMsg).get_ledBlinkState(3));

     // Set blinking States
      // To be done
    SeekBar seekbar = (SeekBar) _curFragmentView.findViewById(R.id.blinkseekbar1);
    switch (((NDEFDiscoveryKitCtrlMessage)ndefMsg).get_ledBlinkSpeed())
    {
    case HIGH:
        seekbar.setProgress(3);
        break;
    case MEDIUM:
        seekbar.setProgress(2);
        break;
    case OFF:
        seekbar.setProgress(0);
        break;
    default:
        seekbar.setProgress(1);

    }


     // Set Strings
         // labels are not yet handled

    //Line 1
    EditText editText = (EditText) _curFragmentView.findViewById(R.id.edit_line1);
    TextView textView = (TextView) _curFragmentView.findViewById(R.id.tvline1);
    editText.setText(((NDEFDiscoveryKitCtrlMessage)ndefMsg).getCtrlTocken(0).get_value());

    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line2);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline2);
    editText.setText(((NDEFDiscoveryKitCtrlMessage)ndefMsg).getCtrlTocken(1).get_value());

    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line3);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline3);
    editText.setText(((NDEFDiscoveryKitCtrlMessage)ndefMsg).getCtrlTocken(2).get_value());

    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line4);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline4);
    editText.setText(((NDEFDiscoveryKitCtrlMessage)ndefMsg).getCtrlTocken(3).get_value());

    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line5);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline5);
    editText.setText(((NDEFDiscoveryKitCtrlMessage)ndefMsg).getCtrlTocken(4).get_value());

    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line6);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline6);
    editText.setText(((NDEFDiscoveryKitCtrlMessage)ndefMsg).getCtrlTocken(5).get_value());

    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line7);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline7);
    editText.setText(((NDEFDiscoveryKitCtrlMessage)ndefMsg).getCtrlTocken(6).get_value());

    editText = (EditText) _curFragmentView.findViewById(R.id.edit_line8);
    textView = (TextView) _curFragmentView.findViewById(R.id.tvline8);
    editText.setText(((NDEFDiscoveryKitCtrlMessage)ndefMsg).getCtrlTocken(7).get_value());

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

