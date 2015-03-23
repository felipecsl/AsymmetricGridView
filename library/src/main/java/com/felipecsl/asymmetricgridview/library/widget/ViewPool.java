package com.felipecsl.asymmetricgridview.library.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Stack;

class ViewPool<T extends View> implements Parcelable {

  Stack<T> stack = new Stack<>();
  PoolObjectFactory<T> factory;
  PoolStats stats;

  public ViewPool(Parcel in) {
  }

  ViewPool() {
    stats = new PoolStats();
  }

  ViewPool(PoolObjectFactory<T> factory) {
    this.factory = factory;
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

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(@NonNull Parcel dest, final int flags) {
  }

  public static final Parcelable.Creator<ViewPool> CREATOR = new Parcelable.Creator<ViewPool>() {

    @Override public ViewPool createFromParcel(@NonNull Parcel in) {
      return new ViewPool(in);
    }

    @Override @NonNull public ViewPool[] newArray(int size) {
      return new ViewPool[size];
    }
  };
}
