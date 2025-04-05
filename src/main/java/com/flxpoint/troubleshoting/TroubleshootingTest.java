package com.flxpoint.troubleshoting;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
@Slf4j
public class TroubleshootingTest {
    private static int cpuCount = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(cpuCount - 1);
    private static final ExecutorService infiniteLoopExecutor = Executors.newSingleThreadExecutor();
    private static final Map<String, Integer> sharedData = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        TroubleshootingTest test = new TroubleshootingTest();
        test.runTest();
    }

    public void runTest() {
        List<Future<Integer>> results = new ArrayList<>();
        results.add(executor.submit(() -> updateSharedData("key1")));
        results.add(executor.submit(() -> updateSharedData("key1")));

        results.add(executor.submit(() -> causeNullPointer()));

        results.add(executor.submit(() -> parseInteger("ABC")));

        Object lock1 = new Object();
        Object lock2 = new Object();
        executor.submit(() -> deadlockMethod(lock1, lock2));
        executor.submit(() -> deadlockMethod(lock2, lock1));

        executor.submit(() -> missingMethod());

        String num = "100";

        executor.submit(() -> {
            try {
                methodThrowsException();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                System.err.println("An Exception occured: " + e.getMessage());
            }
        });
        infiniteLoopExecutor.submit(this::infiniteLoop);

        executor.submit(this::unclosedScanner);

        executor.shutdown();
        infiniteLoopExecutor.shutdown();
    }

    private void missingMethod() {}

    private Integer updateSharedData(String key) {
        return sharedData.merge(key, 1, Integer::sum);
    }

    private Integer causeNullPointer() {
        String str = null;
        return Optional.ofNullable(str)
                .map(s -> s.length())
                .orElse(0);
    }

    private Integer parseInteger(String value) {
        return Optional.ofNullable(value)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .filter(s -> s.matches("-?\\d+"))
                .map(Integer::parseInt)
                .orElse(null);
    }

    private void deadlockMethod(Object lock1, Object lock2) {
        int hashA = System.identityHashCode(lock1);
        int hashB = System.identityHashCode(lock2);

        Object firstLock = hashA <= hashB ? lock1 : lock2;
        Object secondLock = hashA <= hashB ? lock2 : lock1;

        synchronized (firstLock) {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            synchronized (secondLock) {
                System.out.println("Acquired both locks");
            }
        }
    }

    private void infiniteLoop() {
        while (true) { }
    }

    private void unclosedScanner() {
        Scanner scanner = new Scanner(System.in);;
        try {
            System.out.println("Enter something: ");
            String input = scanner.nextLine();
            System.out.println("You entered: " + input);
        }catch(NoSuchElementException | IllegalStateException e) {
            log.error(e.getMessage(), e);
        }finally {
            scanner.close();
        }
    }

    private void methodThrowsException() throws Exception {
        throw new Exception("Test Exception");
    }
}