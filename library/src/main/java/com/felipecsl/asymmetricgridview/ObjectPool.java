package com.felipecsl.asymmetricgridview;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Stack;

class ObjectPool<T> implements Parcelable {
  Stack<T> stack = new Stack<>();
  PoolObjectFactory<T> factory;
  PoolStats stats;

  public ObjectPool(Parcel in) {
  }

  ObjectPool() {
    stats = new PoolStats();
  }

  ObjectPool(PoolObjectFactory<T> factory) {
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
    if (!stack.isEmpty()) {
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

  public static final Parcelable.Creator<ObjectPool> CREATOR = new Parcelable.Creator<ObjectPool>() {

    @Override public ObjectPool createFromParcel(@NonNull Parcel in) {
      return new ObjectPool(in);
    }

    @Override @NonNull public ObjectPool[] newArray(int size) {
      return new ObjectPool[size];
    }
  };
}
