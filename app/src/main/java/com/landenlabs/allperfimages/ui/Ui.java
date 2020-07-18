/**
 * Copyright (c) 2017 Dennis Lang (LanDen Labs) landenlabs@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Dennis Lang  (1/10/2017)
 * @see http://landenlabs.com
 *
 */

package com.landenlabs.allperfimages.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Outline;
import android.os.Build;
import androidx.fragment.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

/**
 *
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="http://landenlabs.com/android/index-m.html"> author's web-site </a>
 */
public class Ui {

    @SuppressWarnings("unchecked")
    public static <E extends View> E viewById(View rootView, int id) {
        return (E) rootView.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public static <E extends View> E needViewById(View rootView, int id) {
        E foundView = (E)rootView.findViewById(id);
        if (foundView == null)
            throw new NullPointerException("layout resource missing");
        return foundView;
    }

    public static <E extends View> E viewById(FragmentActivity fact, int id) {
        return (E) fact.findViewById(id);
    }

/*
    public static void ToastBig(Activity activity, String str) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_big, (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(str);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
*/
    public static AlertDialog ShowMessage(Activity activity, String message) {
        AlertDialog dialog = new AlertDialog.Builder(activity).setMessage(message)
                .setPositiveButton("More", null)
                .setNegativeButton("Close", null)
                .show();
        dialog.setCanceledOnTouchOutside(true);
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setSingleLine(false);
        textView.setTextSize(20);
        return dialog;
    }

    /**
     * @return Screen Width in Pixels,
     */
    public static int getScreenWidthPixels() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeightPixels() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int pxToDp(int px) {
        return  (int)(px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int)(dp *  Resources.getSystem().getDisplayMetrics().density);
    }

    public static void setRectOutline(View view) {
        final int width = view.getWidth();
        final int height = view.getHeight();

        if (Build.VERSION.SDK_INT >= 21) {
            view.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= 21) {
                        outline.setRect(0, 0, width, height);
                    }
                }
            });
        }
    }
}
