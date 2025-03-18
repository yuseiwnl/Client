package jp.client.event

import net.minecraft.block.Block
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.Entity
import net.minecraft.item.EnumAction
import net.minecraft.network.Packet
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World

class AttackEvent(
    val entity: Entity
) : Event()

class BlockAABBEvent(
    val world: World,
    val block: Block,
    var blockPos: BlockPos,
    var boundingBox: AxisAlignedBB?,
    var maskBoundingBox: AxisAlignedBB
) : Event()

class BlockBreakEvent(
    val blockPos: BlockPos
) : Event()

class BlockDamageEvent(
    val blockPos: BlockPos
) : Event()

class ChatEvent(
    val message: String
) : Event()

class GetCollisionBorderSizeEvent(
    val entity: Entity,
    var size: Float
) : Event()

class JumpEvent(
    var yaw: Float
) : Event()

class KeyEvent(
    val key: Int
) : Event()

class LoadWorldEvent() : Event()

class LookEvent(
    var yaw: Float,
    var pitch: Float
) : Event()

class MinimumMotionEvent(
    var motion: Double
) : Event()

class MotionEvent(
    var posX: Double,
    var posY: Double,
    var posZ: Double,
    var yaw: Float,
    var pitch: Float,
    var onGround: Boolean,
    var state: State
) : Event() {
    enum class State {
        PRE, POST
    }
}

class MouseOverEvent(
    var movingObjectPosition: MovingObjectPosition?,
    var range: Double
) : Event()

class MouseRotationEvent(
    var deltaX: Float,
    var deltaY: Float
) : Event()

class MoveInputEvent(
    var forward: Float,
    var strafe: Float,
    var jump: Boolean
) : Event()

class PacketEvent(
    var packet: Packet<*>,
    var state: State
) : Event() {
    enum class State {
        RECEIVE, SEND
    }
}

class PreUpdateEvent : Event()

class Render2DEvent(
    val partialTicks: Float,
    val scaledResolution: ScaledResolution
) : Event()

class Render3DEvent(
    val partialTicks: Float,
) : Event()

class RenderItemEvent(
    var enumAction: EnumAction,
    var useItem: Boolean
) : Event()

class RenderLivingEvent(
    val entity: Entity,
    val state: State
) : Event() {
    enum class State {
        PRE, POST
    }
}

class RenderNameEvent(
    val entity: Entity,
    val state: State
) : Event() {
    enum class State {
        PRE, POST
    }
}

class SlowdownEvent(
    val type: Type
) : Event() {
    enum class Type {
        KEEP_SPRINT, NO_SLOW
    }
}

class StrafeEvent(
    var strafe: Float,
    var forward: Float,
    var friction: Float,
    var yaw: Float
) : Event()

class TeleportEvent(
    /*
    var response: C03PacketPlayer,
    var posX: Double,
    var posY: Double,
    var posZ: Double,
    var yaw: Float,
    var pitch: Float
     */
) : Event()

class TickEvent : Event()