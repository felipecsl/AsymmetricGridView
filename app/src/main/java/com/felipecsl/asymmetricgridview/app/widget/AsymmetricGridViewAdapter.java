package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.felipecsl.asymmetricgridview.app.R;
import com.felipecsl.asymmetricgridview.app.Utils;
import com.felipecsl.asymmetricgridview.app.model.Item;

public abstract class AsymmetricGridViewAdapter extends EndlessAdapter<Item> {

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
            if (Build.VERSION.SDK_INT >= 11) {
                layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                layout.setDividerDrawable(getContext().getResources().getDrawable(R.drawable.item_divider));
            }

            layout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            layout.setPadding(defaultHorizontalSpacing, 0, defaultHorizontalSpacing, 0);
        } else {
            layout = (LinearLayout) convertView;
        }

        for (int i = 0; i < listView.getNumColumns(); i++) {
            final int adjustedPosition = actualPosition + i;

            View childConvertView = layout.getChildAt(i);
            if (childConvertView != null && childConvertView.getId() == R.id.pendingView) {
                layout.removeView(childConvertView);
                childConvertView = null;
            }

            if (adjustedPosition > items.size() || (!hasMorePages && adjustedPosition >= items.size())) {
                for (int j = i; j < listView.getNumColumns(); j++) {
                    View v = layout.getChildAt(j);
                    if (v != null)
                        layout.removeView(v);
                }

                break;
            }

            final View v = getSuperView(adjustedPosition, childConvertView, parent);

            if (v.getId() != R.id.pendingView) {
                if (childConvertView == null) {
                    layout.addView(v);
                    layout.setGravity(GravityCompat.START);
                }
            } else {
                layout.removeAllViews();
                layout.addView(v);
                layout.setGravity(Gravity.CENTER);
                break;
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
