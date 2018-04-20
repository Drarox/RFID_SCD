package com.st.Fragments.patches;

import android.support.v4.view.PagerAdapter;
import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


public abstract class SortableFragmentStatePagerAdapter extends PagerAdapter {
    private static final String TAG = "SortableFragmentStatePagerAdapter";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    private long[] mItemIds = new long[] {};
    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private Fragment mCurrentPrimaryItem = null;


       public SortableFragmentStatePagerAdapter(FragmentManager fm) {

            mFragmentManager = fm;
            createIdCache();
        }

        /**
         * Return the Fragment associated with a specified position.
         */
        public abstract Fragment getItem(int position);

        /**
         * Return a unique identifier for the item at the given position.
         */
        public abstract long getItemId(int position);

        @Override
        public void startUpdate(ViewGroup container) {
        }

        private void checkForIdChanges() {
            long[] newItemIds = new long[getCount()];
            for (int i = 0; i < newItemIds.length; i++) {
                newItemIds[i] = getItemId(i);
            }

            if (!Arrays.equals(mItemIds, newItemIds)) {
                ArrayList<Fragment.SavedState> newSavedState = new ArrayList<Fragment.SavedState>();
                ArrayList<Fragment> newFragments = new ArrayList<Fragment>();
                for (int i = 0; i < newItemIds.length; i++) {
                    newFragments.add(null);
                }

                for (int oldPosition = 0; oldPosition < mItemIds.length; oldPosition++) {
                    int newPosition = POSITION_NONE;
                    for (int i = 0; i < newItemIds.length; i++) {
                        if (mItemIds[oldPosition] == newItemIds[i]) {
                            newPosition = i;
                            break;
                        }
                    }
                    if (newPosition >= 0) {
                        if (oldPosition < mSavedState.size()) {
                            Fragment.SavedState savedState = mSavedState.get(oldPosition);
                            if (savedState != null) {
                                while (newSavedState.size() <= newPosition) {
                                    newSavedState.add(null);
                                }
                                newSavedState.set(newPosition, savedState);
                            }
                        }
                        if (oldPosition < mFragments.size()) {
                            Fragment fragment = mFragments.get(oldPosition);
                            if (fragment != null) {
                                while (newFragments.size() <= newPosition) {
                                    newFragments.add(null);
                                }
                                newFragments.set(newPosition, fragment);
                            }
                        }
                    }
                }

                mItemIds = newItemIds;
                mSavedState = newSavedState;
                mFragments = newFragments;
            }
        }

        @Override
        public void notifyDataSetChanged() {
            checkForIdChanges();

            super.notifyDataSetChanged();
        }

        /**
         * Create the initial set of item IDs. Run this after you have set your adapter data.
         */
        public void createIdCache() {
            // If we have already stored ids, don't overwrite them
            if (mItemIds.length == 0) {
                // getCount might have overhead, so run it as late as possible
                final int count = getCount();
                if (count > 0) {
                    mItemIds = new long[count];
                    for (int i = 0; i < count; i++) {
                        mItemIds[i] = getItemId(i);
                    }
                }
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            createIdCache();

            // If we already have this item instantiated, there is nothing
            // to do.  This can happen when we are restoring the entire pager
            // from its saved state, where the fragment manager has already
            // taken care of restoring the fragments we previously had instantiated.
            if (mFragments.size() > position) {
                Fragment f = mFragments.get(position);
                if (f != null) {
                    return f;
                }
            }

            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }

            Fragment fragment = getItem(position);
            if (DEBUG) Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
            if (mSavedState.size() > position) {
                Fragment.SavedState fss = mSavedState.get(position);
                if (fss != null) {
                    fragment.setInitialSavedState(fss);
                }
            }
            while (mFragments.size() <= position) {
                mFragments.add(null);
            }
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
            mFragments.set(position, fragment);
            mCurTransaction.add(container.getId(), fragment);

            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment)object;

            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            if (DEBUG) Log.v(TAG, "Removing item #" + position + ": f=" + object
                    + " v=" + ((Fragment)object).getView());
            while (mSavedState.size() <= position) {
                mSavedState.add(null);
            }
            while (mFragments.size() <= position){
                mFragments.add(null);
            }
            if(mFragments.get(position) != null){
                mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(mFragments.get(position)));
                mFragments.set(position, null);
            }

            mCurTransaction.remove(fragment);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment)object;
            if (fragment != mCurrentPrimaryItem) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.setMenuVisibility(false);
                    mCurrentPrimaryItem.setUserVisibleHint(false);
                }
                if (fragment != null) {
                    fragment.setMenuVisibility(true);
                    fragment.setUserVisibleHint(true);
                }
                mCurrentPrimaryItem = fragment;
            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
//            try{
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
//            }catch(Exception e) {
                // Null pointeur exception ..... TB investigatedc Why
//            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment)object).getView() == view;
        }

        @Override
        public Parcelable saveState() {
            Bundle state = null;

            mItemIds = new long[getCount()];
            for (int i = 0; i < mItemIds.length; i++) {
                mItemIds[i] = getItemId(i);
            }
            if (mSavedState.size() > 0) {
                state = new Bundle();

                if (mItemIds.length > 0) {
                    state.putLongArray("itemids", mItemIds);
                }

                Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
                mSavedState.toArray(fss);
                state.putParcelableArray("states", fss);
            }
            for (int i=0; i<mFragments.size(); i++) {
                Fragment f = mFragments.get(i);
                if (f != null) {
                    if (state == null) {
                        state = new Bundle();
                    }
                    String key = "f" + i;
                    mFragmentManager.putFragment(state, key, f);
                }
            }
            return state;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            if (state != null) {
                Bundle bundle = (Bundle)state;
                bundle.setClassLoader(loader);

                mItemIds = bundle.getLongArray("itemids");
                if (mItemIds == null) {
                    mItemIds = new long[] {};
                }

                Parcelable[] fss = bundle.getParcelableArray("states");
                mSavedState.clear();
                mFragments.clear();
                if (fss != null) {
                    for (int i=0; i<fss.length; i++) {
                        mSavedState.add((Fragment.SavedState)fss[i]);
                    }
                }
                Iterable<String> keys = bundle.keySet();
                for (String key: keys) {
                    if (key.startsWith("f")) {
                        int index = Integer.parseInt(key.substring(1));
                        Fragment f = mFragmentManager.getFragment(bundle, key);
                        if (f != null) {
                            while (mFragments.size() <= index) {
                                mFragments.add(null);
                            }
                            f.setMenuVisibility(false);
                            mFragments.set(index, f);
                        } else {
                            Log.w(TAG, "Bad fragment at key " + key);
                        }
                    }
                }
                checkForIdChanges();
            }
        }
}
