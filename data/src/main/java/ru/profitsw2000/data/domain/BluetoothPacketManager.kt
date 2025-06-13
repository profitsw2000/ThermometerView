package ru.profitsw2000.data.domain

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


    fun insertBytesToRingBuffer(bytesList: List<Byte>)

    fun parseBuffer()

    fun decodePacket(bytesList: List<Byte>, command: Int, packetSize: Int)

    fun getNextBufferByte(): Byte

}