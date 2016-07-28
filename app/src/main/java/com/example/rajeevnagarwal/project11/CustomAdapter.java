package com.example.rajeevnagarwal.project11;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rajeev Nagarwal on 7/15/2016.
 */
public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Rooms> rm;
    public CustomAdapter(Context context,ArrayList<Rooms> rm)
    {
        this.context = context;
        this.rm = rm;
    }
    public View getView(int position,View convertView, ViewGroup parent)
    {

        View gridView;
        TextView textView=null;
        TextView textView1=null;
        ImageView img=null;
        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.item,null);


        }
        else
        {
            gridView = convertView;
        }

        if (position<rm.size()) {
            textView = (TextView) gridView.findViewById(R.id.roomname);
            textView1 = (TextView) gridView.findViewById(R.id.devlist);
            textView.setText(rm.get(position).getName());
            textView1.setText("Your Devices:\n");
            for (int i = 0; i < rm.get(position).getDev().size(); i++) {
                String str = rm.get(position).getDev().get(i).getStatus() ? "ON" : "OFF";
                textView1.append(rm.get(position).getDev().get(i).getName() + "   " + str + "\n");
            }
            int flag = 0;
            for (int j = 0; j < rm.get(position).getDev().size(); j++) {
                if (rm.get(position).getDev().get(j).getStatus() == true) {
                    flag = 1;
                    break;
                }

            }
            gridView.setBackground(context.getDrawable(R.drawable.homeback));
            if (flag == 1) {
                GradientDrawable dr = (GradientDrawable)gridView.getBackground().getCurrent().getCurrent();
                dr.setColor(Color.parseColor("#00ff00"));

            } else if (flag == 0) {
                GradientDrawable dr = (GradientDrawable)gridView.getBackground().getCurrent().getCurrent();
                dr.setColor(Color.parseColor("#00bfff"));
            }


        } else if(position==rm.size()) {
            textView = (TextView) gridView.findViewById(R.id.roomname);
            textView1 = (TextView) gridView.findViewById(R.id.devlist);
            img = (ImageView) gridView.findViewById(R.id.plus);
            textView.setVisibility(View.INVISIBLE);
            textView1.setVisibility(View.INVISIBLE);
            img.setVisibility(View.VISIBLE);

        }

        return gridView;
    }
    @Override
    public int getCount() {
        return rm.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }




}
