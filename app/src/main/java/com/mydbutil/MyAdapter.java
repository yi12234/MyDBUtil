package com.mydbutil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/10/12.
 */
public class MyAdapter extends BaseAdapter {

    Context context;
    List<Student> list;

    public MyAdapter(Context context, List<Student> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=View.inflate(context,R.layout.item,null);
        TextView textView=view.findViewById(R.id.item);
        textView.setText(list.get(i).getName());
        return view;
    }
}
