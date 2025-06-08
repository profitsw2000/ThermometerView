package ru.profitsw2000.core.utils.constants

//пакет для запроса времени термометра
val dateTimeRequestPacket = byteArrayOf(0x53, 0x0A, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0B)
//часть пакета, посылаемого на термометр для обновления времени
val updateTimeRequestPacketHead = byteArrayOf(0x53, 0x0A, 0x02)
//пакет для запроса текущего адреса микросхемы памяти термометра
val currentMemoryAddressRequestPacket = byteArrayOf(0x53, 0x03, 0x05, 0x08)
//пакет для запроса основной информации датчиков - температура, идентификационный номер и код символа. ассоциированного с ним
val sensorInfoPacket = byteArrayOf(0x53, 0x05, 0x09, 0x00, 0x00, 0x0E)
val mainDataBluetoothRequestsList: List<ByteArray> = listOf(dateTimeRequestPacket, currentMemoryAddressRequestPacket, sensorInfoPacket)