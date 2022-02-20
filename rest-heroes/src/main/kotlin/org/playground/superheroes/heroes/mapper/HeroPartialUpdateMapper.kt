package org.playground.superheroes.heroes.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.playground.superheroes.heroes.model.Hero
import org.mapstruct.NullValuePropertyMappingStrategy.IGNORE

@Mapper(componentModel = "CDI", nullValuePropertyMappingStrategy = IGNORE)
interface HeroPartialUpdateMapper {
    /**
     * Maps all `**non-null**` fields from `input` onto `target`.
     * @param input The input [Hero]
     * @param target The target [Hero]
     */
    fun mapPartialUpdate(input: Hero?, @MappingTarget target: Hero?)
}