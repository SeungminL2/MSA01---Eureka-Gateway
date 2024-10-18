[ Service01, Service02 ]
	|-> Spring Starter Project
	|	|-> Type:  Maven
	|	|-> Java Version: 17
	|	|-> Package: pack
	|
	|-> Dependency
	|	|-> Spring Boot DevTools
	|	|-> Spring Web
	|	|-> Lombok
	|	|-> Thymeleaf (?)
	|	|-> Eureka Discovery Client
	|	|-> Gateway
	|
	|-> pom.xml
	|	|-> https를 http로 수정
	|
	|-> application.yml
	|	|-> !! 아래 참고 !!
	|
	|-> Service01Application.java
	|	|-> @EnableDiscoveryClient
	|
	|-> templates >> index.html 생성
	|
	|-> Service01Controller 생성


*** application.yml
____________________________________________________________________
spring:
  application:
    name: Service01
    # 유레카 서버에 등록되는 서비스 명
    # Service02에서는 바꿔야함
server:
  port: 8081
  # Service02에서는 8082로 해야됨

# eureka.instance.prefer-ip-address=true
eureka:
  client:
    register-with-eureka: true
      # 유레카 디스커버리 서버의 등록
    fetch-registry: true
      # defaultZone의 유레카 서버에서 클라이언트 정보를 가져옴
  instance:
    prefer-ip-address: true
____________________________________________________________________


========================================================================

[ EurekaServer ]
	|-> Dependency
	|	|-> Spring Boot DevTools
	|	|-> Spring Web
	|	|-> Eureka Server
	|
	|-> pom.xml
	|	|-> https를 http로 수정
	|
	|-> application.yml
	|	|-> !! 아래 참고 !!
	|
	|-> EurekaServerApplication.java
	|	|-> @EnableEurekaServer
	|
	|-> Boot Dashboard에서 모두 실행 >> Eureka 대시보드 (localhost:8761)
		|-> 서비스 목록에 GatewayServer, Service01, 02 있어야 함


*** application.yml
_______________________________________________________________________________
spring:
  application:
    name: EurekaServer

server:
  port: 8761
eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
    registryFetchIntervalSeconds: 5
_______________________________________________________________________________


========================================================================

[ GatewayServer ]
	|-> Dependency
	|	|-> Spring Boot DevTools
	|	|-> Spring Web
	|	|-> Eureka Discovery Client ---> GatewayServer도 Eureka에 등록되어야함
	|	|-> Gateway
	|	
	|-> pom.xml
	|	|-> https를 http로 수정
	|	|-> (line 36) 뒤에 flux 붙임
	|	|	|-> <artifactId>spring-boot-starter-webflux</artifactId>
	|	|-> (line 40) 뒤에 -mvc 지움
	|		|-> <artifactId>spring-cloud-starter-gateway</artifactId>
	|
	|-> application.yml
	|	|-> !! 아래 참고 !!
	|
	|-> GatewayServerApplication.java
	|	|-> @EnableDiscoveryClient
	|
	|-> localhost:8765로 접속
			|-> 뒤에  '/tiger'이면  Service01의 컨트롤러로 이동
			|-> 뒤에  '/lion'이면  Service02의 컨트롤러로 이동
		
	*** Gateway
		|->  1) 단일 진입점
		|		|-> 클라이언트는 각 서비스의 주소, 포트번호을 몰라도 됨
		|		|	|-> 클라이언트는 Gateway에만 요청을 보냄
		|		|	|-> Gateway는 내부 서비스로 라우팅함
		|		|-> 보안성
		|		|	|-> Gateway에서 일괄적으로 보안관리
		|		|		|-> 각 서비스마다 개별적인 보안 설정 안해도 됨
		|		|-> 공통 기능 처리
		|			|-> 로깅, 모니터링, 요청/응답 변환 등
		|->  2) Eureka와 연동
				|-> Gateway는 동적으로 서비스 위치를 가져와 요청을 라우팅할 수 있음
				|-> 새로운 서비스가 추가되거나 제거되면 Gateway는 이를 인식함


*** application.yml
_____________________________________________________________________________________________
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      routes:
        # routes(라우트) : 응답을 보낼 목적지 URI, 필터 항목을 식별하기 위한 ID
        #  |-> http://localhost:8081 (Service01의 포트번호)
        - id: first-service
          uri: lb://SERVICE01
               # lb :  load balancing,  Service02쪽으로 라우팅
          predicates:
          # predicates(조건자) : 요청을 처리하기전 HTTP 요청이 정의된 조건에 부합하는지 검사
          #  |-> http://localhost:8765/t1로 접근 들어온다면 (8765 : GatewayServer 포트번호)
            - Path=/service01/**
          # filters도 있음
        - id: second-service
          uri: lb://SERVICE02
          predicates:
            - Path=/service02/**

eureka:
  instance:
    non-secure-port: ${server.port}
    prefer-ip-address: true
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

server:
  port: 8765
