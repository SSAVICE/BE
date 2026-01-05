package teamssavice.ssavice.company.infrastructure.nts.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class NtsValidationRequest {

    private List<BusinessData> businesses;

    public static NtsValidationRequest of(String bNo, String startDt, String pNm) {
        return NtsValidationRequest.builder()
                .businesses(Collections.singletonList(BusinessData.builder()
                        .businessNumber(bNo)
                        .startDate(startDt)
                        .ownerName(pNm)
                        .build()))
                .build();
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BusinessData {

        @JsonProperty("b_no")
        private String businessNumber;

        @JsonProperty("start_dt")
        private String startDate;

        @JsonProperty("p_nm")
        private String ownerName;

        // 아래는 선택값인데 혹시 더 받을건지 해서 우선 남겨둠

        @JsonProperty("p_nm2")
        private String ownerName2;

        @JsonProperty("b_nm")
        private String businessName;

        @JsonProperty("corp_no")
        private String corporationNumber;

        @JsonProperty("b_sector")
        private String businessSector;

        @JsonProperty("b_type")
        private String businessType;

        @JsonProperty("b_adr")
        private String businessAddress;
    }
}
