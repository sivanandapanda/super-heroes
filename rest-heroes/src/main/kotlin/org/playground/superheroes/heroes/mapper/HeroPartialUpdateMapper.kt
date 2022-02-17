package org.playground.superheroes.heroes.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy.IGNORE
import org.playground.superheroes.heroes.model.Hero

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = IGNORE)
interface HeroPartialUpdateMapper {

    /**
     * Maps all <code><strong>non-null</strong></code> fields from {@code input} onto {@code target}.
     * @param input The input {@link Hero}
     * @param target The target {@link Hero}
     */
    fun mapPartialUpdate(input: Hero, @MappingTarget target: Hero)
}