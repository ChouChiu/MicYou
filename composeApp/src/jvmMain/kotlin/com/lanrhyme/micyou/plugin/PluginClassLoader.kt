package com.lanrhyme.micyou.plugin

import java.net.URL

/**
 * 自定义插件类加载器，支持安全检查。
 * 注意：当前实现为被动安全模式，需要主动集成权限检查。
 */
class PluginClassLoader(
    urls: Array<URL>,
    parent: ClassLoader,
    private val pluginId: String,
    private val securityManager: PluginSecurityManager
) : java.net.URLClassLoader(urls, parent) {

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        synchronized(getClassLoadingLock(name)) {
            var c = findLoadedClass(name)

            if (c == null) {
                // 插件自己的类（com.lanrhyme.micyou.plugin. 但不是 api）优先从插件 jar 加载
                if (name.startsWith("com.lanrhyme.micyou.plugin.") &&
                    !name.startsWith("com.lanrhyme.micyou.plugin.api.")) {
                    try {
                        c = findClass(name)
                    } catch (e: ClassNotFoundException) {
                        // 插件 jar 中找不到，尝试从父加载器加载
                        c = parent.loadClass(name)
                    }
                } else {
                    // 其他类优先从父加载器加载（避免污染主应用类）
                    try {
                        c = parent.loadClass(name)
                    } catch (e: ClassNotFoundException) {
                        c = findClass(name)
                    }
                }
            }

            if (resolve) {
                resolveClass(c)
            }

            return c
        }
    }

    /**
     * 获取关联的安全管理器
     */
    fun getSecurityManager(): PluginSecurityManager = securityManager
}
