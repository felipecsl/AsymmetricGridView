package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felipecsl.asymmetricgridview.app.Utils;
import com.felipecsl.asymmetricgridview.app.model.AsymmetricItem;

public class ListAdapter extends AsymmetricGridViewAdapter {

    public ListAdapter(final Context context, final AsymmetricGridView listView) {
        super(context, listView);
    }

    private int getRowHeight(final AsymmetricItem item) {
        final int rowHeight = listView.getColumnWidth() * item.getRowSpan();
        // when the item spans multiple rows, we need to account for the vertical padding
        // and add that to the total final height
        return rowHeight + ((item.getRowSpan() - 1) * listView.getRequestedVerticalSpacing());
    }

    private int getRowWidth(final AsymmetricItem item) {
        final int rowWidth = listView.getColumnWidth() * item.getColumnSpan();
        // when the item spans multiple columns, we need to account for the horizontal padding
        // and add that to the total final width
        return Math.min(rowWidth + ((item.getColumnSpan() - 1) * listView.getRequestedHorizontalSpacing()), Utils.getScreenWidth(getContext()));
    }

    @Override
    public View getImplementedView(final int position, final View convertView, final ViewGroup parent) {
        TextView v;

        AsymmetricItem item = items.get(position);

        if (convertView == null) {
            v = new TextView(context);
            v.setGravity(Gravity.CENTER);
            v.setBackgroundColor(Color.parseColor("#cc0000"));
            v.setTextColor(Color.parseColor("#ffffff"));
        } else
            v = (TextView) convertView;

        v.setLayoutParams(new LinearLayout.LayoutParams(
                getRowWidth(item),
                getRowHeight(item)));

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
