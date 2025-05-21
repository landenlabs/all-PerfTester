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
 * @see https://LanDenLabs.com
 *
 */


package com.landenlabs.allperfimages.tests.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.landenlabs.allperfimages.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="https://LanDenLabs.com/android/index-m.html"> author's web-site </a>
 */


public class ImageAdapter extends BaseAdapter {
    private final ArrayList<ImageAdapter.Item> mListData;
    private final BitmapLoader.Loader mLoader;
    private final LayoutInflater mLayoutInflater;
    private long mImageLoadCount = 0;
    private int mDupCnt = 1;

    private static final int[] mColors = { 0xffc0c0ff, 0xffc0ffc0 } ;

    public static class Item  {
        final int mResId;

        public Item(int resId) {
            mResId = resId;
        }
    }

    public ImageAdapter(Context context, ArrayList<ImageAdapter.Item> listData, BitmapLoader.Loader loader) {
        mListData = listData;
        mLoader = loader;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public long getImagesLoadCount() {
        return mImageLoadCount;
    }

    public void clearImageLoadCount() {
        mImageLoadCount = 0;
    }

    public void setDupCnt(int dupCnt) {
        if (mDupCnt != dupCnt) {
            mDupCnt = dupCnt;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mListData.size() * mDupCnt;
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position / mDupCnt);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.image_row, parent, false);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            holder.sizeView = (TextView) convertView.findViewById(R.id.size);
            holder.dateView = (TextView) convertView.findViewById(R.id.date);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = (Item) mListData.get(position / mDupCnt);
        int resId = item.mResId;
        mImageLoadCount++;
        long startMilli = System.currentTimeMillis();
        Bitmap bitmap = mLoader.getBitmap(parent.getContext(), resId);
        long deltaMilli = System.currentTimeMillis() - startMilli;

        holder.imageView.setImageBitmap(bitmap);
        holder.titleView.setText(String.format(Locale.US, "Dim %d x %d", bitmap.getWidth(), bitmap.getHeight() ));
        holder.sizeView.setText(String.format(Locale.US, "Size: %,d", bitmap.getAllocationByteCount()));
        // holder.dateView.setText(String.format("%,.2f", deltaMilli / 1000.0));
        holder.dateView.setText("" + bitmap.hashCode());
        convertView.setBackgroundColor(mColors[position % mColors.length]);

        return convertView;
    }

    static class ViewHolder {
        TextView titleView;
        TextView sizeView;
        TextView dateView;
        ImageView imageView;
    }
}