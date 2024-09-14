package net.jmp.demo.forkjoinpool.demos;

/*
 * (#)TestRecursiveActionDemo.java  0.2.0   09/14/2024
 *
 * @author   Jonathan Parker
 * @version  0.2.0
 * @since    0.2.0
 *
 * MIT License
 *
 * Copyright (c) 2024 Jonathan M. Parker
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public final class TestRecursiveActionDemo {
    @Test
    public void testSquareRootAction() throws Exception {
        final var demo = new RecursiveActionDemo();
        final var method = RecursiveActionDemo.class.getDeclaredMethod("squareRootAction");

        method.setAccessible(true);

        final Object o = method.invoke(demo);
        final double[] results = (double[]) o;

        assertNotNull(results);
        assertEquals(10, results.length);
        assertEquals(0, results[0], 0.001);
        assertEquals(1, results[1], 0.001);
        assertEquals(1.4142, results[2], 0.001);
        assertEquals(1.7321, results[3], 0.001);
        assertEquals(2, results[4], 0.001);
        assertEquals(2.2361, results[5], 0.001);
        assertEquals(2.4495, results[6], 0.001);
        assertEquals(2.6458, results[7], 0.001);
        assertEquals(2.8284, results[8], 0.001);
        assertEquals(3, results[9], 0.001);
    }
}
