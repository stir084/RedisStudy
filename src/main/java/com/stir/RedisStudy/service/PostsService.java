package com.stir.RedisStudy.service;


import com.stir.RedisStudy.service.rdb.Comment;
import com.stir.RedisStudy.service.rdb.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostsService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    public void savePost(String postId, String writer, String title, String content) {

        HashOperations<String, String, String> hop = redisTemplate.opsForHash();
        hop.put("posts", postId, writer);
        hop.put("post:" + postId, "WRITER", writer);
        hop.put("post:" + postId, "TITLE", title);
        hop.put("post:" + postId, "CONTENT", content);
    }

    public void saveComment(String commentId, String postId, String writer, String content, String timestamp) {
        HashOperations<String, String, String> hop = redisTemplate.opsForHash();
        hop.put("comments", commentId, postId); // postId를 외래키로 연결
        hop.put("comment:" + commentId, "POST_ID", postId);
        hop.put("comment:" + commentId, "WRITER", writer);
        hop.put("comment:" + commentId, "CONTENT", content);
        hop.put("comment:" + commentId, "TIMESTAMP", timestamp);
    }

    public Post.PostResDto getPostAndComments(String postId) {
        HashOperations<String, String, String> hop = redisTemplate.opsForHash();
        Map<String, String> postData = hop.entries("post:" + postId);
        Map<String, String> commentData = new HashMap<>();

        String commentsKey = "comments";
        if (hop.hasKey(commentsKey, postId)) {
            String commentId = hop.get(commentsKey, postId);
            commentData = hop.entries("comment:" + commentId);
        }

        Post.PostResDto post = new Post.PostResDto();
        post.setId(postId);
        post.setWriter(postData.get("WRITER"));
        post.setTitle(postData.get("TITLE"));
        post.setContent(postData.get("CONTENT"));

        Comment.CommentResDto comment = new Comment.CommentResDto();

        comment.setPostId(commentData.get("POST_ID"));
        comment.setWriter(commentData.get("WRITER"));
        comment.setContent(commentData.get("CONTENT"));
        comment.setTimestamp(commentData.get("TIMESTAMP"));
        post.setComment(comment);


        return post;
    }
}