package teamssavice.ssavice.wish.infrastructure.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.wish.entity.Wish;
import teamssavice.ssavice.wish.infrastructure.WishRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(QueryDSLConfig.class)
class WishRepositoryConstraintTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private TestEntityManager tem;

    @Test
    @DisplayName("존재하지 않는 userId로 저장 시 FK 제약 조건 이름 확인")
    void failByNonExistentUserId() {

        Users owner = UserFixture.user();
        tem.persist(owner); // 업체 사장님

        Company company = CompanyFixture.company(owner, AddressFixture.address());
        tem.persist(company);

        ServiceItem serviceItem = ServiceItemFixture.recruiting(company);
        tem.persist(serviceItem);

        tem.flush(); // 여기까지 정상 데이터 DB 반영

        Users nonExistentUser = tem.getEntityManager().getReference(Users.class, 9999L);

        Wish wish = Wish.create(nonExistentUser, serviceItem);

        DataIntegrityViolationException ex = assertThrows(DataIntegrityViolationException.class, () -> {
            wishRepository.save(wish);
            tem.flush();
        });

        String errorMessage = ex.getMessage() != null ? ex.getMessage().toUpperCase() : "";
        System.out.println(errorMessage);

        assertThat(errorMessage).containsAnyOf("FK_WISH_USER", "USER_ID");

    }

    @Test
    @DisplayName("존재하지 않는 serviceItemId로 저장 시 FK 제약 조건 이름 확인")
    void failByNonExistentServiceItemId() {
        // 1. Given: 정상적인 유저는 미리 저장
        Users user = UserFixture.user();
        tem.persist(user);
        tem.flush();

        // 2. 존재하지 않는 서비스 아이템 프록시 생성
        ServiceItem nonExistentItem = tem.getEntityManager()
                .getReference(ServiceItem.class, 99999L); // DB에 절대 없을 ID

        Wish wish = Wish.create(user, nonExistentItem);

        // 3. When & Then: 저장 시 제약 조건 위반 발생 확인
        DataIntegrityViolationException ex = assertThrows(DataIntegrityViolationException.class, () -> {
            wishRepository.save(wish);
            tem.flush();
        });

        String errorMessage = ex.getMessage() != null ? ex.getMessage().toUpperCase() : "";
        System.out.println(errorMessage);

        assertThat(errorMessage).containsAnyOf("FK_WISH_ITEM", "SERVICE_ITEM_ID");
    }

    @Test
    @DisplayName("동일한 유저가 동일한 상품을 중복 찜할 경우 Unique 제약 조건 위반 확인")
    void failByDuplicateWish() {
        // 1. Given: 정상적인 유저와 서비스 아이템 저장
        Users user = UserFixture.user();
        tem.persist(user);

        Company company = CompanyFixture.company(user, AddressFixture.address());
        tem.persist(company);

        ServiceItem serviceItem = ServiceItemFixture.recruiting(company);
        tem.persist(serviceItem);
        tem.flush();

        // 2. 첫 번째 찜 저장
        wishRepository.save(Wish.create(user, serviceItem));
        tem.flush();

        // 3. When & Then: 동일한 정보로 두 번째 찜 저장 시 예외 발생 확인
        DataIntegrityViolationException ex = assertThrows(DataIntegrityViolationException.class, () -> {
            wishRepository.save(Wish.create(user, serviceItem));
            tem.flush();
        });

        String errorMessage = ex.getMessage() != null ? ex.getMessage().toUpperCase() : "";
        System.out.println(errorMessage);

        assertThat(errorMessage).contains("UK_WISH_USER_SERVICE");
    }
}