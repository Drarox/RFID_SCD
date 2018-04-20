/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1377 $
  * Revision of last commit    :  $Rev: 1377 $
  * Date of last commit     :  $Date: 2015-11-16 15:29:00 +0100 (Mon, 16 Nov 2015) $ 
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

package com.st.NFC;



public class sysfileHandler  implements SysFileGenHandler {

    private static final int BF_000B = 0;
    private static final int BF_001B = 1;
    private static final int BF_010B = 2;
    private static final int BF_011B = 3;
    private static final int BF_100B = 4;
    private static final int BF_101B = 5;
    private static final int BF_110B = 6;
    private static final int BF_111B = 7;

    private static final int WATCHDOG_VALUE_000B = 0;
    private static final int WATCHDOG_VALUE_001B = 30;
    private static final int WATCHDOG_VALUE_010B = 60;
    private static final int WATCHDOG_VALUE_011B = 120;
    private static final int WATCHDOG_VALUE_100B = 240;
    private static final int WATCHDOG_VALUE_101B = 480;
    private static final int WATCHDOG_VALUE_110B = 960;
    private static final int WATCHDOG_VALUE_111B = 1920;

    // Better to get this String def in String.Xml Definition
    //+ APT 10/09/2013
    //private static final String GPO_RF_VALUE_000B = "do not use";
    private static final String GPO_RF_VALUE_000B = "not used";
    //- APT 10/09/2013
    private static final String GPO_RF_VALUE_001B = "Session opened";
    private static final String GPO_RF_VALUE_010B = "WIP";
    private static final String GPO_RF_VALUE_011B = "MIP";
    private static final String GPO_RF_VALUE_100B = "Interrupt";
    private static final String GPO_RF_VALUE_101B = "State Control";
    private static final String GPO_RF_VALUE_110B = "RF Busy";
    private static final String GPO_RF_VALUE_111B = "Field Detect";

    //+ APT 10/09/2013
    //private static final String GPO_I2C_VALUE_000B = "do not use";
    private static final String GPO_I2C_VALUE_000B = "not used";
    //- APT 10/09/2013
    private static final String GPO_I2C_VALUE_001B = "Session opened";
    private static final String GPO_I2C_VALUE_010B = "WIP";
    private static final String GPO_I2C_VALUE_011B = "I2C Answer Ready";
    private static final String GPO_I2C_VALUE_100B = "Interrupt";
    private static final String GPO_I2C_VALUE_101B = "State Control";
    private static final String GPO_I2C_VALUE_110B = "RFU";
    private static final String GPO_I2C_VALUE_111B = "RFU";


    // SRTAG 2KL Specific
    private static final String WRITE_EVENT_COUNTER_ENABLED = "Write Event Counter Enalbed";
    private static final String READ_EVENT_COUNTER_ENABLED  = "Read Event Counter Enalbed";
    private static final String EVENT_COUNTER_DISABLED         = "Event Counter Disable";
    // SRTAG 2KL Counter Specific
    protected int     mCounterValue;
    protected byte  mEventConfig;

    protected int    mLength;
    protected byte  mI2Cpotect;
    protected byte  mWatchdog;
    protected byte     mGPOStatus;
    protected byte  mWirePowermgt;
    protected byte  mRFEnable;
    protected byte    mNDEFFileNumber;
    protected byte    mProductVersion;
    protected byte  [] mUID;
    protected int mmemSize;
    protected byte    mproductCode;

    public sysfileHandler()
    {
        mLength         = 0;
        mI2Cpotect         = 0;
        mWatchdog         = 0;
        mGPOStatus         = 0;
        mWirePowermgt     = 0;
        mRFEnable         = 0;
        mNDEFFileNumber = 0;
        mProductVersion = 0;
        mUID             = new byte[7];
        mmemSize        = 0;
        mproductCode    = 0;
        mEventConfig    = 0;
        mCounterValue     = 0;

    }




