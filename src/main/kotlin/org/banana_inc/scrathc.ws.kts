import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


runBlocking {
    repeat(10){
        delay(100L)
        val job = launch {
            while(true){
                delay(25L)
                print(".")
            }
        }

        launch {
            delay(100L)
            print("!")
            job.cancel()
        }
    }
}
