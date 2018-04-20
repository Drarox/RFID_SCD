/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1708 $
  * Revision of last commit    :  $Rev: 1708 $
  * Date of last commit     :  $Date: 2016-02-28 17:44:48 +0100 (Sun, 28 Feb 2016) $
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

package com.st.nfcv;


import com.st.NFC.EnergyHarvesting;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.STNfcTagVHandler;

import android.util.Log;

public class stnfcm24LRHarvesting extends EnergyHarvesting {
    private String TAG = "stnfcm24LRHarvesting";

    public stnfcm24LRHarvesting() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int readEHConfig() {
        return m24LRReadEHconfig();
    }

    @Override
    public int writeEHConfig(byte EHconfig) {
        return m24LRWriteEHconfig(EHconfig);
    }

    @Override
    public int checkEHConfig() {
        return m24LRCheckEHconfig();
    }

    @Override
    public int resetEHConfig() {
        return m24LRResetEHconfig();
    }

    @Override
    public int setEHConfig() {
        return m24LRSetEHconfig();
    }

/*
    String valueEHconfigByte = "";

    String valueEHEnableByte = "";


    public String getValueEHconfigByte() {
        return valueEHconfigByte;
    }

    public String getValueEHEnableByte() {
        return valueEHEnableByte;
    }
*/

    private int m24LRReadEHconfig() {
        Log.d(TAG, " m24LRReadEHconfig");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " m24LRReadEHconfig Action");
            //
            byte[] ReadEHconfigAnswer = null;
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            cpt = 0;
            while ((ReadEHconfigAnswer == null || ReadEHconfigAnswer[0] == 1) && cpt <= 10) {
                ReadEHconfigAnswer = mtagHDL.getTypeVTagOperation().SendReadEHconfigCommand(currentTag.getTag(),
                        sysHDL.isUidRequested());
/*                ReadEHconfigAnswer = NFCCommandV.SendReadEHconfigCommand(currentTag.getTag(),
                        sysHDL.isUidRequested());*/
                cpt++;
            }
            if (ReadEHconfigAnswer == null) {
                returncd = currentTag.reportActionStatus("ERROR Read EH CONFIG byte (No tag answer) ", -1);
            } else if (ReadEHconfigAnswer[0] == (byte) 0x01) {
                returncd = currentTag.reportActionStatus("ERROR Read EH CONFIG byte ", 1);
            } else if (ReadEHconfigAnswer[0] == (byte) 0xFF) {
                returncd = currentTag.reportActionStatus("ERROR Read EH CONFIG byte ", 0xFF);
            } else if (ReadEHconfigAnswer[0] == (byte) 0x00) {
                setValueEHConfigByte(Helper.ConvertHexByteToString(ReadEHconfigAnswer[1]).toUpperCase());
                //valueEHconfigByte = Helper.ConvertHexByteToString(ReadEHconfigAnswer[1]).toUpperCase();
                returncd = currentTag.reportActionStatus("Read EH CONFIG byte Sucessfull ", 0x00);
                // finish();
            } else {
                returncd = currentTag.reportActionStatus("Read EH CONFIG byte ERROR ", -1);
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }

