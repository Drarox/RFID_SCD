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

package com.st.demo;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 */
public class PlotingActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;


    private XYPlot plot1;
    private XYPlot plot2;


    private String _mlogTitle;
    private String _mXlabel;
    private String _mY1label;
    private String _mY2label;
    private int _mNBSample;
    private int _mX1ValueSize;
    private int _mY1ValueSize;
    private int _mY2ValueSize;
    private String [] _mXArrayValue;
    private String [] _mY1ArrayValue;
    private String [] _mY2ArrayValue;

    private byte[] _mlogspayload;


    public byte [] _mpayload;


    
    private void parsePayload(byte [] payload)
    {


        if (payload.length != 0)
        {
            int payloadOffset = 0;
            _mlogspayload = payload.clone();

            // retrieve Log Title
            byte [] logTitle = new byte[payload[0]&0xFF];
            System.arraycopy(payload, 1, logTitle, 0, payload[0]&0xFF);
            payloadOffset = ((int)payload[0])+1;

            try {
                _mlogTitle = new String(logTitle,"UTF-8");
            } catch (UnsupportedEncodingException e) {
            };

            // extract X unit label
            byte [] xlabel = new byte[payload[payloadOffset]];
            System.arraycopy(payload,payloadOffset+1, xlabel, 0, payload[payloadOffset]);
            payloadOffset = (payload[payloadOffset]) +1 + payloadOffset;

            try {
                _mXlabel = new String(xlabel,"UTF-8");
            } catch (UnsupportedEncodingException e) {
            };


            // extract Y1 unit label
            byte [] y1label = new byte[payload[payloadOffset]&0xFF];
            System.arraycopy(payload,payloadOffset+1, y1label, 0, payload[payloadOffset]);
            payloadOffset = (payload[payloadOffset]) +1 + payloadOffset;

            try {
                _mY1label = new String(y1label,"UTF-8");
            } catch (UnsupportedEncodingException e) {
            };

            // extract Y2 unit label
            byte [] y2label = new byte[payload[payloadOffset]&0xFF];
            System.arraycopy(payload, payloadOffset+1, y2label, 0, payload[payloadOffset]);
            payloadOffset = (payload[payloadOffset]) +1 + payloadOffset;
            try {
                _mY2label = new String(y2label,"UTF-8");
            } catch (UnsupportedEncodingException e) {
            };

            // retrieve nb samples value
            _mNBSample = (payload[payloadOffset]);
            payloadOffset=payloadOffset+1;

            // retrieve X1 value size
            _mX1ValueSize = (payload[payloadOffset]);
            payloadOffset=payloadOffset+1;

            // retrieve Y1 value size
            _mY1ValueSize = (payload[payloadOffset]);
            payloadOffset=payloadOffset+1;

            // retrieve Y2 value size
            _mY2ValueSize = (payload[payloadOffset]);
            payloadOffset=payloadOffset+1;

            // instantiate Value array

            _mXArrayValue =  (String[]) new String[_mNBSample];
            _mY1ArrayValue = (String[]) new String[_mNBSample];
            _mY2ArrayValue =  (String[]) new String[_mNBSample];

            byte[] tempXArray = new byte[_mX1ValueSize];
            byte[] tempY1Array =  new byte [_mY1ValueSize];
            byte[] tempY2Array =  new byte [_mY2ValueSize];

            for (int i=0;i<_mNBSample;i++)
            {
                System.arraycopy(payload, payloadOffset, tempXArray, 0, _mX1ValueSize);
                payloadOffset = payloadOffset +_mX1ValueSize;
                try {
                    _mXArrayValue[i] = new String(tempXArray,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                };
            }
            for (int i=0;i<_mNBSample;i++)
            {
                System.arraycopy(payload, payloadOffset, tempY1Array, 0, _mY1ValueSize);
                payloadOffset = payloadOffset +_mY1ValueSize;
                try {
                    _mY1ArrayValue[i] = new String(tempY1Array,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                };
            }
            for (int i=0;i<_mNBSample;i++)
            {
                System.arraycopy(payload, payloadOffset, tempY2Array, 0, _mY2ValueSize);
                payloadOffset = payloadOffset +_mY2ValueSize;
                try {
                    _mY2ArrayValue[i] = new String(tempY2Array,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                };
            }
        }

    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        setContentView(R.layout.activity_ploting);

        Intent launcherintent = getIntent();
        if (launcherintent==null)
        {
            // must not happen..
            Log.d("TAG","Creator INTENT is NULL");
            this.finish();
        }

        _mpayload = launcherintent.getByteArrayExtra("datalogs");
        parsePayload(_mpayload);

        // fun little snippet that prevents users from taking screenshots
        // on ICS+ devices :-)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                                 WindowManager.LayoutParams.FLAG_SECURE);
 
        setContentView(R.layout.activity_ploting);
 
        // initialize our XYPlot reference:
        plot1 = (XYPlot) findViewById(R.id.mySimpleXYPlotSerie1);
        plot2 = (XYPlot) findViewById(R.id.mySimpleXYPlotSerie2);
        

        Float[] _mFloatXArrayValue= new Float[_mNBSample];
        Float[] _mFloatY1ArrayValue= new Float[_mNBSample];
        Float[] _mFloatY2ArrayValue= new Float[_mNBSample];
        
        int i=0;
        for(String str:_mXArrayValue){
            _mFloatXArrayValue[i]=Float.parseFloat(str);
            i++;}
         i=0;
        for(String str:_mY1ArrayValue){
            _mFloatY1ArrayValue[i]=Float.parseFloat(str);
            i++;}
         i=0;
        for(String str:_mY2ArrayValue){
            _mFloatY2ArrayValue[i]=Float.parseFloat(str);
            i++;}
        
            
        // Create a couple arrays of y-values to plot:
        //Number[] series1Numbers = _mFloatY1ArrayValue.clone();//;{1,1,2,8,3,5,4,2,5,7,6,4};
        // Number[] series2Numbers = ;//{4, 6, 3, 8, 2, 10};

        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(_mFloatY1ArrayValue),             // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                _mY1label);                                     // Set the display title of the series
 
        // same as above
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(_mFloatY2ArrayValue), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, _mY2label);
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getApplicationContext(), R.xml.line_point_formatter_with_plf1);
 
        // add a new series' to the xyplot:
        plot1.addSeries(series1, series1Format);
        plot1.setTitle(_mlogTitle);
        plot1.setDomainLabel(_mXlabel);
        plot1.setRangeLabel(_mY1label);
        // same as above:
        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf2);
        plot2.addSeries(series2, series2Format);
        plot2.setTitle(_mlogTitle);
        plot2.setDomainLabel(_mXlabel);
        plot2.setRangeLabel(_mY2label);
        // reduce the number of range labels
        plot1.setTicksPerRangeLabel(3);
        plot1.getGraphWidget().setDomainLabelOrientation(-45);
        // reduce the number of range labels
        plot2.setTicksPerRangeLabel(3);
        plot2.getGraphWidget().setDomainLabelOrientation(-45);
    }



}
