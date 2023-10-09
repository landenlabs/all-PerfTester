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

package com.landenlabs.allperfimages.tests.math;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.Location;
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

import com.google.android.gms.maps.model.LatLng;
import com.landenlabs.allperfimages.BaseFrag;
import com.landenlabs.allperfimages.R;
import com.landenlabs.allperfimages.ui.ShareUtil;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.landenlabs.allperfimages.ui.Ui.viewById;

/**
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="http://landenlabs.com"> author's web-site </a>
 */

public class Math0TestFrag extends BaseFrag implements
        AbsListView.OnScrollListener,
        View.OnClickListener {

    TextView mResultsTv;
    ProgressBar mProgress;
    CheckBox mNanCb, mNotZeroCb, mZeroCb;
    Button mRunBtn, mShareBtn;
    String mRunText;
    Menu mMenu;

    TestMathAsync mTestMathAsync;
    SortedMap<String, List<String>> testResults = new TreeMap<>();
    Map<String, String> posZipcodes = new HashMap<>();

    private static final float EARTH_RADIUS_METERS = 6378137f; // meters WGS84 Major axis

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        mRootView = inflater.inflate(R.layout.math0_test_view, container, false);

        setup();
        return mRootView;
    }

    // ---------------------------------------------------------------------------------------------
    // Menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu.addSubMenu("Math Options");
        inflater.inflate(R.menu.math0_menu, mMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.math_menu_zip:
                item.setChecked(true);
                mMenu.findItem(R.id.math_menu_sfc).setChecked(false);
                break;
            case R.id.math_menu_sfc:
                item.setChecked(true);
                mMenu.findItem(R.id.math_menu_zip).setChecked(false);
                break;
        }

        if (item.isChecked()) {
            switch (id) {
                case R.id.math_menu_zip:
                    addZipcodeTests();
                    return true;
                case R.id.math_menu_sfc:
                    addSurfaceTests();
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // =============================================================================================
    // BaseFrag

    @Override
    public int getFragId() {
        return R.id.math_test_id;
    }

    @Override
    public String getName() {
        return "Math0";
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

    boolean tests[];

    ExpandableListAdapter mAdapter;

    void initTests() {
        tests = new boolean[] {  mNanCb.isChecked(), mNotZeroCb.isChecked(), mZeroCb.isChecked() };
    }

    static class LocPair {
        LatLng loc1, loc2;
        public LocPair(LatLng loc1) {
            this.loc1 = loc1;
            this.loc2 = loc1;
        }
        public LocPair(LatLng loc1, LatLng loc2) {
            this.loc1 = loc1;
            this.loc2 = loc2;
        }
        public LocPair(double lat1, double lng1) {
            this.loc1 = new LatLng(lat1, lng1);
            this.loc2 = this.loc1;
        }
        public LocPair(double lat1, double lng1, double lat2, double lng2) {
            this.loc1 = new LatLng(lat1, lng1);
            this.loc2 = new LatLng(lat2, lng2);
        }
        public String toString() {
            if (loc1.equals(loc2)) {
                return String.format("%f,%f", loc1.latitude, loc1.longitude);
            } else {
                return String.format("%f,%f %f,%f", loc1.latitude, loc1.longitude, loc2.latitude,
                        loc2.longitude);
            }
        }
    }

    private static final List<LocPair> testPnts = new ArrayList<>();
    static {
        testPnts.add(new LocPair(30.44, -91.19));
        // testPnts.add(new LocPair(31.44, -91.19));
    }


    /**
     * @return Approximate kilometers between two earth surface points.
     *
     * Law of cosines:
     *       d = acos( sin φ1 ⋅ sin φ2 + cos φ1 ⋅ cos φ2 ⋅ cos Δλ ) ⋅ R
     *
     * Note - Android's Location class has a distance method but
     * this is accurate enough for points within a few degrees with
     * less work.
     */

    private  static double kilometersBetweenLatLng(LatLng g1Position, LatLng g2Position, boolean useFix) {
        double dValue = Math.sin(Math.toRadians(g1Position.latitude))
                * Math.sin(Math.toRadians(g2Position.latitude))
                + Math.cos(Math.toRadians(g1Position.latitude))
                * Math.cos(Math.toRadians(g2Position.latitude))
                * Math.cos(Math.toRadians(g2Position.longitude - g1Position.longitude));

        double ans;
        if (useFix) {
            // Fix to work around Android math error when same points [30.44, -91.19] generate NaN.
            // Clamp dValue to <= 1.0 to prevent Not-a-number
            ans = EARTH_RADIUS_METERS / 1000.0 * Math.acos(Math.min(dValue, 1.0));
            if (Double.isNaN(ans)) {
                return 0;
            }
        } else {
            ans = EARTH_RADIUS_METERS / 1000.0  * Math.acos(dValue);
        }

        return ans;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     */
    public static double earthDistanceKm(LatLng gp1, LatLng gp2) {

        double latDistance = Math.toRadians(gp2.latitude - gp1.latitude);
        double lonDistance = Math.toRadians(gp2.longitude - gp1.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(gp1.latitude)) * Math.cos(Math.toRadians(gp2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double meters =  Math.sqrt(Math.pow(EARTH_RADIUS_METERS * c, 2)); // convert to meters
        return meters / 1000;   // convert to Kilometers
    }

    private double earthDistanceKmAndroid(LatLng pos1, LatLng pos2) {
        float[] meters = new float[3];
        Location.distanceBetween(pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude, meters);
        return meters[0] / 1000;
    }

    private boolean keepResult(double value) {
        if (Double.isNaN(value)) {
            return mNanCb.isChecked();
        }
        if (value != 0.0) {
            return mNotZeroCb.isChecked();
        }
        
        return mZeroCb.isChecked();
    }
    
    public String updateReport(TestMathAsync TestMathAsync) {

        StringBuilder resultSb = new StringBuilder(3000);
      
        int testCnt = 0;
        for (LocPair locPair : testPnts) {

            List<String> results = new ArrayList<>();
            double km = kilometersBetweenLatLng(locPair.loc1, locPair.loc2, false);
            if (keepResult(km))
                results.add(String.format(" NoFix value=%f", km));
            km = kilometersBetweenLatLng(locPair.loc1, locPair.loc2, true);
            if (keepResult(km))
                results.add(String.format("YesFix value=%f", km));
            km = earthDistanceKm(locPair.loc1, locPair.loc2);
            if (keepResult(km))
                results.add(String.format("Surface value=%f", km));
            testCnt++;
            TestMathAsync.sendProgress(testCnt*100 / testPnts.size(), resultSb);

            testResults.put(locPair.toString(), results);

            if (TestMathAsync.isCancelled())
                break;
        }

        Iterator<Map.Entry<String, List<String>>> iter = testResults.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, List<String>> entry = iter.next();
            if (entry.getValue().isEmpty()) {
                iter.remove();
            }
        }

        resultSb.append("\n ---[Default]---- \n");
        

        if (true) {
            resultSb.append("\n -------------- ");
            resultSb.append("\nModel:").append(Build.MODEL);
            resultSb.append("\nOS:").append(Build.VERSION.RELEASE);
            resultSb.append("\nTargetSDK:").append(getString(R.string.targetSdkVersion));
            resultSb.append("\nCompileSDK:").append(getString(R.string.compileSdkVersion));
            resultSb.append("\nBuildTools:").append(getString(R.string.buildToolsVersion));
            // resultSb.append("\nJavaVersion:").append(getString(R.string.javaVersion));
            // resultSb.append("\nGradleVersion:").append(getString(R.string.gradleVersion));
        }
        resultSb.append("\n\n");

        return resultSb.toString();
    }

    public class TestMathAsync extends AsyncTask<Void, Integer, String> implements DialogInterface.OnCancelListener {

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
            mProgressDialog.setMessage("Testing Math...");
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
                    mTestMathAsync = new TestMathAsync();
                    mTestMathAsync.execute();
                 } else {
                    mTestMathAsync.cancelRun();
                 }
                break;
            case R.id.shareBtn:
                StringBuffer sb = new StringBuffer(mResultsTv.getText().toString());
                for (String key : testResults.keySet()) {
                    List<String> results = testResults.get(key);
                    for (String result : results) {
                        String zipcode = posZipcodes.get(key);
                        sb.append(String.format("\n %s  Result: %s, Zip: %s", key, result, zipcode));
                    }
                }
                ShareUtil.shareViaEmail(getActivity(),
                        "dlang@wsi.com", "Math Tester", sb.toString(), null);
                break;
        }
    }

    ExpandableListView mMathListView;

    protected void setup() {
        mRunText  = getString(R.string.run_test);
        mResultsTv = viewById(mRootView, R.id.testResultsTv);
        mRunBtn = viewById(mRootView, R.id.runTestBtn);
        mRunBtn.setOnClickListener(this);

        mShareBtn = viewById(mRootView, R.id.shareBtn);
        mShareBtn .setOnClickListener(this);
        
        mProgress = viewById(mRootView, R.id.mathProgressBar);
        mProgress.setVisibility(View.GONE);

        mNanCb = viewById(mRootView, R.id.mathNanCb);
        mNotZeroCb = viewById(mRootView, R.id.mathNotZeroCb);
        mZeroCb = viewById(mRootView, R.id.mathZeroCb);

        mMathListView = viewById(mRootView, R.id.math_list);
        mAdapter = new ExpandableListAdapter();
        mMathListView.setAdapter(mAdapter);

        addZipcodeTests();
    }

    private void addSurfaceTests() {
        testPnts.clear();
        LatLng pos1 = new LatLng(30.44, -91.19);

        double heading = 45.0;

        for (int meters = 10; meters < 1000000; meters *= 10) {
            testPnts.add(new LocPair(pos1, earthPositionTraveling(pos1, meters, heading)));
        }
    }

    private void addZipcodeTests() {
        testPnts.clear();

        String[] testGeopoints = getResources().getStringArray(R.array.geopoints);
        for (String testGeopoint : testGeopoints) {
            String[] parts = testGeopoint.split(",");
            if (parts.length == 3) {
                LocPair locPair = new LocPair(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
                testPnts.add(locPair);
                posZipcodes.put(locPair.toString(), parts[2]);
            }
        }
    }

    // https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com/google/maps/android/SphericalUtil.java
    /**
     * Returns the LatLng resulting from moving a distance from an origin
     * in the specified heading (expressed in degrees clockwise from north).
            * @param from     The LatLng from which to start.
     * @param meters The distance to travel.
     * @param headingDeg  The heading in degrees clockwise from north.
     */
    public static LatLng earthPositionTraveling(LatLng from, double meters, double headingDeg) {
        double distance = meters /  EARTH_RADIUS_METERS;
        double heading = Math.toRadians(headingDeg);
        // http://williams.best.vwh.net/avform.htm#LL
        double fromLat =  Math.toRadians(from.latitude);
        double fromLng =  Math.toRadians(from.longitude);
        double cosDistance =  Math.cos(distance);
        double sinDistance =  Math.sin(distance);
        double sinFromLat =  Math.sin(fromLat);
        double cosFromLat =  Math.cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat *  Math.cos(heading);
        double dLng =  Math.atan2(
                sinDistance * cosFromLat *  Math.sin(heading),
                cosDistance - sinFromLat * sinLat);
        return new LatLng( Math.toDegrees( Math.asin(sinLat)), Math.toDegrees(fromLng + dLng));
    }

    // =============================================================================================
    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            String key = testResults.keySet().toArray()[groupPosition].toString();
            return testResults.get(key).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {

            final  String  result = (String) getChild(groupPosition, childPosition);

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
            String fullMsg = result;

            childTx.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            // childTx.setTextAppearance(R.style.TextFixed14);
            childTx.setText(fullMsg);
            int colorBg = ((groupPosition & 1) == 0) ? 0xffe0ffe0 : 0xffe0e0ff;
            childTx.setBackgroundColor(colorBg);
            return childTx;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            String language = testResults.keySet().toArray()[groupPosition].toString();
            return testResults.get(language).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return testResults.keySet().toArray()[groupPosition].toString();
        }

        @Override
        public int getGroupCount() {
            return testResults.keySet().size();
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
