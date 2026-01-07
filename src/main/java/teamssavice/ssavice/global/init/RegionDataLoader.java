package teamssavice.ssavice.global.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.global.entity.Region;
import teamssavice.ssavice.global.repository.RegionRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegionDataLoader implements CommandLineRunner {

    private final RegionRepository regionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. 중복 실행 방지
        if (regionRepository.count() > 0) {
            return;
        }

        var resource = getClass().getResourceAsStream("/region_data.csv");
        if (resource == null) {
            log.error("region_data.csv 파일을 찾을 수 없습니다.");
            return;
        }

        List<Region> regions = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))) {
            String line;

            // 첫줄부터 바로 데이터
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                // 콤마로 분리
                // CSV 형태: [0]법정동코드, [1]법정동명, [2]폐지여부(존재) - 어차피 안씀
                String[] cols = line.split(",");

                String code = cols[0].replaceAll("[^0-9]", "");

                // 혹시나 이상하면 건너뛰기 - 없긴한듯
                if (code.length() != 10) {
                    continue;
                }

                // 데이터 파싱
                String fullAddress = cols[1].trim().replace("\"", ""); // 서울특별시 종로구
                // cols[2]에 "존재"가 있어도 우린 안 쓰니까 무시하면 됨

                // 로직: 구군 코드 & 이름 분리 & Depth
                String gugunCode = code.substring(0, 5);

                String[] addrParts = fullAddress.split(" ");
                String displayName = addrParts[addrParts.length - 1]; // 맨 뒤 단어 (종로구 or 청운동)

                int depth = code.endsWith("00000") ? 2 : 3;

                regions.add(Region.builder()
                        .regionCode(code)
                        .gugunCode(gugunCode)
                        .displayName(displayName)
                        .fullAddress(fullAddress)
                        .depth(depth)
                        .build());
            }
        }

        // MySql 로컬 설정으로
        if (!regions.isEmpty()) {
            regionRepository.saveAll(regions);
        }
    }
}