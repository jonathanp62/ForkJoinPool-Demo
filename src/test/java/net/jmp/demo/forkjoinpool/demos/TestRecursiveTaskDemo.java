package net.jmp.demo.forkjoinpool.demos;

/*
 * (#)TestRecursiveTaskDemo.java    0.3.0   09/14/2024
 *
 * @author   Jonathan Parker
 * @version  0.3.0
 * @since    0.3.0
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

import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Test;

public final class TestRecursiveTaskDemo {
    @Test
    public void testSumTask() throws Exception {
        final var demo = new RecursiveTaskDemo();
        final var method = RecursiveTaskDemo.class.getDeclaredMethod("sumTask");

        method.setAccessible(true);

        final Object o = method.invoke(demo);
        final Integer result = (Integer) o;

        assertNotNull(result);
        assertEquals(500_500, (long) result);
    }

    @Test
    public void testLetterDistributionTask() throws Exception {
        final var demo = new RecursiveTaskDemo();
        final var method = RecursiveTaskDemo.class.getDeclaredMethod("letterDistributionTask");

        method.setAccessible(true);

        final Object o = method.invoke(demo);

        @SuppressWarnings("unchecked")
        final Map<Character, Integer> results = (Map<Character, Integer>) o;

        assertNotNull(results);
        assertEquals(12, results.entrySet().size());
        assertEquals(1, (long) results.get('a'));
        assertEquals(2, (long) results.get('b'));
        assertEquals(3, (long) results.get('c'));
        assertEquals(4, (long) results.get('d'));
        assertEquals(5, (long) results.get('e'));
        assertEquals(6, (long) results.get('f'));
        assertEquals(7, (long) results.get('g'));
        assertEquals(8, (long) results.get('h'));
        assertEquals(9, (long) results.get('i'));
        assertEquals(10, (long) results.get('j'));
        assertEquals(11, (long) results.get('k'));
        assertEquals(12, (long) results.get('l'));
    }
}
