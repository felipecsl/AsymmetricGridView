package com.felipecsl.asymmetricgridview.library.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.felipecsl.asymmetricgridview.library.AsyncTaskCompat;
import com.felipecsl.asymmetricgridview.library.R;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsymmetricGridViewAdapter<T extends AsymmetricItem>
        extends BaseAdapter
        implements View.OnClickListener,
        View.OnLongClickListener,
        WrapperListAdapter {

    private static final String TAG = "AsymmetricGridViewAdapter";
    protected final AsymmetricGridView listView;
    protected final Context context;
    protected final ListAdapter wrappedAdapter;

    private Map<Integer, RowInfo<T>> itemsPerRow = new HashMap<>();
    private final ViewPool<IcsLinearLayout> linearLayoutPool;
    private final ViewPool<View> viewPool = new ViewPool<>();
    private ProcessRowsTask asyncTask;

    class GridDataSetObserver extends DataSetObserver {
        @Override public void onChanged() {
            recalculateItemsPerRow();
        }

        @Override public void onInvalidated() {
            recalculateItemsPerRow();
        }
    }

    public AsymmetricGridViewAdapter(Context context, AsymmetricGridView listView,
                                     ListAdapter adapter) {
        this.linearLayoutPool = new ViewPool<>(new LinearLayoutPoolObjectFactory(context));
        this.wrappedAdapter = adapter;
        this.context = context;
        this.listView = listView;

        wrappedAdapter.registerDataSetObserver(new GridDataSetObserver());
    }

    protected int getRowHeight(final AsymmetricItem item) {
        return getRowHeight(item.getRowSpan());
    }

    @SuppressWarnings("unchecked")
    public T getItem(int position) {
        return (T) wrappedAdapter.getItem(position);
    }

    public long getItemId(int position) {
        return wrappedAdapter.getItemId(position);
    }

    protected int getRowHeight(int rowSpan) {
        final int rowHeight = listView.getColumnWidth() * rowSpan;
        // when the item spans multiple rows, we need to account for the vertical padding
        // and add that to the total final height
        return rowHeight + ((rowSpan - 1) * listView.getDividerHeight());
    }

    protected int getRowWidth(final AsymmetricItem item) {
        return getRowWidth(item.getColumnSpan());
    }

    protected int getRowWidth(int columnSpan) {
        final int rowWidth = listView.getColumnWidth() * columnSpan;
        // when the item spans multiple columns, we need to account for the horizontal padding
        // and add that to the total final width
        return Math.min(rowWidth + ((columnSpan - 1) * listView.getRequestedHorizontalSpacing()), Utils.getScreenWidth(context));
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        if (listView.isDebugging())
            Log.d(TAG, "getView(" + String.valueOf(position) + ")");

        LinearLayout layout = findOrInitializeLayout(convertView);

        final RowInfo<T> rowInfo = itemsPerRow.get(position);
        final List<RowItem<T>> rowItems = new ArrayList<>();
        rowItems.addAll(rowInfo.getItems());

        // Index to control the current position
        // of the current column in this row
        int columnIndex = 0;

        // Index to control the current position
        // in the array of all the items available for this row
        int currentIndex = 0;

        int spaceLeftInColumn = rowInfo.getRowHeight();

        while (!rowItems.isEmpty() && columnIndex < listView.getNumColumns()) {
            final RowItem<T> currentItem = rowItems.get(currentIndex);

            if (spaceLeftInColumn == 0) {
                // No more space in this column. Move to next one
                columnIndex++;
                currentIndex = 0;
                spaceLeftInColumn = rowInfo.getRowHeight();
                continue;
            }

            // Is there enough space in this column to accommodate currentItem?
            if (spaceLeftInColumn >= currentItem.getItem().getRowSpan()) {
                rowItems.remove(currentItem);

                final LinearLayout childLayout = findOrInitializeChildLayout(layout, columnIndex);
                final View childConvertView = viewPool.get();
                final View v = wrappedAdapter.getView(currentItem.getIndex(), childConvertView, parent);
                v.setTag(currentItem);
                v.setOnClickListener(this);
                v.setOnLongClickListener(this);

                spaceLeftInColumn -= currentItem.getItem().getRowSpan();
                currentIndex = 0;

                v.setLayoutParams(new LinearLayout.LayoutParams(getRowWidth(currentItem.getItem()),
                        getRowHeight(currentItem.getItem())));

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

    /*public Parcelable saveState() {
        final Bundle bundle = new Bundle();
        bundle.putInt("totalItems", wrappedAdapter.getCount());

        for (int i = 0; i < wrappedAdapter.getCount(); i++)
            bundle.putParcelable("item_" + i, (Parcelable) wrappedAdapter.getItem(i));

        return bundle;
    }

    @SuppressWarnings("unchecked")
    public void restoreState(final Parcelable state) {
        final Bundle bundle = (Bundle) state;

        if (bundle != null) {
            bundle.setClassLoader(getClass().getClassLoader());

            final int totalItems = bundle.getInt("totalItems");
            final List<T> tmpItems = new ArrayList<>();

            for (int i = 0; i < totalItems; i++)
                tmpItems.add((T) bundle.getParcelable("item_" + i));

            // will trigger recalculateItemsPerRow()
            setItems(tmpItems);
        }
    }*/

    @SuppressWarnings("MagicConstant")
    private IcsLinearLayout findOrInitializeLayout(View convertView) {
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
    private IcsLinearLayout findOrInitializeChildLayout(LinearLayout parentLayout, int childIndex) {
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

    /*@SuppressWarnings("unchecked")
    public void appendItems(List<T> newItems) {
        items.addAll(newItems);

        RowInfo<T> rowInfo = null;
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

                final RowInfo<T> stuffThatFit = calculateItemsForRow(newItems);
                final List<T> itemsThatFit = stuffThatFit.getItems();

                if (!itemsThatFit.isEmpty()) {
                    for (T anItemsThatFit : itemsThatFit) newItems.remove(anItemsThatFit);

                    itemsPerRow.put(lastRow, stuffThatFit);
                    notifyDataSetChanged();
                }
            }
        }

        asyncTask = new ProcessRowsTask();
        asyncTask.executeSerially(newItems);
    }*/

    @Override
    @SuppressWarnings("unchecked")
    public void onClick(final View v) {
        RowItem<T> rowItem = (RowItem<T>) v.getTag();
        listView.fireOnItemClick(rowItem.getIndex(), v);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onLongClick(View v) {
        RowItem<T> rowItem = (RowItem<T>) v.getTag();
        return listView.fireOnItemLongClick(rowItem.getIndex(), v);
    }

    @Override
    public int getCount() {
        // Returns the row count for ListView display purposes
        return getRowCount();
    }

    public int getRowCount() {
        return itemsPerRow.size();
    }

    @SuppressWarnings("unchecked")
    public void recalculateItemsPerRow() {
        if (asyncTask != null)
            asyncTask.cancel(true);

        linearLayoutPool.clear();
        viewPool.clear();
        itemsPerRow.clear();

        asyncTask = new ProcessRowsTask();
        asyncTask.executeSerially();
    }

    private RowInfo<T> calculateItemsForRow(List<RowItem<T>> items) {
        return calculateItemsForRow(items, listView.getNumColumns());
    }

    private RowInfo<T> calculateItemsForRow(List<RowItem<T>> items, float initialSpaceLeft) {
        final List<RowItem<T>> itemsThatFit = new ArrayList<>();
        int currentItem = 0;
        int rowHeight = 1;
        float areaLeft = initialSpaceLeft;

        while (areaLeft > 0 && currentItem < items.size()) {
            final RowItem<T> item = items.get(currentItem++);
            float itemArea = item.getItem().getRowSpan() * item.getItem().getColumnSpan();

            if (listView.isDebugging())
                Log.d(TAG, String.format("item %s in row with height %s consumes %s area", item, rowHeight, itemArea));

            if (rowHeight < item.getItem().getRowSpan()) {
                // restart with double height
                itemsThatFit.clear();
                rowHeight = item.getItem().getRowSpan();
                currentItem = 0;
                areaLeft = initialSpaceLeft * item.getItem().getRowSpan();
            } else if (areaLeft >= itemArea) {
                areaLeft -= itemArea;
                itemsThatFit.add(item);
            } else if (!listView.isAllowReordering()) {
                break;
            }
        }

        return new RowInfo<>(rowHeight, itemsThatFit, areaLeft);
    }

    @Override public ListAdapter getWrappedAdapter() {
        return wrappedAdapter;
    }

    class ProcessRowsTask extends AsyncTaskCompat<Void, Void, List<RowInfo<T>>> {
        @Override @SuppressWarnings("unchecked")
        protected final List<RowInfo<T>> doInBackground(Void... params) {
            // We need a map in order to associate the item position in the wrapped adapter.
            List<RowItem<T>> itemsToAdd = new ArrayList<>();
            for (int i = 0; i < wrappedAdapter.getCount(); i++)
                itemsToAdd.add(new RowItem(i, (T) wrappedAdapter.getItem(i)));

            return calculateItemsPerRow(itemsToAdd);
        }

        @Override protected void onPostExecute(List<RowInfo<T>> rows) {
            for (RowInfo<T> row : rows)
                itemsPerRow.put(getRowCount(), row);

            if (listView.isDebugging()) {
                for (Map.Entry<Integer, RowInfo<T>> e : itemsPerRow.entrySet())
                    Log.d(TAG, "row: " + e.getKey() + ", items: " + e.getValue().getItems().size());
            }

            notifyDataSetChanged();
        }

        private List<RowInfo<T>> calculateItemsPerRow(List<RowItem<T>> itemsToAdd) {
            List<RowInfo<T>> rows = new ArrayList<>();

            while (!itemsToAdd.isEmpty()) {
                RowInfo<T> stuffThatFit = calculateItemsForRow(itemsToAdd);
                List<RowItem<T>> itemsThatFit = stuffThatFit.getItems();

                if (itemsThatFit.isEmpty()) {
                    // we can't fit a single item inside a row.
                    // bail out.
                    break;
                }

                for (RowItem entry : itemsThatFit)
                    itemsToAdd.remove(entry);

                rows.add(stuffThatFit);
            }

            return rows;
        }
    }

}
