package teamssavice.ssavice.company.infrastructure.dto;

import lombok.Builder;

public class CompanyInfraCommand {

    @Builder
    public record Validate(
        String businessNumber,
        String startDate,
        String name
    ) {

    }

}
