package jp.client.component.impl

import jp.client.component.Component
import jp.client.event.*
import jp.client.event.annotations.SubscribeEvent
import jp.client.util.chat.ChatUtil
import kotlin.math.abs

object RotationComponent : Component() {
    var active = false
    var smoothed = false
    var yaw = 0f
    var pitch = 0f
    var targetYaw = 0f
    var targetPitch = 0f
    var prevYaw = 0f
    var prevPitch = 0f

    fun setRotations(yaw: Float, pitch: Float) {
        active = true
        this.targetYaw = yaw
        this.targetPitch = pitch

        smooth()
    }

    @SubscribeEvent(EventPriority.VERY_LOW)
    fun onPreUpdate(e: PreUpdateEvent) {
        if (!active) {
            this.yaw = mc.thePlayer.rotationYaw
            this.targetYaw = mc.thePlayer.rotationYaw
            this.prevYaw = mc.thePlayer.rotationYaw
            this.pitch = mc.thePlayer.rotationPitch
            this.targetPitch = mc.thePlayer.rotationPitch
            this.prevPitch = mc.thePlayer.rotationPitch
        }

        if (active) {
            smooth()
        }
    }

    @SubscribeEvent(EventPriority.VERY_LOW)
    fun onLook(e: LookEvent) {
        if (active) {
            e.yaw = this.yaw
            e.pitch = this.pitch
        }
    }

    /*
    @SubscribeEvent(EventPriority.VERY_LOW)
    fun onStrafe(e: StrafeEvent) {
        if (active) {
            e.yaw = this.yaw
        }
    }

    @SubscribeEvent(EventPriority.VERY_LOW)
    fun onJump(e: JumpEvent) {
        if (active) {
            e.yaw = this.yaw
        }
    }
     */

    @SubscribeEvent(EventPriority.VERY_LOW)
    fun onMotion(e: MotionEvent) {
        if (active) {
            e.yaw = this.yaw
            e.pitch = this.pitch

            if (abs((this.yaw - mc.thePlayer.rotationYaw) % 360) < 1 && abs((this.pitch - mc.thePlayer.rotationPitch)) < 1) {
                active = false
                ChatUtil.display("rotation done")
            }

            this.prevYaw = this.yaw
            this.prevPitch = this.pitch
        } else {
            this.prevYaw = mc.thePlayer.rotationYaw
            this.prevPitch = mc.thePlayer.rotationPitch
        }

        this.targetYaw = mc.thePlayer.rotationYaw
        this.targetPitch = mc.thePlayer.rotationPitch

        smoothed = false
    }

    fun smooth() {
        if (!smoothed) {
            this.yaw = this.targetYaw //RotationUtil.updateRotation(this.prevYaw, this.targetYaw, 72f)
            this.pitch = this.targetPitch //wRotationUtil.updateRotation(this.prevPitch, this.targetPitch, 72f)
        }

        smoothed = true
    }
}