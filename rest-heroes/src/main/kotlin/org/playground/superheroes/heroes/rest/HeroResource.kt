package org.playground.superheroes.heroes.rest

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.playground.superheroes.heroes.model.Hero
import org.playground.superheroes.heroes.service.HeroService
import java.util.function.Consumer
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response

@Path("/api/heroes")
@Tag(name = "heroes")
@Produces(APPLICATION_JSON)
class HeroResource {

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var heroService: HeroService

    @GET
    @Path("/random")
    @Operation(summary = "Returns a random hero")
    @APIResponse(responseCode = "200", description = "Gets a random hero"/*,
    content = [Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = Hero.class))]*/)
    @APIResponse(responseCode = "404", description = "No hero found")
    fun getRandomHero(): Uni<Response>? {
        val randomHero : Uni<Hero>? = heroService.findRandomHero()

        return if(randomHero !== null) {
            logger.debugf("Found random hero: %s", randomHero)
            randomHero.onItem().ifNotNull().transform { Response.ok(it).build() }
        } else {
            logger.debug("No random hero found");
            Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND).build())
        }
    }

    @GET
    @Operation(summary = "Returns a random hero")
    @APIResponse(responseCode = "200", description = "Gets a random hero"/*,
    content = [Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = Hero.class))]*/)
    fun getAllHeroes(): Uni<MutableList<Hero>>? {
        return heroService.findAllHeroes()!!
            .invoke { heroes: List<Hero?> -> logger.debugf("Total number of heroes: %d",heroes.size)}
    }

}