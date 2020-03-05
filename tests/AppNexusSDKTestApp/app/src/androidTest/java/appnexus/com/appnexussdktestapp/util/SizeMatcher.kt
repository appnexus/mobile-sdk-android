package appnexus.com.appnexussdktestapp.util

import android.view.View
import org.hamcrest.TypeSafeMatcher


class SizeMatcher(private val expectedWith: Int, private val expectedHeight: Int) :
    TypeSafeMatcher<View?>(View::class.java) {

    override fun matchesSafely(target: View?): Boolean {
        val targetWidth: Int = target!!.getWidth()
        val targetHeight: Int = target!!.getHeight()
        return targetWidth >= expectedWith - 2 && targetWidth <= expectedWith + 2 && targetHeight >= expectedHeight - 2 && targetHeight <= expectedHeight + 2
    }

    override fun describeTo(description: org.hamcrest.Description?) {
        description?.appendText("with SizeMatcher: ")
        description?.appendValue(expectedWith.toString() + "x" + expectedHeight)
    }

}