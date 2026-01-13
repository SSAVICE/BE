package teamssavice.ssavice.company.service.client;

import lombok.Builder;

@Builder
public record BusinessVerifyRequest(
        String businessNumber,
        String startDate,
        String name
) {}