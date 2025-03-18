package jp.client.util.vector

import kotlin.math.sqrt

class Vector3d(val x: Double, val y: Double, val z: Double) {
    fun add(x: Double, y: Double, z: Double): Vector3d {
        return Vector3d(this.x + x, this.y + y, this.z + z)
    }

    fun add(vector: Vector3d): Vector3d {
        return add(vector.x, vector.y, vector.z)
    }

    fun subtract(x: Double, y: Double, z: Double): Vector3d {
        return add(-x, -y, -z)
    }

    fun subtract(vector: Vector3d): Vector3d {
        return add(-vector.x, -vector.y, -vector.z)
    }

    fun length(): Double {
        return sqrt(x * x + y * y + z * z)
    }

    fun multiply(v: Double): Vector3d {
        return Vector3d(x * v, y * v, z * v)
    }
}