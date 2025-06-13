package ru.profitsw2000.data.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.profitsw2000.core.utils.constants.CURRENT_MEMORY_PACKET_ID
import ru.profitsw2000.core.utils.constants.DATE_TIME_PACKET_ID
import ru.profitsw2000.core.utils.constants.SENSORS_INFO_PACKET_ID
import ru.profitsw2000.data.domain.BluetoothPacketManager
import ru.profitsw2000.data.domain.BluetoothRepository

class BluetoothPacketManagerImpl(
    private val bluetoothRepository: BluetoothRepository
) : BluetoothPacketManager {
    private val RING_BUFFER_SIZE = 128
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val ringBuffer: MutableList<Byte> = arrayListOf()
    override val packetBuffer: MutableList<Byte> = arrayListOf()
    override var byteCount = 0
    override var bufferTail = 0
    override var bufferHead = 0
    override var packetState = 0
    override var checkSum = 0
    override var packetId = 0
    override var packetSize = 0

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
            byteCount++
        }
    }

    override fun parseBuffer() {
        coroutineScope.launch {
            while (isActive) {
                val symbol = getNextBufferByte()
                if (byteCount > 0) {
                    when(packetState) {
                        0 -> checkStartByte(symbol = symbol)
                        1 -> checkPacketSize(symbol = symbol)
                        2 -> checkPacketId(symbol = symbol)
                        else -> getPacketData(symbol = symbol)
                    }
                }
                delay(1)
            }
        }
    }

    override fun decodePacket(bytesList: List<Byte>, command: Int, packetSize: Int) {
        when(command) {
            DATE_TIME_PACKET_ID -> TODO()
            CURRENT_MEMORY_PACKET_ID -> TODO()
            SENSORS_INFO_PACKET_ID -> TODO()
            else -> TODO()
        }
    }

    override fun getNextBufferByte(): Byte {
        return if (byteCount > 0) {
            ringBuffer[bufferHead]
        } else 0
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
        packetBuffer.add((packetState - 3), symbol)
        if (packetState < (packetSize - 1)) {
            checkSum += symbol.toInteger()
            packetState++
        } else {
            if (checkSum == symbol.toInteger()) decodePacket(packetBuffer, packetId, packetSize - 3)
            packetState = 0
        }
    }

    private fun Byte.toInteger(): Int = this.toInt() and 0xFF
}