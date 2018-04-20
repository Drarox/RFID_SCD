/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1616 $
  * Revision of last commit    :  $Rev: 1616 $
  * Date of last commit     :  $Date: 2016-02-03 19:03:03 +0100 (Wed, 03 Feb 2016) $ 
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


import com.st.demo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;



public class InformDialogFragment extends DialogFragment {
    /**
     * Defines
     */

    /**
     * Attributes
     */
    private String _TitleStr = null;
    private String _InformStr = null;

    /**
     * Methods
     */
    // Constructor
    public static InformDialogFragment newInstance(String mTitle, String mInformMsg) {
        InformDialogFragment fragment = new InformDialogFragment();
        fragment.setTitle(mTitle);
        fragment.setMessage(mInformMsg);
        return fragment;
    }

    public InformDialogFragment() {
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface InformDialogListener {
        public void onInformDialogPositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    InformDialogListener mListener;

    // Accessors
    public void setTitle(String mTitle) {
        _TitleStr = mTitle;
    }
    public void setMessage(String mInformMsg) {
        _InformStr = mInformMsg;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (InformDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement InformDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(_InformStr)
               .setTitle(_TitleStr)
               .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // No specific action required, come back to previous screen
                       mListener.onInformDialogPositiveClick(InformDialogFragment.this);
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
