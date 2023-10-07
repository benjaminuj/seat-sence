<div align=center><h1> SEAT SENSE 🪑 </h1></div>

<div align=center>
일반 유저는 실시간 좌석 현황을 확인하고, 좌석 '바로 사용' 혹은 '예약' 기능을 이용해 내가 원하는 좌석을 이용할 수 있고,
<br>
사장님은 나만의 가게 좌석 배치도를 직접 만들어, 가게 관리와 좌석 이용 관리 및 예약 관리를 할 수 있는 서비스입니다.
</div>

## Website

- 일반 사용자 : https://myuser.seat-sense.site/
```
// 일반 사용자 테스트 계정

email : dryme@naver.com
password : dryme123!@#
```

- 사장님 : https://admin.seat-sense.site/
```
// 사장님 테스트 계정

email : oksoon3@naver.com
password : admin3333@
```

# Index

- [How to Use](#How-to-Use)
- [Stacks](#Stacks)
 
---

# How to Use

## Requirements 
You require the following to build SEAT SENSE:
- Latest stable Java 11
- Latest stable Gradle

## Installation 
```
$ git clone https://github.com/benjaminuj/seat-sence.git
$ cd seat-sense 
```

## Build 
```
$ ./gradlew --exclude-task -test clean build
```

## Run 
```
$ java -jar -Dspring.profiles.active=dev build/libs/seat-sence-0.0.1-SNAPSHOT.jar
```

# Stacks

### Development
<div align=center> 
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/jpa-6DB33F?style=for-the-badge&logo=jpa&logoColor=white">
<img src="https://img.shields.io/badge/Querydsl-4695EB?style=for-the-badge&logo=Querydsl&logoColor=white">
</div>

### Tool
<div align=center> 
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
</div>

### Environment
<div align=center> 
<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
</div>
