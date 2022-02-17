package org.playground.superheroes.heroes.repository

import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.quarkus.panache.common.Page
import io.smallrye.mutiny.Uni
import org.playground.superheroes.heroes.model.Hero
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class HeroRepository : PanacheRepository<Hero> {

    fun findRandom(): Uni<Hero>? {
        return count()
            .map { count: Long -> if (count > 0) count else null }
            .onItem().ifNotNull().transform { count: Long? -> Random().nextInt(count!!.toInt()) }
            .onItem().ifNotNull().transformToUni { randomHero -> findAll().page<Hero>( Page.of(randomHero, 1)).firstResult() }
    }

}