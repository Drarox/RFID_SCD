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

import java.util.Arrays;

import com.st.Fragments.MenuToolsFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;


public class PasswordDialogFragment extends DialogFragment {
 /* The activity that creates an instance of this dialog fragment must
  * implement this interface in order to receive event callbacks.
  * Each method passes the DialogFragment in case the host needs to query it. */

 public interface NoticeDialogListener {
     public void onDiagPasswordPositiveClick(DialogFragment dialog);
     public void onDiagPasswordNegativeClick(DialogFragment dialog);
 }

 /**
     * Defines
     */

    /**
     * Attributes
     */
 // Use this instance of the interface to deliver action events
 NoticeDialogListener _Listener;

    protected final static String DEFAULT_PASSWORD = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
    protected final static String DEFAULT_PASSWORD64 = "FFFFFFFFFFFFFFFF";
    protected final static String DEFAULT_PASSWORD32 = "FFFFFFFF";
 protected String     _passwordstring;
 protected EditText   _passwordEditText;
 
 protected String _TitleStr = null;
 protected String _AlertStr = null;
 protected String _okButtonStr = null;
 protected String _cancelButtonStr = null;
 protected String _neutralButtonStr = null;
 public String _mComputedPassword;
protected int mNbOfBits;
    /**
     * Methods
     */
    // Constructor
    public static PasswordDialogFragment newInstance(String mTitle, String mAlertMsg,
                                                     String mOKBtnStr, String mCancelBtnStr) {
        PasswordDialogFragment fragment = new PasswordDialogFragment();
        fragment.setTitle(mTitle);
        fragment.setMessage(mAlertMsg);
        fragment.setOKBtn(mOKBtnStr);
        fragment.setCancelBtn(mCancelBtnStr);
        //fragment.setNeutralBtn(mNeutralBtnStr);
        return fragment;
    }
    public static PasswordDialogFragment newInstance(String mTitle, String mAlertMsg,
                                                     String mOKBtnStr, String mCancelBtnStr, int nbBits) {
        //PasswordDialogFragment fragment = new PasswordDialogFragment(nbBits);
        PasswordDialogFragment fragment = new PasswordDialogFragment();
        fragment.setNbBits(nbBits);
        fragment.setTitle(mTitle);
        fragment.setMessage(mAlertMsg);
        fragment.setOKBtn(mOKBtnStr);
        fragment.setCancelBtn(mCancelBtnStr);
        //fragment.setNeutralBtn(mNeutralBtnStr);
        return fragment;
    }

    public PasswordDialogFragment() {
        mNbOfBits = 32;
    }
    //public PasswordDialogFragment(int nbBits) {
    //    mNbOfBits = nbBits;
    //}

    // Accessors
    public void setNbBits(int nbOfBits) {
        mNbOfBits = nbOfBits;
    }
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

    public void resetPassword()
    {
        _passwordstring = DEFAULT_PASSWORD;
    }
 // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
 @Override
 public void onAttach(Activity activity) {
     super.onAttach(activity);
     // Verify that the host activity implements the callback interface
    try {
         // Instantiate the NoticeDialogListener so we can send events to the host
         _Listener = (NoticeDialogListener) activity;
     } catch (ClassCastException e) {
         // The activity doesn't implement the interface, throw exception
         _Listener = null; // no listener activity - we address a fragment
     }
     
 }

 @Override
 public Dialog onCreateDialog(Bundle savedInstanceState) {
     // Use the Builder class for convenient dialog construction

     _passwordEditText = new EditText(getActivity());
     _passwordEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

     InputFilter[] filters = new InputFilter[2];
     filters[1] = new InputFilter.LengthFilter(this.mNbOfBits);
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
     
     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     builder.setMessage(_AlertStr)
            .setTitle(_TitleStr)
            .setPositiveButton(_okButtonStr, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Send the positive button event back to the host activity
 

                    String currentPassword = _passwordEditText.getText().toString();
                    if ((currentPassword.length()%2)==1)
                    {
                        currentPassword = currentPassword + "F"; // Password must be even. Add systematically a F in case of Odd password.
                    }
                    _mComputedPassword = _passwordEditText.getText().toString();
                    
                       if (_Listener != null)
                    {
                     _Listener.onDiagPasswordPositiveClick(PasswordDialogFragment.this);
                    }
                    else
                    {
                        Intent i =getActivity().getIntent();
                        i.putExtra("PasswordState", true);
                        i.putExtra("password",_passwordEditText.getText().toString());
                        if (getTargetFragment() != null)
                        getTargetFragment().onActivityResult(getTargetRequestCode(), MenuToolsFragment.RESULT_OK, i);
                    }
                }
            })
            .setNegativeButton(_cancelButtonStr, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Send the negative button event back to the host activity
                    if (_Listener != null)
                    {
                        _Listener.onDiagPasswordNegativeClick(PasswordDialogFragment.this);
                    }
                    Intent i =getActivity().getIntent();
                    i.putExtra("PasswordState", false);
                    if (getTargetFragment() != null)
                    getTargetFragment().onActivityResult(getTargetRequestCode(), MenuToolsFragment.RESULT_OK, i);
                }
            })
            .setView(_passwordEditText).create();


     return builder.create();
 }
}
