package com.felipecsl.asymmetricgridview.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.felipecsl.asymmetricgridview.library.AsyncTaskCompat;
import com.felipecsl.asymmetricgridview.library.R;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public abstract class AsymmetricGridViewAdapter<T
        extends AsymmetricItem> extends ArrayAdapter<T>
        implements View.OnClickListener, View.OnLongClickListener {

    private class RowInfo {

        private final List<T> items;
        private final int rowHeight;
        private final float spaceLeft;

        public RowInfo(final int rowHeight,
                       final List<T> items,
                       final float spaceLeft) {
            this.rowHeight = rowHeight;
            this.items = items;
            this.spaceLeft = spaceLeft;
        }

        public List<T> getItems() {
            return items;
        }

        public int getRowHeight() {
            return rowHeight;
        }

        public float getSpaceLeft() {
            return spaceLeft;
        }
    }

    private static final String TAG = "AsymmetricGridViewAdapter";
    protected final AsymmetricGridView listView;
    protected final Context context;
    protected final List<T> items;

    private final Map<Integer, RowInfo> itemsPerRow = new HashMap<>();

    Pool<IcsLinearLayout> linearLayoutPool;
    Pool<View> viewPool;

    public AsymmetricGridViewAdapter(final Context context,
                                     final AsymmetricGridView listView,
                                     final List<T> items) {

        super(context, 0, items);

        this.items = items;
        this.context = context;
        this.listView = listView;

        linearLayoutPool = new Pool<>(linearLayoutPoolObjectFactory);
        viewPool = new Pool<>();
    }

    public abstract View getActualView(final int position, final View convertView, final ViewGroup parent);

    protected int getRowHeight(final AsymmetricItem item) {
        return getRowHeight(item.getRowSpan());
    }

    protected int getRowHeight(int rowSpan) {
        final int rowHeight = listView.getColumnWidth() * rowSpan;
        // when the item spans multiple rows, we need to account for the vertical padding
        // and add that to the total final height
        return rowHeight + ((rowSpan - 1) * listView.getRequestedVerticalSpacing());
    }

    protected int getRowWidth(final AsymmetricItem item) {
        return getRowWidth(item.getColumnSpan());
    }

    protected int getRowWidth(int columnSpan) {
        final int rowWidth = listView.getColumnWidth() * columnSpan;
        // when the item spans multiple columns, we need to account for the horizontal padding
        // and add that to the total final width
        return Math.min(rowWidth + ((columnSpan - 1) * listView.getRequestedHorizontalSpacing()), Utils.getScreenWidth(getContext()));
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LinearLayout layout = findOrInitializeLayout(convertView);

        final RowInfo rowInfo = itemsPerRow.get(position);
        final List<T> rowItems = new ArrayList<>();
        rowItems.addAll(rowInfo.getItems());

        // Index to control the current position
        // of the current column in this row
        int columnIndex = 0;

        // Index to control the current position
        // in the array of all the items available for this row
        int currentIndex = 0;

        int spaceLeftInColumn = rowInfo.getRowHeight();

        while (!rowItems.isEmpty() && columnIndex < listView.getNumColumns()) {
            final T currentItem = rowItems.get(currentIndex);

            if (spaceLeftInColumn == 0) {
                // No more space in this column. Move to next one
                columnIndex++;
                currentIndex = 0;
                spaceLeftInColumn = rowInfo.getRowHeight();
                continue;
            }

            // Is there enough space in this column to accommodate currentItem?
            if (spaceLeftInColumn >= currentItem.getRowSpan()) {
                rowItems.remove(currentItem);

                int index = items.indexOf(currentItem);
                final LinearLayout childLayout = findOrInitializeChildLayout(layout, columnIndex);
                final View childConvertView = viewPool.get();
                final View v = getActualView(index, childConvertView, parent);
                v.setTag(currentItem);
                v.setOnClickListener(this);
                v.setOnLongClickListener(this);

                spaceLeftInColumn -= currentItem.getRowSpan();
                currentIndex = 0;

                v.setLayoutParams(new LinearLayout.LayoutParams(getRowWidth(currentItem),
                        getRowHeight(currentItem)));

                childLayout.addView(v);
            } else if (currentIndex < rowItems.size() - 1) {
                // Try again with next item
                currentIndex++;
            } else {
                break;
            }
        }

        if (listView.isDebugging() && position % 20 == 0) {
            Log.d(TAG, linearLayoutPool.getStats("LinearLayout"));
            Log.d(TAG, viewPool.getStats("Views"));
        }

        return layout;
    }

    @SuppressWarnings("MagicConstant")
    private IcsLinearLayout findOrInitializeLayout(final View convertView) {
        IcsLinearLayout layout;

        if (convertView == null || !(convertView instanceof IcsLinearLayout)) {
            layout = new IcsLinearLayout(context, null);
            if (listView.isDebugging())
                layout.setBackgroundColor(Color.parseColor("#83F27B"));

            layout.setShowDividers(IcsLinearLayout.SHOW_DIVIDER_MIDDLE);
            layout.setDividerDrawable(context.getResources().getDrawable(R.drawable.item_divider_horizontal));

            layout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT));
        } else
            layout = (IcsLinearLayout) convertView;

        // Clear all layout children before starting
        for (int j = 0; j < layout.getChildCount(); j++) {
            IcsLinearLayout tempChild = (IcsLinearLayout) layout.getChildAt(j);
            linearLayoutPool.put(tempChild);
            for (int k = 0; k < tempChild.getChildCount(); k++)
                viewPool.put(tempChild.getChildAt(k));
            tempChild.removeAllViews();
        }
        layout.removeAllViews();

        return layout;
    }

    @SuppressWarnings("MagicConstant")
    private IcsLinearLayout findOrInitializeChildLayout(final LinearLayout parentLayout, final int childIndex) {
        IcsLinearLayout childLayout = (IcsLinearLayout) parentLayout.getChildAt(childIndex);

        if (childLayout == null) {
            childLayout = linearLayoutPool.get();
            childLayout.setOrientation(LinearLayout.VERTICAL);

            if (listView.isDebugging())
                childLayout.setBackgroundColor(Color.parseColor("#837BF2"));

            childLayout.setShowDividers(IcsLinearLayout.SHOW_DIVIDER_MIDDLE);
            childLayout.setDividerDrawable(context.getResources().getDrawable(R.drawable.item_divider_vertical));

            childLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT,
                    AbsListView.LayoutParams.MATCH_PARENT));
            parentLayout.addView(childLayout);
        }

        return childLayout;
    }

    public void setItems(List<T> newItems) {
        linearLayoutPool.clear();
        viewPool.clear();
        items.clear();
        items.addAll(newItems);
        recalculateItemsPerRow();
        notifyDataSetChanged();
    }

    public void appendItems(List<T> newItems) {
        items.addAll(newItems);

        RowInfo rowInfo = null;
        final int lastRow = getRowCount() - 1;
        if (lastRow >= 0)
            rowInfo = itemsPerRow.get(lastRow);

        if (rowInfo != null) {
            final float spaceLeftInLastRow = rowInfo.getSpaceLeft();

            if (listView.isDebugging())
                Log.d(TAG, "Space left in last row: " + spaceLeftInLastRow);

            // Try to add new items into the last row, if there is any space left
            if (spaceLeftInLastRow > 0) {

                for (final T i : rowInfo.getItems())
                    newItems.add(0, i);

                final RowInfo stuffThatFit = calculateItemsForRow(newItems);
                final List<T> itemsThatFit = stuffThatFit.getItems();

                if (!itemsThatFit.isEmpty()) {
                    for (int i = 0; i < itemsThatFit.size(); i++)
                        newItems.remove(itemsThatFit.get(i));

                    itemsPerRow.put(lastRow, stuffThatFit);
                    notifyDataSetChanged();
                }
            }
        }

        new ProcessRowsTask().executeSerially(newItems);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onClick(final View v) {
        final T item = (T) v.getTag();
        listView.fireOnItemClick(items.indexOf(item), v);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onLongClick(View v) {
        final T item = (T) v.getTag();
        return listView.fireOnItemLongClick(items.indexOf(item), v);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // Returns the row count for ListView display purposes
        return getRowCount();
    }

    public int getRowCount() {
        return itemsPerRow.size();
    }

    public void recalculateItemsPerRow() {
        linearLayoutPool.clear();
        viewPool.clear();

        itemsPerRow.clear();
        final List<T> itemsToAdd = new ArrayList<>();
        itemsToAdd.addAll(items);

        new ProcessRowsTask().executeSerially(itemsToAdd);
    }

    private RowInfo calculateItemsForRow(final List<T> items) {
        return calculateItemsForRow(items, listView.getNumColumns());
    }

    private RowInfo calculateItemsForRow(final List<T> items, final float initialSpaceLeft) {
        final List<T> itemsThatFit = new ArrayList<>();
        int currentItem = 0;
        int rowHeight = 1;
        float spaceLeft = initialSpaceLeft;

        while (spaceLeft > 0 && currentItem < items.size()) {
            final T item = items.get(currentItem++);

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
                    spaceLeft = initialSpaceLeft;
                } else if (spaceLeft >= spaceConsumption) {
                    spaceLeft -= spaceConsumption;
                    itemsThatFit.add(item);
                } else if (!listView.isAllowReordering()) {
                    break;
                }
            }
        }

        return new RowInfo(rowHeight, itemsThatFit, spaceLeft);
    }

    class ProcessRowsTask extends AsyncTaskCompat<List<T>, Void, List<RowInfo>> {

        @Override
        @SafeVarargs
        protected final List<RowInfo> doInBackground(final List<T>... params) {
            return calculateItemsPerRow(0, params[0]);
        }

        @Override
        protected void onPostExecute(List<RowInfo> rows) {
            for (RowInfo row : rows)
                itemsPerRow.put(getRowCount(), row);

            notifyDataSetChanged();
        }

        private List<RowInfo> calculateItemsPerRow(int currentRow, final List<T> itemsToAdd) {
            List<RowInfo> rows = new ArrayList<>();

            while (!itemsToAdd.isEmpty()) {
                final RowInfo stuffThatFit = calculateItemsForRow(itemsToAdd);

                final List<T> itemsThatFit = stuffThatFit.getItems();
                if (itemsThatFit.isEmpty()) {
                    // we can't fit a single item inside a row.
                    // bail out.
                    break;
                }

                for (T anItemsThatFit : itemsThatFit)
                    itemsToAdd.remove(anItemsThatFit);

                rows.add(stuffThatFit);
                currentRow++;
            }

            if (listView.isDebugging()) {
                for (Map.Entry<Integer, RowInfo> e : itemsPerRow.entrySet())
                    Log.d(TAG, "row: " + e.getKey() + ", items: " + e.getValue().getItems().size());
            }

            return rows;
        }
    }

    PoolObjectFactory<IcsLinearLayout> linearLayoutPoolObjectFactory = new PoolObjectFactory<IcsLinearLayout>() {
        @Override
        public IcsLinearLayout createObject() {
            return new IcsLinearLayout(context, null);
        }
    };

    static class Pool<T> {
        Stack<T> stack = new Stack<>();
        PoolObjectFactory<T> factory = null;
        PoolStats stats;

        Pool() {
            stats = new PoolStats();
        }

        Pool(PoolObjectFactory<T> factory) {
            this.factory = factory;
        }

        T get() {
            if (stack.size() > 0) {
                stats.hits++;
                stats.size--;
                return stack.pop();
            }

            stats.misses++;

            T object = factory != null ? factory.createObject() : null;

            if (object != null) {
                stats.created++;
            }

            return object;
        }

        void put(T object) {
            stack.push(object);
            stats.size++;
        }

        void clear() {
            stats = new PoolStats();
            stack.clear();
        }

        String getStats(String name) {
            return stats.getStats(name);
        }
    }

    static class PoolStats {
        int size = 0;
        int hits = 0;
        int misses = 0;
        int created = 0;

        String getStats(String name) {
            return String.format("%s: size %d, hits %d, misses %d, created %d", name, size, hits,
                    misses, created);
        }
    }

    public interface PoolObjectFactory<T> {
        public T createObject();
    }
}
