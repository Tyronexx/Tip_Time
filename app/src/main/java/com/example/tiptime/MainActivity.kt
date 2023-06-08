package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiptime.ui.theme.TipTimeTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipTimeScreen()
                }
            }
        }
    }
}

@Composable
fun TipTimeScreen(
    modifier: Modifier=Modifier
){
//  This state is used in both composables(i.e Hoisted from here)
    var amountInput by remember {
        mutableStateOf("")
    }
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    var tipInput by remember {
        mutableStateOf("")
    }
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
    var roundUp by remember {
        mutableStateOf(false)
    }

//  The LocalFocusManager interface is used to control focus in Compose.
//  We use this variable to move the focus to, and clear the focus from, the text boxes.
    val focusManager = LocalFocusManager.current

    val tip = calculateTip(amount = amount, tipPercent = tipPercent, roundUp = roundUp)


    Column(
        modifier = modifier
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Text(
            text = stringResource(R.string.calculate_tip),
            fontSize = 24.sp,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = modifier.height(16.dp))

        EditNumberField(
            label = R.string.bill_amount,
            value = amountInput,
            onValueChange = { amountInput=it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Down)})
//      The moveFocus() function moves the focus in the specified direction, which is down to the Tip (%) text field in this case.
        )
        EditNumberField(
            label = R.string.how_was_the_service,
            value = tipInput,
            onValueChange = { tipInput=it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
        RoundTheTipRow(
            roundUp = roundUp,
            onRoundUpChanged = { roundUp = it }
        )
        
        Spacer(modifier = modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.tip_amount, tip),
            modifier = modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


/**
 * Composable for the textfield whose state is hoisted from "TipTimeScreen" above, this composable function is usedd twice with different parameters
 * @param value is the current value to display i.e amountInput variable and tipInput variable
 * @param onValueChange  is used as the "onValueChange" callback passed into the TextField composable.*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNumberField(
    @StringRes label:Int,
    value:String,
    onValueChange:( String ) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier=Modifier,
){
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(id = label))
        },
        modifier = modifier
            .fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        )
}

@Composable
fun RoundTheTipRow(
    roundUp:Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier=Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = stringResource(R.string.round_up_tip)
        )
        Switch(
            modifier= modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            checked = roundUp,
            onCheckedChange = onRoundUpChanged,
            colors = SwitchDefaults.colors(uncheckedThumbColor = Color.DarkGray)
        )
    }
}




/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency and display it onscreen.
 * Example would be "$10.00".
 */

@VisibleForTesting
internal fun calculateTip(amount:Double, tipPercent:Double, roundUp: Boolean):String{
    var tip = tipPercent/100 * amount
    if (roundUp){
        tip = kotlin.math.ceil(tip)
    }
    return NumberFormat.getCurrencyInstance().format(tip)
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    TipTimeTheme {
        TipTimeScreen()
    }
}