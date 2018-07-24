
package com.can.dbc.parser

import java.io._
import java.math.BigInteger
import java.util.Calendar
import java.util.regex.Pattern

import com.can.dbc.parser.DbcReader._
import com.can.dbc.types._

import scala.collection.JavaConversions._
import scala.collection.immutable.TreeMap
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/** Reads CAN database (*.dbc) format.
  *
  * @author Vishrant Gupta
  *
  */
object DbcReader {

  // CAN database possible keywords
  private val KEYWORDS: Array[String] = Array(
    "VERSION ",
    "NS_ : ",
    "BS_:",
    "BU_: ",
    "BO_ ",
    "BO_TX_BU_ ",
    "CM_ ",
    "CM_ BO_ ",
    "CM_ EV_ ",
    "CM_ SG_ ",
    "BA_DEF_ ",
    "BA_DEF_ BU_ ",
    "BA_DEF_REL_ BU_SG_REL_ ",
    "BA_DEF_ SG_ ",
    "BA_ ",
    "EV_ ",
    "VAL_ ",
    "BA_DEF_DEF_ ",
    "BA_DEF_DEF_REL_ ",
    "VAL_TABLE_ ",
    "SIG_VALTYPE_ "
  )

  private val NOT_DEFINED: String = "Vector__XXX"

  private val ORPHANED_SIGNALS: String = "VECTOR__INDEPENDENT_SIG_MSG"

  //  private val DOC_CONTENT: String =
  //    "Converted by reading DBC schema"

  def getCanIdFromString(canIdStr: String): Int = {
    canIdStr.toInt & 0x1FFFFFFF
  }

  def isExtendedFrameFormat(canIdStr: String): Boolean = {
    ((canIdStr.toLong >>> 31 & 1) == 1)
  }

  def int32ToBigInt(big: BigInteger): BigInteger =
    if (big.signum() != -1) {
      big
    } else {
      // negative because long int value exceeds 2^31-1
      big.add(BigInteger.valueOf(4294967296L))
    }

  def bigEndianLeastSignificantBitOffset(msb: Int, length: Int): Int = {

    val pos = 7 - (msb % 8) + (length - 1)

    val lsb: Int = if (pos < 8) {
      /* msb pass a byte order */
      msb - length + 1
    } else {
      val cpos: Int = 7 - (pos % 8)
      val bytes: Int = pos / 8
      cpos + (bytes * 8) + (msb / 8) * 8
    }

    lsb
  }

  protected def splitString(s: String): Array[String] = {

    val elements: ArrayBuffer[String] = ArrayBuffer.empty

    val element: mutable.StringBuilder = new mutable.StringBuilder
    var inString: Boolean = false

    for (i <- 0 until s.length) {

      if (!inString && isDivider(s.charAt(i))) {
        if ("" != element.toString()) {
          elements += element.toString()
        }

        element.clear()
      } else if (!inString && isSymbol(s.charAt(i))) {
        /* Signed unsigned character */

        if (s.charAt(i - 2) == '@') {
          elements += element.toString()
          element.clear()

          element.append("" + s.charAt(i))

        } else {
          element.append(s.charAt(i))
        }
      } else if (isQuote(s.charAt(i))) {
        if (inString) {
          elements += element.toString()
          element.clear()
          inString = false
        } else {
          inString = true
        }
      } else {
        /* Default: add to element */
        element += s.charAt(i)
      }
    }
    if ("" != element.toString()) {
      elements += element.toString()
    }

    elements.toArray[String]
  }

  private def isDivider(c: Char): Boolean =
    c == '[' || c == ']' || c == '(' || c == ')' || c == '|' ||
    c == ',' ||
    c == '@' ||
    c == ' '

  private def isSymbol(c: Char): Boolean = c == '+' || c == '-'

  private def isQuote(c: Char): Boolean = c == '"'

