package com.rambus.cmks.cli.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/** RedisUser object to store key,value */
@RedisHash("RedisUser")
public class RedisUser implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private @Id String id;
  private Map<String, String> redisValueMap = new HashMap<>();

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the redisValueMap
   */
  public Map<String, String> getRedisValueMap() {
    return redisValueMap;
  }

  /**
   * @param redisValueMap the redisValueMap to set
   */
  public void setRedisValueMap(Map<String, String> redisValueMap) {
    this.redisValueMap = redisValueMap;
  }

}
