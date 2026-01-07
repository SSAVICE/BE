package teamssavice.ssavice.user.controller.dto;

import java.sql.Timestamp;
import lombok.Builder;
import teamssavice.ssavice.user.service.dto.UserModel;

public class UserResponse {

    @Builder
    public record Login(
        String accessToken,
        Long expiresIn,
        String refreshToken
    ) {

        public static Login from(UserModel.Login model) {
            return Login.builder()
                .accessToken(model.accessToken())
                .expiresIn(model.expiresIn())
                .refreshToken(model.refreshToken())
                .build();
        }
    }

    @Builder
    public record Info(
        String imageUrl,
        String name,
        Timestamp createdAt,
        String email,
        String phoneNumber,
        String postCode,
        String address,
        String detailAddress
    ) {

        public static Info from(UserModel.Info model) {
            return Info.builder()
                .imageUrl(model.imageUrl())
                .name(model.name())
                .createdAt(model.createdAt())
                .email(model.email())
                .phoneNumber(model.phoneNumber())
                .postCode(model.postCode())
                .address(model.address())
                .detailAddress(model.detailAddress())
                .build();
        }
    }

    @Builder
    public record Summary(
        String name,
        String email,
        String phoneNumber,
        String postCode,
        String address,
        String detailAddress
    ) {

        public static Summary from(UserModel.Modify model) {
            return Summary.builder()
                .name(model.name())
                .email(model.email())
                .phoneNumber(model.phoneNumber())
                .postCode(model.postCode())
                .address(model.address())
                .detailAddress(model.detailAddress())
                .build();
        }
    }


}
