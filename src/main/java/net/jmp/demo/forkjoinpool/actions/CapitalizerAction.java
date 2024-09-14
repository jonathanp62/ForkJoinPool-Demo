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
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** The character array to capitalize. */
    private final char[] characters;

    /** The starting point in the array. */
    private final int start;

    /** The ending point in the array. */
    private final int end;

    /** The threshold for the creation of subtasks. */
    private final int workloadThreshold;

    /**
     * The constructor.
     *
     * @param   characters          char[]
     * @param   start               int
     * @param   end                 int
     * @param   workloadThreshold   int
     */
    public CapitalizerAction(final char[] characters,
                             final int start,
                             final int end,
                             final int workloadThreshold) {
        super();

        this.characters = Objects.requireNonNull(characters);
        this.start = start;
        this.end = end;
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

        if ((this.end - this.start) < this.workloadThreshold) {
            this.processWorkload();
        } else {
            ForkJoinTask.invokeAll(this.createSubtasks());
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

        final int middle = (this.start + this.end) / 2;

        subtasks.add(new CapitalizerAction(this.characters, this.start, middle, this.workloadThreshold));
        subtasks.add(new CapitalizerAction(this.characters, middle, this.end, this.workloadThreshold));

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

        for (int i = 0; i < this.characters.length; i++) {
            this.characters[i] = Character.toUpperCase(this.characters[i]);

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("{}: {}", this.characters[i], Thread.currentThread().getName());
            }
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