    private int m24LRWriteEHconfig(byte EHconfig) {
        Log.d(TAG, " m24LRWriteEHconfig");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " m24LRWriteEHconfig Action");
            //
            byte[] cmdEHconfigAnswer = null;
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            cpt = 0;
            while ((cmdEHconfigAnswer == null || cmdEHconfigAnswer[0] == 1) && cpt <= 10) {
/*                cmdEHconfigAnswer = NFCCommandV.SendWriteEHconfigCommand(currentTag.getTag(),
                        sysHDL.isUidRequested(), EHconfig);*/
                cmdEHconfigAnswer = mtagHDL.getTypeVTagOperation().SendWriteEHconfigCommand(currentTag.getTag(),
                        sysHDL.isUidRequested(), EHconfig);
                cpt++;
            }
            if (cmdEHconfigAnswer == null) {
                returncd = currentTag.reportActionStatus("ERROR Write EH CONFIG byte (No tag answer) ", -1);
            } else if (cmdEHconfigAnswer[0] == (byte) 0x01) {
                returncd = currentTag.reportActionStatus("ERROR Write EH CONFIG byte", 1);
            } else if (cmdEHconfigAnswer[0] == (byte) 0xFF) {
                returncd = currentTag.reportActionStatus("ERROR Write EH CONFIG byte", 0xFF);
            } else if (cmdEHconfigAnswer[0] == (byte) 0x00) {
                //valueEHconfigByte = Helper.ConvertHexByteToString(EHconfig).toUpperCase();
                setValueEHConfigByte(Helper.ConvertHexByteToString(EHconfig).toUpperCase());
                returncd = currentTag.reportActionStatus("Write EH CONFIG byte Sucessfull", 0x00);
                // finish();
            } else {
                returncd = currentTag.reportActionStatus("Write EH CONFIG byte ERROR", -1);
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }

    public int writeD0Config(byte EHconfig) {
        Log.d(TAG, " m24LRWriteD0config");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " m24LRWriteD0config Action");
            //
            byte[] cmdEHconfigAnswer = null;
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            cpt = 0;
            while ((cmdEHconfigAnswer == null || cmdEHconfigAnswer[0] == 1) && cpt <= 10) {
                cmdEHconfigAnswer = mtagHDL.getTypeVTagOperation().SendWriteD0configCommand(currentTag.getTag(),
                        sysHDL.isUidRequested(), EHconfig);
/*                cmdEHconfigAnswer = NFCCommandV.SendWriteD0configCommand(currentTag.getTag(),
                        sysHDL.isUidRequested(), EHconfig);*/
                cpt++;
            }
            if (cmdEHconfigAnswer == null) {
                returncd = currentTag.reportActionStatus("ERROR Write D0 CONFIG byte (No tag answer) ", -1);
            } else if (cmdEHconfigAnswer[0] == (byte) 0x01) {
                returncd = currentTag.reportActionStatus("ERROR Write D0 CONFIG byte", 1);
            } else if (cmdEHconfigAnswer[0] == (byte) 0xFF) {
                returncd = currentTag.reportActionStatus("ERROR Write D0 CONFIG byte", 0xFF);
            } else if (cmdEHconfigAnswer[0] == (byte) 0x00) {
                //valueEHconfigByte = Helper.ConvertHexByteToString(EHconfig).toUpperCase();
                setValueEHConfigByte(Helper.ConvertHexByteToString(EHconfig).toUpperCase());
                returncd = currentTag.reportActionStatus("Write D0 CONFIG byte Sucessfull", 0x00);
                // finish();
            } else {
                returncd = currentTag.reportActionStatus("Write EH CONFIG byte ERROR", -1);
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;

    }

