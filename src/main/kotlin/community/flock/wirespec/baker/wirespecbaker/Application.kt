package community.flock.wirespec.baker.wirespecbaker

import com.ing.baker.il.CompiledRecipe
import com.ing.baker.runtime.common.RecipeRecord
import com.ing.baker.runtime.javadsl.EventInstance
import com.ing.baker.runtime.kotlindsl.Baker
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.util.*


@SpringBootApplication
class WirespecBakerApplication

fun main(args: Array<String>) {
    runApplication<WirespecBakerApplication>(*args)
}


@Component
class Run(private val baker: Baker, private val compiledRecipe: CompiledRecipe) {

    init {
        runBlocking {
            val visualization: String = compiledRecipe.recipeVisualization
            println(visualization)
            val instanceId = UUID.randomUUID().toString()
            val record = RecipeRecord.of(compiledRecipe, System.currentTimeMillis(), true)
            val id = baker.addRecipe(record)
            baker.bake(id, instanceId)
            val event = EventInstance.from(PizzaOrder("MyPizza"))
            baker.fireEventAndResolveWhenCompleted(instanceId, event)
        }
    }
}
