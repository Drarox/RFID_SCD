package com.st.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.demo.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuST25TVToolsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuST25TVToolsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuST25TVToolsFragment extends NFCPagerFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Store view corresponding to current fragment
    private View mCurFragmentView = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Current Activity Handling the fragment
    MenuSmartNdefViewFragment.NdefViewFragmentListener _mListener;

    @Override
    public void onStart() {
        super.onStart();

        // Fill in the layout with the currentTag
        onTagChanged (NFCApplication.getApplication().getCurrentTag());
    }

    public MenuST25TVToolsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuST25TVToolsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuST25TVToolsFragment newInstance(String param1, String param2) {
        MenuST25TVToolsFragment fragment = new MenuST25TVToolsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static MenuST25TVToolsFragment newInstance(NFCTag mNFCTag) {
        MenuST25TVToolsFragment fragment = new MenuST25TVToolsFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }

    public static MenuST25TVToolsFragment newInstance(NFCTag mNFCTag, int page, String title) {
        MenuST25TVToolsFragment fragment = new MenuST25TVToolsFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mCurFragmentView = inflater.inflate(R.layout.fragment_menu_st25tv_tools, container, false);

        // Create the nested fragments (tag header one)
        FragmentManager fragMng = getChildFragmentManager();
        // First check if the fragment already exists (in case current fragment has been temporarily destroyed)
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) fragMng.findFragmentById(R.id.ST25TVToolFragTagHeaderFragmentId);
        if (mTagHeadFrag == null) {
            mTagHeadFrag = new TagHeaderFragment();
            FragmentTransaction transaction = fragMng.beginTransaction();
            transaction.add(R.id.ST25TVToolFragTagHeaderFragmentId, mTagHeadFrag);
            transaction.commit();
            fragMng.executePendingTransactions();
        }

        // Inflate the layout for this fragment
        return mCurFragmentView;
    }

    public void setNFCTag(NFCTag mNFCTag) {
        NFCApplication.getApplication().setCurrentTag(mNFCTag);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            _mListener = (MenuSmartNdefViewFragment.NdefViewFragmentListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MenuSmartNdefViewFragmentListener");
        }

    }

    @Override
    public void onTagChanged(NFCTag newTag) {
        TextView commonTxtView;

        if (newTag == null)
        {
            newTag = NFCApplication.getApplication().getCurrentTag();
        }
        else
        {
            NFCApplication.getApplication().setCurrentTag(newTag);
        }

        // Set the layout content according to the content of the tag
        // - Tag header
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) getChildFragmentManager().findFragmentById(R.id.ST25TVToolFragTagHeaderFragmentId);
        mTagHeadFrag.onTagChanged(newTag);

        // Set the fields of CC File Magic Number
        commonTxtView = (TextView) mCurFragmentView.findViewById(R.id.ST25TVToolMemConfTextView);
        commonTxtView.setText("1 Area");


    }

    @Override
    public void onDetach() {
        super.onDetach();
        _mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
