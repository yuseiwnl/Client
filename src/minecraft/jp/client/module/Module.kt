package jp.client.module

import jp.client.Client
import jp.client.module.setting.Setting
import jp.client.util.InstanceAccess
import org.lwjgl.input.Keyboard
import java.util.concurrent.CopyOnWriteArrayList

open class Module(var name: String, val category: Category, var keyCode: Int) : InstanceAccess {
    constructor(name: String, category: Category) : this(name, category, Keyboard.KEY_NONE)

    private var toggled = false
    var settings = CopyOnWriteArrayList<Setting<*>>()

    fun isToggled(): Boolean {
        return toggled
    }

    fun setToggled(toggled: Boolean) {
        this.toggled = toggled
        if (this.toggled) {
            onEnable()
        } else {
            onDisable()
        }
    }

    fun toggle() {
        setToggled(!toggled)
    }

    open fun onEnable() {
        Client.eventBus.register(this)
    }

    open fun onDisable() {
        Client.eventBus.unregister(this)
    }

    enum class Category(val category: String) {
        COMBAT("Combat"),
        EXPLOIT("Exploit"),
        MOVEMENT("Movement"),
        OTHER("Other"),
        PLAYER("Player"),
        VISUAL("Visual")
    }
}