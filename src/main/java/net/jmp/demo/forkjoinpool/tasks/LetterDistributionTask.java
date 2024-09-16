package net.jmp.demo.forkjoinpool.tasks;

/*
 * (#)LetterDistributionTask.java   0.3.0   09/15/2024
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

import com.google.common.util.concurrent.Striped;

import java.util.*;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import java.util.stream.Stream;

import static net.jmp.demo.forkjoinpool.util.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task class that counts the distribution of
 * letters in an array of characters. The return
 * type is Void so this really should be an action
 * and not a task.
 */
public final class LetterDistributionTask extends RecursiveTask<Void> {
    /** The logger. */
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** The array of characters to process. */
    private final char[] characters;

    /** The workload threshold. */
    private final int workloadThreshold = 5;

    /** The map of letter distributions. */
    private final Map<Character, Integer> letters;

    /** Control access to the individual key/value entries in the map. */
    private final transient Striped<ReadWriteLock> locks = Striped.readWriteLock(26);

    /**
     * The constructor.
     *
     * @param   characters  char[]
     * @param   letters     java.util.Map&lt;java.lang.Character, java.lang.Integer&gt;
     */
    public LetterDistributionTask(final char[] characters, final Map<Character, Integer> letters) {
        super();

        this.characters = Objects.requireNonNull(characters);
        this.letters = Objects.requireNonNull(letters);
    }

    /**
     * The main computation performed by this task.
     *
     * @return  java.lang.Void
     */
    @Override
    protected Void compute() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        if (this.characters.length > this.workloadThreshold) {
            ForkJoinTask.invokeAll(this.createSubtasks());
        } else {
            this.processWorkload();
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(null));
        }

        return null;
    }

    /**
     * Return a list of subtasks.
     *
     * @return  java.util.List&lt;net.jmp.demo.forkjoinpool.tasks.LetterDistributionTask&gt;
     */
    private List<LetterDistributionTask> createSubtasks() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating subtasks: {}", Thread.currentThread().getName());
        }

        final List<LetterDistributionTask> subtasks = new ArrayList<>();

        final LetterDistributionTask left = new LetterDistributionTask(Arrays.copyOfRange(this.characters, 0, this.characters.length / 2), this.letters);
        final LetterDistributionTask right = new LetterDistributionTask(Arrays.copyOfRange(this.characters, this.characters.length / 2, this.characters.length), this.letters);

        subtasks.add(left);
        subtasks.add(right);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(subtasks));
        }

        return subtasks;
    }

    /**
     * Process the workload.
     */
    private void processWorkload() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        final Stream<Character> stream = new String(this.characters)
                .chars()
                .mapToObj(i -> (char) i);

        stream.filter(Character::isLetter)
                .forEach(character -> {
                    final char letter = Character.toLowerCase(character);
                    final Lock lock = this.locks.get(letter).writeLock();

                    lock.lock();

                    try {
                        this.letters.merge(letter, 1, Integer::sum);
                    } finally {
                        lock.unlock();
                    }
                });

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
