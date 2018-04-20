package com.st.Fragments;

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

import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.demo.BasicWriteLRActivity;
import com.st.demo.Chronometer;
import com.st.demo.FTMChronometer;
import com.st.demo.FastTransferActivity;
import com.st.demo.FileManagement;
import com.st.demo.FirmwareUpdate;
import com.st.demo.PictureUpdate;
import com.st.demo.R;
import com.st.demo.ScanReadLRActivity;

/**
 * Created on 5/26/16.
 */
public class MenuST25DVDemoFragment extends NFCPagerFragment {

    private View mView = null;      // Store view corresponding to current fragment
    MenuSmartNdefViewFragment.NdefViewFragmentListener mListener;

    public static final String WAIT_FOR_TAP_ACTION="WAIT_FOR_TAP_ACTION";
    public enum actionType{
        BASIC_READ,
        BASIC_WRITE,
        BASIC_FILE_TRANSFER,
        BASIC_MAILBOX_TRANSFER,
        BASIC_FWU_TRANSFER,
        UNDEFINED_ACTION
    }
    public static final int RESULT_OK = 101;
    public static final int TOOL_REQUEST_DONE = 102;
    public static final int TOOL_EXCHANGE_DATA_DONE = 103;

    public static final int TOOL_BASIC_READ = 201;
    public static final int TOOL_BASIC_WRITE = 202;
    public static final int TOOL_FILE_TRANSFER = 302;
    public static final int TOOL_MAILBOX_TRANSFER = 402;
    public static final int TOOL_FWU_TRANSFER = 403;

    private actionType mCurrentAction;

    public static MenuST25DVDemoFragment newInstance(NFCTag mNFCTag) {
        MenuST25DVDemoFragment fragment = new MenuST25DVDemoFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }
    public static MenuST25DVDemoFragment newInstance(NFCTag mNFCTag,int page, String title) {
        MenuST25DVDemoFragment fragment = new MenuST25DVDemoFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    public void setNFCTag(NFCTag mNFCTag) {
        NFCApplication.getApplication().setCurrentTag(mNFCTag);
    }
    public void onCreate(Bundle savedInstanceState) {
        //Log.v(this.getClass().getName(), "OnCreate Fragment");
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        Log.v(this.getClass().getName(), "OnCreate Fragment" + "page: " + page + " Name: " + title);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.v(this.getClass().getName(), "OnCreateView Fragment");
        mView = inflater.inflate(R.layout.fragment_menu_st25dvdemo, container, false);
        mCurrentAction = actionType.UNDEFINED_ACTION;


        Button bBasicRead = (Button) mView.findViewById(R.id.BBasicRead);
        bBasicRead.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                MenuST25DVDemoFragment.this.mCurrentAction = MenuST25DVDemoFragment.actionType.BASIC_READ;
                Intent intent = new Intent(getActivity(), ScanReadLRActivity.class);
                intent.putExtra(WAIT_FOR_TAP_ACTION, mCurrentAction);
                // need to provide here the settings - update could be done by Settings Button
                startActivityForResult(intent, TOOL_BASIC_READ);
            }
        });
        Button bBasicWrite = (Button) mView.findViewById(R.id.BBasicWrite);
        bBasicWrite.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                MenuST25DVDemoFragment.this.mCurrentAction = MenuST25DVDemoFragment.actionType.BASIC_WRITE;
                Intent intent = new Intent(getActivity(), BasicWriteLRActivity.class);
                intent.putExtra(WAIT_FOR_TAP_ACTION, mCurrentAction);
                // need to provide here the settings - update could be done by Settings Button
                startActivityForResult(intent, TOOL_BASIC_WRITE);
            }
        });

        Button bFileTransfer = (Button) mView.findViewById(R.id.BFileTransfer);
        bFileTransfer.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FileManagement.class);
                // need to provide here the settings - update could be done by Settings Button
                startActivity(intent);

            }
        });

        Button bfiletransferbox = (Button) mView.findViewById(R.id.BMailBoxTransfer);
        bfiletransferbox.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), FastTransferActivity.class);

                // need to provide here the settings - update could be done by Settings Button
                startActivity(intent);
            }
        });


        Button bfwubox = (Button) mView.findViewById(R.id.BFTMFWUTransfer);


        bfwubox.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), FirmwareUpdate.class);
                // need to provide here the settings - update could be done by Settings Button
                startActivity(intent);
            }
        });

        Button bImageUpdatebox = (Button) mView.findViewById(R.id.BFTMImageTransfer);


        bImageUpdatebox.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PictureUpdate.class);
                // need to provide here the settings - update could be done by Settings Button
                startActivity(intent);
            }
        });

        Button bChronoUpdatebox = (Button) mView.findViewById(R.id.BFTMChronoTransfer);


        bChronoUpdatebox.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), FTMChronometer.class);
                // need to provide here the settings - update could be done by Settings Button
                startActivity(intent);
            }
        });
        enableButton(true);
        return mView;

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            mListener = (MenuSmartNdefViewFragment.NdefViewFragmentListener) activity;

        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement MenuSmartNdefViewFragmentListener");
        }

    }



    private void enableButton(boolean bool) {

        LinearLayout llmb = (LinearLayout) mView.findViewById(R.id.MailBoxTLayout);
        llmb.setVisibility(View.VISIBLE);
        Button button = (Button) mView.findViewById(R.id.BMailBoxTransfer);
        button.setEnabled(true);
        llmb = (LinearLayout) mView.findViewById(R.id.FTMFWUTLayout);
        llmb.setVisibility(View.VISIBLE);
        button = (Button) mView.findViewById(R.id.BFTMFWUTransfer);
        button.setEnabled(true);

        llmb = (LinearLayout) mView.findViewById(R.id.FTMImageTLayout);
        llmb.setVisibility(View.VISIBLE);
        button = (Button) mView.findViewById(R.id.BFTMImageTransfer);
        button.setEnabled(true);

        llmb = (LinearLayout) mView.findViewById(R.id.FTMChronoTLayout);
        llmb.setVisibility(View.VISIBLE);
        button = (Button) mView.findViewById(R.id.BFTMChronoTransfer);
        button.setEnabled(true);

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
        onTagChanged (NFCApplication.getApplication().getCurrentTag());
    }

    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getName(), "onDestroyView Fragment");

        super.onDestroyView();
    }

    @Override
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
        TextView tmFootNoteTxt = (TextView) mView.findViewById(R.id.FootNoteTxtId);
        tmFootNoteTxt.setText(newTag.getFootNote());


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case TOOL_REQUEST_DONE:
            {
                Log.d("DIALOG DEBUG","Tool request done !");
                // NFCApplication.getApplication().getCurrentTag().decodeTagType4A();

                break;
            }
            case TOOL_EXCHANGE_DATA_DONE:
            {
                Log.d("DIALOG DEBUG","Tool-Exchange request done !");
                break;
            }
            case TOOL_BASIC_READ:
            {
                Log.d("DIALOG DEBUG","Tool-Basic Read request done !");
                break;
            }
            case TOOL_BASIC_WRITE:
            {
                Log.d("DIALOG DEBUG","Tool-Basic Write request done !");
                break;
            }
        }
    }
}
