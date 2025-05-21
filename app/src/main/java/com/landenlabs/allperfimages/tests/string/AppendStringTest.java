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

import android.os.Debug;
import android.text.SpannableString;

import com.landenlabs.allperfimages.MemUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="https://LanDenLabs.com/android/index-m.html"> author's web-site </a>
 */
public class AppendStringTest {

    static int APPEND_COUNT = 50;
    static int REPEAT_COUNT = 1000;
    static final String APPENDER = "HelloWorld;";

    static Debug.MemoryInfo memInfo1 = new Debug.MemoryInfo();
    static Debug.MemoryInfo memInfo2 = new Debug.MemoryInfo();

    public interface StrBuilder {
        String getString();
    }

    static class UseStringBuilder implements StrBuilder {
        public String test(String str1, String str2) {
            return new StringBuilder().append(str1).append(str2).toString();
        }

        public String getString() {
            StringBuilder strBld = new StringBuilder();
            for (int idx = 0; idx < APPEND_COUNT; idx++) {
                // String str = "temp" + idx + ".ext";
                strBld.append(APPENDER);
            }
            return strBld.toString();
        }
    }

    static class UseStringBuilder2 implements StrBuilder {
        public String test(String str1, String str2) {
            return new StringBuilder2().append(str1).append(str2).toString();
        }

        public String getString() {
            StringBuilder2 strBld = new StringBuilder2();
            for (int idx = 0; idx < APPEND_COUNT; idx++) {
                // String str = "temp" + idx + ".ext";
                strBld.append(APPENDER);
            }
            return strBld.toString();
        }
    }

    static class UseStringAppender implements StrBuilder {
        public String test(String str1, String str2) {
            return new StringAppender1().append(str1).append(str2).toString();
        }

        public String getString() {
            StringAppender1 strApp = new StringAppender1();
            for (int idx = 0; idx < APPEND_COUNT; idx++) {
                // String str = "temp" + idx + ".ext";
                strApp.add(APPENDER);
            }
            return strApp.toString();
        }
    }

    static class UseStringAppend2 implements StrBuilder {
        public String test(String str1, String str2) {
            return new StringAppender2().append(str1).append(str2).toString();
        }

        public String getString() {
            StringAppender2 strApp = new StringAppender2();
            for (int idx = 0; idx < APPEND_COUNT; idx++) {
                // String str = "temp" + idx + ".ext";
                strApp.add(APPENDER);
            }
            return strApp.toString();
        }
    }

    static class UseStringList implements StrBuilder {
        public String getString() {
            ArrayList<String> strApp = new ArrayList<String>();
            for (int idx = 0; idx < APPEND_COUNT; idx++) {
                // String str = "temp" + idx + ".ext";
                strApp.add(APPENDER);
            }
            return APPENDER + strApp.size();
        }
    }

    public static class PerfStat {
        public String name;
        public String data;
        public long   hash;
        public long    memused;
        public double  seconds;
    }

    public static SpannableString getReport(List<PerfStat> perfList, Comparator<PerfStat> comp) {

        double minSeconds = Double.MAX_VALUE;
        for (PerfStat perfStat : perfList) {
            if (perfStat.seconds < minSeconds){
                minSeconds = perfStat.seconds;
            }
        }

        Collections.sort(perfList, comp);

        StringBuilder sb = new StringBuilder(500);
        sb.append(String.format(Locale.US, "%-15.15s %10s, %8s, %8s %8s\n", "Test Name", "Mem Used", "Seconds", "Scale", "Hash"));
        for (PerfStat perfStat : perfList) {
            sb.append(String.format(Locale.US, "%-15.15s %,10d, %,8.6f, %,8.2f, %d\n",
                    perfStat.name, perfStat.memused, perfStat.seconds,
                    perfStat.seconds / minSeconds, perfStat.hash));
        }

        SpannableString span = new SpannableString(sb.toString());
        return span;
    }

    /**
     * @param loopCnt   3
     * @param appendCnt 50
     * @param useCnt    1000
     * @return
     */
    public static List<PerfStat> tests(int loopCnt, int appendCnt, int useCnt, boolean[] whichTest) {

        List<PerfStat> perfList = new ArrayList<>(4);

        final int LOOP_CNT = loopCnt;
        APPEND_COUNT = appendCnt;
        REPEAT_COUNT = useCnt;

        /*
        results.append("\ntestStringList");
        for (int idx = 0; idx < LOOP_CNT; idx++) {
            testString(, resultsnew UseStringList());
        }
        */

        if (whichTest[0]) {
            // results.append("\ntestStringAppender\n");
            // results.append(new UseStringAppender().test("hello", "world"));
            for (int idx = 0; idx < LOOP_CNT; idx++) {
                testString(perfList, "0:Appendr1_"+idx, new UseStringAppender());
            }
        }

        if (whichTest[1]) {
            // results.append("\ntestStringBuilder\n");
            // results.append(new UseStringBuilder().test("hello", "world"));
            for (int idx = 0; idx < LOOP_CNT; idx++) {
                testString(perfList, "1:Builder1_"+idx, new UseStringBuilder());
            }
        }

        if (whichTest[2]) {
            // results.append("\ntestStringAppend2\n");
            // results.append(new UseStringAppend2().test("hello", "world"));
            for (int idx = 0; idx < LOOP_CNT; idx++) {
                testString(perfList, "2:Appendr2_"+idx, new UseStringAppend2());
            }
        }


        if (whichTest[3]) {
            // results.append("\ntestStringBuilder2\n");
            // results.append(new UseStringBuilder2().test("hello", "world"));
            for (int idx = 0; idx < LOOP_CNT; idx++) {
                testString(perfList, "3:Builder2_"+idx, new UseStringBuilder2());
            }
        }

        return perfList;
    }

    static void testString(List<PerfStat> perfList, String name, StrBuilder strBuilder) {

        List<String> strList = new ArrayList<String>(REPEAT_COUNT);
        int total = 0;
        long nanoSec1 = System.nanoTime();
        long mem1 = MemUtil.used(true);

        // Debug.getMemoryInfo(memInfo1);
        // Runtime rt = Runtime.getRuntime();

        for (int idx = 0; idx < REPEAT_COUNT; idx++) {
            strList.add(strBuilder.getString());
            total += strList.get(idx).length();
        }

        long nanoSec2 = System.nanoTime();
        long memUsed =  MemUtil.used(false);
        // Debug.getMemoryInfo(memInfo1);

        PerfStat perfStat = new PerfStat();
        perfStat.data = strList.get(10).substring(0, 10);
        perfStat.hash = strList.get(0).hashCode();
        perfStat.name = name;
        perfStat.memused = memUsed - mem1;
        perfStat.seconds = (nanoSec2 - nanoSec1) / 1e9;
        perfList.add(perfStat);
    }
}
