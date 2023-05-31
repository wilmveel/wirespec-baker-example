@file:OptIn(ExperimentalStdlibApi::class)

package community.flock.wirespec.generated

import kotlin.reflect.KType
import kotlin.reflect.typeOf

enum class Method { GET, PUT, POST, DELETE, OPTIONS, HEAD, PATCH, TRACE }
data class Content<T>(val type: String, val body: T)
interface Request<T> {
    val url: String;
    val method: Method;
    val query: Map<String, String>;
    val headers: Map<String, List<String>>;
    val content: Content<T>?
}

interface Response<T> {
    val status: Int;
    val headers: Map<String, List<String>>;
    val content: Content<T>?
}

interface ContentMapper<B> {
    fun <T> read(content: Content<B>, valueType: KType): Content<T>
    fun <T> write(content: Content<T>): Content<B>
}

interface GetIngredients {
    sealed interface GetIngredientsRequest<T> : Request<T>
    class GetIngredientsRequestUnit(pizzaId: String) : GetIngredientsRequest<Unit> {
        override val url = "/pizzas/${pizzaId}/ingredients";
        override val method = Method.GET;
        override val query = mapOf<String, String>();
        override val headers = mapOf<String, List<String>>();
        override val content = null
    }

    sealed interface GetIngredientsResponse<T> : Response<T>
    sealed interface GetIngredientsResponse200<T> : GetIngredientsResponse<T>
    sealed interface GetIngredientsResponse404<T> : GetIngredientsResponse<T>
    class GetIngredientsResponse200ApplicationJson(
        override val headers: Map<String, List<String>>,
        body: List<Ingredient>
    ) : GetIngredientsResponse200<List<Ingredient>> {
        override val status = 200;
        override val content = Content("application/json", body)
    }

    class GetIngredientsResponse404Unit(override val headers: Map<String, List<String>>) :
        GetIngredientsResponse404<Unit> {
        override val status = 404;
        override val content = null
    }

    suspend fun getIngredients(request: GetIngredientsRequest<out Any>): GetIngredientsResponse<out Any>

    companion object {
        const val PATH = "/pizzas/{pizzaId}/ingredients"
        fun <B> RESPONSE_MAPPER(contentMapper: ContentMapper<B>) =
            fun(status: Int, headers: Map<String, List<String>>, content: Content<B>?) =
                when {
                    status == 200 && content?.type == "application/json" -> contentMapper
                        .read<List<Ingredient>>(content, typeOf<List<Ingredient>>())
                        .let { GetIngredientsResponse200ApplicationJson(headers, it.body) }

                    status == 404 && content == null -> GetIngredientsResponse404Unit(headers)

                    else -> error("Cannot map response with status $status")
                }
    }
}

data class Ingredient(
    val id: String,
    val name: String,
    val quantity: String
)
