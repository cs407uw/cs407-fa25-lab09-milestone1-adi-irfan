package com.cs407.lab09

class Ball(
    private val backgroundWidth: Float,
    private val backgroundHeight: Float,
    private val ballSize: Float
) {
    var posX = 0f
    var posY = 0f
    var velocityX = 0f
    var velocityY = 0f
    private var accX = 0f
    private var accY = 0f

    private var isFirstUpdate = true

    init {
        reset()
    }

    /**
     * Updates the ball's position and velocity based on the given acceleration and time step.
     * Implements Equations (1) and (2) from the lab handout.
     */
    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {
        if (isFirstUpdate) {
            // First update: just store acceleration
            accX = xAcc
            accY = yAcc
            isFirstUpdate = false
            return
        }

        val oldAccX = accX
        val oldAccY = accY

        // Update acceleration for next iteration
        accX = xAcc
        accY = yAcc

        // -------------------------
        // Equation (1): new velocity
        // v1 = v0 + 0.5 * (a1 + a0) * dt
        // -------------------------
        val newVelX = velocityX + 0.5f * (oldAccX + accX) * dT
        val newVelY = velocityY + 0.5f * (oldAccY + accY) * dT

        // -------------------------
        // Equation (2): displacement
        // l = v0 * dt + (1/6) * (3*a0 + a1) * dt^2
        // -------------------------
        val dx = velocityX * dT + (1f / 6f) * (3f * oldAccX + accX) * dT * dT
        val dy = velocityY * dT + (1f / 6f) * (3f * oldAccY + accY) * dT * dT

        posX += dx
        posY += dy

        velocityX = newVelX
        velocityY = newVelY
    }

    /**
     * Ensures the ball does not escape the screen boundaries.
     * If it hits a wall, clamp the position and zero out the velocity.
     */
    fun checkBoundaries() {
        val radius = ballSize / 2f

        // Left wall
        if (posX < radius) {
            posX = radius
            velocityX = 0f
            accX = 0f
        }

        // Right wall
        if (posX > backgroundWidth - radius) {
            posX = backgroundWidth - radius
            velocityX = 0f
            accX = 0f
        }

        // Top wall
        if (posY < radius) {
            posY = radius
            velocityY = 0f
            accY = 0f
        }

        // Bottom wall
        if (posY > backgroundHeight - radius) {
            posY = backgroundHeight - radius
            velocityY = 0f
            accY = 0f
        }
    }

    /**
     * Resets ball to the center of the background with zero velocity & acceleration.
     */
    fun reset() {
        posX = backgroundWidth / 2f
        posY = backgroundHeight / 2f

        velocityX = 0f
        velocityY = 0f

        accX = 0f
        accY = 0f

        isFirstUpdate = true
    }
}