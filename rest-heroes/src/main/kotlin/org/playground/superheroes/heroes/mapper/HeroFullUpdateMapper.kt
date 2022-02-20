package org.playground.superheroes.heroes.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.playground.superheroes.heroes.model.Hero

@Mapper(componentModel = "CDI")
interface HeroFullUpdateMapper {
    /**
     * Maps all fields except `id` from `input` onto `target`.
     * @param input The input [Hero]
     * @param target The target [Hero]
     */
    @Mapping(target = "id", ignore = true)
    fun mapFullUpdate(input: Hero?, @MappingTarget target: Hero?)
}