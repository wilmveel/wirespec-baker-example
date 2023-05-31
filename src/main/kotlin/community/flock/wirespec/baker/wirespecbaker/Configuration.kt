package community.flock.wirespec.baker.wirespecbaker

import com.fasterxml.jackson.databind.ObjectMapper
import com.ing.baker.compiler.RecipeCompiler
import com.ing.baker.il.CompiledRecipe
import com.ing.baker.recipe.javadsl.Interaction
import com.ing.baker.runtime.common.RecipeRecord
import com.ing.baker.runtime.kotlindsl.Baker
import com.ing.baker.runtime.kotlindsl.InMemoryBaker
import community.flock.wirespec.generated.Content
import community.flock.wirespec.generated.ContentMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

@Configuration
class Configuration {

    @Bean
    fun compiledRecipe(): CompiledRecipe {
        return RecipeCompiler
            .compileRecipe(recipe)
    }

    @Bean
    fun baker(implementations: List<Interaction>): Baker {
        return InMemoryBaker.kotlin(implementations)
    }

    @Bean
    fun contentMapper(objectMapper: ObjectMapper) =
        object : ContentMapper<String> {
            override fun <T> read(content: Content<String>, valueType: KType): Content<T> {
                val type = objectMapper.constructType(valueType.javaType)
                val body = objectMapper.readValue<T>(content.body, type)
                return Content(content.type, body)
            }

            override fun <T> write(content: Content<T>): Content<String> {
                TODO("Not yet implemented")
            }

        }

}