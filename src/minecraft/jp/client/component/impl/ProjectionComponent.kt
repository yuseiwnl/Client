package jp.client.component.impl

import jp.client.component.Component
import jp.client.event.EventPriority
import jp.client.event.Render2DEvent
import jp.client.event.annotations.SubscribeEvent
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.opengl.Display
import org.lwjgl.util.glu.GLU
import javax.vecmath.Vector3d
import javax.vecmath.Vector4d

object ProjectionComponent : Component() {
    private val nextProjections = HashMap<Entity, Projection>()
    private var currentProjections = HashMap<Entity, Projection>()

    @SubscribeEvent(EventPriority.VERY_LOW)
    fun onRender2D(e: Render2DEvent) {
        //Executors.newFixedThreadPool(1.coerceAtLeast(2.coerceAtMost(Runtime.getRuntime().availableProcessors() - 1))).execute {
            val newProjections = HashMap<Entity, Projection>()

            synchronized(nextProjections) {
                for ((key, projection) in nextProjections) {
                    projection.position = project(key)

                    newProjections[key] = projection
                }

                nextProjections.clear()
            }

            currentProjections = newProjections
        //}
    }

    fun get(entity: Entity?): Vector4d? {
        if (entity == null) return null

        if (!nextProjections.containsKey(entity)) {
            nextProjections[entity] = Projection()
        }

        val projection = currentProjections[entity]

        return projection?.position
    }

    private fun project(factor: Int, x: Double, y: Double, z: Double): Vector3d? {
        return if (GLU.gluProject(x.toFloat(), y.toFloat(), z.toFloat(), ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, ActiveRenderInfo.OBJECTCOORDS)) {
            Vector3d(
                (ActiveRenderInfo.OBJECTCOORDS[0] / factor).toDouble(),
                ((Display.getHeight() - ActiveRenderInfo.OBJECTCOORDS[1]) / factor).toDouble(),
                ActiveRenderInfo.OBJECTCOORDS[2].toDouble()
            )
        } else {
            null
        }
    }

    fun project(entity: Entity): Vector4d? {
        val renderX = mc.renderManager.renderPosX
        val renderY = mc.renderManager.renderPosY
        val renderZ = mc.renderManager.renderPosZ

        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - renderX
        val y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks) - renderY
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - renderZ
        val width = (entity.width + 0.14) / 2
        val height = entity.height + (if (entity.isSneaking) -0.1 else 0.2) + 0.01
        val aabb = AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width)
        val vectors = listOf(
            Vector3d(aabb.minX, aabb.minY, aabb.minZ),
            Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
            Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
            Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
            Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
            Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
            Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
            Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)
        )

        var position: Vector4d? = null
        for (vector in vectors) {
            val projectedVector = project(ScaledResolution(mc).scaleFactor, vector.x, vector.y, vector.z)

            if (projectedVector != null && projectedVector.z >= 0.0 && projectedVector.z < 1.0) {
                if (position == null) {
                    position = Vector4d(projectedVector.x, projectedVector.y, projectedVector.z, 0.0)
                }

                position = Vector4d(
                    minOf(projectedVector.x, position.x),
                    minOf(projectedVector.y, position.y),
                    maxOf(projectedVector.x, position.z),
                    maxOf(projectedVector.y, position.w)
                )
            }
        }

        return position
    }

    private class Projection {
        var position: Vector4d? = null
    }
}