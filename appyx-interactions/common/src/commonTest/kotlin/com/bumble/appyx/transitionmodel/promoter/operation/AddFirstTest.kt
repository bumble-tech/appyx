package com.bumble.appyx.transitionmodel.promoter.operation

import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.transitionmodel.NavTarget
import com.bumble.appyx.transitionmodel.NavTarget.Child1
import com.bumble.appyx.transitionmodel.NavTarget.Child2
import com.bumble.appyx.transitionmodel.NavTarget.Child3
import com.bumble.appyx.transitionmodel.NavTarget.Child4
import com.bumble.appyx.transitionmodel.NavTarget.Child5
import com.bumble.appyx.transitionmodel.NavTarget.Child6
import com.bumble.appyx.transitionmodel.promoter.PromoterModel
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState.DESTROYED
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState.STAGE1
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState.STAGE2
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState.STAGE3
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState.STAGE4
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState.STAGE5
import kotlin.test.Test
import kotlin.test.assertEquals

class AddFirstTest {

    @Test
    fun `Given created When add first Then moves to stage1`() {
        val state = PromoterModel.State(
            elements = listOf<Pair<NavElement<NavTarget>, ElementState>>()
        )

        val addFirst = AddFirst(Child1)

        val firstElement = addFirst(state).targetState.elements[0]

        assertEquals(
            actual = firstElement.first.navTarget to firstElement.second,
            expected = Child1 to STAGE1
        )
    }

    @Test
    fun `Given stage1 When add first Then moves to stage2`() {
        val state = PromoterModel.State(
            elements = listOf(NavElement(Child1) to STAGE1)
        )

        val addFirst = AddFirst(Child2)

        val finalState = addFirst.invoke(state).targetState

        val firstElement = finalState.elements[0]
        val secondElement = finalState.elements[1]

        assertEquals(
            actual = firstElement.first.navTarget to firstElement.second,
            expected = Child2 to STAGE1
        )

        assertEquals(
            actual = secondElement.first.navTarget to secondElement.second,
            expected = Child1 to STAGE2
        )
    }

    @Test
    fun `Given stage2 When add first Then moves to stage3`() {
        val state = PromoterModel.State(
            elements = listOf(
                NavElement(Child2) to STAGE1,
                NavElement(Child1) to STAGE2
            )
        )

        val addFirst = AddFirst(Child3)

        val finalState = addFirst.invoke(state).targetState

        val firstElement = finalState.elements[0]
        val secondElement = finalState.elements[1]
        val thirdElement = finalState.elements[2]

        assertEquals(
            actual = firstElement.first.navTarget to firstElement.second,
            expected = Child3 to STAGE1
        )

        assertEquals(
            actual = secondElement.first.navTarget to secondElement.second,
            expected = Child2 to STAGE2
        )

        assertEquals(
            actual = thirdElement.first.navTarget to thirdElement.second,
            expected = Child1 to STAGE3
        )
    }

    @Test
    fun `Given stage3 When add first Then moves to stage4`() {
        val state = PromoterModel.State(
            elements = listOf(
                NavElement(Child3) to STAGE1,
                NavElement(Child2) to STAGE2,
                NavElement(Child1) to STAGE3
            )
        )

        val addFirst = AddFirst(Child4)

        val finalState = addFirst.invoke(state).targetState

        val firstElement = finalState.elements[0]
        val secondElement = finalState.elements[1]
        val thirdElement = finalState.elements[2]
        val fourthElement = finalState.elements[3]

        assertEquals(
            actual = firstElement.first.navTarget to firstElement.second,
            expected = Child4 to STAGE1
        )

        assertEquals(
            actual = secondElement.first.navTarget to secondElement.second,
            expected = Child3 to STAGE2
        )

        assertEquals(
            actual = thirdElement.first.navTarget to thirdElement.second,
            expected = Child2 to STAGE3
        )

        assertEquals(
            actual = fourthElement.first.navTarget to fourthElement.second,
            expected = Child1 to STAGE4
        )
    }

    @Test
    fun `Given stage4 When add first Then moves to stage5`() {
        val state = PromoterModel.State(
            elements = listOf(
                NavElement(Child4) to STAGE1,
                NavElement(Child3) to STAGE2,
                NavElement(Child2) to STAGE3,
                NavElement(Child1) to STAGE4
            )
        )

        val addFirst = AddFirst(Child5)

        val finalState = addFirst.invoke(state).targetState

        val firstElement = finalState.elements[0]
        val secondElement = finalState.elements[1]
        val thirdElement = finalState.elements[2]
        val fourthElement = finalState.elements[3]
        val fifthElement = finalState.elements[4]

        assertEquals(
            actual = firstElement.first.navTarget to firstElement.second,
            expected = Child5 to STAGE1
        )

        assertEquals(
            actual = secondElement.first.navTarget to secondElement.second,
            expected = Child4 to STAGE2
        )

        assertEquals(
            actual = thirdElement.first.navTarget to thirdElement.second,
            expected = Child3 to STAGE3
        )

        assertEquals(
            actual = fourthElement.first.navTarget to fourthElement.second,
            expected = Child2 to STAGE4
        )

        assertEquals(
            actual = fifthElement.first.navTarget to fifthElement.second,
            expected = Child1 to STAGE5
        )
    }

    @Test
    fun `Given stage5 When add first Then moves to destroyed`() {
        val state = PromoterModel.State(
            elements = listOf(
                NavElement(Child5) to STAGE1,
                NavElement(Child4) to STAGE2,
                NavElement(Child3) to STAGE3,
                NavElement(Child2) to STAGE4,
                NavElement(Child1) to STAGE5
            )
        )

        val addFirst = AddFirst(Child6)

        val finalState = addFirst.invoke(state).targetState

        val firstElement = finalState.elements[0]
        val secondElement = finalState.elements[1]
        val thirdElement = finalState.elements[2]
        val fourthElement = finalState.elements[3]
        val fifthElement = finalState.elements[4]
        val sixthElement = finalState.elements[5]

        assertEquals(
            actual = firstElement.first.navTarget to firstElement.second,
            expected = Child6 to STAGE1
        )

        assertEquals(
            actual = secondElement.first.navTarget to secondElement.second,
            expected = Child5 to STAGE2
        )

        assertEquals(
            actual = thirdElement.first.navTarget to thirdElement.second,
            expected = Child4 to STAGE3
        )

        assertEquals(
            actual = fourthElement.first.navTarget to fourthElement.second,
            expected = Child3 to STAGE4
        )

        assertEquals(
            actual = fifthElement.first.navTarget to fifthElement.second,
            expected = Child2 to STAGE5
        )

        assertEquals(
            actual = sixthElement.first.navTarget to sixthElement.second,
            expected = Child1 to DESTROYED
        )
    }
}
