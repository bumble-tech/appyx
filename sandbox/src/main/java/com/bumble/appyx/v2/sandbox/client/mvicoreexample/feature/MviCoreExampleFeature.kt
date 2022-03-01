package com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Effect
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.News
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish.ChangeState
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish.ChildInput
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish.Finish
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish.LoadData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MviCoreExampleFeature(initialStateName: String) :
    ActorReducerFeature<Wish, Effect, State, News>(
        initialState = State.InitialState(initialStateName),
        actor = ActorImpl,
        reducer = ReducerImpl,
        newsPublisher = NewsPublisherImpl
    ) {

    sealed class State {
        data class InitialState(val stateName: String) : State()
        object Loading : State()
        data class Loaded(val stateName: String) : State()
        object Finished : State()
    }

    sealed class Wish {
        data class ChildInput(val data: String) : Wish()
        object LoadData : Wish()
        object Finish : Wish()
        data class ChangeState(val stateName: String) : Wish()
    }

    sealed class Effect {
        data class ChangeState(val stateName: String) : Effect()
        data class ChildInput(val data: String) : Effect()
        object Loading : Effect()
        object Finished : Effect()
        object DataLoaded : Effect()
    }

    sealed class News {
        object Finished : News()
        data class StateUpdated(val message: String) : News()
    }

    private object ActorImpl : Actor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<Effect> =
            when (wish) {
                is ChangeState -> Effect.ChangeState(wish.stateName).toObservable()
                is Finish -> Effect.Finished.toObservable()
                is ChildInput -> Effect.ChildInput(wish.data).toObservable()
                is LoadData -> Observable.timer(2, TimeUnit.SECONDS)
                    .map<Effect> { Effect.DataLoaded }
                    .startWith(Effect.Loading)
                    .observeOn(AndroidSchedulers.mainThread())
            }
    }

    private object ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State =
            when (effect) {
                is Effect.ChangeState -> State.Loaded(effect.stateName)
                is Effect.Finished -> State.Finished
                is Effect.ChildInput -> State.Loaded(effect.data)
                is Effect.DataLoaded -> State.Loaded("Loaded")
                is Effect.Loading -> State.Loading
            }
    }

    private object NewsPublisherImpl : NewsPublisher<Wish, Effect, State, News> {
        override fun invoke(action: Wish, effect: Effect, state: State): News? =
            when (effect) {
                is Effect.ChangeState -> News.StateUpdated(effect.stateName)
                is Effect.Finished -> News.Finished
                else -> null
            }
    }
}


fun <T> T?.toObservable(): Observable<T> =
    if (this == null) Observable.empty() else Observable.just(this)
