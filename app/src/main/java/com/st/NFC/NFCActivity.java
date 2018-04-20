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

package com.st.NFC;


import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.demo.R;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class NFCActivity extends FragmentActivity {
    /**
     * Attributes
     */

    // Current applicable tag always stored at application level,
    // only store new tag in protected variable, so that child class acts accordingly
    //protected NFCTag _newTag = null;

    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
//    private IntentFilter[] nfcFiltersArray;
//    private IntentFilter nfcFilter;
//    private String[][] nfcTechLists;

//    private ProgressDialog dialog = new ProgressDialog(NFCActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnCreate Activity");

        super.onCreate(savedInstanceState);


        // Get the new tag from NFC adapter
        // --> later will be from tag manager thread with already decoded data...
        Intent intent = getIntent();
        internalNewIntent(intent);

        // Prepare for getting next NFC Tag detection intents
        // Check for enabled NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // Create the PendingIntent, Filters and technologies that will be used in onResume, either after NFC_NOT_ENABLED or NFC_ENABLED states
        nfcPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//        nfcFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//        try {
//            nfcFilter.addDataType("*/*");
//        }
//        catch  (MalformedMimeTypeException e) {
//            throw new RuntimeException("fail", e);
//        }
//        nfcFiltersArray = new IntentFilter[] {nfcFilter};
//        nfcTechLists = new String[][] { new String[] { android.nfc.tech.Ndef.class.getName()} };
     }

    @Override
    protected void onNewIntent(Intent intent) 
    {
        Log.v(this.getClass().getName(), "OnNewIntent Activity");

        ProgressDialog dialog = new ProgressDialog(NFCActivity.this);
        dialog.setMessage(getString(R.string.nfc_act_new_tag_reading));
        try {
            dialog.show();
        }catch (Exception e) {

        }

        super.onNewIntent(intent);

        internalNewIntent(intent);
        try {
            dialog.dismiss();
        } catch (Exception e) {
        }

    }

   @Override
    protected void onResume() 
    {
        Log.v(this.getClass().getName(), "OnResume Activity");

        // TODO Auto-generated method stub
        super.onResume();

        // Route the NFC events to the next activity (Tag Info ?)
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null /*nfcFiltersArray*/, null /*nfcTechLists*/);
   }


    @Override
    protected void onPause() 
    {
        Log.v(this.getClass().getName(), "OnPause Activity");

        // TODO Auto-generated method stub
        super.onPause();

        nfcAdapter.disableForegroundDispatch(this);

        return;
    }

    private void internalNewIntent (Intent intent) {
        String action = intent.getAction();
        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
            || (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
            || (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)))
        {
            Tag rawTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NFCTag tmpTag = null;

            if (rawMsgs != null) {
                NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                tmpTag = new NFCTag(rawTag, msgs);
            } else {
                tmpTag = new NFCTag(rawTag);
            }

            NFCApplication.getApplication().setCurrentTag(tmpTag);
        }
    }
}
