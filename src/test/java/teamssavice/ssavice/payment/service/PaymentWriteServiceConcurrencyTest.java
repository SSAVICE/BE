package teamssavice.ssavice.payment.service;

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
import teamssavice.ssavice.payment.entity.Payment;
import teamssavice.ssavice.payment.entity.PaymentStatus;
import teamssavice.ssavice.payment.infrastructure.repository.PaymentRepository;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;
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
class PaymentWriteServiceConcurrencyTest {

    private final List<Payment> payments = new ArrayList<>();
    @Autowired
    private PaymentWriteService paymentWriteService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ServiceItemRepository serviceItemRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;
    private ServiceItem serviceItem;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        serviceItemRepository.deleteAllInBatch();
        companyRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();

        payments.clear();

        transactionTemplate.execute(status -> {
            Account companyAccount = accountRepository.save(CompanyFixture.account());
            Company company = companyRepository.save(CompanyFixture.company(companyAccount));

            // maximumMember=5, currentMember=0
            serviceItem = ServiceItemFixture.base(company).toBuilder()
                .currentMember(0L)
                .minimumMember(1L)
                .maximumMember(5L)
                .build();
            serviceItem = serviceItemRepository.save(serviceItem);

            // 10명의 유저가 각각 PENDING Payment를 가지고 있는 상태
            for (int i = 0; i < 10; i++) {
                Users user = UserFixture.of(
                    teamssavice.ssavice.user.constants.UserRole.USER,
                    "user" + i,
                    "user" + i + "@test.com",
                    "010-0000-00" + String.format("%02d", i)
                );
                accountRepository.save(user.getAccount());
                userRepository.save(user);

                Payment payment = paymentWriteService.createPendingPayment(user.getId(), serviceItem.getId());
                payments.add(payment);
            }

            return null;
        });
    }

    @Test
    @DisplayName("동시에 10명이 completePayment 호출 시 maximumMember(5)만큼만 성공한다")
    void completePayment_concurrency_test() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executorService.submit(() -> {
                try {
                    Payment payment = payments.get(index);
                    paymentWriteService.completePayment(
                        payment.getOrderId(),
                        "paymentKey_" + index,
                        "카드"
                    );
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

        // then
        ServiceItem actual = serviceItemRepository.findById(serviceItem.getId()).orElseThrow();
        long approvedCount = paymentRepository.findAll().stream()
            .filter(p -> p.getStatus() == PaymentStatus.APPROVED)
            .count();
        long bookCount = bookRepository.count();

        assertAll(
            () -> assertThat(successCount.get()).isEqualTo(5),
            () -> assertThat(failCount.get()).isEqualTo(5),
            () -> assertThat(actual.getCurrentMember()).isEqualTo(5L),
            () -> assertThat(approvedCount).isEqualTo(5L),
            () -> assertThat(bookCount).isEqualTo(5L)
        );
    }
}
