package net.jmp.demo.forkjoinpool.demos;

/*
 * (#)RecursiveTaskDemo.java    0.4.0   09/24/2024
 * (#)RecursiveTaskDemo.java    0.3.0   09/14/2024
 *
 * @author   Jonathan Parker
 * @version  0.4.0
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

import java.math.BigInteger;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import net.jmp.demo.forkjoinpool.tasks.FactorialTask;
import net.jmp.demo.forkjoinpool.tasks.LetterDistributionTask;
import net.jmp.demo.forkjoinpool.tasks.SumTask;

import static net.jmp.util.logging.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class the demonstrates the recursive task.
 */
public final class RecursiveTaskDemo implements Demo {
    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * The default constructor.
     */
    public RecursiveTaskDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        if (this.logger.isInfoEnabled()) {
            this.logger.info("Sum: {}", this.sumTask());
            this.logger.info("Letters: {}", this.letterDistributionTask());
            this.logger.info("16!: {}", this.factorialTask());
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /**
     * Demonstrate the task that sums
     * the integers in an array.
     *
     * @return  int
     */
    private int sumTask() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        int result;

        final int[] integers = new int[1_000];

        for (int i = 0; i < 1_000; i++) {
            integers[i] = i + 1;
        }

        try (final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {
            final ForkJoinTask<Integer> task = forkJoinPool.submit(new SumTask(integers));

            result = task.join();
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }

    /**
     * Demonstrate the task that counts
     * the occurrences of each letter in
     * an array of characters.
     *
     * @return  java.util.Map&lt;java.lang.Character, java.lang.Integer&gt;
     */
    private Map<Character, Integer> letterDistributionTask() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        final Map<Character, Integer> letterDistribution = new ConcurrentHashMap<>();

        final String test = "abBcCcdDdDeEeEefFfFfFgGgGgGghHhHhHhHiIiIiIiIijJjJjJjJjJkKkKkKkKkKklLlLlLlLlLlL";

        try (final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {
            forkJoinPool.invoke(new LetterDistributionTask(test.toCharArray(), letterDistribution));
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(letterDistribution));
        }

        return letterDistribution;
    }

    /**
     * Demonstrate the factorial task.
     *
     * @return  java.math.BigInteger
     */
    private BigInteger factorialTask() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        // Uses the ForkJoinPool

        final BigInteger result = new FactorialTask(1, 16).invoke();

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }
}
