/* Copyright @ Rambus */
package com.rambus.cmks.cli.service;

/** Event Listener interface. */
public interface EventListener {
  /**
   * This method should be used by the implementation to process the message.
   *
   * @param message the event string to process
   */
  public void processEvent(String message);

  /**
   * Add the event handlers to event handler list.
   *
   * @param eventHandler the eventHandler to add
   */
  public void addEventHandler(EventHandler eventHandler);
}
