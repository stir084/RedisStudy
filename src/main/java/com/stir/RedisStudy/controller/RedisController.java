package com.stir.RedisStudy.controller;

import com.stir.RedisStudy.service.PostsService;
import com.stir.RedisStudy.service.RedisBrpopService;
import com.stir.RedisStudy.service.rdb.Comment;
import com.stir.RedisStudy.service.rdb.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class RedisController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisBrpopService redisBrpopService;

    @Autowired
    private PostsService postsService;

    /**
     * 세션 클러스터링 테스트
     *
     * @param session
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpSession session) {
        UUID uid = Optional.ofNullable(UUID.class.cast(session.getAttribute("uid")))
                .orElse(UUID.randomUUID());
        session.setAttribute("uid", uid);
        return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>("로그아웃 성공", HttpStatus.OK);
    }

    /**
     * 각종 데이터 타입 입력 및 출력
     *
     * @return
     */
    @PostMapping("/data")
    public ResponseEntity<?> addRedisKey() {
        // String
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set("stir", "developer");
        System.out.println("stir type is " + redisTemplate.type("stir")); // cli : TYPE stir


        // List
        // 스택, 큐, 메시지 큐 등을 구현할 수 있음
        // ex) leftPush 후 rightPop하면 선입선출 방식의 Queue 기능 구현
        ListOperations<String, String> lop = redisTemplate.opsForList();
        lop.leftPush("loose", "isStir");
        System.out.println("loose type is " + redisTemplate.type("loose")); // cli : TYPE loose

        // Hash
        HashOperations<String, String, String> hop = redisTemplate.opsForHash();
        hop.put("userInfo", "name", "stir");
        hop.put("userInfo", "age", "30");

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/data/{key}")
    public ResponseEntity<?> getRedisKey(@PathVariable String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String value = vop.get(key);
        return new ResponseEntity<>(value, HttpStatus.OK);
    }


    /**
     * <p>조회수 구현</p>
     * Redis의 INCR 명령을 사용하여 조회수를 구현하는 방식은
     * 여러 사람이 동시에 접속하는 경우에도 데이터 정합성을 보장합니다.
     *
     * @return view-count
     */
    @GetMapping("/view-count")
    public ResponseEntity<?> viewCount() {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();

        vop.increment("view-count"); // cli : INCR view-count
        String viewCount = vop.get("view-count");
        System.out.println(viewCount);
        return new ResponseEntity<>(viewCount, HttpStatus.OK);
    }

    /**
     * <p>메시지큐 구현</p>
     * List를 이용해서 구현할 수 있다.
     * BRPOP은 데이터를 꺼내면서 blocking을 거는 것이며 데이터가 들어올 때까지 대기한다.
     * 아래는 BRPOP에 대한 구현이며 CLI에서 LPUSH를 해줘야한다.
     * <p>
     * 아래 Get 요청 후 CLI에서 LPUSH msgTest "value1"
     */
    @GetMapping("/message-queue")
    public ResponseEntity<?> messageQueue() {
        redisBrpopService.brpopExample("msgTest");

        return ResponseEntity.ok("BRPOP 실행 중입니다.");
    }

    /**
     * RDBMS 타입의 데이터 캐싱하기
     * 컬럼 출력 - HGET post:1 TITLE
     * 레코드 출력 - HGETALL post:1
     */
    @PostMapping("/posts")
    public void insertTestData() {
        // 게시물 등록
        Post.PostReqDto post1 = new Post.PostReqDto("1", "John", "Redis is awesome", "Redis is a powerful in-memory data store.");
        Post.PostReqDto post2 = new Post.PostReqDto("2", "Alice", "Introduction to Redis", "Redis is a key-value store.");
        postsService.savePost(post1.getId(), post1.getWriter(), post1.getTitle(), post1.getContent());
        postsService.savePost(post2.getId(), post2.getWriter(), post2.getTitle(), post2.getContent());

        // 댓글 등록
        Comment.CommentReqDto comment1 = new Comment.CommentReqDto("1", "1", "Jane", "I agree!", "2023-07-25 12:34:56");
        Comment.CommentReqDto comment2 = new Comment.CommentReqDto("2", "1", "Bob", "Great post!", "2023-07-25 13:45:30");
        postsService.saveComment(
                comment1.getId(),
                comment1.getPostId(),
                comment1.getWriter(),
                comment1.getContent(),
                comment1.getTimestamp()
        );
        postsService.saveComment(
                comment2.getId(),
                comment2.getPostId(),
                comment2.getWriter(),
                comment2.getContent(),
                comment2.getTimestamp()
        );
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostAndComments(@PathVariable String postId) {
        return new ResponseEntity<>(postsService.getPostAndComments(postId), HttpStatus.OK);
    }
}
