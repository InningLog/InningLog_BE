package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.dto.res.CommentListResDto;
import com.inninglog.inninglog.domain.comment.dto.res.CommentResDto;
import com.inninglog.inninglog.domain.comment.repository.CommentRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentGetService {
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public CommentListResDto getCommentList(ContentType contentType, MemberShortResDto memberShortResDto, Long targetId){
       List<Comment> comments =  getcomments(contentType, targetId);
       List<CommentResDto> results = buildCommentTree(memberShortResDto, comments);

       return CommentListResDto.from(results);
    }

    //댓글 조회
    @Transactional(readOnly = true)
    protected List<Comment> getcomments(ContentType contentType, Long targetId){
        return commentRepository.findAllByContentTypeAndTargetIdOrderByCommentAtDesc(contentType, targetId);
    }

    //대댓글 매핑
    @Transactional(readOnly = true)
    protected List<CommentResDto> buildCommentTree(MemberShortResDto memberShortResDto, List<Comment> comments) {

        Map<Long, CommentResDto> map = new HashMap<>();
        List<CommentResDto> roots = new ArrayList<>();

        // 1) DTO 변환 후 map에 넣기
        for (Comment c : comments) {
            CommentResDto dto = CommentResDto.of(c, memberShortResDto);
            map.put(c.getId(), dto);
        }

        // 2) 부모/자식 연결
        for (Comment c : comments) {
            if (c.getRootCommentId() == null) {
                roots.add(map.get(c.getId()));     // 루트 댓글
            } else {
                CommentResDto parent = map.get(c.getRootCommentId());
                parent.replies().add(map.get(c.getId()));     // 대댓글 붙이기
            }
        }

        return roots;
    }
}
