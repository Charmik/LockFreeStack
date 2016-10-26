package com.lovkov;

import com.sun.tools.javac.util.Assert;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Main {


    private static int numberAdd = 1000;
    private static int numberOfThreads = 100;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        LockFreeStack<Integer> stack = new LockFreeStack<>();

        CountDownLatch latchForAdd = new CountDownLatch(numberOfThreads);
        CountDownLatch latchForPop = new CountDownLatch(numberOfThreads);

        Runnable runnableAdd = () -> {
            try {
                latchForAdd.countDown();
                latchForAdd.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < numberAdd; i++) {
                stack.add(i);
            }
        };

        Thread threads[] = new Thread[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(runnableAdd);
            threads[i].start();
        }

        long answer = (((long) numberAdd - 1) * (numberAdd / 2)) * numberOfThreads;

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        Callable<Long> callable = () -> {
            long sum = 0;
            latchForPop.countDown();
            latchForPop.await();
            for (; ; ) {
                try {
                    int element = stack.pop();
                    sum += element;
                } catch (NoSuchElementException e) {
                    break;
                }
            }
            return sum;
        };

        FutureTask threadsTasks[] = new FutureTask[numberOfThreads];

        for (int i = 0; i < threadsTasks.length; i++) {
            FutureTask<Long> futureTask = new FutureTask<>(callable);
            threadsTasks[i] = futureTask;
            threads[i] = new Thread(futureTask);
            threads[i].start();
        }
        long sum = 0;

        for (int i = 0; i < threadsTasks.length; i++) {
            Object o = threadsTasks[i].get();
            long t = (long) o;
            sum += t;

        }

        Assert.check(answer == sum);

        System.out.println("answer=" + answer + "   sum=" + sum);

    }
}
