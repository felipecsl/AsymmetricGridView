package com.felipecsl.asymmetricgridview.app;

import com.felipecsl.asymmetricgridview.app.model.DemoItem;

import java.util.ArrayList;
import java.util.List;

final class DemoUtils {
  int currentOffset;

  DemoUtils() {
  }

  public List<DemoItem> moarItems(int qty) {
    List<DemoItem> items = new ArrayList<>();

    for (int i = 0; i < qty; i++) {
      int colSpan = Math.random() < 0.2f ? 2 : 1;
      // Swap the next 2 lines to have items with variable
      // column/row span.
      // int rowSpan = Math.random() < 0.2f ? 2 : 1;
      int rowSpan = colSpan;
      DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
      items.add(item);
    }

    currentOffset += qty;

    return items;
  }
}
