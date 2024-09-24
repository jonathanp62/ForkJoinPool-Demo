package net.jmp.demo.forkjoinpool.actions;

/*
 * (#)SquareRootAction.java 0.4.0   09/24/2024
 * (#)SquareRootAction.java 0.2.0   09/14/2024
 *
 * @author    Jonathan Parker
 * @version   0.4.0
 * @since     0.2.0
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

import java.util.Objects;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import static net.jmp.util.logging.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action class that computes the square
 * roots of an array of doubles.
 */
public final class SquareRootAction extends RecursiveAction {
    /** The logger. */
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** The threshold for the creation of subtasks. */
    private final int workloadThreshold = 1_000;

    /** The array of doubles to transform to their square roots. */
    private final double[] data;

    /** The starting point in the array. */
    private final int start;

    /** The ending point in the array. */
    private final int end;

    public SquareRootAction(double[] data, int start, int end) {
        super();

        this.data = Objects.requireNonNull(data);
        this.start = start;
        this.end = end;
    }

    /**
     * The main computation performed by this task.
     */
    @Override
    protected void compute() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        /*
         * If the number of elements are less
         * than the workload threshold then
         * operate on the workload
         */

        if ((this.end - this.start) < this.workloadThreshold) {
            for (int i = this.start; i < this.end; i++) {
                this.data[i] = Math.sqrt(data[i]);
            }

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Done: {}", Thread.currentThread().getName());
            }
        } else {
            /*
             * Otherwise, continue to break the
             * data into halves using a midpoint
             */

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Creating subtasks: {}", Thread.currentThread().getName());
            }

            final int middle = (this.start + this.end) / 2;

            ForkJoinTask.invokeAll(new SquareRootAction(this.data, this.start, middle),
                    new SquareRootAction(this.data, middle, this.end));
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
