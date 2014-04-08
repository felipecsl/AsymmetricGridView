package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

public class ListAdapter extends AsymmetricGridViewAdapter {

    public ListAdapter(final Context context, final AsymmetricGridView listView) {
        super(context, listView);
    }

    @Override
    public View getImplementedView(final int position, final View convertView, final ViewGroup parent) {
        TextView v;

        if (convertView == null) {
            v = new TextView(context);
            final AbsListView.MarginLayoutParams params = new AbsListView.MarginLayoutParams(
                    listView.getColumnWidth(),
                    (int) (listView.getColumnWidth() * 0.8));

            v.setGravity(Gravity.CENTER);
            v.setLayoutParams(params);
            v.setBackgroundColor(Color.parseColor("#cc0000"));
            v.setTextColor(Color.parseColor("#ffffff"));
        } else
            v = (TextView) convertView;

        v.setText(String.valueOf(position));

        return v;
    }

    @Override
    public View.BaseSavedState onSaveInstanceState(final Parcelable state) {
        return null;
    }

    @Override
    public void onRestoreInstanceState(final View.BaseSavedState state) {

    }
}
