package com.lanrhyme.micyou

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Protocol 数据结构序列化/反序列化测试
 */
@OptIn(ExperimentalSerializationApi::class)
class ProtocolTest {

    private val proto = ProtoBuf {}

    @Test
    fun testAudioPacketMessageSerialization() {
        val buffer = byteArrayOf(1, 2, 3, 4, 5)
        val original = AudioPacketMessage(
            buffer = buffer,
            sampleRate = 48000,
            channelCount = 2,
            audioFormat = 2
        )

        val encoded = proto.encodeToByteArray(AudioPacketMessage.serializer(), original)
        val decoded = proto.decodeFromByteArray(AudioPacketMessage.serializer(), encoded)

        assertTrue(decoded.buffer.contentEquals(original.buffer))
        assertEquals(original.sampleRate, decoded.sampleRate)
        assertEquals(original.channelCount, decoded.channelCount)
        assertEquals(original.audioFormat, decoded.audioFormat)
    }

    @Test
    fun testMuteMessageSerialization() {
        val original = MuteMessage(isMuted = true)

        val encoded = proto.encodeToByteArray(MuteMessage.serializer(), original)
        val decoded = proto.decodeFromByteArray(MuteMessage.serializer(), encoded)

        assertEquals(original.isMuted, decoded.isMuted)
    }

    @Test
    fun testPluginInfoMessageSerialization() {
        val original = PluginInfoMessage(
            id = "com.example.test",
            name = "Test Plugin",
            version = "1.0.0"
        )

        val encoded = proto.encodeToByteArray(PluginInfoMessage.serializer(), original)
        val decoded = proto.decodeFromByteArray(PluginInfoMessage.serializer(), encoded)

        assertEquals(original.id, decoded.id)
        assertEquals(original.name, decoded.name)
        assertEquals(original.version, decoded.version)
    }

    @Test
    fun testPluginSyncMessageSerialization() {
        val plugins = listOf(
            PluginInfoMessage("com.example.plugin1", "Plugin 1", "1.0.0"),
            PluginInfoMessage("com.example.plugin2", "Plugin 2", "2.0.0")
        )
        val original = PluginSyncMessage(plugins = plugins, platform = "Desktop")

        val encoded = proto.encodeToByteArray(PluginSyncMessage.serializer(), original)
        val decoded = proto.decodeFromByteArray(PluginSyncMessage.serializer(), encoded)

        assertEquals(original.plugins.size, decoded.plugins.size)
        assertEquals(original.plugins[0].id, decoded.plugins[0].id)
        assertEquals(original.platform, decoded.platform)
    }

    @Test
    fun testMessageWrapperWithAudioPacket() {
        val audioPacket = AudioPacketMessage(
            buffer = byteArrayOf(0, 1, 2, 3),
            sampleRate = 44100,
            channelCount = 1,
            audioFormat = 2
        )
        val original = MessageWrapper(
            audioPacket = AudioPacketMessageOrdered(sequenceNumber = 100, audioPacket = audioPacket)
        )

        val encoded = proto.encodeToByteArray(MessageWrapper.serializer(), original)
        val decoded = proto.decodeFromByteArray(MessageWrapper.serializer(), encoded)

        assertTrue(decoded.audioPacket != null)
        assertEquals(100, decoded.audioPacket!!.sequenceNumber)
        assertTrue(decoded.audioPacket!!.audioPacket.buffer.contentEquals(audioPacket.buffer))
    }

    @Test
    fun testMessageWrapperWithMute() {
        val original = MessageWrapper(mute = MuteMessage(isMuted = false))

        val encoded = proto.encodeToByteArray(MessageWrapper.serializer(), original)
        val decoded = proto.decodeFromByteArray(MessageWrapper.serializer(), encoded)

        assertTrue(decoded.mute != null)
        assertEquals(false, decoded.mute!!.isMuted)
    }

    @Test
    fun testPacketMagicConstant() {
        // 验证 PACKET_MAGIC 是 "MicY" 的 ASCII 编码
        assertEquals(0x4D696359, PACKET_MAGIC)
        // 验证各字节
        val bytes = PACKET_MAGIC.toString(16).uppercase()
        assertTrue(bytes.contains("4D")) // M
        assertTrue(bytes.contains("69")) // i
        assertTrue(bytes.contains("63")) // c
        assertTrue(bytes.contains("59")) // Y
    }
}