package com.bumble.appyx.sandbox.stub

import com.badoo.mvicore.feature.Feature
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer

open class FeatureStub<Wish : Any, State : Any, News : Any>(
    initialState: State,
    val wishesRelay: PublishRelay<Wish> = PublishRelay.create(),
    val statesRelay: BehaviorRelay<State> = BehaviorRelay.createDefault(initialState),
    val newsRelay: PublishRelay<News> = PublishRelay.create(),
    private val disposable: Disposable = Disposables.empty()
) : Feature<Wish, State, News>,
    ObservableSource<State> by statesRelay,
    Consumer<Wish> by wishesRelay,
    Disposable by disposable {

    private val testWishes = wishesRelay.test()

    override val state: State
        get() = statesRelay.value!!

    override val news: ObservableSource<News>
        get() = newsRelay
}
