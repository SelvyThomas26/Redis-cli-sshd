package com.rambus.cmks.cli;

import com.rambus.cmks.cli.service.impl.RedisEventListener;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@SpringBootApplication
public class Application {

  private static final Logger logger = LogManager.getLogger(Application.class);

  private static final String REDIS_HOST = "REDIS_HOST";
  private static final String REDIS_TYPE = "REDIS_TYPE";
  private static final String REDIS_PORT = "REDIS_PORT";
  private static final String REDIS_EVENT_NAME = "event";
  private static final String DEFAULT_CACHE_NAME = "redis";

  private Map<String, Integer> cacheMap;

  @Autowired
  private Environment env;

  @Value("${redis.cache.host}")
  private String host;

  @Value("${redis.cache.port}")
  private int port;

  @Value("${redis.cache.type}")
  private String type;

  @Value("${redis.cache.sentinel.master}")
  private String sentinelMasterName;

  @Bean(name = "lettuceConnectionFactory")
  public LettuceConnectionFactory lettuceConnectionFactory() {
    if (this.type != null && "standalone".contentEquals(this.type)) {
      logger.info("Using RedisStandaloneConfiguration in LettuceConnectionFactory");
      return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    } else {
      logger.info("Using RedisSentinelConfiguration in LettuceConnectionFactory");
      return new LettuceConnectionFactory(new RedisSentinelConfiguration()
          .master(this.sentinelMasterName).sentinel(this.host, this.port));
    }
  }

  @Bean
  RedisMessageListenerContainer container(LettuceConnectionFactory lettuceConnectionFactory,
      MessageListenerAdapter listenerAdapter) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(lettuceConnectionFactory);
    container.addMessageListener(listenerAdapter, new PatternTopic(REDIS_EVENT_NAME));
    return container;
  }

  @Bean
  MessageListenerAdapter listenerAdapter(RedisEventListener eventListener) {
    return new MessageListenerAdapter(eventListener, "addEventHandler");
  }

  @Bean
  StringRedisTemplate template(LettuceConnectionFactory lettuceConnectionFactory) {
    return new StringRedisTemplate(lettuceConnectionFactory);
  }

  /**
   * Create a RedisCacheManager with list of caches.
   * 
   * @return RedisCacheManager
   */
  @Bean
  public RedisCacheManager cacheManager() {
    if (cacheMap == null || cacheMap.isEmpty()) {
      Set<String> defaultCacheNameSet = new HashSet<>();
      defaultCacheNameSet.add(DEFAULT_CACHE_NAME);
      return RedisCacheManager.builder(lettuceConnectionFactory())
          .cacheDefaults(defaultCacheConfiguration()).initialCacheNames(defaultCacheNameSet)
          .build();
    }
    Map<String, RedisCacheConfiguration> cacheNamesConfigurationMap = new HashMap<>();
    cacheMap.entrySet()
        .forEach(entry -> cacheNamesConfigurationMap.put(entry.getKey(), RedisCacheConfiguration
            .defaultCacheConfig().entryTtl(Duration.ofSeconds(entry.getValue()))));
    return new RedisCacheManager(redisCacheWriter(), defaultCacheConfiguration(),
        cacheNamesConfigurationMap);
  }

  private RedisCacheConfiguration defaultCacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(600))
        .disableCachingNullValues();
  }

  private RedisCacheWriter redisCacheWriter() {
    return RedisCacheWriter.lockingRedisCacheWriter(lettuceConnectionFactory());
  }


  /** Initialize method for redis configuration. */
  @PostConstruct
  public void init() {
    String redisType = env.getProperty(REDIS_TYPE);
    logger.debug("Environment variable REDIS_TYPE is : " + redisType);
    if (redisType != null && !redisType.isEmpty()) {
      logger.info("Setting Redis type as : " + redisType);
      this.type = redisType;
    }

    String redisHost = env.getProperty(REDIS_HOST);
    logger.debug("Environment variable REDIS_HOST is : " + redisHost);
    if (redisHost != null && !redisHost.isEmpty()) {
      logger.info("Setting Redis host as : " + redisHost);
      this.host = redisHost;
    }

    String redisPort = env.getProperty(REDIS_PORT);
    logger.debug("Environment variable REDIS_PORT is : " + redisPort);
    if (redisPort != null && !redisPort.isEmpty()) {
      logger.info("Setting Redis host as : " + redisPort);
      this.port = Integer.parseInt(redisPort);
    }
  }

  public static void main(String[] args) throws InterruptedException {
    SpringApplication.run(Application.class, args);
    Thread.currentThread().join();
  }
}
