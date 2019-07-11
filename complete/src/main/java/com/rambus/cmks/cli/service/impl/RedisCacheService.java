package com.rambus.cmks.cli.service.impl;

import com.rambus.cmks.cli.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

/**
 * Redis implementation for Cache Service.
 */
@Service
public class RedisCacheService implements CacheService {


  @Autowired
  private RedisCacheManager rcacheManager;

  @Override
  public void put(String cacheName, String key, String value) {
    rcacheManager.getCache(cacheName).put(key, value);

  }

  @Override
  public String get(String cacheName, String key) {
    ValueWrapper valueWrapper = rcacheManager.getCache(cacheName).get(key);
    if (valueWrapper != null) {
      return (String) valueWrapper.get();
    }
    return null;
  }

  @Override
  public void evict(String cacheName, String key) {
    rcacheManager.getCache(cacheName).evict(key);
  }

}
