package teamssavice.ssavice.book.infrastructure.repository;

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
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private TestEntityManager tem;

    private Users user;
    private final List<ServiceItem> serviceItems = new ArrayList<>();

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
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(), BookStatusFilter.ALL, pageable);
        Page<BookModel.Info> actualModels = actuals.map(BookModel.Info::from);

        // then
        assertThat(actualModels.getTotalElements()).isEqualTo(8);
    }

    @Test
    @DisplayName("BookStatusFilter가 RECRUITING일 때 findAllByUserIdAndStatus() 테스트")
    void findAllByUserIdAndStatusTestWhenRecruiting() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(), BookStatusFilter.RECRUITING, pageable);
        Page<BookModel.Info> actualModels = actuals.map(BookModel.Info::from);

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
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(), BookStatusFilter.SUCCEEDED, pageable);
        Page<BookModel.Info> actualModels = actuals.map(BookModel.Info::from);

        // then
        assertThat(actualModels.getTotalElements()).isEqualTo(3);
        for (BookModel.Info model : actualModels) {
            assertThat(model.bookStatus()).isIn(BookViewStatus.SUCCEEDED, BookViewStatus.FULLED, BookViewStatus.IN_USE);
        }
    }

    @Test
    @DisplayName("BookStatusFilter가 COMPLETE일 때 findAllByUserIdAndStatus() 테스트")
    void findAllByUserIdAndStatusTestWhenComplete() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(), BookStatusFilter.COMPLETED, pageable);
        Page<BookModel.Info> actualModels = actuals.map(BookModel.Info::from);

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
        Page<Book> actuals = bookRepository.findAllByUserIdAndStatus(user.getId(), BookStatusFilter.CANCELED, pageable);
        Page<BookModel.Info> actualModels = actuals.map(BookModel.Info::from);

        // then
        assertThat(actualModels.getTotalElements()).isEqualTo(3);
        for (BookModel.Info model : actualModels) {
            assertThat(model.bookStatus()).isIn(BookViewStatus.FAILED, BookViewStatus.USER_CANCELED, BookViewStatus.SERVICE_CANCELED);
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
        List<Book> actuals = bookRepository.findLatestBooksByUserIdAndServiceItemId(user.getId(), serviceIds);
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
}