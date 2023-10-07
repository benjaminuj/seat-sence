package project.seatsence.src.admin.service;

import static project.seatsence.global.code.ResponseCode.*;
import static project.seatsence.global.entity.BaseTimeAndStateEntity.State.ACTIVE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.config.security.JwtProvider;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.global.utils.CookieUtils;
import project.seatsence.src.admin.dao.AdminRepository;
import project.seatsence.src.admin.dto.request.AdminSignInRequest;
import project.seatsence.src.admin.dto.request.AdminSignUpRequest;
import project.seatsence.src.store.dao.StoreMemberRepository;
import project.seatsence.src.store.dao.StoreRepository;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.store.domain.StoreMember;
import project.seatsence.src.store.domain.StorePosition;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.user.domain.UserRole;
import project.seatsence.src.user.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreMemberRepository storeMemberRepository;
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final CookieUtils cookieUtils;
    private final StoreRepository storeRepository;

    public Boolean checkDuplicatedEmail(String email) {
        return !adminRepository.existsByEmailAndState(email, ACTIVE);
    }

    public Boolean checkDuplicatedNickname(String nickname) {
        return !adminRepository.existsByNicknameAndState(nickname, ACTIVE);
    }

    public void checkPassword(AdminSignUpRequest adminSignUpRequest) {
        if (!adminSignUpRequest.getPassword().equals(adminSignUpRequest.getPasswordChecked())) {
            throw new BaseException(USER_MISMATCHED_PASSWORD);
        }
    }

    public User findById(Long userId) {
        return adminRepository
                .findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return adminRepository
                .findByEmailAndState(email, ACTIVE)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    }

    public void adminSignUp(AdminSignUpRequest adminSignUpRequest) {
        User newAdmin =
                new User(
                        adminSignUpRequest.getEmail(),
                        passwordEncoder.encode(adminSignUpRequest.getPassword()),
                        UserRole.ADMIN,
                        adminSignUpRequest.getName(),
                        adminSignUpRequest.getBirthDate(),
                        adminSignUpRequest.getNickname(),
                        adminSignUpRequest.getSex(),
                        adminSignUpRequest.getConsentToMarketing(),
                        adminSignUpRequest.getConsentToTermsOfUser());

        LocalDate openDate =
                LocalDate.parse(adminSignUpRequest.getOpenDate(), DateTimeFormatter.ISO_DATE);

        Store newStore =
                new Store(
                        newAdmin,
                        adminSignUpRequest.getBusinessRegistrationNumber(),
                        openDate,
                        adminSignUpRequest.getAdminName(),
                        adminSignUpRequest.getStoreName(),
                        adminSignUpRequest.getAddress(),
                        adminSignUpRequest.getDetailAddress());

        checkPassword(adminSignUpRequest);

        if (!checkDuplicatedEmail(adminSignUpRequest.getEmail())) {
            throw new BaseException(USER_EMAIL_ALREADY_EXIST);
        }
        if (!checkDuplicatedNickname(adminSignUpRequest.getNickname())) {
            throw new BaseException(USER_NICKNAME_ALREADY_EXIST);
        }

        // OWNER 권한
        StoreMember newStoreMember =
                StoreMember.builder()
                        .user(newAdmin)
                        .store(newStore)
                        .position(StorePosition.OWNER)
                        .permissionByMenu(
                                "{\"storeStatus\":true,\"seatSetting\":true,\"storeStatistics\":true,\"storeSetting\":true}")
                        .build();

        adminRepository.save(newAdmin);
        storeRepository.save(newStore);
        storeMemberRepository.save(newStoreMember);
    }

    public User findAdmin(AdminSignInRequest adminSignInRequest) {
        User user = userService.findByEmailAndState(adminSignInRequest.getEmail());

        UserRole userRole = user.getRole();

        if (!(userRole.equals(UserRole.ADMIN)
                || storeMemberRepository.existsByUserIdAndState(user.getId(), ACTIVE))) {
            throw new BaseException(USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(adminSignInRequest.getPassword(), user.getPassword())) {
            throw new BaseException(USER_NOT_FOUND);
        }

        return user;
    }

    public void adminSignIn(HttpServletResponse response, String refreshToken) {
        cookieUtils.addCookie(response, refreshToken);
    }

    /** 메인 페이지 가게 우선순위: 가장 최근에 직원이 된 가게 -> OWNER: 가게 등록순서와 일치 -> MEMBER: 직원 등록 순서 */
    public StoreMember getHighestPriorityStore(User user) {
        return storeMemberRepository
                .findFirstByUserIdOrderByCreatedAtAsc(user.getId())
                .orElseThrow(() -> new BaseException(STORE_MEMBER_NOT_FOUND));
    }
}
