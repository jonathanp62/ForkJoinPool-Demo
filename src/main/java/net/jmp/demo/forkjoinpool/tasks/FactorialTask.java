package net.jmp.demo.forkjoinpool.tasks;

/*
 * (#)FactorialTask.java    0.3.0   09/16/2024
 *
 * @author    Jonathan Parker
 * @version   0.3.0
 * @since     0.3.0
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

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static net.jmp.demo.forkjoinpool.util.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task class that computes a factorial.
 */
public final class FactorialTask extends RecursiveTask<BigInteger> {
    /** The logger. */
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** The 'from' value. */
    private final int from;

    /** The 'to' value. */
    private final int to;

    /**
     * The constructor.
     *
     * @param   from    int
     * @param   to      int
     */
    public FactorialTask(final int from, final int to) {
        super();

        this.from = from;
        this.to = to;
    }

    /**
     * The main computation performed by this task.
     *
     * @return  java.math.BigInteger
     */
    @Override
    protected BigInteger compute() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        BigInteger result;

        final int range = this.to - this.from;

        switch (range) {
            case 0 -> result = BigInteger.valueOf(this.from);
            case 1 -> result = BigInteger.valueOf(this.from).multiply(BigInteger.valueOf(this.to));
            default -> {
                final int middle = this.from + range / 2;

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Creating subtasks: {}", Thread.currentThread().getName());
                }

                final FactorialTask left = new FactorialTask(this.from, middle);

                left.fork();                        // Execute using the ForkJoinPool.commonPool()

                result = new FactorialTask(middle + 1, this.to)
                        .compute()                  // Compute the right side ...
                        .multiply(left.join());     // After waiting for the left side
            }
        }

//        if (range == 0) {
//            result = BigInteger.valueOf(this.from);
//        } else if (range == 1) {
//            result = BigInteger.valueOf(this.from).multiply(BigInteger.valueOf(this.to));
//        } else {
//            final int middle = this.from + range / 2;
//
//            final FactorialTask left = new FactorialTask(this.from, middle);
//
//            left.fork();
//
//            result = new FactorialTask(middle + 1, this.to)
//                    .compute()
//                    .multiply(left.join());
//        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }
}
