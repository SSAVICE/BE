package teamssavice.ssavice.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.service.BookReadService;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.PaymentFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.global.exception.ExternalApiException;
import teamssavice.ssavice.global.exception.ForbiddenException;
import teamssavice.ssavice.payment.entity.Payment;
import teamssavice.ssavice.payment.service.client.PaymentClient;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelResult;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmResult;
import teamssavice.ssavice.payment.service.dto.PaymentCommand;
import teamssavice.ssavice.payment.service.dto.PaymentModel;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentReadService paymentReadService;

    @Mock
    private PaymentWriteService paymentWriteService;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private BookReadService bookReadService;

    private Users user;
    private Company company;
    private ServiceItem serviceItem;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        ReflectionTestUtils.setField(user, "id", 1L);

        company = CompanyFixture.company(CompanyFixture.account(), AddressFixture.address());
        ReflectionTestUtils.setField(company, "id", 2L);

        serviceItem = ServiceItemFixture.base(company);
        ReflectionTestUtils.setField(serviceItem, "id", 100L);

        payment = PaymentFixture.pending(user, serviceItem);
    }

    @Nested
    @DisplayName("createPendingPayment 메서드")
    class CreatePendingPayment {

        private final PaymentCommand.Prepare command = PaymentCommand.Prepare.builder()
                .userId(1L)
                .serviceItemId(100L)
                .build();

        @Test
        @DisplayName("성공: 중복 예약도 없고 진행 중인 결제도 없으면 PENDING Payment를 생성하고 Prepare 모델을 반환한다")
        void success() {
            // given
            given(bookReadService.existsByUserAndServiceAndStatusNot(1L, 100L, BookStatus.CANCELED))
                    .willReturn(false);
            given(paymentReadService.existsPendingPayment(1L, 100L))
                    .willReturn(false);
            given(paymentWriteService.createPendingPayment(1L, 100L))
                    .willReturn(payment);

            // when
            PaymentModel.Prepare result = paymentService.createPendingPayment(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.orderId()).isEqualTo(payment.getOrderId());
            assertThat(result.orderName()).isEqualTo(serviceItem.getTitle());
            assertThat(result.amount()).isEqualTo(payment.getAmount());
            assertThat(result.customerKey()).isNotBlank();

            verify(paymentWriteService).createPendingPayment(1L, 100L);
        }

        @Test
        @DisplayName("성공: customerKey는 userId 기반으로 결정적으로 생성된다 (동일 userId면 동일 키)")
        void success_customerKeyIsDeterministic() {
            // given
            given(bookReadService.existsByUserAndServiceAndStatusNot(1L, 100L, BookStatus.CANCELED))
                    .willReturn(false);
            given(paymentReadService.existsPendingPayment(1L, 100L))
                    .willReturn(false);
            given(paymentWriteService.createPendingPayment(1L, 100L))
                    .willReturn(payment);

            // when
            PaymentModel.Prepare result1 = paymentService.createPendingPayment(command);
            PaymentModel.Prepare result2 = paymentService.createPendingPayment(command);

            // then
            assertThat(result1.customerKey()).isEqualTo(result2.customerKey());
        }

        @Test
        @DisplayName("실패: 취소가 아닌 기존 예약이 있으면 ConflictException(ALREADY_APPLIED)을 던진다")
        void fail_whenDuplicateBookExists() {
            // given
            given(bookReadService.existsByUserAndServiceAndStatusNot(1L, 100L, BookStatus.CANCELED))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> paymentService.createPendingPayment(command))
                    .isInstanceOf(ConflictException.class)
                    .satisfies(ex -> assertThat(((ConflictException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.ALREADY_APPLIED));

            verify(paymentReadService, never()).existsPendingPayment(any(), any());
            verify(paymentWriteService, never()).createPendingPayment(any(), any());
        }

        @Test
        @DisplayName("실패: 이미 진행 중인 PENDING Payment가 있으면 ConflictException(DUPLICATE_PENDING_PAYMENT)을 던진다")
        void fail_whenDuplicatePendingPaymentExists() {
            // given
            given(bookReadService.existsByUserAndServiceAndStatusNot(1L, 100L, BookStatus.CANCELED))
                    .willReturn(false);
            given(paymentReadService.existsPendingPayment(1L, 100L))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> paymentService.createPendingPayment(command))
                    .isInstanceOf(ConflictException.class)
                    .satisfies(ex -> assertThat(((ConflictException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.DUPLICATE_PENDING_PAYMENT));

            verify(paymentWriteService, never()).createPendingPayment(any(), any());
        }
    }

    @Nested
    @DisplayName("confirmPayment 메서드")
    class ConfirmPayment {

        private static final String PAYMENT_KEY = "toss-payment-key-abc";
        private static final String METHOD = "카드";

        private PaymentCommand.Confirm command(Long userId, String orderId) {
            return PaymentCommand.Confirm.builder()
                    .userId(userId)
                    .paymentKey(PAYMENT_KEY)
                    .orderId(orderId)
                    .build();
        }

        private PaymentConfirmResult approvedResult(String orderId, Long amount) {
            return new PaymentConfirmResult(PAYMENT_KEY, orderId, "DONE", amount, METHOD, "2024-01-01T00:00:00");
        }

        @Test
        @DisplayName("성공: 소유자 검증 통과, Toss 승인 DONE, completePayment 정상 완료")
        void success() {
            // given
            String orderId = payment.getOrderId();
            given(paymentReadService.findByOrderIdWithUserAndServiceItem(orderId))
                    .willReturn(payment);
            given(paymentClient.confirm(any(PaymentConfirmCommand.class)))
                    .willReturn(approvedResult(orderId, payment.getAmount()));
            willDoNothing().given(paymentWriteService).completePayment(orderId, PAYMENT_KEY, METHOD);

            // when
            paymentService.confirmPayment(command(1L, orderId));

            // then
            verify(paymentReadService).findByOrderIdWithUserAndServiceItem(orderId);
            verify(paymentClient).confirm(any(PaymentConfirmCommand.class));
            verify(paymentWriteService).completePayment(orderId, PAYMENT_KEY, METHOD);
        }

        @Test
        @DisplayName("실패: 결제 소유자가 아니면 ForbiddenException(FORBIDDEN)을 던진다")
        void fail_whenNotOwner() {
            // given
            String orderId = payment.getOrderId();
            Long anotherUserId = 999L;
            given(paymentReadService.findByOrderIdWithUserAndServiceItem(orderId))
                    .willReturn(payment);

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(command(anotherUserId, orderId)))
                    .isInstanceOf(ForbiddenException.class)
                    .satisfies(ex -> assertThat(((ForbiddenException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.FORBIDDEN));

            verify(paymentClient, never()).confirm(any());
            verify(paymentWriteService, never()).completePayment(any(), any(), any());
        }

        @Test
        @DisplayName("실패: Toss 승인 응답이 DONE이 아니면 ExternalApiException(PAYMENT_CONFIRM_STATUS_INVALID)을 던진다")
        void fail_whenTossStatusIsNotDone() {
            // given
            String orderId = payment.getOrderId();
            PaymentConfirmResult nonDoneResult =
                    new PaymentConfirmResult(PAYMENT_KEY, orderId, "ABORTED", payment.getAmount(), null, null);
            given(paymentReadService.findByOrderIdWithUserAndServiceItem(orderId))
                    .willReturn(payment);
            given(paymentClient.confirm(any(PaymentConfirmCommand.class)))
                    .willReturn(nonDoneResult);

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(command(1L, orderId)))
                    .isInstanceOf(ExternalApiException.class)
                    .satisfies(ex -> assertThat(((ExternalApiException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.PAYMENT_CONFIRM_STATUS_INVALID));

            verify(paymentWriteService, never()).completePayment(any(), any(), any());
        }

        @Test
        @DisplayName("실패: completePayment 예외 발생 시 Toss 취소와 failPayment 호출 후 원래 예외를 다시 던진다")
        void fail_whenCompletePaymentThrows_thenCancelAndRethrow() {
            // given
            String orderId = payment.getOrderId();
            RuntimeException cause = new RuntimeException("정원 초과");

            given(paymentReadService.findByOrderIdWithUserAndServiceItem(orderId))
                    .willReturn(payment);
            given(paymentClient.confirm(any(PaymentConfirmCommand.class)))
                    .willReturn(approvedResult(orderId, payment.getAmount()));
            willThrow(cause).given(paymentWriteService).completePayment(orderId, PAYMENT_KEY, METHOD);
            given(paymentClient.cancel(any(PaymentCancelCommand.class)))
                    .willReturn(new PaymentCancelResult(PAYMENT_KEY, orderId, "CANCELED"));
            willDoNothing().given(paymentWriteService).failPayment(orderId);

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(command(1L, orderId)))
                    .isSameAs(cause);

            verify(paymentClient).cancel(any(PaymentCancelCommand.class));
            verify(paymentWriteService).failPayment(orderId);
        }

        @Test
        @DisplayName("실패: completePayment 예외 발생 후 Toss 취소도 실패하면 ExternalApiException(TOSS_PAYMENT_CANCEL_FAILED)을 던진다")
        void fail_whenCompletePaymentThrowsAndCancelAlsoFails() {
            // given
            String orderId = payment.getOrderId();
            RuntimeException cause = new RuntimeException("정원 초과");
            RuntimeException cancelException = new RuntimeException("Toss 취소 실패");

            given(paymentReadService.findByOrderIdWithUserAndServiceItem(orderId))
                    .willReturn(payment);
            given(paymentClient.confirm(any(PaymentConfirmCommand.class)))
                    .willReturn(approvedResult(orderId, payment.getAmount()));
            willThrow(cause).given(paymentWriteService).completePayment(orderId, PAYMENT_KEY, METHOD);
            willThrow(cancelException).given(paymentClient).cancel(any(PaymentCancelCommand.class));

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(command(1L, orderId)))
                    .isInstanceOf(ExternalApiException.class)
                    .satisfies(ex -> assertThat(((ExternalApiException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.TOSS_PAYMENT_CANCEL_FAILED));

            verify(paymentWriteService, never()).failPayment(any());
        }
    }

    @Nested
    @DisplayName("failPayment 메서드")
    class FailPayment {

        private PaymentCommand.Fail command(Long userId, String orderId) {
            return PaymentCommand.Fail.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .errorCode("PAY_PROCESS_ABORTED")
                    .errorMessage("사용자 결제 취소")
                    .build();
        }

        @Test
        @DisplayName("성공: 소유자 검증 통과 후 writeService.failPayment를 호출한다")
        void success() {
            // given
            String orderId = payment.getOrderId();
            given(paymentReadService.findByOrderIdWithUserAndServiceItem(orderId))
                    .willReturn(payment);
            willDoNothing().given(paymentWriteService).failPayment(orderId);

            // when
            paymentService.failPayment(command(1L, orderId));

            // then
            verify(paymentReadService).findByOrderIdWithUserAndServiceItem(orderId);
            verify(paymentWriteService).failPayment(orderId);
        }

        @Test
        @DisplayName("실패: 결제 소유자가 아니면 ForbiddenException(FORBIDDEN)을 던진다")
        void fail_whenNotOwner() {
            // given
            String orderId = payment.getOrderId();
            Long anotherUserId = 999L;
            given(paymentReadService.findByOrderIdWithUserAndServiceItem(orderId))
                    .willReturn(payment);

            // when & then
            assertThatThrownBy(() -> paymentService.failPayment(command(anotherUserId, orderId)))
                    .isInstanceOf(ForbiddenException.class)
                    .satisfies(ex -> assertThat(((ForbiddenException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.FORBIDDEN));

            verify(paymentWriteService, never()).failPayment(any());
        }
    }
}
