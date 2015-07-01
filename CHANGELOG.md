# 2.0.1

* Removes unused ic_launcher images from library code which caused build issues with latest
build tools
* Adds support for multiple itemViewTypes on AsymmetricGridViewAdapter (issue #30)

# 2.0.0

* `AsymmetricGridViewAdapter` received a major refactoring. It now doesn't keep track of the ListView
adapter items anymore. Instead, it just wraps the actual ListView adapter and subscribes for changes,
reacting to them when needed. It now acts just as a simple proxy. This allows much more flexibility,
allowing the support for CursorAdapters, for example. An example implementation of cursor adapter
support has been added to the sample app.

* Support for RecyclerView (#)

# 1.1.0

* Allows using a WrapperListAdapter as the AsymmetricGridView adapter for more flexibility.

# 1.0.27

* Fixes grid padding, vertical and horizontal spacing calculation. Uses standard listView
getDividerHeight() and getPaddingLeft/Right() instead.

# 1.0.25 and 1.0.26

* Fixes incorrect layout sometimes after rotating the screen,
caused by incorrect getNumColumns() value.

# 1.0.24

* Updates dependencies (build tools version, app compat, etc)