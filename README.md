### [Spring Boot에서 Redis로 세션 클러스터링 구축](https://stir.tistory.com/256)


# 캐싱 전략(Caching Strategies)

## 읽기전략   
   
### Look Aside(지연로딩)   
   
Redis에 1차적으로 조회 후 값이 없으면 DB 조회   
   
특징 1. Redis 서버가 죽어도 괜찮다.   
특징 2. Redis 서버가 죽거나 애플리케이션 처음 구동 시에 DB로부터 Redis에 미리 캐시를 올려놔야 성능 이슈없음.   

## 쓰기전략

### Write Around
모든 데이터는 DB에 저장하고 후에 캐시 요청이 있을 시 캐시가 존재하지 않으면 DB에서 꺼내서 캐시에 적재한다.   
특징 1. 쓰기 작업에 부담이 없다.   
특징 1. 쓰는 작업이 상대적으로 적은 경우에 유용하다.
특징 2. 읽기 성능에서 캐시 요청이 빈번한 경우 성능 저하가 발생한다.

### Write Through

모든 데이터는 Redis에 저장 후에 바로 DB에도 저장한다.
특징 1. 이용하지 않을 데이터도 캐시에 들어갈 수 있어서 상대적으로 느리며 메모리 낭비가 발생한다.
특징 2. 값의 관리를 위해 expired Time 걸어야 한다.

![image](https://github.com/stir084/RedisStudy/assets/47946124/17cc3ff1-1b30-4917-9d03-5184764baea2)
