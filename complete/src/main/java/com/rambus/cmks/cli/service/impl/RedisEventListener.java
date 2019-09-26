/* Copyright @ Rambus */
package com.rambus.cmks.cli.service.impl;

import com.rambus.cmks.cli.service.EventHandler;
import com.rambus.cmks.cli.service.EventListener;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

/** Event Manager for Redis publish event. */
@Service
public class RedisEventListener implements MessageListener, EventListener {

  private Logger logger = LogManager.getLogger(RedisEventListener.class);

  private List<EventHandler> eventHandlers = new ArrayList<>();

  /**
   * Get the event handlers list.
   *
   * @return the eventHandlers
   */
  public List<EventHandler> getEventHandlers() {
    return eventHandlers;
  }

  @Override
  public void addEventHandler(EventHandler eventHandler) {
    logger.info("Adding new event handler : " + eventHandler);
    this.eventHandlers.add(eventHandler);
  }

  @Override
  public void processEvent(String message) {
    this.eventHandlers.forEach(eventHandler -> eventHandler.handleEvent(message));
  }

  @Override
  public final void onMessage(Message message, byte[] pattern) {
    if (message != null) {
      this.processEvent(message.toString());
    }
  }
}