    public sysfileHandler(byte[] sysFileBuff) {
        int currentIndex = 0;

        if ((sysFileBuff == null) || (sysFileBuff.length == 0)) {
            mLength = 0;
            return;
        }
        if (NFCApplication.getApplication().getCurrentTag().isSRTAG2KL()
                || NFCApplication.getApplication().getCurrentTag().isST25TA02K()) {
            mLength = (((int) sysFileBuff[currentIndex] & 0xFF) << 8) + ((int) sysFileBuff[++currentIndex] & 0xFF);
            mI2Cpotect = 0x00;
            mWatchdog = 0x00;
            mGPOStatus = sysFileBuff[++currentIndex];
            mEventConfig = sysFileBuff[++currentIndex];
            mCounterValue = (((int) sysFileBuff[++currentIndex] & 0xFF) << 16)
                    + (((int) sysFileBuff[++currentIndex] & 0xFF) << 8) + ((int) sysFileBuff[++currentIndex] & 0xFF);
            mProductVersion = (byte) (sysFileBuff[++currentIndex] & 0xFF); // Product
                                                                            // Version
                                                                            // Location
                                                                            // <=0x13
                                                                            // SRTAG2K
            mUID = new byte[7];
            System.arraycopy(sysFileBuff, ++currentIndex, this.mUID, 0, 7);
            currentIndex = currentIndex + 6;
            mmemSize = (((int) sysFileBuff[++currentIndex] & 0xFF) << 8) + ((int) sysFileBuff[++currentIndex] & 0xFF);
            mproductCode = sysFileBuff[++currentIndex];

        } else if (NFCApplication.getApplication().getCurrentTag().isM24SR()) {
            mLength = (((int) sysFileBuff[currentIndex] & 0xFF) << 8) + ((int) sysFileBuff[++currentIndex] & 0xFF);
            mI2Cpotect = sysFileBuff[++currentIndex];
            mWatchdog = sysFileBuff[++currentIndex];
            mGPOStatus = sysFileBuff[++currentIndex];
            mWirePowermgt = sysFileBuff[++currentIndex];
            mRFEnable = sysFileBuff[++currentIndex];
            mNDEFFileNumber = sysFileBuff[++currentIndex];
            mUID = new byte[7];
            System.arraycopy(sysFileBuff, ++currentIndex, this.mUID, 0, 7);
            currentIndex = currentIndex + 6;
            mmemSize = (((int) sysFileBuff[++currentIndex] & 0xFF) << 8) + ((int) sysFileBuff[++currentIndex] & 0xFF);
            mproductCode = sysFileBuff[++currentIndex];
        } else {
            mLength = 0;
            mI2Cpotect = 0;
            mWatchdog = 0;
            mGPOStatus = 0;
            mWirePowermgt = 0;
            mRFEnable = 0;
            mNDEFFileNumber = 0;
            mProductVersion = 0;
            mUID = new byte[7];
            currentIndex = currentIndex + 6;
            mmemSize = 0;
            mproductCode = 0;
        }
    }

    public byte [] getUID()
    {
        return mUID;
    }

    public String getUIDtoString()
    {
        return stnfchelper.bytArrayToHex(mUID);
    }

    public byte getNDEFfilenumber()
    {
        return mNDEFFileNumber;
    }

    public int getmemorySize()
    {
        return mmemSize;
    }

    public byte getProductCode()
    {
        return mproductCode;
    }

    public boolean isWatchdogActive()
    {
        return (mWatchdog!=0)?true:false;
    }

    public boolean isI2CProtectedEnabled()
    {
        //+ APT 06/09/2013
        //return (this.mI2Cpotect==0)?true:false;
        return (this.mI2Cpotect==0)?false:true;
        //- APT 06/09/2013
    }

    public int getGPORF()
    {
        //+ APT 10/09/2013
        /*return this.mGPOStatus&0x70;*/
        return ((this.mGPOStatus&0x70) >> 4);
        //- APT 10/09/2013
    }

    public int getGPOI2C()
    {
        return this.mGPOStatus&0x7;
    }

