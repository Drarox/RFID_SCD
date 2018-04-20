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
package com.st.MB;

import com.st.MB.FTMHeaderBuilder.MBcmd;
import com.st.MB.FTMPTColGeneric.ProtocolStateMachine;
import com.st.MB.FTMPTColGeneric.ProtocolStatus;

import android.util.Log;

public class FTMTaskAcknowledge  extends FTMTaskRequestResponse {

    public FTMTaskAcknowledge() {
        // TODO Auto-generated constructor stub
    }
    private byte mMBConfigRegister;
    private byte[] cmd = null;

    @Override
    protected void onPostExecute(final Void unused) {
        Log.v(this.getClass().getName(), "onPostExecute started ... ");
        mMBConfigRegister = getMBConfigRegister(bop);
        boolean canwrite = isRFCanWrite(mMBConfigRegister);
        ProtocolStatus step = mProtocol.getCurrentProtocolStep();
        this.mEndTime_ms = System.currentTimeMillis();
        if (step != ProtocolStatus.MB_PTL_Finished && step !=null &&
                (mProtocol.mMBProtocolFunction != FTMHeaderBuilder.MBFct.MB_FCT_SIMPLE || mProtocol.mMBProtocolFunction != FTMHeaderBuilder.MBFct.MB_FCT_DUMMY)) {
            // this.setM_execution_result(false);
            Log.e(this.getClass().getName(), "onPostExecute Step2 protocol error ...");
            mProtocol.setProtocoltoTheEnd();

            if (mProtocol.mMBProtocolBuilderReceived != null) {
                notifySomethingHappened(mProtocol.getProtocolError() | mMBTaskExecutionError |  mProtocol.mMBProtocolBuilderReceived.getHeaderError());
            } else {
                notifySomethingHappened(mProtocol.getProtocolError() | mMBTaskExecutionError);
            }
        } else {
            // check reported data
            Log.v(this.getClass().getName(), "onPostExecute Step2 protocol finished ...");
            //notifySomethingHappened(mMBTaskExecutionError == 0? false:true);
            if (mProtocol.mMBProtocolBuilderReceived != null) {
                notifySomethingHappened(mProtocol.getProtocolError() | mMBTaskExecutionError |  mProtocol.mMBProtocolBuilderReceived.getHeaderError());
            } else {
                notifySomethingHappened(mProtocol.getProtocolError() | mMBTaskExecutionError);
            }

        }

    }

}
