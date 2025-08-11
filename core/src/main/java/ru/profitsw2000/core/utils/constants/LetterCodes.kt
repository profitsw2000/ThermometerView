package ru.profitsw2000.core.utils.constants

fun getLetterFromCode(letterCode: Int): String {
    return when (letterCode) {
        0x78C6 -> "А"
        0x38DE -> "Б"
        0x7A1D -> "В"
        0x00C6 -> "Г"
        0x4A1F -> "Д"
        0x10DE -> "Е"
        0x8721 -> "Ж"
        0x681E -> "З"
        0xC8E0 -> "И"
        0x94C0 -> "К"
        0xC820 -> "Л"
        0xC9C0 -> "М"
        0x78C0 -> "Н"
        0x48DE -> "О"
        0x48C6 -> "П"
        0x7207 -> "Р"
        0x00DE -> "С"
        0x0207 -> "Т"
        0x7898 -> "У"
        0x7287 -> "Ф"
        0x8520 -> "Х"
        0x02D9 -> "Ц"
        0x3281 -> "Ч"
        0x4AD9 -> "Ш"
        0x320B -> "Э"
        0x5AD5 -> "Ю"
        0x32C3 -> "Я"
        else -> "N"
    }
}

fun getIntCodeFromLetter(letter: String): Int {
    return when (letter) {
        "А" -> 0x78C6
        "Б" -> 0x38DE
        "В" -> 0x7A1D
        "Г" -> 0x00C6
        "Д" -> 0x4A1F
        "Е" -> 0x10DE
        "Ж" -> 0x8721
        "З" -> 0x681E
        "И" -> 0xC8E0
        "К" -> 0x94C0
        "Л" -> 0xC820
        "М" -> 0xC9C0
        "Н" -> 0x78C0
        "О" -> 0x48DE
        "П" -> 0x48C6
        "Р" -> 0x7207
        "С" -> 0x00DE
        "Т" -> 0x0207
        "У" -> 0x7898
        "Ф" -> 0x7287
        "Х" -> 0x8520
        "Ц" -> 0x02D9
        "Ч" -> 0x3281
        "Ш" -> 0x4AD9
        "Э" -> 0x320B
        "Ю" -> 0x5AD5
        "Я" -> 0x32C3
        else -> 0xFFFF
    }
}