package com.rambus.cmks.cli.sshd;

import com.rambus.cmks.cli.common.Events.Destination;
import com.rambus.cmks.cli.domain.RedisUser;
import com.rambus.cmks.cli.repository.UserRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sshd.shell.springboot.autoconfiguration.SshdShellCommand;

/** StoreCommand - used for store */
@Component
@SshdShellCommand(value = "store", description = "store log values")
public class StoreCommand {

  private static final String ROOT_LOGGER = "rootlogger.level";
  private static final String CMKS = "cmks";

  @Autowired
  private UserRepository userRepository;

  String cmdType;
  String cmdValue;

  /**
   * Store logs.
   * 
   * @param getString the key ,value string.
   * @return the string that store.
   */
  @SshdShellCommand(value = "log", description = "store log. Usage: store log <arg>")
  public String logShell(String getString) {

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
    return "key, value to Store: " + getString;
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
}
