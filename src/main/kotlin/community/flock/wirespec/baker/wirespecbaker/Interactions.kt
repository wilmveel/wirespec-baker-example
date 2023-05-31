package community.flock.wirespec.baker.wirespecbaker

import community.flock.wirespec.generated.ContentMapper
import community.flock.wirespec.generated.GetIngredients
import community.flock.wirespec.generated.Ingredient
import org.springframework.stereotype.Component

@Component
class PizzaBakerImpl : PizzaBaker {
    override fun apply(pizzaId: String): PizzaBaker.PizzaProcess {
        val request = GetIngredients.GetIngredientsRequestUnit(pizzaId)
        return PizzaBaker.PizzaProcess(request)
    }

}

@Component
class PizzaIngredientApiImpl(private val contentMapper: ContentMapper<String>) : PizzaIngredientApi {

    val body = """
        [
            { "id": "1", "name": "kaas", "quantity": "200 gram" },
            { "id": "2", "name": "tomaat", "quantity": "100 gram" },
            { "id": "3", "name": "salami", "quantity": "7 plakjes" }
        ]
    """.trimIndent()

    val mapper = GetIngredients.RESPONSE_MAPPER(contentMapper)

    override fun apply(getIngredientsRequest: GetIngredients.GetIngredientsRequestUnit): GetIngredients.GetIngredientsResponse<*> {
//        return mapper(200, emptyMap(), Content("application/json", body))
        return mapper(404, emptyMap(), null)
    }
}

@Component
class PizzaDeliveryImpl : PizzaDelivery {
    override fun apply(pizzaId: String, body: List<Ingredient>): PizzaDelivery.PizzaSuccess {
        println(
            """
                |---------------------------------------------
                |Voor een pizza met id '$pizzaId' zijn de volgende ingredienten nodig:
                |${body.joinToString("\n") { "\t${it.quantity} ${it.name}" }}
                |---------------------------------------------
            """.trimMargin()
        )

        return PizzaDelivery.PizzaSuccess
    }
}

@Component
class PizzaBinImpl : PizzaBin {
    override fun apply(): PizzaBin.PizzaError {
        println("""
            |---------------------------------------------
            |No Pizza for you!
            |---------------------------------------------
        """.trimMargin())
        return PizzaBin.PizzaError
    }

}