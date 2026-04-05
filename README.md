# SSAVICE

## 🎯 프로젝트 소개
SSAVICE는 헬스장 PT, 필라테스 등 오프라인 서비스를 혼자보다 여럿이 함께 이용하면 더 저렴하게 이용할 수 있도록 연결해주는 공동구매 중개 플랫폼입니다. 
<br>
판매자가 직접 서비스를 등록하고 구매자를 모집하며, 인원이 충족되면 할인된 가격으로 거래가 성사됩니다. 
<br>
개인 단위의 대량구매도 지원하여 조건에 맞는 구매 방식을 유연하게 선택할 수 있습니다. 
<br>
지도 기반 검색을 통해 내 주변의 공동구매 가능한 서비스를 빠르게 탐색할 수 있는 것이 핵심입니다.

### 프로젝트 기술 스택

- **Framework:** Spring Boot
- **Database:** MySQL, Redis, H2 (테스트용)
- **Infrastructure:** AWS (AWS EC2, S3, RDS, SQS, Lambda, OpenSearch)
- **CI/CD:** GitHub Actions, Docker
- **Authentication:** OAuth2.0 (Kakao)
- **APIs:** Kakao API, Toss API

### 주요 설정 및 참고 사항

- NGINX: SSL 설정으로 HTTPS 보안 강화, 로깅 및 proxy_pass를 통한 경로 라우팅, gzip 압축으로 응답 최적화
- Redis: OAuth2.0 Kakao 로그인 후 JWT 토큰 관리 및 캐싱 용도로 활용
- MySQL: 주 데이터베이스로 활용, AWS RDS로 운영 환경 관리. Outbox 패턴 적용으로 OpenSearch와 데이터 정합성 보장
- AWS S3: temp/origin 이중 버킷 구조로 이미지 관리. Presigned URL로 클라이언트 직접 업로드, S3 Lifecycle으로 미확인 temp 파일 자동 삭제
- AWS SQS: Lambda 썸네일 생성 완료 후 서버 DB 갱신 트리거, Outbox → OpenSearch 색인 파이프라인에 활용
- AWS Lambda: S3 이미지 업로드 이벤트 트리거로 썸네일 자동 생성
- AWS OpenSearch: Nori 형태소 분석기 기반 한국어 검색, search_after 커서 페이지네이션 적용
- GitHub Actions: 코드 푸시 시 자동 테스트 및 Docker 이미지 빌드·EC2 배포 자동화
- H2: 로컬 및 테스트 환경에서 인메모리 DB로 활용

## ⚠️ ISSUE
- [이미지 서비스 구현 설계](https://velog.io/@momnpa333/%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B5%AC%ED%98%84-%EC%84%A4%EA%B3%84)
- [이미지가 실존하는지 validate 처리를 해야하는가?](https://velog.io/@momnpa333/%EC%9D%B4%EB%AF%B8%EC%A7%80%EA%B0%80-%EC%8B%A4%EC%A1%B4%ED%95%98%EB%8A%94%EC%A7%80-validate-%EC%B2%98%EB%A6%AC%EB%A5%BC-%ED%95%B4%EC%95%BC%ED%95%98%EB%8A%94%EA%B0%80)
- [geohash를 이용한 위치기반 서비스 검색](https://velog.io/@momnpa333/%EC%A7%80%EC%98%A4%ED%95%B4%EC%8B%9C%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%9C%84%EC%B9%98-%EA%B8%B0%EB%B0%98-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%84%A4%EA%B3%84)
- [위치 + 텍스트 검색을 위한 ElasticSearch 도입기](https://velog.io/@momnpa333/%EC%9C%84%EC%B9%98-%ED%85%8D%EC%8A%A4%ED%8A%B8-%EA%B2%80%EC%83%89%EC%9D%84-%EC%9C%84%ED%95%9C-ElasticSearch-%EB%8F%84%EC%9E%85%EA%B8%B0)
- [엘라스틱 서치 관련 정리](https://velog.io/@momnpa333/%EC%97%98%EB%9D%BC%EC%8A%A4%ED%8B%B1-%EC%84%9C%EC%B9%98-%EA%B4%80%EB%A0%A8-%EC%A0%95%EB%A6%AC)

## 🏗 시스템 아키텍쳐

## 📊 ERD

## **프로젝트 중점사항**
- 위치 기반 검색 인프라
  - MySQL LIKE → GeoHash → OpenSearch + Nori 형태소 분석기

- 이미지 업로드 파이프라인
  - Presigned URL → S3 temp 버킷 → HeadObject 검증 → Lambda 썸네일 → SQS → DB 갱신
  - 서버 메모리·IO 부하 제거, Spring Retry + 지수 백오프 + Jitter 적용

- 데이터 동기화
  - Outbox 패턴 + SQS로 DB → OpenSearch 색인
  - 다중 인스턴스 중복 방지: ShedLock 적용
