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

package com.landenlabs.allperfimages.tests.image;

import android.os.Debug;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.landenlabs.allperfimages.BaseFrag;
import com.landenlabs.allperfimages.R;
import com.landenlabs.allperfimages.ui.Ui;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="http://landenlabs.com/android/index-m.html"> author's web-site </a>
 */

public abstract class ImageBaseFrag extends BaseFrag implements
        AbsListView.OnScrollListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {


    protected ListView mListView;
    protected ImageAdapter mImageAdapter;
    protected Runnable mRunnableStatus;
    protected static final long updateMill = 2000;

    SeekBar mDupSb;
    TextView mDupTv;
    Button mAutoBtn;
    TextView mRateTv;


    int mDupCnt = 1;
    long mStartTime = System.currentTimeMillis();
    long mEndTime = mStartTime;


    protected void setup(BitmapLoader.Loader loader) {

        final TextView statusTv = Ui.viewById(mRootView, R.id.status);
        mRunnableStatus = new Runnable() {
            @Override
            public void run () {
                long memUsed = Debug.getNativeHeapSize() - Debug.getNativeHeapFreeSize();
                statusTv.setText(String.format(Locale.US, "Images %,d\nHeap %,d",
                        mImageAdapter.getImagesLoadCount(),
                        memUsed));
                statusTv.postDelayed(this, updateMill);
            }
        };
        statusTv.postDelayed(mRunnableStatus, updateMill);


        ArrayList<ImageAdapter.Item> listData = getListData();

        mListView = Ui.viewById(mRootView, R.id.image_list);
        mImageAdapter = new ImageAdapter(this.getContext(), listData, loader);
        mListView.setAdapter(mImageAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                ImageAdapter.Item newsData = (ImageAdapter.Item) mListView.getItemAtPosition(position);
                Toast.makeText(ImageBaseFrag.this.getContext(), "Selected :" + " " + newsData, Toast.LENGTH_LONG).show();
            }
        });

        mDupSb = (SeekBar)mRootView.findViewById(R.id.dup_count_sb);
        mDupTv = (TextView)mRootView.findViewById(R.id.dup_count_tv);
        mAutoBtn = (Button)mRootView.findViewById(R.id.auto_scroll_btn);
        mRateTv = (TextView)mRootView.findViewById(R.id.rate_tv);

        if (mDupSb != null) {
            mDupSb.setOnSeekBarChangeListener(this);
            mAutoBtn.setOnClickListener(this);
            mRateTv.setOnClickListener(this);
        }

        mListView.setOnScrollListener(this);
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mEndTime = System.currentTimeMillis();

        double seconds = (mEndTime - mStartTime) / 1000.0;
        if (seconds > 0) {
            double imagesPerSec = mImageAdapter.getImagesLoadCount() / seconds;
            if (mRateTv != null) {
                mRateTv.setText(String.format(Locale.US, "Rate: %.2f i/s", imagesPerSec));
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mDupCnt = progress;
        if (mDupTv != null) {
            mDupTv.setText(String.format(Locale.US, "Duplicate:%d", mDupCnt));
        }
        mImageAdapter.setDupCnt(mDupCnt);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.auto_scroll_btn:
                autoScroll();
                break;
            case R.id.rate_tv:
                resetRate(mRateTv);
                break;
        }
    }

    public ArrayList<ImageAdapter.Item> getListData() {
        ArrayList<ImageAdapter.Item> listMockData = new ArrayList<ImageAdapter.Item>();

        for (int idx = 0; idx < ImageResList.ids.length; idx++) {
            ImageAdapter.Item item = new ImageAdapter.Item(ImageResList.ids[idx]);
            listMockData.add(item);
        }
        return listMockData;
    }

    void resetRate(TextView rateTv) {
        mStartTime = System.currentTimeMillis();
        rateTv.setText("Rate: 0");
    }

    void autoScroll() {

        mListView.smoothScrollToPosition(0);
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mImageAdapter.clearImageLoadCount();
                mStartTime = System.currentTimeMillis();
                mListView.smoothScrollToPosition(mImageAdapter.getCount());
            }
        }, 1000);
    }

}
