package org.viepik.sandbox.palindrome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class PalindromeApplication {
    private AtomicLong minTime = new AtomicLong(Integer.MAX_VALUE);
    private AtomicLong maxTime = new AtomicLong(0);
    private AtomicLong tasksRun = new AtomicLong(0);
    private AtomicLong duration = new AtomicLong(0);
    private ConcurrentHashMap<Long, String> result = new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private PalindromeCalculatorImpl calculator = new PalindromeCalculatorImpl();
    private long timeToWait;

    private PalindromeApplication(int numberOfThreads, int timeToWait) {
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.timeToWait = timeToWait * 1000;
    }

    public static void main(String[] args) {
        String numberOfThreads = "8";
        String timeToWait = "30";
        for (String arg : args) {
            if (arg.contains("-DnumberOfThreads=")) {
                numberOfThreads = arg.split("=")[1];
            }
            if (arg.contains("-DtimeToWait=")) {
                timeToWait = arg.split("=")[1];
            }
        }
        PalindromeApplication application = new PalindromeApplication(Integer.valueOf(numberOfThreads),
                Integer.valueOf(timeToWait));
        application.start();
    }

    private void start() {
        processResult(new PalindromeCalculationResult(-1, 1L, new Long[]{3L}));
        try {
            Thread.sleep(timeToWait);
            executorService.shutdownNow();
            ArrayList<Long> keys = new ArrayList<>(result.keySet());
            Collections.sort(keys);
            StringBuilder sb = new StringBuilder("Palindromes:\n");
            for (Long key : keys) {
                sb.append("decimal:").append(key).append(" binary:").append(result.get(key)).append("\n");
            }
            sb.append(String.format("Performance (millis) max: %f, min: %f, mean: %f",
                    maxTime.floatValue() / 1000000f, minTime.floatValue() / 1000000f,
                    duration.floatValue() / tasksRun.get() / 1000000f)).append("\n");
            sb.append("Tasks run: ").append(tasksRun.toString()).append("\n");
            sb.append(String.format("Duration: %f millis", (duration.floatValue() / 1000000f))).append("\n");
            System.out.println(sb.toString());
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processResult(PalindromeCalculationResult calculationResult) {
        tasksRun.incrementAndGet();
        duration.addAndGet(calculationResult.calcTime > 0 ? calculationResult.calcTime : 0);
        minTime.accumulateAndGet(calculationResult.calcTime, (left, right) -> right < left && right > 0 ? right : left);
        maxTime.accumulateAndGet(calculationResult.calcTime, (left, right) -> left < right ? right : left);
        if (calculationResult.palindrome != null) {
            result.putIfAbsent(calculationResult.palindrome, Long.toBinaryString(calculationResult.palindrome));
        }
        for (Long palindrome : calculationResult.nextToCheck) {
            CompletableFuture.supplyAsync(new PalindromeSupplier<PalindromeCalculationResult>(palindrome,
                    calculator), executorService).thenAccept(this::processResult);
        }
    }

    private static class PalindromeSupplier<T> implements Supplier<T> {
        private PalindromeCalculator calculator;
        private Long palindrome;

        PalindromeSupplier(Long palindrome, PalindromeCalculator calculator) {
            this.palindrome = palindrome;
            this.calculator = calculator;
        }

        @Override
        public T get() {
            return (T) calculator.calculatePalindrome(palindrome);
        }
    }
}
