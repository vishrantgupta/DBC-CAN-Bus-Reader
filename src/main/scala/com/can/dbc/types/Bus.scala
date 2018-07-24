package com.can.dbc.types

import scala.collection.mutable.ListBuffer

/** Bus for CAN
  *
  * Wikipedia: https://en.wikipedia.org/wiki/CAN_bus
  *
  * @author Vishrant Gupta
  *
  */
class Bus {

  // list of all the messages
  protected val _message: ListBuffer[Message] = ListBuffer.empty

  // Bus name
  protected var _name: String = _

  // baud rate of bus
  // Section 5.1.15 Autobaud Loopback
  // Introduction to the Controller Area Network (CAN) [http://www.ti.com/lit/an/sloa101b/sloa101b.pdf]
  // Standard baud rates are 125 kbit/s, 250 kbit/s, 500 kbit/s and 1 Mbit/s.
  protected var _baudrate: Integer = _

  def message: ListBuffer[Message] = this._message

  def name: String = _name

  def name_=(value: String): Unit = this._name = value

  def baudrate: Int = if (Option(_baudrate).isDefined) _baudrate else 500000

  def baudrate_=(value: Integer): Unit = this._baudrate = value

}

