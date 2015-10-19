package com.felipecsl.asymmetricgridview;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

final class AsymmetricViewImpl {
  private static final int DEFAULT_COLUMN_COUNT = 2;
  protected int numColumns = DEFAULT_COLUMN_COUNT;
  protected int requestedHorizontalSpacing;
  protected int requestedColumnWidth;
  protected int requestedColumnCount;
  protected boolean allowReordering;
  protected boolean debugging;

  AsymmetricViewImpl(Context context) {
    requestedHorizontalSpacing = Utils.dpToPx(context, 5);
  }

  public void setRequestedColumnWidth(int width) {
    requestedColumnWidth = width;
  }

  public void setRequestedColumnCount(int requestedColumnCount) {
    this.requestedColumnCount = requestedColumnCount;
  }

  public int getRequestedHorizontalSpacing() {
    return requestedHorizontalSpacing;
  }

  public void setRequestedHorizontalSpacing(int spacing) {
    requestedHorizontalSpacing = spacing;
  }

  public int determineColumns(int availableSpace) {
    int numColumns;

    if (requestedColumnWidth > 0) {
      numColumns = (availableSpace + requestedHorizontalSpacing) /
          (requestedColumnWidth + requestedHorizontalSpacing);
    } else if (requestedColumnCount > 0) {
      numColumns = requestedColumnCount;
    } else {
      // Default to 2 columns
      numColumns = DEFAULT_COLUMN_COUNT;
    }

    if (numColumns <= 0) {
      numColumns = 1;
    }

    this.numColumns = numColumns;

    return numColumns;
  }

  public Parcelable onSaveInstanceState(Parcelable superState) {
    SavedState ss = new SavedState(superState);
    ss.allowReordering = allowReordering;
    ss.debugging = debugging;
    ss.numColumns = numColumns;
    ss.requestedColumnCount = requestedColumnCount;
    ss.requestedColumnWidth = requestedColumnWidth;
    ss.requestedHorizontalSpacing = requestedHorizontalSpacing;
    return ss;
  }

  public void onRestoreInstanceState(SavedState ss) {
    allowReordering = ss.allowReordering;
    debugging = ss.debugging;
    numColumns = ss.numColumns;
    requestedColumnCount = ss.requestedColumnCount;
    requestedColumnWidth = ss.requestedColumnWidth;
    requestedHorizontalSpacing = ss.requestedHorizontalSpacing;
  }

  public int getNumColumns() {
    return numColumns;
  }

  public int getColumnWidth(int availableSpace) {
    return (availableSpace - ((numColumns - 1) * requestedHorizontalSpacing)) / numColumns;
  }

  public boolean isAllowReordering() {
    return allowReordering;
  }

  public void setAllowReordering(boolean allowReordering) {
    this.allowReordering = allowReordering;
  }

  public boolean isDebugging() {
    return debugging;
  }

  public void setDebugging(boolean debugging) {
    this.debugging = debugging;
  }

  static class SavedState extends View.BaseSavedState {
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

    SavedState(Parcelable superState) {
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

    @Override public void writeToParcel(@NonNull Parcel dest, int flags) {
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

    public static final Parcelable.Creator<SavedState> CREATOR =
        new Parcelable.Creator<SavedState>() {
          @Override public SavedState createFromParcel(@NonNull Parcel in) {
            return new SavedState(in);
          }

          @Override @NonNull public SavedState[] newArray(int size) {
            return new SavedState[size];
          }
        };
  }
}
