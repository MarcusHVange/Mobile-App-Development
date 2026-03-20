package dk.itu.moapd.x9.mhiv.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import dk.itu.moapd.x9.mhiv.R


@Composable
fun LoginScreen(
    onEmailLogin: () -> Unit,
    onPhoneLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    val screenBackground = colorResource(R.color.background_light)
    val screenHorizontalPadding = dimensionResource(R.dimen.horizontal_padding)
    val screenVerticalPadding = dimensionResource(R.dimen.vertical_padding)
    val loginContentWidth = dimensionResource(R.dimen.login_screen_content_width)
    val loginSectionSpacing = dimensionResource(R.dimen.login_screen_section_spacing)
    val mediumSpacing = dimensionResource(R.dimen.section_spacing_medium)
    val loginIconSize = dimensionResource(R.dimen.login_screen_icon_size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = screenHorizontalPadding,
                vertical = screenVerticalPadding
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.width(loginContentWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(loginSectionSpacing))

            LoginOptionButton(
                label = stringResource(R.string.login_email_button),
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_person_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = onEmailLogin
            )

            Spacer(modifier = Modifier.height(mediumSpacing))

            LoginOptionButton(
                label = stringResource(R.string.login_phone_button),
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_local_phone_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = onPhoneLogin
            )

            Spacer(modifier = Modifier.height(mediumSpacing))

            LoginOptionButton(
                label = stringResource(R.string.login_google_button),
                icon = {
                    Image(
                        painter = painterResource(R.drawable.google_logo),
                        contentDescription = null,
                        modifier = Modifier.size(loginIconSize)
                    )
                },
                onClick = onGoogleLogin
            )

            Spacer(modifier = Modifier.height(loginSectionSpacing))

            Text(
                text = stringResource(R.string.login_guest_button),
                modifier = Modifier.clickable(onClick = onContinueAsGuest),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun LoginOptionButton(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val cornerRadius = dimensionResource(R.dimen.login_screen_button_corner_radius)
    val buttonHorizontalPadding = dimensionResource(R.dimen.card_padding)
    val buttonVerticalPadding = dimensionResource(R.dimen.section_spacing_medium)
    val iconLabelSpacing = dimensionResource(R.dimen.login_screen_label_spacing)
    val buttonElevation = dimensionResource(R.dimen.card_elevation)

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = buttonElevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = buttonHorizontalPadding,
                    vertical = buttonVerticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()

            Spacer(modifier = Modifier.width(iconLabelSpacing))

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
