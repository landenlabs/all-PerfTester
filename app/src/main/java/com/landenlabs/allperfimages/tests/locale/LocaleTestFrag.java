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

package com.landenlabs.allperfimages.tests.locale;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.landenlabs.allperfimages.BaseFrag;
import com.landenlabs.allperfimages.R;
import com.landenlabs.allperfimages.ui.ShareUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.landenlabs.allperfimages.ui.Ui.viewById;

/**
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="http://landenlabs.com/android/index-m.html"> author's web-site </a>
 */

public class LocaleTestFrag extends BaseFrag implements
        AbsListView.OnScrollListener,
        View.OnClickListener {


    TextView mResultsTv;
    ProgressBar mProgress;
    CheckBox mFmtCb, mNumCb, mCalCb;    // String.format(), NumericFormat, Calendare format.
    CheckBox mTestRegionsCb;
    Button mRunBtn, mShareBtn;
    String mRunText;
    Menu mMenu;

    TestLocaleAsync mTestLocalAsync;
    Map<String, List<Locale>> languages = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        mRootView = inflater.inflate(R.layout.locale_test_view, container, false);

        setup();
        return mRootView;
    }

    // ---------------------------------------------------------------------------------------------
    // Menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu.addSubMenu("Locale Options");
        inflater.inflate(R.menu.locale_menu, mMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.locale_menu_fmt_d:
            case R.id.locale_menu_fmt_f:
            case R.id.locale_menu_fmt_g:
                item.setChecked(!item.isChecked());
                return true;

            case R.id.locale_menu_cal_time:
            case R.id.locale_menu_cal_date:
            case R.id.locale_menu_cal_day:
            case R.id.locale_menu_cal_month:
            case R.id.locale_menu_cal_full:
                item.setChecked(!item.isChecked());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // =============================================================================================
    // BaseFrag

    @Override
    public int getFragId() {
        return R.id.locale_test_id;
    }

    @Override
    public String getName() {
        return "Locale";
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

    private static String getAbbr(Locale locale) {
        if (Build.VERSION.SDK_INT >= 21) {
            return locale.toLanguageTag();  // Returns well formatted BCP-47 language-region, ex: en-US
        } else {
            return locale.toString();       // Returns ll_rr language_region, ex: en_US
        }
    }

    String buildStr(String[] parts, boolean[] selected, String sep) {
        StringBuilder strBld = new StringBuilder();
        for (int idx = 0; idx < Math.min(parts.length, selected.length); idx++) {
            if (selected[idx]) {
                if (strBld.length() != 0)
                    strBld.append(sep);
                strBld.append(parts[idx]);
            }
        }
        strBld.append(" ");
        return strBld.toString();
    }

    final Calendar cal = Calendar.getInstance();
    final float fvalue = -12.345f;
    final int ivalue = -123456;

    String localeFmt;
    String timeFmt;
    boolean tests[];
    boolean testFmts[];
    boolean testCals[];
    boolean testRegions = true;

    ExpandableListAdapter mAdapter;

    void initTests() {
        testRegions = mTestRegionsCb.isChecked();
        tests = new boolean[] {  mFmtCb.isChecked(), mNumCb.isChecked(), mCalCb.isChecked() };

        testFmts = new boolean[]{
                mMenu.findItem(R.id.locale_menu_fmt_d).isChecked(),
                mMenu.findItem(R.id.locale_menu_fmt_f).isChecked(),
                mMenu.findItem(R.id.locale_menu_fmt_g).isChecked(),
        };
        testCals = new boolean[]{
                mMenu.findItem(R.id.locale_menu_cal_time).isChecked(),
                mMenu.findItem(R.id.locale_menu_cal_date).isChecked(),
                mMenu.findItem(R.id.locale_menu_cal_day).isChecked(),
                mMenu.findItem(R.id.locale_menu_cal_month).isChecked(),
                mMenu.findItem(R.id.locale_menu_cal_full).isChecked(),
        };

        localeFmt = buildStr(new String[] {"%1$d","%2$f","%2$g"}, testFmts, "/");
        timeFmt = buildStr(new String[]
                        {
                                "%1$tT",    // Time:  hh:mm:ss
                                "%1$tF",    // Date:  yyyy-mm-dd
                                "%1$tA",    // Day:   Sunday, Monday, ...
                                "%1$tB",    // Month: January, February, ..
                                "%1$tc"},   // Full:  Sun Jul 20 16:17:00 EDT 1969
                testCals, " ");
    }

    public String getLocaleStr(Locale locale) {
        StringBuilder sb = new StringBuilder();

        if (tests[0])
            sb.append(String.format(locale, localeFmt, ivalue, fvalue));

        if (tests[1]) {
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            sb.append("NUM[" + format.format(ivalue) + "/" + format.format(fvalue) + "] ");
        }

        if (tests[2])
            sb.append(String.format(locale, timeFmt, cal));

        return sb.toString();
    }

    public String updateReport(TestLocaleAsync testLocaleAsync) {

        StringBuilder resultSb = new StringBuilder(3000);

        // resultSb.append("Model:").append(Build.MODEL).append("\n");
        // resultSb.append("OS:").append(Build.VERSION.RELEASE).append("\n\n");

        // Map<String, List<Locale>> languages = new HashMap<>();
        languages.clear();
        String localeFmtStr;
        String abbrLang, language, testFmt;
        Set<String> isoSet = new HashSet<>();

        Locale arabic = null;
        Locale ukrainian = null;
        Locale defLocale = Locale.getDefault();

        Locale[] locales = Locale.getAvailableLocales();
        int localeCnt = 0;
        for (Locale locale : locales) {
            testFmt = getLocaleStr(locale);

            language = locale.getDisplayLanguage();
            abbrLang = getAbbr(locale);

            if (!languages.containsKey(testFmt)) {
                List<Locale> values = new ArrayList<>();
                languages.put(testFmt, values);

                /*
                if (false) {
                    localeFmtStr = String.format(locale, "%6.6s %15.15s ", abbrLang, language);
                    localeFmtStr += String.format(locale, localeFmt, ivalue, fvalue, fvalue);
                    localeFmtStr += "NUM[" +format.format(ivalue) + "/" + format.format(fvalue) + "] ";
                    localeFmtStr += String.format(locale, timeFmt, cal);
                    localeFmtStr += "\n";
                    resultSb.append(localeFmtStr);
                }
                */
            }

            String iso = locale.getISO3Language();
            if (testRegions || !isoSet.contains(iso)) {
                languages.get(testFmt).add(locale);
            }
            isoSet.add(iso);

            if (iso.equals("ara")) {    // language Arabic
                arabic = locale;
            }
            if (iso.equals("ukr")) {    // language Ukrainian
                ukrainian = locale;
            }

            /*
            if (false) {
                localeFmtStr = String.format(locale, "%6.6s %15.15s ", abbrLang, language);
                if (tests[0])
                    localeFmtStr += String.format(locale, localeFmt, ivalue, fvalue, fvalue);
                if (tests[1])
                    localeFmtStr += format.format(ivalue) + "/" + format.format(fvalue) + " ";
                if (tests[2])
                    localeFmtStr += String.format(locale, timeFmt, cal);
                localeFmtStr += "\n";
                // resultSb.append(localeFmtStr);
                Log.d("Locale", localeFmtStr);
            }
            */

            localeCnt++;
            testLocaleAsync.sendProgress(localeCnt*100 / locales.length, resultSb);
            if (testLocaleAsync.isCancelled())
                break;
        }

        Iterator<Map.Entry<String, List<Locale>>> iter = languages.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, List<Locale>> entry = iter.next();
            if (entry.getValue().isEmpty()) {
                iter.remove();
            }
        }

         /*
        List<String> sortedKeys = new ArrayList<>(languages.keySet());
        // sortedKeys.addAll(languages.keySet());
        Collections.sort(sortedKeys);

        for (String testKey : sortedKeys) {
            String lastIso = "";
            resultSb.append(String.format(Locale.US, "%3d ", languages.get(testKey).size())).append(testKey).append("\n");
            if (false) {
                for (Locale locale : languages.get(testKey)) {
                    if (!locale.getISO3Language().equals(lastIso)) {
                        lastIso = locale.getISO3Language();
                        language = locale.getDisplayLanguage();
                        abbrLang = getAbbr(locale);
                        resultSb.append("   ").append(abbrLang).append(" ").append(language).append("\n");
                    }
                }
            }
        }
        */


        resultSb.append("\n ---[Default]---- \n");
        resultSb.append(getLocalizedString(defLocale, ivalue, fvalue, cal));

        if (ukrainian != null) {
            Locale.setDefault(ukrainian);
            resultSb.append("\n ---[" + getAbbr(Locale.getDefault()) + "]---- \n");
            resultSb.append(getLocalizedString(arabic, ivalue, fvalue, cal));
            Locale.setDefault(defLocale);
        }

        if (arabic != null) {
            Locale.setDefault(arabic);
            resultSb.append("\n ---[" + getAbbr(Locale.getDefault()) + "]---- \n");
            resultSb.append(getLocalizedString(arabic, ivalue, fvalue, cal));
            Locale.setDefault(defLocale);
        }

        if (true) {
            resultSb.append("\n -------------- ");
            resultSb.append("\nDefault Locale:").append(getAbbr(Locale.getDefault()));
            resultSb.append("\nModel:").append(Build.MODEL);
            resultSb.append("\nOS:").append(Build.VERSION.RELEASE);
            resultSb.append("\nTargetSDK:").append(getString(R.string.targetSdkVersion));
            resultSb.append("\nCompileSDK:").append(getString(R.string.compileSdkVersion));
            resultSb.append("\nBuildTools:").append(getString(R.string.buildToolsVersion));
            resultSb.append("\nJavaVersion:").append(getString(R.string.javaVersion));
            // resultSb.append("\nGradleVersion:").append(getString(R.string.gradleVersion));
        }
        resultSb.append("\n\n");

        return resultSb.toString();
    }

    String  getLocalizedString(Locale locale, int ivalue, float fvalue, Calendar cal) {
        final String  timeFmt = "cal= %1$tT %1$tF [%1$tc]\n";
        final String  localeLatLngFmt = "%s %d/%f/%g\n";
        String strLang = locale.getLanguage();

        String strFmt = String.format(localeLatLngFmt, "fmt=", ivalue, fvalue, fvalue);
        strFmt += String.format(timeFmt, cal);
        String strCat = "cat=" + " " + ivalue + "/" + fvalue  + "\n";
        String strVal = "val=" + " " + String.valueOf(ivalue) + "/" + String.valueOf(fvalue) + "\n";

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        // format.setCurrency(Currency.getInstance("CZK"));
        String strNum = "num= " + format.format(ivalue) + "/" + format.format(fvalue) + "\n";

        StringBuilder resultSb = new StringBuilder();
        resultSb
                .append(strFmt)
                .append(strCat)
                .append(strVal)
                .append(strNum);
        return resultSb.toString();
    }

    public class TestLocaleAsync extends AsyncTask<Void, Integer, String> implements DialogInterface.OnCancelListener {


        ProgressDialog mProgressDialog;
        StringBuilder mResultSb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRunBtn.setText("Cancel");
            mProgress.setVisibility(View.VISIBLE);
            mProgress.setProgress(0);

            // prepare for a progress bar dialog
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(true);
            mProgressDialog.setMessage("Testing Locales...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setProgress(0);
            mProgressDialog.setMax(100);
            mProgressDialog.setOnCancelListener(this);

            // set the drawable as progress drawavle

            if (Build.VERSION.SDK_INT >= 21) {
                mProgressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progress, getActivity().getTheme()));
            } else {
                mProgressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progress));
            }
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgress.setProgress(values[0].intValue());
            mProgressDialog.setProgress(values[0].intValue());
        }

        @Override
        protected String doInBackground(Void... params) {
            return updateReport(this);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mResultsTv.setText(result);
            mAdapter.notifyDataSetChanged();
            cancelRun();
        }

        public void sendProgress(int num, StringBuilder resultSb) {
            mResultSb = resultSb;
            publishProgress(num);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            cancelRun();
        }

        public void cancelRun() {
            this.cancel(false);
            mRunBtn.setText(mRunText);
            if (mResultSb != null && mResultSb.length() > 0) {
                mResultsTv.setText(mResultSb.toString());
            }
            mShareBtn.setEnabled(mResultsTv.getText().toString().length() > 0);
            mProgress.setProgress(100);
            mProgress.setVisibility(View.GONE);
            if (mProgressDialog != null) {
                mProgressDialog.hide();
                mProgressDialog = null;
            }
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.runTestBtn:
                initTests();
                if (mRunBtn.getText().equals(mRunText)) {
                    mTestLocalAsync = new TestLocaleAsync();
                    mTestLocalAsync.execute();
                 } else {
                    mTestLocalAsync.cancelRun();
                 }
                break;
            case R.id.shareBtn:
                ShareUtil.shareViaEmail(getActivity(),
                        "dlang@wsi.com", "Locale Tester", mResultsTv.getText().toString(), null);
                break;
        }
    }

    ExpandableListView mLocaleListView;

    protected void setup() {
        mRunText  = getString(R.string.run_test);
        mResultsTv = viewById(mRootView, R.id.testResultsTv);
        mRunBtn = viewById(mRootView, R.id.runTestBtn);
        mRunBtn.setOnClickListener(this);

        mTestRegionsCb = viewById(mRootView, R.id.testLocalRegionsCb);
        mShareBtn = viewById(mRootView, R.id.shareBtn);
        mShareBtn .setOnClickListener(this);
        
        mProgress = viewById(mRootView, R.id.localeProgressBar);
        mProgress.setVisibility(View.GONE);

        mFmtCb = viewById(mRootView, R.id.localeFmtCb);
        mNumCb = viewById(mRootView, R.id.localeNumCb);
        mCalCb = viewById(mRootView, R.id.localeCalCb);

        mLocaleListView = viewById(mRootView, R.id.locale_list);
        mAdapter = new ExpandableListAdapter();
        mLocaleListView.setAdapter(mAdapter);
    }

    // =============================================================================================
    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            String language = languages.keySet().toArray()[groupPosition].toString();
            return languages.get(language).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {

            final Locale locale = (Locale) getChild(groupPosition, childPosition);

            if (convertView == null) {
                /*
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
                */
                convertView = new TextView(parent.getContext());
            }

            // TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
            TextView childTx = (TextView)convertView;

            String language = locale.getDisplayLanguage();
            String abbrLang = getAbbr(locale);
            String fullMsg = String.format("%6.6s %15.15s ", abbrLang, language) + getLocaleStr(locale);

            childTx.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            // childTx.setTextAppearance(R.style.TextFixed14);
            childTx.setText(fullMsg);
            int colorBg = ((groupPosition & 1) == 0) ? 0xffe0ffe0 : 0xffe0e0ff;
            childTx.setBackgroundColor(colorBg);
            return childTx;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            String language = languages.keySet().toArray()[groupPosition].toString();
            return languages.get(language).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return languages.keySet().toArray()[groupPosition].toString();
        }

        @Override
        public int getGroupCount() {
            return languages.keySet().size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(
                int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            int childCnt = getChildrenCount(groupPosition);

            if (convertView == null) {
                /*
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
                */
                convertView = new TextView(parent.getContext());
            }

            // TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            TextView groupTv = (TextView)convertView;

            groupTv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            groupTv.setText(String.format("[%3d] ", childCnt) + headerTitle);
            int colorBg = ((groupPosition & 1) == 0) ? 0xffd0ffd0 : 0xffd0d0ff;
            groupTv.setBackgroundColor(colorBg);

            return groupTv;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
