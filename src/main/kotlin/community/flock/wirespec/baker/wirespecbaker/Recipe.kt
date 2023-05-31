package community.flock.wirespec.baker.wirespecbaker

import com.ing.baker.compiler.RecipeCompiler
import com.ing.baker.recipe.javadsl.Interaction
import com.ing.baker.recipe.kotlindsl.Recipe
import com.ing.baker.recipe.kotlindsl.recipe
import community.flock.wirespec.generated.GetIngredients
import community.flock.wirespec.generated.Ingredient

data class PizzaOrder(
    val pizzaId: String
)

interface PizzaBaker : Interaction {
    data class PizzaProcess(val getIngredientsRequest: GetIngredients.GetIngredientsRequestUnit)

    fun apply(pizzaId: String): PizzaProcess
}

interface PizzaIngredientApi : Interaction {
    fun apply(getIngredientsRequest: GetIngredients.GetIngredientsRequestUnit): GetIngredients.GetIngredientsResponse<*>
}

interface PizzaDelivery : Interaction {
    object PizzaPizzaSuccess

    fun apply(pizzaId: String, body: List<Ingredient>): PizzaPizzaSuccess
}

val recipe: Recipe = recipe("RecipeWithCheckpointEvent") {
    sensoryEvents {
        event<PizzaOrder>()
    }
    interaction<PizzaBaker>()
    interaction<PizzaIngredientApi>()
    interaction<PizzaDelivery> {
        requiredEvents {
            event<GetIngredients.GetIngredientsResponse200ApplicationJson>()
        }
    }
}

fun main() {

    val recipe = RecipeCompiler.compileRecipe(recipe)
    val visualization = recipe.recipeVisualization

    println(visualization)
}
