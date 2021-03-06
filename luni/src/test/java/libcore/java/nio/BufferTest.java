/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.java.nio;

import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Arrays;
import junit.framework.TestCase;

public class BufferTest extends TestCase {
    private static ByteBuffer allocateMapped(int size) throws Exception {
        File f = File.createTempFile("mapped", "tmp");
        f.deleteOnExit();
        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        raf.setLength(size);
        FileChannel ch = raf.getChannel();
        MappedByteBuffer result = ch.map(FileChannel.MapMode.READ_WRITE, 0, size);
        ch.close();
        return result;
    }

    public void testByteSwappedBulkGetDirect() throws Exception {
        testByteSwappedBulkGet(ByteBuffer.allocateDirect(10));
    }

    public void testByteSwappedBulkGetHeap() throws Exception {
        testByteSwappedBulkGet(ByteBuffer.allocate(10));
    }

    public void testByteSwappedBulkGetMapped() throws Exception {
        testByteSwappedBulkGet(allocateMapped(10));
    }

    private void testByteSwappedBulkGet(ByteBuffer b) throws Exception {
        for (int i = 0; i < b.limit(); ++i) {
            b.put(i, (byte) i);
        }
        b.position(1);

        char[] chars = new char[6];
        b.order(ByteOrder.BIG_ENDIAN).asCharBuffer().get(chars, 1, 4);
        assertEquals("[\u0000, \u0102, \u0304, \u0506, \u0708, \u0000]", Arrays.toString(chars));
        b.order(ByteOrder.LITTLE_ENDIAN).asCharBuffer().get(chars, 1, 4);
        assertEquals("[\u0000, \u0201, \u0403, \u0605, \u0807, \u0000]", Arrays.toString(chars));

        double[] doubles = new double[3];
        b.order(ByteOrder.BIG_ENDIAN).asDoubleBuffer().get(doubles, 1, 1);
        assertEquals(0, Double.doubleToRawLongBits(doubles[0]));
        assertEquals(0x0102030405060708L, Double.doubleToRawLongBits(doubles[1]));
        assertEquals(0, Double.doubleToRawLongBits(doubles[2]));
        b.order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().get(doubles, 1, 1);
        assertEquals(0, Double.doubleToRawLongBits(doubles[0]));
        assertEquals(0x0807060504030201L, Double.doubleToRawLongBits(doubles[1]));
        assertEquals(0, Double.doubleToRawLongBits(doubles[2]));

        float[] floats = new float[4];
        b.order(ByteOrder.BIG_ENDIAN).asFloatBuffer().get(floats, 1, 2);
        assertEquals(0, Float.floatToRawIntBits(floats[0]));
        assertEquals(0x01020304, Float.floatToRawIntBits(floats[1]));
        assertEquals(0x05060708, Float.floatToRawIntBits(floats[2]));
        assertEquals(0, Float.floatToRawIntBits(floats[3]));
        b.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(floats, 1, 2);
        assertEquals(0, Float.floatToRawIntBits(floats[0]));
        assertEquals(0x04030201, Float.floatToRawIntBits(floats[1]));
        assertEquals(0x08070605, Float.floatToRawIntBits(floats[2]));
        assertEquals(0, Float.floatToRawIntBits(floats[3]));

        int[] ints = new int[4];
        b.order(ByteOrder.BIG_ENDIAN).asIntBuffer().get(ints, 1, 2);
        assertEquals(0, ints[0]);
        assertEquals(0x01020304, ints[1]);
        assertEquals(0x05060708, ints[2]);
        assertEquals(0, ints[3]);
        b.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(ints, 1, 2);
        assertEquals(0, ints[0]);
        assertEquals(0x04030201, ints[1]);
        assertEquals(0x08070605, ints[2]);
        assertEquals(0, ints[3]);

        long[] longs = new long[3];
        b.order(ByteOrder.BIG_ENDIAN).asLongBuffer().get(longs, 1, 1);
        assertEquals(0, longs[0]);
        assertEquals(0x0102030405060708L, longs[1]);
        assertEquals(0, longs[2]);
        b.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(longs, 1, 1);
        assertEquals(0, longs[0]);
        assertEquals(0x0807060504030201L, longs[1]);
        assertEquals(0, longs[2]);

        short[] shorts = new short[6];
        b.order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(shorts, 1, 4);
        assertEquals(0, shorts[0]);
        assertEquals(0x0102, shorts[1]);
        assertEquals(0x0304, shorts[2]);
        assertEquals(0x0506, shorts[3]);
        assertEquals(0x0708, shorts[4]);
        assertEquals(0, shorts[5]);
        b.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts, 1, 4);
        assertEquals(0, shorts[0]);
        assertEquals(0x0201, shorts[1]);
        assertEquals(0x0403, shorts[2]);
        assertEquals(0x0605, shorts[3]);
        assertEquals(0x0807, shorts[4]);
        assertEquals(0, shorts[5]);
    }

    private static String toString(ByteBuffer b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < b.limit(); ++i) {
            result.append(String.format("%02x", (int) b.get(i)));
        }
        return result.toString();
    }

    public void testByteSwappedBulkPutDirect() throws Exception {
        testByteSwappedBulkPut(ByteBuffer.allocateDirect(10));
    }

    public void testByteSwappedBulkPutHeap() throws Exception {
        testByteSwappedBulkPut(ByteBuffer.allocate(10));
    }

    public void testByteSwappedBulkPutMapped() throws Exception {
        testByteSwappedBulkPut(allocateMapped(10));
    }

    private void testByteSwappedBulkPut(ByteBuffer b) throws Exception {
        b.position(1);

        char[] chars = new char[] { '\u2222', '\u0102', '\u0304', '\u0506', '\u0708', '\u2222' };
        b.order(ByteOrder.BIG_ENDIAN).asCharBuffer().put(chars, 1, 4);
        assertEquals("00010203040506070800", toString(b));
        b.order(ByteOrder.LITTLE_ENDIAN).asCharBuffer().put(chars, 1, 4);
        assertEquals("00020104030605080700", toString(b));

        double[] doubles = new double[] { 0, Double.longBitsToDouble(0x0102030405060708L), 0 };
        b.order(ByteOrder.BIG_ENDIAN).asDoubleBuffer().put(doubles, 1, 1);
        assertEquals("00010203040506070800", toString(b));
        b.order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().put(doubles, 1, 1);
        assertEquals("00080706050403020100", toString(b));

        float[] floats = new float[] { 0, Float.intBitsToFloat(0x01020304),
                Float.intBitsToFloat(0x05060708), 0 };
        b.order(ByteOrder.BIG_ENDIAN).asFloatBuffer().put(floats, 1, 2);
        assertEquals("00010203040506070800", toString(b));
        b.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().put(floats, 1, 2);
        assertEquals("00040302010807060500", toString(b));

        int[] ints = new int[] { 0, 0x01020304, 0x05060708, 0 };
        b.order(ByteOrder.BIG_ENDIAN).asIntBuffer().put(ints, 1, 2);
        assertEquals("00010203040506070800", toString(b));
        b.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(ints, 1, 2);
        assertEquals("00040302010807060500", toString(b));

        long[] longs = new long[] { 0, 0x0102030405060708L, 0 };
        b.order(ByteOrder.BIG_ENDIAN).asLongBuffer().put(longs, 1, 1);
        assertEquals("00010203040506070800", toString(b));
        b.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().put(longs, 1, 1);
        assertEquals("00080706050403020100", toString(b));

        short[] shorts = new short[] { 0, 0x0102, 0x0304, 0x0506, 0x0708, 0 };
        b.order(ByteOrder.BIG_ENDIAN).asShortBuffer().put(shorts, 1, 4);
        assertEquals("00010203040506070800", toString(b));
        b.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts, 1, 4);
        assertEquals("00020104030605080700", toString(b));
    }

    public void testByteBufferByteOrderDirectRW() throws Exception {
        testByteBufferByteOrder(ByteBuffer.allocateDirect(10), false);
    }

    public void testByteBufferByteOrderHeapRW() throws Exception {
        testByteBufferByteOrder(ByteBuffer.allocate(10), false);
    }

    public void testByteBufferByteOrderMappedRW() throws Exception {
        testByteBufferByteOrder(allocateMapped(10), false);
    }

    public void testByteBufferByteOrderDirectRO() throws Exception {
        testByteBufferByteOrder(ByteBuffer.allocateDirect(10), true);
    }

    public void testByteBufferByteOrderHeapRO() throws Exception {
        testByteBufferByteOrder(ByteBuffer.allocate(10), true);
    }

    public void testByteBufferByteOrderMappedRO() throws Exception {
        testByteBufferByteOrder(allocateMapped(10), true);
    }

    private void testByteBufferByteOrder(ByteBuffer b, boolean readOnly) throws Exception {
        if (readOnly) {
            b = b.asReadOnlyBuffer();
        }
        // allocate/allocateDirect/map always returns a big-endian buffer.
        assertEquals(ByteOrder.BIG_ENDIAN, b.order());

        // wrap always returns a big-endian buffer.
        assertEquals(ByteOrder.BIG_ENDIAN, b.wrap(new byte[10]).order());

        // duplicate always returns a big-endian buffer.
        b.order(ByteOrder.BIG_ENDIAN);
        assertEquals(ByteOrder.BIG_ENDIAN, b.duplicate().order());
        b.order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(ByteOrder.BIG_ENDIAN, b.duplicate().order());

        // slice always returns a big-endian buffer.
        b.order(ByteOrder.BIG_ENDIAN);
        assertEquals(ByteOrder.BIG_ENDIAN, b.slice().order());
        b.order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(ByteOrder.BIG_ENDIAN, b.slice().order());

        // asXBuffer always returns a current-endian buffer.
        b.order(ByteOrder.BIG_ENDIAN);
        assertEquals(ByteOrder.BIG_ENDIAN, b.asCharBuffer().order());
        assertEquals(ByteOrder.BIG_ENDIAN, b.asDoubleBuffer().order());
        assertEquals(ByteOrder.BIG_ENDIAN, b.asFloatBuffer().order());
        assertEquals(ByteOrder.BIG_ENDIAN, b.asIntBuffer().order());
        assertEquals(ByteOrder.BIG_ENDIAN, b.asLongBuffer().order());
        assertEquals(ByteOrder.BIG_ENDIAN, b.asShortBuffer().order());
        assertEquals(ByteOrder.BIG_ENDIAN, b.asReadOnlyBuffer().order());
        b.order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(ByteOrder.LITTLE_ENDIAN, b.asCharBuffer().order());
        assertEquals(ByteOrder.LITTLE_ENDIAN, b.asDoubleBuffer().order());
        assertEquals(ByteOrder.LITTLE_ENDIAN, b.asFloatBuffer().order());
        assertEquals(ByteOrder.LITTLE_ENDIAN, b.asIntBuffer().order());
        assertEquals(ByteOrder.LITTLE_ENDIAN, b.asLongBuffer().order());
        assertEquals(ByteOrder.LITTLE_ENDIAN, b.asShortBuffer().order());
        // ...except for asReadOnlyBuffer, which always returns a big-endian buffer.
        assertEquals(ByteOrder.BIG_ENDIAN, b.asReadOnlyBuffer().order());
    }

    public void testCharBufferByteOrderWrapped() throws Exception {
        assertEquals(ByteOrder.nativeOrder(), CharBuffer.wrap(new char[10]).order());
        assertEquals(ByteOrder.nativeOrder(), CharBuffer.wrap(new char[10]).asReadOnlyBuffer().order());
    }

    private void testCharBufferByteOrder(CharBuffer b, ByteOrder bo) throws Exception {
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
        b = b.asReadOnlyBuffer();
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
    }

    private CharBuffer allocateCharBuffer(ByteOrder order) {
        return ByteBuffer.allocate(10).order(order).asCharBuffer();
    }

    public void testCharBufferByteOrderArray() throws Exception {
        testCharBufferByteOrder(CharBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testCharBufferByteOrderBE() throws Exception {
        testCharBufferByteOrder(allocateCharBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testCharBufferByteOrderLE() throws Exception {
        testCharBufferByteOrder(allocateCharBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testDoubleBufferByteOrderWrapped() throws Exception {
        assertEquals(ByteOrder.nativeOrder(), DoubleBuffer.wrap(new double[10]).order());
        assertEquals(ByteOrder.nativeOrder(), DoubleBuffer.wrap(new double[10]).asReadOnlyBuffer().order());
    }

    private void testDoubleBufferByteOrder(DoubleBuffer b, ByteOrder bo) throws Exception {
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
        b = b.asReadOnlyBuffer();
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
    }

    private DoubleBuffer allocateDoubleBuffer(ByteOrder order) {
        return ByteBuffer.allocate(10*8).order(order).asDoubleBuffer();
    }

    public void testDoubleBufferByteOrderArray() throws Exception {
        testDoubleBufferByteOrder(DoubleBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testDoubleBufferByteOrderBE() throws Exception {
        testDoubleBufferByteOrder(allocateDoubleBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testDoubleBufferByteOrderLE() throws Exception {
        testDoubleBufferByteOrder(allocateDoubleBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testFloatBufferByteOrderWrapped() throws Exception {
        assertEquals(ByteOrder.nativeOrder(), FloatBuffer.wrap(new float[10]).order());
        assertEquals(ByteOrder.nativeOrder(), FloatBuffer.wrap(new float[10]).asReadOnlyBuffer().order());
    }

    private void testFloatBufferByteOrder(FloatBuffer b, ByteOrder bo) throws Exception {
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
        b = b.asReadOnlyBuffer();
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
    }

    private FloatBuffer allocateFloatBuffer(ByteOrder order) {
        return ByteBuffer.allocate(10*8).order(order).asFloatBuffer();
    }

    public void testFloatBufferByteOrderArray() throws Exception {
        testFloatBufferByteOrder(FloatBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testFloatBufferByteOrderBE() throws Exception {
        testFloatBufferByteOrder(allocateFloatBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testFloatBufferByteOrderLE() throws Exception {
        testFloatBufferByteOrder(allocateFloatBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testIntBufferByteOrderWrapped() throws Exception {
        assertEquals(ByteOrder.nativeOrder(), IntBuffer.wrap(new int[10]).order());
        assertEquals(ByteOrder.nativeOrder(), IntBuffer.wrap(new int[10]).asReadOnlyBuffer().order());
    }

    private void testIntBufferByteOrder(IntBuffer b, ByteOrder bo) throws Exception {
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
        b = b.asReadOnlyBuffer();
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
    }

    private IntBuffer allocateIntBuffer(ByteOrder order) {
        return ByteBuffer.allocate(10*8).order(order).asIntBuffer();
    }

    public void testIntBufferByteOrderArray() throws Exception {
        testIntBufferByteOrder(IntBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testIntBufferByteOrderBE() throws Exception {
        testIntBufferByteOrder(allocateIntBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testIntBufferByteOrderLE() throws Exception {
        testIntBufferByteOrder(allocateIntBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testLongBufferByteOrderWrapped() throws Exception {
        assertEquals(ByteOrder.nativeOrder(), LongBuffer.wrap(new long[10]).order());
        assertEquals(ByteOrder.nativeOrder(), LongBuffer.wrap(new long[10]).asReadOnlyBuffer().order());
    }

    private void testLongBufferByteOrder(LongBuffer b, ByteOrder bo) throws Exception {
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
        b = b.asReadOnlyBuffer();
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
    }

    private LongBuffer allocateLongBuffer(ByteOrder order) {
        return ByteBuffer.allocate(10*8).order(order).asLongBuffer();
    }

    public void testLongBufferByteOrderArray() throws Exception {
        testLongBufferByteOrder(LongBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testLongBufferByteOrderBE() throws Exception {
        testLongBufferByteOrder(allocateLongBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testLongBufferByteOrderLE() throws Exception {
        testLongBufferByteOrder(allocateLongBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testShortBufferByteOrderWrapped() throws Exception {
        assertEquals(ByteOrder.nativeOrder(), ShortBuffer.wrap(new short[10]).order());
        assertEquals(ByteOrder.nativeOrder(), ShortBuffer.wrap(new short[10]).asReadOnlyBuffer().order());
    }

    private void testShortBufferByteOrder(ShortBuffer b, ByteOrder bo) throws Exception {
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
        b = b.asReadOnlyBuffer();
        assertEquals(bo, b.order());
        assertEquals(bo, b.duplicate().order());
        assertEquals(bo, b.slice().order());
    }

    private ShortBuffer allocateShortBuffer(ByteOrder order) {
        return ByteBuffer.allocate(10*8).order(order).asShortBuffer();
    }

    public void testShortBufferByteOrderArray() throws Exception {
        testShortBufferByteOrder(ShortBuffer.allocate(10), ByteOrder.nativeOrder());
    }

    public void testShortBufferByteOrderBE() throws Exception {
        testShortBufferByteOrder(allocateShortBuffer(ByteOrder.BIG_ENDIAN), ByteOrder.BIG_ENDIAN);
    }

    public void testShortBufferByteOrderLE() throws Exception {
        testShortBufferByteOrder(allocateShortBuffer(ByteOrder.LITTLE_ENDIAN), ByteOrder.LITTLE_ENDIAN);
    }

    public void testRelativePositionsHeap() throws Exception {
        testRelativePositions(ByteBuffer.allocate(10));
    }

    public void testRelativePositionsDirect() throws Exception {
        testRelativePositions(ByteBuffer.allocateDirect(10));
    }

    public void testRelativePositionsMapped() throws Exception {
        testRelativePositions(allocateMapped(10));
    }

    // http://b/3291927 - ensure that the relative get and put methods advance 'position'.
    private void testRelativePositions(ByteBuffer b) throws Exception {
        // gets
        b.position(0);
        b.get();
        assertEquals(1, b.position());

        byte[] buf = new byte[5];
        b.position(0);
        b.get(buf);
        assertEquals(5, b.position());

        b.position(0);
        b.get(buf, 1, 3);
        assertEquals(3, b.position());

        b.position(0);
        b.getChar();
        assertEquals(2, b.position());

        b.position(0);
        b.getDouble();
        assertEquals(8, b.position());

        b.position(0);
        b.getFloat();
        assertEquals(4, b.position());

        b.position(0);
        b.getInt();
        assertEquals(4, b.position());

        b.position(0);
        b.getLong();
        assertEquals(8, b.position());

        b.position(0);
        b.getShort();
        assertEquals(2, b.position());

        // puts
        b.position(0);
        b.put((byte) 0);
        assertEquals(1, b.position());

        b.position(0);
        b.put(buf);
        assertEquals(5, b.position());

        b.position(0);
        b.put(buf, 1, 3);
        assertEquals(3, b.position());

        b.position(0);
        b.putChar('x');
        assertEquals(2, b.position());

        b.position(0);
        b.putDouble(0);
        assertEquals(8, b.position());

        b.position(0);
        b.putFloat(0);
        assertEquals(4, b.position());

        b.position(0);
        b.putInt(0);
        assertEquals(4, b.position());

        b.position(0);
        b.putLong(0);
        assertEquals(8, b.position());

        b.position(0);
        b.putShort((short) 0);
        assertEquals(2, b.position());
    }

    // This test will fail on the RI. Our direct buffers are cooler than theirs.
    // http://b/3384431
    public void testDirectByteBufferHasArray() throws Exception {
        ByteBuffer b = ByteBuffer.allocateDirect(10);
        assertTrue(b.isDirect());
        // Check the buffer has an array of the right size.
        assertTrue(b.hasArray());
        assertEquals(0, b.arrayOffset());
        byte[] array = b.array();
        assertEquals(10, array.length);
        // Check that writes to the array show up in the buffer.
        assertEquals(0, b.get(0));
        array[0] = 1;
        assertEquals(1, b.get(0));
        // Check that writes to the buffer show up in the array.
        assertEquals(1, array[0]);
        b.put(0, (byte) 0);
        assertEquals(0, array[0]);
    }

    public void testSliceOffset() throws Exception {
        // Slicing changes the array offset.
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.get();
        ByteBuffer slice = buffer.slice();
        assertEquals(0, buffer.arrayOffset());
        assertEquals(1, slice.arrayOffset());

        ByteBuffer directBuffer = ByteBuffer.allocateDirect(10);
        directBuffer.get();
        ByteBuffer directSlice = directBuffer.slice();
        assertEquals(0, directBuffer.arrayOffset());
        assertEquals(1, directSlice.arrayOffset());
    }

    // http://code.google.com/p/android/issues/detail?id=16184
    public void testPutByteBuffer() throws Exception {
        ByteBuffer dst = ByteBuffer.allocate(10).asReadOnlyBuffer();

        // Can't put into a read-only buffer.
        try {
            dst.put(ByteBuffer.allocate(5));
            fail();
        } catch (ReadOnlyBufferException expected) {
        }

        // Can't put a buffer into itself.
        dst = ByteBuffer.allocate(10);
        try {
            dst.put(dst);
            fail();
        } catch (IllegalArgumentException expected) {
        }

        // Can't put the null ByteBuffer.
        try {
            dst.put((ByteBuffer) null);
            fail();
        } catch (NullPointerException expected) {
        }

        // Can't put a larger source into a smaller destination.
        try {
            dst.put(ByteBuffer.allocate(dst.capacity() + 1));
            fail();
        } catch (BufferOverflowException expected) {
        }

        assertPutByteBuffer(ByteBuffer.allocate(10), ByteBuffer.allocate(8), false);
        assertPutByteBuffer(ByteBuffer.allocate(10), ByteBuffer.allocateDirect(8), false);
        assertPutByteBuffer(ByteBuffer.allocate(10), allocateMapped(8), false);
        assertPutByteBuffer(ByteBuffer.allocate(10), ByteBuffer.allocate(8), true);
        assertPutByteBuffer(ByteBuffer.allocate(10), ByteBuffer.allocateDirect(8), true);
        assertPutByteBuffer(ByteBuffer.allocate(10), allocateMapped(8), true);

        assertPutByteBuffer(ByteBuffer.allocateDirect(10), ByteBuffer.allocate(8), false);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), ByteBuffer.allocateDirect(8), false);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), allocateMapped(8), false);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), ByteBuffer.allocate(8), true);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), ByteBuffer.allocateDirect(8), true);
        assertPutByteBuffer(ByteBuffer.allocateDirect(10), allocateMapped(8), true);
    }

    private void assertPutByteBuffer(ByteBuffer dst, ByteBuffer src, boolean readOnly) {
        // Start 'dst' off as the index-identity pattern.
        for (int i = 0; i < dst.capacity(); ++i) {
            dst.put(i, (byte) i);
        }
        // Deliberately offset the position we'll write to by 1.
        dst.position(1);

        // Make the source more interesting.
        for (int i = 0; i < src.capacity(); ++i) {
            src.put(i, (byte) (16 + i));
        }
        if (readOnly) {
            src = src.asReadOnlyBuffer();
        }

        ByteBuffer dst2 = dst.put(src);
        assertSame(dst, dst2);
        assertEquals(0, src.remaining());
        assertEquals(src.position(), src.capacity());
        assertEquals(dst.position(), src.capacity() + 1);
        for (int i = 0; i < src.capacity(); ++i) {
            assertEquals(src.get(i), dst.get(i + 1));
        }

        // No room for another.
        src.position(0);
        try {
            dst.put(src);
            fail();
        } catch (BufferOverflowException expected) {
        }
    }

    public void testCharBufferSubSequence() throws Exception {
        ByteBuffer b = ByteBuffer.allocateDirect(10).order(ByteOrder.nativeOrder());
        b.putChar('H');
        b.putChar('e');
        b.putChar('l');
        b.putChar('l');
        b.putChar('o');
        b.flip();

        assertEquals("Hello", b.asCharBuffer().toString());

        CharBuffer cb = b.asCharBuffer();
        CharSequence cs = cb.subSequence(0, cb.length());
        assertEquals("Hello", cs.toString());
    }

    public void testHasArrayOnJniDirectByteBuffer() throws Exception {
        // Simulate a call to JNI's NewDirectByteBuffer.
        Class<?> c = Class.forName("java.nio.ReadWriteDirectByteBuffer");
        Constructor<?> ctor = c.getDeclaredConstructor(int.class, int.class);
        ctor.setAccessible(true);
        ByteBuffer bb = (ByteBuffer) ctor.newInstance(0, 0);

        try {
            bb.array();
            fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            bb.arrayOffset();
            fail();
        } catch (UnsupportedOperationException expected) {
        }
        assertFalse(bb.hasArray());
    }
}
