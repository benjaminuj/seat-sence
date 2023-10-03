<div align=center><h2>SEAT SENSE 🪑 </h2></div>

---

### 실시간 **좌석 현황 확인** 서비스, Seat Sense입니다!

가게에 가기 전, 가게의 현재 좌석 이용 상황에 대해 궁금하지 않으셨나요?

가게에 갔는데, 만석이고 또 이후 일정이 있어서 대기를 해야 할지 말아야 할지 고민해 본 경험이 있으신가요?

가게 예약시 원하는 시간만 지정하는 게 아니라, 원하는 좌석까지 직접 지정하고 싶다는 생각 해보신 적 있으신가요?

💡 **이 모든 고민을 Seat Sense 가 해결해 줄게요!**

**실시간 좌석 현황을 확인**한 후, **좌석 바로 사용 혹은 예약 기능**을 이용해 간편하게 내가 원하는 좌석을 이용할 수 있어요.

이뿐만 아니라, **좌석 평균 이용 시간 정보**가 제공되어, 대기시간 예측을 도와줄 수 있어요. ⏳

🙌🏻 사장님이신가요?

Seat Sense 를 통해 **나만의 가게 좌석 배치도**를 직접 만들고, **기본적인 가게 관리**와 **좌석 이용 관리 및 예약 관리**를 한 서비스에서 손쉽게 관리해 보세요! 

### 서비스 이용하기
- 일반 사용자 : https://myuser.seat-sense.site/
- 사장님 : https://admin.seat-sense.site/