  /** Find signal provided list of messages
    *
    * @param messages List of messages
    * @param id CAN identifier
    *
    * @return Option[Signal]
    */
  private def findSignal(messages: ListBuffer[Message], id: Long,
                         e: Boolean, name: String): Option[Signal] = {

    val message = findMessage(messages, id, e)

    val signals: ListBuffer[Signal] = ListBuffer.empty

    if (message.isDefined) {
      signals.clear()
      signals.addAll(message.get.signal)

      /* Find signal name */
      for (signal <- signals) {
        if (signal.name.equals(name)) {
          return Option(signal)
        }
      }

      for (
        multiplex <- message.get.multiplex;
        group <- multiplex.muxGroup;
        signal <- group.signal if signal.name == name
      ) {

        return Option(signal)
      }

    } else if (id == 0) {
      /* orphaned signal found */
      // ignore
      None
    } else {
      // ignoring
      /* valid signal found but message not defined */
      None
    }

    None
  }

  /** Find single CAN message from list of messages
    *
    * @param messages List of messages
    * @param id CAN identifier
    *
    * @return Option[Message]
    */
  private def findMessage(messages: ListBuffer[Message], id: Long, e: Boolean): Option[Message] = {

    for (message <- messages) {

      val extended: Boolean = "extended".equals(message.format)

      if (java.lang.Long.parseLong(message.id.substring(2), 16) ==
          id && extended == e) {
        return Option(message)
      }
    }

    None
  }

  /*
  * Check if a message starts with certain DBC keyword
  *
  * @return <code>true</code> if message starts with known keyword
  * */
  private def startsWithKeyword(line: String): Boolean =
    KEYWORDS.find(line.startsWith(_)).map(_ => true).getOrElse(false)

  // below methods are kept for future implementation if needed
  private def parseBitTimingSection(line: StringBuilder): Unit = {}

  private def parseNewSymbols(line: StringBuilder): Unit = {}

  private def parseMessageTransmitter(line: StringBuilder): Unit = {}

  /** CM_
    * Description field.
    * Format: CM_ [<BU_|BO_|SG_> [CAN-ID] [SignalName] "<DescriptionText>";
    *
    */
  private def parseComment(line: StringBuilder): Unit = {}

  case class LabelDescription(id: Long, signalName: String, labels: Set[Label], extended: Boolean)

  case class SignalComment(id: Long, signalName: String, comment: String, extended: Boolean)

  /** Defines the type of signal
    *
    */
  object SignalType {

    val MULTIPLEXOR: Type = new Type()

    val MULTIPLEX: Type = new Type()

    val PLAIN: Type = new Type()

    class Type

    import scala.language.implicitConversions

    implicit def convertValue(v: Value): Type =
      v.asInstanceOf[Type]

  }

}

/** Companion object for <code>DbcReader</code>
  *
  */
class DbcReader {

  val labels: mutable.HashSet[LabelDescription] = mutable.HashSet[LabelDescription]()
  val signalComments: mutable.HashSet[SignalComment] = mutable.HashSet[SignalComment]()
  val factory: ObjectFactory = new ObjectFactory()
  val DOC_CONTENT: String = "Converted by reading DBC schema"

  val muxed = TreeMap[Long, Set[Signal]]()
  //  var version: String = ""

  /** Parse CAN database file, provided inputstream of DBC file
    *
    * Bus: https://en.wikipedia.org/wiki/CAN_bus
    *
    *  @return <code>Bus</code> after reading DBC file
    *
    */
  def parseFile(file: InputStream): Bus = {

    val bus: Bus = factory.createBus

    val document: Document = factory.createDocument(
      _content = DOC_CONTENT,
      _name = file.toString, _date = Calendar.getInstance.getTime.toString)

    val network: NetworkDefinition = factory.createNetworkDefinition(_document = (document))

    bus.name_=("Private")
    //    val contents: StringBuilder = new StringBuilder()

    try {
      var isFirstLine: Boolean = true

      lazy val contents = {

        val sb: StringBuilder = new StringBuilder()

        val lines = scala.io.Source.fromInputStream(file).getLines

        lines.foreach(line => {
          if (startsWithKeyword(line) && !isFirstLine) {
            processLine(sb, network, bus)
            sb.delete(0, sb.length)
          }
          sb.append(line)

          isFirstLine = false
        })

        sb
      }

      processLine(contents, network, bus)
      network.bus :+ bus
    } catch {
      case e: FileNotFoundException => {
        e.printStackTrace()
      }

      case e: IOException => {
        e.printStackTrace()
      }

    } finally file.close()

    labels.foreach(description => {
      val messages = bus.message

      val set: LabelSet = new LabelSet()
      val labellist: List[BasicLabelType] = set.labelOrLabelGroup
      labellist.addAll(description.labels)
      val signal = findSignal(messages, description.id, description.extended, description.signalName)

      if (signal.isDefined) {
        signal.get.labelSet_=(set)
      }
    })

    signalComments.foreach(comment => {
      val messages: ListBuffer[Message] = bus.message

      val signal = findSignal(messages, comment.id, comment.extended, comment.signalName)

      if (signal.isDefined) {
        signal.get.notes_=(comment.comment)
      }
    })

    bus
  }

