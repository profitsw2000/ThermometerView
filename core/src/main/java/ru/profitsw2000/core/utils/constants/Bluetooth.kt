package ru.profitsw2000.core.utils.constants

const val DATE_TIME_PACKET_ID = 0x01
const val CURRENT_MEMORY_PACKET_ID = 0x05
const val SENSORS_INFO_PACKET_ID = 0x09

//пакет для запроса времени термометра
val dateTimeRequestPacket = byteArrayOf(0x53, 0x0A, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0B)
//часть пакета, посылаемого на термометр для обновления времени
val updateTimeRequestPacketHead = byteArrayOf(0x53, 0x0A, 0x02)
//пакет для запроса текущего адреса микросхемы памяти термометра
val currentMemoryAddressRequestPacket = byteArrayOf(0x53, 0x03, 0x05, 0x08)
//пакет для запроса основной информации датчиков - температура, идентификационный номер и код символа. ассоциированного с ним
val sensorInfoPacket = byteArrayOf(0x53, 0x05, 0x09, 0x00, 0x00, 0x0E)
val mainDataBluetoothRequestsList: List<ByteArray> = listOf(dateTimeRequestPacket, currentMemoryAddressRequestPacket, sensorInfoPacket)

fun getDateTimePacket(dateTimeArray: Array<Int>): ByteArray {
    val updateTimePacketData = ByteArray(8)
    var checkSum = updateTimeRequestPacketHead[1] + updateTimeRequestPacketHead[2]

    dateTimeArray.forEachIndexed { index, item ->
        updateTimePacketData[index] = item.fromIntToBcdByte()
        checkSum += updateTimePacketData[index]
    }
    updateTimePacketData[updateTimePacketData.size - 1] = checkSum.toByte()
    return updateTimeRequestPacketHead + updateTimePacketData
}

private fun Int.fromIntToBcdByte(): Byte {
    val dec = (this/10).shl(4)
    val units = (this%10)
    val result = dec.or(units).toByte()

    return result
}