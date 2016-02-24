package com.kotcrab.xgbc.util

import com.kotcrab.xgbc.toHex
import org.jsoup.Jsoup
import java.io.File

fun main(argv: Array<String>) {
    //http://www.pastraiser.com/cpu/gameboy/gameboy_opcodes.html
    //relative path doesn't seem to work, whatever
    val doc = Jsoup.parse(File("""E:\Git\xGBC\assets\jsoup\opcodes.html"""), "UTF-8");
    val elements = doc.select("[width=1350]");
    val opTable = elements[0]
    val extOpTable = elements[1]

    val iterator = extOpTable.select("td").iterator();
    val opTableName = "op";

    iterator.next()

    var opcode = 0x00;
    for (element in iterator) {
        if (element.text().startsWith(" ") && element.text().length > 1) continue

        val text = element.text();

        if (text.equals(" ")) {
            println("$opTableName[0x${toHex(opcode.toByte())}] = null")
            opcode += 1
            continue
        }

        val afterLength = text.indexOf("  ")
        val lengthIndex = text.lastIndexOf(" ", afterLength) + 1
        val cyclesIndex = afterLength + 2;
        val afterCycles = text.indexOf(" ", cyclesIndex);
        val opcodeText = text.substring(0, lengthIndex - 1)
                .replace(",", ", ").replace(" ", " ")
        val cyclesText = text.substring(cyclesIndex, afterCycles)

        if (cyclesText.contains("/")) {
            val splited = cyclesText.split("/")
            println("$opTableName[0x${toHex(opcode.toByte())}] = CondInstr(${text.substring(lengthIndex, afterLength)}, ${splited[0]}, ${splited[1]}, \"$opcodeText\", {false})")
        } else
            println("$opTableName[0x${toHex(opcode.toByte())}] = Instr(${text.substring(lengthIndex, afterLength)}, ${text.substring(cyclesIndex, afterCycles)}, \"$opcodeText\", {})")

        opcode += 1;
    }

}
