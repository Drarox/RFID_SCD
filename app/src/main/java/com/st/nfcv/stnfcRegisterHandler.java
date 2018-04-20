package com.st.nfcv;

import java.util.Hashtable;
import java.util.Set;

import android.nfc.Tag;

public class stnfcRegisterHandler {


    public enum ST25DVRegisterTable {
        Reg_GPO,
        Reg_ITime,
        Reg_EH,
        Reg_Rfdis,
        Reg_RFZ1SS,
        Reg_End1,
        Reg_RFZ2SS,
        Reg_End2,
        Reg_RFZ3SS,
        Reg_End3,
        Reg_RFZ4SS,
        Reg_LockCCfile,
        Reg_MB_EN,
        Reg_MB_WDG,
        Reg_LockCfg
    }

    public byte Reg_GPO_value = -1;
    public byte Reg_ITime_value = -1;
    public byte Reg_EH_value = -1;
    public byte Reg_Rfdis_value = -1;
    public byte Reg_RFZ1SS_value = -1;
    public byte Reg_End1_value = -1;
    public byte Reg_RFZ2SS_value = -1;
    public byte Reg_End2_value = -1;
    public byte Reg_RFZ3SS_value = -1;
    public byte Reg_End3_value = -1;
    public byte Reg_RFZ4SS_value = -1;
    public byte Reg_LockCCfile_value = -1;
    public byte Reg_MB_EN_value = -1;
    public byte Reg_MB_WDG_value = -1;
    public byte Reg_LockCfg_value = -1;

    private Hashtable<ST25DVRegisterTable, Byte> RegisterFctDescr = new Hashtable<ST25DVRegisterTable, Byte>() {
        {
            put(ST25DVRegisterTable.Reg_GPO, (byte) 0x00);
            put(ST25DVRegisterTable.Reg_ITime, (byte) 0x01);
            put(ST25DVRegisterTable.Reg_EH, (byte) 0x02);
            put(ST25DVRegisterTable.Reg_Rfdis, (byte) 0x03);
            put(ST25DVRegisterTable.Reg_RFZ1SS, (byte) 0x04);
            put(ST25DVRegisterTable.Reg_End1, (byte) 0x05);
            put(ST25DVRegisterTable.Reg_RFZ2SS, (byte) 0x06);
            put(ST25DVRegisterTable.Reg_End2, (byte) 0x07);
            put(ST25DVRegisterTable.Reg_RFZ3SS, (byte) 0x08);
            put(ST25DVRegisterTable.Reg_End3, (byte) 0x09);
            put(ST25DVRegisterTable.Reg_RFZ4SS, (byte) 0x0A);
            put(ST25DVRegisterTable.Reg_LockCCfile, (byte) 0x0C);
            put(ST25DVRegisterTable.Reg_MB_EN, (byte) 0x0D);
            put(ST25DVRegisterTable.Reg_MB_WDG, (byte) 0x0E);
            put(ST25DVRegisterTable.Reg_LockCfg, (byte) 0x0F);
        }
    };
    private Hashtable<ST25DVRegisterTable, Short> RegisterFctValue = new Hashtable<ST25DVRegisterTable, Short>();


    public stnfcRegisterHandler() {
        // TODO Auto-generated constructor stub

        RegisterFctValue.put(ST25DVRegisterTable.Reg_GPO, (short) Reg_GPO_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_ITime, (short)Reg_ITime_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_EH, (short)Reg_EH_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_Rfdis, (short)Reg_Rfdis_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_RFZ1SS, (short)Reg_RFZ1SS_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_End1, (short)Reg_End1_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_RFZ2SS, (short)Reg_RFZ2SS_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_End2, (short)Reg_End2_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_RFZ3SS, (short)Reg_RFZ3SS_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_End3, (short)Reg_End3_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_RFZ4SS, (short)Reg_RFZ4SS_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_LockCCfile, (short)Reg_LockCCfile_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_MB_EN, (short)Reg_MB_EN_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_MB_WDG, (short)Reg_MB_WDG_value);
        RegisterFctValue.put(ST25DVRegisterTable.Reg_LockCfg, (short)Reg_LockCfg_value);

    }

    public byte getKnownRegister(ST25DVRegisterTable target) {
        byte ret = -1;
        if (RegisterFctDescr.containsKey(target)) {
            ret = RegisterFctDescr.get(target);
            ;
        } else {
            ret = -1;
        }
        return ret;
    }


    public int getNbMemoryZone(byte icRef) {
        int ret = 0;
        int endZone;
        endZone = -1;
        int value;
        value = -1;

        endZone = getKnownRegisterValue(ST25DVRegisterTable.Reg_End1);
        if (endZone != -1) {
            ret ++;
        }
        value = getKnownRegisterValue(ST25DVRegisterTable.Reg_End2);
        if ( value > endZone) {
            endZone = value;
            ret++;
        }
        value = getKnownRegisterValue(ST25DVRegisterTable.Reg_End3);
        if ( value > endZone) {
            endZone = value;
            ret++;
            if (isALastZone(icRef,endZone)) {
                ret++;
            }
        }
        return ret;
    }

    private boolean isALastZone (byte icRef, int lastValue) {
        boolean ret = false;
        if (icRef == 0x26 ) { // FF  = 64k or 3F = 16k
            // according to IcRef 26 can be 64k or 16k
            if (lastValue < 0xFF && lastValue > 0x3F) ret = true;
            else if (lastValue < 0x3F) ret = true;
        }  else if (icRef == 0x24  || icRef == 0x25) { // Product Code 24 or 25 for 4k
            if (lastValue < 0x0F) ret = true;
        }else {
            // Nothing to do
        }
        return ret;
    }

