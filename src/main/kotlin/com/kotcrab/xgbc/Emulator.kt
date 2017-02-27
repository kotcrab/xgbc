package com.kotcrab.xgbc

import com.badlogic.gdx.files.FileHandle
import com.kotcrab.xgbc.cpu.Cpu
import com.kotcrab.xgbc.cpu.Reg
import com.kotcrab.xgbc.cpu.Reg16
import com.kotcrab.xgbc.io.Gpu
import com.kotcrab.xgbc.io.IO
import com.kotcrab.xgbc.rom.Rom
import java.util.*
import com.badlogic.gdx.utils.Array as GdxArray

/** @author Kotcrab */
class Emulator(romFile: FileHandle) {
    companion object {
        val BASE_CLOCK = 4096 //khz
        val CLOCK = BASE_CLOCK * 1000 / 60
        val REG_IF = 0xFF0F
        val REG_IE = 0xFFFF
    }

    val rom = Rom(romFile)
    val cpu = Cpu(this)
    val gpu = Gpu(this)
    val io = IO(this)

    var debuggerListener: DebuggerListener = DebuggerDelegate()
        private set
    var debuggerDelegates = GdxArray<DebuggerListener>()

    private val ram: ByteArray = ByteArray(0x2000)
    private val oam: ByteArray = ByteArray(0xA0)
    private val internalRam: ByteArray = ByteArray(0x7F)

    /** Interrupt Enable */
    private var ie: Byte = 0

    val interruptHandlers = ArrayList<(Interrupt) -> Unit>()
    var lcdTransferHandler: () -> Unit = {}
    var mode: EmulatorMode = EmulatorMode.NORMAL

    init {
        reset()
    }

    public fun reset() {
        mode = EmulatorMode.NORMAL
        debuggerDelegates.clear()

        ram.fill(0)
        oam.fill(0)
        internalRam.fill(0)
        gpu.reset()
        io.reset()
        ie = 0

        cpu.sp = 0xFFFE
        cpu.pc = 0x0100
        cpu.writeReg(Reg16.AF, 0x1180)
        cpu.writeReg(Reg16.BC, 0x0000)
        cpu.writeReg(Reg16.DE, 0xFF56)
        cpu.writeReg(Reg16.HL, 0x000D)
        write(0xFF05, 0x00) //TIMA
        write(0xFF06, 0x00) //TMA
        write(0xFF07, 0x00) //TAC
        write(0xFF10, 0x80) //NR10
        write(0xFF11, 0xBF) //NR11
        write(0xFF12, 0xF3) //NR12
        write(0xFF14, 0xBF) //NR14
        write(0xFF16, 0x3F) //NR21
        write(0xFF17, 0x00) //NR22
        write(0xFF19, 0xBF) //NR24
        write(0xFF1A, 0x7F) //NR30
        write(0xFF1B, 0xFF) //NR31
        write(0xFF1C, 0x9F) //NR32
        write(0xFF1E, 0xBF) //NR33
        write(0xFF20, 0xFF) //NR41
        write(0xFF21, 0x00) //NR42
        write(0xFF22, 0x00) //NR43
        write(0xFF23, 0xBF) //NR30
        write(0xFF24, 0x77) //NR50
        write(0xFF25, 0xF3) //NR51
        write(0xFF26, 0xF1) //NR52 (F1-GB, F0-SGB)
        write(0xFF40, 0x91) //LCDC
        write(0xFF42, 0x00) //SCY
        write(0xFF43, 0x00) //SCX
        write(0xFF45, 0x00) //LYC
        write(0xFF47, 0xFC) //BGP
        write(0xFF48, 0xFF) //OBP0
        write(0xFF49, 0xFF) //OBP1
        write(0xFF4A, 0x00) //WY
        write(0xFF4B, 0x00) //WX
        write(0xFFFF, 0x00) //IE
    }

    fun update(updateBreaker: () -> Boolean = { false }) {
        val targetCycles = cpu.cycle + CLOCK
        var manuallySyncedCycles = 0

        while (true) {
            step()
            manuallySyncedCycles += io.getAndClearSyncedCycles()
            if (cpu.cycle + manuallySyncedCycles > targetCycles) {
                break
            }

            if (updateBreaker.invoke()) {
                break
            }
        }
    }

