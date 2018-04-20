/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1257 $
  * Revision of last commit    :  $Rev: 1257 $
  * Date of last commit     :  $Date: 2015-10-22 16:02:56 +0200 (Thu, 22 Oct 2015) $ 
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


package com.st.util;

import com.st.Fragments.MenuToolsFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;


public class PasswordDialogFragmentExt extends PasswordDialogFragment {
/* The activity that creates an instance of this dialog fragment must
* implement this interface in order to receive event callbacks.
* Each method passes the DialogFragment in case the host needs to query it. */

    /*
public interface NoticeDialogListener {
   public void onDialogPositiveClick(DialogFragment dialog);
   public void onDialogNegativeClick(DialogFragment dialog);
}
*/

/**
     * Defines
     */

    /**
     * Attributes
     */
// Use this instance of the interface to deliver action events
//NoticeDialogListener _Listener;

private String     _updateRightstring; // use in case of Read lock Management
private EditText   _updateRightText;   // use in case of Read lock Management


    /**
     * Methods
     */
    // Constructor
    public static PasswordDialogFragmentExt newInstance(String mTitle, String mAlertMsg,
            String mOKBtnStr, String mCancelBtnStr) {
        PasswordDialogFragmentExt fragment = new PasswordDialogFragmentExt();
        fragment.setTitle(mTitle);
        fragment.setMessage(mAlertMsg);
        fragment.setOKBtn(mOKBtnStr);
        fragment.setCancelBtn(mCancelBtnStr);
        //fragment.setNeutralBtn(mNeutralBtnStr);
        return fragment;
    }

    public PasswordDialogFragmentExt() {
    }

    // Accessors
    public void setTitle(String mTitle) {
        _TitleStr = mTitle;
    }
    public void setMessage(String mAlertMsg) {
        _AlertStr = mAlertMsg;
    }
    public void setOKBtn(String mOKBtnStr) {
        _okButtonStr = mOKBtnStr;
    }
    public void setCancelBtn(String mCancelBtnStr) {
        _cancelButtonStr = mCancelBtnStr;
    }
    public void setNeutralBtn(String mNeutralBtnStr) {
        _neutralButtonStr = mNeutralBtnStr;
    }

    public void setPassword(String password)
    {
        _passwordstring = password;
    }

    public void setUpdateRight(String updateRight)
    {
        _updateRightstring = updateRight;
    }

    public void resetPassword()
    {
        _updateRightstring = DEFAULT_PASSWORD;
    }
// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
@Override
public void onAttach(Activity activity) {
   super.onAttach(activity);

}

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
   // Use the Builder class for convenient dialog construction

   _passwordEditText = new EditText(getActivity());
   _passwordEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
   
   _updateRightText= new EditText(getActivity());
   _updateRightText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
   
   
   InputFilter[] filters = new InputFilter[2];
   filters[1] = new InputFilter.LengthFilter(8);
   filters[0] = new InputFilter(){
   @Override
   public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

       String TempString ="";
       if (end > start) {
           char[] acceptedChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F','0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

           for (int index = start; index < end; index++) {                                         
               if (new String(acceptedChars).indexOf(source.charAt(index)) != -1) {
                   TempString = TempString+source.charAt(index);

               }               
           }
       }
       return TempString;
   }
};
   _passwordEditText.setFilters(filters);
   _updateRightText.setFilters(filters);
   
   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
   builder.setMessage(_AlertStr)
          .setTitle(_TitleStr)
          .setPositiveButton(_okButtonStr, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                  // Send the positive button event back to the host activity
                  // _Listener.onDialogPositiveClick(PasswordDialogFragment.this);
                  Intent i =getActivity().getIntent();
                  i.putExtra("PasswordState", true);
                  String currentPassword = _passwordEditText.getText().toString();
                  if ((currentPassword.length()%2)==1)
                  {
                      currentPassword = currentPassword + "F"; // Password must be even. Add systematically a F in case of Odd password.
                  }
                  i.putExtra("password",_passwordEditText.getText().toString());
                  
                  String currentRightAccess = _updateRightText.getText().toString();
                  if ((currentRightAccess.length()%2)==1)
                  {
                      currentRightAccess = currentRightAccess + "F"; // Password must be even. Add systematically a F in case of Odd password.
                  }
                  i.putExtra("rightAccess",_updateRightText.getText().toString());
                  
                  getTargetFragment().onActivityResult(getTargetRequestCode(), MenuToolsFragment.RESULT_OK, i);
              }
          })
          .setNegativeButton(_cancelButtonStr, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                  // Send the negative button event back to the host activity
                   //_Listener.onDialogNegativeClick(PasswordDialogFragment.this);

                  Intent i =getActivity().getIntent();
                  i.putExtra("PasswordState", false);
                  getTargetFragment().onActivityResult(getTargetRequestCode(), MenuToolsFragment.RESULT_OK, i);
                  
              }
          })
          .setView(_passwordEditText)
             .setView(_updateRightText).create();
          /*.setNeutralButton(_neutralButtonStr, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                  // Send the negative button event back to the host activity
                  _Listener.onDialogNeutralClick(PasswordDialogFragment.this);
              }
          });*/

   return builder.create();
}
}
