![main.png](img/main-banner.png)

## 🚀<a href="https://pictz.site" target="blank">Pictz 바로가기</a> (**👊🏻 VS 월드컵은 Pictz에서 투표하세요!**)

### 🔧 기술 스택

| **BackEnd** |
|:-----------:|
| ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white) ![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white) ![JPA](https://img.shields.io/badge/JPA-005A9C?style=flat-square&logo=&logoColor=white) |

| **FrontEnd** |
|:------------:|
| ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F32?style=flat-square&logo=thymeleaf&logoColor=white) ![AJAX](https://img.shields.io/badge/AJAX-000000?style=flat-square&logo=&logoColor=white) ![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=flat-square&logo=Bootstrap&logoColor=white) |

| **DataBase** |
|:------------:|
| ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=MySQL&logoColor=white) |

| **DevOps** |
|:----------:|
| ![AWS EC2](https://img.shields.io/badge/EC2-FF9900?style=flat-square&logo=AmazonEC2&logoColor=white) ![S3](https://img.shields.io/badge/Amazon_S3-569A31?style=flat-square&logo=AmazonS3&logoColor=white) ![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=Nginx&logoColor=white) |

---

### 아키텍처
![architecture.png](img/architecture.png)

### ERD
![db.png](img/db.png)

## 트러블슈팅

**1. 메인 페이지 캐싱 전략 및 부하 테스트 진행** [#94](https://github.com/als904204/pictz/pull/94)
- 실제 가상 머신(EC2 Free Tier와 유사한 환경)에서 **Caffeine 캐시**와 **Redis 캐시**를 적용해 부하 테스트 진행

**2. 투표 동시성 문제 해결 과정** [#112](https://github.com/als904204/pictz/pull/112)
- 전체 synchronized 적용
- 부분 synchronized 적용  
- **Atomic** 및 **Concurrent**를 이용

**3. 비효율적으로 투표 요청마다 DB에 저장하는 과정 개선하기** [#96](https://github.com/als904204/pictz/pull/96)
- 클라이언트 조건부 투표 결과 전송
- 투표 정보 메모리 임시 저장
- 일정 시간마다 투표 정보 batch로 일괄 저장
