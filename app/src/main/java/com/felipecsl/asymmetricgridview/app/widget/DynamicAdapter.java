package com.felipecsl.asymmetricgridview.app.widget;

import android.os.Parcelable;
import android.view.View;
import android.widget.ListAdapter;

import java.util.List;

public interface DynamicAdapter<T> extends ListAdapter, SimpleDynamicAdapter<T> {

    @Override
    T getItem(final int position);

    List<T> getItems();

    void clearObjects();

    View.BaseSavedState onSaveInstanceState(final Parcelable state);

    void onRestoreInstanceState(final View.BaseSavedState state);

    void notifyDataSetChanged();

    void setHasMorePages(final boolean hasMorePages);
}
