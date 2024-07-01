/**
 * Copyright (c) 2017 Dennis Lang (LanDen Labs) landenlabs@gmail.com
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Dennis Lang  (1/10/2017)
 * @see https://landenlabs.com
 *
 */

package com.landenlabs.allperfimages.tests.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

/**
 * Helper class to load Bitmaps from package resources using different loaders to
 * demonstrate how Android caches Bitmaps and it is dangerous to call recycle
 * on a bitmap.
 *
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="https://landenlabs.com/android/index-m.html"> author's web-site </a>
 */

public class BitmapLoader {

    interface  Loader {
        Bitmap getBitmap(Context context,int resId);
    }

    /**
     * Load bitmap resource - no internally caching.
     * TODO - verify it is supports density scaling.
     */
    public static class Loader1 implements  Loader {

        @Override
        public Bitmap getBitmap(Context context,int resId) {
            return BitmapFactory.decodeResource(context.getResources(), resId);
        }
    }

    /**
     * Load bitmap resource - drawable cached internally
     * Supports density scaling.
     * <p>
     * From android code (density is passed as 0)
     *         // If the drawable's XML lives in our current density qualifier,
     *         // it's okay to use a scaled version from the cache. Otherwise, we
     *         // need to actually load the drawable from XML.
     *         final boolean useCache = density == 0 || value.density == mMetrics.densityDpi;
     */
    public static class Loader2 implements  Loader {

        @Override
        public Bitmap getBitmap(Context context,int resId) {
            BitmapDrawable bmDrawable;
            bmDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(context.getResources(), resId, context.getTheme());
            return bmDrawable.getBitmap();
        }
    }

    /**
     * Load bitmap resource - drawable cached internally
     * Supports density scaling.
     */
    public static class Loader3 implements  Loader {

        @Override
        public Bitmap getBitmap(Context context,int resId) {
            return ((BitmapDrawable) ResourcesCompat.getDrawable(context.getResources(), resId, context.getTheme())).getBitmap();
        }
    }

    /**
     * Load bitmap resource - drawable cached internally
     * Does not appear to support density scaling.
     */
    public static class Loader4 implements  Loader {

        @Override
        public Bitmap getBitmap(Context context,int resId) {
            BitmapDrawable bmDrawable  = (BitmapDrawable) AppCompatResources.getDrawable(context, resId);
            return bmDrawable.getBitmap();
        }
    }

}
