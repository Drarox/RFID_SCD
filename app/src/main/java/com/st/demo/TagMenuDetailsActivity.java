/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1719 $
  * Revision of last commit    :  $Rev: 1719 $
  * Date of last commit     :  $Date: 2016-03-04 15:42:31 +0100 (Fri, 04 Mar 2016) $ 
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






import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.st.Fragments.MenuCCFileFragment;
import com.st.Fragments.MenuCCFileLRFragment;
import com.st.Fragments.MenuM24LRDemoFragment;
import com.st.Fragments.MenuM24SRDemoFragment;
import com.st.Fragments.MenuNDEFFilesFragment;
import com.st.Fragments.MenuST25DVDemoFragment;
import com.st.Fragments.MenuST25TVToolsFragment;
import com.st.Fragments.MenuSYSFileFragment;
import com.st.Fragments.MenuSYSFileLRFragment;
import com.st.Fragments.MenuSmartNdefViewFragment;
import com.st.Fragments.NFCPagerFragment;
import com.st.Fragments.MenuSmartNdefViewFragment.NdefViewFragmentListener;

import com.st.Fragments.MenuTagInfoFragment;
import com.st.Fragments.MenuToolsFragment;
import com.st.Fragments.MenuToolsLRFragment;
import com.st.Fragments.PageIndicator;

import com.st.NFC.NFCActivity;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.NfcMenus;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.st.Fragments.patches.FixedFragmentStatePagerAdapter;
import com.st.Fragments.patches.SmartFragmentStatePagerAdapter;
import com.st.Fragments.patches.SortableFragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TagMenuDetailsActivity extends NFCActivity implements NdefViewFragmentListener{
// NDEF editor
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    final String TAG = this.getClass().getName();

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    int mPagetodisplay = 0;

    PageIndicator pageIndicator;

    FragmentManager _mfm = null;
    /**
     * Class input arguments
     */
    public static final String ARG_TAB_NUMBER = "tab_number";

    public static NFCTag m_tag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "onCreate Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_menu_details);
        _mfm = getSupportFragmentManager();
/*
        // in case we want to save data for next start ...
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(DATA_TASKS)) {
                ArrayList<String> taskTags = savedInstanceState.getStringArrayList(DATA_TASKS);
                mTaskFragments.clear();
                Fragment fragment;
                for(String tag : taskTags) {
                    fragment = getSupportFragmentManager().findFragmentByTag(tag);
                    if(fragment != null) {
                        FragmentPager ffp = new FragmentPager(fragment,);
                        mTaskFragments.add(ffp);
                    }
                }
            }
            if(bundle.containsKey(DATA_LISTS)) {
                ArrayList<String> taskTags = bundle.getStringArrayList(DATA_LISTS);
                mListFragments.clear();
                Fragment fragment;
                for(String tag : taskTags) {
                    for(String tag : taskTags) {
                        fragment = getSupportFragmentManager().findFragmentByTag(tag);
                        if(fragment != null) {
                            FragmentPager ffp = new FragmentPager(fragment,);
                            mListFragments.add(ffp);
                        }
                    }
            }

        }
*/
        // Create the adapter that will return a fragment for each of the
        // primary sections of the app.

        if (NFCApplication.getApplication().getCurrentTag() != null) {


            if (mSectionsPagerAdapter != null) {
                this.buildTaskFragments(NFCApplication.getApplication().getCurrentTag(), mSectionsPagerAdapter);
                mSectionsPagerAdapter = new SectionsPagerAdapter(
                        _mfm, ((NFCApplication) getApplication()).getCurrentTag());
            } else {
                mSectionsPagerAdapter = new SectionsPagerAdapter(
                        _mfm, ((NFCApplication) getApplication()).getCurrentTag());
                this.buildTaskFragments(NFCApplication.getApplication().getCurrentTag(), mSectionsPagerAdapter);

            }

             mSectionsPagerAdapter.setTasks(mTaskFragments);


            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

                // This method will be invoked when a new page becomes selected.
                    @Override
                    public void onPageSelected(int pos) {
                        pageIndicator.setActiveDot(pos);
                        mPagetodisplay = pos;

                    }
                    // This method will be invoked when the current page is scrolled
                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub
                    }

                    // Called when the scroll state changes:
                    // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
                    @Override
                    public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub

                    }
                });



            pageIndicator = (PageIndicator)findViewById(R.id.pageIndicator);
            pageIndicator.setTotalNoOfDots(3);
            pageIndicator.setActiveDot(0);
            pageIndicator.setDotSpacing(10);

            m_tag = (NFCTag)(NFCApplication.getApplication().getCurrentTag());
            //NFCApplication.getApplication().getCurrentTag().tagInvalidate = true;

            // Set current item
            Intent mIntent = getIntent();
            int itemNb = mIntent.getIntExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, 0);
            //String itemNb = mIntent.getIntExtra(TagMenuDetailsActivity.ARG_TAB_NAME, "TOOLS");
            mViewPager.setCurrentItem(itemNb);
            mPagetodisplay = itemNb;
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        final ArrayList<String> taskTags = new ArrayList<String>();
        final ArrayList<String> listTags = new ArrayList<String>();

        for (int ix=0;ix<mTaskFragments.size();ix++) {
            FragmentPager fp = mTaskFragments.get(ix);
            if (fp != null) {
                if (fp.m_frg != null) {
                    taskTags.add(fp.getFragmentTitle(fp.m_type));
                }
            }
        }
        for (int ix=0;ix<mListFragments.size();ix++) {
            FragmentPager fp = mListFragments.get(ix);
            if (fp != null) {
                if (fp.m_frg != null) {
                    listTags.add(fp.getFragmentTitle(fp.m_type));
                }
            }
        }


