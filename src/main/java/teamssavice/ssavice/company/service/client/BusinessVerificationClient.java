package teamssavice.ssavice.company.service.client;

public interface BusinessVerificationClient {


    // 사업자 등록 정보를 입력받아 진위 여부를 확인
    boolean verify(String businessNumber, String openDate, String ownerName);
}
