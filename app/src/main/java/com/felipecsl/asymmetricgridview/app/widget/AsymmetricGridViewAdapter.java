package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.felipecsl.asymmetricgridview.app.R;
import com.felipecsl.asymmetricgridview.app.Utils;
import com.felipecsl.asymmetricgridview.app.model.AsymmetricItem;

public abstract class AsymmetricGridViewAdapter extends EndlessAdapter<AsymmetricItem> {

    protected final int defaultHorizontalSpacing;
    protected final AsymmetricGridView listView;

    protected AsymmetricGridViewAdapter(final Context context, final AsymmetricGridView listView) {
        super(context);

        this.listView = listView;
        defaultHorizontalSpacing = Utils.dpToPx(context, 5);
    }

    public boolean getHasMorePages() {
        return hasMorePages;
    }

    public int getDefaultHorizontalSpacing() {
        return defaultHorizontalSpacing;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final int actualPosition = position * listView.getNumColumns();

        LinearLayout layout;

        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setBackgroundColor(Color.parseColor("#00ff00"));

            if (Build.VERSION.SDK_INT >= 11) {
                layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                layout.setDividerDrawable(context.getResources().getDrawable(R.drawable.item_divider_horizontal));
            }

            layout.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT));
        } else
            layout = (LinearLayout) convertView;

        int currentChildIndex = 0;

        for (int i = 0; i < listView.getNumColumns(); i++) {
            final int adjustedPosition = actualPosition + i;
            final AsymmetricItem item = items.get(adjustedPosition);
            LinearLayout childLayout;

            if (i > 1 && items.get(adjustedPosition - 2).getColumnSpan() > 1)
                childLayout = (LinearLayout) layout.getChildAt(i - 1);
            else {
                currentChildIndex = 0;
                childLayout = (LinearLayout) layout.getChildAt(i);
            }

            if (childLayout == null) {
                childLayout = new LinearLayout(context);
                childLayout.setOrientation(LinearLayout.VERTICAL);
                if (Build.VERSION.SDK_INT >= 11) {
                    childLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    childLayout.setDividerDrawable(context.getResources().getDrawable(R.drawable.item_divider_vertical));
                }
                childLayout.setLayoutParams(new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.WRAP_CONTENT,
                        AbsListView.LayoutParams.MATCH_PARENT));
                childLayout.setBackgroundColor(Color.parseColor("#0000ff"));

                layout.addView(childLayout);
            }

            View childConvertView = childLayout.getChildAt(currentChildIndex++);

            final View v = getSuperView(adjustedPosition, childConvertView, parent);

            if (childConvertView == null) {
                childLayout.addView(v);
            }
        }

        return layout;
    }

    public View getSuperView(final int position, final View convertView, final ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return (int) Math.ceil((double) items.size() / (double) listView.getNumColumns()) + (hasMorePages ? 1 : 0);
    }
}
