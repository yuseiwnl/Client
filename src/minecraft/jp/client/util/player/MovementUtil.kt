package jp.client.util.player

import jp.client.event.MoveInputEvent
import jp.client.util.InstanceAccess
import net.minecraft.potion.Potion
import net.minecraft.util.MathHelper
import kotlin.math.*


object MovementUtil : InstanceAccess {
    const val WALK_SPEED = 0.221
    const val MOD_SPRINTING = 1.3F

    fun getAllowedHorizontalDistance(): Double {
        var horizontalDistance = WALK_SPEED
        if (mc.thePlayer.isSprinting)
            horizontalDistance *= MOD_SPRINTING

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).duration > 0) {
            horizontalDistance *= 1 + (0.15 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1))
        }

        return horizontalDistance
    }

    fun getDirection(): Double {
        val moveForward = mc.thePlayer.moveForward
        val moveStrafing = mc.thePlayer.moveStrafing
        var rotationYaw = mc.thePlayer.rotationYaw
        val isGoingBackward = moveForward < 0F
        val isGoingForward = moveForward > 0F
        var forward = 1F

        if (isGoingBackward) {
            rotationYaw += 180F
        }
        if (isGoingBackward) forward = -0.5F
        else if (isGoingForward) forward = 0.5F
        if (moveStrafing > 0F) rotationYaw -= 90F * forward
        if (moveStrafing < 0F) rotationYaw += 90F * forward
        return Math.toRadians(rotationYaw.toDouble())
    }

    fun isMoving(): Boolean {
        return (mc.thePlayer.movementInput.moveForward != 0f || mc.thePlayer.movementInput.moveStrafe != 0f)
    }

    fun speed(): Double {
        return hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ)
    }

    fun strafe(speed: Double) {
        if (!isMoving())
            return

        val yaw = getDirection()
        mc.thePlayer.motionX = -sin(yaw) * speed
        mc.thePlayer.motionZ = cos(yaw) * speed
    }

    fun fixMovement(event: MoveInputEvent, yaw: Float) {
        val forward: Float = event.forward
        val strafe: Float = event.strafe

        val angle = MathHelper.wrapAngleTo180_double(
            Math.toDegrees(
                direction(mc.thePlayer.rotationYaw, forward, strafe)
            )
        )

        if (forward == 0f && strafe == 0f) {
            return
        }

        var closestForward = 0f
        var closestStrafe = 0f
        var closestDifference = Float.MAX_VALUE

        var predictedForward = -1f
        while (predictedForward <= 1f) {
            var predictedStrafe = -1f
            while (predictedStrafe <= 1f) {
                if (predictedStrafe == 0f && predictedForward == 0f) {
                    predictedStrafe += 1f
                    continue
                }

                val predictedAngle = MathHelper.wrapAngleTo180_double(
                    Math.toDegrees(
                        direction(yaw, predictedForward, predictedStrafe)
                    )
                )
                val difference: Double = wrappedDifference(angle, predictedAngle)

                if (difference < closestDifference) {
                    closestDifference = difference.toFloat()
                    closestForward = predictedForward
                    closestStrafe = predictedStrafe
                }
                predictedStrafe += 1f
            }
            predictedForward += 1f
        }

        event.forward = closestForward
        event.strafe = closestStrafe
    }

    fun wrappedDifference(number1: Double, number2: Double): Double {
        return min(
            abs(number1 - number2),
            min(abs(number1 - 360) - abs(number2 - 0), abs(number2 - 360) - abs(number1 - 0))
        )
    }

    fun direction(rotationYaw: Float, moveForward: Float, moveStrafing: Float): Double {
        var rotationYaw = rotationYaw
        if (moveForward < 0f) rotationYaw += 180f

        var forward = 1f

        if (moveForward < 0f) forward = -0.5f
        else if (moveForward > 0f) forward = 0.5f

        if (moveStrafing > 0f) rotationYaw -= 90f * forward
        if (moveStrafing < 0f) rotationYaw += 90f * forward

        return Math.toRadians(rotationYaw.toDouble())
    }
}