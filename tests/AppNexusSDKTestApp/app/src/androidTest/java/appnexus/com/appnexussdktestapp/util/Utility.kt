package appnexus.com.appnexussdktestapp.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import java.lang.ref.WeakReference

class Utility {
    companion object {
        fun checkVisibilityDetectorMap(checkZero: Int, context: Context) {


            Handler(Looper.getMainLooper()).post({
                val vDetector = Class.forName("com.appnexus.opensdk.VisibilityDetector")
                val create = vDetector.getDeclaredMethod("create", WeakReference::class.java)
                create.isAccessible = true
                val weakReference = WeakReference(View(context))
                val vDetInst = create.invoke(null, weakReference)

                val destroy = vDetector.getDeclaredMethod("destroy", WeakReference::class.java)
                destroy.isAccessible = true
                destroy.invoke(vDetInst, weakReference)

                val declaredField = vDetector.getDeclaredField("viewListenerMap")
                declaredField.isAccessible = true
                var map = declaredField.get(vDetInst) as HashMap<Object, Object>
                Log.e("VISIBILITY", " Size: ${checkZero} ${map.size}");

                junit.framework.Assert.assertTrue(map.size == checkZero)

            })
        }
    }
}