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

package com.st.Fragments;




import java.lang.reflect.Field;

import com.st.NFC.NFCTag;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;



public abstract class NFCPagerFragment extends Fragment {
    /**
     * Attributes
     */
    private FragmentActivity myContext;

       // Store instance variables
    protected String title = "";
    protected int page = 0;
    /**
     * Methods
     */
       public int getFagmentPageCreationNumber() {
           return page;
       }
       public String getFagmentPageCreationString() {
           return title;
       }

       public abstract void onTagChanged(NFCTag newTag);

/*

        public void onAttach(Activity activity) {
             myContext=(FragmentActivity) activity;
             super.onAttach(activity);
        }
 */ 
        @Override
        public void onDetach() {
            super.onDetach();
            try {
                Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFragmentManager.setAccessible(true);
                childFragmentManager.set(this, null);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

}
