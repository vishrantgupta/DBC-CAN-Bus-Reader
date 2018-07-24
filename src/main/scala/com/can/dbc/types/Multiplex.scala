package com.can.dbc.types

/** Multiplexed CAN message
  *
  * @author Vishrant Gupta
  *
  */
class Multiplex extends BasicSignalType {

  protected var _muxGroup: List[MuxGroup] = List.empty
  protected var _notes: String = _
  protected var _consumer: Consumer = _
  protected var _value: Value = _

  def muxGroup: List[MuxGroup] = _muxGroup

  def notes: String = _notes

  def notes_=(value: String): Unit = this._notes = value

  def consumer: Consumer = _consumer

  def consumer_=(value: Consumer): Unit = this._consumer = value

  def value: Value = _value

  def value_=(value: Value): Unit = this._value = value

}
