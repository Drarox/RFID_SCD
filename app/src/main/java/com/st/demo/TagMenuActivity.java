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
package com.st.demo;



import com.st.demo.TagMenuDetailsActivity;
import com.st.Fragments.TagHeaderFragment;
import com.st.NFC.NFCActivity;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.NfcMenus;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TagMenuActivity extends NFCActivity {
    // Constants for NFC menus definition
    // - state is detected at class creation
    // - STATE_NFC_UNKNOWN is the initial state
    // - STATE_NFC_NOT_AVAILABLE is a final state (no NFC chip in current device)
    // - STATE_NFC_NOT_ENABLED is a transient state: NFC activation can be detected in onResume method (if user switched to paremeters menu and came back to the application)
    // - STATE_NFC_ENABLED is a transient state: NFC can be deactivated by end user, then need to be detected
/*    public enum NfcMenus {
        NFC_MENU_DUMMY,
        NFC_MENU_SMART_VIEW_NDEF_FILE,
        NFC_MENU_TAG_INFO,
        NFC_MENU_NDEF_FILES,
        NFC_MENU_CC_FILE,
        NFC_MENU_SYS_FILE,
        NFC_MENU_TOOLS,
        NFC_MENU_BIN_FILE,
        NFC_MENU_M24LR_PWD,
        NFC_MENU_M24LR_LOCK,
        NFC_MENU_M24LR_EH,
        NFC_MENU_M24SR_PWD,
        NFC_MENU_M24SR_IT
    }
*/
    // Menus relative Indexes
    private Button _ndefMenuBtn = null;
    private Button _ndefSmartviewBtn = null;
    private Button _ccMenuBtn = null;
    private Button _sysMenuBtn = null;
    private Button _binMenuBtn = null;
    private Button _m24lrPwdMenuBtn = null;
    private Button _m24lrLockMenuBtn = null;
    private Button _m24lrEhMenuBtn = null;
    private Button _m24srPwdMenuBtn = null;
    private Button _m24srItMenuBtn = null;

    // Menus relative Indexes
    private int _ndefSmartviewIdx;
    private int _ndefMenuRelIdx;
    private int _ccMenuRelIdx;
    private int _sysMenuRelIdx;
    private int _binMenuRelIdx;
    private int _m24lrPwdMenuRelIdx;
    private int _m24lrLockMenuRelIdx;
    private int _m24lrEhMenuRelIdx;
    private int _m24srPwdMenuRelIdx;
    private int _m24srItMenuRelIdx;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnCreate Activity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_menu);

        // "NDEF" button
        _ndefSmartviewBtn = (Button) findViewById(R.id.TmActNDEFBtnAreaId).findViewById(R.id.BasicBtnId);
        _ndefSmartviewBtn.setText(R.string.tm_act_SMARTNDEF_btn_txt);
        _ndefSmartviewBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _ndefSmartviewIdx);
                startActivity(intent);
            }
        });

        // "NDEF" button
        _ndefMenuBtn = (Button) findViewById(R.id.TmActNDEFBtnAreaId).findViewById(R.id.BasicBtnId);
        _ndefMenuBtn.setText(R.string.tm_act_NDEF_btn_txt);
        _ndefMenuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _ndefMenuRelIdx);
                startActivity(intent);
            }
        });

        // "CC file" button
        _ccMenuBtn = (Button) findViewById(R.id.TmActCCBtnAreaId).findViewById(R.id.BasicBtnId);
        _ccMenuBtn.setText(R.string.tm_act_CC_btn_txt);
        _ccMenuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _ccMenuRelIdx);
                startActivity(intent);
            }
        });

        // "SYSTEM file" button
        _sysMenuBtn = (Button) findViewById(R.id.TmActSystemBtnAreaId).findViewById(R.id.BasicBtnId);
        _sysMenuBtn.setText(R.string.tm_act_system_btn_txt);
        _sysMenuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _sysMenuRelIdx);
                startActivity(intent);
            }
        });

        // "BIN content" button
        _binMenuBtn = (Button) findViewById(R.id.TmActBinBtnAreaId).findViewById(R.id.BasicBtnId);
        _binMenuBtn.setText(R.string.tm_act_bin_btn_txt);
        _binMenuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _binMenuRelIdx);
                startActivity(intent);
            }
        });

        // M24LR specific buttons
        // "Password" button
        _m24lrPwdMenuBtn = (Button) findViewById(R.id.TmActM24LRPwdBtnAreaId).findViewById(R.id.BasicBtnId);
        _m24lrPwdMenuBtn.setText(R.string.tm_act_m24lr_pwd_btn_txt);
