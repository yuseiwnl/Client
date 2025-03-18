package jp.client.util.rotation

import com.google.common.base.Predicates
import jp.client.util.InstanceAccess
import net.minecraft.entity.Entity
import net.minecraft.util.*

object RaytraceUtil : InstanceAccess {
    fun getLook(yaw: Float, pitch: Float, partialTicks: Float): Vec3 {
        return getVectorForRotation(pitch, yaw)

        /*
        if (partialTicks == 1.0f) {
            return getVectorForRotation(pitch, yaw)
        } else {
            val f: Float = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks
            val f1: Float = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks
            return getVectorForRotation(f, f1)
        }
         */
    }

    fun getRotationOver(yaw: Float, pitch: Float): MovingObjectPosition? {
        val entity: Entity = mc.renderViewEntity

        if (mc.theWorld != null) {
            mc.mcProfiler.startSection("pick")
            var d0: Double = mc.playerController.blockReachDistance.toDouble()
            var objectMouseOver: MovingObjectPosition? = null //rayTrace(yaw, pitch, d0, 1.0F)
            var d1 = d0
            val vec3 = entity.getPositionEyes(1.0F)
            var flag = false
            val i = 3

            if (mc.playerController.extendedReach()) {
                d0 = 6.0
                d1 = 6.0
            } else if (d0 > 3.0) {
                flag = true
            }

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3)
            }

            val vec31 = getLook(yaw, pitch, 1.0F)
            val vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
            var pointedEntity: Entity? = null
            var vec33: Vec3? = null
            val f = 1.0f
            val list: List<Entity> =
                mc.theWorld.getEntitiesInAABBexcluding(
                    entity,
                    entity.entityBoundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
                        .expand(f.toDouble(), f.toDouble(), f.toDouble()),
                    Predicates.and(
                        EntitySelectors.NOT_SPECTATING
                    ) { p_apply_1_ -> p_apply_1_!!.canBeCollidedWith() }
                )
            var d2 = d1

