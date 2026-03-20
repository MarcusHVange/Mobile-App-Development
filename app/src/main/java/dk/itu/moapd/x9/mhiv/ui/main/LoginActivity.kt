package dk.itu.moapd.x9.mhiv.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.ui.composables.LoginScreen
import dk.itu.moapd.x9.mhiv.ui.theme.X9Theme

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startMainActivity()
            return
        }

        setContent {
            X9Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        onEmailLogin = { launchEmailSignIn() },
                        onPhoneLogin = { launchPhoneSignIn() },
                        onGoogleLogin = { launchGoogleSignIn() },
                        onContinueAsGuest = { continueAsGuest() }
                    )
                }
            }
        }
    }

    private fun launchEmailSignIn() {
        launchSignInFlow(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
    }

    private fun launchPhoneSignIn() {
        launchSignInFlow(listOf(AuthUI.IdpConfig.PhoneBuilder().build()))
    }

    private fun launchGoogleSignIn() {
        launchSignInFlow(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
    }

    private fun launchSignInFlow(providers: List<AuthUI.IdpConfig>) {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setCredentialManagerEnabled(false)
            .setAvailableProviders(providers)
            .setLogo(R.drawable.baseline_firebase_24)
            .setTheme(R.style.Theme_X9)
            .apply {
                setTosAndPrivacyPolicyUrls(
                    "https://firebase.google.com/terms/",
                    "https://firebase.google.com/policies/analytics"
                )
            }
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun continueAsGuest() {
        startMainActivity()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        when (result.resultCode) {
            RESULT_OK -> {
                showSnackBar(getString(R.string.login_success_message))
                startMainActivity()
            }

            else -> {
                showSnackBar(getString(R.string.login_failed_message))
            }
        }
    }

    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            window.decorView.rootView, message, Snackbar.LENGTH_SHORT
        ).show()
    }
}
