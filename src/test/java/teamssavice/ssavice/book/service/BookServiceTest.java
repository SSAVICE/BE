package teamssavice.ssavice.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.BookFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.ImageResourceFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ForbiddenException;
import teamssavice.ssavice.imageresource.constants.ImageConstants;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import teamssavice.ssavice.user.entity.Users;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookReadService bookReadService;

    @Mock
    private ServiceItemReadService serviceItemReadService;

    @Mock
    private S3Service s3Service;

    private Users user;
    private Company company;
    private ServiceItem serviceItem;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        ReflectionTestUtils.setField(user, "id", 1L);

        company = CompanyFixture.company(user, AddressFixture.address());
        ReflectionTestUtils.setField(company, "id", 1L);

        serviceItem = ServiceItemFixture.base(company);
        ReflectionTestUtils.setField(serviceItem, "id", 100L);
    }

    @Test
    @DisplayName("사용자의 예약 요약 정보 조회 시, 각 상태별 카운트가 정확히 합산되어 반환된다")
    void getBookSummary_test() {
        // given
        Long userId = 1L;
        Long recruitingCount = 5L;
        Long completedCount = 6L;

        given(bookReadService.countRecruitingBooksByUserId(userId)).willReturn(recruitingCount);
        given(bookReadService.countSucceededBooksByUserId(userId)).willReturn(completedCount);

        // when
        BookModel.BookSummary result = bookService.getBookSummary(userId);

        // then
        assertThat(result.applying()).isEqualTo(recruitingCount);
        assertThat(result.completed()).isEqualTo(completedCount);
    }

    @Nested
    @DisplayName("getParticipants 메서드")
    class GetParticipants {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        @DisplayName("성공: 서비스 소유자가 참가자 목록을 조회한다")
        void success() {
            // given
            Long companyId = company.getId();
            Long serviceItemId = serviceItem.getId();
            String presignedUrl = "https://s3.amazonaws.com/bucket/profile/user1.png?presigned";

            ImageResource imageResource = ImageResourceFixture.imageResource();
            ReflectionTestUtils.setField(user, "imageResource", imageResource);

            Book book = BookFixture.book(user, serviceItem, BookStatus.RESERVED);
            ReflectionTestUtils.setField(book, "id", 10L);

            given(serviceItemReadService.findById(serviceItemId)).willReturn(serviceItem);
            given(bookReadService.findAllParticipantsByServiceItemId(serviceItemId, pageable))
                    .willReturn(new PageImpl<>(List.of(book), pageable, 1));
            given(s3Service.generateGetPresignedUrl(imageResource.getObjectKey())).willReturn(presignedUrl);

            // when
            Page<BookModel.Participant> participants = bookService.getParticipants(companyId, serviceItemId, pageable);

            // then
            assertThat(participants.getContent()).hasSize(1);
            assertThat(participants.getTotalElements()).isEqualTo(1);

            BookModel.Participant participant = participants.getContent().get(0);
            assertThat(participant.bookId()).isEqualTo(10L);
            assertThat(participant.userId()).isEqualTo(user.getId());
            assertThat(participant.name()).isEqualTo(user.getName());
            assertThat(participant.thumbnailUrl()).isEqualTo(presignedUrl);
        }

        @Test
        @DisplayName("성공: 예약이 없으면 빈 목록을 반환한다")
        void success_emptyList() {
            // given
            Long companyId = company.getId();
            Long serviceItemId = serviceItem.getId();

            given(serviceItemReadService.findById(serviceItemId)).willReturn(serviceItem);
            given(bookReadService.findAllParticipantsByServiceItemId(serviceItemId, pageable))
                    .willReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

            // when
            Page<BookModel.Participant> participants = bookService.getParticipants(companyId, serviceItemId, pageable);

            // then
            assertThat(participants.getContent()).isEmpty();
            assertThat(participants.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("성공: 프로필 이미지가 있는 사용자는 presigned URL을 반환한다")
        void success_withProfileImage() {
            // given
            Long companyId = company.getId();
            Long serviceItemId = serviceItem.getId();
            String presignedUrl = "https://s3.amazonaws.com/bucket/profile/user1-thumbnail.png?presigned";

            ImageResource imageResource = ImageResourceFixture.imageResource();
            ReflectionTestUtils.setField(user, "imageResource", imageResource);

            Book book = BookFixture.book(user, serviceItem, BookStatus.RESERVED);
            ReflectionTestUtils.setField(book, "id", 10L);

            given(serviceItemReadService.findById(serviceItemId)).willReturn(serviceItem);
            given(bookReadService.findAllParticipantsByServiceItemId(serviceItemId, pageable))
                    .willReturn(new PageImpl<>(List.of(book), pageable, 1));
            given(s3Service.generateGetPresignedUrl(imageResource.getObjectKey())).willReturn(presignedUrl);

            // when
            Page<BookModel.Participant> participants = bookService.getParticipants(companyId, serviceItemId, pageable);

            // then
            assertThat(participants.getContent()).hasSize(1);
            assertThat(participants.getContent().get(0).thumbnailUrl()).isEqualTo(presignedUrl);
            verify(s3Service).generateGetPresignedUrl(imageResource.getObjectKey());
        }

        @Test
        @DisplayName("성공: 프로필 이미지가 없는 사용자는 기본 이미지를 반환한다")
        void success_withoutProfileImage() {
            // given
            Long companyId = company.getId();
            Long serviceItemId = serviceItem.getId();
            String defaultPresignedUrl = "https://s3.amazonaws.com/bucket/profile/default.png?presigned";

            // user는 imageResource가 null (UserFixture.user()는 imageResource(null))
            Book book = BookFixture.book(user, serviceItem, BookStatus.RESERVED);
            ReflectionTestUtils.setField(book, "id", 20L);

            given(serviceItemReadService.findById(serviceItemId)).willReturn(serviceItem);
            given(bookReadService.findAllParticipantsByServiceItemId(serviceItemId, pageable))
                    .willReturn(new PageImpl<>(List.of(book), pageable, 1));
            given(s3Service.generateGetPresignedUrl(ImageConstants.DEFAULT_PROFILE_IMAGE_OBJECT_KEY))
                    .willReturn(defaultPresignedUrl);

            // when
            Page<BookModel.Participant> participants = bookService.getParticipants(companyId, serviceItemId, pageable);

            // then
            assertThat(participants.getContent()).hasSize(1);
            assertThat(participants.getContent().get(0).thumbnailUrl()).isEqualTo(defaultPresignedUrl);
            verify(s3Service).generateGetPresignedUrl(ImageConstants.DEFAULT_PROFILE_IMAGE_OBJECT_KEY);
        }

        @Test
        @DisplayName("실패: 서비스 소유자가 아니면 ForbiddenException을 던진다")
        void fail_whenNotServiceOwner() {
            // given
            Long differentCompanyId = 999L;
            Long serviceItemId = serviceItem.getId();

            given(serviceItemReadService.findById(serviceItemId)).willReturn(serviceItem);

            // when & then
            assertThatThrownBy(() -> bookService.getParticipants(differentCompanyId, serviceItemId, pageable))
                    .isInstanceOf(ForbiddenException.class)
                    .satisfies(exception -> {
                        ForbiddenException forbiddenException = (ForbiddenException) exception;
                        assertThat(forbiddenException.getErrorCode()).isEqualTo(ErrorCode.NOT_SERVICE_OWNER);
                    });

            verify(bookReadService, never()).findAllParticipantsByServiceItemId(serviceItemId, pageable);
        }
    }
}