    public String getGPORFToString()
    {
        switch(getGPORF())
        {
        case BF_001B:
            return GPO_RF_VALUE_001B;
        case BF_010B:
            return GPO_RF_VALUE_010B;
        case BF_011B:
            return GPO_RF_VALUE_011B;
        case BF_100B:
            return GPO_RF_VALUE_100B;
        case BF_101B:
            return GPO_RF_VALUE_101B;
        case BF_110B:
            return GPO_RF_VALUE_110B;
        case BF_111B:
            return GPO_RF_VALUE_111B;
        default:
            //+ APT 10/09/2013
            //return GPO_I2C_VALUE_000B;
            return GPO_RF_VALUE_000B;
            //- APT 10/09/2013
        }
    }

    public String getGPOI2CToString()
    {
        switch(getGPOI2C())
        {
        case BF_001B:
            return GPO_I2C_VALUE_001B;
        case BF_010B:
            return GPO_I2C_VALUE_010B;
        case BF_011B:
            return GPO_I2C_VALUE_011B;
        case BF_100B:
            return GPO_I2C_VALUE_100B;
        case BF_101B:
            return GPO_I2C_VALUE_101B;
        case BF_110B:
            return GPO_I2C_VALUE_110B;
        case BF_111B:
            return GPO_I2C_VALUE_111B;
        default:
            return GPO_I2C_VALUE_000B;
        }
    }

    public int getWatchdogTimerValueinms()
    {
        switch (mWatchdog&0x07)
        {
        case BF_001B:
            return WATCHDOG_VALUE_001B;
        case BF_010B:
            return WATCHDOG_VALUE_010B;
        case BF_011B:
            return WATCHDOG_VALUE_011B;
        case BF_100B:
            return WATCHDOG_VALUE_100B;
        case BF_101B:
            return WATCHDOG_VALUE_101B;
        case BF_110B:
            return WATCHDOG_VALUE_110B;
        case BF_111B:
            return WATCHDOG_VALUE_111B;
        default:
            return WATCHDOG_VALUE_000B;
        }
    }

    public boolean isPowerSuppliedByRF()
    {
        return ((this.mWirePowermgt&0x01) == 0)?true:false;
    }

    public boolean isRFFielenabled()
    {
        //+ APT 10/09/2013
//        return ((this.mRFEnable&0x80)==1)?true:false;
        return ((this.mRFEnable&0x80)==0)?false:true;
        //- APT 10/09/2013
    }

    public boolean isRFdisablePadIsHigh()
    {
        return ((this.mRFEnable&0x04)==1)?true:false;
    }

    public boolean isRFCMDdecoded()
    {
        return ((this.mRFEnable&0x01)==1)?true:false;
    }
    
    public boolean isCounterEnabled()
    {
        if (NFCApplication.getApplication().getCurrentTag().isSRTAG2KL() || NFCApplication.getApplication().getCurrentTag().isST25TA02K())
            return ((mEventConfig & (0x02))!= 0x00);
        else
            return false;
    }

    public boolean isWriteCounter()
    {
        if (NFCApplication.getApplication().getCurrentTag().isSRTAG2KL() || NFCApplication.getApplication().getCurrentTag().isST25TA02K())
        {
            return ((mEventConfig & (0x01))== 0x01);
        }
        else
            return false;
    }

    public boolean isReadCounter()
    {
        if (NFCApplication.getApplication().getCurrentTag().isSRTAG2KL()|| NFCApplication.getApplication().getCurrentTag().isST25TA02K())
        {
            return ((mEventConfig & (0x01))== 0x00);
        }
        else
            return false;
    }

    public boolean isLockedCounter()
    {
        if (NFCApplication.getApplication().getCurrentTag().isSRTAG2KL()|| NFCApplication.getApplication().getCurrentTag().isST25TA02K())
        {
            return ((mEventConfig & (0x80))== 0x80);
        }
        else
            return false;
    }

    public int getCounterValue()
    {
        if (NFCApplication.getApplication().getCurrentTag().isSRTAG2KL()|| NFCApplication.getApplication().getCurrentTag().isST25TA02K())
        {
            return mCounterValue;
        }
        else
            return 0;
    }

    //+ APT 06/09/2013
    public int getSYSLength() {
        return mLength;
    }
    //- APT 06/09/2013

    // CR 29/06/2015 ST25TA02K-P
    public byte getProductVersion() {
        return mProductVersion;
    }
}
