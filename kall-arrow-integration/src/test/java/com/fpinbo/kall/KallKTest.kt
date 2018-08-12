package com.fpinbo.kall

import arrow.typeclasses.binding
import com.fpinbo.kall.api.jokes.JokesAPI
import com.fpinbo.kall.api.jokes.Response
import com.fpinbo.kall.api.randomuser.Name
import com.fpinbo.kall.api.randomuser.RandomUserAPI
import com.fpinbo.kall.response.fold
import org.junit.Test

class KallKTest {

    private val jokesApi = JokesAPI()
    private val randomUserApi = RandomUserAPI()


    @Test
    fun getTwoRandomJokes() {

        val jokesWithRandomUsers = with(KallK.monad()) {
            binding {
                val names = getTwoUsersCall().bind()
                getJokesCall(names).bind()
            }
        }.fix()

        val response = jokesWithRandomUsers.kall.execute()
        val message = response.fold({ "Error" }, { it.body.toString() })
        println(message)
    }

    private fun getTwoUsersCall(): KallK<List<Name>> {
        val firstUserCall = randomUserApi.getUser().k()
        val secondUserCall = randomUserApi.getUser().k()


        return with(KallK.applicative()) {
            map(firstUserCall, secondUserCall) {
                listOf(it.a.results.first().name, it.b.results.first().name)
            }
        }.fix()
    }


    private fun getJokesCall(names: List<Name>): KallK<List<String>> {
        val firstCall = getJokeCall(names[0])
        val secondCall = getJokeCall(names[1])

        return with(KallK.applicative()) {
            map(firstCall, secondCall) {
                listOf(it.a.value.joke, it.b.value.joke)
            }
        }.fix()
    }

    private fun getJokeCall(name: Name): KallK<Response> {
        return jokesApi.getJoke(name.first, name.last).k()
    }
}