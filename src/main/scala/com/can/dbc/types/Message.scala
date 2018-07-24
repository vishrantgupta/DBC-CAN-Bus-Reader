package com.can.dbc.types

import scala.collection.mutable.ListBuffer

/** Parsed DBC message
  *
  * @author Vishrant Gupta
  *
  */
class Message {

  protected var _notes: String = _
  protected var _producer: Producer = _
  protected var _multiplex: ListBuffer[Multiplex] = ListBuffer.empty
  protected var _signal: ListBuffer[Signal] = ListBuffer.empty
  protected var _id: String = _
  protected var _name: String = _
  protected var _length: String = _
  protected var _interval: Int = _
  protected var _triggered: Boolean = _
  protected var _count: Int = _
  protected var _format: String = _
  protected var _remote: Boolean = _

  def notes: String = _notes

  def notes_=(value: String): Unit = this._notes = value

  def producer: Producer = _producer

  def producer_=(value: Producer): Unit = this._producer = value

  def multiplex: ListBuffer[Multiplex] = _multiplex

  def signal: ListBuffer[Signal] = _signal

  def id: String = _id

  def id_=(value: String): Unit = this._id = value

  def name: String = _name

  def same_=(value: String): Unit = this._name = value

  def length: Int = if (Option(_length).isDefined) Integer.valueOf(_length) else 0

  def length_=(value: String): Unit = this._length = value

  def interval: Int = if (Option(_interval).isDefined) _interval else 0

  def interval_=(value: Integer): Unit = this._interval = value

  def isTriggered: Boolean = _triggered

  def isTriggered_=(value: Boolean): Unit = this._triggered = value

  def count: Int = if (Option(_count).isDefined) _count else 0

  def count_=(value: Integer): Unit = this._count = value

  def format: String = if (Option(_format).isDefined) _format else "standard"

  def format_=(value: String): Unit = this._format = value

  def isRemote: Boolean = _remote

  def isRemote_=(value: Boolean): Unit = this._remote = value

  def canEqual(other: Any): Boolean = other.isInstanceOf[Message]

  override def equals(other: Any): Boolean = other match {
    case that: Message =>
      (that canEqual this) &&
        _id == that._id &&
        _name == that._name &&
        _length == that._length
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(_id, _name, _length)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
