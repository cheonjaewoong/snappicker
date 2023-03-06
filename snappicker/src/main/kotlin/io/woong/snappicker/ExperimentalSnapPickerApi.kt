package io.woong.snappicker

/**
 * Marker for experimental feature.
 * Experimental features could be changed or removed.
 */
@RequiresOptIn(
    message = "This is experimental API. It could be removed or changed in the future.",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
public annotation class ExperimentalSnapPickerApi
