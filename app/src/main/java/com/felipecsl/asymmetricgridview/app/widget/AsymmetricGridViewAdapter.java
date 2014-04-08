package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.felipecsl.asymmetricgridview.app.R;
import com.felipecsl.asymmetricgridview.app.Utils;
import com.felipecsl.asymmetricgridview.app.model.AsymmetricItem;

import java.util.List;

public abstract class AsymmetricGridViewAdapter extends ArrayAdapter<AsymmetricItem> {

    private static final String TAG = "AsymmetricGridViewAdapter";
    private static final boolean DEBUG = false;
    protected final AsymmetricGridView listView;
    private final Context context;
    private final List<AsymmetricItem> items;

    protected AsymmetricGridViewAdapter(final Context context,
                                        final AsymmetricGridView listView,
                                        final List<AsymmetricItem> items) {

        super(context, 0, items);

        this.items = items;
        this.context = context;
        this.listView = listView;
    }

    public abstract View getActualView(final int position, final View convertView, final ViewGroup parent);

    protected int getRowHeight(final AsymmetricItem item) {
        final int rowHeight = listView.getColumnWidth() * item.getRowSpan();
        // when the item spans multiple rows, we need to account for the vertical padding
        // and add that to the total final height
        return rowHeight + ((item.getRowSpan() - 1) * listView.getRequestedVerticalSpacing());
    }

    protected int getRowWidth(final AsymmetricItem item) {
        final int rowWidth = listView.getColumnWidth() * item.getColumnSpan();
        // when the item spans multiple columns, we need to account for the horizontal padding
        // and add that to the total final width
        return Math.min(rowWidth + ((item.getColumnSpan() - 1) * listView.getRequestedHorizontalSpacing()), Utils.getScreenWidth(getContext()));
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final int numColumns = listView.getNumColumns();
        final int totalItems = getActualCount();
        final int actualPosition = position * numColumns;

        LinearLayout layout;

        if (convertView == null) {
            layout = new LinearLayout(context);
            if (DEBUG)
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

        // Clear all layout children before starting
        for (int j = 0; j < layout.getChildCount(); j++) {
            LinearLayout tempChild = (LinearLayout) layout.getChildAt(j);
            tempChild.removeAllViews();
        }
        layout.removeAllViews();

        int currentChildIndex = 0;

        for (int i = 0; i < numColumns; i++) {
            final int adjustedPosition = actualPosition + i;

            if (adjustedPosition > totalItems - 1)
                break;

            LinearLayout childLayout;

            if (numColumns > 2 &&
                    i > 1 &&
                    getItem(adjustedPosition - 2).getColumnSpan() > 1) {

                if (DEBUG)
                    Log.d(TAG, "Case 1 for item " + adjustedPosition);

                childLayout = (LinearLayout) layout.getChildAt(i - 1);
                // We're on the third column and the current item should go below
                // the previous one, so we actually grab the layout with index == i - 1
                // because in this case we have one less column.
                //  _____________
                // |        | 2  |
                // |    1   |____|
                // |        |(3) |
                // |________|____|
            } else if (numColumns > 2 &&
                    i > 1 &&
                    i == numColumns - 1 &&
                    numColumns % 2 == 1 &&
                    getItem(adjustedPosition - 1).getColumnSpan() > 1) {

                if (DEBUG)
                    Log.d(TAG, "Case 2 for item " + adjustedPosition);

                childLayout = (LinearLayout) layout.getChildAt(i - 2);
                // We're on the first column and the current item should go below
                // the previous one, so we actually grab the layout with index == 0
                // because in this case we have one less column.
                //  _____________
                // | 1  |        |
                // |____|   2    |
                // |(3) |        |
                // |____|________|
            } else if (numColumns > 2 &&
                    i < numColumns - 1 &&
                    adjustedPosition + 1 < totalItems &&
                    getItem(adjustedPosition + 1).getColumnSpan() > 1) {

                if (DEBUG)
                    Log.d(TAG, "Case 3 for item " + adjustedPosition);

                childLayout = (LinearLayout) layout.getChildAt(i - 1);
                // There is not enough space to fit the next item because the column span
                // overflows the column count. In this case, we push the current item
                // into the previous column
                //  _____________
                // | 1  |        |
                // |____|   3    |
                // |(2) |        |
                // |____|________|
            } else {
                if (DEBUG)
                    Log.d(TAG, "Case 4 for item " + adjustedPosition);

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

                if (DEBUG)
                    childLayout.setBackgroundColor(Color.parseColor("#0000ff"));

                layout.addView(childLayout);
            }

            View childConvertView = childLayout.getChildAt(currentChildIndex++);

            final View v = getActualView(adjustedPosition, childConvertView, parent);

            if (childConvertView == null)
                childLayout.addView(v);
        }

        return layout;
    }

    public int getActualCount() {
        // This guy returns the actual item count that we have
        return items.size();
    }

    @Override
    public int getCount() {
        // Returns the row count for ListView display purposes
        return (int) Math.ceil((double) getActualCount() / (double) listView.getNumColumns());
    }
}
