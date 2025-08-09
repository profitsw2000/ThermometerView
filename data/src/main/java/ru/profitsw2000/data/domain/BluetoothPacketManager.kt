package ru.profitsw2000.data.domain

import kotlinx.coroutines.flow.StateFlow
import ru.profitsw2000.data.model.status.BluetoothRequestResultStatus

interface BluetoothPacketManager {

    val ringBuffer: MutableList<Byte>
    val packetBuffer: MutableList<Byte>
    var byteCount: Int
    var bufferTail: Int
    var bufferHead: Int
    var packetState: Int
    var checkSum: Int
    var packetId: Int
    var packetSize: Int

    val bluetoothRequestResult: StateFlow<BluetoothRequestResultStatus>

    fun insertBytesToRingBuffer(bytesList: List<Byte>)

    fun parseBuffer()

    fun decodePacket(bytesList: List<Byte>, command: Int, packetSize: Int)

    fun getNextBufferByte(): Byte

}