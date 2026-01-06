package teamssavice.ssavice.user.service.dto;

import java.sql.Timestamp;
import lombok.Builder;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.user.entity.Users;

public class UserModel {

    @Builder
    public record Login(
        String accessToken,
        Long expiresIn,
        String refreshToken
    ) {

        public static Login from(Token token) {
            return Login.builder()
                .accessToken(token.accessToken())
                .expiresIn(token.expiresIn())
                .refreshToken(token.refreshToken())
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

        public static Info from(
            Users user, String presignedUrl
        ) {
            return Info.builder()
                .imageUrl(presignedUrl)
                .name(user.getName())
                .createdAt(Timestamp.valueOf(user.getCreatedAt()))
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .postCode(user.getAddress() != null ? user.getAddress().getPostCode() : null)
                .address(user.getAddress() != null ? user.getAddress().getAddress() : null)
                .detailAddress(
                    user.getAddress() != null ? user.getAddress().getDetailAddress() : null)
                .build();
        }
    }

    @Builder
    public record Modify(
        String name,
        String email,
        String phoneNumber,
        String postCode,
        String address,
        String detailAddress
    ) {

        public static Modify from(Users user) {
            return Modify.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .postCode(user.getAddress() != null ? user.getAddress().getPostCode()
                    : null)
                .address(
                    user.getAddress() != null ? user.getAddress().getAddress() : null)
                .detailAddress(user.getAddress() != null ? user.getAddress()
                    .getDetailAddress() : null)
                .build();
        }
    }

}
