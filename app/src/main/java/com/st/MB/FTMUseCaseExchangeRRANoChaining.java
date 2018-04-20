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

import java.util.ArrayList;
import java.util.List;

import com.st.MB.FTMHeaderBuilder.MBFct;

public class FTMUseCaseExchangeRRANoChaining  extends FTMUseCaseGen  implements MBTransferListener {

    private List<MBTransferListenerFWU> listeners = new ArrayList<MBTransferListenerFWU>();

    protected FTMPTColRtoHFWUReqResp sp;
    protected FTMPTColRtoHFWUAck sp2;

    public FTMUseCaseExchangeRRANoChaining(MBFct fct ) {
        // TODO Auto-generated constructor stub
        setupTaskAndProtocol(fct);

    }


    public FTMUseCaseExchangeRRANoChaining( ) {
        // TODO Auto-generated constructor stub
        setupTaskAndProtocol(MBFct.MB_FCT_SIMPLE);
/*        sp = new MBSimplePTColRtoHFWUReqResp(MBFct.MB_FCT_SIMPLE);
        sp2 = new MBSimplePTColRtoHFWUAck(MBFct.MB_FCT_SIMPLE);
        // ================== Step 2
        sp2.setCmdPayload(null);
        sp2.setCheckProtocolDataEXchangeParameters(false, 0);
        // ========================
        sp.setCmdPayload(null);
        sp.setCheckProtocolDataEXchangeParameters(false, 0);*/

    }

    private void setupTaskAndProtocol(MBFct fct) {
        sp = new FTMPTColRtoHFWUReqResp(fct);
        sp2 = new FTMPTColRtoHFWUAck(fct);
        // ================== Step 2
        sp2.setCmdPayload(null);
        sp2.setCheckProtocolDataEXchangeParameters(false, 0);
        // ========================
        sp.setCmdPayload(null);
        sp.setCheckProtocolDataEXchangeParameters(false, 0);

        stask = new FTMTaskRequestResponse();
        stask.addListener(this);
        stask2 = new FTMTaskAcknowledge();
        stask2.addListener(this);

        stask2.setProtocol(sp2);
        stask.setProtocol(sp);

        stask.mNextTransferTask = stask2;

    }

    public void execute() {
        // ================== Step 1 task
        // MBProcessTask stask = new MBProcessTask();

        mStartTime_ms = System.currentTimeMillis();
        stask.execute();
        // ================== Step 2 task2 configured in postExecute Step1
    }




}
