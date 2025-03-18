package jp.client.module.setting

import jp.client.module.Module

open class Setting<T>(val name: String, val parent: Module, val defaultSetting: T) {
    var setting: T = defaultSetting

    init {
        parent.settings.add(this)
    }
}