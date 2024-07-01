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

/*
 *  Pulled StringBuilder source code from Android release.
 *
  * @author Dennis Lang (LanDen Labs)
 * @see <a href="https://landenlabs.com/android/index-m.html"> author's web-site </a>
 */

public final class StringBuilder2
    extends AbstractStringBuilder
    implements java.io.Serializable, CharSequence
{

    /** use serialVersionUID for interoperability */
    static final long serialVersionUID = 4383685877147921099L;

    /**
     * Constructs a string builder with no characters in it and an
     * initial capacity of 16 characters.
     */
    public StringBuilder2() {
        super(16);
    }

    /**
     * Constructs a string builder with no characters in it and an
     * initial capacity specified by the <code>capacity</code> argument.
     *
     * @param      capacity  the initial capacity.
     * @throws     NegativeArraySizeException  if the <code>capacity</code>
     *               argument is less than <code>0</code>.
     */
    public StringBuilder2(int capacity) {
        super(capacity);
    }

    /**
     * Constructs a string builder initialized to the contents of the
     * specified string. The initial capacity of the string builder is
     * <code>16</code> plus the length of the string argument.
     *
     * @param   str   the initial contents of the buffer.
     * @throws    NullPointerException if <code>str</code> is <code>null</code>
     */
    public StringBuilder2(String str) {
        super(str.length() + 16);
        append(str);
    }

    /**
     * Constructs a string builder that contains the same characters
     * as the specified <code>CharSequence</code>. The initial capacity of
     * the string builder is <code>16</code> plus the length of the
     * <code>CharSequence</code> argument.
     *
     * @param      seq   the sequence to copy.
     * @throws    NullPointerException if <code>seq</code> is <code>null</code>
     */
    public StringBuilder2(CharSequence seq) {
        this(seq.length() + 16);
        append(seq);
    }

    public StringBuilder2 append(Object obj) {
        return append(String.valueOf(obj));
    }

    public StringBuilder2 append(String str) {
        super.append(str);
        return this;
    }

    // Appends the specified string builder to this sequence.
    private StringBuilder2 append(StringBuilder2 sb) {
        if (sb == null)
            return append("null");
        int len = sb.length();
        int newcount = count + len;
        if (newcount > value.length)
            expandCapacity(newcount);
        sb.getChars(0, len, value, count);
        count = newcount;
        return this;
    }

    /**
     * Appends the specified <tt>StringBuffer</tt> to this sequence.
     * <p>
     * The characters of the <tt>StringBuffer</tt> argument are appended,
     * in order, to this sequence, increasing the
     * length of this sequence by the length of the argument.
     * If <tt>sb</tt> is <tt>null</tt>, then the four characters
     * <tt>"null"</tt> are appended to this sequence.
     * <p>
     * Let <i>n</i> be the length of this character sequence just prior to
     * execution of the <tt>append</tt> method. Then the character at index
     * <i>k</i> in the new character sequence is equal to the character at
     * index <i>k</i> in the old character sequence, if <i>k</i> is less than
     * <i>n</i>; otherwise, it is equal to the character at index <i>k-n</i>
     * in the argument <code>sb</code>.
     *
     * @param   sb   the <tt>StringBuffer</tt> to append.
     * @return  a reference to this object.
     */
    public StringBuilder2 append(StringBuffer sb) {
        super.append(sb);
        return this;
    }

    /**
     */
    public StringBuilder2 append(CharSequence s) {
        if (s == null)
            s = "null";
        if (s instanceof String)
            return this.append((String)s);
        if (s instanceof StringBuffer)
            return this.append((StringBuffer)s);
        if (s instanceof StringBuilder2)
            return this.append((StringBuilder2)s);
        return this.append(s, 0, s.length());
    }

    /**
     * @throws     IndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 append(CharSequence s, int start, int end) {
        super.append(s, start, end);
        return this;
    }

    public StringBuilder2 append(char[] str) {
        super.append(str);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 append(char[] str, int offset, int len) {
        super.append(str, offset, len);
        return this;
    }

    public StringBuilder2 append(boolean b) {
        super.append(b);
        return this;
    }

    public StringBuilder2 append(char c) {
        super.append(c);
        return this;
    }

    public StringBuilder2 append(int i) {
        super.append(i);
        return this;
    }

    public StringBuilder2 append(long lng) {
        super.append(lng);
        return this;
    }

    public StringBuilder2 append(float f) {
        super.append(f);
        return this;
    }

    public StringBuilder2 append(double d) {
        super.append(d);
        return this;
    }

    /*
    public StringBuilder2 appendCodePoint(int codePoint) {
        super.appendCodePoint(codePoint);
        return this;
    }
    */

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 delete(int start, int end) {
        super.delete(start, end);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 deleteCharAt(int index) {
        super.deleteCharAt(index);
        return this;
    }

    /*
    public StringBuilder2 replace(int start, int end, String str) {
        super.replace(start, end, str);
        return this;
    }
    */

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int index, char[] str, int offset,
                                int len)
    {
        super.insert(index, str, offset, len);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int offset, String str) {
        super.insert(offset, str);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int offset, char[] str) {
        super.insert(offset, str);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int dstOffset, CharSequence s) {
        if (s == null)
            s = "null";
        if (s instanceof String)
            return this.insert(dstOffset, (String)s);
        return this.insert(dstOffset, s, 0, s.length());
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int dstOffset, CharSequence s,
                                int start, int end)
    {
        super.insert(dstOffset, s, start, end);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int offset, boolean b) {
        super.insert(offset, b);
        return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int offset, char c) {
        super.insert(offset, c);
        return this;
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int offset, int i) {
        return insert(offset, String.valueOf(i));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int offset, long l) {
        return insert(offset, String.valueOf(l));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int offset, float f) {
        return insert(offset, String.valueOf(f));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     */
    public StringBuilder2 insert(int offset, double d) {
        return insert(offset, String.valueOf(d));
    }

    /*
    public int indexOf(String str) {
        return indexOf(str, 0);
    }
    */

    /*
    public int indexOf(String str, int fromIndex) {
        return String.indexOf(value, 0, count,
                              str.toCharArray(), 0, str.length(), fromIndex);
    }
    */

    /*
    public int lastIndexOf(String str) {
        return lastIndexOf(str, count);
    }
    */

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    /*
    public int lastIndexOf(String str, int fromIndex) {
        return String.lastIndexOf(value, 0, count,
                              str.toCharArray(), 0, str.length(), fromIndex);
    }
    */

    public StringBuilder2 reverse() {
        super.reverse();
        return this;
    }

    public String toString() {
        // Create a copy, don't share the array
        return new String(value, 0, count);
    }

    /**
     * Save the state of the <tt>StringBuilder2</tt> instance to a stream
     * (that is, serialize it).
     *
     * @serialData the number of characters currently stored in the string
     *             builder (<tt>int</tt>), followed by the characters in the
     *             string builder (<tt>char[]</tt>).   The length of the
     *             <tt>char</tt> array may be greater than the number of
     *             characters currently stored in the string builder, in which
     *             case extra characters are ignored.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        s.defaultWriteObject();
        s.writeInt(count);
        s.writeObject(value);
    }

    /**
     * readObject is called to restore the state of the StringBuffer from
     * a stream.
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        count = s.readInt();
        value = (char[]) s.readObject();
    }

}
