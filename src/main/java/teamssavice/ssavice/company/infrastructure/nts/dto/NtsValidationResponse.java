package teamssavice.ssavice.company.infrastructure.nts.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NtsValidationResponse {

    @JsonProperty("request_cnt")
    private int requestCount;

    @JsonProperty("status_code")
    private String statusCode; // 국세청 서버 응답 상태 (예: "OK")

    @JsonProperty("data")
    private List<BusinessDataResponse> data;

    @Getter
    @NoArgsConstructor
    public static class BusinessDataResponse {

        @JsonProperty("b_no")
        private String businessNumber;

        @JsonProperty("valid")
        private String valid; // "01": 유효, "02": 확인불가

        @JsonProperty("valid_msg")
        private String validMessage; // 검증 결과 메시지

        @JsonProperty("request_param")
        private RequestParam requestParam;
    }

    @Getter
    @NoArgsConstructor
    public static class RequestParam {
        @JsonProperty("b_no")
        private String businessNumber;

        @JsonProperty("start_dt")
        private String startDate;

        @JsonProperty("p_nm")
        private String ownerName;

    }

}
