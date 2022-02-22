package org.playground.superheroes.heroes.rest

import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException
import io.smallrye.common.annotation.NonBlocking
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.logging.Logger
import org.playground.superheroes.heroes.model.Hero
import org.playground.superheroes.heroes.service.HeroService
import java.net.URI
import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.MediaType.TEXT_PLAIN
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.UriInfo


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
    @APIResponse(responseCode = "200", description = "Gets a random hero",
    content = [Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = Hero::class))])
    @APIResponse(responseCode = "404", description = "No hero found")
    suspend fun getRandomHero(): Response {
        return heroService.findRandomHero()!!
            .onItem().ifNotNull().transform {
                logger.debugf("Found random hero: %s", it)
                Response.ok(it).build()
            }
            .onItem().ifNull().continueWith {
                logger.debug("No random hero found")
                Response.status(Status.NOT_FOUND).build()
            }.awaitSuspending()
    }

    @GET
    @Operation(summary = "Returns a random hero")
    @APIResponse(responseCode = "200", description = "Gets a random hero",
    content = [Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = Hero::class))])
    fun getAllHeroes(): Uni<MutableList<Hero>> = heroService.findAllHeroes()!!
            .invoke { heroes: List<Hero?> -> logger.debugf("Total number of heroes: %d",heroes.size)}

    @GET
    @Path("/{id}")
    @Operation(summary = "Returns a hero for a given identifier")
    @APIResponse(
        responseCode = "200",
        description = "Gets a hero for a given id",
        content = [Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = Hero::class))]
    )
    @APIResponse(responseCode = "404", description = "The hero is not found for a given identifier")
    suspend fun getHero(@Parameter(name = "id", required = true) @PathParam("id") id: Long?): Response {
        return heroService.findHeroById(id!!)!!
            .onItem().ifNotNull().transform { h: Hero? ->
                logger.debugf("Found hero: %s", h)
                Response.ok(h).build()
            }
            .onItem().ifNull().continueWith {
                logger.debugf("No hero found with id %d", id)
                Response.status(Status.NOT_FOUND).build()
            }.awaitSuspending()
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Creates a valid hero")
    @APIResponse(
        responseCode = "201",
        description = "The URI of the created hero",
        content = [Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = URI::class))]
    )
    @APIResponse(responseCode = "400", description = "Invalid hero passed in (or no request body found)")
    suspend fun createHero(@Valid @NotNull hero: Hero?, @Context uriInfo: UriInfo): Response {
        return heroService.persistHero(hero!!)!!
            .map { h: Hero ->
                val builder = uriInfo.absolutePathBuilder.path((h.id!!).toString())
                logger.debugf("New Hero created with URI %s", builder.build().toString())
                Response.created(builder.build()).build()
            }.awaitSuspending()
    }

    @PUT
    @Path("/{id}")
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Completely updates/replaces an exiting hero by replacing it with the passed-in hero")
    @APIResponse(responseCode = "204", description = "Replaced the hero")
    @APIResponse(responseCode = "400", description = "Invalid hero passed in (or no request body found)")
    @APIResponse(responseCode = "404", description = "No hero found")
    suspend fun fullyUpdateHero(@Valid @NotNull hero: Hero): Response {
        return heroService.replaceHero(hero)!!
            .onItem().ifNotNull().transform { h: Hero? ->
                logger.debugf("Hero replaced with new values %s", h)
                Response.noContent().build()
            }
            .onItem().ifNull().continueWith {
                logger.debugf("No hero found with id %d", hero.id)
                Response.status(Status.NOT_FOUND).build()
            }.awaitSuspending()
    }

    @PATCH
    @Path("/{id}")
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Partially updates an exiting hero")
    @APIResponse(
        responseCode = "200",
        description = "Updated the hero",
        content = [Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = Hero::class))]
    )
    @APIResponse(responseCode = "400", description = "Null hero passed in (or no request body found)")
    @APIResponse(responseCode = "404", description = "No hero found")
    suspend fun partiallyUpdateHero(
        @Parameter(name = "id", required = true) @PathParam("id") id: Long?,
        @NotNull hero: Hero
    ): Uni<Response?>? {
        if (hero.id == null) {
            hero.id = id
        }
        return heroService.partialUpdateHero(hero)!!
            .onItem().ifNotNull().transform { h: Hero? ->
                logger.debugf("Hero updated with new values %s", h)
                Response.ok(h).build()
            }
            .onItem().ifNull().continueWith {
                logger.debugf("No hero found with id %d", hero.id)
                Response.status(Status.NOT_FOUND).build()
            }
            .onFailure(ConstraintViolationException::class.java)
            .transform {
                ResteasyReactiveViolationException(
                    (it as ConstraintViolationException).constraintViolations
                )
            }
    }

    @DELETE
    @Operation(summary = "Delete all heroes")
    @APIResponse(responseCode = "204", description = "Deletes all heroes")
    suspend fun deleteAllHeros(): Uni<Void?>? {
        return heroService.deleteAllHeroes()!!
            .invoke(Runnable { logger.debug("Deleted all heroes") })
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes an exiting hero")
    @APIResponse(responseCode = "204", description = "Deletes a hero")
    suspend fun deleteHero(@Parameter(name = "id", required = true) @PathParam("id") id: Long?): Uni<Void?>? {
        return heroService.deleteHero(id!!)!!
            .invoke(Runnable { logger.debugf("Hero deleted with %d", id) })
    }

    @GET
    @Path("/hello")
    @Produces(TEXT_PLAIN)
    @Tag(name = "hello")
    @Operation(summary = "Ping hello")
    @APIResponse(responseCode = "200", description = "Ping hello")
    @NonBlocking
    suspend fun hello(): String? {
        return "Hello Hero Resource"
    }
}