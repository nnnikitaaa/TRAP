package com.nnnikitaaa.trap.jokes

import retrofit2.Call
import retrofit2.http.GET

interface JokeApiService {
    @GET("joke/Any?blacklistFlags=nsfw,religious,political,racist,sexist,explicit&type=twopart")
    fun getJoke(): Call<Joke>
}