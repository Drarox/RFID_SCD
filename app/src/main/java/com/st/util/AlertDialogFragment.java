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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class AlertDialogFragment extends DialogFragment {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogNeutralClick(DialogFragment dialog);
    }

    /**
     * Defines
     */

    /**
     * Attributes
     */
    // Use this instance of the interface to deliver action events
    NoticeDialogListener _Listener;

    private String _TitleStr = null;
    private String _AlertStr = null;
    private String _okButtonStr = null;
    private String _cancelButtonStr = null;
    private String _neutralButtonStr = null;

    /**
     * Methods
     */
    // Constructor
    public static AlertDialogFragment newInstance(String mTitle, String mAlertMsg,
            String mOKBtnStr, String mCancelBtnStr, String mNeutralBtnStr) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setTitle(mTitle);
        fragment.setMessage(mAlertMsg);
        fragment.setOKBtn(mOKBtnStr);
        fragment.setCancelBtn(mCancelBtnStr);
        fragment.setNeutralBtn(mNeutralBtnStr);
        return fragment;
    }

    public AlertDialogFragment() {
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
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(_AlertStr)
               .setTitle(_TitleStr)
               .setPositiveButton(_okButtonStr, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the positive button event back to the host activity
                       _Listener.onDialogPositiveClick(AlertDialogFragment.this);
                   }
               })
               .setNegativeButton(_cancelButtonStr, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the negative button event back to the host activity
                       _Listener.onDialogNegativeClick(AlertDialogFragment.this);
                   }
               })
               .setNeutralButton(_neutralButtonStr, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the negative button event back to the host activity
                       _Listener.onDialogNeutralClick(AlertDialogFragment.this);
                   }
               });
       // Create the AlertDialog object and return it
        return builder.create();
    }
}
