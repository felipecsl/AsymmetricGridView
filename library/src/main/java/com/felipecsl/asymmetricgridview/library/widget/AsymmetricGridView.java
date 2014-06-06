package com.felipecsl.asymmetricgridview.library.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.felipecsl.asymmetricgridview.library.AsymmetricGridViewAdapterContract;
import com.felipecsl.asymmetricgridview.library.Utils;

public class AsymmetricGridView extends ListView {

    private static final int DEFAULT_COLUMN_COUNT = 2;
    private static final String TAG = "MultiColumnListView";
    protected int numColumns = DEFAULT_COLUMN_COUNT;
    protected final Rect padding;
    protected int defaultPadding;
    protected int requestedHorizontalSpacing;
    protected int requestedVerticalSpacing;
    protected int requestedColumnWidth;
    protected int requestedColumnCount;
    protected boolean allowReordering;
    protected boolean debugging = false;
    protected AsymmetricGridViewAdapterContract gridAdapter;
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    public AsymmetricGridView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        defaultPadding = Utils.dpToPx(context, 5);
        requestedHorizontalSpacing = defaultPadding;
        requestedVerticalSpacing = defaultPadding;
        padding = new Rect(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        final ViewTreeObserver vto = getViewTreeObserver();
        if (vto != null)
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    determineColumns();
                    if (gridAdapter != null)
                        gridAdapter.notifyDataSetChanged();
                    return false;
                }
            });
    }

    @Override
    public void setOnItemClickListener(final OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    protected void fireOnItemClick(final int position, final View v) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(this, v, position, v.getId());
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }

    protected boolean fireOnItemLongClick(final int position, final View v) {
        return onItemLongClickListener != null && onItemLongClickListener.onItemLongClick(this, v, position, v.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setAdapter(final ListAdapter adapter) {
        if (!(adapter instanceof AsymmetricGridViewAdapterContract))
            throw new UnsupportedOperationException("Adapter must implement AsymmetricGridViewAdapterContract");

        gridAdapter = (AsymmetricGridViewAdapterContract) adapter;
        super.setAdapter(adapter);
        gridAdapter.recalculateItemsPerRow();
    }

    public void setRequestedColumnWidth(final int width) {
        requestedColumnWidth = width;
    }

    public void setRequestedColumnCount(int requestedColumnCount) {
        this.requestedColumnCount = requestedColumnCount;
    }

    public int getRequestedHorizontalSpacing() {
        return requestedHorizontalSpacing;
    }

    public int getRequestedVerticalSpacing() {
        return requestedVerticalSpacing;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        determineColumns();
    }

    public int determineColumns() {
        int numColumns;
        final int availableSpace = getAvailableSpace();

        if (requestedColumnWidth > 0) {
            numColumns = (availableSpace + requestedHorizontalSpacing) /
                    (requestedColumnWidth + requestedHorizontalSpacing);
        } else if (requestedColumnCount > 0) {
            numColumns = requestedColumnCount;
        } else {
            // Default to 2 columns
            numColumns = DEFAULT_COLUMN_COUNT;
        }

        if (numColumns <= 0)
            numColumns = 1;

        this.numColumns = numColumns;

        return numColumns;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.allowReordering = allowReordering;
        ss.debugging = debugging;
        ss.defaultPadding = defaultPadding;
        ss.numColumns = numColumns;
        ss.requestedColumnCount = requestedColumnCount;
        ss.requestedColumnWidth = requestedColumnWidth;
        ss.requestedHorizontalSpacing = requestedHorizontalSpacing;
        ss.requestedVerticalSpacing = requestedVerticalSpacing;

        if (gridAdapter != null) {
            ss.adapterState = gridAdapter.saveState();
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (gridAdapter != null)
            gridAdapter.restoreState(ss.adapterState);

        allowReordering = ss.allowReordering;
        debugging = ss.debugging;
        defaultPadding = ss.defaultPadding;
        numColumns = ss.numColumns;
        requestedColumnCount = ss.requestedColumnCount;
        requestedColumnWidth = ss.requestedColumnWidth;
        requestedHorizontalSpacing = ss.requestedHorizontalSpacing;
        requestedVerticalSpacing = ss.requestedVerticalSpacing;

        setSelectionFromTop(20, 0);
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getColumnWidth() {
        return (getAvailableSpace() - ((numColumns - 1) * requestedHorizontalSpacing)) / numColumns;
    }

    public int getAvailableSpace() {
        return getMeasuredWidth() - padding.left - padding.right;
    }

    public boolean isAllowReordering() {
        return allowReordering;
    }

    public void setAllowReordering(final boolean allowReordering) {
        this.allowReordering = allowReordering;
        if (gridAdapter != null) {
            gridAdapter.recalculateItemsPerRow();
            gridAdapter.notifyDataSetChanged();
        }
    }

    public boolean isDebugging() {
        return debugging;
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    public static class SavedState extends BaseSavedState {

        int numColumns;
        int requestedColumnWidth;
        int requestedColumnCount;
        int requestedVerticalSpacing;
        int requestedHorizontalSpacing;
        int defaultPadding;
        boolean debugging;
        boolean allowReordering;
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(final Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel in) {
            super(in);

            numColumns = in.readInt();
            requestedColumnWidth = in.readInt();
            requestedColumnCount = in.readInt();
            requestedVerticalSpacing = in.readInt();
            requestedHorizontalSpacing = in.readInt();
            defaultPadding = in.readInt();
            debugging = in.readByte() == 1;
            allowReordering = in.readByte() == 1;
            adapterState = in.readParcelable(loader);
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            super.writeToParcel(dest, flags);

            dest.writeInt(numColumns);
            dest.writeInt(requestedColumnWidth);
            dest.writeInt(requestedColumnCount);
            dest.writeInt(requestedVerticalSpacing);
            dest.writeInt(requestedHorizontalSpacing);
            dest.writeInt(defaultPadding);
            dest.writeByte((byte) (debugging ? 1 : 0));
            dest.writeByte((byte) (allowReordering ? 1 : 0));
            dest.writeParcelable(adapterState, flags);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(final Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(final int size) {
                return new SavedState[size];
            }
        };
    }
}
