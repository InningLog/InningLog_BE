package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.comment.service.CommentDeleteService;
import com.inninglog.inninglog.domain.comment.service.CommentGetService;
import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageCreateReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListResDto;
import com.inninglog.inninglog.domain.contentImage.repository.ContentImageRepository;
import com.inninglog.inninglog.domain.contentImage.service.ImageGetService;
import com.inninglog.inninglog.domain.contentImage.service.PostImageCreateService;
import com.inninglog.inninglog.domain.contentImage.service.PostImageUpdateService;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.service.LikeDeleteService;
import com.inninglog.inninglog.domain.like.service.LikeValidateService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.member.service.MemberGetService;
import com.inninglog.inninglog.domain.member.service.MemberValidateService;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.dto.req.PostCreateReqDto;
import com.inninglog.inninglog.domain.post.dto.req.PostUpdateReqDto;
import com.inninglog.inninglog.domain.post.dto.res.CommunityHomePostResDto;
import com.inninglog.inninglog.domain.post.dto.res.CommunityHomeResDto;
import com.inninglog.inninglog.domain.post.dto.res.PostSingleResDto;
import com.inninglog.inninglog.domain.post.dto.res.PostSummaryResDto;
import com.inninglog.inninglog.domain.scrap.service.ScrapDeleteService;
import com.inninglog.inninglog.domain.scrap.service.ScrapValidateService;
import com.inninglog.inninglog.global.dto.SliceResponse;
import com.inninglog.inninglog.global.s3.ThumbnailUrlGenerator;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUsecase {

    private final PostCreateService postCreateService;
    private final PostImageCreateService postImageCreateService;
    private final PostGetService postGetService;
    private final PostValidateService postValidateService;
    private final PostDeleteService postDeleteService;
    private final PostUpdateService postUpdateService;
    private final PostImageUpdateService postImageUpdateService;

    private final MemberGetService memberGetService;
    private final MemberValidateService memberValidateService;
    private final ImageGetService imageGetService;

    private final LikeValidateService likeValidateService;
    private final LikeDeleteService likeDeleteService;

    private final ScrapValidateService scrapValidateService;
    private final ScrapDeleteService scrapDeleteService;

    private final CommentDeleteService commentDeleteService;
    private final CommentGetService commentGetService;

    private final ContentImageRepository contentImageRepository;
    private final ThumbnailUrlGenerator thumbnailUrlGenerator;

    //게시글 업로드 + 이미지 저장
    @Transactional
    public void uploadPost (PostCreateReqDto dto,String team_Shortcode, Member member){
        String thumbnailUrl = resolveFirstImageThumbnailUrl(dto.imageCreateReqDto());
        Long postId = postCreateService.createPost(dto, team_Shortcode, member, thumbnailUrl);
        postImageCreateService.createPostImageList(postId, dto.imageCreateReqDto());
    }

    //게시글 단일 조회
    @Transactional(readOnly = true)
    public PostSingleResDto getSinglePost(ContentType contentType, Long postId, Member me){
        Post post = postValidateService.getPostById(postId);
        Member writer = postGetService.getPostWriterId(post);
        MemberShortResDto memberShortResDto = memberGetService.toMemberShortResDto(writer);
        ImageListResDto imageListResDto = imageGetService.getImageListToDto(contentType, postId);

        boolean writedByMe = memberValidateService.checkPostWriter(writer.getId(), me.getId());
        boolean likedByMe = likeValidateService.likedByMe(contentType, postId, me);
        boolean scrapedByMe = scrapValidateService.scrapedByMe(contentType, postId, me);

        return postGetService.getSinglePost(post, memberShortResDto, imageListResDto, writedByMe, likedByMe, scrapedByMe);
    }

    //게시글 삭제
    @Transactional
    public void deletePost(Long memberId, Long postId){
        Post post = postValidateService.getPostById(postId);
        memberValidateService.validateWriter(memberId, post.getMember().getId());
        likeDeleteService.deleteByTargetId(ContentType.POST, postId);
        commentDeleteService.deleteByTargetId(ContentType.POST, postId);
        scrapDeleteService.deleteByTargetId(ContentType.POST, postId);
        contentImageRepository.deleteAllByContent(ContentType.POST, postId);
        postDeleteService.postDelete(post);
    }

    //게시글 수정
    @Transactional
    public void updatePost(Long memberId, Long postId, PostUpdateReqDto dto){
        Post post = postValidateService.getPostById(postId);
        memberValidateService.validateWriter(memberId, post.getMember().getId());
        postUpdateService.updatePostFromDto(post, dto);

        List<ContentImage> existingImages = imageGetService.getImageList(ContentType.POST, postId);
        postImageUpdateService.updateImages(dto.remainImages(), existingImages);
        postImageCreateService.createPostImageList(postId, dto.newImages());

        // 이미지 변경 후 썸네일 재계산
        List<ContentImage> updatedImages = imageGetService.getImageList(ContentType.POST, postId);
        String thumbnailUrl = resolveFirstContentImageThumbnailUrl(updatedImages);
        post.updateThumbnailUrl(thumbnailUrl);
    }

    //게시글 조회
    @Transactional(readOnly = true)
    public SliceResponse<PostSummaryResDto> getPostList(String teamShortCode, Pageable pageable){
        Slice<Post> posts = postGetService.getPostsByTeam(teamShortCode, pageable);
        Slice<PostSummaryResDto> dtos =  getPostsByTeam(posts);
        return SliceResponse.of(dtos);
    }

    @Transactional(readOnly = true)
    public Slice<PostSummaryResDto> getPostsByTeam(Slice<Post> posts) {
        return posts.map(post ->
                PostSummaryResDto.of(
                        post,
                        memberGetService.toMemberShortResDto(post.getMember())
                )
        );
    }

    //인기 게시글 조회 (좋아요 10개 이상)
    @Transactional(readOnly = true)
    public SliceResponse<PostSummaryResDto> getPopularPostList(Pageable pageable) {
        Slice<Post> posts = postGetService.getPopularPosts(10L, pageable);
        Slice<PostSummaryResDto> dtos = getPostsByTeam(posts);
        return SliceResponse.of(dtos);
    }

    //커뮤니티 홈 조회
    @Transactional(readOnly = true)
    public CommunityHomeResDto getCommunityHome(Member member) {
        Member memberWithTeam = memberGetService.getMemberWithTeam(member.getId());
        String teamShortCode = memberWithTeam.getTeam().getShortCode();
        Slice<Post> posts = postGetService.getPopularPosts(10L, PageRequest.of(0, 2));

        List<CommunityHomePostResDto> popularPosts = posts.stream()
                .map(post -> {
                    boolean likedByMe = likeValidateService.likedByMe(ContentType.POST, post.getId(), member);
                    boolean scrapedByMe = scrapValidateService.scrapedByMe(ContentType.POST, post.getId(), member);
                    return CommunityHomePostResDto.of(post, likedByMe, scrapedByMe);
                })
                .toList();

        return CommunityHomeResDto.of(teamShortCode, popularPosts);
    }

    //커뮤니티 검색: 키워드로 게시글 검색
    @Transactional(readOnly = true)
    public SliceResponse<PostSummaryResDto> searchPosts(Member member, String keyword, Pageable pageable) {
        Slice<Post> posts = postGetService.searchByKeyword(keyword, pageable);
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        Set<Long> likedPostIds = likeValidateService.findLikedTargetIds(ContentType.POST, postIds, member);
        Set<Long> scrapedPostIds = scrapValidateService.findScrapedTargetIds(ContentType.POST, postIds, member);

        List<PostSummaryResDto> dtos = posts.stream()
                .map(post -> PostSummaryResDto.of(
                        post,
                        memberGetService.toMemberShortResDto(post.getMember()),
                        likedPostIds.contains(post.getId()),
                        scrapedPostIds.contains(post.getId())
                ))
                .toList();

        return SliceResponse.of(dtos, posts.hasNext(), pageable);
    }

    //마이페이지: 내가 쓴 글 목록
    @Transactional(readOnly = true)
    public SliceResponse<PostSummaryResDto> getMyPosts(Member member, Pageable pageable) {
        Slice<Post> posts = postGetService.getMyPosts(member, pageable);
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        // N+1 최적화: 배치 조회
        Set<Long> likedPostIds = likeValidateService.findLikedTargetIds(ContentType.POST, postIds, member);
        Set<Long> scrapedPostIds = scrapValidateService.findScrapedTargetIds(ContentType.POST, postIds, member);

        List<PostSummaryResDto> dtos = posts.stream()
                .map(post -> PostSummaryResDto.of(
                        post,
                        memberGetService.toMemberShortResDto(post.getMember()),
                        likedPostIds.contains(post.getId()),
                        scrapedPostIds.contains(post.getId())
                ))
                .toList();

        return SliceResponse.of(dtos, posts.hasNext(), pageable);
    }

    //마이페이지: 내가 댓글 단 글 목록
    @Transactional(readOnly = true)
    public SliceResponse<PostSummaryResDto> getMyCommentedPosts(Member member, Pageable pageable) {
        Slice<Long> postIdSlice = commentGetService.getCommentedPostIds(member, pageable);
        List<Long> postIds = postIdSlice.getContent();

        if (postIds.isEmpty()) {
            return SliceResponse.empty(pageable);
        }

        List<Post> posts = postGetService.findAllByIds(postIds);
        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

        // N+1 최적화: 배치 조회
        Set<Long> likedPostIds = likeValidateService.findLikedTargetIds(ContentType.POST, postIds, member);
        Set<Long> scrapedPostIds = scrapValidateService.findScrapedTargetIds(ContentType.POST, postIds, member);

        List<PostSummaryResDto> dtos = postIds.stream()
                .map(postMap::get)
                .filter(post -> post != null)
                .map(post -> PostSummaryResDto.of(
                        post,
                        memberGetService.toMemberShortResDto(post.getMember()),
                        likedPostIds.contains(post.getId()),
                        scrapedPostIds.contains(post.getId())
                ))
                .toList();

        return SliceResponse.of(dtos, postIdSlice.hasNext(), pageable);
    }

    //마이페이지: 내가 스크랩한 글 목록
    @Transactional(readOnly = true)
    public SliceResponse<PostSummaryResDto> getMyScrappedPosts(Member member, Pageable pageable) {
        Slice<Long> postIdSlice = scrapValidateService.getScrappedPostIds(member, pageable);
        List<Long> postIds = postIdSlice.getContent();

        if (postIds.isEmpty()) {
            return SliceResponse.empty(pageable);
        }

        List<Post> posts = postGetService.findAllByIds(postIds);
        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

        // N+1 최적화: 배치 조회
        Set<Long> likedPostIds = likeValidateService.findLikedTargetIds(ContentType.POST, postIds, member);
        Set<Long> scrapedPostIds = scrapValidateService.findScrapedTargetIds(ContentType.POST, postIds, member);

        List<PostSummaryResDto> dtos = postIds.stream()
                .map(postMap::get)
                .filter(post -> post != null)
                .map(post -> PostSummaryResDto.of(
                        post,
                        memberGetService.toMemberShortResDto(post.getMember()),
                        likedPostIds.contains(post.getId()),
                        scrapedPostIds.contains(post.getId())
                ))
                .toList();

        return SliceResponse.of(dtos, postIdSlice.hasNext(), pageable);
    }

    // 생성 시: ImageCreateReqDto 리스트에서 sequence 최소인 이미지의 key로 썸네일 URL 생성
    private String resolveFirstImageThumbnailUrl(List<ImageCreateReqDto> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .min(Comparator.comparingInt(ImageCreateReqDto::sequence))
                .map(img -> thumbnailUrlGenerator.generateThumbnailUrl(img.key()))
                .orElse(null);
    }

    // 수정 시: ContentImage 리스트에서 sequence 최소인 이미지의 originalUrl로 썸네일 URL 생성
    private String resolveFirstContentImageThumbnailUrl(List<ContentImage> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .min(Comparator.comparingInt(ContentImage::getSequence))
                .map(img -> thumbnailUrlGenerator.generateThumbnailUrl(extractKeyFromUrl(img.getOriginalUrl())))
                .orElse(null);
    }

    // originalUrl에서 S3 key 추출 (baseUrl 이후 부분)
    private String extractKeyFromUrl(String originalUrl) {
        // originalUrl: https://{bucket}.s3.{region}.amazonaws.com/{key}
        int idx = originalUrl.indexOf(".amazonaws.com/");
        if (idx == -1) {
            return originalUrl;
        }
        return originalUrl.substring(idx + ".amazonaws.com/".length());
    }
}
