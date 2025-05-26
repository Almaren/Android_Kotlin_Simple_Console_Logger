/**
 * Provides an effective mechanism for handling logs.
 *
 * @author https://github.com/AlDevWizard
 * @version 1.0.1, 29/06/2021.
 * @since 1.0.0
 */
object Log {

    const val TAG = "ads"   // TODO change default tag for logging
    var DEBUG = true        // TODO disable in release
    private val lockHeader = ReentrantLock(true)

    fun getHeader(): String {
        if (!lockHeader.tryLock(50, TimeUnit.MILLISECONDS)) {
            return "[]: "
        }

        try {
            val currentClassName = Log.javaClass.name
            val traces = Thread.currentThread().stackTrace
            var found = false

            for (trace in traces) {
                try {
                    if (found) {
                        if (!trace.className.startsWith(currentClassName)) {
                            val targetClassName = Class.forName(trace.className)
                            return "[${getClassName(targetClassName)}.${trace.methodName}.${trace.lineNumber}]: "
                        }
                    }
                    else if (trace.className.startsWith(currentClassName)) {
                        found = true
                        continue
                    }
                } catch (e: ClassNotFoundException) {
                } catch (e2: IncompatibleClassChangeError) {
                }
            }
        } catch (eThread: InterruptedException) {
            e(eThread)
        } finally {
            lockHeader.unlock()
        }
        return "[]: "
    }

    private fun getClassName(clazz: Class<*>?): String {
        if (clazz != null) {
            if (!clazz.simpleName.isNullOrEmpty()) {
                return clazz.simpleName
            } else {
                return getClassName(clazz.enclosingClass)
            }
        } else {
            return ""
        }
    }

    inline fun d(tag: String = TAG, msg: () -> String) {
        if (DEBUG) android.util.Log.d(tag, getHeader() + msg())
    }

    inline fun i(tag: String = TAG, msg: () -> String) {
        if (DEBUG) android.util.Log.i(tag, getHeader() + msg())
    }

    inline fun v(tag: String = TAG, msg: () -> String) {
        if (DEBUG) android.util.Log.v(tag, getHeader() + msg())
    }

    inline fun w(tag: String = TAG, msg: () -> String) {
        if (DEBUG) android.util.Log.w(tag, getHeader() + msg())
    }

    inline fun e(tag: String = TAG, cause: Throwable, noinline msg: (() -> String)? = null) {
        if (DEBUG) {
            android.util.Log.e(tag, msg?.let { it.invoke() } ?: "", cause)
        }
    }

    inline fun e(cause: Throwable) {
        if (DEBUG) android.util.Log.e(TAG, "", cause)
    }

}