  /*
  * Process message line by line if it matches DBC keywords
  *
  * */
  private def processLine(line: StringBuilder, network: NetworkDefinition, bus: Bus): Unit = {

    if (Pattern.matches("BO_.?\\d+.*", line)) {
      parseMessageDefinition(line, bus)
    } else if (Pattern.matches("VAL_.?\\d+.*", line)) {
      try parseValueDescription(line)
      catch {
        case e: Exception => {}
      }
    } else if (Pattern.matches("BA_\\s+\".*", line)) {
      try parseAttribute(line, bus)
      catch {
        case e: Exception => System.err.println(line + e.getMessage)
      }
    } else if (Pattern.matches("CM_ SG_.*", line)) {
      parseSignalComment(line)
    } else if (Pattern.matches("CM.*", line)) {
      parseComment(line)
    } else if (Pattern.matches("BO_TX_BU_.*", line)) {
      parseMessageTransmitter(line)
    } else if (Pattern.matches("BU_.*", line)) {
      parseNetworkNode(line, network)
    } else if (Pattern.matches("NS_.?:.*", line)) {
      parseNewSymbols(line)
    } else if (Pattern.matches("BS_.*", line)) {
      parseBitTimingSection(line)
    } else if (Pattern.matches("VERSION.*", line)) {
      parseVersion(line)
    } else if (Pattern.matches("EV_.*", line)) {
      parseEnvironmentVariable(line)
    } else if (Pattern.matches("VAL_.?\\w+.*", line)) {
      parseEnvironmentVariableDescription(line)
    } else {
      // System.out.println("Line does not match:'" + line + "'\n");
    }

  }

  /*
  * Parser DBC version
  *
  * VERSION
  * Version identifier of the DBC file.
  * Format: VERSION "<VersionIdentifier>"
  *
  * */
  private def parseVersion(line: StringBuilder): Unit = {
    splitString(line.toString)(1)
  }

  /** BU_
    * List of all CAN-Nodes, seperated by whitespaces.
    *
    */
  private def parseNetworkNode(line: StringBuilder, network: NetworkDefinition): Unit = {
    line.replace(0, 5, "")

    val lineArray: Array[String] = line.toString.split("\\s+")
    val nodes = lineArray.toList

    nodes.foreach(nodeString => {
      val node: Node = factory.createNode(_id = nodeString, _name = nodeString)
      network.node.add(node)
    })

  }

  /*
  * Parse attribute that starts with BA
  *
  * BA_
  * Attribute
  * Format: BA_ "<AttributeName>" [BU_|BO_|SG_] [Node|CAN-ID] [SignalName] <AttributeValue>;
  *
  * */
  private def parseAttribute(line: StringBuilder, bus: Bus): Unit = {
    /* Find message with given id*/

    if (Pattern.matches("BA_\\s+", line)) {

      val splitted: Array[String] = splitString(line.toString)

      if (!splitted.isEmpty) {
        val messages: ListBuffer[Message] = bus.message
        val message = findMessage(messages, getCanIdFromString(splitted(3)).longValue(), isExtendedFrameFormat(splitted(3)))

        val fval: Float = splitted(4).substring(0, splitted(4).length - 1).toFloat
        val ival: Int = Math.round(fval)

        // Omit default interval = 0
        if (ival != 0 && message.isDefined) {
          message.get.interval_=(ival)
        }
      }
    }
  }