    public int getZoneAddress(int zone) {
        int ret = 0;
        switch (zone) {
            case 0:
                ret = 0;
                break;
            case 1:
                ret = (getKnownRegisterValue(ST25DVRegisterTable.Reg_End1) * 8 + 7)+1;
                break;
            case 2:
                ret = (getKnownRegisterValue(ST25DVRegisterTable.Reg_End2) * 8 + 7)+1;
                break;
            case 3:
                ret = (getKnownRegisterValue(ST25DVRegisterTable.Reg_End3) * 8 + 7)+1;
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     *
     * @param zone
     * @param maxMemoryBlockSize
     * @return Zone size in blocks
     */
    public int getZoneSize(int zone, int maxMemoryBlockSize) {
        int ret = 0;
        switch (zone) {
            case 0:
                ret = (getKnownRegisterValue(ST25DVRegisterTable.Reg_End1) * 8 + 7) +1 ;
                break;
            case 1:
                ret = ((getKnownRegisterValue(ST25DVRegisterTable.Reg_End2) * 8 + 7) -
                        ((getKnownRegisterValue(ST25DVRegisterTable.Reg_End1))* 8 + 7) );
                break;
            case 2:
                ret = ((getKnownRegisterValue(ST25DVRegisterTable.Reg_End3)* 8 + 7) -
                        ((getKnownRegisterValue(ST25DVRegisterTable.Reg_End2)) * 8 +7) );
                break;
            case 3:
                int lastEndZ3 = getKnownRegisterValue(ST25DVRegisterTable.Reg_End3);
                ret = (((maxMemoryBlockSize))  -  (lastEndZ3* 8 + 7));
                break;
            default:
                break;
        }
        return ret;
    }



    public boolean isAKnownRegister(ST25DVRegisterTable target) {
        boolean ret = false;
        if (RegisterFctDescr.containsKey(target)) {
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }

    public boolean isADynamicRegister(ST25DVRegisterTable target) {
        boolean ret = false;
        if (target == ST25DVRegisterTable.Reg_EH || target == ST25DVRegisterTable.Reg_MB_EN) {
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }


    public short getKnownRegisterValue(ST25DVRegisterTable target) {
        short ret = -1;
        if (RegisterFctValue.containsKey(target)) {
            ret = RegisterFctValue.get(target);
        } else {
            ret = -1;
        }
        return ret;
    }

    public boolean readAllSystemRegister(Tag myTag, SysFileLRHandler ma) {
        boolean ret = true;
        Set<ST25DVRegisterTable> keys = RegisterFctDescr.keySet();
        BasicOperation bop = new BasicOperation(ma.getMaxTransceiveLength());
        boolean staticRegister = true;
        for (ST25DVRegisterTable key : keys) {
            if (bop.readRegister(key, staticRegister) == 0) {
                // ok
                byte[] answer = bop.getMBBlockAnswer();
                if (answer[0] == 0 && answer.length == 2) {
                    RegisterFctValue.put(key, (short) (answer[1] & 0x00FF));
                } else {
                    // issue answer length
                    RegisterFctValue.put(key, (short) -1);
                    ret = false;
                }

            } else {
                // ko
                if (key == ST25DVRegisterTable.Reg_LockCCfile) {
                } else {
                    ret = false;
                }
                byte[] answer = bop.getMBBlockAnswer();
                RegisterFctValue.put(key, (short) -1);

            }

        }
        return ret;
    }

    public void setRegisterValue(ST25DVRegisterTable reg, byte value) {
        // TODO Auto-generated method stub
        if (RegisterFctValue.containsKey(reg)) {
            RegisterFctValue.put(reg, (short) value);
        }

    }


    public boolean isWriteUnLocked(ST25DVRegisterTable reg) {
        boolean ret = false;
        short value = -1;
        if (RegisterFctValue.containsKey(reg)) {
            value = RegisterFctValue.get(reg);
        }
        if (value != -1) {
            //if ((value & 0x03) == 0) ret = true; // no pwd
            //if ((value & 0x04) == 0) ret = true; // no protection           if ((value & 0x04) == 0) ret = true; // no protection
            if ((value & 0x0C) == 0) ret = true;
        }
        return ret;
    }
    public boolean isReadUnLocked(ST25DVRegisterTable reg) {
        boolean ret = false;
        short value = -1;
        if (reg == ST25DVRegisterTable.Reg_RFZ1SS) return true;
        if (RegisterFctValue.containsKey(reg)) {
            value = RegisterFctValue.get(reg);
        }
        if (value != -1) {
            //if ((value & 0x03) == 0) ret = true; // no pwd
            if ((value & 0x08) == 0) ret = true; // no protection
        }
        return ret;
    }

    public int getPasswordNumber(ST25DVRegisterTable reg) {
        int ret = -1;
        short value = -1;
        if (RegisterFctValue.containsKey(reg)) {
            value = RegisterFctValue.get(reg);
        }
        if (value != -1) {
            //if ((value & 0x03) == 0) ret = true; // no pwd
            ret = (value & 0x03); // pwd
        }
        return ret;
    }
    public int getPasswordNumber(int area) {
        int ret = -1;
        short value = -1;
        ST25DVRegisterTable reg = getZSSentry(area);
        if (RegisterFctValue.containsKey(reg)) {
            value = RegisterFctValue.get(reg);
        }
        if (value != -1) {
            //if ((value & 0x03) == 0) ret = true; // no pwd
            ret = (value & 0x03); // pwd
        }
        return ret;
    }

    public ST25DVRegisterTable getZSSentry(int area) {
        ST25DVRegisterTable reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ1SS;
        switch (area){
            case 0:
                break;
            case 1:
                reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ2SS;
                break;
            case 2:
                reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ3SS;
                break;
            case 3:
                reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ4SS;
                break;
            default:
                break;
        }
        return reg;
    }



}
