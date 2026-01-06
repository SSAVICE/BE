package teamssavice.ssavice.user.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.fixture.ImageResourceFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.infrastructure.repository.ImageResourceRepository;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDSLConfig.class, UserWriteService.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserWriteServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageResourceRepository imageResourceRepository;
    @Autowired
    EntityManager em;

    private Users user;
    private ImageResource imageResource;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        imageResource = ImageResourceFixture.imageResource();
    }

    @Test
    @DisplayName("이미지 active 테스트")
    void updateProfileImageTest() {
        // given
        Users user = userRepository.save(this.user);
        ImageResource imageResource = imageResourceRepository.save(this.imageResource);
        boolean originalActive = imageResource.isActive();

        // when
        user.updateImage(imageResource);
        em.flush();
        em.clear();
        var actual = imageResourceRepository.findById(imageResource.getId()).get();

        // then
        assertThat(originalActive).isFalse();
        assertThat(actual.isActive()).isTrue();

    }
}