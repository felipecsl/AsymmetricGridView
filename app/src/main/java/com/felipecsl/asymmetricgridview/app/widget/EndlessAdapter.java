package com.felipecsl.asymmetricgridview.app.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.felipecsl.asymmetricgridview.app.R;

import java.util.ArrayList;
import java.util.List;

public abstract class EndlessAdapter<T>
        extends BaseAdapter
        implements DynamicAdapter<T> {

    private static final String TAG = "EndlessAdapter";

    protected List<T> items;
    protected Context context;
    protected boolean hasMorePages = true;

    public static final int ITEM_VIEW_TYPE_LOADING = 0;
    public static final int ITEM_VIEW_TYPE_FIRST_TYPE = 1;

    protected EndlessAdapter(final Context context, final List<T> items) {
        this.context = context;
        this.items = items;
    }

    protected EndlessAdapter(final Context context) {
        this(context, new ArrayList<T>());
    }

    public Context getContext() {
        return context;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public void setObjects(final List<T> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public void appendObjects(final List<T> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public boolean isEmpty() {
        // Cannot use BaseAdapter.isEmpty() because
        // we cannot rely on getCount() - it takes into account
        // the pending view.
        return items.isEmpty();
    }

    @Override
    public void clearObjects() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (hasMorePages ? 1 : 0) + items.size();
    }

    @Override
    public void setHasMorePages(final boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
        notifyDataSetChanged();
    }

    @Override
    public T getItem(final int position) {
        return position >= items.size() ? null : items.get(position);
    }

    @Override
    public List<T> getItems() {
        return items;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == ITEM_VIEW_TYPE_LOADING) {
            return buildPendingView();
        }

        return getImplementedView(position, convertView, parent);
    }

    public abstract View getImplementedView(int position, View convertView, ViewGroup parent);

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && items.isEmpty())
            return ITEM_VIEW_TYPE_LOADING;
        if (position >= items.size() && !items.isEmpty())
            return ITEM_VIEW_TYPE_LOADING;

        return getImplementedViewType(position);
    }

    /**
     * Override this to implement multiples ViewTypes on child implementations.
     */
    protected int getImplementedViewType(int position) {
        return ITEM_VIEW_TYPE_FIRST_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 1 + getImplementedViewTypeCount();
    }

    /**
     * Override this to implement multiple ViewTypes on child implementations
     */
    protected int getImplementedViewTypeCount() {
        return 1;
    }

    protected View buildPendingView() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.layout_pending_view, null);
    }
}
