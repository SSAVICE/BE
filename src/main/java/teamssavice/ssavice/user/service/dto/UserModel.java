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
            Users user
        ) {
            return Info.builder()
                .imageUrl(user.getImageUrl())
                .name(user.getName())
                .createdAt(Timestamp.valueOf(user.getCreatedAt()))
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .postCode(user.getAddress().getPostCode())
                .address(user.getAddress().getAddress())
                .detailAddress(user.getAddress().getDetailAddress())
                .build();
        }
    }

}
