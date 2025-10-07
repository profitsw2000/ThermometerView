package ru.profitsw2000.data.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.profitsw2000.core.utils.constants.CLEAR_MEMORY_PACKET_ID
import ru.profitsw2000.core.utils.constants.CURRENT_MEMORY_PACKET_ID
import ru.profitsw2000.core.utils.constants.DATE_TIME_PACKET_ID
import ru.profitsw2000.core.utils.constants.LOAD_MEMORY_DATA_PACKET_ID
import ru.profitsw2000.core.utils.constants.RING_BUFFER_BYTE_PARSING_PERIOD
import ru.profitsw2000.core.utils.constants.SENSORS_INFO_PACKET_ID
import ru.profitsw2000.core.utils.constants.SENSOR_INFO_PACKET_ID
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.utils.constants.TOTAL_MEMORY_BYTE_SIZE
import ru.profitsw2000.core.utils.constants.getLetterFromCode
import ru.profitsw2000.data.domain.BluetoothPacketManager
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.data.model.MemoryInfoModel
import ru.profitsw2000.data.model.MemoryServiceDataModel
import ru.profitsw2000.data.model.SensorModel
import ru.profitsw2000.data.model.status.BluetoothRequestResultStatus
import java.math.RoundingMode
import kotlin.experimental.and
import kotlin.experimental.inv

