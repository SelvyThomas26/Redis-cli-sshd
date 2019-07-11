package com.rambus.cmks.cli.sshd;

import com.rambus.cmks.cli.common.Events;
import com.rambus.cmks.cli.common.Events.Destination;
import com.rambus.cmks.cli.common.Events.EventName;
import com.rambus.cmks.cli.domain.RedisUser;
import com.rambus.cmks.cli.repository.UserRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import sshd.shell.springboot.autoconfiguration.SshdShellCommand;

/** EventCommand - used for store and propagate event */
@Component
@SshdShellCommand(value = "event",
    description = "event log values. Type 'event help' for supported subcommands")
public class EventCommand {
  private static final String ROOT_LOGGER = "rootlogger.level";
  private static final String CMKS = "cmks";
  private static final String REDIS_EVENT_NAME = "event";

  @Autowired
  private StringRedisTemplate template;
  @Autowired
  private UserRepository userRepository;

  String cmdType;
  String cmdValue;

  /**
   * Store logs and events publish.
   * 
   * @param getString the key ,value string.
   * @return the string that store and publish event.
   */
  @SshdShellCommand(value = "log", description = "store log. Usage: event log -d<arg> -v<arg>")
  public String logEventAndStore(String getString) {
    String[] splitted = getString.split(" ");
    Arrays.stream(splitted).forEach(string -> {
      if (string.startsWith("-d")) {
        cmdType = string.substring(2);
      } else if (string.startsWith("-v")) {
        cmdValue = string.substring(2);
      }
    });
    Stream.of(Destination.values()).forEach(types -> {
      if (types.name().equalsIgnoreCase(cmdType)) {
        serviceType(cmdType, cmdValue);
      }
    });
    Events event = sentEvent(cmdType, cmdValue);
    return "key, value to Store: " + getString + " and event: " + event;
  }

  /**
   * Store log command as key value pair.
   * 
   * @return the redisUser object
   */
  public RedisUser serviceType(String type, String value) {
    Map<String, String> valueMap = null;
    RedisUser redisUser = null;
    Optional<RedisUser> redisUserGet = userRepository.findById(CMKS.concat("." + type));
    if (redisUserGet.isPresent()) {
      valueMap = generateMap(value, redisUserGet.get().getRedisValueMap());
      redisUserGet.get().setRedisValueMap(valueMap);
      redisUser = redisUserGet.get();
    } else {
      valueMap = generateMap(value, null);
      redisUser = new RedisUser();
      redisUser.setId(CMKS.concat("." + type));
      redisUser.setRedisValueMap(valueMap);
    }
    return userRepository.save(redisUser);
  }

  /**
   * Create value map.
   * 
   * @param getValueMap the value map.
   * @param value the value to update.
   * @return the value map.
   */
  public Map<String, String> generateMap(String value, Map<String, String> getValueMap) {
    Map<String, String> valueMap = new HashMap<>();
    if (getValueMap != null && !getValueMap.isEmpty()) {
      getValueMap.entrySet().stream().forEach(entry -> {
        if (entry != null && entry.getKey().contentEquals(ROOT_LOGGER)) {
          entry.setValue(value);
        }
      });
      if (!getValueMap.containsKey(ROOT_LOGGER)) {
        getValueMap.put(ROOT_LOGGER, value);
      }
      valueMap = getValueMap;
    } else {
      valueMap.put(ROOT_LOGGER, value);
    }
    return valueMap;
  }

  /**
   * Set the event object to publish.
   *
   * @return the event object
   */
  public Events sentEvent(String type, String value) {
    Events event = new Events();
    Arrays.stream(Destination.values()).forEach(types -> {
      if (types.name().equalsIgnoreCase(type)) {
        event.setDestination(types);
        event.setEventName(EventName.LOG_LEVEL_CHANGE);
        event.setEventValue(value);
        template.convertAndSend(REDIS_EVENT_NAME, event.toString());
      }
    });
    return event;
  }

  @SshdShellCommand(value = "help", description = "Usage: event help")
  public String helpCommand(String arg) {
    return "To store log and publish event. Usage : event log -d<arg> -v<arg>";
  }
}