  /*
  * Parse message definition
  *
  * BO_ Message definition.
  * Format: BO_ <CAN-ID> <MessageName>: <MessageLength> <SendingNode>
  * MessageLength in bytes.
  *
  * */
  private def parseMessageDefinition(line: StringBuilder, bus: Bus): Unit = {

    muxed.clear()
    // remove BO_
    line.replace(0, 4, "")

    val lineArray: Array[String] = line.toString.split("\\s*SG_\\s+")
    val messageArray: Array[String] = lineArray(0).split("\\s+")
    val message: Message = factory.createMessage
    val messageIdDecimal: Int = getCanIdFromString(messageArray(0))

    // Optimization
    message.id_=("0x" + java.lang.Integer.toString(messageIdDecimal, 16).toUpperCase())

    if (isExtendedFrameFormat(messageArray(0))) {
      message.format_=("extended")
    }

    message.same_=(messageArray(1).replace(":", ""))
    message.length_=(messageArray(2))

    if (!messageArray(3).contains(NOT_DEFINED)) {
      val producer: Producer = factory.createProducer
      val ref: NodeRef = factory.createNodeRef
      ref.id_=(messageArray(3))
      producer.nodeRef.add(ref)
      message.producer_=(producer)
    }

    for (i <- 1 until lineArray.length) {
      parseSignal(message, lineArray(i))
    }

    if (!muxed.isEmpty) {

      if (message.multiplex.size == 1) {

        val mul: Multiplex = message.multiplex.get(0)
        val muxgroups: List[MuxGroup] = mul.muxGroup

        for (i <- muxed.keySet) {
          val group: MuxGroup = new MuxGroup(count = i)
          group.signal.++(muxed.get(i))
          muxgroups :+ group
        }
      }
    } else {
      message.multiplex.clear()
    }

    if (!message.name.contains(ORPHANED_SIGNALS)) {
      bus.message.add(message)
    }
  }

  /** Parse a signal provided message line
    *
    */
  protected def parseSignal(message: Message, line: String): Unit = {

    val lineArray: Array[String] = line.split(":")
    val signalName: String = lineArray(0).trim()

    if (Pattern.compile("\\w+\\s+\\w+").matcher(lineArray(0)).find()) {

      if (signalName.endsWith("M")) {
        // Remove multiplex coding ' M' from name "Muxname M"
        parseSignalLine(message, signalName.substring(0, signalName.length - 2), SignalType.MULTIPLEXOR, lineArray(1))
      } else {

        var countstring: String = lineArray(0).trim()
        var i: Int = countstring.length - 1

        while (i > 0) {
          if (countstring.charAt(i) == 'm') {
            countstring = countstring.substring(i + 1)
            //break
          }
          i -= 1;
        }

        val muxcount: Long = countstring.toLong
        val signal = parseSignalLine(message, lineArray(0).split(" ")(0), SignalType.MULTIPLEX, lineArray(1))

        var signalSet = muxed.get(muxcount).get
        if (signalSet == null) {
          signalSet = Set[Signal]()
          muxed.put(muxcount, signalSet)
        }
        signalSet.add(signal.get)
      }

    } else {

      parseSignalLine(message, signalName, SignalType.PLAIN, lineArray(1).trim())
    }
  }

