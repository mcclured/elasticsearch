/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.benchmark.counter;

import org.elasticsearch.common.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kimchy (Shay Banon)
 */
public class SimpleCounterBenchmark {

    private static long NUMBER_OF_ITERATIONS = 10000000;
    private static int NUMBER_OF_THREADS = 100;

    public static void main(String[] args) throws Exception {
        final AtomicLong counter = new AtomicLong();
        StopWatch stopWatch = new StopWatch().start();
        System.out.println("Running " + NUMBER_OF_ITERATIONS);
        for (long i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            counter.incrementAndGet();
        }
        System.out.println("Took " + stopWatch.stop().totalTime() + " TP Millis " + (NUMBER_OF_ITERATIONS / stopWatch.totalTime().millisFrac()));

        System.out.println("Running using " + NUMBER_OF_THREADS + " threads with " + NUMBER_OF_ITERATIONS + " iterations");
        final CountDownLatch latch = new CountDownLatch(NUMBER_OF_THREADS);
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override public void run() {
                    for (long i = 0; i < NUMBER_OF_ITERATIONS; i++) {
                        counter.incrementAndGet();
                    }
                    latch.countDown();
                }
            });
        }
        stopWatch = new StopWatch().start();
        for (Thread thread : threads) {
            thread.start();
        }
        latch.await();
        stopWatch.stop();
        System.out.println("Took " + stopWatch.totalTime() + " TP Millis " + ((NUMBER_OF_ITERATIONS * NUMBER_OF_THREADS) / stopWatch.totalTime().millisFrac()));
    }
}
