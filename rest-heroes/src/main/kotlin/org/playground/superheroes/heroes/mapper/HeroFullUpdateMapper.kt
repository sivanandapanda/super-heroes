package org.playground.superheroes.heroes.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.playground.superheroes.heroes.model.Hero

@Mapper(componentModel = "cdi")
interface HeroFullUpdateMapper {

    /**
     * Maps all fields except <code>id</code> from {@code input} onto {@code target}.
     * @param input The input {@link Hero}
     * @param target The target {@link Hero}
     */
    fun mapFullUpdate(input: Hero, @MappingTarget target: Hero)
}