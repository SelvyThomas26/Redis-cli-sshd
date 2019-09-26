package com.rambus.cmks.cli.common;

import java.io.Serializable;

/** Event object to capture changes in log level, configuration etc */
public class Events implements Serializable {
  /** */
  private static final long serialVersionUID = 7376078999122486990L;

  public enum Destination {
    DAS, KIS, LIS, KSC, CPH, CAL
  }

  public enum EventName {
    LOG_LEVEL_CHANGE, KIS_PROCESS_FILE, LIS_PROCESS_FILE
  }

  private Destination destination;
  private EventName eventName;
  private String eventValue;

  /** @return the destination */
  public Destination getDestination() {
    return destination;
  }

  /** @param destination the destination to set */
  public void setDestination(Destination destination) {
    this.destination = destination;
  }

  /** @return the eventName */
  public EventName getEventName() {
    return eventName;
  }

  /** @param eventName the eventName to set */
  public void setEventName(EventName eventName) {
    this.eventName = eventName;
  }

  /** @return the eventValue */
  public String getEventValue() {
    return eventValue;
  }

  /** @param eventValue the eventValue to set */
  public void setEventValue(String eventValue) {
    this.eventValue = eventValue;
  }

  @Override
  public String toString() {
    return "Events [destination=" + destination + ", eventName=" + eventName + ", eventValue="
        + eventValue + "]";
  }

}