            for (j in list.indices) {
                val entity1 = list[j]
                val f1 = entity1.collisionBorderSize
                val axisalignedbb = entity1.entityBoundingBox.expand(f1.toDouble(), f1.toDouble(), f1.toDouble())
                val movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32)

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0) {
                        pointedEntity = entity1
                        vec33 = if (movingobjectposition == null) vec3 else movingobjectposition.hitVec
                        d2 = 0.0
                    }
                } else if (movingobjectposition != null) {
                    val d3 = vec3.distanceTo(movingobjectposition.hitVec)

                    if (d3 < d2 || d2 == 0.0) {
                        val flag1 = false

                        if (!flag1 && entity1 === entity.ridingEntity) {
                            if (d2 == 0.0) {
                                pointedEntity = entity1
                                vec33 = movingobjectposition.hitVec
                            }
                        } else {
                            pointedEntity = entity1
                            vec33 = movingobjectposition.hitVec
                            d2 = d3
                        }
                    }
                }
            }

            if (pointedEntity != null && flag && (vec3.distanceTo(vec33) > 3.0)) {
                pointedEntity = null
                objectMouseOver = MovingObjectPosition(
                    MovingObjectPosition.MovingObjectType.MISS,
                    vec33,
                    null as EnumFacing?,
                    BlockPos(vec33)
                )
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = MovingObjectPosition(pointedEntity, vec33)
            }

            mc.mcProfiler.endSection()

            return objectMouseOver
        }

        return null
    }

    fun getVectorForRotation(pitch: Float, yaw: Float): Vec3 {
        val f = MathHelper.cos(-yaw * 0.017453292f - Math.PI.toFloat())
        val f1 = MathHelper.sin(-yaw * 0.017453292f - Math.PI.toFloat())
        val f2 = -MathHelper.cos(-pitch * 0.017453292f)
        val f3 = MathHelper.sin(-pitch * 0.017453292f)
        return Vec3((f1 * f2).toDouble(), f3.toDouble(), (f * f2).toDouble())
    }

    fun isMouseOver(target: Entity, yaw: Float, pitch: Float, range: Double): Boolean {
        val entity: Entity = mc.renderViewEntity

        if (mc.theWorld != null) {
            mc.mcProfiler.startSection("pick")
            var d0: Double = mc.playerController.blockReachDistance.toDouble()
            var objectMouseOver: MovingObjectPosition? = null //rayTrace(yaw, pitch, d0, 1.0F)
            var d1 = d0
            val vec3 = entity.getPositionEyes(1.0F)
            var flag = false
            val i = 3

            if (mc.playerController.extendedReach()) {
                d0 = 6.0
                d1 = 6.0
            } else if (d0 > 3.0 || range > 3.0) {
                flag = true
            }

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3)
            }

            val vec31 = getLook(yaw, pitch, 1.0F)
            val vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
            var pointedEntity: Entity? = null
            var vec33: Vec3? = null
            val f = 1.0f
            /*
            val list: List<Entity> =
                mc.theWorld.getEntitiesInAABBexcluding(
                    entity,
                    entity.entityBoundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
                        .expand(f.toDouble(), f.toDouble(), f.toDouble()),
                    Predicates.and(
                        EntitySelectors.NOT_SPECTATING
                    ) { p_apply_1_ -> p_apply_1_!!.canBeCollidedWith() }
                )

             */
            var d2 = d1

            //for (j in list.indices) {
            val entity1 = target //list[j]
            val f1 = entity1.collisionBorderSize
            val axisalignedbb = entity1.entityBoundingBox.expand(f1.toDouble(), f1.toDouble(), f1.toDouble())
            val movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32)

            if (axisalignedbb.isVecInside(vec3)) {
                if (d2 >= 0.0) {
                    pointedEntity = entity1
                    vec33 = if (movingobjectposition == null) vec3 else movingobjectposition.hitVec
                    d2 = 0.0
                }
            } else if (movingobjectposition != null) {
                val d3 = vec3.distanceTo(movingobjectposition.hitVec)

                if (d3 < d2 || d2 == 0.0) {
                    val flag1 = false

                    if (!flag1 && entity1 === entity.ridingEntity) {
                        if (d2 == 0.0) {
                            pointedEntity = entity1
                            vec33 = movingobjectposition.hitVec
                        }
                    } else {
                        pointedEntity = entity1
                        vec33 = movingobjectposition.hitVec
                        d2 = d3
                    }
                }
            }

            //if (entity1 == target)
            //    break
            //}

            if (pointedEntity != null && flag && (vec3.distanceTo(vec33) > range)) {
                pointedEntity = null
                objectMouseOver = MovingObjectPosition(
                    MovingObjectPosition.MovingObjectType.MISS,
                    vec33,
                    null as EnumFacing?,
                    BlockPos(vec33)
                )
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = MovingObjectPosition(pointedEntity, vec33)
            }

            mc.mcProfiler.endSection()

            return objectMouseOver?.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && objectMouseOver.entityHit == target
        }

        return false
    }

    fun getRotationOver2(yaw: Float, pitch: Float): MovingObjectPosition? {
        val entity: Entity = mc.renderViewEntity

        if (mc.theWorld != null) {
            mc.mcProfiler.startSection("pick")
            var d0: Double = mc.playerController.blockReachDistance.toDouble()
            var objectMouseOver: MovingObjectPosition? = rayTrace(yaw, pitch, d0, 1.0F)
            var d1 = d0
            val vec3 = entity.getPositionEyes(1.0F)
            var flag = false
            val i = 3

            if (mc.playerController.extendedReach()) {
                d0 = 6.0
                d1 = 6.0
            } else if (d0 > 3.0) {
                flag = true
            }

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3)
            }

            val vec31 = getLook(yaw, pitch, 1.0F)
            val vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
            var pointedEntity: Entity? = null
            var vec33: Vec3? = null
            val f = 1.0f
            val list: List<Entity> =
                mc.theWorld.getEntitiesInAABBexcluding(
                    entity,
                    entity.entityBoundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
                        .expand(f.toDouble(), f.toDouble(), f.toDouble()),
                    Predicates.and(
                        EntitySelectors.NOT_SPECTATING
                    ) { p_apply_1_ -> p_apply_1_!!.canBeCollidedWith() }
                )
            var d2 = d1

            for (j in list.indices) {
                val entity1 = list[j]
                val f1 = entity1.collisionBorderSize
                val axisalignedbb = entity1.entityBoundingBox.expand(f1.toDouble(), f1.toDouble(), f1.toDouble())
                val movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32)

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0) {
                        pointedEntity = entity1
                        vec33 = if (movingobjectposition == null) vec3 else movingobjectposition.hitVec
                        d2 = 0.0
                    }
                } else if (movingobjectposition != null) {
                    val d3 = vec3.distanceTo(movingobjectposition.hitVec)

                    if (d3 < d2 || d2 == 0.0) {
                        val flag1 = false

                        if (!flag1 && entity1 === entity.ridingEntity) {
                            if (d2 == 0.0) {
                                pointedEntity = entity1
                                vec33 = movingobjectposition.hitVec
                            }
                        } else {
                            pointedEntity = entity1
                            vec33 = movingobjectposition.hitVec
                            d2 = d3
                        }
                    }
                }
            }

            if (pointedEntity != null && flag && (vec3.distanceTo(vec33) > 3.0)) {
                pointedEntity = null
                objectMouseOver = MovingObjectPosition(
                    MovingObjectPosition.MovingObjectType.MISS,
                    vec33,
                    null as EnumFacing?,
                    BlockPos(vec33)
                )
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = MovingObjectPosition(pointedEntity, vec33)
            }

            mc.mcProfiler.endSection()

            return objectMouseOver
        }

        return null
    }

    fun rayTrace(yaw: Float, pitch: Float, blockReachDistance: Double, partialTicks: Float): MovingObjectPosition? {
        val vec3: Vec3 = mc.thePlayer.getPositionEyes(partialTicks)
        val vec31 = getLook(yaw, pitch, partialTicks)
        val vec32 = vec3.addVector(
            vec31.xCoord * blockReachDistance,
            vec31.yCoord * blockReachDistance,
            vec31.zCoord * blockReachDistance
        )
        return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true)
    }
}