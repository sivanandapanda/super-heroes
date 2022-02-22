package org.playground.superheroes.heroes.repository

import io.quarkus.test.TestReactiveTransaction
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.vertx.UniAsserter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
@TestReactiveTransaction
internal class HeroRepositoryTest {

    @Inject
    lateinit var heroRepository: HeroRepository;

    @Test
    fun findRandomNotFound(asserter: UniAsserter) {
        asserter.execute(this.heroRepository::deleteAll)
            .assertEquals(this.heroRepository::count, 0L)
            .assertThat(this.heroRepository::findRandom) { hero -> assertThat(hero).isNull() }
    }

}