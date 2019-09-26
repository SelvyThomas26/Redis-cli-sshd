/* Copyright @ Rambus */
package com.rambus.cmks.cli.service;

/** Event Handler interface for subscription events. */
public interface EventHandler {
  /**
   * Handle the message received through subscription.
   *
   * @param message - the event message to be processed
   */
  void handleEvent(String message);
}