    fun step() {
        val cycles = cpu.cycle
        cpu.tick()
        io.tick((cpu.cycle - cycles).toInt())
    }

    fun read(addr: Int): Byte {
        when (addr) {
            in 0x0000..0x8000 - 1 -> return rom.mbc.read(addr)
            in 0x8000..0xA000 - 1 -> return gpu.vram[addr - 0x8000]
            in 0xA000..0xC000 - 1 -> return rom.mbc.read(addr)
            in 0xC000..0xE000 - 1 -> return ram[addr - 0xC000]
            in 0xE000..0xFE00 - 1 -> return ram[addr - 0xE000] //ram echo
            in 0xFE00..0xFEA0 - 1 -> return oam[addr - 0xFE00]
            in 0xFEA0..0xFF00 - 1 -> return 0//throw EmulatorException("Read attempt from unusable, empty IO memory")
            in 0xFF00..0xFF4C - 1 -> return io.read(addr)
            in 0xFF4C..0xFF80 - 1 -> return 0//throw EmulatorException("Read attempt from unusable, empty IO memory")
            in 0xFF80..0xFFFF - 1 -> return internalRam[addr - 0xFF80]
            0xFFFF -> return ie
            else -> throw EmulatorException("Read address out of range: " + toHex(addr))
        }
    }

    fun readInt(addr: Int): Int {
        return read(addr).toUnsignedInt()
    }

    fun read16(addr: Int): Int {
        val ls = readInt(addr)
        val hs = readInt(addr + 1)

        return ((hs shl 8) or ls)
    }

    fun write(addr: Int, value: Byte) {
        when (addr) {
            in 0x0000..0x8000 - 1 -> rom.mbc.write(addr, value)
            in 0x8000..0xA000 - 1 -> gpu.vram[addr - 0x8000] = value
            in 0xA000..0xC000 - 1 -> rom.mbc.write(addr, value)
            in 0xC000..0xE000 - 1 -> ram[addr - 0xC000] = value
            in 0xE000..0xFE00 - 1 -> ram[addr - 0xE000] = value //ram echo
            in 0xFE00..0xFEA0 - 1 -> oam[addr - 0xFE00] = value
            in 0xFEA0..0xFF00 - 1 -> return//throw EmulatorException("Write attempt to unusable, empty IO memory")
            in 0xFF00..0xFF4C - 1 -> io.write(addr, value)
            in 0xFF4C..0xFF80 - 1 -> return//throw EmulatorException("Write attempt to unusable, empty IO memory")
            in 0xFF80..0xFFFF - 1 -> internalRam[addr - 0xFF80] = value
            0xFFFF -> ie = value
            else -> throw EmulatorException("Write address out of range: " + toHex(addr))
        }

        debuggerListener.onMemoryWrite(addr, value)
    }

    fun write(addr: Int, value: Int) {
        write(addr, value.toByte())
    }

    fun write16(addr: Int, value: Int) {
        write(addr, value)
        write(addr + 1, value ushr 8)
    }

    fun addDebuggerListener(listener: DebuggerListener) {
        debuggerDelegates.add(listener)
    }

    inner class DebuggerDelegate : DebuggerListener {
        override fun onCpuTick(oldPc: Int, pc: Int) {
            if (debuggerDelegates.size == 0) return
            for (listener in debuggerDelegates)
                listener.onCpuTick(oldPc, pc)
        }

        override fun onMemoryWrite(addr: Int, value: Byte) {
            if (debuggerDelegates.size == 0) return
            for (listener in debuggerDelegates)
                listener.onMemoryWrite(addr, value)
        }

        override fun onRegisterWrite(reg: Reg, value: Byte) {
            if (debuggerDelegates.size == 0) return
            for (listener in debuggerDelegates)
                listener.onRegisterWrite(reg, value)
        }

    }

    fun interrupt(interrupt: Interrupt) {
        write(REG_IF, read(REG_IF).setBit(interrupt.interruptBit))
        interruptHandlers.forEach { it.invoke(interrupt) }
    }
}

enum class Interrupt(val interruptBit: Int, val addr: Int) {
    //ordered by priority
    VBLANK(0, 0x0040),
    LCDC(1, 0x0048),
    TIMER(2, 0x0050),
    SERIAL(3, 0x0058),
    JOYPAD(4, 0x0060),
}

enum class EmulatorMode {
    NORMAL, DEBUGGING
}
