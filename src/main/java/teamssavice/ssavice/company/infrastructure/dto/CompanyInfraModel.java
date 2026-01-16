package teamssavice.ssavice.company.infrastructure.dto;

import lombok.Builder;

public class CompanyInfraModel {

    @Builder
    public record Validate(
        boolean isValid
    ) {

    }

}
