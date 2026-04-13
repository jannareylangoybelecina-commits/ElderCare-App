package com.eldercare.app.ui.theme

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.Shape

/**
 * Header with a soft curved bottom edge (asymmetric, dips toward the left like the design mockup).
 */
val ElderlyDashboardWavyHeaderShape: Shape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height * 0.78f)
    quadraticBezierTo(
        size.width * 0.52f,
        size.height * 0.98f,
        0f,
        size.height * 0.68f
    )
    close()
}