/*        bundle.putStringArrayList(DATA_TASKS, taskTags);
        bundle.putStringArrayList(DATA_LISTS, listTags);
*/
        super.onSaveInstanceState(bundle);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.tag_menu_details, menu);
        // if (BuildConfig.DEBUG)
        Log.v(this.getClass().getName(), "onCreateOptionsMenu Activity");
        return true;
    }


    //private PendingIntent nfcPendingIntent;
    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(this.getClass().getName(), "OnNewIntent Activity");
        super.onNewIntent(intent);

        int itemNb = intent.getIntExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, 0);
        mPagetodisplay = itemNb;

        if (NFCApplication.getApplication().getCurrentTag() != null && m_tag != null) {
            // Tag has probably changed
            if (m_tag.getModel() != NFCApplication.getApplication().getCurrentTag().getModel()) {
                // tag info has changed .......
                // FBE - restart activity taking into account new pager - fragmentManager

                //onTagChanged(NFCApplication.getApplication().getCurrentTag());
                Toast toast = Toast.makeText(NFCApplication.getContext(), "Tag has changed ... please tap again the tag for coherency ...", Toast.LENGTH_LONG);
                toast.show();
                //onTagChanged(NFCApplication.getApplication().getCurrentTag());
                this.onBackPressed();
                //Intent intent = getIntent();
                //finish();
                //startActivity(intent);


            } else {
                if (mPagetodisplay != 0) {
                    Log.v(this.getClass().getName(), "Page to display different to 0");

                } else {
                    // tag is the same, update needed
                    // Specific Toast for ST tags
                    if ((NFCApplication.getApplication().getCurrentTag().getModel().contains("24SR"))
                            || (NFCApplication.getApplication().getCurrentTag().getModel().contains("SRTAG"))
                            || (NFCApplication.getApplication().getCurrentTag().getModel().contains("ST25TA"))
                            || (NFCApplication.getApplication().getCurrentTag().getModel().contains("M24LR"))
                            || (NFCApplication.getApplication().getCurrentTag().getModel().contains("ST25DV"))) {
                        showSTToast(NFCApplication.getApplication().getCurrentTag());
                    } else {
                        Toast toast = Toast.makeText(NFCApplication.getContext(),
                                getString(R.string.ti_act_new_tag_toast), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                mViewPager.setCurrentItem(0);
                pageIndicator.setActiveDot(0);
                onTagChanged(NFCApplication.getApplication().getCurrentTag());
                //m_tag = NFCApplication.getApplication().getCurrentTag();
            }


        } else {
            Log.v(this.getClass().getName(), "Tag is null...");

        }

    }

    public void onBackPressed() {
         //super.onBackPressed();
        finish();

    }

    @Override
    protected void onResume() {
        Log.v(this.getClass().getName(), "OnResume Activity");
        super.onResume();
        if (NFCApplication.getApplication().getCurrentTag() != null) {

            if (mPagetodisplay != 0) {
                Log.v(this.getClass().getName(), "Page to display different to 0");

            } else {

                if ((NFCApplication.getApplication().getCurrentTag().getModel().contains("24SR")
                        || (NFCApplication.getApplication().getCurrentTag().getModel().contains("SRTAG")))
                        || (NFCApplication.getApplication().getCurrentTag().getModel().contains("ST25TA"))
                        || (NFCApplication.getApplication().getCurrentTag().getModel().contains("M24LR"))
                        || (NFCApplication.getApplication().getCurrentTag().getModel().contains("ST25DV"))) {
                    showSTToast(NFCApplication.getApplication().getCurrentTag());
                }
//                if (NFCApplication.getApplication().getCurrentTag().tagInvalidate == true) {
//                    NFCApplication.getApplication().getCurrentTag().tagInvalidate = false;
//                    NFCApplication.getApplication().getCurrentTag().decodeTag();
//                }
            }
            // Reference current tag is stored at application level
            // Check if we need to modify all tags info
            onTagChanged(NFCApplication.getApplication().getCurrentTag());
        }
    }

    @Override
    protected void onPause() 
    {
        Log.v(this.getClass().getName(), "OnPause Activity");
        super.onPause();
        return;
    }

    private void onTagChanged (NFCTag newTag) {
        Log.v(this.getClass().getName(), "onTagChanged Activity");
       // Set the layout content according to the content of the tag
        // - Application header
        // Update Application header
        NFCAppHeaderFragment mHeadFrag = (NFCAppHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.TmdActNFCAppHeaderFragmentId);
        mHeadFrag.onTagChanged(newTag);

        try {
            if (m_tag.getModel() != NFCApplication.getApplication().getCurrentTag().getModel()) {
                mViewPager.removeAllViews();
                if (mSectionsPagerAdapter != null) {
                    buildTaskFragments(newTag, mSectionsPagerAdapter);
                    //mSectionsPagerAdapter.notifyDataSetChanged();
                    mSectionsPagerAdapter = new SectionsPagerAdapter(
                            _mfm, ((NFCApplication) getApplication()).getCurrentTag());
                } else {
                    mSectionsPagerAdapter = new SectionsPagerAdapter(
                            _mfm, ((NFCApplication) getApplication()).getCurrentTag());
                    buildTaskFragments(newTag, mSectionsPagerAdapter);
                }
                mSectionsPagerAdapter.setTasks(mTaskFragments);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mSectionsPagerAdapter.notifyDataSetChanged();

            } else {
                Log.v(this.getClass().getName(), "Same tag ...");
                buildTaskFragments(newTag, mSectionsPagerAdapter);
                mSectionsPagerAdapter.setTasks(mTaskFragments);

                mViewPager.setAdapter(mSectionsPagerAdapter);
                mSectionsPagerAdapter.notifyDataSetChanged();
            }

             if (mPagetodisplay != 0) {

                mViewPager.setCurrentItem(mPagetodisplay);
                pageIndicator.setActiveDot(mPagetodisplay);
                mSectionsPagerAdapter.notifyDataSetChanged();

            }
        } catch(IndexOutOfBoundsException ex) {
            Toast toast = Toast.makeText(NFCApplication.getContext(),
                    "Fragments manager issue detected - can't resume...", Toast.LENGTH_SHORT);
            toast.show();
            this.onBackPressed();

        }
        catch(IllegalStateException ex) {
            //this.finish();
            Toast toast = Toast.makeText(NFCApplication.getContext(),
                    "Fragments manager issue detected - can't resume...", Toast.LENGTH_SHORT);
            toast.show();
            //this.onBackPressed();

        }
    }

    private void clearBackstack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void OnlockNdefMessage()
    {
        onTagChanged(NFCApplication.getApplication().getCurrentTag());
    }

    protected static ArrayList<FragmentPager> mListFragments = new ArrayList<FragmentPager>();
    protected static ArrayList<FragmentPager> mTaskFragments = new ArrayList<FragmentPager>();

    private void buildTaskFragments (NFCTag newTag, SectionsPagerAdapter sectionsPagerAdapter) {
        NfcMenus[] menu = newTag.getMenusList();
        boolean found = false;
        NFCApplication currentApp = (NFCApplication) getApplication();

        if (mTaskFragments.size() > 0)  {
            mTaskFragments.clear();
            //if (sectionsPagerAdapter != null) sectionsPagerAdapter.notifyDataSetChanged();
        }
        for (int i=0;i<menu.length;i++) {
            NfcMenus mm = menu[i];
            found = false;
            for (int ix=0;ix<mListFragments.size();ix++) {
                FragmentPager fp = mListFragments.get(ix);
                if (fp != null) {
                    if (fp.m_type == mm) {
                        if (fp.m_frg == null) {
                            FragmentPager ffp = createFragmentPager (newTag,fp.m_type,ix);
                            mListFragments.set(ix,ffp);
                            if (fragmentToBeDisplayed(currentApp.isEnableDemoFeature(), fp.m_type))
                                mTaskFragments.add(ffp);

                        } else {
                            if (fragmentToBeDisplayed(currentApp.isEnableDemoFeature(), fp.m_type))
                                mTaskFragments.add(fp);
                        }
                        found = true;
                    }
                }
            }
            // if we arrive here, no Fragment found for this menu according to Tag
            // create the Fragment
            if (found == false){
                FragmentPager ffp = createFragmentPager (newTag,mm,i);
                mListFragments.add(ffp);
                if (fragmentToBeDisplayed(currentApp.isEnableDemoFeature(), mm))
                    mTaskFragments.add(ffp);
            }
        }
        //updateUsedFragments(newTag);
    }
    
    private void updateUsedFragments(NFCTag newTag){
        for (int ix=0;ix<mTaskFragments.size();ix++) {
            FragmentPager fp = mTaskFragments.get(ix);
            if (fp != null) {
                if (fp.m_frg != null) {
                    NFCPagerFragment fgg = (NFCPagerFragment) fp.m_frg;
                    fgg.onTagChanged(newTag);
                }
            }

        }
    }
    
    public static ArrayList<FragmentPager> getListFragmentsPager() {
        return mListFragments;
    }
    public static ArrayList<FragmentPager> getTasksFragmentsPager() {
        return mTaskFragments;
    }
   
    // ======================================================= SPecific method for Fragments ================
    // Reorg needed


    public  FragmentPager createFragmentPager(NFCTag tag, NfcMenus type, int position) {
        FragmentPager fp = null;
        String title;
        fp = new FragmentPager(type);
        title = fp.getFragmentTitle(type);
        fp.m_type = type;
        switch (type) {
            case NFC_MENU_TAG_INFO:
                fp.m_frg = MenuTagInfoFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_SMART_VIEW_NDEF_FILE:
                fp.m_frg = MenuSmartNdefViewFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_NDEF_FILES:
                fp.m_frg = MenuNDEFFilesFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_CC_FILE:
                fp.m_frg = MenuCCFileFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_CC_FILE_LR:
                fp.m_frg = MenuCCFileLRFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_SYS_FILE_LR:
                fp.m_frg = MenuSYSFileLRFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_SYS_FILE:
                fp.m_frg = MenuSYSFileFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_TOOLS:
                fp.m_frg = MenuToolsFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_LR_TOOLS:
                fp.m_frg = MenuToolsLRFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_M24SR_DEMO:
                fp.m_frg = MenuM24SRDemoFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_M24LR_DEMO:
                fp.m_frg = MenuM24LRDemoFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_ST25DV_DEMO:
                fp.m_frg = MenuST25DVDemoFragment.newInstance(tag, position, title);
                break;
            case NFC_MENU_ST25TV_TOOLS:
                fp.m_frg = MenuST25TVToolsFragment.newInstance(tag, position, title);
                break;
            default:
                fp.m_frg = new DummySectionFragment();
        }
        return fp;
    }

    private  boolean fragmentToBeDisplayed(boolean demo, NfcMenus type) {
        boolean val = true;
        switch (type) {
        case NFC_MENU_TAG_INFO:
            break;
        case NFC_MENU_SMART_VIEW_NDEF_FILE:
            break;
        case NFC_MENU_NDEF_FILES:
            break;
        case NFC_MENU_CC_FILE:
            break;
        case NFC_MENU_SYS_FILE:
            break;
        case NFC_MENU_SYS_FILE_LR:
            break;
        case NFC_MENU_TOOLS:
            break;
        case NFC_MENU_M24SR_DEMO:
            val = false;
            if (demo) val = true;
            break;
        case NFC_MENU_M24LR_DEMO:
            break;
        default:
        }
        return val;
    }
   
    // ======================================================================================================
   
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
//    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {             /// IllegalStateException Recursive ......
    public class SectionsPagerAdapter extends FragmentPagerAdapter {                /// Works but need to recreate activity when tags do not have the same list of Fragments
//    public class SectionsPagerAdapter extends SmartFragmentStatePagerAdapter {        /// IllegalStateException Cannot change tag of frament .....
//    public class SectionsPagerAdapter extends SortableFragmentStatePagerAdapter {    /// Null pointeur exception ..... TB investigatedc Why
//    public class SectionsPagerAdapter extends FixedFragmentStatePagerAdapter {        /// IllegalStateException Recursive ......
//        public class SectionsPagerAdapter extends FixedFragmentStatePagerAdapter {
        private NFCTag _curTag;
        final private FragmentManager _fm;

        private ArrayList<FragmentPager> m_FragmentPager = new ArrayList<FragmentPager>();

//        @Override
        public String getTag(int position) { // for FixedFragmentStatePagerAdapter
            if (m_FragmentPager != null) {
                FragmentPager fp = m_FragmentPager.get(position);
                return fp.getFragmentTitle(fp.m_type);
            } else {
                return "TBD";
            }
        }

        public SectionsPagerAdapter(FragmentManager fm, NFCTag mNFCTag) {
            super(fm);
            Log.v(this.getClass().getName(), "SectionsPagerAdapter  creation ");

            _fm = fm;
            // FBE
            //FragmentManager.enableDebugLogging(true);
            _curTag = mNFCTag;
            //m_FragmentPager = new ArrayList<FragmentPager>();
            //Log.v(this.getClass().getName(), "SectionsPagerAdapter  Listinfo " + getFragmentsListInfo());

        }

        /* (non-Javadoc)
         * @see com.st.Fragments.FixedFragmentStatePagerAdapter#destroyItem(android.view.ViewGroup, int, java.lang.Object)
         */
/*        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            Log.v(this.getClass().getName(), "destroyItem  item:  " + position);
            super.destroyItem(container, position, object);
            if (m_FragmentPager != null) m_FragmentPager.remove(position);
            notifyDataSetChanged();

        }*/

        public void attach() {
            clearBackStackFM();
            FragmentTransaction trans = _fm.beginTransaction();

            for(int i=0; i<TagMenuDetailsActivity.getTasksFragmentsPager().size(); i++) {
//                boolean mffp = m_FragmentPager.contains(TagMenuDetailsActivity.getTasksFragmentsPager().get(i));
//                if (mffp) {
//                    trans.show(TagMenuDetailsActivity.getTasksFragmentsPager().get(i).m_frg);

//                } else {
                trans.attach(TagMenuDetailsActivity.getTasksFragmentsPager().get(i).m_frg);
                trans.show(TagMenuDetailsActivity.getTasksFragmentsPager().get(i).m_frg);
//                }
            }
            //this.clearBackStackFM();
            //trans.commit();
            trans.commitAllowingStateLoss();
            notifyDataSetChanged();
        }

        public void detach() {
            FragmentTransaction trans = _fm.beginTransaction();
            /*
            List<Fragment> lfp = _fm.getFragments();
            for (int i=0;i<lfp.size();i++) {
                Fragment fp = lfp.get(i);
                trans.hide(fp);
            }
            */
            // added to purge when new creation of a fm
            List<Fragment> lstfgs = _fm.getFragments();
            for(int i=0; i<lstfgs.size(); i++) {
                trans.detach(lstfgs.get(i));
            }
/*
            for(int i=0; i<TagMenuDetailsActivity.getListFragmentsPager().size(); i++) {
                trans.hide(TagMenuDetailsActivity.getListFragmentsPager().get(i).m_frg);
                //trans.detach(TagMenuDetailsActivity.getListFragmentsPager().get(i).m_frg);
            }
*/

            //trans.commit();
            trans.commitAllowingStateLoss();
            //notifyDataSetChanged();

        }

/*
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            this.m_FragmentPager.remove(position);
            super.destroyItem(container, position, object);
        }
*/
        //@Override
        public long getItemId(int position) { // for SortableFragmentStatePagerAdapter
            // give an ID different from position when position has been changed
            return position;
        }

        public void setTasks(ArrayList<FragmentPager> tasks) {
            m_FragmentPager = tasks;
        }


        private String getFragmentsListInfo(){
            String _FragmentsListInfo;
            if (m_FragmentPager !=null) {
            _FragmentsListInfo = "m_FragmentPager: elements = " + this.m_FragmentPager.size() + "details : " + this.m_FragmentPager.toString();
            } else {
                _FragmentsListInfo = "m_FragmentPager: elements not yet created .....";
            }
            return _FragmentsListInfo;
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return suitable fragment for given item (create it if not existing)
            // return this.fragments.get(position);
            //NFCPagerFragment nfcpf = (NFCPagerFragment)m_FragmentPager.get(position).m_frg;
            //nfcpf.onTagChanged(this._curTag);
            Fragment fg;
            //return m_FragmentPager.get(position).m_frg;
            fg = m_FragmentPager.get(position).m_frg;
            if (position == 0) {
                fg.setMenuVisibility(true);
                //fg.setUserVisibleHint(true);

            }
            _mfm.beginTransaction().commitAllowingStateLoss();
            return fg;
        }

        @Override
        public int getCount() {
            // Show the number of pages that are given by the menus list of the tag
            // need to update Indicator object
            //Log.v(this.getClass().getName(), "getCount  Needed: "+  _curTag.getMenusList().length + " Fgt list :" + m_FragmentPager.size() );

            if (m_FragmentPager !=null) {
                if (pageIndicator!=null)
                {
                    pageIndicator.setTotalNoOfDots(m_FragmentPager.size());
                    //pageIndicator.setTotalNoOfDots(_curTag.getMenusList().length);
                    //pageIndicator.setTotalNoOfDots(this.fragments.size());
                }
                //return this.fragments.size();
                //Log.v(this.getClass().getName(), "getCount  Fragments + pageIndicator : " + _curTag.getMenusList().length);
                return m_FragmentPager.size();
            }
            else return 0;
            //return _curTag.getMenusList().length;
        }



        @Override
        public CharSequence getPageTitle(int position) {
            FragmentPager fp = null;
            if (position <this.m_FragmentPager.size()) {
                fp = this.m_FragmentPager.get(position);
                return fp.getFragmentTitle(fp.m_type);
            }else {
                return getString(R.string.tmd_act_default_menu_title);
            }

//            return null;
        }
        public int getPageposition(String name) {
            for (int i=0;i<this.m_FragmentPager.size();i++) {
                FragmentPager fp = m_FragmentPager.get(i);
                if (fp != null) {

                    if (fp.m_frg.getTag() == name) {
                        //FragmentPager ffp = createFragmentPager (this._curTag,fp.m_type,position,getFragmentTitle(menu));
                        //m_FragmentPager.set(i,ffp);
                        return i;
                    }
                }
            }
            return 0;
        }
        @Override
        public int getItemPosition(Object mObject) {
            /*
             * Purpose of this method is to check whether an item in the adapter
             * still exists in the dataset and where it should show.
             * For each entry in dataset, request its Fragment.
             *
             * If the Fragment is found, return its (new) position. There's
             * no need to return POSITION_UNCHANGED; ViewPager handles it.
             *
             * If the Fragment passed to this method is not found, remove all
             * references and let the ViewPager remove it from display by
             * by returning POSITION_NONE;
             */

            NFCPagerFragment f = (NFCPagerFragment) mObject;
            int ref = ((NFCPagerFragment) f).getFagmentPageCreationNumber();
            String title = ((NFCPagerFragment) f).getFagmentPageCreationString();
            Log.v(this.getClass().getName(), "getItemPosition  ==> Fragment check  " + title + ref);

            for (int i=0;i<this.m_FragmentPager.size();i++) {
                FragmentPager fp = m_FragmentPager.get(i);
                if (fp != null) {

                    if (fp.m_frg == f) {
                        //FragmentPager ffp = createFragmentPager (this._curTag,fp.m_type,position,getFragmentTitle(menu));
                        //m_FragmentPager.set(i,ffp);
                        Log.v(this.getClass().getName(), "getItemPosition  ==> Fragment requested : " + fp.getFragmentTitle(fp.m_type) + "  position : " + i);
                        return i;
                    }
                }
            }


            return POSITION_NONE;


        }


        private void clearBackStackFM() {
            FragmentManager fm = _fm;

            for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                fm.popBackStack();
            }
        }

        public void onTagChanged (NFCTag newTag) {
            _curTag = newTag;
            Log.v(this.getClass().getName(), "SectionsPagerAdapter  onTagChanged Listinfo " + getFragmentsListInfo());

              /*
               * ==============================================
               */
                    for (int i=0;i<m_FragmentPager.size();i++) {
                        FragmentPager fp = m_FragmentPager.get(i);
                        if (fp != null) {
                                if (fp.m_frg != null) {
                                    NFCPagerFragment fgg = (NFCPagerFragment) fp.m_frg;
                                        if (fgg != null) fgg.onTagChanged(newTag);

                                }
                        }
                    }
        }



    }


    private void showSTToast(NFCTag mTag) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.st_toast_view,
                                       (ViewGroup) findViewById(R.id.STToastId));

        ImageView image = (ImageView) layout.findViewById(R.id.STToastImgId);
        image.setImageResource(mTag.getTranspLogo());

        TextView text = (TextView) layout.findViewById(R.id.STToastTxtId);
        String toastTxt = mTag.getModel() + " " + getString(R.string.st_toast_txt_suffix);
        text.setText(toastTxt);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,
                0,
                getResources().getDimensionPixelSize(R.dimen.st_toast_bottom_margin));
        //toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * A dummy fragment, to implement default menu, in case requested one is not already implemented
     */
    public static class DummySectionFragment extends NFCPagerFragment {

        public DummySectionFragment() {
        }
        public void onTagChanged (NFCTag newTag) {
            //_curTag = newTag;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_tag_menu_details_dummy, container, false);
            TextView dummyTextView = (TextView) rootView
                    .findViewById(R.id.section_label);
            dummyTextView.setText(getString(R.string.tmd_act_default_menu_txt));
            return rootView;
        }
    }

    public class FragmentPager
    {
        Fragment m_frg;
        NfcMenus m_type;

        public FragmentPager(NfcMenus type)
        {
            this.m_frg = null;
            this.m_type = type;
        }
        public FragmentPager(Fragment frg, NfcMenus type)
        {
            this.m_frg = frg;
            this.m_type = type;
        }
        public String getFragmentTitle(NfcMenus position) {
            switch (position) {
                case NFC_MENU_TAG_INFO:
                    return getString(R.string.tm_act_Tag_Info_btn_txt);
                case NFC_MENU_SMART_VIEW_NDEF_FILE:
                    return getString(R.string.tm_act_SMARTNDEF_btn_txt);
                case NFC_MENU_NDEF_FILES:
                    return getString(R.string.tm_act_NDEF_btn_txt);
                case NFC_MENU_CC_FILE:
                    return getString(R.string.tm_act_CC_btn_txt);
                case NFC_MENU_CC_FILE_LR:
                    return getString(R.string.tm_act_CC_btn_txt);
                case NFC_MENU_SYS_FILE:
                    return getString(R.string.tm_act_system_btn_txt);
                case NFC_MENU_SYS_FILE_LR:
                    return getString(R.string.tm_act_system_btn_txt);
                case NFC_MENU_TOOLS:
                    return getString(R.string.tm_act_tools_btn_txt);
                case NFC_MENU_LR_TOOLS:
                case NFC_MENU_ST25TV_TOOLS:
                    return getString(R.string.tm_act_toolslr_btn_txt);
                case NFC_MENU_BIN_FILE:
                    return getString(R.string.tm_act_bin_btn_txt);
                case NFC_MENU_M24LR_PWD:
                    return getString(R.string.tm_act_m24lr_pwd_btn_txt);
                case NFC_MENU_M24LR_LOCK:
                    return getString(R.string.tm_act_m24lr_lock_btn_txt);
                case NFC_MENU_M24LR_EH:
                    return getString(R.string.tm_act_m24lr_eh_btn_txt);
                case NFC_MENU_M24SR_PWD:
                    return getString(R.string.tm_act_m24sr_pwd_btn_txt);
                case NFC_MENU_M24SR_IT:
                    return getString(R.string.tm_act_m24sr_it_btn_txt);
                case NFC_MENU_M24SR_DEMO:
                    return getString(R.string.tm_act_m24sr_demo_btn_txt);
                case NFC_MENU_M24LR_DEMO:
                    return getString(R.string.tm_act_m24lr_demo_btn_txt);
                case NFC_MENU_ST25DV_DEMO:
                    return getString(R.string.tm_act_st25dv_demo_btn_txt);
                default:
                    return getString(R.string.tmd_act_default_menu_title);
            }
        }
    }

}



