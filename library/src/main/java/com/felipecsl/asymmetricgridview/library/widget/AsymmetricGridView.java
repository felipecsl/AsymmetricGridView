package com.felipecsl.asymmetricgridview.library.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.WrapperListAdapter;

import com.felipecsl.asymmetricgridview.library.AsymmetricGridViewAdapterContract;
import com.felipecsl.asymmetricgridview.library.Utils;

public class AsymmetricGridView extends ListView {

    private static final int DEFAULT_COLUMN_COUNT = 2;
    private static final String TAG = "AsymmetricGridView";
	private boolean requestedColumnCountCalled = false;
    protected int numColumns = DEFAULT_COLUMN_COUNT;
    protected int requestedHorizontalSpacing;
    protected int requestedColumnWidth;
    protected int requestedColumnCount;
    protected boolean allowReordering;
    protected boolean debugging;
    protected AsymmetricGridViewAdapterContract gridAdapter;
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    public AsymmetricGridView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        requestedHorizontalSpacing = Utils.dpToPx(context, 5);

        final ViewTreeObserver vto = getViewTreeObserver();
        if (vto != null)
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override public void onGlobalLayout() {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    determineColumns();
                    if (gridAdapter != null)
                        gridAdapter.recalculateItemsPerRow();
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
        ListAdapter innerAdapter = adapter;

        if (innerAdapter instanceof WrapperListAdapter) {
            WrapperListAdapter wrapperAdapter = (WrapperListAdapter) innerAdapter;
            innerAdapter = wrapperAdapter.getWrappedAdapter();

            if (!(innerAdapter instanceof AsymmetricGridViewAdapterContract))
                throw new UnsupportedOperationException("Wrapped adapter must implement AsymmetricGridViewAdapterContract");
        } else {
            if (!(innerAdapter instanceof AsymmetricGridViewAdapterContract))
                throw new UnsupportedOperationException("Adapter must implement AsymmetricGridViewAdapterContract");
        }

        gridAdapter = (AsymmetricGridViewAdapterContract) innerAdapter;
        super.setAdapter(innerAdapter);
        gridAdapter.recalculateItemsPerRow();
    }

    public void setRequestedColumnWidth(final int width) {
        requestedColumnWidth = width;
    }

    public void setRequestedColumnCount(int requestedColumnCount) {
        this.requestedColumnCount = requestedColumnCount;
	    this.requestedColumnCountCalled = true;
    }

    public int getRequestedHorizontalSpacing() {
        return requestedHorizontalSpacing;
    }

    public void setRequestedHorizontalSpacing(int spacing) {
        requestedHorizontalSpacing = spacing;
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
        ss.numColumns = numColumns;
        ss.requestedColumnCount = requestedColumnCount;
        ss.requestedColumnWidth = requestedColumnWidth;
        ss.requestedHorizontalSpacing = requestedHorizontalSpacing;

        if (gridAdapter != null)
            ss.adapterState = gridAdapter.saveState();

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

        allowReordering = ss.allowReordering;
        debugging = ss.debugging;
        numColumns = ss.numColumns;
	    if (!requestedColumnCountCalled) {
		    requestedColumnCount = ss.requestedColumnCount;
	    }
        requestedColumnWidth = ss.requestedColumnWidth;
        requestedHorizontalSpacing = ss.requestedHorizontalSpacing;

        if (gridAdapter != null)
            gridAdapter.restoreState(ss.adapterState);

        setSelectionFromTop(20, 0);
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getColumnWidth() {
        return (getAvailableSpace() - ((numColumns - 1) * requestedHorizontalSpacing)) / numColumns;
    }

    public int getAvailableSpace() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    public boolean isAllowReordering() {
        return allowReordering;
    }

    public void setAllowReordering(final boolean allowReordering) {
        this.allowReordering = allowReordering;
        if (gridAdapter != null) {
            gridAdapter.recalculateItemsPerRow();
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
