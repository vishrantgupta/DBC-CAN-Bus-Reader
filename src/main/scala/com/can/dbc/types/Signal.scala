package com.can.dbc.types

/** BUS signal
  *
  * @author Vishrant Gupta
  *
  */
class Signal extends BasicSignalType {

  protected var _notes: String = _
  protected var _consumer: Consumer = _
  protected var _value: Value = _
  protected var _labelSet: LabelSet = _

  def notes: String = _notes

  def notes_=(value: String): Unit = this._notes = value

  def consumer: Consumer = _consumer

  def consumer_=(value: Consumer): Unit = this._consumer = value

  def value: Value = _value

  def value_=(value: Value): Unit = this._value = value

  def labelSet: LabelSet = _labelSet

  def labelSet_=(value: LabelSet): Unit = this._labelSet = value
}
