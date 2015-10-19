package com.felipecsl.asymmetricgridview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public final class Utils {
  public static int dpToPx(final Context context, final float dp) {
    // Took from http://stackoverflow.com/questions/8309354/formula-px-to-dp-dp-to-px-android
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) ((dp * scale) + 0.5f);
  }

  static int getScreenWidth(final Context context) {
    if (context == null) {
      return 0;
    }
    return getDisplayMetrics(context).widthPixels;
  }

  /**
   * Returns a valid DisplayMetrics object
   *
   * @param context valid context
   * @return DisplayMetrics object
   */
  static DisplayMetrics getDisplayMetrics(final Context context) {
    final WindowManager
        windowManager =
        (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    final DisplayMetrics metrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getMetrics(metrics);
    return metrics;
  }
}
