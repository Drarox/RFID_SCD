package com.st.Fragments.DVRegisters;

import android.content.Context;
import android.widget.TextView;

import com.st.util.EditTextWithOneChoiceCustomBehaviour;

/**
 * Created by MMY team on 11/9/2016.
 */
public class EditTextWithOneChoiceDVEHBehaviour extends EditTextWithOneChoiceCustomBehaviour {

    public EditTextWithOneChoiceDVEHBehaviour(Context context, TextView textView, byte initRefValue) {

        super(context, textView,"[0-1]+", "Energy Harvesting mode Fields?", new String[]{
                "EH_Mode = 0",
                "EH_Mode = 1"
        }, new boolean[]{
                (initRefValue & 0x01) == 0?true:false, //
                (initRefValue & 0x01) == 1?true:false
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
