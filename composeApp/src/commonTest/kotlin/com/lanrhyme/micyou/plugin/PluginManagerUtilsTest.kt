package com.lanrhyme.micyou.plugin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * PluginManager 工具函数测试
 */
class PluginManagerUtilsTest {

    /**
     * 版本比较函数（从 PluginManager 复制用于测试）
     */
    private fun compareVersions(version1: String, version2: String): Int {
        val parts1 = version1.split(".")
        val parts2 = version2.split(".")

        val maxLength = maxOf(parts1.size, parts2.size)

        for (i in 0 until maxLength) {
            val v1 = parts1.getOrNull(i)?.toIntOrNull() ?: 0
            val v2 = parts2.getOrNull(i)?.toIntOrNull() ?: 0

            if (v1 != v2) {
                return v1 - v2
            }
        }

        return 0
    }

    @Test
    fun testCompareVersionsEqual() {
        assertEquals(0, compareVersions("1.0.0", "1.0.0"))
        assertEquals(0, compareVersions("2.5.3", "2.5.3"))
        assertEquals(0, compareVersions("1.0", "1.0.0")) // 缺失部分视为 0
    }

    @Test
    fun testCompareVersionsGreater() {
        assertTrue(compareVersions("1.0.1", "1.0.0") > 0)
        assertTrue(compareVersions("2.0.0", "1.9.9") > 0)
        assertTrue(compareVersions("1.10.0", "1.9.0") > 0)
        assertTrue(compareVersions("1.0.0", "0.9.9") > 0)
    }

    @Test
    fun testCompareVersionsLess() {
        assertTrue(compareVersions("1.0.0", "1.0.1") < 0)
        assertTrue(compareVersions("1.9.9", "2.0.0") < 0)
        assertTrue(compareVersions("1.9.0", "1.10.0") < 0)
        assertTrue(compareVersions("0.9.9", "1.0.0") < 0)
    }

    @Test
    fun testCompareVersionsWithMissingParts() {
        assertEquals(0, compareVersions("1", "1.0.0"))
        assertTrue(compareVersions("1.1", "1.0.0") > 0)
        assertTrue(compareVersions("1.0", "1.0.1") < 0)
    }

    @Test
    fun testCompareVersionsWithNonNumeric() {
        // 非数字部分视为 0
        assertEquals(0, compareVersions("1.0.0", "1.a.0"))
        assertTrue(compareVersions("1.1.0", "1.a.0") > 0)
    }

    @Test
    fun testPluginPermissionEnum() {
        // 验证权限枚举存在
        val storage = PluginPermission.STORAGE
        val network = PluginPermission.NETWORK
        val systemInfo = PluginPermission.SYSTEM_INFO

        assertEquals("STORAGE", storage.name)
        assertEquals("NETWORK", network.name)
        assertEquals("SYSTEM_INFO", systemInfo.name)
    }
}