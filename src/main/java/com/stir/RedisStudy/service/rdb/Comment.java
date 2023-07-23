package com.stir.RedisStudy.service.rdb;
import lombok.*;

public class Comment {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentReqDto{
        private String id;
        private String postId;
        private String writer;
        private String content;
        private String timestamp;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentResDto{
        private String postId;
        private String writer;
        private String content;
        private String timestamp;
    }
}