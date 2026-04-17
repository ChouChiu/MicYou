package com.lanrhyme.micyou.plugin

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * 插件权限类型定义
 */
@Serializable
enum class PluginPermission {
    /** 文件存储权限 - 允许插件读写其数据目录 */
    STORAGE,
    /** 网络权限 - 允许插件进行网络通信 */
    NETWORK,
    /** 系统信息权限 - 允许插件访问系统基本信息 */
    SYSTEM_INFO
}

@Serializable
data class PluginManifest(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String = "",
    val tags: List<String> = emptyList(),
    val platform: PluginPlatform = PluginPlatform.BOTH,
    /** 插件请求的权限列表，默认为空（仅能访问自身数据目录） */
    val permissions: List<PluginPermission> = emptyList(),
    @SerialName("minApiVersion")
    val minApiVersion: String,
    @SerialName("mainClass")
    val mainClass: String
)
