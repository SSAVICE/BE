package teamssavice.ssavice.company.service.client;

import lombok.Builder;

@Builder
public record BusinessVerifyResponse(
        boolean isValid
) {
}
