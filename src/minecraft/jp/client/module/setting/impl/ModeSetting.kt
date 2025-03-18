package jp.client.module.setting.impl

import jp.client.module.Module
import jp.client.module.setting.Setting

class ModeSetting(name: String, parent: Module, defaultMode: String, vararg modes: String) : Setting<String>(name, parent, defaultMode) {
    var modes: List<String> = modes.asList()
    private var modeIndex = modes.indexOf(defaultMode)
    var currentMode: String = defaultMode
}