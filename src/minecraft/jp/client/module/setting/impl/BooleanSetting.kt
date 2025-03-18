package jp.client.module.setting.impl

import jp.client.module.Module
import jp.client.module.setting.Setting

class BooleanSetting(name: String, parent: Module, defaultSetting: Boolean) : Setting<Boolean>(name, parent, defaultSetting) {
    fun isEnabled(): Boolean {
        return setting
    }

    fun toggle() {
        setting = !isEnabled()
    }
}