/**
 * 
 */

package com.rambus.cmks.cli.service;

import org.springframework.stereotype.Service;

/**
 * Interface for Cache operations.
 */
@Service
public interface CacheService {

  /**
   * Put a Value in to a cache using a given key.
   * 
   * @param cacheName the name of the cache
   * @param key the key for inserting value
   * @param value the value to inserted to cache
   */
  public void put(String cacheName, String key, String value);

  /**
   * Get the value from the cache using the key.
   * 
   * @param cacheName the name of the cache
   * @param key the key for inserting value
   * @return the value from cache, null if key not present in cache.
   */
  public String get(String cacheName, String key);

  /**
   * Evict a key from the given cache.
   * 
   * @param cacheName the name of the cache
   * @param key the key for eviction
   */
  public void evict(String cacheName, String key);

}
