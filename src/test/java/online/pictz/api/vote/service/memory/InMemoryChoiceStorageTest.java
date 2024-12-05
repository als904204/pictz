package online.pictz.api.vote.service.memory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import online.pictz.api.vote.service.memory.atmoic.AtomicChoiceStorage;
import online.pictz.api.vote.service.memory.sync.SyncChoiceStorageV1;
import online.pictz.api.vote.service.memory.sync.SyncChoiceStorageV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryChoiceStorageTest {

    private AtomicChoiceStorage atomicChoiceStorage = new AtomicChoiceStorage();
    private SyncChoiceStorageV1 syncChoiceStorageV1 = new SyncChoiceStorageV1();
    private SyncChoiceStorageV2 syncChoiceStorageV2 = new SyncChoiceStorageV2();

    @DisplayName("멀티 스레드 atomic 사용")
    @Test
    void atomicVote() throws InterruptedException {
        int threads = 200;

        // 시도할 횟수
        int operationsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        long startAtomic = System.nanoTime();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    atomicChoiceStorage.store(1L, 1);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        long endAtomic = System.nanoTime();

        System.out.println("AtomicChoiceStorage Time: " + (endAtomic - startAtomic) / 1_000_000 + " ms");
        int count = atomicChoiceStorage.getCount(1L);
        System.out.println("count = " + count);
    }

    @DisplayName("멀티 스레드 전체 sync 사용")
    @Test
    void syncVoteV1() throws InterruptedException {
        int threads = 200;

        // 시도할 횟수
        int operationsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        long startAtomic = System.nanoTime();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    syncChoiceStorageV1.store(1L, 1);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        long endAtomic = System.nanoTime();

        System.out.println("SyncChoiceStorageV1 Time: " + (endAtomic - startAtomic) / 1_000_000 + " ms");
        int count = syncChoiceStorageV1.getCount(1L);
        System.out.println("count = " + count);
    }

    @DisplayName("멀티 스레드 부분 sync 사용")
    @Test
    void syncVoteV2() throws InterruptedException {
        int threads = 200;

        // 시도할 횟수
        int operationsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        long startAtomic = System.nanoTime();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    syncChoiceStorageV2.store(1L, 1);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        long endAtomic = System.nanoTime();

        System.out.println("SyncChoiceStorageV2 Time: " + (endAtomic - startAtomic) / 1_000_000 + " ms");
        int count = syncChoiceStorageV2.getCount(1L);
        System.out.println("count = " + count);
    }

}