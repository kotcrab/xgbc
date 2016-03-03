package com.kotcrab.xgbc.io

import com.badlogic.gdx.utils.IntMap
import com.kotcrab.xgbc.Emulator

/** @author Kotcrab */
class IO(private val emulator: Emulator) {
    val ioMem: ByteArray = ByteArray(0x4C)
    val devicesMap: IntMap<IODevice> = IntMap(0x4C) //TODO direct mapping

    val serialPort = SerialPort(emulator)
    val div = Div(emulator)
    val timer = Timer(emulator)

//    val devices = arrayOf(serialPort, div, timer)
    val devices = arrayOf(serialPort)

    init {
        for (device in devices) {
            device.register({ addr -> devicesMap.put(addr - 0xFF00, device) })
        }
    }

    fun reset() {
        ioMem.fill(0)
        for (device in devices) {
            device.reset()
        }
    }

    fun tick() {
        for (device in devices) {
            device.tick()
        }
    }

    fun read(addr: Int): Byte {
        val relAddr = addr - 0xFF00;
        val device = devicesMap.get(relAddr)
        device?.onRead(addr)
        return ioMem[relAddr]
    }

    fun write(addr: Int, value: Byte) {
        val relAddr = addr - 0xFF00;
        ioMem[relAddr] = value
        val device = devicesMap.get(relAddr)
        device?.onWrite(addr, value)
    }

    fun directWrite(addr: Int, value: Byte) {
        ioMem[addr - 0xFF00] = value
    }
}

interface IODevice {
    fun reset()

    fun tick()

    fun register(registrar: (addr: Int) -> Unit)

    fun onRead(addr: Int)

    fun onWrite(addr: Int, value: Byte)
}
