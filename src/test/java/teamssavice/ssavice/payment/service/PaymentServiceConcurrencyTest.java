package teamssavice.ssavice.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import teamssavice.ssavice.payment.service.client.PaymentClient;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelResult;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmResult;
import teamssavice.ssavice.payment.service.dto.PaymentCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class PaymentServiceConcurrencyTest {

    private final List<Payment> payments = new ArrayList<>();
    private final List<Users> users = new ArrayList<>();
    @Autowired
    private PaymentService paymentService;
    @MockitoBean
    private PaymentClient paymentClient;
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
        users.clear();

        transactionTemplate.execute(status -> {
            Account companyAccount = accountRepository.save(CompanyFixture.account());
            Company company = companyRepository.save(CompanyFixture.company(companyAccount));

            serviceItem = ServiceItemFixture.base(company).toBuilder()
                .currentMember(0L)
                .minimumMember(1L)
                .maximumMember(5L)
                .build();
            serviceItem = serviceItemRepository.save(serviceItem);

            for (int i = 0; i < 10; i++) {
                Users user = UserFixture.of(
                    teamssavice.ssavice.user.constants.UserRole.USER,
                    "user" + i,
                    "user" + i + "@test.com",
                    "010-0000-00" + String.format("%02d", i)
                );
                accountRepository.save(user.getAccount());
                userRepository.save(user);
                users.add(user);

                Payment payment = paymentWriteService.createPendingPayment(user.getId(), serviceItem.getId());
                payments.add(payment);
            }

            return null;
        });
    }

    @Test
    @DisplayName("동시에 10명이 confirmPayment 호출 시 5명만 성공하고 나머지 5명은 Toss 취소 후 FAILED 상태가 된다")
    void confirmPayment_concurrency_test() throws InterruptedException {
        // given - PaymentClient mock 설정
        given(paymentClient.confirm(any(PaymentConfirmCommand.class)))
            .willAnswer(invocation -> {
                PaymentConfirmCommand cmd = invocation.getArgument(0);
                return new PaymentConfirmResult(
                    cmd.paymentKey(), cmd.orderId(), "DONE", cmd.amount(), "카드", "2024-01-01T00:00:00"
                );
            });
        given(paymentClient.cancel(any(PaymentCancelCommand.class)))
            .willAnswer(invocation -> {
                PaymentCancelCommand cmd = invocation.getArgument(0);
                return new PaymentCancelResult(cmd.paymentKey(), "", "CANCELED");
            });

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
                    Users user = users.get(index);
                    PaymentCommand.Confirm command = PaymentCommand.Confirm.builder()
                        .userId(user.getId())
                        .paymentKey("paymentKey_" + index)
                        .orderId(payment.getOrderId())
                        .build();
                    paymentService.confirmPayment(command);
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
        long failedCount = paymentRepository.findAll().stream()
            .filter(p -> p.getStatus() == PaymentStatus.FAILED)
            .count();
        long bookCount = bookRepository.count();

        assertAll(
            () -> assertThat(successCount.get()).isEqualTo(5),
            () -> assertThat(failCount.get()).isEqualTo(5),
            () -> assertThat(actual.getCurrentMember()).isEqualTo(5L),
            () -> assertThat(approvedCount).isEqualTo(5L),
            () -> assertThat(failedCount).isEqualTo(5L),
            () -> assertThat(bookCount).isEqualTo(5L),
            () -> verify(paymentClient, times(10)).confirm(any(PaymentConfirmCommand.class)),
            () -> verify(paymentClient, times(5)).cancel(any(PaymentCancelCommand.class))
        );
    }
}
