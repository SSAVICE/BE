package teamssavice.ssavice.book.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.constants.BookViewStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.fixture.*;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(QueryDSLConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

class BookRepositoryImplTest {

    private final List<ServiceItem> serviceItems = new ArrayList<>();
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private TestEntityManager tem;
    private Users user;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        Company company = CompanyFixture.company(user, AddressFixture.address());
        ServiceItem recruitingService = ServiceItemFixture.recruiting(company);
        ServiceItem succeededService = ServiceItemFixture.succeeded(company);
        ServiceItem fulledService = ServiceItemFixture.fulled(company);
        ServiceItem inUseService = ServiceItemFixture.inUse(company);
        ServiceItem completeService = ServiceItemFixture.completed(company);
        ServiceItem failService = ServiceItemFixture.failed(company);
        ServiceItem canceledService = ServiceItemFixture.canceled(company);

        tem.persist(user);
        tem.persist(company);
        tem.persist(recruitingService);
        tem.persist(succeededService);
        tem.persist(fulledService);
        tem.persist(inUseService);
        tem.persist(completeService);
        tem.persist(failService);
        tem.persist(canceledService);

        serviceItems.add(recruitingService);
        serviceItems.add(succeededService);
        serviceItems.add(fulledService);
        serviceItems.add(inUseService);
        serviceItems.add(completeService);
        serviceItems.add(failService);
        serviceItems.add(canceledService);

        Book recruitingBook = BookFixture.book(user, recruitingService, BookStatus.RESERVED);
        Book succeededBook = BookFixture.book(user, succeededService, BookStatus.RESERVED);
        Book fulledBook = BookFixture.book(user, fulledService, BookStatus.RESERVED);
        Book inUseBook = BookFixture.book(user, inUseService, BookStatus.RESERVED);
        Book completeBook = BookFixture.book(user, completeService, BookStatus.RESERVED);
        Book failBook = BookFixture.book(user, failService, BookStatus.RESERVED);
        Book userCancelBook = BookFixture.book(user, canceledService, BookStatus.RESERVED);
        Book serviceCancelBook = BookFixture.book(user, recruitingService, BookStatus.CANCELED);

        tem.persist(recruitingBook);
        tem.persist(succeededBook);
        tem.persist(fulledBook);
        tem.persist(inUseBook);
        tem.persist(completeBook);
        tem.persist(failBook);
        tem.persist(userCancelBook);
        tem.persist(serviceCancelBook);
    }

    @Test
    @DisplayName("BookStatusFilter가 ALL일 때 findAllByUserIdAndStatus() 테스트")
    void findAllByUserIdAndStatusTestWhenAll() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(),
            BookStatusFilter.ALL, pageable);
        Page<BookModel.Info> actualModels =
            actuals.map(book -> BookModel.Info.from(book, null));

        // then
        assertThat(actualModels.getTotalElements()).isEqualTo(8);
    }

    @Test
    @DisplayName("BookStatusFilter가 RECRUITING일 때 findAllByUserIdAndStatus() 테스트")
    void findAllByUserIdAndStatusTestWhenRecruiting() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(),
            BookStatusFilter.RECRUITING, pageable);
        Page<BookModel.Info> actualModels =
            actuals.map(book -> BookModel.Info.from(book, null));
        // then
        assertThat(actualModels.getTotalElements()).isEqualTo(1);
        for (BookModel.Info model : actualModels) {
            assertThat(model.bookStatus()).isEqualTo(BookViewStatus.RECRUITING);
        }
    }

    @Test
    @DisplayName("BookStatusFilter가 SUCCEEDED일 때 findAllByUserIdAndStatus() 테스트")
    void findAllByUserIdAndStatusTestWhenSucceeded() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(),
            BookStatusFilter.SUCCEEDED, pageable);
        Page<BookModel.Info> actualModels =
            actuals.map(book -> BookModel.Info.from(book, null));
        // then
        assertThat(actualModels.getTotalElements()).isEqualTo(3);
        for (BookModel.Info model : actualModels) {
            assertThat(model.bookStatus()).isIn(BookViewStatus.SUCCEEDED, BookViewStatus.FULLED,
                BookViewStatus.IN_USE);
        }
    }

    @Test
    @DisplayName("BookStatusFilter가 COMPLETE일 때 findAllByUserIdAndStatus() 테스트")
    void findAllByUserIdAndStatusTestWhenComplete() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(),
            BookStatusFilter.COMPLETED, pageable);
        Page<BookModel.Info> actualModels =
            actuals.map(book -> BookModel.Info.from(book, null));
        // then
        assertThat(actualModels.getTotalElements()).isEqualTo(1);
        for (BookModel.Info model : actualModels) {
            assertThat(model.bookStatus()).isEqualTo(BookViewStatus.COMPLETED);
        }
    }

    @Test
    @DisplayName("BookStatusFilter가 CANCELED일 때 findAllByUserIdAndStatus() 테스트")
    void findAllByUserIdAndStatusTestWhenCanceled() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(),
            BookStatusFilter.CANCELED, pageable);
        Page<BookModel.Info> actualModels =
            actuals.map(book -> BookModel.Info.from(book, null));
        // then
        assertThat(actualModels.getTotalElements()).isEqualTo(3);
        for (BookModel.Info model : actualModels) {
            assertThat(model.bookStatus()).isIn(BookViewStatus.FAILED, BookViewStatus.USER_CANCELED,
                BookViewStatus.SERVICE_CANCELED);
        }
    }

    @Test
    @DisplayName("서비스별 마지막 예약 조회 테스트")
    void findLatestBooksByUserIdAndServiceItemIdTest() {
        // given
        tem.persist(BookFixture.book(user, serviceItems.get(0), BookStatus.CANCELED));
        tem.persist(BookFixture.book(user, serviceItems.get(3), BookStatus.CANCELED));
        tem.persist(BookFixture.book(user, serviceItems.get(5), BookStatus.CANCELED));
        List<Long> serviceIds = serviceItems.stream().map(ServiceItem::getId).toList();
        tem.flush();
        tem.clear();

        // when
        List<Book> actuals = bookRepository.findLatestBooksByUserIdAndServiceItemId(user.getId(),
            serviceIds);
        Map<Long, BookStatus> map = new HashMap<>();
        for (Book actual : actuals) {
            map.put(actual.getServiceItem().getId(), actual.getBookStatus());
        }
        // then
        assertAll(
            () -> assertThat(actuals.size()).isEqualTo(serviceItems.size()),
            () -> assertThat(map.get(1L)).isEqualTo(BookStatus.CANCELED),
            () -> assertThat(map.get(2L)).isEqualTo(BookStatus.RESERVED),
            () -> assertThat(map.get(3L)).isEqualTo(BookStatus.RESERVED),
            () -> assertThat(map.get(4L)).isEqualTo(BookStatus.CANCELED),
            () -> assertThat(map.get(5L)).isEqualTo(BookStatus.RESERVED),
            () -> assertThat(map.get(6L)).isEqualTo(BookStatus.CANCELED),
            () -> assertThat(map.get(7L)).isEqualTo(BookStatus.RESERVED)
        );
    }

    @Test
    @DisplayName("findAllByServiceItemIdWithUserAndImageResource - 서비스 아이템 ID와 예약 상태로 참가자 목록을 페이징 조회한다")
    void findAllByServiceItemIdWithUserAndImageResource_success() {
        // given
        Users owner = UserFixture.of(user.getUserRole(), "참가자서비스소유자",
            "participant-owner@test.com", "010-9999-0000");
        Company participantCompany = CompanyFixture.company(owner, AddressFixture.address());
        ServiceItem participantServiceItem = ServiceItemFixture.recruiting(participantCompany);
        tem.persist(owner);
        tem.persist(participantCompany);
        tem.persist(participantServiceItem);

        Users participant1 = UserFixture.of(user.getUserRole(), "참가자1", "p1@test.com",
            "010-1111-1111");
        Users participant2 = UserFixture.of(user.getUserRole(), "참가자2", "p2@test.com",
            "010-2222-2222");
        tem.persist(participant1);
        tem.persist(participant2);

        Book book1 = BookFixture.book(participant1, participantServiceItem, BookStatus.RESERVED);
        Book book2 = BookFixture.book(participant2, participantServiceItem, BookStatus.RESERVED);
        tem.persist(book1);
        tem.persist(book2);

        tem.flush();
        tem.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Book> result = bookRepository.findAllByServiceItemIdWithUserAndImageResource(
            participantServiceItem.getId(), BookStatus.RESERVED, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
            .extracting(book -> book.getUser().getName())
            .containsExactlyInAnyOrder("참가자1", "참가자2");
    }

    @Test
    @DisplayName("findAllByServiceItemIdWithUserAndImageResource - 취소된 예약은 조회되지 않는다")
    void findAllByServiceItemIdWithUserAndImageResource_excludesCanceledBookings() {
        // given
        Users owner = UserFixture.of(user.getUserRole(), "참가자서비스소유자",
            "participant-owner@test.com", "010-9999-0000");
        Company participantCompany = CompanyFixture.company(owner, AddressFixture.address());
        ServiceItem participantServiceItem = ServiceItemFixture.recruiting(participantCompany);
        tem.persist(owner);
        tem.persist(participantCompany);
        tem.persist(participantServiceItem);

        Users participant1 = UserFixture.of(user.getUserRole(), "예약자", "reserved@test.com",
            "010-1111-1111");
        Users participant2 = UserFixture.of(user.getUserRole(), "취소자", "canceled@test.com",
            "010-2222-2222");
        tem.persist(participant1);
        tem.persist(participant2);

        Book reservedBook = BookFixture.book(participant1, participantServiceItem,
            BookStatus.RESERVED);
        Book canceledBook = BookFixture.book(participant2, participantServiceItem,
            BookStatus.CANCELED);
        tem.persist(reservedBook);
        tem.persist(canceledBook);

        tem.flush();
        tem.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Book> result = bookRepository.findAllByServiceItemIdWithUserAndImageResource(
            participantServiceItem.getId(), BookStatus.RESERVED, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUser().getName()).isEqualTo("예약자");
    }

    @Test
    @DisplayName("findAllByServiceItemIdWithUserAndImageResource - 예약이 없으면 빈 페이지를 반환한다")
    void findAllByServiceItemIdWithUserAndImageResource_returnsEmptyPageWhenNoBookings() {
        // given
        Users owner = UserFixture.of(user.getUserRole(), "참가자서비스소유자",
            "participant-owner@test.com", "010-9999-0000");
        Company participantCompany = CompanyFixture.company(owner, AddressFixture.address());
        ServiceItem participantServiceItem = ServiceItemFixture.recruiting(participantCompany);
        tem.persist(owner);
        tem.persist(participantCompany);
        tem.persist(participantServiceItem);

        tem.flush();
        tem.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Book> result = bookRepository.findAllByServiceItemIdWithUserAndImageResource(
            participantServiceItem.getId(), BookStatus.RESERVED, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("findAllByServiceItemIdWithUserAndImageResource - 페이징이 정상적으로 동작한다")
    void findAllByServiceItemIdWithUserAndImageResource_paginationWorks() {
        // given
        Users owner = UserFixture.of(user.getUserRole(), "참가자서비스소유자",
            "participant-owner@test.com", "010-9999-0000");
        Company participantCompany = CompanyFixture.company(owner, AddressFixture.address());
        ServiceItem participantServiceItem = ServiceItemFixture.recruiting(participantCompany);
        tem.persist(owner);
        tem.persist(participantCompany);
        tem.persist(participantServiceItem);

        for (int i = 0; i < 15; i++) {
            Users participant = UserFixture.of(user.getUserRole(), "참가자" + i,
                "p" + i + "@test.com", "010-0000-000" + i);
            tem.persist(participant);
            Book book = BookFixture.book(participant, participantServiceItem, BookStatus.RESERVED);
            tem.persist(book);
        }

        tem.flush();
        tem.clear();

        Pageable firstPage = PageRequest.of(0, 10);
        Pageable secondPage = PageRequest.of(1, 10);

        // when
        Page<Book> firstResult = bookRepository.findAllByServiceItemIdWithUserAndImageResource(
            participantServiceItem.getId(), BookStatus.RESERVED, firstPage);
        Page<Book> secondResult = bookRepository.findAllByServiceItemIdWithUserAndImageResource(
            participantServiceItem.getId(), BookStatus.RESERVED, secondPage);

        // then
        assertThat(firstResult.getContent()).hasSize(10);
        assertThat(firstResult.getTotalElements()).isEqualTo(15);
        assertThat(firstResult.getTotalPages()).isEqualTo(2);

        assertThat(secondResult.getContent()).hasSize(5);
    }

    @Test
    @DisplayName("findAllByServiceItemIdWithUserAndImageResource - User와 ImageResource가 함께 조회된다")
    void findAllByServiceItemIdWithUserAndImageResource_fetchJoinsUserAndImageResource() {
        // given
        Users owner = UserFixture.of(user.getUserRole(), "참가자서비스소유자",
            "participant-owner@test.com", "010-9999-0000");
        Company participantCompany = CompanyFixture.company(owner, AddressFixture.address());
        ServiceItem participantServiceItem = ServiceItemFixture.recruiting(participantCompany);
        tem.persist(owner);
        tem.persist(participantCompany);
        tem.persist(participantServiceItem);

        ImageResource imageResource = ImageResourceFixture.imageResource();
        tem.persist(imageResource);

        Users participantWithImage = UserFixture.of(user.getUserRole(), "이미지있는참가자",
            "img@test.com", "010-9999-9999");
        participantWithImage.updateImage(imageResource);
        tem.persist(participantWithImage);

        Book book = BookFixture.book(participantWithImage, participantServiceItem,
            BookStatus.RESERVED);
        tem.persist(book);

        tem.flush();
        tem.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Book> result = bookRepository.findAllByServiceItemIdWithUserAndImageResource(
            participantServiceItem.getId(), BookStatus.RESERVED, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        Book fetchedBook = result.getContent().get(0);
        assertThat(fetchedBook.getUser().getName()).isEqualTo("이미지있는참가자");
        assertThat(fetchedBook.getUser().hasImageResource()).isTrue();
    }

    @Test
    @DisplayName("findAllByServiceItemIdWithUserAndImageResource - 다른 서비스 아이템의 예약은 조회되지 않는다")
    void findAllByServiceItemIdWithUserAndImageResource_onlyReturnsBookingsForSpecificServiceItem() {
        // given
        Users owner = UserFixture.of(user.getUserRole(), "참가자서비스소유자",
            "participant-owner@test.com", "010-9999-0000");
        Company participantCompany = CompanyFixture.company(owner, AddressFixture.address());
        ServiceItem participantServiceItem = ServiceItemFixture.recruiting(participantCompany);
        tem.persist(owner);
        tem.persist(participantCompany);
        tem.persist(participantServiceItem);

        ServiceItem anotherServiceItem = ServiceItemFixture.recruiting(participantCompany);
        tem.persist(anotherServiceItem);

        Users participant1 = UserFixture.of(user.getUserRole(), "서비스1참가자", "s1@test.com",
            "010-1111-1111");
        Users participant2 = UserFixture.of(user.getUserRole(), "서비스2참가자", "s2@test.com",
            "010-2222-2222");
        tem.persist(participant1);
        tem.persist(participant2);

        Book bookForServiceItem = BookFixture.book(participant1, participantServiceItem,
            BookStatus.RESERVED);
        Book bookForAnotherServiceItem = BookFixture.book(participant2, anotherServiceItem,
            BookStatus.RESERVED);
        tem.persist(bookForServiceItem);
        tem.persist(bookForAnotherServiceItem);

        tem.flush();
        tem.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Book> result = bookRepository.findAllByServiceItemIdWithUserAndImageResource(
            participantServiceItem.getId(), BookStatus.RESERVED, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUser().getName()).isEqualTo("서비스1참가자");
    }
}