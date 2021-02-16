package com.antsfamily.biketrainer.ui.util

import com.facebook.shimmer.ShimmerFrameLayout

inline var ShimmerFrameLayout.isShimmering: Boolean
    get() = isShimmerStarted
    set(value) {
        if (value) startShimmer() else stopShimmer()
    }
