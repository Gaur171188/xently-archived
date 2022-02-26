package co.ke.xently.accounts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ke.xently.accounts.ui.password_reset.PasswordResetScreen
import co.ke.xently.accounts.ui.password_reset.request.PasswordResetRequestScreen
import co.ke.xently.accounts.ui.signin.SignInScreen
import co.ke.xently.accounts.ui.signup.SignUpScreen
import co.ke.xently.accounts.ui.verification.VerificationScreen
import co.ke.xently.feature.theme.XentlyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XentlyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    ProductsNavHost(navController = navController) {
                        if (!navController.navigateUp()) onBackPressed()
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProductsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onNavigationIconClicked: () -> Unit,
) {
    NavHost(modifier = modifier, navController = navController, startDestination = "signin") {
        val signInScreen: @Composable (NavBackStackEntry) -> Unit = {
            SignInScreen(
                modifier = Modifier.fillMaxSize(),
                username = it.arguments?.getString("username") ?: "",
                password = it.arguments?.getString("password") ?: "",
                onNavigationIconClicked = onNavigationIconClicked,
                onSuccessfulSignIn = { user ->
                    if (user.isVerified) {
                        // TODO: Navigate to home screen...
                        onNavigationIconClicked()
                    } else {
                        navController.navigate("verify-account")
                    }
                },
                onCreateAccountButtonClicked = { username, password ->
                    navController.navigate("signup?username=${username}&password=${password}") {
                        launchSingleTop = true
                    }
                },
                onForgotPasswordButtonClicked = {
                    navController.navigate("request-password-reset")
                },
            )
        }
        composable(
            "signin",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "xently://accounts/signin/"
                }
            ),
            content = signInScreen,
        )
        composable(
            "signin?username={username}&password={password}",
            listOf(
                navArgument("username") {
                    defaultValue = ""
                },
                navArgument("password") {
                    defaultValue = ""
                },
            ),
            content = signInScreen,
        )
        composable(
            "signup?username={username}&password={password}",
            listOf(
                navArgument("username") {
                    defaultValue = ""
                },
                navArgument("password") {
                    defaultValue = ""
                },
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "xently://accounts/signup/"
                }
            ),
        ) {
            SignUpScreen(
                modifier = Modifier.fillMaxSize(),
                username = it.arguments?.getString("username") ?: "",
                password = it.arguments?.getString("password") ?: "",
                onNavigationIconClicked = onNavigationIconClicked,
                onSuccessfulSignUp = { user ->
                    if (user.isVerified) {
                        // TODO: Navigate to home screen...
                        onNavigationIconClicked()
                    } else {
                        navController.navigate("verify-account")
                    }
                },
                onSignInButtonClicked = { username, password ->
                    navController.navigate("signin?username=${username}&password=${password}") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            "verify-account?code={code}",
            listOf(navArgument("code") { defaultValue = "" }),
        ) {
            VerificationScreen(
                modifier = Modifier.fillMaxSize(),
                verificationCode = it.arguments?.getString("code") ?: "",
                onNavigationIconClicked = onNavigationIconClicked,
                onSuccessfulVerification = {
//                    navController.navigate("reset-password")
                    // TODO: Navigate to home page... Above code may not be necessary if the `this` activity is finished when starting a new activity
                },
            )
        }
        composable(
            "request-password-reset?email={email}",
            listOf(navArgument("email") { defaultValue = "" }),
        ) {
            PasswordResetRequestScreen(
                modifier = Modifier.fillMaxSize(),
                email = it.arguments?.getString("email") ?: "",
                onNavigationIconClicked = onNavigationIconClicked,
                onSuccessfulRequest = {
                    navController.navigate("reset-password")
                },
            )
        }
        composable(
            "reset-password?isChange={isChange}",
            listOf(
                navArgument("isChange") {
                    defaultValue = false
                    type = NavType.BoolType
                },
            ),
        ) {
            PasswordResetScreen(
                modifier = Modifier.fillMaxSize(),
                isChange = it.arguments?.getBoolean("isChange") ?: false,
                onNavigationIconClicked = onNavigationIconClicked,
                onSuccessfulReset = {
                    navController.popBackStack("reset-password", true)
                    // TODO: Navigate to home page... Above code may not be necessary if the `this` activity is finished when starting a new activity
                },
            )
        }
    }
}