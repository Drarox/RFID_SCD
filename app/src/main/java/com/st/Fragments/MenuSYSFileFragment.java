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
package com.st.Fragments;

import com.st.demo.R;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.sysfileHandler;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuSYSFileFragment extends NFCPagerFragment {
    // Store current tag
    // private NFCTag _curNFCTag = null;
    // Store view corresponding to current fragment
    private View _curFragmentView = null;

    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag
     *            NFC Tag to consider
     * @return A new instance of fragment MenuNDEFFilesFragment.
     */
    public static MenuSYSFileFragment newInstance(NFCTag mNFCTag) {
        MenuSYSFileFragment fragment = new MenuSYSFileFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }

    public static MenuSYSFileFragment newInstance(NFCTag mNFCTag, int page, String title) {
        MenuSYSFileFragment fragment = new MenuSYSFileFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

/*    private boolean isSRTAG2KL() {
        return (NFCApplication.getApplication().getCurrentTag().isSRTAG2KL());
    }

    private boolean isM24SR() {
        return (NFCApplication.getApplication().getCurrentTag().isM24SR());
    }*/

    public MenuSYSFileFragment() {
        // Required empty public constructor
    }

    public void setNFCTag(NFCTag mNFCTag) {
        NFCApplication.getApplication().setCurrentTag(mNFCTag);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Log.v(this.getClass().getName(), "OnCreate Fragment");
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        Log.v(this.getClass().getName(), "OnCreate Fragment" + "page: " + page + " Name: " + title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnCreateView Fragment");

        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_menu_sys_file, container, false);

        // Create the nested fragments (tag header one)
        FragmentManager fragMng = getChildFragmentManager();
        // First check if the fragment already exists (in case current fragment
        // has been temporarily destroyed)
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) fragMng
                .findFragmentById(R.id.SYSFileFragTagHeaderFragmentId);
        if (mTagHeadFrag == null) {
            mTagHeadFrag = new TagHeaderFragment();
            FragmentTransaction transaction = fragMng.beginTransaction();
            transaction.add(R.id.SYSFileFragTagHeaderFragmentId, mTagHeadFrag);
            transaction.commit();
            fragMng.executePendingTransactions();
        }

        return _curFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnActivityCreated Fragment");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        Log.v(this.getClass().getName(), "onStart Fragment");
        super.onStart();

        // Fill in the layout with the currentTag
        onTagChanged(NFCApplication.getApplication().getCurrentTag());
    }

    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getName(), "onDestroyView Fragment");

        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onTagChanged(NFCTag newTag) {
        // Update instance attribute
        RelativeLayout layout;
        // Set the layout content according to the content of the tag
        // - Tag header
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) getChildFragmentManager()
                .findFragmentById(R.id.SYSFileFragTagHeaderFragmentId);
        if (mTagHeadFrag != null) {
            mTagHeadFrag.onTagChanged(newTag);
        }
        // - Check if SYS File is available for current tag
        sysfileHandler tagSYSHandler = (sysfileHandler)newTag.getSYSHandler();
        if (tagSYSHandler == null) {
            // Unhide "Not available" text view
            TextView curTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragNotAvailableId);
            curTxtView.setVisibility(View.VISIBLE);
            // Hide all other elements (encapsulated in a single RelativeLayout)
            RelativeLayout availLayout = (RelativeLayout) _curFragmentView
                    .findViewById(R.id.SYSFileFragAvailFileRelLayout);
            availLayout.setVisibility(View.GONE);
        } else {
            TextView commonTxtView;
            String commonStr;
            // Hide "Not available" text view
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragNotAvailableId);
            commonTxtView.setVisibility(View.GONE);
            // Unhide all other elements (encapsulated in a single
            // RelativeLayout)
            RelativeLayout availLayout = (RelativeLayout) _curFragmentView
                    .findViewById(R.id.SYSFileFragAvailFileRelLayout);
            availLayout.setVisibility(View.VISIBLE);

            // Set the fields of SYSTEM File
            // SYS File Length
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragSYSLgthFieldId);
            commonStr = String.valueOf(tagSYSHandler.getSYSLength()) + " "
                    + getString(R.string.mnf_frag_NDEF_NLEN_suffix_txt);
            commonTxtView.setText(commonStr);
            // I2C protect
            if (newTag.isSRTAG2KL()|| newTag.isST25TA02K()) {
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragI2CProtectAreaId);
                layout.setVisibility(View.GONE);

            } else {
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragI2CProtectAreaId);
                layout.setVisibility(View.VISIBLE);

                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragI2CProtectFieldId);
                if (tagSYSHandler.isI2CProtectedEnabled()) {
                    commonStr = getString(R.string.sys_file_i2c_protect_by_pwd);
                } else {
                    commonStr = getString(R.string.sys_file_super_user_rights);
                }
                commonTxtView.setText(commonStr);
            }
            // I2C watchdog
            if (newTag.isSRTAG2KL()|| newTag.isST25TA02K()) {
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragI2CWatchdogFieldId);
                commonTxtView.setVisibility(View.GONE);
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragI2CWatchdogHeaderId);
                commonTxtView.setVisibility(View.GONE);
            } else {
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragI2CWatchdogFieldId);
                commonTxtView.setVisibility(View.VISIBLE);
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragI2CWatchdogHeaderId);
                commonTxtView.setVisibility(View.VISIBLE);

                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragI2CWatchdogFieldId);
                if (!tagSYSHandler.isWatchdogActive()) {
                    commonStr = getString(R.string.all_act_not_active);
                } else {
                    commonStr = String.valueOf(tagSYSHandler.getWatchdogTimerValueinms()) + " ms";
                }
                commonTxtView.setText(commonStr);
            }
            // GPO
            if (newTag.isSRTAG2KL()|| newTag.isST25TA02K()) {

                commonStr = getString(R.string.msf_frag_gpo_rf_header_txt) + " " + tagSYSHandler.getGPORFToString();

            } else {
                commonStr = getString(R.string.msf_frag_gpo_rf_header_txt) + " " + tagSYSHandler.getGPORFToString()
                        + "\n" + getString(R.string.msf_frag_gpo_i2c_header_txt) + " "
                        + tagSYSHandler.getGPOI2CToString();
            }
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragGPOFieldId);
            commonTxtView.setText(commonStr);
            // Specific SRTAG2KL Counter
            if (newTag.isSRTAG2KL()|| newTag.isST25TA02K()) {
                // Counter Config
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragEventCounterConfigAreaId);
                layout.setVisibility(View.VISIBLE);
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragEventCounterConfigFieldId);
                if (tagSYSHandler.isWriteCounter()) {
                    if (tagSYSHandler.isCounterEnabled())
                        commonTxtView.setText("Write Counter Enabled");
                    else
                        commonTxtView.setText("Counter Disabled");
                } else {
                    if (tagSYSHandler.isCounterEnabled())
                        commonTxtView.setText("Read Counter Enabled");
                    else
                        commonTxtView.setText("Counter Disabled");
                }
                // Access Counter Value
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragEventCounterValueAreaId);
                layout.setVisibility(View.VISIBLE);
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragEventCounterValueFieldId);
                commonTxtView.setText(String.valueOf(tagSYSHandler.getCounterValue()));
            } else {
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragEventCounterConfigAreaId);
                layout.setVisibility(View.GONE);
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragEventCounterValueAreaId);
                layout.setVisibility(View.GONE);
            }

            // Specific SRTAG2KL Counter Value

            if (newTag.isSRTAG2KL() || newTag.isST25TA02K()) {
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragWirePwrMngAreaId);
                layout.setVisibility(View.GONE);
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragRFEnableAreaId);
                layout.setVisibility(View.GONE);
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragNDEFFilesNbAreaId);
                layout.setVisibility(View.GONE);
            } else {
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragWirePwrMngAreaId);
                layout.setVisibility(View.VISIBLE);
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragRFEnableAreaId);
                layout.setVisibility(View.VISIBLE);
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragNDEFFilesNbAreaId);
                layout.setVisibility(View.VISIBLE);

                // Wire power management
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragWirePwrMngFieldId);
                if (tagSYSHandler.isPowerSuppliedByRF()) {
                    commonStr = getString(R.string.msf_frag_tag_rf_powered_txt);
                } else {
                    commonStr = getString(R.string.msf_frag_tag_vcc_powered_txt);
                }
                commonTxtView.setText(commonStr);

                // RF enable
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragRFEnableFieldActFieldId);
                if (tagSYSHandler.isRFFielenabled()) {
                    commonStr = getString(R.string.all_act_on);
                } else {
                    commonStr = getString(R.string.all_act_off);
                }
                commonTxtView.setText(commonStr);
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragRFEnableDisPadStateFieldId);
                if (tagSYSHandler.isRFdisablePadIsHigh()) {
                    commonStr = getString(R.string.all_act_high);
                } else {
                    commonStr = getString(R.string.all_act_low);
                }
                commonTxtView.setText(commonStr);
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragRFEnableCmdDecFieldId);
                if (tagSYSHandler.isRFCMDdecoded()) {
                    commonStr = getString(R.string.all_act_on);
                } else {
                    commonStr = getString(R.string.all_act_off);
                }
                commonTxtView.setText(commonStr);
                // NDEF Files number
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragNDEFFilesNbFieldId);
                commonTxtView.setText(String.valueOf((tagSYSHandler.getNDEFfilenumber() & 0xFFL) + 1));
            }

            if (newTag.isSRTAG2KL() || newTag.isST25TA02K()) { // Product
                                                                                // Version
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragProductVersionAreaId);
                layout.setVisibility(View.VISIBLE);
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragProductVersionFieldId);
                commonStr = String.format("0x%02X", tagSYSHandler.getProductVersion());
                commonTxtView.setText(commonStr);
            } else {
                layout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragProductVersionAreaId);
                layout.setVisibility(View.GONE);
            }

            // UID
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragUIDFieldId);
            commonTxtView.setText(tagSYSHandler.getUIDtoString());
            // Memory Size
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragMemSizeFieldId);
            commonStr = String.valueOf(tagSYSHandler.getmemorySize() + 1) + " "
                    + getString(R.string.mnf_frag_NDEF_NLEN_suffix_txt) + " ("
                    + String.format("0x%04X", tagSYSHandler.getmemorySize()) + ")";
            commonTxtView.setText(commonStr);
            // Product Code
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragProdCodeFieldId);
            commonStr = String.format("0x%02X", tagSYSHandler.getProductCode()) + " (" + newTag.getModel() + ")";
            commonTxtView.setText(commonStr);
        }

        // Set footnote
        TextView tmFootNoteTxt = (TextView) _curFragmentView.findViewById(R.id.FootNoteTxtId);
        tmFootNoteTxt.setText(newTag.getFootNote());
    }

}
