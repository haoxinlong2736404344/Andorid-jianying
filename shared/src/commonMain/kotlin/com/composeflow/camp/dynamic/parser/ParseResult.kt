package com.composeflow.camp.dynamic.parser

data class ParseError(
    val path: String,
    val message: String,
)

sealed interface ParseResult<out T> {
    data class Success<T>(val value: T) : ParseResult<T>
    data class Failure(val errors: List<ParseError>) : ParseResult<Nothing>
}
