package org.banana_inc.data

import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.banana_inc.logger
import org.banana_inc.util.initialization.InitOnStartup
import kotlin.time.Duration.Companion.minutes

@InitOnStartup
class AutoSave {
    init {
        Scopes.ioScope.launch {
            while(true){
                logger.info("AUTO SAVING PlAYERS!!!")
                DatabaseActions.save<Data.Player>()
                delay(2.5.minutes)
                logger.info("AUTO SAVING EVERYTHING!!!")
                DatabaseActions.saveAll()
                delay(2.5.minutes)
            }
        }
    }
}