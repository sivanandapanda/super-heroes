package org.playground.superheroes.heroes.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

@Entity
class Hero {
    @Id
    @GeneratedValue
    var id: Long? = null

    @NotNull
    @Positive
    var level: Int? = null

    @NotNull
    @Size(min = 3, max = 50)
    var name: String? = null

    var otherName: String? = null

    var picture: String? = null

    @Column(columnDefinition = "TEXT")
    var powers: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hero

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}