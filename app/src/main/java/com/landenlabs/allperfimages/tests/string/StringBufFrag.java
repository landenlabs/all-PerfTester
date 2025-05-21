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

package com.landenlabs.allperfimages.tests.string;

import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.landenlabs.allperfimages.BaseFrag;
import com.landenlabs.allperfimages.R;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="https://LanDenLabs.com/android/index-m.html"> author's web-site </a>
 */

public class StringBufFrag extends BaseFrag implements
        AbsListView.OnScrollListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    View mControlHolder;

    TextView mLoopCntTv;
    SeekBar mLoopCntSb;
    TextView mAppendCntTv;
    SeekBar mAppendCntSb;
    TextView mUseCntTv;
    SeekBar mUseCntSb;
    TextView mResultsTv;
    Button mRunBtn;

    int loopCnt = 3;
    int appendCnt = 50;
    int useCnt = 1000;

    final boolean[] tests = new boolean[] { true, true, true, true };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.string_test_view, container, false);

        setup();
        return mRootView;
    }

    @Override
    public int getFragId() {
        return R.id.string_test_id;
    }

    @Override
    public String getName() {
        return "String";
    }

    @Override
    public String getDescription() {
        return "??";
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        switch (id) {
            case R.id.testCntSb:
                loopCnt = progress;
                mLoopCntTv.setText(String.format(Locale.US, "LoopCnt(%d)", loopCnt));
                break;
            case R.id.repeatCntSb:
                useCnt = progress;
                mUseCntTv.setText(String.format(Locale.US, "UseCnt(%d)", useCnt));
                break;
            case R.id.appendCntSb:
                appendCnt = progress;
                mAppendCntTv.setText(String.format(Locale.US, "AppendCnt(%d)", appendCnt));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    final Comparator<AppendStringTest.PerfStat> sortBySeconds = (o1, o2) -> (int)Math.signum(o1.seconds - o2.seconds);

    final Comparator<AppendStringTest.PerfStat> sortByMemUsed = (o1, o2) -> (int)(o1.memused - o2.memused);

    final Comparator<AppendStringTest.PerfStat> sortByName = (o1, o2) -> o1.name.compareTo(o2.name);

    Comparator<AppendStringTest.PerfStat> sortPerf = sortBySeconds;

    void updateReport() {
        List<AppendStringTest.PerfStat> perfList = AppendStringTest.tests(loopCnt, appendCnt, useCnt, tests);
        SpannableString report = AppendStringTest.getReport(perfList, sortPerf);

        mResultsTv.setText(
                String.format("Loop:%d App:%d Use:%d\n", loopCnt, appendCnt, useCnt) + report);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.runTestBtn:
                updateReport();
                break;
            case R.id.controlBtn:
                if (mControlHolder.getVisibility() == View.GONE)
                    mControlHolder.setVisibility(View.VISIBLE);
                else
                    mControlHolder.setVisibility(View.GONE);
                break;
        }
    }


    protected void setup() {
        mRootView.findViewById(R.id.controlBtn).setOnClickListener(this);
        mControlHolder = mRootView.findViewById(R.id.controlHolder);

        mLoopCntTv = (TextView)mRootView.findViewById(R.id.testCntTv);
        mLoopCntSb = (SeekBar)mRootView.findViewById(R.id.testCntSb);
        mAppendCntTv = (TextView)mRootView.findViewById(R.id.appendCntTv);
        mAppendCntSb = (SeekBar)mRootView.findViewById(R.id.appendCntSb);
        mUseCntTv = (TextView)mRootView.findViewById(R.id.repeatCntTv);
        mUseCntSb = (SeekBar)mRootView.findViewById(R.id.repeatCntSb);
        mResultsTv = (TextView)mRootView.findViewById(R.id.testResultsTv);
        mRunBtn = (Button)mRootView.findViewById(R.id.runTestBtn);

        ((CheckBox)mRootView.findViewById(R.id.test0)).setOnCheckedChangeListener(this);
        ((CheckBox)mRootView.findViewById(R.id.test1)).setOnCheckedChangeListener(this);
        ((CheckBox)mRootView.findViewById(R.id.test2)).setOnCheckedChangeListener(this);
        ((CheckBox)mRootView.findViewById(R.id.test3)).setOnCheckedChangeListener(this);

        mLoopCntSb.setOnSeekBarChangeListener(this);
        mAppendCntSb.setOnSeekBarChangeListener(this);
        mUseCntSb.setOnSeekBarChangeListener(this);
        mRunBtn.setOnClickListener(this);
        mResultsTv.setOnClickListener(this);

        RadioGroup rg = (RadioGroup)mRootView.findViewById(R.id.sortRg);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.sortNameRb:
                    sortPerf = sortByName;
                    break;
                case R.id.sortMemRb:
                    sortPerf = sortByMemUsed;
                    break;
                case R.id.sortSecRb:
                    sortPerf = sortBySeconds;
                    break;
            }

            updateReport();
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.test0:
                tests[0] = isChecked;
                break;
            case R.id.test1:
                tests[1] = isChecked;
                break;
            case R.id.test2:
                tests[2] = isChecked;
                break;
            case R.id.test3:
                tests[3] = isChecked;
                break;
        }
    }
}
