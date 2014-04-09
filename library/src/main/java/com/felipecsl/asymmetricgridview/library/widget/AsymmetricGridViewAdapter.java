package com.felipecsl.asymmetricgridview.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.felipecsl.asymmetricgridview.library.R;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AsymmetricGridViewAdapter extends ArrayAdapter<AsymmetricItem> {

    private static class RowInfo {

        private final List<AsymmetricItem> items;
        private final int rowHeight;

        public RowInfo(final int rowHeight, final List<AsymmetricItem> items) {
            this.rowHeight = rowHeight;
            this.items = items;
        }

        public List<AsymmetricItem> getItems() {
            return items;
        }

        public int getRowHeight() {
            return rowHeight;
        }
    }

    private static final String TAG = "AsymmetricGridViewAdapter";
    private static final boolean DEBUG = true;
    protected final AsymmetricGridView listView;
    private final Context context;
    private final List<AsymmetricItem> items;
    private final Map<Integer, RowInfo> itemsPerRow = new HashMap<>();

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
        LinearLayout layout = findOrInitializeLayout(convertView);

        final RowInfo rowInfo = itemsPerRow.get(position);
        final List<AsymmetricItem> rowItems = new ArrayList<>();
        rowItems.addAll(rowInfo.getItems());

        // Index to control the current position
        // of the current column in this row
        int columnIndex = 0;

        // Index to control the current position
        // in the array of all the items available for this row
        int currentIndex = 0;

        // Index to control the current position
        // within the current column
        int currentColumnIndex = 0;

        int spaceLeftInColumn = rowInfo.getRowHeight();

        while (!rowItems.isEmpty() && columnIndex < listView.getNumColumns()) {
            final AsymmetricItem currentItem = rowItems.get(currentIndex);

            if (spaceLeftInColumn == 0) {
                // No more space in this column. Move to next one
                columnIndex++;
                currentIndex = 0;
                currentColumnIndex = 0;
                spaceLeftInColumn = rowInfo.getRowHeight();
                continue;
            }

            // Is there enough space in this column to accommodate currentItem?
            if (spaceLeftInColumn >= currentItem.getRowSpan()) {
                rowItems.remove(currentItem);

                final LinearLayout childLayout = findOrInitializeChildLayout(layout, columnIndex);
                final View childConvertView = childLayout.getChildAt(currentColumnIndex);
                final View v = getActualView(items.indexOf(currentItem), childConvertView, parent);

                currentColumnIndex += currentItem.getRowSpan();
                spaceLeftInColumn -= currentItem.getRowSpan();
                currentIndex = 0;

                if (childConvertView == null)
                    childLayout.addView(v);
            } else if (currentIndex < rowItems.size() - 1) {
                // Try again with next item
                currentIndex++;
            } else {
                break;
            }
        }

        return layout;
    }

    private LinearLayout findOrInitializeLayout(final View convertView) {
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

        return layout;
    }

    private LinearLayout findOrInitializeChildLayout(final LinearLayout parentLayout, final int childIndex) {
        LinearLayout childLayout = (LinearLayout) parentLayout.getChildAt(childIndex);

        if (childLayout == null) {
            childLayout = new LinearLayout(context);
            childLayout.setOrientation(LinearLayout.VERTICAL);

            if (DEBUG)
                childLayout.setBackgroundColor(Color.parseColor("#0000ff"));

            if (Build.VERSION.SDK_INT >= 11) {
                childLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                childLayout.setDividerDrawable(context.getResources().getDrawable(R.drawable.item_divider_vertical));
            }

            childLayout.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.WRAP_CONTENT,
                    AbsListView.LayoutParams.MATCH_PARENT));

            parentLayout.addView(childLayout);
        }

        return childLayout;
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

        while (!itemsCopy.isEmpty()) {
            final RowInfo itemsThatFit = calculateItemsForRow(itemsCopy);

            if (itemsThatFit.getItems().isEmpty()) {
                // we can't fit a single item inside a row.
                // bail out.
                break;
            }

            if (DEBUG) {
                for (int i = 0; i < itemsThatFit.getItems().size(); i++)
                    itemsCopy.remove(0);
            }

            itemsPerRow.put(currentRow, itemsThatFit);
            currentRow++;
        }

        for (Map.Entry<Integer, RowInfo> e : itemsPerRow.entrySet())
            Log.d(TAG, "row: " + e.getKey() + ", items: " + e.getValue().getItems().size());
    }

    private RowInfo calculateItemsForRow(final List<AsymmetricItem> items) {
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
            } else {
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

        return new RowInfo(rowHeight, itemsThatFit);
    }
}
