package appnexus.com.appnexussdktestapp.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import org.junit.Assert

class Utility {
    companion object {
        fun checkVisibilityDetectorMap(checkZero: Int, context: Context) {


            Handler(Looper.getMainLooper()).post({
                val vDetector = Class.forName("com.appnexus.opensdk.VisibilityDetector")
                val getInstance = vDetector.getDeclaredMethod("getInstance")
                getInstance.isAccessible = true
                val viewReference = View(context)
                val vDetInst = getInstance.invoke(null)

                val addVisibilityListener = vDetector.getDeclaredMethod("addVisibilityListener", View::class.java)
                addVisibilityListener.isAccessible = true
                addVisibilityListener.invoke(vDetInst, viewReference)

                val destroy = vDetector.getDeclaredMethod("destroy", View::class.java)
                destroy.isAccessible = true
                destroy.invoke(vDetInst, viewReference)

                val declaredField = vDetector.getDeclaredField("viewList")
                declaredField.isAccessible = true
                var list = declaredField.get(vDetInst) as List<Object>
                Log.e("VISIBILITY", " Size: ${checkZero} ${list.size}");

                Assert.assertEquals(checkZero, list.size)

            })
        }
    }
}