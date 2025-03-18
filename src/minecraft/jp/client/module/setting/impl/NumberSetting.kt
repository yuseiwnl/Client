package jp.client.module.setting.impl

import jp.client.module.Module
import jp.client.module.setting.Setting

class NumberSetting(name: String, parent: Module, defaultSetting: Number, val min: Number, val max: Number) : Setting<Number>(name, parent, defaultSetting) {
}