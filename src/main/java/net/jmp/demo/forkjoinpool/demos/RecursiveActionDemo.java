package net.jmp.demo.forkjoinpool.demos;

/*
 * (#)RecursiveActionDemo.java  0.2.0   09/14/2024
 * (#)RecursiveActionDemo.java  0.1.0   09/14/2024
 *
 * @author   Jonathan Parker
 * @version  0.2.0
 * @since    0.1.0
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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import net.jmp.demo.forkjoinpool.actions.CapitalizerAction;
import net.jmp.demo.forkjoinpool.actions.SquareRootAction;

import static net.jmp.demo.forkjoinpool.util.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class the demonstrates the recursive action.
 */
public class RecursiveActionDemo implements Demo {
    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * The default constructor.
     */
    public RecursiveActionDemo() {
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
            this.logger.info(this.capitalizerAction());

            final double[] results = this.squareRootAction();

            for (final double result : results) {
                this.logger.info("{}", String.format("%.4f ", result));
            }
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /**
     * Demonstrate the capitalizer action
     * using execute. Execute does not
     * return a ForkJoinTask so there is
     * nothing to wait on.
     *
     * @return  java.lang.String
     */
    private String capitalizerAction() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        final String sentences = "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
                "aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint " +
                "occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                "id est laborum.";

        final char[] characters = sentences.toCharArray();

        try (final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {
            forkJoinPool.execute(new CapitalizerAction(characters, 0, characters.length, 32));

            try {
                Thread.sleep(200);
            } catch (final InterruptedException ie) {
                this.logger.error(ie.getMessage(), ie);
                Thread.currentThread().interrupt();
            }
        }

        final String result = new String(characters);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }

    /**
     * Demonstrate the square root action
     * using submit. Submit does return
     * a ForkJoinTask so there is something
     * to wait on.
     *
     * @return  double[]
     */
    private double[] squareRootAction() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        final double[] doubles = new double[100_000];

        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = i;
        }

        try (final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {
            final ForkJoinTask<Void> task = forkJoinPool.submit(new SquareRootAction(doubles, 0, doubles.length));

            task.join();
        }

        final double[] results = new double[10];

        System.arraycopy(doubles, 0, results, 0, results.length);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(results));
        }

        return results;
    }
}