  /*
  * Parse signal line based on provided signal type
  *
  * */
  private def parseSignalLine(message: Message, signalName: String,
                              `type`: SignalType.Type, line: String): Option[Signal] = {

    val value: Value = factory.createValue

    val tSignal: Signal = factory.createSignal
    tSignal.name_=(signalName)

    val splitted: Array[String] = splitString(line)
    val sConsumers: Array[String] = splitted.slice(9, splitted.length)

    if (!splitted.isEmpty) {

      val offset: Int = splitted(0).toInt
      val length: Int = splitted(1).toInt
      val isBigEndian: Boolean = "0" == splitted(2)

      if (length > 0) {
        tSignal.length_=(length)
      }

      if (isBigEndian && length > 1) {
        // big endian signal and signal length greater than 1
        tSignal.offset_=(bigEndianLeastSignificantBitOffset(offset, length))
      } else {
        // little endian OR signal length == 1
        tSignal.offset_=(offset)
      }
      if (isBigEndian) {
        tSignal.endianess_=("big")
      }
      val slope: Double = splitted(4).toDouble
      val intercept: Double = splitted(5).toDouble
      val min: Double = splitted(6).toDouble
      val max: Double = splitted(7).toDouble

      if (sConsumers.length > 0) {
        val consumer: Consumer =
          factory.createConsumer
        for (sConsumer <- sConsumers) {
          val ref: NodeRef = factory.createNodeRef
          consumer.nodeRef.add(ref)
          ref.id_=(sConsumer)
        }
        tSignal.consumer_=(consumer)
      }
      if ((intercept != 0.0) || (slope != 1.0) || "" != splitted(8) ||
          "-" == splitted(3) ||
          (min != 0.0) ||
          (max != 1.0)) {

        if ("-" == splitted(3)) {
          value.type_=("signed")
        }
        // Omit default slope
        if (slope != 1.0) {
          value.slope_=(slope)
        }
        // Omit default intercept = 0.0
        if (intercept != 0.0) {
          value.intercept_=(intercept)
        }
        // Omit empty units
        if ("" != splitted(8)) {
          value.unit_=(splitted(8))
        }
        // Omit default min = 0.0
        if (min != 0.0) {
          value.min_=(min)
        }
        // Omit default max = 1.0
        if (max != 1.0) {
          value.max_=(max)
        }
      }
    }

    if (`type` == SignalType.MULTIPLEXOR) {
      val mux: Multiplex = factory.createMultiplex
      mux.name_=(tSignal.name)
      mux.offset_=(tSignal.offset)
      mux.consumer_=(tSignal.consumer)
      if (tSignal.getLength > 0) {
        mux.length_=(tSignal.getLength)
      }
      if ("big" == tSignal.endianess) {
        mux.endianess_=(tSignal.endianess)
      }
      mux.value_=(value)
      message.multiplex.add(mux)

      None
    } else {
      val signal: Signal = factory.createSignal
      signal.name_=(tSignal.name)
      signal.offset_=(tSignal.offset)
      signal.consumer_=(tSignal.consumer)
      if (tSignal.getLength != 0) {
        signal.length_=(tSignal.getLength)
      }
      if ("big" == tSignal.endianess) {
        signal.endianess_=(tSignal.endianess)
      }
      signal.value_=(value)
      if (`type` == SignalType.PLAIN) {
        // Prevent from adding MULTIPLEX signals twice
        message.signal.add(signal)
      }
      Option(signal)
    }
  }

  // kept for future implementation
  private def parseEnvironmentVariable(line: StringBuilder): Unit = {}

  /** VAL_
    * Value definitions for signals.
    * Format: VAL_ <CAN-ID> <SignalsName> <ValTableName|ValTableDefinition>;
    *
    */
  private def parseEnvironmentVariableDescription(line: StringBuilder): Unit = {}

  /*
  * Parse value description
  *
  * VAL_ Value definitions for signals.
  * Format: VAL_ <CAN-ID> <SignalsName> <ValTableName|ValTableDefinition>;
  *
  * */
  private def parseValueDescription(line: StringBuilder): Unit = {

    val splitted: Array[String] = splitString(line.toString)

    val labelSet = Set[Label]()
    labelSet.add(new Label())

    var i: Int = 3
    while (i < (splitted.length - 1)) {
      val label: Label = new Label()
      label.value_=(int32ToBigInt(new BigInteger(splitted(i))))
      label.name_=(splitted(i + 1))
      labelSet.add(label)
      i += 2
    }

    val description: LabelDescription = new LabelDescription(id = getCanIdFromString(splitted(1)).longValue(), signalName = splitted(2), labels = labelSet, extended = isExtendedFrameFormat(splitted(1)))

    labels.add(description)
  }

  /*
  * Parse comment for a signal
  *
  * */
  private def parseSignalComment(line: StringBuilder): Unit = {
    val splitted: Array[String] = splitString(line.toString)

    signalComments.add(SignalComment(id = getCanIdFromString(splitted(2)).longValue(), signalName = splitted(3),
      comment = splitted(4), extended = isExtendedFrameFormat(splitted(2))))
  }

}
