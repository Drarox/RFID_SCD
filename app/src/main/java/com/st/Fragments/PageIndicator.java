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

package com.st.Fragments;

import com.st.demo.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PageIndicator extends View {

    int totalNoOfDots;
    int activeDot;
    int dotSpacing;
    int horizontalSpace = 5;
    Bitmap activeDotBitmap;
    Bitmap normalDotBitmap;
    int x=0;


    private Paint paint;

     public PageIndicator(Context context) {
         super(context);
         paint = new Paint(Paint.ANTI_ALIAS_FLAG);
         activeDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_active);
         normalDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_normal);
         }

     public PageIndicator(Context context, AttributeSet attrs ) {
         super(context,attrs);
         paint = new Paint(Paint.ANTI_ALIAS_FLAG);
         activeDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_active);
         normalDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_normal);
     }

     public PageIndicator(Context context, AttributeSet attrs , int defStyle) {
         super(context,attrs,defStyle);
         paint = new Paint(Paint.ANTI_ALIAS_FLAG);
         activeDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_active);
         normalDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_normal);
     }

     @Override
     protected void onDraw(Canvas canvas) {
         drawDot(canvas);
         super.onDraw(canvas);
     }

     private void drawDot(Canvas canvas){
         for(int i=0;i<totalNoOfDots;i++){
         if(i==activeDot){
             canvas.drawBitmap(activeDotBitmap, x, 0, paint);
         }else{
             canvas.drawBitmap(normalDotBitmap, x, 0, paint);
         }
         x=x+activeDotBitmap.getWidth()+horizontalSpace+dotSpacing;
         }
     }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = totalNoOfDots*(activeDotBitmap.getWidth()+horizontalSpace+ getDotSpacing());
        width = resolveSize(width, widthMeasureSpec);
        int height = activeDotBitmap.getHeight();
        height = resolveSize(height, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public void refresh() {
        x = 0;
        invalidate();
    }

     public int getTotalNoOfDots() {
         return totalNoOfDots;
    }

     public void setTotalNoOfDots(int totalNoOfDots) {
         this.totalNoOfDots = totalNoOfDots;
         x=0;
         invalidate();
     }

     public int getActiveDot() {
         return activeDot;
     }

     public void setActiveDot(int activeDot) {
         this.activeDot = activeDot;
         x=0;
         invalidate();
     }

     public int getDotSpacing() {
         return dotSpacing;
     }

     public void setDotSpacing(int dotSpacing) {
         this.dotSpacing = dotSpacing;
         x=0;
         invalidate();
     }
}
