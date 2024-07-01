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

package com.landenlabs.allperfimages.tests.string;

import java.util.Arrays;

/**
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="https://landenlabs.com/android/index-m.html"> author's web-site </a>
 */
public class StringAppender2 {
    private String[] mArray = null;
    private int mSize = 0;
    private int mLength = 0;

    private char[] m_allParts;

    public StringAppender2() {
        ensureCapacityInternal(16);
    }

    public StringAppender2(int initialCapacity) {
        ensureCapacityInternal(initialCapacity);
    }

    /***
     * Start building a string.
     * @param str
     * @return this
     */
    public StringAppender2 start(String str) {
        clear();
        return append(str);
    }

    /***
     * Append to working string
     * @param str
     * @return this
     */
    public StringAppender2 append(String str) {
        add(str);
        return this;
    }

    public void clear() {
        mSize = 0;
        mLength = 0;
    }

    public void add(String str) {
        ensureCapacityInternal(mSize + 1);
        mArray[mSize++] = str;
        mLength += str.length();
    }

    private void ensureCapacityInternal(int minCapacity) {
        if (mArray == null) {
            mArray = new String[minCapacity];
            return;
        }

        if (minCapacity <= mArray.length) {
            return;
        }

        int oldCapacity = mArray.length;
        int newCapacity = oldCapacity * 2 + 2;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;

        // minCapacity is usually close to size, so this is a win
        mArray = Arrays.copyOf(mArray, newCapacity);

        /*  Alternate solution, merge
        String current = toString();
        mSize = 0;
        //mArray = new String[newCapacity];
        mArray[mSize++] = current;
        */
    }

    /***
     * @return length of all parts.
     */
    public int length() {
        return mLength;
    }

    /***
     * @return combination of parts.
     */
    public String toString() {
        if (mLength == 0)
            return "";

        if (m_allParts == null || m_allParts.length < mLength)
            m_allParts = new char[mLength];

        int pos = 0;
        for (int idx = 0; idx < mSize; idx++) {
            String str = mArray[idx];
            int len = str.length();
            str.getChars(0, len, m_allParts, pos);
            // System.arraycopy(str.toCharArray(), 0, m_allParts, pos, len);
            pos += len;
        }

        // return String.valueOf(m_allParts);
        return new String(m_allParts, 0, mLength);
        // return String.copyValueOf(m_allParts, 0, mLength);
    }
}
