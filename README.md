### [Spring Boot에서 Redis로 세션 클러스터링 구축](https://stir.tistory.com/256)


# 캐싱 전략(Caching Strategies)

## 읽기전략   
   
### Look Aside(지연로딩)  
   
가장 일반적인 방법이며 Redis에 1차적으로 조회 후 값이 없으면 DB 조회하는 방식이다.   
   
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

# Redis 아키텍처

## Replication 
마스터 노드에 대한 슬레이브 노드를 복제본으로 두어서 마스터 노드가 죽었을 경우 슬레이브 노드를 사용하고 싶을때 사용한다.

## Sentinel
마스터 노드에 대한 슬레이브 노드들이 존재하고 Sentinal 노드가 마스터 노드가 비정상이라고 판단할 경우 페일오버(컴퓨터 서버, 시스템, 네트워크 등에서 이상이 생겼을 때 예비 시스템으로 자동전환되는 기능   
) 기능으로 인해 슬레이브 노드를 마스터 노드로 승격시켜 사용한다.   
Sentinal 노드는 홀수로 운영되며 과반수가 찬성을 해야 슬레이브 노드를 마스터 노드로 승격시킨다.   
이러한 기능으로 인해 고가용성 HA(High Availiablity) 기능을 보장할 수 있다.   

## Cluster
여러 마스터 노드에 데이터를 분산 저장해서 가용성을 향상시키는 아키텍처다.
분산 저장 방법에는 샤딩과 Consistent Hashing 기능이 대표적이다.
샤딩은 데이터를 분산하여 여러 DB에 저장하는 기술이다. 
하나의 마스터 노드가 죽을 경우 데이터가 유실되기 때문에 각각의 마스터 노드들은 데이터에 대한 복제본인 Replication을 같이 두어서 사용한다.   
Consistent Hashing 방법으로 데이터를 분산 저장한다.   
Consistent Hashing은 데이터의 일관성과 효율적인 재배치를 중시하는 반면, 샤딩은 데이터베이스의 확장성과 부하 분산을 중시합니다


![image](https://github.com/stir084/RedisStudy/assets/47946124/93499f34-6921-4c38-adc9-0819725865cc)   

# 간단한 SQL을 대체하는 법

![image](https://github.com/stir084/RedisStudy/assets/47946124/0d7dbe57-270a-49f1-8b7b-ac42a2c564a4)   

