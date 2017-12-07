package nl.breun.datagenerator.random

internal fun <T> Iterable<T>.random(): T? = shuffled().firstOrNull()

internal fun <T> Array<T>.random(): T? = toList().shuffled().firstOrNull()