package com.rambus.cmks.cli.sshd;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import com.rambus.cmks.cli.common.Events;
import com.rambus.cmks.cli.common.Events.Destination;
import com.rambus.cmks.cli.common.Events.EventName;
import com.rambus.cmks.cli.domain.RedisUser;
import com.rambus.cmks.cli.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;

/** The Test class for EventCommand. */
public class EventCommandTest {

  // String constants
  private static final String KEY = "cmks.lis";
  private static final String VALUEKEY = "rootlogger.level";
  private static final String LIS = "lis";
  private static final String INFO = "info";
  private static final String VALUE = "loggerinfo";

  @InjectMocks
  private EventCommand eventCommand;
  @Mock
  private UserRepository userRepository;
  @Mock
  private StringRedisTemplate template;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for storeLog method.
   */
  @Test
  public void logStore() {

    RedisUser redisUser = new RedisUser();
    Map<String, String> innerHash = new HashMap<>();
    redisUser.setId(KEY);
    innerHash.put(VALUEKEY, VALUE);
    redisUser.setRedisValueMap(innerHash);

    when(userRepository.findById(KEY)).thenReturn(Optional.of(redisUser));
    when(userRepository.save(redisUser)).thenReturn(redisUser);
    RedisUser redisUserGet = eventCommand.serviceType(LIS, INFO);
    assertThat(redisUserGet, is(sameInstance(redisUser)));
  }

  /**
   * Test method for eventPublish method.
   */
  @Test
  public void sentEvent() {

    Events event = new Events();
    event.setDestination(Destination.LIS);
    event.setEventName(EventName.LOG_LEVEL_CHANGE);
    event.setEventValue(INFO);
    Events eventSent = eventCommand.sentEvent(LIS, INFO);
    assertEquals(event.getEventValue(), eventSent.getEventValue());
  }
}
