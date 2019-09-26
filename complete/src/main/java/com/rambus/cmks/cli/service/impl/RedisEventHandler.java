package com.rambus.cmks.cli.service.impl;

import com.rambus.cmks.cli.service.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Event Handler for Redis publish event.
 */
@Component
public class RedisEventHandler implements EventHandler {

  private static final Logger logger = LogManager.getLogger(RedisEventHandler.class);

  public RedisEventHandler(@Autowired RedisEventListener event) {
    event.addEventHandler(this);
  }

  @Override
  public void handleEvent(String message) {
    logger.info("Message: " + message);
  }

}
