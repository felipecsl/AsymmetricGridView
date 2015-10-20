package com.felipecsl.asymmetricgridview;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class AdapterImpl implements View.OnClickListener, View.OnLongClickListener {
  private static final String TAG = "AdapterImpl";
  private final Map<Integer, RowInfo> itemsPerRow = new HashMap<>();
  private final ObjectPool<LinearLayout> linearLayoutPool;
  private final Map<Integer, ObjectPool<AsymmetricViewHolder<?>>> viewHoldersMap = new ArrayMap<>();
  private final Context context;
  private final AGVBaseAdapter<?> agvAdapter;
  private final AsymmetricView listView;
  private final boolean debugEnabled;
  private ProcessRowsTask asyncTask;

  AdapterImpl(Context context, AGVBaseAdapter<?> agvAdapter, AsymmetricView listView) {
    this.context = context;
    this.agvAdapter = agvAdapter;
    this.listView = listView;
    this.debugEnabled = listView.isDebugging();
    this.linearLayoutPool = new ObjectPool<>(new LinearLayoutPoolObjectFactory(context));
  }

  private RowInfo calculateItemsForRow(List<RowItem> items) {
    return calculateItemsForRow(items, listView.getNumColumns());
  }

  private RowInfo calculateItemsForRow(List<RowItem> items, float initialSpaceLeft) {
    final List<RowItem> itemsThatFit = new ArrayList<>();
    int currentItem = 0;
    int rowHeight = 1;
    float areaLeft = initialSpaceLeft;

    while (areaLeft > 0 && currentItem < items.size()) {
      final RowItem item = items.get(currentItem++);
      float itemArea = item.getItem().getRowSpan() * item.getItem().getColumnSpan();

      if (debugEnabled) {
        Log.d(TAG, String.format("item %s in row with height %s consumes %s area", item,
            rowHeight, itemArea));
      }

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

    return new RowInfo(rowHeight, itemsThatFit, areaLeft);
  }

  int getRowCount() {
    return itemsPerRow.size();
  }

  void recalculateItemsPerRow() {
    if (asyncTask != null) {
      asyncTask.cancel(true);
    }

    linearLayoutPool.clear();
    itemsPerRow.clear();

    asyncTask = new ProcessRowsTask();
    asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
  }

  @Override public void onClick(@NonNull View v) {
    // noinspection unchecked
    ViewState rowItem = (ViewState) v.getTag();
    listView.fireOnItemClick(rowItem.rowItem.getIndex(), v);
  }

  @Override public boolean onLongClick(@NonNull View v) {
    // noinspection unchecked
    ViewState rowItem = (ViewState) v.getTag();
    return listView.fireOnItemLongClick(rowItem.rowItem.getIndex(), v);
  }

  void onBindViewHolder(ViewHolder holder, int position, ViewGroup parent) {
    if (debugEnabled) {
      Log.d(TAG, "onBindViewHolder(" + String.valueOf(position) + ")");
    }

    RowInfo rowInfo = itemsPerRow.get(position);
    if (rowInfo == null) {
      return;
    }

    List<RowItem> rowItems = new ArrayList<>(rowInfo.getItems());
    LinearLayout layout = initializeLayout(holder.itemView());
    // Index to control the current position of the current column in this row
    int columnIndex = 0;
    // Index to control the current position in the array of all the items available for this row
    int currentIndex = 0;
    int spaceLeftInColumn = rowInfo.getRowHeight();

    while (!rowItems.isEmpty() && columnIndex < listView.getNumColumns()) {
      RowItem currentItem = rowItems.get(currentIndex);

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

        int actualIndex = currentItem.getIndex();
        int viewType = agvAdapter.getItemViewType(actualIndex);
        ObjectPool<AsymmetricViewHolder<?>> pool = viewHoldersMap.get(viewType);
        if (pool == null) {
          pool = new ObjectPool<>();
          viewHoldersMap.put(viewType, pool);
        }
        AsymmetricViewHolder viewHolder = pool.get();
        if (viewHolder == null) {
          viewHolder = agvAdapter.onCreateAsymmetricViewHolder(actualIndex, parent, viewType);
        }
        agvAdapter.onBindAsymmetricViewHolder(viewHolder, parent, actualIndex);
        View view = viewHolder.itemView;
        view.setTag(new ViewState(viewType, currentItem, viewHolder));
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        spaceLeftInColumn -= currentItem.getItem().getRowSpan();
        currentIndex = 0;

        view.setLayoutParams(new LinearLayout.LayoutParams(getRowWidth(currentItem.getItem()),
            getRowHeight(currentItem.getItem())));

        LinearLayout childLayout = findOrInitializeChildLayout(layout, columnIndex);
        childLayout.addView(view);
      } else if (currentIndex < rowItems.size() - 1) {
        // Try again with next item
        currentIndex++;
      } else {
        break;
      }
    }

    if (debugEnabled && position % 20 == 0) {
      Log.d(TAG, linearLayoutPool.getStats("LinearLayout"));
      for (Map.Entry<Integer, ObjectPool<AsymmetricViewHolder<?>>> e : viewHoldersMap.entrySet()) {
        Log.d(TAG, e.getValue().getStats("ConvertViewMap, viewType=" + e.getKey()));
      }
    }
  }

  ViewHolder onCreateViewHolder() {
    if (debugEnabled) {
      Log.d(TAG, "onCreateViewHolder()");
    }

    LinearLayout layout = new LinearLayout(context, null);
    if (debugEnabled) {
      layout.setBackgroundColor(Color.parseColor("#83F27B"));
    }

    layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    layout.setDividerDrawable(
        ContextCompat.getDrawable(context, R.drawable.item_divider_horizontal));

    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
        AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
    layout.setLayoutParams(layoutParams);
    return new ViewHolder(layout);
  }

  int getRowHeight(AsymmetricItem item) {
    return getRowHeight(item.getRowSpan());
  }

  int getRowHeight(int rowSpan) {
    final int rowHeight = listView.getColumnWidth() * rowSpan;
    // when the item spans multiple rows, we need to account for the vertical padding
    // and add that to the total final height
    return rowHeight + ((rowSpan - 1) * listView.getDividerHeight());
  }

  int getRowWidth(AsymmetricItem item) {
    return getRowWidth(item.getColumnSpan());
  }

  protected int getRowWidth(int columnSpan) {
    final int rowWidth = listView.getColumnWidth() * columnSpan;
    // when the item spans multiple columns, we need to account for the horizontal padding
    // and add that to the total final width
    return Math.min(rowWidth + ((columnSpan - 1) * listView.getRequestedHorizontalSpacing()),
        Utils.getScreenWidth(context));
  }

  private LinearLayout initializeLayout(LinearLayout layout) {
    // Clear all layout children before starting
    int childCount = layout.getChildCount();
    for (int j = 0; j < childCount; j++) {
      LinearLayout tempChild = (LinearLayout) layout.getChildAt(j);
      linearLayoutPool.put(tempChild);
      int innerChildCount = tempChild.getChildCount();
      for (int k = 0; k < innerChildCount; k++) {
        View innerView = tempChild.getChildAt(k);
        ViewState viewState = (ViewState) innerView.getTag();
        ObjectPool<AsymmetricViewHolder<?>> pool = viewHoldersMap.get(viewState.viewType);
        pool.put(viewState.viewHolder);
      }
      tempChild.removeAllViews();
    }
    layout.removeAllViews();

    return layout;
  }

  private LinearLayout findOrInitializeChildLayout(LinearLayout parentLayout, int childIndex) {
    LinearLayout childLayout = (LinearLayout) parentLayout.getChildAt(childIndex);

    if (childLayout == null) {
      childLayout = linearLayoutPool.get();
      childLayout.setOrientation(LinearLayout.VERTICAL);

      if (debugEnabled) {
        childLayout.setBackgroundColor(Color.parseColor("#837BF2"));
      }

      childLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
      childLayout.setDividerDrawable(
          ContextCompat.getDrawable(context, R.drawable.item_divider_vertical));

      childLayout.setLayoutParams(new AbsListView.LayoutParams(
          AbsListView.LayoutParams.WRAP_CONTENT,
          AbsListView.LayoutParams.MATCH_PARENT));

      parentLayout.addView(childLayout);
    }

    return childLayout;
  }

  class ProcessRowsTask extends AsyncTask<Void, Void, List<RowInfo>> {
    @Override protected final List<RowInfo> doInBackground(Void... params) {
      // We need a map in order to associate the item position in the wrapped adapter.
      List<RowItem> itemsToAdd = new ArrayList<>();
      for (int i = 0; i < agvAdapter.getActualItemCount(); i++) {
        try {
          itemsToAdd.add(new RowItem(i, agvAdapter.getItem(i)));
        } catch (CursorIndexOutOfBoundsException e) {
          Log.w(TAG, e);
        }
      }

      return calculateItemsPerRow(itemsToAdd);
    }

    @Override protected void onPostExecute(List<RowInfo> rows) {
      for (RowInfo row : rows) {
        itemsPerRow.put(getRowCount(), row);
      }

      if (debugEnabled) {
        for (Map.Entry<Integer, RowInfo> e : itemsPerRow.entrySet()) {
          Log.d(TAG, "row: " + e.getKey() + ", items: " + e.getValue().getItems().size());
        }
      }

      agvAdapter.notifyDataSetChanged();
    }

    private List<RowInfo> calculateItemsPerRow(List<RowItem> itemsToAdd) {
      List<RowInfo> rows = new ArrayList<>();

      while (!itemsToAdd.isEmpty()) {
        RowInfo stuffThatFit = calculateItemsForRow(itemsToAdd);
        List<RowItem> itemsThatFit = stuffThatFit.getItems();

        if (itemsThatFit.isEmpty()) {
          // we can't fit a single item inside a row.
          // bail out.
          break;
        }

        for (RowItem entry : itemsThatFit) {
          itemsToAdd.remove(entry);
        }

        rows.add(stuffThatFit);
      }

      return rows;
    }
  }

  private static class ViewState {
    private final int viewType;
    private final RowItem rowItem;
    private final AsymmetricViewHolder<?> viewHolder;

    private ViewState(int viewType, RowItem rowItem, AsymmetricViewHolder<?> viewHolder) {
      this.viewType = viewType;
      this.rowItem = rowItem;
      this.viewHolder = viewHolder;
    }
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    ViewHolder(LinearLayout itemView) {
      super(itemView);
    }

    LinearLayout itemView() {
      return (LinearLayout) itemView;
    }
  }
}
