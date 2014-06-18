# AsymmetricGridView

An Android custom ListView that implements multiple columns and variable sized elements.

Please note that this is currently in a preview state.
This basically means that the API is not fixed and you should expect changes between releases.

## Sample application:

Try out the sample application on [Google Play](https://play.google.com/store/apps/details?id=com.felipecsl.asymmetricgridview.app)

[![Gplay](https://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.felipecsl.asymmetricgridview.app)

### Screenshots:

![screenshot 1](https://raw.githubusercontent.com/felipecsl/AsymmetricGridView/master/screenshots/ss_2_cols.png)
![screenshot 2](https://raw.githubusercontent.com/felipecsl/AsymmetricGridView/master/screenshots/ss_3_cols.png)
![screenshot 3](https://raw.githubusercontent.com/felipecsl/AsymmetricGridView/master/screenshots/ss_4_cols.png)
![screenshot 4](https://raw.githubusercontent.com/felipecsl/AsymmetricGridView/master/screenshots/ss_5_cols.png)

### Usage

In your ``build.gradle`` file:

```groovy
repositories {
    maven { url 'https://github.com/felipecsl/m2repository/raw/master/' }
    // ...
}

dependencies {
    // ...
    compile 'com.felipecsl:asymmetricgridview:1.0.+'
}
```

In your layout xml:

```xml
<com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/listView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

In your activity class:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listView = (AsymmetricGridView) findViewById(R.id.listView);

    // Choose your own preferred column width
    listView.setRequestedColumnWidth(Utils.dpToPx(this, 120));
    final List<AsymmetricItem> items = new ArrayList<>();

    // initialize your items array
    adapter = new ListAdapter(this, listView, items);

    listView.setAdapter(adapter);
}
```

Supports resetting and appending more elements into the adapter:

```java
// Will append more items at the end of the adapter.
listView.getAdapter().appendItems(moreItems);

// resetting the adapter items. Will clear the adapter
// and add the new items.
listView.getAdapter().setItems(items);
```

Toggle to enable/disable reordering of elements to better fill the grid

```java
// Setting to true will move items up and down to better use the space
// Defaults to false.
listView.setAllowReordering(true);

listView.isAllowReordering(); // true
```

Works with Android 2.3.x and above.

### Caveats

* Currently only has good support for items with rowSpan = 2 and columnSpan = 2.
In the near future it will support different layout configurations.

* It will work best if you don't have too many items with different sizes. Ideally less
than 20% of your items are of special sizes, otherwise the library may not find the best
way to accommodate all the items without leaving a lot of empty space behind.

* Row layout is too complex, with many nested LinearLayouts. Move to a more flat layout
with a custom ViewGroup possibly.

### Contributing

* Check out the latest master to make sure the feature hasn't been implemented or the bug hasn't been fixed yet
* Check out the issue tracker to make sure someone already hasn't requested it and/or contributed it
* Fork the project
* Start a feature/bugfix branch
* Commit and push until you are happy with your contribution
* Make sure to add tests for it. This is important so I don't break it in a future version unintentionally.

### Copyright and license

Code and documentation copyright 2011-2014 Felipe Lima.
Code released under the [MIT license](https://github.com/felipecsl/AsymmetricGridview/blob/master/LICENSE.txt).
