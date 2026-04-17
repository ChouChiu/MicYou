package com.lanrhyme.micyou

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Constants 常量定义测试
 */
class ConstantsTest {

    @Test
    fun testAudioFrameSize() {
        // RNNoise 帧大小应为 480 (10ms at 48kHz)
        assertEquals(480, Constants.AUDIO_FRAME_SIZE)
    }

    @Test
    fun testUlunasWindowSize() {
        // Ulunas FFT 窗口大小应为 960
        assertEquals(960, Constants.ULUNAS_WINDOW_SIZE)
    }

    @Test
    fun testMaxPacketSize() {
        // 最大数据包大小应为 2MB
        assertEquals(2 * 1024 * 1024, Constants.MAX_PACKET_SIZE)
    }

    @Test
    fun testServerStopTimeout() {
        // 服务器停止超时应为 5000ms
        assertEquals(5000L, Constants.SERVER_STOP_TIMEOUT_MS)
    }

    @Test
    fun testExitCleanupTimeout() {
        // 退出清理超时应为 3000ms
        assertEquals(3000L, Constants.EXIT_CLEANUP_TIMEOUT_MS)
    }

    @Test
    fun testTcpConnectionTimeout() {
        // TCP 连接超时应为 10000ms
        assertEquals(10000L, Constants.TCP_CONNECTION_TIMEOUT_MS)
    }

    @Test
    fun testAudioPacketChannelCapacity() {
        // 音频包通道容量应为 32
        assertEquals(32, Constants.AUDIO_PACKET_CHANNEL_CAPACITY)
    }

    @Test
    fun testMessageChannelCapacity() {
        // 消息通道容量应为 64
        assertEquals(64, Constants.MESSAGE_CHANNEL_CAPACITY)
    }
}