package teamssavice.ssavice.book.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.account.infrastructure.repository.AccountRepository;
import teamssavice.ssavice.book.infrastructure.repository.BookRepository;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.outbox.infrastructure.repository.OutboxEventRepository;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class BookServiceConcurrencyTest {

    @Autowired private BookService bookService;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private ServiceItemRepository serviceItemRepository;
    @Autowired private OutboxEventRepository outboxEventRepository;
    @Autowired private TransactionTemplate transactionTemplate;

    private ServiceItem serviceItem;
    private final List<Users> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        outboxEventRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        serviceItemRepository.deleteAllInBatch();
        companyRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();

        users.clear();

        transactionTemplate.execute(status -> {
            Account companyAccount = accountRepository.save(CompanyFixture.account());
            Company company = companyRepository.save(CompanyFixture.company(companyAccount));

            serviceItem = ServiceItemFixture.base(company).toBuilder()
                    .currentMember(0L)
                    .minimumMember(5L)
                    .maximumMember(10L)
                    .build();
            serviceItem = serviceItemRepository.save(serviceItem);

            for (int i = 0; i < 20; i++) {
                Users user = UserFixture.of(
                        UserRole.USER,
                        "user" + i,
                        "user" + i + "@test.com",
                        "010-0000-" + String.format("%04d", i)
                );
                accountRepository.save(user.getAccount());
                userRepository.save(user);
                users.add(user);
            }

            return null;
        });
    }

    @Test
    @DisplayName("동시에 20명이 apply 호출 시 최대 인원(10명)만 성공하고 나머지는 실패한다")
    void apply_concurrency_max_member_test() throws InterruptedException {
        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executorService.submit(() -> {
                try {
                    bookService.apply(users.get(index).getId(), serviceItem.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        ServiceItem actual = serviceItemRepository.findById(serviceItem.getId()).orElseThrow();
        long bookCount = bookRepository.count();

        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(10),
                () -> assertThat(failCount.get()).isEqualTo(10),
                () -> assertThat(actual.getCurrentMember()).isEqualTo(10L),
                () -> assertThat(actual.getStatus()).isEqualTo(ServiceStatus.FULLED),
                () -> assertThat(bookCount).isEqualTo(10L)
        );
    }

    @Test
    @DisplayName("동시에 5명이 apply 호출 시 최소 인원 충족되면 상태가 SUCCEEDED로 변경된다")
    void apply_concurrency_min_member_status_transition_test() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executorService.submit(() -> {
                try {
                    bookService.apply(users.get(index).getId(), serviceItem.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // ignore
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        ServiceItem actual = serviceItemRepository.findById(serviceItem.getId()).orElseThrow();

        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(5),
                () -> assertThat(actual.getCurrentMember()).isEqualTo(5L),
                () -> assertThat(actual.getStatus()).isEqualTo(ServiceStatus.SUCCEEDED)
        );
    }
}
