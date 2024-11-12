package com.zorbeytorunoglu.kLib.task

import kotlinx.coroutines.Job
import org.bukkit.scheduler.BukkitTask

sealed interface CancelableTask {

    fun cancel()

}

class SyncTask(val task: BukkitTask): CancelableTask{
    override fun cancel() = task.cancel()

}

class AsyncTask(val task: Job): CancelableTask{
    override fun cancel() = task.cancel()

}
