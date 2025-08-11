package ru.profitsw2000.core.utils.constants

const val DATE_TIME_PACKET_ID = 0x01
const val CURRENT_MEMORY_PACKET_ID = 0x05
const val SENSORS_INFO_PACKET_ID = 0x09
const val SENSOR_INFO_PACKET_ID = 0x0C

//пакет для запроса времени термометра
val dateTimeRequestPacket = byteArrayOf(0x53, 0x0A, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0B)
//часть пакета, посылаемого на термометр для обновления времени
val updateTimeRequestPacketHead = byteArrayOf(0x53, 0x0A, 0x02)
//пакет для запроса текущего адреса микросхемы памяти термометра
val currentMemoryAddressRequestPacket = byteArrayOf(0x53, 0x03, 0x05, 0x08)
//пакет для запроса основной информации датчиков - температура, идентификационный номер и код символа. ассоциированного с ним
val sensorInfoPacket = byteArrayOf(0x53, 0x05, 0x09, 0x00, 0x00, 0x0E)
//часть пакета, посылаемого на термометр для получения информации о датчике с конкретным индексом
val sensorInfoRequestPacketHead = byteArrayOf(0x53, 0x04, 0x0C)
//часть пакета, посылаемого на термометр для отправки кода буквы, ассоциированного с датчиком с конкретным индексом
val sensorLetterCodePacketHead = byteArrayOf(0x53, 0x06, 0x08)
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

fun getSensorInfoPacket(index: Int): ByteArray {
    val indexByte = index.toUByte().toByte()
    val checkSum = ((sensorInfoRequestPacketHead[1].toInteger() +
            sensorInfoRequestPacketHead[2].toInteger() +
            indexByte.toInteger()) and 0xFF).toUByte().toByte()
    val sensorInfoPacketData = byteArrayOf(indexByte, checkSum)
    return sensorInfoRequestPacketHead + sensorInfoPacketData
}

fun getSensorLetterCodePacket(index: Int, letter: String): ByteArray {
    val indexByte = index.toUByte().toByte()
    val letterCodePacketData = getIntCodeFromLetter(letter).toByteArray()
    val checkSum = ((sensorLetterCodePacketHead[1].toInteger()
            + sensorLetterCodePacketHead[2].toInteger()
            + indexByte.toInteger()
            + letterCodePacketData[1].toInteger()
            + letterCodePacketData[0].toInteger()) and 0xFF).toUByte().toByte()

    return sensorInfoRequestPacketHead +
            byteArrayOf(indexByte) +
            letterCodePacketData +
            byteArrayOf(checkSum)
}

private fun Int.fromIntToBcdByte(): Byte {
    val dec = (this/10).shl(4)
    val units = (this%10)
    val result = dec.or(units).toByte()

    return result
}

private fun Int.toByteArray(): ByteArray {
    return byteArrayOf(
        (this shr 8).toUByte().toByte(),
        (this and 0xFF).toUByte().toByte()
    )
}

private fun Byte.toInteger(): Int = this.toInt() and 0xFF