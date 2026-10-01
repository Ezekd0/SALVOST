package com.example.ai_ctdrs.client

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.ai_ctdrs.theme.AICTDRSTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class RedesignTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    @Test fun allFiveScenariosRemainSelectableAndSubmitOriginalTelemetry() {
        var submitted: Scenario? = null
        rule.setContent { AICTDRSTheme { Column(Modifier.verticalScroll(rememberScrollState())) { ScanScreen(ClientState(busy=false)) { submitted=it } } } }
        Scenario.entries.forEach { scenario ->
            rule.onNodeWithContentDescription("Choose scenario").performClick()
            val name=if(scenario==Scenario.BENIGN) "Normal Traffic / Benign" else scenario.title
            rule.onAllNodesWithText(name).onLast().performClick()
            rule.onNodeWithText("Start Scan").performScrollTo().performClick()
            rule.runOnIdle { assertEquals(scenario,submitted);assertTrue(submitted!!.telemetry.is_demo) }
        }
    }
    @Test fun shapUsesReturnedFeatureValuesAndCanCollapse() {
        val analysis=Analysis(7,"Test classification",.81,32.5,"""[{"feature":"packet_rate","value":321.0,"contribution":-0.125}]""")
        rule.setContent { AICTDRSTheme { Explanation(analysis) } }
        rule.onNodeWithText("packet rate").assertExists()
        rule.onNodeWithText("-0.1250").assertExists()
        rule.onNodeWithText("Feature value: 321.0").assertExists()
        rule.onNodeWithContentDescription("Collapse SHAP").performClick()
        rule.onNodeWithText("packet rate").assertDoesNotExist()
    }
    @Test fun onboardingFinishesOnlyOnUserAction() {
        var finished=false
        rule.setContent { AICTDRSTheme { Onboarding(2, {}, {finished=true}) } }
        rule.runOnIdle {assertFalse(finished)}
        rule.onNodeWithText("Get Started").performScrollTo().performClick()
        rule.runOnIdle {assertTrue(finished)}
    }
}
