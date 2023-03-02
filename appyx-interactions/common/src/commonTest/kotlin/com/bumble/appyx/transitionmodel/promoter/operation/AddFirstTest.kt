package com.bumble.appyx.transitionmodel.promoter.operation

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.NavTarget
import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.NavTarget.Child3
import com.bumble.appyx.NavTarget.Child4
import com.bumble.appyx.NavTarget.Child5
import com.bumble.appyx.NavTarget.Child6
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
    fun GIVEN_created_WHEN_add_first_THEN_moves_to_stage1() {
        val state = PromoterModel.State(
            elements = listOf<Pair<Element<NavTarget>, ElementState>>()
        )

        val addFirst = AddFirst(Child1)

        val firstElement = addFirst(state).targetState.elements[0]

        assertEquals(
            actual = firstElement.first.navTarget to firstElement.second,
            expected = Child1 to STAGE1
        )
    }

    @Test
    fun GIVEN_stage1_WHEN_add_first_THEN_moves_to_stage2() {
        val state = PromoterModel.State(
            elements = listOf(Element(Child1) to STAGE1)
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
    fun GIVEN_stage2_WHEN_add_first_THEN_moves_to_stage3() {
        val state = PromoterModel.State(
            elements = listOf(
                Element(Child2) to STAGE1,
                Element(Child1) to STAGE2
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
    fun GIVEN_stage3_WHEN_add_first_THEN_moves_to_stage4() {
        val state = PromoterModel.State(
            elements = listOf(
                Element(Child3) to STAGE1,
                Element(Child2) to STAGE2,
                Element(Child1) to STAGE3
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
    fun GIVEN_stage4_WHEN_add_first_THEN_moves_to_stage5() {
        val state = PromoterModel.State(
            elements = listOf(
                Element(Child4) to STAGE1,
                Element(Child3) to STAGE2,
                Element(Child2) to STAGE3,
                Element(Child1) to STAGE4
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
    fun GIVEN_stage5_WHEN_add_first_THEN_moves_to_destroyed() {
        val state = PromoterModel.State(
            elements = listOf(
                Element(Child5) to STAGE1,
                Element(Child4) to STAGE2,
                Element(Child3) to STAGE3,
                Element(Child2) to STAGE4,
                Element(Child1) to STAGE5
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
