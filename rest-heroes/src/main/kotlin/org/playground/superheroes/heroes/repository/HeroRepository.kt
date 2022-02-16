package org.playground.superheroes.heroes.repository

import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.quarkus.panache.common.Page
import io.smallrye.mutiny.Uni
import org.playground.superheroes.heroes.model.Hero
import java.util.*
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class HeroRepository : PanacheRepository<Hero> {

    /*fun findRandom() =
        count()
            .map { if (it > 0) it else null }
            .onItem().ifNotNull().transform { it?.let { it1 -> Random().nextInt(it1.toInt()) } }
            .onItem().ifNotNull()
            .transformToUni { it?.let { it1 -> findAll().page<Hero>(Page.of(it1, 1)).firstResult<Hero>() } }*/

    fun findRandom(): Uni<Hero>? {
        return count()
            .map { count: Long -> if (count > 0) count else null }
            .onItem().ifNotNull().transform { count: Long? -> Random().nextInt(count!!.toInt()) }
            .onItem().ifNotNull().transformToUni { randomHero ->
                findAll().page<Hero>( Page.of(randomHero, 1)).firstResult()
            }
    }
}