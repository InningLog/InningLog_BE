# 기능 기획서: 회원 탈퇴 (카카오 연동 해제 + Soft Delete)

> 작성일: 2026-03-21
> 관련 이슈: 미정
> 상태: 구현 완료

---

## 1. 개요

카카오 소셜 로그인 유저가 서비스를 탈퇴할 수 있는 기능.

- **카카오 연동 해제**: 카카오 Admin Key를 이용해 서버 측에서 연결 끊기 API 호출
- **Soft Delete**: `Member` 엔티티에 `deletedAt` 필드 추가, 실제 Row는 삭제하지 않음
- **작성 콘텐츠 익명화**: 탈퇴한 회원의 게시글/댓글 조회 시 작성자 정보를 "알 수 없는 사용자"로 반환

---

## 2. 현재 구조 분석

### Member 엔티티 현황
- `deletedAt` 필드 없음 → 추가 필요
- `kakaoId` 필드 존재 → 카카오 연동 해제에 활용
- `unique` 컬럼 다수 (`kakaoId`, `kakao_nickname`, `kakao_profile_url`, `nickname`) → soft delete 시 재가입 가능 여부 고려 필요

### 작성 콘텐츠 연관 관계

| 엔티티 | Member 참조 | optional |
|--------|------------|----------|
| Post | `@ManyToOne` | false |
| Comment | `@ManyToOne` | false |
| Journal | `@ManyToOne` | false |
| SeatView | `@ManyToOne` | false |
| Like | `@ManyToOne` | false |
| Scrap | `@ManyToOne` | false |

→ Post/Comment/Journal/SeatView는 Member FK를 유지한 채로 soft delete로 처리 가능
→ Like/Scrap은 탈퇴 시 hard delete (의미 없는 데이터)

### MemberShortResDto
```java
public record MemberShortResDto(String nickName, String profile_url) {
    public static MemberShortResDto from(Member member) { ... }
}
```
→ `from(Member)` 팩토리 메서드에 `isDeleted` 분기 추가

### Comment 기존 Soft Delete 패턴
- Comment는 이미 `isDeleted` 플래그 사용
- 삭제된 댓글은 `memberShortResDto = null`, `content = "삭제된 댓글입니다."`로 처리
- 회원 탈퇴 익명화도 동일 레이어(DTO 변환 시점)에서 처리 → **일관성 확보**

### KakaoService 현황
- `kakaoApiClient` (WebClient, `https://kapi.kakao.com`) 이미 존재
- 카카오 연동 해제 API: `POST /v1/user/unlink`
  - Header: `Authorization: KakaoAK {admin_key}`
  - Body: `target_id_type=user_id&target_id={kakaoId}`
  - Admin Key 방식 사용 → 액세스 토큰 재발급 불필요

---

## 3. API 설계

### DELETE /api/member/me

| 항목 | 내용 |
|------|------|
| HTTP Method | DELETE |
| URL | `/api/member/me` |
| 인증 | JWT 필수 (현재 로그인 유저) |
| Request Body | 없음 |
| Response | `200 OK` / 공통 성공 응답 |

**응답 예시:**
```json
{
  "status": 200,
  "message": "success",
  "data": null
}
```

**에러 케이스:**
| 상황 | HTTP 상태 | ErrorCode |
|------|-----------|-----------|
| 이미 탈퇴한 회원 | 400 | `ALREADY_DELETED_MEMBER` |
| 카카오 연동 해제 실패 | 500 | `KAKAO_UNLINK_FAILED` (신규 추가) |

---

## 4. 구현 계획

### 4-1. Member 엔티티 수정
**파일:** `domain/member/domain/Member.java`

```java
// 추가 필드
private LocalDateTime deletedAt;

// 추가 메서드
public void softDelete() {
    this.deletedAt = LocalDateTime.now();
}

public boolean isDeleted() {
    return this.deletedAt != null;
}
```

> **unique 컬럼 충돌 문제**: 탈퇴 후 재가입 시 `kakaoId`, `nickname` 등 unique 제약 위반
> → 탈퇴 시 `kakaoId`를 null로 변경 (카카오 연동 해제되므로 더 이상 식별 불필요)
> → `kakao_nickname`, `kakao_profile_url`, `nickname`은 추후 null 처리 혹은 suffix 방식 고려
> → **1차 구현에서는 kakaoId만 null 처리**

### 4-2. KakaoService 수정
**파일:** `domain/kakao/service/KakaoService.java`

```java
@Value("${kakao.admin_key}")
private String adminKey;

public void unlinkKakaoUser(Long kakaoId) {
    // POST https://kapi.kakao.com/v1/user/unlink
    // Header: Authorization: KakaoAK {adminKey}
    // Body: target_id_type=user_id&target_id={kakaoId}
}
```

환경변수 추가:
- `.env.local`, `.env` → `KAKAO_ADMIN_KEY=...`
- `application.yml` → `kakao.admin_key: ${KAKAO_ADMIN_KEY}`