class BluetoothPacketManagerImpl(
    private val bluetoothRepository: BluetoothRepository
) : BluetoothPacketManager {

    private val RING_BUFFER_SIZE = 128
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val ringBuffer: MutableList<Byte> = mutableListOf()
    override val packetBuffer: MutableList<Byte> = mutableListOf()
    @Volatile override var byteCount = 0
    override var bufferTail = 0
    override var bufferHead = 0
    override var packetState = 0
    override var checkSum = 0
    override var packetId = 0
    override var packetSize = 0
    private val _bluetoothRequestResult =
        MutableStateFlow<BluetoothRequestResultStatus>(BluetoothRequestResultStatus.Error)
    override val bluetoothRequestResult: StateFlow<BluetoothRequestResultStatus> by this::_bluetoothRequestResult

    init {
        observeBluetoothBytesFlow()
        parseBuffer()
    }

    private fun observeBluetoothBytesFlow() {
        coroutineScope.launch {
            bluetoothRepository.bluetoothReadByteList.collect { value ->
                insertBytesToRingBuffer(value)
            }
        }
    }

    override fun insertBytesToRingBuffer(bytesList: List<Byte>) {
        bytesList.forEach { item ->
            ringBuffer.add(bufferTail, item)
            bufferTail++
            bufferTail %= RING_BUFFER_SIZE
        }
    }

    override fun parseBuffer() {
        coroutineScope.launch {
            while (isActive) {
               if (bufferTail != bufferHead) {
                    val symbol = getNextBufferByte()
                    when (packetState) {
                        0 -> checkStartByte(symbol = symbol)
                        1 -> checkPacketSize(symbol = symbol)
                        2 -> checkPacketId(symbol = symbol)
                        else -> getPacketData(symbol = symbol)
                    }
                }
                delay(RING_BUFFER_BYTE_PARSING_PERIOD)
            }
        }
    }

    override fun decodePacket(bytesList: List<Byte>, command: Int, packetSize: Int) {
        when (command) {
            DATE_TIME_PACKET_ID -> emitDateTimeData(bytesList, packetSize)
            CURRENT_MEMORY_PACKET_ID -> emitMemoryInfo(bytesList, packetSize)
            SENSORS_INFO_PACKET_ID -> emitSensorsInfo(bytesList, packetSize)
            SENSOR_INFO_PACKET_ID -> emitSensorInfo(bytesList, packetSize)
            CLEAR_MEMORY_PACKET_ID -> emitMemoryClearResult(bytesList)
            LOAD_MEMORY_DATA_PACKET_ID -> defineMemoryLoadPacketType(bytesList, packetSize)
            else -> {}
        }
    }

    override fun getNextBufferByte(): Byte {

        val symbol: Byte = ringBuffer[bufferHead]
        bufferHead++
        bufferHead %= RING_BUFFER_SIZE

        return symbol
    }

    private fun checkStartByte(symbol: Byte) {
        if (symbol.toInteger() == 0x53) {
            packetState = 1
            checkSum = 0
            packetBuffer.clear()
        }
    }

    private fun checkPacketSize(symbol: Byte) {
        packetSize = symbol.toInteger()
        checkSum += packetSize
        packetState = if (packetSize > RING_BUFFER_SIZE) 0
        else 2
    }

    private fun checkPacketId(symbol: Byte) {
        packetId = symbol.toInteger()
        checkSum += packetId
        packetState = if (packetId > 0x20) 0
        else 3
    }

    private fun getPacketData(symbol: Byte) {
        if (packetState < packetSize) {
            packetBuffer.add((packetState - 3), symbol)
            checkSum += symbol.toInteger()
            packetState++
        } else {
            if ((checkSum and 0xFF) == symbol.toInteger()) decodePacket(packetBuffer, packetId, packetSize - 3)
            packetState = 0
        }
    }

    private fun emitDateTimeData(data: List<Byte>, listSize: Int) {
        if (listSize >= 7) {
            val seconds = data[0].fromBCDtoInt().toDateTimeString()
            val minutes = data[1].fromBCDtoInt().toDateTimeString()
            val hours = data[2].fromBCDtoInt().toDateTimeString()
            val day = data[4].fromBCDtoInt().toDateTimeString()
            val month = data[5].fromBCDtoInt().toDateTimeString()
            val year = data[6].fromBCDtoInt().toDateTimeString()

            _bluetoothRequestResult.value = BluetoothRequestResultStatus.DateTimeInfo(
                "$hours:$minutes:$seconds $day.$month.$year"
            )
        } else {
            _bluetoothRequestResult.value = BluetoothRequestResultStatus.Error
        }
    }

    private fun emitMemoryInfo(data: List<Byte>, listSize: Int) {
        if (listSize >= 4) {
            val address = data[3].toInteger() or
                    (data[2].toInteger() shl 8) or
                    (data[1].toInteger() shl 16) or
                    (data[0].toInteger() shl 24)
            val percentage = ((address.toFloat() / TOTAL_MEMORY_BYTE_SIZE) * 100).toBigDecimal().setScale(2, RoundingMode.DOWN).toFloat()

            _bluetoothRequestResult.value = BluetoothRequestResultStatus.CurrentMemorySpace(
                MemoryInfoModel(address, percentage)
            )
        } else {
            _bluetoothRequestResult.value = BluetoothRequestResultStatus.Error
        }
    }

    private fun emitSensorsInfo(data: List<Byte>, listSize: Int) {
        if (listSize > 0) {
            val sensorsNumber = data[0]
            if (listSize >= (sensorsNumber * 12 + 1)) {
                val sensorModelList: MutableList<SensorModel> = mutableListOf()

                for (index in 0..<sensorsNumber) {
                    val sensorIdList = data.subList((1 + index * 12), (1 + index * 12 + 8))
                    val sensorLetterCodeList =
                        data.subList((1 + index * 12 + 8), (1 + index * 12 + 10))
                    val sensorTemperatureList =
                        data.subList((1 + index * 12 + 10), (1 + index * 12 + 12))

                    sensorModelList.add(
                        SensorModel(
                            sensorIdList.toULongBigEndian(),
                            index,
                            getLetterFromCode(sensorLetterCodeList.toLetterCode()),
                            sensorTemperatureList.toTemperature()
                        )
                    )
                }
                _bluetoothRequestResult.value = BluetoothRequestResultStatus.SensorsCurrentInfo(sensorModelList)
            } else _bluetoothRequestResult.value = BluetoothRequestResultStatus.Error
        } else _bluetoothRequestResult.value = BluetoothRequestResultStatus.Error
    }

    private fun emitSensorInfo(data: List<Byte>, listSize: Int) {
        if (listSize >= 13) {
            _bluetoothRequestResult.value =
                BluetoothRequestResultStatus.SensorInfo(
                    getSensorModelFromList(data)
                )
        } else _bluetoothRequestResult.value = BluetoothRequestResultStatus.Error
    }

    private fun emitMemoryClearResult(data: List<Byte>) {
        if (data.size == 1) {
            _bluetoothRequestResult.value =
                BluetoothRequestResultStatus.MemoryClearResult(data[0].toInteger() == 0xFF)
        } else _bluetoothRequestResult.value = BluetoothRequestResultStatus.Error
    }

    private fun defineMemoryLoadPacketType(data: List<Byte>, listSize: Int) {
        when(data[0].toInteger()) {
            0x01 -> emitMemoryServiceData(data.drop(1), (listSize - 1))
            0x02 -> emitMemoryData(data.drop(1), (listSize - 1))
            0x03 -> TODO()
            else -> {}
        }
    }

    private fun emitMemoryServiceData(data: List<Byte>, listSize: Int) {
        val sensorsNumber = data[4].toInteger()
        if (data.size == listSize && data.size == (sensorsNumber*11 + 5)) {
            val address = fourBytesToIntBigEndian(data.take(4))
            val localIdList: MutableList<Int> = mutableListOf()
            val sensorLetterCodeList: MutableList<Int> = mutableListOf()
            val sensorIdList: MutableList<ULong> = mutableListOf()

            for (i in 0..<sensorsNumber) {
                localIdList.add((data[5 + i*11]).toInteger())
                sensorLetterCodeList.add((data.subList(6 + i*11, 7 + i*11)).toLetterCode())
                sensorIdList.add((data.subList(8 + i*11, 16 + i*11)).toULongBigEndian())
            }
            _bluetoothRequestResult.value = BluetoothRequestResultStatus.MemoryServiceDataReceived(
                memoryServiceDataModel = MemoryServiceDataModel(
                    currentAddress = address,
                    sensorsNumber = sensorsNumber,
                    localIdList = localIdList,
                    sensorsLetterCodeList = sensorLetterCodeList,
                    sensorIdsList = sensorIdList
                )
            )
        } else _bluetoothRequestResult.value = BluetoothRequestResultStatus.Error
    }

    private fun emitMemoryData(data: List<Byte>, listSize: Int) {
        TODO()
    }

    private fun emitStopMemoryDataTransfer() {
        TODO()
    }

    private fun getSensorModelFromList(data: List<Byte>): SensorModel {
        val sensorLocalId = data[0]
        val sensorId = data.subList(1, 9)
        val sensorLetterCode = data.subList(9, 11)
        val sensorTemperature = data.subList(11, 13)

        return SensorModel(
            sensorId = sensorId.toULongBigEndian(),
            sensorLocalId = sensorLocalId.toInteger(),
            getLetterFromCode(sensorLetterCode.toLetterCode()),
            sensorTemperature.toTemperature()
        )

    }

    @OptIn(ExperimentalStdlibApi::class)
    fun Byte.toHex(): String = this.toHexString(
        HexFormat {
            upperCase = true
            number{ prefix = "0x" }
        }
    )

    fun ByteArray.toHex(): String = joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }

    private fun List<Byte>.toHex(): String = joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }

    private fun Byte.toInteger(): Int = this.toInt() and 0xFF

    private fun Byte.fromBCDtoInt(): Int {
        val units = (this and 0x0F).toInt()
        val decimals = (this and 0xF0.toByte()).toInt() shr 4
        return (decimals * 10 + units)
    }

    private fun Int.toDateTimeString(): String = if (this < 10) "0$this"
    else this.toString()

    private fun List<Byte>.toULongBigEndian(): ULong {
        if (this.size != 8) {
            throw IllegalArgumentException("List must contain exactly 8 bytes")
        }

        var result: ULong = 0u
        for (i in 0..7) {
            result = result or ((this[i].toULong() and 0xFFu) shl ((7 - i) * 8))
        }
        return result
    }

    private fun List<Byte>.toLetterCode(): Int {
        if (this.size != 2) {
            throw IllegalArgumentException("List must contain exactly 2 bytes")
        }
        return (this[0].toInteger() shl 8) or (this[1].toInteger())
    }

    private fun List<Byte>.toTemperature(): Double {
        if (this.size != 2) {
            throw IllegalArgumentException("List must contain exactly 2 bytes")
        }

        return if (this[0] < 0) {
            val fractionalPart = ((this[1].inv().toInt() and 0x0F) + 1)*0.0625
            val wholePart = (this[0].inv().toInt() shl 4) or ((this[1].inv().toInt() and 0xFF) shr 4)
            ((wholePart + fractionalPart).toBigDecimal().setScale(1, RoundingMode.DOWN).toDouble())*(-1)
        } else {
            val fractionalPart = (this[1].toInt() and 0x0F)*0.0625
            val wholePart = (this[0].toInt() shl 4) or (this[1].toInteger() shr 4)
            (wholePart + fractionalPart).toBigDecimal().setScale(1, RoundingMode.DOWN).toDouble()
        }
    }

    private fun fourBytesToIntBigEndian(bytes: List<Byte>): Int {
        require(bytes.size == 4) { "Input ByteArray must contain exactly 4 bytes." }

        var result = 0
        result = result or (bytes[0].toInt() and 0xFF shl 24)
        result = result or (bytes[1].toInt() and 0xFF shl 16)
        result = result or (bytes[2].toInt() and 0xFF shl 8)
        result = result or (bytes[3].toInt() and 0xFF)
        return result
    }
}