## INDEX
---
- [DIRECTORY STRUCTURE](#DIRECTORY-STRUCTURE)
- [STACKS](#STACKS)
 
## DIRECTORY STRUCTURE

---

<pre>
├── src
│   ├── main
│   │   ├── generated
│   │   ├── java
│   │   │   └── project
│   │   │       └── seatsence
│   │   │           ├── SeatSenceApplication.java
│   │   │           ├── global
│   │   │           │   ├── advice
│   │   │           │   │   ├── ExceptionAdvice.java
│   │   │           │   │   └── ResponseAdvice.java
│   │   │           │   ├── annotation
│   │   │           │   │   ├── ValidBirthDate.java
│   │   │           │   │   ├── ValidBusinessRegistrationNumber.java
│   │   │           │   │   ├── ValidCategory.java
│   │   │           │   │   ├── ValidEmail.java
│   │   │           │   │   ├── ValidNickname.java
│   │   │           │   │   ├── ValidPassword.java
│   │   │           │   │   ├── ValidWifi.java
│   │   │           │   │   └── validator
│   │   │           │   │       ├── BirthDateValidator.java
│   │   │           │   │       ├── BusinessRegistrationNumberValidator.java
│   │   │           │   │       ├── CategoryValidator.java
│   │   │           │   │       ├── EmailValidator.java
│   │   │           │   │       ├── NicknameValidator.java
│   │   │           │   │       ├── PasswordValidator.java
│   │   │           │   │       └── WifiValidator.java
│   │   │           │   ├── code
│   │   │           │   │   └── ResponseCode.java
│   │   │           │   ├── config
│   │   │           │   │   ├── S3Config.java
│   │   │           │   │   ├── SwaggerConfig.java
│   │   │           │   │   ├── database
│   │   │           │   │   │   └── QueryDslConfig.java
│   │   │           │   │   ├── filter
│   │   │           │   │   │   ├── CustomAuthenticationFilter.java
│   │   │           │   │   │   └── JwtFilter.java
│   │   │           │   │   ├── handler
│   │   │           │   │   │   ├── CustomAuthFailureHandler.java
│   │   │           │   │   │   ├── CustomAuthSuccessHandler.java
│   │   │           │   │   │   └── CustomAuthenticationProvider.java
│   │   │           │   │   └── security
│   │   │           │   │       ├── CorsConfig.java
│   │   │           │   │       ├── JwtProvider.java
│   │   │           │   │       └── SpringSecurityConfig.java
│   │   │           │   ├── constants
│   │   │           │   │   └── Constants.java
│   │   │           │   ├── converter
│   │   │           │   │   └── StateAttributeConverter.java
│   │   │           │   ├── entity
│   │   │           │   │   ├── BaseEntity.java
│   │   │           │   │   └── BaseTimeAndStateEntity.java
│   │   │           │   ├── exceptions
│   │   │           │   │   └── BaseException.java
│   │   │           │   ├── mapper
│   │   │           │   │   └── GenericMapper.java
│   │   │           │   ├── response
│   │   │           │   │   ├── BaseResponse.java
│   │   │           │   │   ├── ErrorResponse.java
│   │   │           │   │   └── SliceResponse.java
│   │   │           │   ├── token
│   │   │           │   │   └── CustomAuthenticationToken.java
│   │   │           │   └── utils
│   │   │           │       ├── CookieUtils.java
│   │   │           │       ├── EnumUtils.java
│   │   │           │       └── StringUtils.java
│   │   │           └── src
│   │   │               ├── admin
│   │   │               │   ├── api
│   │   │               │   │   └── AdminApi.java
│   │   │               │   ├── dao
│   │   │               │   │   ├── AdminInfoRepository.java
│   │   │               │   │   └── AdminRepository.java
│   │   │               │   ├── domain
│   │   │               │   │   └── AdminInfo.java
│   │   │               │   ├── dto
│   │   │               │   │   ├── request
│   │   │               │   │   │   ├── AdminSignInRequest.java
│   │   │               │   │   │   └── AdminSignUpRequest.java
│   │   │               │   │   └── response
│   │   │               │   │       └── AdminSignInResponse.java
│   │   │               │   └── service
│   │   │               │       └── AdminService.java
│   │   │               ├── auth
│   │   │               │   ├── dao
│   │   │               │   │   └── RefreshTokenRepository.java
│   │   │               │   └── domain
│   │   │               │       ├── JwtState.java
│   │   │               │       ├── RefreshToken.java
│   │   │               │       └── TokenType.java
│   │   │               ├── holding
│   │   │               │   ├── api
│   │   │               │   └── domain
│   │   │               │       ├── Holding.java
│   │   │               │       └── HoldingStatus.java
│   │   │               ├── store
│   │   │               │   ├── api
│   │   │               │   │   ├── StoreApi.java
│   │   │               │   │   ├── admin
│   │   │               │   │   │   ├── AdminStoreApi.java
│   │   │               │   │   │   ├── StoreCustomApi.java
│   │   │               │   │   │   ├── StoreMemberApi.java
│   │   │               │   │   │   └── StoreSpaceApi.java
│   │   │               │   │   └── user
│   │   │               │   │       └── UserStoreApi.java
│   │   │               │   ├── dao
│   │   │               │   │   ├── CustomUtilizationFieldRepository.java
│   │   │               │   │   ├── StoreChairRepository.java
│   │   │               │   │   ├── StoreMemberRepository.java
│   │   │               │   │   ├── StoreRepository.java
│   │   │               │   │   ├── StoreSpaceRepository.java
│   │   │               │   │   └── StoreTableRepository.java
│   │   │               │   ├── domain
│   │   │               │   │   ├── Category.java
│   │   │               │   │   ├── CustomUtilizationField.java
│   │   │               │   │   ├── CustomUtilizationFieldType.java
│   │   │               │   │   ├── Day.java
│   │   │               │   │   ├── ReservationUnit.java
│   │   │               │   │   ├── Store.java
│   │   │               │   │   ├── StoreChair.java
│   │   │               │   │   ├── StoreMember.java
│   │   │               │   │   ├── StorePosition.java
│   │   │               │   │   ├── StoreSpace.java
│   │   │               │   │   └── StoreTable.java
│   │   │               │   ├── dto
│   │   │               │   │   ├── mapper
│   │   │               │   │   │   └── StoreMapper.java
│   │   │               │   │   ├── request
│   │   │               │   │   │   └── admin
│   │   │               │   │   │       ├── basic
│   │   │               │   │   │       │   ├── StoreBasicInformationRequest.java
│   │   │               │   │   │       │   ├── StoreCreateRequest.java
│   │   │               │   │   │       │   ├── StoreIsClosedTodayRequest.java
│   │   │               │   │   │       │   ├── StoreNewBusinessInformationRequest.java
│   │   │               │   │   │       │   ├── StoreOperatingTimeRequest.java
│   │   │               │   │   │       │   └── StoreReservationUnitRequest.java
│   │   │               │   │   │       ├── custom
│   │   │               │   │   │       │   └── StoreCustomUtilizationFieldRequest.java
│   │   │               │   │   │       ├── member
│   │   │               │   │   │       │   ├── StoreMemberRegistrationRequest.java
│   │   │               │   │   │       │   └── StoreMemberUpdateRequest.java
│   │   │               │   │   │       └── space
│   │   │               │   │   │           ├── StoreSpaceChairRequest.java
│   │   │               │   │   │           ├── StoreSpaceCreateRequest.java
│   │   │               │   │   │           ├── StoreSpaceTableRequest.java
│   │   │               │   │   │           └── StoreSpaceUpdateRequest.java
│   │   │               │   │   └── response
│   │   │               │   │       ├── LoadSeatStatisticsInformationOfStoreResponse.java
│   │   │               │   │       ├── admin
│   │   │               │   │       │   ├── basic
│   │   │               │   │       │   │   ├── StoreBasicInformationResponse.java
│   │   │               │   │       │   │   ├── StoreNewBusinessInformationResponse.java
│   │   │               │   │       │   │   ├── StoreOperatingTimeResponse.java
│   │   │               │   │       │   │   ├── StoreOwnedStoreResponse.java
│   │   │               │   │       │   │   ├── StorePermissionResponse.java
│   │   │               │   │       │   │   └── StoreReservationUnitResponse.java
│   │   │               │   │       │   ├── custom
│   │   │               │   │       │   │   └── StoreCustomUtilizationFieldListResponse.java
│   │   │               │   │       │   ├── member
│   │   │               │   │       │   │   └── StoreMemberListResponse.java
│   │   │               │   │       │   └── space
│   │   │               │   │       │       ├── StoreSpaceChairResponse.java
│   │   │               │   │       │       ├── StoreSpaceCreateResponse.java
│   │   │               │   │       │       ├── StoreSpaceResponse.java
│   │   │               │   │       │       ├── StoreSpaceSeatResponse.java
│   │   │               │   │       │       └── StoreSpaceTableResponse.java
│   │   │               │   │       └── user
│   │   │               │   │           ├── StoreDetailResponse.java
│   │   │               │   │           └── StoreListResponse.java
│   │   │               │   └── service
│   │   │               │       ├── S3Service.java
│   │   │               │       ├── StoreChairService.java
│   │   │               │       ├── StoreCustomService.java
│   │   │               │       ├── StoreMemberService.java
│   │   │               │       ├── StoreService.java
│   │   │               │       ├── StoreSpaceService.java
│   │   │               │       └── StoreTableService.java
│   │   │               ├── user
│   │   │               │   ├── api
│   │   │               │   │   └── UserApi.java
│   │   │               │   ├── dao
│   │   │               │   │   └── UserRepository.java
│   │   │               │   ├── domain
│   │   │               │   │   ├── User.java
│   │   │               │   │   ├── UserRole.java
│   │   │               │   │   └── UserSex.java
│   │   │               │   ├── dto
│   │   │               │   │   ├── CustomUserDetailsDto.java
│   │   │               │   │   ├── request
│   │   │               │   │   │   ├── FindUserByEmailRequest.java
│   │   │               │   │   │   ├── UserSignInRequest.java
│   │   │               │   │   │   ├── UserSignUpRequest.java
│   │   │               │   │   │   ├── ValidateEmailRequest.java
│   │   │               │   │   │   └── ValidateNicknameRequest.java
│   │   │               │   │   └── response
│   │   │               │   │       ├── FindUserByEmailResponse.java
│   │   │               │   │       ├── UserSignInResponse.java
│   │   │               │   │       ├── UserSignUpResponse.java
│   │   │               │   │       └── ValidateUserInformationResponse.java
│   │   │               │   └── service
│   │   │               │       ├── UserDetailsServiceImpl.java
│   │   │               │       └── UserService.java
│   │   │               └── utilization
│   │   │                   ├── api
│   │   │                   │   ├── AdminUtilizationApi.java
│   │   │                   │   ├── UtilizationApi.java
│   │   │                   │   ├── participation
│   │   │                   │   │   └── ParticipationApi.java
│   │   │                   │   ├── reservation
│   │   │                   │   │   ├── AdminReservationApi.java
│   │   │                   │   │   └── UserReservationApi.java
│   │   │                   │   └── walkin
│   │   │                   │       └── UserWalkInApi.java
│   │   │                   ├── dao
│   │   │                   │   ├── CustomUtilizationContentRepository.java
│   │   │                   │   ├── UtilizationRepository.java
│   │   │                   │   ├── UtilizationRepositoryCustom.java
│   │   │                   │   ├── UtilizationRepositoryImpl.java
│   │   │                   │   ├── participation
│   │   │                   │   │   └── ParticipationRepository.java
│   │   │                   │   ├── reservation
│   │   │                   │   │   └── ReservationRepository.java
│   │   │                   │   └── walkin
│   │   │                   │       └── WalkInRepository.java
│   │   │                   ├── domain
│   │   │                   │   ├── CustomUtilizationContent.java
│   │   │                   │   ├── Participation
│   │   │                   │   │   ├── Participation.java
│   │   │                   │   │   └── ParticipationStatus.java
│   │   │                   │   ├── Utilization.java
│   │   │                   │   ├── UtilizationStatus.java
│   │   │                   │   ├── reservation
│   │   │                   │   │   ├── Reservation.java
│   │   │                   │   │   └── ReservationStatus.java
│   │   │                   │   └── walkin
│   │   │                   │       └── WalkIn.java
│   │   │                   ├── dto
│   │   │                   │   ├── request
│   │   │                   │   │   ├── ChairUtilizationRequest.java
│   │   │                   │   │   ├── CustomUtilizationContentRequest.java
│   │   │                   │   │   └── SpaceUtilizationRequest.java
│   │   │                   │   ├── response
│   │   │                   │   │   ├── LoadSeatsCurrentlyInUseResponse.java
│   │   │                   │   │   ├── participation
│   │   │                   │   │   │   └── ParticipationListResponse.java
│   │   │                   │   │   ├── reservation
│   │   │                   │   │   │   ├── AdminReservationListResponse.java
│   │   │                   │   │   │   ├── AllReservationsForSeatAndDateResponse.java
│   │   │                   │   │   │   └── UserReservationListResponse.java
│   │   │                   │   │   └── walkin
│   │   │                   │   │       └── UserWalkInListResponse.java
│   │   │                   │   └── walkin
│   │   │                   │       ├── request
│   │   │                   │       └── response
│   │   │                   └── service
│   │   │                       ├── AdminUtilizationService.java
│   │   │                       ├── UserUtilizationService.java
│   │   │                       ├── UtilizationService.java
│   │   │                       ├── participation
│   │   │                       │   └── ParticipationService.java
│   │   │                       ├── reservation
│   │   │                       │   ├── AdminReservationService.java
│   │   │                       │   ├── ReservationService.java
│   │   │                       │   └── UserReservationService.java
│   │   │                       └── walkin
│   │   │                           ├── UserWalkInService.java
│   │   │                           └── WalkInService.java
│   │   └── resources
│   │       ├── application-dev.properties
│   │       ├── application-local.properties
│   │       ├── application.yml
│   │       ├── ssl
│   │       │   └── keystore.p12
│   │       ├── static
│   │       └── templates
</pre>

## 💻STACKS
---
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


