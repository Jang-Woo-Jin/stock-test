## 프로젝트 구조 설계
* 환경: SpringBoot3 + Java17 + gradle
* 구조: src - main - java - kakaopay_securities - back - stocks
  - api: 실제 API 구현
    + controller, dto, service 폴더로 구성
    + dto는 추후 확장성을 위해 따로 controller dto 와 service dto 로 분리해 작성 
    + service interface의 경우 API 서버에서는 불필요하다 생각되어 작성하지 않음
  - common: 상수, 예외 처리, 상속용 classs, 유틸성 함수 등 공통으로 사용할 코드
  - config: modelMapper, filter 관련 class 등 프로젝트 설정 관련 코드
  - domain: DB table 모델 및 접근 관련 코드
* DB / Model
  - H2 database: Java 환경에서 간편하게 사용 가능
    + H2 console 을 사용하여 별다른 tool 없이 데이터 확인 가능
    + http://localhost:8080/h2-console (username: sa, password: password)
  - Model: Stock(주식), Price(시세) 로 구분
    + Stock: 주식 종목정보를 저장. 조회수 항목을 통해 인기순 정렬 가능
    + Price: 종목의 당일 시세를 저장. 전일 대비, 거래량 항목을 통해 상승/하락/거래량 정렬 가능
             Stock 의 종목 코드를 FK로 가지고 있음
             기본적인 가격 외에 상승/하락을 쉽게 소팅할 수 있도록 전일 대비 항목 추가
             가격 변경 시 현실과 동일한 상/하한선을 정하기 위해 상한가/하한가 필드 및 StockUtil에 계산 로직 추가
  - Data
    + @PostConstruct 를 사용해 App 실행 시 자동으로 데이터를 저장하도록 구현
    + csv 파일을 load 뒤 기본값은 그대로 저장하고, 추가된 필드 값들은 랜덤하게 저장하도록 구현

## 문제해결 전략
* 태그별 조회 API
  - API 기본 기능구현
    + 인기: Stock 의 viewCount 항목을 DESC로 조회
    + 상승: Price 의 diffPrice 항목을 DESC로 조회
    + 하락: Price 의 diffPrice 항목을 ASC로 조회
    + 거래량: Price 의 volume 항목을 DESC로 조회
  - Http Method
    + POST: 조회성 API 의 경우 GET을 사용하는 것이 맞지만 금융권에서 GET을 선호하지 않는 경험 상 POST를 사용함
  - 페이징 기능
    + Spring Data Jpa 의 Pageable 을 이용하여 구현
  - 고려 사항
    + Price 가 StocK 을 FK로 가지고 있어 PriceDao 를 통해 한 method로 조회 가능
    + 필터 값 별 조회해야 하는 필드를 미리 Map 으로 만들어두어 사용
    + 필요한 데이터만 보여주기 위해 StockRank VO 작성
    + 상승/하락 조회 시 종목 수가 충분히 많다면 전일 대비 값의 0 이상/이하를 따로 고려하지 않아도 될 것이라 생각하고 구현함  
* 테스트용 데이터 변환 API
  - API 기본 기능구현
    + 랜덤: Stock의 viewCount, Price 의 주요 값들을 모두 랜덤하게 변화시킴.
    + 지정 값 변경: 지정한 필드(viewCount, price, volume)의 값을 원하는 값으로 변경 가능
  - Http Method
    + PATCH: 데이터 업데이트 기능을 담당하므로 PATCH 를 사용
* 기타 고려 사항
  - Swagger 를 사용하여 RestAPI 문서화
    + http://localhost:8080/api-docs
  - custom error 및 error handler 를 만들어 error 를 상황에 맞게 세분화하여 반환할 수 있도록 함

## 단위테스트
* lib: SpringBootTest
* 방식: 전체 데이터 초기화 - 테스트 데이터 세팅 - 기능 검증
* 테스트케이스
  - 태그별 조회 API
    + testViewCountRank: 인기(조회수) 랭킹 테스트
    + testBullMarketRank: 상승장 랭킹 테스트
    + testBearMarketRank: 하락장 랭킹 테스트
    + testVolumeRank: 거래량 랭킹 테스트
  - 테스트 API
    + testChangeViewCount: 인기(조회수) 변화 테스트
    + testChangePrice: 가격 변화 테스트
    + testChangeVolume: 거래량 변화 테스트
