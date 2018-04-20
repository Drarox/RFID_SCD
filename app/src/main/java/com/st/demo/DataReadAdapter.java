// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS 
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE 
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY 
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS 
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS 
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.st.demo;

import java.util.List;


import com.st.nfcv.DataRead;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
//import android.util.Log;

public class DataReadAdapter extends BaseAdapter
{
    private Context context;

    private List<DataRead> listDataRead;

    public DataReadAdapter(Context context, List<DataRead> listData)
    {
        this.context = context;
        this.listDataRead = listData;
    }

    public int getCount() {
        return listDataRead.size();
    }

    public Object getItem(int position) {
        return listDataRead.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        DataRead entry = listDataRead.get(position);
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.block_lr_data_row, null);
        }
        TextView nameBlock = (TextView) convertView.findViewById(R.id.nameBlock);
        nameBlock.setText(entry.getName());

        TextView valueBlock = (TextView) convertView.findViewById(R.id.valueBlock);
        valueBlock.setText(entry.getValue());

        return convertView;
    }
}
