// feature/alert/src/main/java/com/sypark/finnhub/feature/alert/AlertCreateScreen.kt
package com.sypark.finnhub.feature.alert

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.ui.component.CapsuleButton
import com.sypark.finnhub.core.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AlertCreateRoute(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlertCreateViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AlertCreateEffect.Dismiss -> onDismiss()
                is AlertCreateEffect.ShowSnackbar -> Unit
            }
        }
    }

    AlertCreateScreen(state = state, onIntent = viewModel::onIntent, onDismissRequest = onDismiss, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertCreateScreen(
    state: AlertCreateState,
    onIntent: (AlertCreateIntent) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest, modifier = modifier) {
        Column(modifier = Modifier.padding(Spacing.space4)) {
            Text(text = "${state.symbol} 가격 알림", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.padding(top = Spacing.space4))

            OutlinedTextField(
                value = state.targetPriceInput,
                onValueChange = { onIntent(AlertCreateIntent.TargetPriceChanged(it)) },
                label = { Text("목표가") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.priceError != null,
                supportingText = { state.priceError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.padding(top = Spacing.space4))

            Text(text = "조건", style = MaterialTheme.typography.bodyMedium)
            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.condition == AlertCondition.ABOVE,
                        onClick = { onIntent(AlertCreateIntent.ConditionChanged(AlertCondition.ABOVE)) },
                    )
                    Text("이상")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.condition == AlertCondition.BELOW,
                        onClick = { onIntent(AlertCreateIntent.ConditionChanged(AlertCondition.BELOW)) },
                    )
                    Text("이하")
                }
            }

            Spacer(Modifier.padding(top = Spacing.space4))

            CapsuleButton(
                text = "알림 저장",
                onClick = { onIntent(AlertCreateIntent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.padding(bottom = Spacing.space6))
        }
    }
}