//        _m24lrPwdMenuBtn.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.tm_act_m24lr_pwd_btn_width));
        _m24lrPwdMenuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _m24lrPwdMenuRelIdx);
                startActivity(intent);
            }
        });

        // "Lock sector" button
        _m24lrLockMenuBtn = (Button) findViewById(R.id.TmActM24LRLockBtnAreaId).findViewById(R.id.BasicBtnId);
        _m24lrLockMenuBtn.setText(R.string.tm_act_m24lr_lock_btn_txt);
//        m24lrLockMenuBtn.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.tm_act_m24lr_lock_btn_width));
        _m24lrLockMenuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _m24lrLockMenuRelIdx);
                startActivity(intent);
            }
        });

        // "Energy Harvesting" button
        _m24lrEhMenuBtn = (Button) findViewById(R.id.TmActM24LREHBtnAreaId).findViewById(R.id.BasicBtnId);
        _m24lrEhMenuBtn.setText(R.string.tm_act_m24lr_eh_btn_txt);
        _m24lrEhMenuBtn.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.tm_act_m24lr_eh_btn_width));
        _m24lrEhMenuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _m24lrEhMenuRelIdx);
                startActivity(intent);
            }
        });

        // M24SR specific buttons
        // "Manage pwd & access rights" button
        _m24srPwdMenuBtn = (Button) findViewById(R.id.TmActM24SRPwdBtnAreaId).findViewById(R.id.BasicBtnId);
        _m24srPwdMenuBtn.setText(R.string.tm_act_m24sr_pwd_btn_txt);
        _m24srPwdMenuBtn.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.tm_act_m24sr_pwd_btn_width));
        _m24srPwdMenuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _m24srPwdMenuRelIdx);
                startActivity(intent);
            }
        });

        // "Manage interrupts" button
        _m24srItMenuBtn = (Button) findViewById(R.id.TmActM24SRITBtnAreaId).findViewById(R.id.BasicBtnId);
        _m24srItMenuBtn.setText(R.string.tm_act_m24sr_it_btn_txt);
        _m24srItMenuBtn.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.tm_act_m24sr_it_btn_width));
        _m24srItMenuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, _m24srItMenuRelIdx);
                startActivity(intent);
            }
        });


        // "Tap" button
        RelativeLayout tmTapBtnArea = (RelativeLayout) findViewById(R.id.TmActTapBtnAreaId);
        Button tapButton = (Button) tmTapBtnArea.findViewById(R.id.BasicBtnId);
        tapButton.setText(R.string.all_act_tap_btn_txt);
        tapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TagMenuActivity.this, WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Get the current tag and update view content accordingly
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag= currentApp.getCurrentTag();
        onTagChanged(currentTag);

    }

    @Override
    protected void onNewIntent(Intent intent) 
    {
        Log.v(this.getClass().getName(), "OnNewIntent Activity");
        super.onNewIntent(intent);
    }

   @Override
    protected void onResume() 
    {
        Log.v(this.getClass().getName(), "OnResume Activity");

        // TODO Auto-generated method stub
        super.onResume();

    }


    @Override
    protected void onPause() 
    {
        Log.v(this.getClass().getName(), "OnPause Activity");

        // TODO Auto-generated method stub
        super.onPause();

        return;
    }
    
    private void onTagChanged(NFCTag newTag) {
        // At this point, newTag should be a valid tag (not null)
        // Deactivate all buttons, to reactivate the only needed one after menus list parsing
        _ndefMenuBtn.setVisibility(View.GONE);
        _ndefSmartviewBtn.setVisibility(View.GONE);
        _ccMenuBtn.setVisibility(View.GONE);
        _sysMenuBtn.setVisibility(View.GONE);
        _binMenuBtn.setVisibility(View.GONE);
        _m24lrPwdMenuBtn.setVisibility(View.GONE);
        _m24lrLockMenuBtn.setVisibility(View.GONE);
        _m24lrEhMenuBtn.setVisibility(View.GONE);
        _m24srPwdMenuBtn.setVisibility(View.GONE);
        _m24srItMenuBtn.setVisibility(View.GONE);

        // Update headers
        // - Application header
        NFCAppHeaderFragment mHeadFrag = (NFCAppHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.TmActNFCAppHeaderFragmentId);
        mHeadFrag.onTagChanged(newTag);
        // - Tag header
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.TmActTagHeaderFragmentId);
        mTagHeadFrag.onTagChanged(newTag);

        // Get menus list that is applicable for the new tag
        com.st.NFC.NfcMenus[] tmMenusList = newTag.getMenusList();

        // Take care of tags that don't have a defined menu list (competitors ones, for example)
        // User is sent back to TagInfoActivity
        if (tmMenusList == null) {
            /*Intent intent = new Intent(TagMenuActivity.this, TagInfoActivity.class);
            intent.setAction(getString(R.string.intent_new_app_current_tag));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
        } else {
            // Parse the menu list, to activate suitable buttons
            int menuIdx;
            for (menuIdx = 0; menuIdx < tmMenusList.length; menuIdx++) {
                switch (tmMenusList[menuIdx]) {
                    // Smart View fragment
                    case NFC_MENU_SMART_VIEW_NDEF_FILE:
                    // "NDEF" button
                    _ndefSmartviewIdx = menuIdx;
                    _ndefSmartviewBtn.setVisibility(View.VISIBLE);
                    break;
                    case NFC_MENU_NDEF_FILES:
                        // "NDEF" button
                        _ndefMenuRelIdx = menuIdx;
                        _ndefMenuBtn.setVisibility(View.VISIBLE);
                        break;
                    case NFC_MENU_CC_FILE:
                        // "CC file" button
                        _ccMenuRelIdx = menuIdx;
                        _ccMenuBtn.setVisibility(View.VISIBLE);
                        break;
                    case NFC_MENU_SYS_FILE:
                        // "SYSTEM file" button
                        _sysMenuRelIdx = menuIdx;
                        _sysMenuBtn.setVisibility(View.VISIBLE);
                        break;
                    case NFC_MENU_BIN_FILE:
                        // "BIN content" button
                        _binMenuRelIdx = menuIdx;
                        _binMenuBtn.setVisibility(View.VISIBLE);
                        break;
                    // M24LR specific buttons
                    case NFC_MENU_M24LR_PWD:
                        // "Password" button
                        _m24lrPwdMenuRelIdx = menuIdx;
                        _m24lrPwdMenuBtn.setVisibility(View.VISIBLE);
                        break;
                    case NFC_MENU_M24LR_LOCK:
                        // "Lock sector" button
                        _m24lrLockMenuRelIdx = menuIdx;
                        _m24lrLockMenuBtn.setVisibility(View.VISIBLE);
                        break;
                    case NFC_MENU_M24LR_EH:
                        // "Energy Harvesting" button
                        _m24lrEhMenuRelIdx = menuIdx;
                        _m24lrEhMenuBtn.setVisibility(View.VISIBLE);
                        break;
                    // M24SR specific buttons
                    case NFC_MENU_M24SR_PWD:
                        // "Manage pwd & access rights" button
                        _m24srPwdMenuRelIdx = menuIdx;
                        _m24srPwdMenuBtn.setVisibility(View.VISIBLE);
                        break;
                    case NFC_MENU_M24SR_IT:
                        // "Manage interrupts" button
                        _m24srItMenuRelIdx = menuIdx;
                        _m24srItMenuBtn.setVisibility(View.VISIBLE);
                        break;
                }
            }


            // FootNote
            TextView tmFootNoteTxt = (TextView) findViewById(R.id.FootNoteTxtId);
            tmFootNoteTxt.setText(newTag.getFootNote());
        }

    }

}
