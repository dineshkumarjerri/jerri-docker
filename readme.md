# Microservices Basic Building blocks

## Ports
Applications	Ports  
Limits Service	8080, 8081, ...  
Spring Cloud Config Server	8888  
Currency Exchange Service	8000, 8001, 8002, ...   
Currency Conversion Service	8100, 8101, 8102, ...  
Netflix Eureka Naming Server	8761   
Netflix Zuul API Gateway Server	8765   
Zipkin Distributed Tracing Server	9411

## Applications URLs
Limits Service	http://localhost:8080/limits http://localhost:8080/actuator/refresh (POST)  
Spring Cloud Config Server	http://localhost:8888/limits-service/default http://localhost:8888/limits-service/dev   
Currency Converter Service - Direct Call	http://localhost:8100/currency-converter/from/USD/to/INR/quantity/10   
Currency Converter Service - Feign	http://localhost:8100/currency-converter-feign/from/EUR/to/INR/quantity/10000   
Currency Exchange Service	http://localhost:8000/currency-exchange/from/EUR/to/INR   
http://localhost:8001/currency-exchange/from/USD/to/INR    
Eureka	http://localhost:8761/    
Zuul - Currency Exchange & Exchange Services	http://localhost:8765/currency-exchange-service/currency-exchange/from/EUR/to/INR http://localhost:8765/currency-conversion-service/currency-converter-feign/from/USD/to/INR/quantity/10    
Zipkin	http://localhost:9411/zipkin/   
Spring Cloud Bus Refresh	http://localhost:8080/actuator/bus-refresh (POST)

## Feign Client:
@EnableFeignClients -- add this annotation to boot main class
### Dependency
```
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
## NamingServer/Service Registry:
@EnableEurekaServer -- add this annotation to boot main class
### Dependency
```
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

### Properties
eureka.client.register-with-eureka=false; -- add this to avoid self registration
eureka.client.fetch-registry=false

### netflix eureka server for client services -- below dependency will make the service registered to the naming server
#### this netflix eureka client is default giving us the client side load balancing
```
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
##### Properties
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka -- add this for default to

### Load Balancing
* is default given here by netflix eureka client
* earlier ribbon was used for load balancing

## Spring Cloud API Gateway
### Common features
    Authentication 
    Authorization 
    logging
    rate limiting

<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

### Properties
spring.cloud.gateway.discovery.locator.enabled=true  -- add this to discover other ms   
spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true -- ad this for lower case gateway

## Circuit Breaker Framework Resilience4j(netflix hystrix was used earlier)
* to give fallback response if a service is down
* to implement circuit breaker pattern to reduce load
* to retry requests in case of temporary failures
* to implement rate limiting

       <dependency>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-aop</artifactId>
  	</dependency>
  	<dependency>
  		<groupId>io.github.resilience4j</groupId>
  		<artifactId>resilience4j-spring-boot3</artifactId>
  	</dependency>

### Retry
@Retry(name = "sample-api", fallbackMethod = "hardcodedResponse") -- annotate rest method
### Properties
resilience4j.retry.instances.sample-api.maxRetryAttempts=5   
resilience4j.retry.instances.sample-api.waitDuration=1s   
resilience4j.retry.instances.sample-api.enableExponentialBackoff=true


### Circuit breaker
@CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
### Properties
#resilience4j.circuitbreaker.instances.default.failureRateThreshold=90 -- if 90% calls getting failed


### Rate limiter
@RateLimiter(name="default")
### Properties
resilience4j.ratelimiter.instances.default.limitForPeriod=2   
resilience4j.ratelimiter.instances.default.limitRefreshPeriod=10s


### Concurrent Requests
@Bulkhead(name="sample-api")
### Properties
resilience4j.bulkhead.instances.default.maxConcurrentCalls=10  
resilience4j.bulkhead.instances.sample-api.maxConcurrentCalls=10




# Docker
## Zipkin -- to trace all the logs of ms
```
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-observation</artifactId>
</dependency>

<!-- OPTION 1: Open Telemetry as Bridge (RECOMMENDED) -->
<!-- Open Telemetry 
    - Simplified Observability (metrics, logs, and traces) -->

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>

<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-zipkin</artifactId>
</dependency>
```

### Additional dependency for feign from sb3
```
<!-- COMMON CHANGES + -->
<!-- Enables tracing of REST API calls made using Feign - V3 ONLY-->
<dependency>
	<groupId>io.github.openfeign</groupId>
	<artifactId>feign-micrometer</artifactId>
</dependency>
```

## Build image using maven plugin
```
<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<image>
						<name>dineshjerri/mmv3-${project.artifactId}:${project.version}</name>
					</image>
					<pullPolicy>IF_NOT_PRESENT</pullPolicy>
				</configuration>
			</plugin>
		</plugins>
	</build>
```
