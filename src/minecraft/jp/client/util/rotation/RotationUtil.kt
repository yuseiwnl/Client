package jp.client.util.rotation

import jp.client.util.InstanceAccess
import jp.client.util.chat.ChatUtil
import jp.client.util.vector.Vector2f
import jp.client.util.vector.Vector3d
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper.wrapAngleTo180_float
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot


object RotationUtil : InstanceAccess {
    private fun calculate(from: Vector3d, to: Vector3d): Vector2f {
        val diff: Vector3d = to.subtract(from)
        val distance = hypot(diff.x, diff.z)
        val yaw = (atan2(diff.z, diff.x) * 180.0F / PI).toFloat() - 90.0F
        val pitch = (-(atan2(diff.y, distance) * 180.0F / PI)).toFloat()
        return Vector2f(mc.thePlayer.rotationYaw + wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch))
    }

    fun calculate(entity: Entity): Vector2f {
        return calculate(
            getCustomPositionVector(entity).add(
                0.0, Math.max(0.0, Math.min(mc.thePlayer.posY - entity.posY + mc.thePlayer.eyeHeight, (entity.entityBoundingBox.maxY - entity.entityBoundingBox.minY) * 0.9)
                ), 0.0
            )
        )
    }

    fun calculate2(entity: Entity): Vector2f {
        return calculate(
            getCustomPositionVector(entity).add(
                0.0, Math.max(0.0, Math.min(mc.thePlayer.posY - entity.posY + mc.thePlayer.eyeHeight, (entity.entityBoundingBox.maxY - entity.entityBoundingBox.minY) * 0.6)
                ), 0.0
            )
        )
    }

    fun calculate(entity: Entity, adaptive: Boolean, range: Double): Vector2f {
        val normalRotations = calculate(entity)
        if (!adaptive || RaytraceUtil.isMouseOver(entity, normalRotations.x, normalRotations.y, range)) {
            return normalRotations
        }

        ChatUtil.display("adaptive")
        for (y in 100 downTo 0 step 25) {
            for (x in 100 downTo -100 step 50) {
                for (z in 100 downTo -100 step 50) {
                    val yPercent = y / 100
                    val xPercent = x / 100
                    val zPercent = z / 100

                    val adaptiveRotations = calculate(Vector3d(entity.posX, entity.posY, entity.posZ).add(
                        (entity.entityBoundingBox.maxX - entity.entityBoundingBox.minX) * xPercent,
                        (entity.entityBoundingBox.maxY - entity.entityBoundingBox.minY) * yPercent,
                        (entity.entityBoundingBox.maxZ - entity.entityBoundingBox.minZ) * zPercent))

                    if (RaytraceUtil.isMouseOver(entity, adaptiveRotations.x, adaptiveRotations.y, range)) {
                        return adaptiveRotations
                    }
                }
            }
        }

        return normalRotations
    }

    private fun calculate(to: Vector3d): Vector2f {
        return calculate(getCustomPositionVector(mc.thePlayer).add(0.0, mc.thePlayer.eyeHeight.toDouble(), 0.0), to)
    }

    fun calculate(to: BlockPos): Vector2f {
        return calculate(
            getCustomPositionVector(mc.thePlayer).add(0.0, mc.thePlayer.eyeHeight.toDouble(), 0.0), Vector3d(
                to.x.toDouble(),
                to.y.toDouble(), to.z.toDouble()
            ).add(0.5, 0.5, 0.5)
        )
    }

    private fun getCustomPositionVector(entity: Entity): Vector3d {
        return Vector3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.viewerPosX, entity.posY, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.viewerPosZ)
    }

    fun getYawDifference(entity: Entity): Float {
        val rotationPosition = calculate(entity)
        val differenceYaw = abs(mc.thePlayer.rotationYaw - rotationPosition.x)
        return differenceYaw
    }

    fun isInFOV(entity: Entity, fov: Float): Boolean {
        val rotationPosition = calculate2(entity)
        val differenceYaw = abs(mc.thePlayer.rotationYaw - rotationPosition.x)
        val differencePitch = abs(mc.thePlayer.rotationPitch - rotationPosition.y)
        return differenceYaw <= fov && differencePitch <= fov
    }

    fun updateRotation(from: Float, to: Float, speed: Float): Float {
        var f = wrapAngleTo180_float(to - from)

        if (f > speed) {
            f = speed
        }

        if (f < -speed) {
            f = -speed
        }

        return from + f
    }

    fun calculatePerfectRangeToEntity(entity: Entity): Double {
        val range = 1000.0
        val eyes = mc.thePlayer.getPositionEyes(1F)
        val rotations = calculate(entity)
        val rotationVector = RaytraceUtil.getVectorForRotation(rotations.y, rotations.x)
        val movingObjectPosition = entity.entityBoundingBox.expand(0.1, 0.1, 0.1).calculateIntercept(
            eyes,
            eyes.addVector(rotationVector.xCoord * range, rotationVector.yCoord * range, rotationVector.zCoord * range)
        )

        return movingObjectPosition.hitVec.distanceTo(eyes)
    }
}