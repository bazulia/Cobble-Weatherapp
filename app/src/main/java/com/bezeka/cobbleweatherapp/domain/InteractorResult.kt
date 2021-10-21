package com.bezeka.cobbleweatherapp.domain

sealed class InteractorResult <out RESULT> {
    abstract val data: RESULT?

    class Success<RESULT>(override val data: RESULT) : InteractorResult<RESULT>()
    class Error<RESULT>(override val data: RESULT?, val exception: Exception) : InteractorResult<RESULT>()

}