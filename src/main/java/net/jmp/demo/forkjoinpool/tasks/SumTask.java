package net.jmp.demo.forkjoinpool.tasks;

/*
 * (#)CapitalizerAction.java    0.4.0   09/24/2024
 * (#)CapitalizerAction.java    0.2.0   09/14/2024
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static net.jmp.util.logging.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task class that sums numbers in an array of integers.
 */
public final class SumTask extends RecursiveTask<Integer> {
    /** The logger. */
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** The array of integers to sum. */
    private final int[] integers;

    /** The workload threshold. */
    private final int workloadThreshold = 20;

    /**
     * The constructor.
     *
     * @param   integers    int[]
     */
    public SumTask(final int[] integers) {
        super();

        this.integers = Objects.requireNonNull(integers);
    }

    /**
     * The main computation performed by this task.
     *
     * @return  java.lang.Integer
     */
    @Override
    protected Integer compute() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        int result;

        if (this.integers.length > this.workloadThreshold) {
            result = ForkJoinTask.invokeAll(this.createSubtasks())
                    .stream()
                    .mapToInt(ForkJoinTask::join)
                    .sum();
        } else {
            result = this.processWorkload();
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }

    /**
     * Return a list of subtasks.
     *
     * @return  java.util.List&lt;net.jmp.demo.forkjoinpool.tasks.SumTask&gt;
     */
    private List<SumTask> createSubtasks() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating subtasks: {}", Thread.currentThread().getName());
        }

        final List<SumTask> subtasks = new ArrayList<>();

        final SumTask left = new SumTask(Arrays.copyOfRange(this.integers, 0, this.integers.length / 2));
        final SumTask right = new SumTask(Arrays.copyOfRange(this.integers, this.integers.length / 2, this.integers.length));

        subtasks.add(left);
        subtasks.add(right);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(subtasks));
        }

        return subtasks;
    }

    /**
     * Process the workload by summing the
     * integers in the array.
     *
     * @return  java.lang.Integer
     */
    private Integer processWorkload() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        final int result = Arrays.stream(this.integers).sum();

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Done - {}: {}", result, Thread.currentThread().getName());
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }
}
