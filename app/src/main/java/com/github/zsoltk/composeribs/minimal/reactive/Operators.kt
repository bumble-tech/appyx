package com.github.zsoltk.composeribs.minimal.reactive

fun <T> just(producer: () -> T): Source<T> =
    object : Source<T> {
        override fun observe(callback: (T) -> Unit): Cancellable {
            callback(producer())
            return Cancellable.Empty
        }
    }

@Suppress("UNCHECKED_CAST")
fun <A : Any, B : Any, C> combineLatest(source1: Source<A>, source2: Source<B>, combination: (A, B) -> C): Source<C> =
    combineLatest(listOf(source1, source2)) { array -> combination(array[0] as A, array[1] as B) }

fun <C> combineLatest(sources: Collection<Source<Any>>, combination: (Array<Any?>) -> C): Source<C> =
    object : Source<C> {
        /**
         * The internal state (first / second values) should be recreated for each new call to observer
         * Otherwise new subscriptions will not start from empty state but will share values, which is not desirable
         */
        override fun observe(callback: (C) -> Unit): Cancellable {
            if (sources.isEmpty()) return Cancellable.Empty

            val initialized = Array(sources.size) { false }
            var allInitialized = false
            val values = Array<Any?>(sources.size) { null }

            fun emitCombined() {
                if (!allInitialized && initialized.all { it }) {
                    allInitialized = true
                }
                if (allInitialized) {
                    callback(combination(values))
                }
            }

            val cancellables = sources.mapIndexed { index, source ->
                source.observe { value ->
                    initialized[index] = true
                    values[index] = value
                    emitCombined()
                }
            }

            return Cancellable.cancellableOf {
                cancellables.forEach { it.cancel() }
            }
        }
    }

fun <T, R> Source<T>.map(transform: (T) -> R): Source<R> =
    object : Source<R> {
        override fun observe(callback: (R) -> Unit): Cancellable =
            this@map.observe { callback(transform(it)) }
    }

fun <T> Source<T>.filter(predicate: (T) -> Boolean): Source<T> =
    object : Source<T> {
        override fun observe(callback: (T) -> Unit): Cancellable =
            this@filter.observe {
                if (predicate(it)) callback(it)
            }
    }

fun <T> Source<T>.startWith(value: T): Source<T> =
    object : Source<T> {
        override fun observe(callback: (T) -> Unit): Cancellable {
            callback(value)
            return this@startWith.observe(callback)
        }
    }

fun <T> defer(factory: () -> Source<T>): Source<T> =
    object : Source<T> {
        override fun observe(callback: (T) -> Unit): Cancellable =
            factory().observe(callback)
    }

fun <T> Source<T>.distinctUntilChanged(): Source<T> =
    object : Source<T> {
        override fun observe(callback: (T) -> Unit): Cancellable {
            var hasLastValue = false
            var lastValue: T? = null
            return this@distinctUntilChanged.observe {
                if (!hasLastValue || lastValue != it) {
                    hasLastValue = true
                    lastValue = it
                    callback(it)
                }
            }
        }
    }

fun <T> Source<T>.onNext(onNext: (T) -> Unit): Source<T> =
    object : Source<T> {
        override fun observe(callback: (T) -> Unit): Cancellable =
            this@onNext.observe {
                onNext(it)
                callback(it)
            }
    }
