package org.playground.superheroes.heroes.service

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import org.playground.superheroes.heroes.mapper.HeroFullUpdateMapper
import org.playground.superheroes.heroes.mapper.HeroPartialUpdateMapper
import org.playground.superheroes.heroes.model.Hero
import org.playground.superheroes.heroes.repository.HeroRepository
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.Validator

@ApplicationScoped
class HeroService {

    @Inject
    lateinit var heroRepository: HeroRepository

    @Inject
    lateinit var validator: Validator

    /*@Inject
    lateinit var heroPartialUpdateMapper: HeroPartialUpdateMapper

    @Inject
    lateinit var heroFullUpdateMapper: HeroFullUpdateMapper*/

    fun findAllHeroes(): Uni<MutableList<Hero>>? {
        return heroRepository.listAll()
    }

    fun findHeroById(id: Long): Uni<Hero>? {
        return heroRepository.findById(id)
    }

    fun findRandomHero(): Uni<Hero>? {
        return heroRepository.findRandom()
    }

    fun persistHero(@NotNull hero: Hero): Uni<Hero>? {
        return heroRepository.persist(hero)
    }

    @ReactiveTransactional
    fun replaceHero(@NotNull @Valid hero: Hero) : Uni<Hero>? {
        return heroRepository.findById(hero.id)
            .onItem().ifNotNull().transform { found: Hero ->
                //heroFullUpdateMapper.mapFullUpdate(hero, found)
                found
            }
    }

    @ReactiveTransactional
    fun partialUpdateHero(@NotNull hero: Hero) : Uni<Hero>? {
        return heroRepository.findById(hero.id)
            .onItem().ifNotNull().transform { found: Hero ->
                //heroPartialUpdateMapper.mapPartialUpdate(hero, found)
                found
            }
            .onItem().ifNotNull().transform { validatePartialUpdate(it) }
    }

    private fun validatePartialUpdate(hero: Hero) : Hero {
        val violations: MutableSet<ConstraintViolation<Hero>> = validator.validate(hero)

        if(violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }

        return hero
    }

    @ReactiveTransactional
    fun deleteAllHeroes(): Uni<Void>? {
        return heroRepository.listAll()
            .onItem().transformToMulti { Multi.createFrom().iterable(it) }
            .map { it.id }
            .onItem().transformToUniAndMerge { deleteHero(it!!) }
            .collect().asList()
            .replaceWithVoid()
    }

    fun deleteHero(id: Long): Uni<Void>? {
        return heroRepository.deleteById(id).replaceWithVoid()
    }
}