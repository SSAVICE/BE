package teamssavice.ssavice.serviceItem.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.address.AddressRequest;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.dto.Auth;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.global.exception.ImageSizeException;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.s3.dto.S3Command;
import teamssavice.ssavice.serviceItem.constants.ServiceCategory;
import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemRequest;
import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemResponse;
import teamssavice.ssavice.serviceItem.service.ServiceItemService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceItemControllerTest {

    @InjectMocks
    private ServiceItemController serviceItemController;

    @Mock
    private ServiceItemService serviceItemService;

    @Mock
    private ImageService imageService;

    @Mock
    private S3Service s3Service;

    @Nested
    @DisplayName("createServiceItem 메서드")
    class CreateServiceItem {

        @Test
        @DisplayName("성공: 이미지 검증 성공 후 서비스 등록에 성공한다")
        void 이미지_검증_성공_후_서비스_등록_성공() {
            // given
            Long companyId = 1L;
            Long serviceId = 100L;
            Auth authCompany = new Auth(companyId, Role.COMPANY);

            ServiceItemRequest.Create request = createValidRequest();
            willDoNothing().given(s3Service).validateAllTempImagesOrDeleteAll(any(S3Command.ValidateKeys.class));
            given(serviceItemService.register(any())).willReturn(serviceId);

            // when
            ServiceItemResponse.Register response = serviceItemController.createServiceItem(authCompany, request).getBody();

            // then
            assertThat(response).isNotNull();
            assertThat(response.serviceId()).isEqualTo(serviceId);
            then(s3Service).should().validateAllTempImagesOrDeleteAll(any(S3Command.ValidateKeys.class));
            then(serviceItemService).should().register(any());
        }

        @Test
        @DisplayName("실패: 이미지 검증 실패(EntityNotFoundException) 시 register가 호출되지 않는다")
        void 이미지_검증_실패_EntityNotFoundException() {
            // given
            Long companyId = 1L;
            Auth authCompany = new Auth(companyId, Role.COMPANY);
            ServiceItemRequest.Create request = createValidRequest();

            willThrow(new EntityNotFoundException(ErrorCode.IMAGE_NOT_FOUND))
                    .given(s3Service).validateAllTempImagesOrDeleteAll(any(S3Command.ValidateKeys.class));

            // when & then
            assertThatThrownBy(() -> serviceItemController.createServiceItem(authCompany, request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.IMAGE_NOT_FOUND);

            // verify: register가 호출되지 않음
            then(s3Service).should().validateAllTempImagesOrDeleteAll(any(S3Command.ValidateKeys.class));
            then(serviceItemService).should(never()).register(any());
        }

        @Test
        @DisplayName("실패: 이미지 검증 실패(ImageSizeException) 시 register가 호출되지 않는다")
        void 이미지_검증_실패_ImageSizeException() {
            // given
            Long companyId = 1L;
            Auth authCompany = new Auth(companyId, Role.COMPANY);
            ServiceItemRequest.Create request = createValidRequest();

            willThrow(new ImageSizeException(ErrorCode.IMAGE_TOO_LARGE, 10_000_000L, 5_242_880L))
                    .given(s3Service).validateAllTempImagesOrDeleteAll(any(S3Command.ValidateKeys.class));

            // when & then
            assertThatThrownBy(() -> serviceItemController.createServiceItem(authCompany, request))
                    .isInstanceOf(ImageSizeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.IMAGE_TOO_LARGE);

            // verify: register가 호출되지 않음
            then(s3Service).should().validateAllTempImagesOrDeleteAll(any(S3Command.ValidateKeys.class));
            then(serviceItemService).should(never()).register(any());
        }

        private ServiceItemRequest.Create createValidRequest() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime futureDeadline = now.plusDays(7);
            LocalDateTime futureStartDate = now.plusDays(10);
            LocalDateTime futureEndDate = now.plusDays(20);

            AddressRequest.Region region = new AddressRequest.Region(
                    "1111010100",
                    "03035",
                    "서울특별시 종로구 세종대로 209",
                    "세종문화회관",
                    BigDecimal.valueOf(126.9780),
                    BigDecimal.valueOf(37.5665)
            );

            List<ImageRequest.Confirm> imageConfirms = List.of(
                    new ImageRequest.Confirm("temp/serviceItem/1/uuid1.jpg"),
                    new ImageRequest.Confirm("temp/serviceItem/1/uuid2.jpg")
            );

            return new ServiceItemRequest.Create(
                    2L,
                    ServiceCategory.CULTURE,
                    "가정집 청소 서비스",
                    "깨끗하게 청소해드립니다",
                    2L,
                    5L,
                    100000L,
                    10,
                    90000L,
                    futureDeadline,
                    "청소,깔끔",
                    futureStartDate,
                    futureEndDate,
                    region,
                    imageConfirms
            );
        }
    }
}
