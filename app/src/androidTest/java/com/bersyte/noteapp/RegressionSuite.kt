package com.bersyte.noteapp

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Aggregated regression suite that groups the regression and full/feature coverage tests.
 * This allows running the full regression group from the test runner explicitly (or CI).
 */

@RunWith(Suite::class)
@Suite.SuiteClasses(
    FullCoverageTests::class,
    FeatureCoverageTests::class,
    ExtraCoverageTests::class,
    RegressionTests::class
)
class RegressionSuite
