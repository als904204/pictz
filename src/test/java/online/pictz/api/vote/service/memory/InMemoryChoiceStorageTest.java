package online.pictz.api.vote.service.memory;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import online.pictz.api.vote.service.memory.atmoic.AtomicChoiceStorage;
import online.pictz.api.vote.service.memory.sync.SyncChoiceStorageV0;
import online.pictz.api.vote.service.memory.sync.SyncChoiceStorageV1;
import online.pictz.api.vote.service.memory.sync.SyncChoiceStorageV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryChoiceStorageTest {

    private AtomicChoiceStorage atomicChoiceStorage = new AtomicChoiceStorage();
    private SyncChoiceStorageV0 syncChoiceStorageV0 = new SyncChoiceStorageV0();
    private SyncChoiceStorageV1 syncChoiceStorageV1 = new SyncChoiceStorageV1();
    private SyncChoiceStorageV2 syncChoiceStorageV2 = new SyncChoiceStorageV2();

    @DisplayName("멀티 스레드 atomic 사용")
    @Test
    void atomicVote() throws InterruptedException {
        int users = 200;

        // 시도할 횟수
        int numberOfAttempts = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(users);
        CountDownLatch latch = new CountDownLatch(users);

        long startAtomic = System.nanoTime();

        for (int i = 0; i < users; i++) {
            executor.submit(() -> {
                for (int j = 0; j < numberOfAttempts; j++) {
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
        int users = 200;

        // 시도할 횟수
        int numberOfAttempts = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(users);
        CountDownLatch latch = new CountDownLatch(users);

        long startAtomic = System.nanoTime();

        for (int i = 0; i < users; i++) {
            executor.submit(() -> {
                for (int j = 0; j < numberOfAttempts; j++) {
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
        int users = 200;

        // 시도할 횟수
        int numberOfAttempts = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(users);
        CountDownLatch latch = new CountDownLatch(users);

        long startAtomic = System.nanoTime();

        for (int i = 0; i < users; i++) {
            executor.submit(() -> {
                for (int j = 0; j < numberOfAttempts; j++) {
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

    @DisplayName("어떠한 동기화도 사용하지 않아 동시성 문제가 발생한다")
    @Test
    void syncVoteV0() throws InterruptedException {

        // 유저 수
        int users = 200;

        // 시도할 횟수
        int numberOfAttempts = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(users);
        CountDownLatch latch = new CountDownLatch(users);

        for (int i = 0; i < users; i++) {
            executor.submit(() -> {
                for (int j = 0; j < numberOfAttempts; j++) {
                    syncChoiceStorageV0.store(1L, 1);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        int result = syncChoiceStorageV0.getCount(1L);

        assertThat(result).isNotEqualTo(users * numberOfAttempts);
    }

}