package ru.profitsw2000.data.domain

interface BluetoothPacketManager {

    val ringBuffer: MutableList<Byte>
    val byteCount: Int
    val bufferTail: Int
    val bufferHead: Int

    fun insertBytesToRingBuffer(bytesList: List<Byte>)

    fun parseBuffer()

    fun decodePacket(bytesList: List<Byte>, command: Int, packetSize: Int)

}