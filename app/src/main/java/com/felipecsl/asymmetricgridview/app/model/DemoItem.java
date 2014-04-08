package com.felipecsl.asymmetricgridview.app.model;

public class DemoItem implements AsymmetricItem {
    private int columnSpan;
    private int rowSpan;

    public DemoItem() {
        this(1, 1);
    }

    public DemoItem(final int columnSpan, final int rowSpan) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
    }

    @Override
    public int getColumnSpan() {
        return columnSpan;
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }
}
