package jp.client.module

import jp.client.Client
import jp.client.event.KeyEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.impl.combat.*
import jp.client.module.impl.movement.*
import jp.client.module.impl.other.*
import jp.client.module.impl.player.*
import jp.client.module.impl.visual.*

class ModuleManager : ArrayList<Module>() {
    init {
        this.addAll(listOf(
            AimAssist,
            AutoClicker,
            KeepSprint,
            KillAura,
            Reach,
            Velocity,

            //Disabler,

            NoJumpDelay,
            NoSlowdown,
            Sprint,
            //Speed,

            Derp,
            GhostHand,
            Logger,

            FastMine,
            FastPlace,
            //NoFall,
            Nuker,

            Ambience,
            Camera,
            Chams,
            HUD,
            Nametags,
            TargetHUD,
            Xray
        ))

        Client.eventBus.register(this)
    }

    @SubscribeEvent
    fun onKey(e: KeyEvent) {
        stream().filter { module: Module -> module.keyCode == e.key }.forEach { obj: Module -> obj.toggle() }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Module> get(module: Class<T>): T {
        return stream()
            .filter { mod: Module -> mod.javaClass == module }
            .findAny().orElse(null) as T
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Module> get(name: String): T? {
        return stream()
            .filter { module: Module ->
                module.name.equals(name, ignoreCase = true)
            }
            .findAny().orElse(null) as T?
    }
}