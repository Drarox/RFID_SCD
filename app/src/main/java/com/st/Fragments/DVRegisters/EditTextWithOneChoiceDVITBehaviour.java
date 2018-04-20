package com.st.Fragments.DVRegisters;

import android.content.Context;
import android.widget.TextView;

import com.st.util.EditTextWithOneChoiceCustomBehaviour;
import com.st.util.EditTextWithXChoiceCustomBehaviour;

/**
 * Created by MMY team on 11/9/2016.
 */
public class EditTextWithOneChoiceDVITBehaviour extends EditTextWithOneChoiceCustomBehaviour {

    public EditTextWithOneChoiceDVITBehaviour(Context context, TextView textView,byte initRefValue) {

        super(context, textView,"[0-7]+", "IT Time Fields?", new String[]{
                "000 Min:293,58 Max=352,73",
                "001 Min:256,Max=88 313,80",
                "010 Min:220,Max=18 274,87",
                "011 Min:183,Max=49 235,94",
                "100 Min:146,Max=79 197,01",
                "101 Min:110,Max=09 158,08",
                "110 Min:73,Max=39 119,15",
                "111 Min:36,Max=70 80,22"
        }, new boolean[]{
                (initRefValue & 0x07) == 0?true:false, //
                (initRefValue & 0x07) == 1?true:false, //
                (initRefValue & 0x07) == 2?true:false, //
                (initRefValue & 0x07) == 3?true:false, //
                (initRefValue & 0x07) == 4?true:false, //
                (initRefValue & 0x07) == 5?true:false, //
                (initRefValue & 0x07) == 6?true:false, //
                (initRefValue & 0x07) == 7?true:false //
        } );
    }

    @Override
    protected byte computeValueFromFields() {
        return value;
    }


    @Override
    protected void manageValueOnChoice(int which) {
        value = (byte) which;
    }
}
