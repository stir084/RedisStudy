package com.stir.RedisStudy.service.rdb;

import lombok.*;

public class Post {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostReqDto{
        private String id;
        private String writer;
        private String title;
        private String content;

    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostResDto{
        private String id;
        private String writer;
        private String title;
        private String content;
        private Comment.CommentResDto comment;
    }

}