package com.lanrhyme.micyou.plugin

import com.lanrhyme.micyou.Logger
import com.lanrhyme.micyou.plugin.PluginPermission
import java.io.File
import java.io.FilePermission
import java.net.NetPermission
import java.security.Permission

/**
 * 插件安全管理器，用于检查插件的操作权限。
 * 注意：当前实现为被动检查模式，需要插件主动调用或在关键操作处集成检查。
 *
 * TODO: 未来可考虑实现主动安全策略，通过自定义 SecurityManager 或
 * 使用 Java 的 Policy 文件来强制执行权限控制。
 */
class PluginSecurityManager(
    private val pluginId: String,
    private val pluginDir: File,
    private val permissions: Set<PluginPermission>
) {
    private val allowedPaths = mutableSetOf<String>()

    init {
        // 插件始终可以访问自己的目录
        allowedPaths.add(pluginDir.absolutePath)
        allowedPaths.add(File(pluginDir, "data").absolutePath)
        allowedPaths.add(File(pluginDir, "assets").absolutePath)
    }

    /**
     * 检查指定权限是否被允许
     * @return true 如果权限被允许，false 否则
     */
    fun checkPermission(perm: Permission): Boolean {
        return when (perm) {
            is FilePermission -> checkFilePermission(perm)
            is NetPermission -> checkNetworkPermission(perm)
            is RuntimePermission -> checkRuntimePermission(perm)
            else -> permissions.isNotEmpty() // 其他权限默认允许（如果插件有任何权限）
        }
    }

    /**
     * 检查文件访问权限
     * 仅允许访问插件数据目录（需要 STORAGE 权限）
     */
    private fun checkFilePermission(perm: FilePermission): Boolean {
        if (!permissions.contains(PluginPermission.STORAGE)) {
            return false
        }

        val path = perm.name

        for (allowedPath in allowedPaths) {
            if (path.startsWith(allowedPath)) {
                return true
            }
        }

        return false
    }

    /**
     * 检查网络权限
     */
    private fun checkNetworkPermission(perm: NetPermission): Boolean {
        return permissions.contains(PluginPermission.NETWORK)
    }

    /**
     * 检查运行时权限
     */
    private fun checkRuntimePermission(perm: RuntimePermission): Boolean {
        val name = perm.name
        return when {
            name.startsWith("accessDeclaredMembers") -> true // 允许反射访问
            name.startsWith("createClassLoader") -> false // 禁止创建新的 ClassLoader
            name.startsWith("getClassLoader") -> true
            name.startsWith("exitVM") -> false // 禁止退出 VM
            name.startsWith("setSecurityManager") -> false // 禁止修改安全管理器
            name.startsWith("getenv") -> permissions.contains(PluginPermission.SYSTEM_INFO)
            name.startsWith("getProperty") -> permissions.contains(PluginPermission.SYSTEM_INFO)
            else -> true
        }
    }

    /**
     * 添加额外的允许访问路径
     */
    fun addAllowedPath(path: String) {
        allowedPaths.add(path)
    }

    /**
     * 检查是否有指定权限
     */
    fun hasPermission(permission: PluginPermission): Boolean {
        return permissions.contains(permission)
    }

    companion object {
        private val pluginSecurityManagers = mutableMapOf<String, PluginSecurityManager>()

        fun register(pluginId: String, pluginDir: File, permissions: Set<PluginPermission>): PluginSecurityManager {
            val manager = PluginSecurityManager(pluginId, pluginDir, permissions)
            pluginSecurityManagers[pluginId] = manager
            Logger.i("PluginSecurityManager", "Registered security manager for plugin: $pluginId with permissions: $permissions")
            return manager
        }

        fun unregister(pluginId: String) {
            pluginSecurityManagers.remove(pluginId)
            Logger.d("PluginSecurityManager", "Unregistered security manager for plugin: $pluginId")
        }

        fun get(pluginId: String): PluginSecurityManager? = pluginSecurityManagers[pluginId]

        fun checkPluginPermission(pluginId: String, perm: Permission): Boolean {
            val manager = pluginSecurityManagers[pluginId]
            if (manager == null) {
                Logger.w("PluginSecurityManager", "No security manager registered for plugin: $pluginId")
                return false
            }
            return manager.checkPermission(perm)
        }

        /**
         * 检查插件是否有指定权限类型
         */
        fun hasPermission(pluginId: String, permission: PluginPermission): Boolean {
            return pluginSecurityManagers[pluginId]?.hasPermission(permission) ?: false
        }
    }
}
