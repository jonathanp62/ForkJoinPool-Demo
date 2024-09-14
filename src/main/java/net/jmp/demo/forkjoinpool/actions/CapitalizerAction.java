package net.jmp.demo.forkjoinpool.actions;

/*
 * (#)CapitalizerAction.java    0.2.0   09/14/2024
 *
 * @author    Jonathan Parker
 * @version   0.2.0
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
import java.util.List;
import java.util.Objects;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import static net.jmp.demo.forkjoinpool.util.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action class that capitalizes a string.
 */
public final class CapitalizerAction extends RecursiveAction {
    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** The string to capitalize. */
    private final String string;

    /** The threshold for the creation of subtasks. */
    private final int workloadThreshold;

    /**
     * The constructor.
     *
     * @param   string              java.lang.String
     * @param   workloadThreshold   int
     */
    public CapitalizerAction(final String string, final int workloadThreshold) {
        super();

        this.string = Objects.requireNonNull(string);
        this.workloadThreshold = workloadThreshold;
    }

    /**
     * The main computation performed by this task.
     */
    @Override
    protected void compute() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        if (this.string.length() > this.workloadThreshold) {
            ForkJoinTask.invokeAll(this.createSubtasks());
        } else {
            this.processWorkload();
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /**
     * Create a list of subtasks and return them.
     *
     * @return  java.util.List&lt;net.jmp.demo.forkjoinpool.actions.CapitalizerAction&gt;
     */
    private List<CapitalizerAction> createSubtasks() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        final List<CapitalizerAction> subtasks = new ArrayList<>();

        final String left = this.string.substring(0, this.string.length() / 2);
        final String right = this.string.substring(this.string.length() / 2);

        subtasks.add(new CapitalizerAction(left, this.workloadThreshold));
        subtasks.add(new CapitalizerAction(right, this.workloadThreshold));

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(subtasks));
        }

        return subtasks;
    }

    /**
     * Process the workload, i.e. the string.
     */
    private void processWorkload() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        final String result = this.string.toUpperCase();

        if (this.logger.isInfoEnabled()) {
            this.logger.info("{}: {}", result, Thread.currentThread().getName());
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
