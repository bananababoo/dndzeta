package org.banana_inc

import org.banana_inc.util.initialization.InitOnStartup
import java.util.*

@InitOnStartup
class ServerSession {

    companion object {
        val sessionID: UUID = UUID.randomUUID()
    }

}