/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1463 $
  * Revision of last commit    :  $Rev: 1463 $
  * Date of last commit     :  $Date: 2015-12-02 16:23:44 +0100 (Wed, 02 Dec 2015) $ 
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

import com.st.Fragments.MenuSmartNdefViewFragment.NdefViewFragmentListener;
import com.st.demo.R;
import com.st.demo.BasicWriteLRActivity;
import com.st.demo.FirmwareUpdate;
import com.st.demo.FileManagement;
import com.st.demo.FastTransferActivity;
import com.st.demo.ScanReadLRActivity;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MenuM24LRDemoFragment extends NFCPagerFragment {


    private View _curFragmentView = null;      // Store view corresponding to current fragment

    public static final int RESULT_OK = 101;
    public static final int TOOL_REQUEST_DONE = 102;
    public static final int TOOL_EXCHANGE_DATA_DONE = 103;

    public static final int TOOL_BASIC_READ = 201;
    public static final int TOOL_BASIC_WRITE = 202;
    public static final int TOOL_FILE_TRANSFER = 302;
    public static final int TOOL_MAILBOX_TRANSFER = 402;
    public static final int TOOL_FWU_TRANSFER = 403;

    // Current Activity Handling the fragment
    NdefViewFragmentListener _mListener;


    public static final String WAIT_FOR_TAP_ACTION = "WAIT_FOR_TAP_ACTION";

    public enum actionType {
        BASIC_READ,
        BASIC_WRITE,
        BASIC_FILE_TRANSFER,
        BASIC_MAILBOX_TRANSFER,
        BASIC_FWU_TRANSFER,
        UNDEFINED_ACTION
    }


    private actionType currentAction;

    static final String TAG_HEADER_FRAGMENT_PARAM = "tagHeaderFrag";


    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag NFC Tag to consider
     * @return A new instance of fragment MenuToolsFragment.
     */

    public static MenuM24LRDemoFragment newInstance(NFCTag mNFCTag) {
        MenuM24LRDemoFragment fragment = new MenuM24LRDemoFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }

    public static MenuM24LRDemoFragment newInstance(NFCTag mNFCTag, int page, String title) {
        MenuM24LRDemoFragment fragment = new MenuM24LRDemoFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuM24LRDemoFragment() {
        // Required empty public constructor
    }

    public void setNFCTag(NFCTag mNFCTag) {
        NFCApplication.getApplication().setCurrentTag(mNFCTag);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            _mListener = (NdefViewFragmentListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MenuSmartNdefViewFragmentListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.v(this.getClass().getName(), "OnCreate Fragment");
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        Log.v(this.getClass().getName(), "OnCreate Fragment" + "page: " + page + " Name: " + title);

        currentAction = actionType.UNDEFINED_ACTION;

    }

    private void enableButton(boolean bool) {

        Button button = (Button) _curFragmentView.findViewById(R.id.BBasicRead);
        button.setEnabled(bool);
        button = (Button) _curFragmentView.findViewById(R.id.BBasicWrite);
        button.setEnabled(bool);
        button = (Button) _curFragmentView.findViewById(R.id.BFileTransfer);
        button.setEnabled(bool);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnCreateView Fragment");

        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_menu_m24lrdemo, container, false);


        Button bBasicRead = (Button) _curFragmentView.findViewById(R.id.BBasicRead);
        bBasicRead.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                MenuM24LRDemoFragment.this.currentAction = MenuM24LRDemoFragment.actionType.BASIC_READ;
                Intent intent = new Intent(getActivity(), ScanReadLRActivity.class);
                intent.putExtra(WAIT_FOR_TAP_ACTION, currentAction);
                // need to provide here the settings - update could be done by Settings Button
                startActivityForResult(intent, TOOL_BASIC_READ);
            }
        });
        Button bBasicWrite = (Button) _curFragmentView.findViewById(R.id.BBasicWrite);
        bBasicWrite.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                MenuM24LRDemoFragment.this.currentAction = MenuM24LRDemoFragment.actionType.BASIC_WRITE;
                Intent intent = new Intent(getActivity(), BasicWriteLRActivity.class);
                intent.putExtra(WAIT_FOR_TAP_ACTION, currentAction);
                // need to provide here the settings - update could be done by Settings Button
                startActivityForResult(intent, TOOL_BASIC_WRITE);
            }
        });

        Button bFileTransfer = (Button) _curFragmentView.findViewById(R.id.BFileTransfer);
        bFileTransfer.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                MenuM24LRDemoFragment.this.currentAction = actionType.BASIC_FILE_TRANSFER;
                Intent intent = new Intent(getActivity(), FileManagement.class);
                intent.putExtra(WAIT_FOR_TAP_ACTION, currentAction);
                // need to provide here the settings - update could be done by Settings Button
                startActivityForResult(intent, TOOL_FILE_TRANSFER);

            }
        });
        enableButton(true);
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
        // Use a Tag parser for UI purpose: parsing is done when creating "NFCTag" object
        // This parser should:
        // - identify the tag manufacturer (STM or other)
        //    -> if not STM, no logo, no menu for tag management
        //    -> else (= STM tag), identify the product to determine the suitable logo, tag name, and specific menu

        // Update instance attribute
        LinearLayout ghostLayout;

        // // Lock management layout - M24SR Model
        //ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.LockManagementSection);
        //ghostLayout.setVisibility(View.GONE);
        // Set footnote
        TextView tmFootNoteTxt = (TextView) _curFragmentView.findViewById(R.id.FootNoteTxtId);
        tmFootNoteTxt.setText(newTag.getFootNote());


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TOOL_REQUEST_DONE: {
                Log.d("DIALOG DEBUG", "Tool request done !");
                // NFCApplication.getApplication().getCurrentTag().decodeTagType4A();

                break;
            }
            case TOOL_EXCHANGE_DATA_DONE: {
                Log.d("DIALOG DEBUG", "Tool-Exchange request done !");
                break;
            }
            case TOOL_BASIC_READ: {
                Log.d("DIALOG DEBUG", "Tool-Basic Read request done !");
                break;
            }
            case TOOL_BASIC_WRITE: {
                Log.d("DIALOG DEBUG", "Tool-Basic Write request done !");
                break;
            }
            case TOOL_FILE_TRANSFER: {
                Log.d("DIALOG DEBUG", "File Transfert request done !");
                break;
            }

        }
    }

}

