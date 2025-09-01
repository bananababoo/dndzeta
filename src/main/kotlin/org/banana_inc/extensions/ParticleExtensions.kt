package org.banana_inc.extensions

import com.destroystokyo.paper.ParticleBuilder
import org.banana_inc.util.storage.tempStorage

fun ParticleBuilder.radiusAsync(radius: Long): ParticleBuilder = apply {
    this.receivers(this.location()!!.getNearbyPlayers(radius))
}

fun ParticleBuilder.showToPlayers(radius: Long, updateEveryTicks: Int): ParticleBuilder = apply {
    this.tempStorage.set<Int>("tick", (this.tempStorage.get<Int>("tick")?: 0) + 1)
    if (this.tempStorage.get<Int>("tick")!! % updateEveryTicks == 0) {
        this.receivers(this.location()!!.getNearbyPlayers(radius))
    }
}

