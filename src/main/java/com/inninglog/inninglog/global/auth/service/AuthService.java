package com.inninglog.inninglog.global.auth.service;

import com.inninglog.inninglog.global.auth.dto.LoginRequest;
import com.inninglog.inninglog.global.auth.dto.LoginResponse;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.domain.MemberCredential;
import com.inninglog.inninglog.member.domain.MemberType;
import com.inninglog.inninglog.member.repository.MemberCredentialRepository;
import com.inninglog.inninglog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberCredentialRepository credentialRepository;
    private final JwtProvider jwtProvider;

    public void signup(String username, String password) {
        if (credentialRepository.existsByUserID(username)) {
            throw new CustomException(ErrorCode.EXIST_USERID);
        }

        if (!isValidPassword(password)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_FORMAT); // "비밀번호는 6~12자리 영문+숫자 조합이어야 합니다."
        }

        Member member = Member.builder()
                .memberType(MemberType.USER)
                .build();
        memberRepository.save(member);

        MemberCredential credential = new MemberCredential();
        credential.setUserID(username);
        credential.setPassword(passwordEncoder.encode(password)); // ✅ 여기!
        credential.setMember(member);
        credentialRepository.save(credential);
    }

    public String login(String username, String password) {
        MemberCredential credential = credentialRepository.findByUserID(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(password, credential.getPassword())) { // ✅ 여기!
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        Member member = credential.getMember();
        return jwtProvider.createToken(member.getId());
    }

    public boolean isUserIdDuplicated(String userID) {
        return credentialRepository.existsByUserID(userID);
    }


    public LoginResponse resdto(LoginRequest dto){

        MemberCredential memberCredential = credentialRepository.findByUserID(dto.getUserID())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Member member = memberRepository.findById(memberCredential.getMember().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String jwt = login(dto.getUserID(), dto.getPassword());

        return LoginResponse.create(member.getId(),jwt);
    }

    public boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$";
        return password.matches(regex);
    }
}