    private int m24LRCheckEHconfig() {
        Log.d(TAG, " m24LRCheckEHconfig");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        //valueEHEnableByte = "XX";
        setValueEHEnableByte("XX");
        if (ret) {
            Log.d(TAG, " m24LRCheckEHconfig Action");
            //
            byte[] EHconfigAnswer = null;
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            cpt = 0;
            while ((EHconfigAnswer == null || EHconfigAnswer[0] == 1) && cpt <= 10) {
                EHconfigAnswer = mtagHDL.getTypeVTagOperation().SendCheckEHenableCommand(currentTag.getTag(),
                        sysHDL.isUidRequested());
/*                EHconfigAnswer = NFCCommandV.SendCheckEHenableCommand(currentTag.getTag(),
                        sysHDL.isUidRequested());
*/
                cpt++;
            }


            if (EHconfigAnswer == null) {
                returncd = currentTag.reportActionStatus("ERROR Check EH ENABLE byte (No tag answer) ", -1);
            } else if (EHconfigAnswer[0] == (byte) 0x01) {
                returncd = currentTag.reportActionStatus("ERROR Check EH ENABLE byte", 1);
            } else if (EHconfigAnswer[0] == (byte) 0xFF) {
                returncd = currentTag.reportActionStatus("ERROR Check EH ENABLE byte", 0xFF);
            } else if (EHconfigAnswer[0] == (byte) 0x00) {
                //valueEHEnableByte = Helper.ConvertHexByteToString(EHconfigAnswer[1]).toUpperCase();
                setValueEHEnableByte(Helper.ConvertHexByteToString(EHconfigAnswer[1]).toUpperCase());
                returncd = currentTag.reportActionStatus("Check EH ENABLE byte Sucessfull ", 0x00);
                // finish();
            } else {
                returncd = currentTag.reportActionStatus("Check EH ENABLE byte ERROR ", -1);
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }

    private int m24LRResetEHconfig() {
        Log.d(TAG, " m24LRResetEHconfig");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " m24LRResetEHconfig Action");
            //
            byte[] EHconfigAnswer = null;
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            cpt = 0;
            while ((EHconfigAnswer == null || EHconfigAnswer[0] == 1) && cpt <= 10) {
                EHconfigAnswer = mtagHDL.getTypeVTagOperation().SendResetEHenableCommand(currentTag.getTag(),
                        sysHDL.isUidRequested());
/*                EHconfigAnswer = NFCCommandV.SendResetEHenableCommand(currentTag.getTag(),
                        sysHDL.isUidRequested());    */
                cpt++;
            }


            if (EHconfigAnswer == null) {
                returncd = currentTag.reportActionStatus("ERROR Reset EH ENABLE byte (No tag answer) ", -1);
            } else if (EHconfigAnswer[0] == (byte) 0x01) {
                returncd = currentTag.reportActionStatus("ERROR Reset EH ENABLE byte", 1);
            } else if (EHconfigAnswer[0] == (byte) 0xFF) {
                returncd = currentTag.reportActionStatus("ERROR Reset EH ENABLE byte", 0xFF);
            } else if (EHconfigAnswer[0] == (byte) 0x00) {
                returncd = currentTag.reportActionStatus("Reset EH ENABLE byte Sucessfull ", 0x00);
                // finish();
            } else {
                returncd = currentTag.reportActionStatus("Reset EH ENABLE byte ERROR ", -1);
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }

    private int m24LRSetEHconfig() {
        Log.d(TAG, " m24LRSetEHconfig");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " m24LRSetEHconfig Action");
            //
            byte[] EHconfigAnswer = null;
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());

            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            cpt = 0;
            while ((EHconfigAnswer == null || EHconfigAnswer[0] == 1) && cpt <= 10) {
                EHconfigAnswer = mtagHDL.getTypeVTagOperation().SendSetEHenableCommand(currentTag.getTag(),
                        sysHDL.isUidRequested());
/*                EHconfigAnswer = NFCCommandV.SendSetEHenableCommand(currentTag.getTag(),
                        sysHDL.isUidRequested());*/
                cpt++;
            }


            if (EHconfigAnswer == null) {
                returncd = currentTag.reportActionStatus("ERROR Set EH ENABLE byte (No tag answer) ", -1);
            } else if (EHconfigAnswer[0] == (byte) 0x01) {
                returncd = currentTag.reportActionStatus("ERROR Set EH ENABLE byte", 1);
            } else if (EHconfigAnswer[0] == (byte) 0xFF) {
                returncd = currentTag.reportActionStatus("ERROR Set EH ENABLE byte", 0xFF);
            } else if (EHconfigAnswer[0] == (byte) 0x00) {
                returncd = currentTag.reportActionStatus("Set EH ENABLE byte Sucessfull ", 0x00);
                // finish();
            } else {
                returncd = currentTag.reportActionStatus("Set EH ENABLE byte ERROR ", -1);
            }

            //
        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }

}