### 4-3. MemberWithdrawService 신규 생성
**파일:** `domain/member/service/MemberWithdrawService.java`

처리 순서:
1. 회원 조회 및 이미 탈퇴 여부 확인
2. KakaoService.unlinkKakaoUser(member.kakaoId) 호출
3. Like, Scrap hard delete (deleteByMember)
4. member.softDelete() + member.kakaoId = null

```java
@Service
@RequiredArgsConstructor
@Transactional
public class MemberWithdrawService {
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final ScrapRepository scrapRepository;
    private final KakaoService kakaoService;

    public void withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_DELETED_MEMBER);
        }

        kakaoService.unlinkKakaoUser(member.getKakaoId());
        likeRepository.deleteByMember(member);
        scrapRepository.deleteByMember(member);
        member.softDelete();
        member.setKakaoId(null);  // unique 컬럼 해제
    }
}
```

> **Post/Comment/Journal/SeatView는 삭제하지 않음**
> → FK를 유지하면서 soft delete 상태의 Member를 참조
> → 조회 시 `member.isDeleted()` 분기로 "알 수 없는 사용자" 처리

### 4-4. MemberDeleteController 신규 생성
**파일:** `domain/member/controller/MemberDeleteController.java`

```java
@RestController
@RequestMapping("/api/member")
@Tag(name = "Member")
@RequiredArgsConstructor
public class MemberDeleteController {
    private final MemberWithdrawService memberWithdrawService;

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴")
    public ApiResponse<Void> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberWithdrawService.withdraw(userDetails.getMemberId());
        return ApiResponse.ok(null);
    }
}
```

### 4-5. MemberShortResDto 수정
**파일:** `domain/member/dto/res/MemberShortResDto.java`

```java
public static MemberShortResDto from(Member member) {
    if (member == null || member.isDeleted()) {
        return new MemberShortResDto("알 수 없는 사용자", null);
    }
    return new MemberShortResDto(member.getNickname(), member.getKakao_profile_url());
}
```

### 4-6. Repository 메서드 추가

**LikeRepository:**
```java
void deleteByMember(Member member);
```

**ScrapRepository:**
```java
void deleteByMember(Member member);
```

### 4-7. ErrorCode 추가
**파일:** `global/exception/ErrorCode.java`

```java
ALREADY_DELETED_MEMBER(400, "이미 탈퇴한 회원입니다."),
KAKAO_UNLINK_FAILED(500, "카카오 연동 해제에 실패했습니다."),
```

---

## 5. 설계 결정 및 트레이드오프

### Q1. Post/Comment/Journal은 왜 삭제하지 않나?
- 콘텐츠를 삭제하면 다른 사용자들의 댓글/좋아요 컨텍스트가 손실됨
- Soft delete + 익명화로 콘텐츠 흐름 보존
- 운영자 관리 목적으로 데이터 보존 필요

### Q2. 익명화는 왜 DB가 아닌 DTO 변환 시점에 처리하나?
- DB의 FK/제약조건 변경 없이 처리 가능
- Comment의 기존 soft delete 패턴과 일관성
- 추후 "작성자 복원" 같은 운영 처리 여지 남김

### Q3. unique 컬럼 충돌 문제
- 탈퇴 후 같은 카카오 계정으로 재가입 시 `kakaoId`, `kakao_nickname` unique 위반 발생
- 1차: `kakaoId = null` 처리로 재가입 가능
- `kakao_nickname`, `nickname` unique 위반은 재가입 시 새 닉네임 입력으로 회피 가능
- 장기적으로는 `nickname + "_deleted_" + id` suffix 방식도 고려

---

## 6. 체크리스트

### 엔티티
- [x] `Member.java` - `deletedAt` 필드, `softDelete()`, `isDeleted()` 메서드 추가

### 서비스
- [x] `KakaoService.java` - `unlinkKakaoUser(Long kakaoId)` 메서드 추가
- [x] `MemberWithdrawService.java` - 신규 생성

### 컨트롤러
- [x] `MemberDeleteController.java` - 신규 생성, `DELETE /member/me`

### DTO
- [x] `MemberShortResDto.java` - `from(Member)` 익명화 분기 추가

### Repository
- [x] `LikeRepository.java` - `deleteByMember(Member)` 추가
- [x] `ScrapRepository.java` - `deleteByMember(Member)` 추가

### 예외
- [x] `ErrorCode.java` - `ALREADY_DELETED_MEMBER`, `KAKAO_UNLINK_FAILED` 추가

### 환경변수
- [ ] `.env.local`, `.env` - `KAKAO_ADMIN_KEY` 추가 (직접 추가 필요)
- [x] `application-local/dev/prod.yml` - `kakao.admin_key` 바인딩 추가

### 테스트
- [x] `MemberWithdrawControllerTest.java` - API 엔드포인트 테스트 (성공, 이미 탈퇴 케이스)
