package com.example.secondactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

public class DiaryAdapter extends ArrayAdapter<Diary> {
    private int resourceId;

    public DiaryAdapter(Context context, int textViewResourceId,
                        List<Diary> objects) {
        super(context, textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        Diary diary=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.diaryTitle=(TextView)view.findViewById(R.id.title_text);
            viewHolder.diaryContent=(TextView)view.findViewById(R.id.content_text);
            viewHolder.diaryDate=(TextView)view.findViewById(R.id.date_text);
            view.setTag(viewHolder);
        }
        else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.diaryTitle.setText(diary.getTitle());
        viewHolder.diaryContent.setText(diary.getContent());
        viewHolder.diaryDate.setText((CharSequence) diary.getDate());
        return view;
    }
    class ViewHolder{
        TextView diaryTitle;
        TextView diaryContent;
        TextView diaryDate;
    }
}
