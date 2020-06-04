# kakaopay_Test
카카오페이 사전과제입니다.

====================================================
<목차>

1. 개발환경
2. 빌드 및 실행
3. 테이블설계
4. 문제해결 전략

====================================================

<개발환경>

* 기본환경
  1. IDE : Eclipse IDE for Java Developers(Ver. 2020-03)
  2. OS: Window 10
  3. Git

* 개발환경
  1. Spring Boot 2.3.0
  2. JAVA 8
  3. JPA
  4. H2
  5. Gradle
　6. JUnit5
　
* 테스트환경
　1.POSTMAN (테스트시 헤더 내 contenttype은 application/json)

====================================================

<빌드 및 실행(윈도우 환경 기준)>
* JAVA8 및 GIT, Gradle 사전 설치 필요.

git clone https://github.com/ajh5469/kakaopay_Test.git
cd kakaopay_Test
gradle build
java -jar build\libs\kakaopayTest-0.0.1-SNAPSHOT.jar

====================================================

##엔티티설계

 1. Payment – 결제기록의 마스터 테이블. 최초 결제 인입시 Insert. 이후 취소결제 인입시 Update.
 2. Payment_Cancel – 취소결제 인입시 Insert.
 3. Data_Transfer - 카드사 전송용 string 데이터 적재용 테이블. 모든 결제/취소 인입시 Insert.

====================================================

##문제해결 전략

1. 결제API 구축
* 호출방식: POST
* 호출 URL: http://localhost:8080/payment
* 파라미터 예시
{
	"crdtCardNum": "9491100100166706",
	"payAmount": "20000",
	"payVat": "909",
	"installment": "00",
	"validYm": "0225",
	"cvc": "582"
}
* 응답 예시
{
    "INSERT_DTM": "2020-06-05T06:53:09.436",
    "AMOUNT": 20000,
    "VAT": 909,
    "ID": 2
}
* 필수 및 선택 파라미터를 확인 후 Base64 암호화를 거쳐 결제 테이블 및 데이터전송용 테이블에 적재

2. 결제조회API 구축
* 호출방식: GET
* 호출 URL:  http://localhost:8080/payment
* 파라미터 예시
　　{
	"id": "2"
}
* 응답 예시
{
    "CARD_INFO": {
        "CVC": "582",
        "CREDIT_CARD_NUMBER": "949110*******706",
        "Valid_YM": "0225"
    },
    "PaymentType": "Pay",
    "Initial_Payment_Dtm": "2020-06-05T06:53:09.436",
    "ID": 2,
    "Amount_INFO": {
        "Pay_Amount": 20000,
        "VAT": 909
    }
}
* 카드번호의 경우, 앞 6자리와 뒤 3자리를 제외하고 마스킹 처리해 노출
* 결제금액(Pay_Amount)와 부가가치세(VAT)의 경우, 전액 취소된 상태가 아니라면 현재 취소되지 않은 채 남아있는 금액만 노출함.
예) 최초 결제 10000원, 취소 5000원 > 5000원만 노출

3. 취소결제API 구축(전체/부분)
* 호출방식: PUT
* 호출 URL: http://localhost:8080/payment
* 파라미터 예시
{
	"id": "1",
	"cancelAmount": "10000",
	"cancelVat": "0"
}
* 응답 예시
{
    "Payment_Cancel_Dtm": "2020-06-05T07:03:15.379",
    "ID": 1,
    "Cancel_VAT": 0,
    "Cancel_Amount": 10000
}
* 전체 및 부분취소 모두 가능
* 제시받은 과제 내 테스트케이스 모두 구현 가능
➢ 유닛테스트 파일(ControolerTest.java) 참조

====================================================
