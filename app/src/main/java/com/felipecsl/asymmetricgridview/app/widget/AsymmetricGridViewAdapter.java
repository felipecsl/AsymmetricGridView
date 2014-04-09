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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AsymmetricGridViewAdapter extends ArrayAdapter<AsymmetricItem> {

    private static final String TAG = "AsymmetricGridViewAdapter";
    private static final boolean DEBUG = true;
    protected final AsymmetricGridView listView;
    private final Context context;
    private final List<AsymmetricItem> items;
    private final Map<Integer, List<AsymmetricItem>> itemsPerRow = new HashMap<>();

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
        int rowHeight = 1;

        final List<AsymmetricItem> itemsForRow = itemsPerRow.get(position);

        for (int i = 0; i < itemsForRow.size(); i++) {

            LinearLayout childLayout;

            if (numColumns > 2 &&
                    i > 1 &&
                    itemsForRow.get(i - 2).getColumnSpan() > 1) {

//                if (DEBUG)
//                    Log.d(TAG, "Case 1 for item " + i);

                childLayout = (LinearLayout) layout.getChildAt(i - 1);
                rowHeight = 2;
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
                    itemsForRow.get(i - 1).getColumnSpan() > 1) {

//                if (DEBUG)
//                    Log.d(TAG, "Case 2 for item " + i);

                childLayout = (LinearLayout) layout.getChildAt(i - 2);
                rowHeight = 2;
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
                    i + 1 < totalItems &&
                    itemsForRow.get(i + 1).getColumnSpan() > 1) {

//                if (DEBUG)
//                    Log.d(TAG, "Case 3 for item " + i);

                childLayout = (LinearLayout) layout.getChildAt(i - 1);
                rowHeight = 2;
                // There is not enough space to fit the next item because the column span
                // overflows the column count. In this case, we push the current item
                // into the previous column
                //  _____________
                // | 1  |        |
                // |____|   3    |
                // |(2) |        |
                // |____|________|
            } /*else if (rowHeight == 2) {
                childLayout = (LinearLayout) layout.getChildAt(i - 1);
            }*/ else {
//                if (DEBUG)
//                    Log.d(TAG, "Case 4 for item " + i);

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

            final View v = getActualView(items.indexOf(itemsForRow.get(i)), childConvertView, parent);

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
        return itemsPerRow.size();
    }

    public void calculateItemsPerRow() {
        int currentRow = 0;
        final List<AsymmetricItem> itemsCopy = new ArrayList<>();
        itemsPerRow.clear();
        itemsCopy.addAll(items);

        while(!itemsCopy.isEmpty()) {
            final List<AsymmetricItem> itemsThatFit = calculateItemsForRow(itemsCopy);

            if (itemsThatFit.isEmpty()) {
                // we can't fit a single item inside a row.
                // bail out.
                break;
            }

            if (DEBUG) {
                for (int i = 0; i < itemsThatFit.size(); i++)
                    itemsCopy.remove(0);
            }

            itemsPerRow.put(currentRow, itemsThatFit);
            currentRow++;
        }

        for (Map.Entry<Integer, List<AsymmetricItem>> e : itemsPerRow.entrySet())
            Log.d(TAG, "row: " + e.getKey() + ", items: " + e.getValue().size());
    }

    private List<AsymmetricItem> calculateItemsForRow(final List<AsymmetricItem> items) {
        final List<AsymmetricItem> itemsThatFit = new ArrayList<>();
        final int numColumns = listView.getNumColumns();
        int currentItem = 0;
        int rowHeight = 1;
        float spaceLeft = numColumns;

        while (spaceLeft > 0 && currentItem < items.size()) {
            final AsymmetricItem item = items.get(currentItem++);

            if (item.getColumnSpan() == 1) {
                // 1x sized items
                float spaceConsumption = (float) (1.0 / rowHeight);

                if (spaceLeft >= spaceConsumption) {
                    spaceLeft -= spaceConsumption;
                    itemsThatFit.add(item);
                }
            }
            else {
                // 2x sizes items
                float spaceConsumption = 2;

                if (rowHeight == 1) {
                    // restart with double height
                    itemsThatFit.clear();
                    rowHeight = 2;
                    currentItem = 0;
                    spaceLeft = numColumns;
                } else if (spaceLeft >= spaceConsumption) {
                    spaceLeft -= spaceConsumption;
                    itemsThatFit.add(item);
                } else {
                    // no more space left in this row
                    break;
                }
            }
        }

        return itemsThatFit;
    }
}
