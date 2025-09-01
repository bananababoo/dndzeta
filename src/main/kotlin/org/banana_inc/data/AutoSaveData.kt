package org.banana_inc.data

import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.banana_inc.data.database.DatabaseActions
import org.banana_inc.util.initialization.InitOnStartup
import kotlin.time.Duration.Companion.minutes

@InitOnStartup
object AutoSaveData {
    init {
        Scopes.ioScope.launch {
            while(true){
                DatabaseActions.saveAll()
                delay(2.5.minutes)
            }
        }
    }
}