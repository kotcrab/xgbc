import com.kotcrab.xgbc.*
import org.junit.Assert.*
import org.junit.Test

class TestUtils {
    @Test
    fun testIsBitSet() {
        val byte = 0b01100111.toByte()
        assertTrue(byte.isBitSet(0))
        assertTrue(byte.isBitSet(1))
        assertTrue(byte.isBitSet(2))
        assertFalse(byte.isBitSet(3))
        assertFalse(byte.isBitSet(4))
        assertTrue(byte.isBitSet(5))
        assertTrue(byte.isBitSet(6))
        assertFalse(byte.isBitSet(7))
    }

    @Test
    fun testSetBit() {
        val byte = 0b0000.toByte()
        assertEquals(byte.setBit(1).setBit(3), 0b1010.toByte())
    }

    @Test
    fun testResetBit() {
        val byte = 0b1111.toByte()
        assertEquals(byte.resetBit(0).resetBit(2), 0b1010.toByte())
    }

    @Test
    fun testToggleBit() {
        val byte = 0b0011.toByte()
        assertEquals(byte.toggleBit(0).toggleBit(1).toggleBit(2).toggleBit(3), 0b1100.toByte())
        assertEquals(byte.toggleBit(0).toggleBit(0), 0b0011.toByte())
    }

    @Test
    fun testSetBitState() {
        val byte = 0b0011.toByte()
        assertEquals(byte.setBitState(0, true).setBitState(0, true).setBitState(2, true), 0b0111.toByte())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testIsBitSetException() {
        val byte = 0b01100111.toByte()
        byte.isBitSet(8)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetBitException() {
        val byte = 0b01100111.toByte()
        byte.setBit(8)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testResetBitException() {
        val byte = 0b01100111.toByte()
        byte.resetBit(8)
    }

    @Test
    fun testByteRotate() {
        val byte = 0b00010111.toByte()
        assertEquals(byte.rotateLeft(1), 0b00101110.toByte())
        assertEquals(byte.rotateRight(1), 0b10001011.toByte())
        assertEquals(byte.rotateLeft(3), 0b10111000.toByte())
        assertEquals(byte.rotateRight(3), 0b11100010.toByte())

        val byte2 = 0b11100111.toByte()
        assertEquals(byte2.rotateLeft(1), 0b11001111.toByte())
        assertEquals(byte2.rotateRight(1), 0b11110011.toByte())
    }

    @Test
    fun testBooleanToInt() {
        assertTrue(true.toInt() == 1)
        assertTrue(false.toInt() == 0)
    }
}
