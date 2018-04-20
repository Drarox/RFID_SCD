package com.st.NFC;


public abstract class EnergyHarvesting {
    private String TAG = "EnergyHarvesting";
    String mValueEHConfigByte = "";
    String mValueEHEnableByte = "";

    public EnergyHarvesting() {
        // TODO Auto-generated constructor stub
    }

    public void setValueEHConfigByte(String valueEHConfigByte) {
        this.mValueEHConfigByte = valueEHConfigByte;
    }

    public void setValueEHEnableByte(String valueEHEnableByte) {
        this.mValueEHEnableByte = valueEHEnableByte;
    }



    public String getValueEHConfigByte() {
        return mValueEHConfigByte;
    }
    public String getValueEHEnableByte() {
        return mValueEHEnableByte;
    }

    public abstract int readEHConfig() ;
    public abstract int writeEHConfig(byte EHconfig);
    public abstract int checkEHConfig() ;
    public abstract int resetEHConfig() ;
    public abstract int setEHConfig() ;
    public abstract int writeD0Config(byte EHconfig);

}
