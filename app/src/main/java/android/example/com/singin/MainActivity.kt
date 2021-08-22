package android.example.com.singin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import java.util.*


class MainActivity : AppCompatActivity() {
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fbButton: Button = findViewById(R.id.fb)
//        val googleButton: Button = findViewById(R.id.google)
        val loginGoogleButton: SignInButton = findViewById(R.id.btn_sign_in)
        val loginFbButton: LoginButton = findViewById(R.id.login_button)
        progressBar = findViewById(R.id.progressBar)

        fbButton.setOnClickListener {
            FacebookManager.callbackManager.logInWithReadPermissions(
                this,
                Arrays.asList("user_birthday", "user_gender", "user_link", "user_photos")
            )
            progressBar?.visibility = View.VISIBLE
            loginFbButton.performClick()
        }
        loginGoogleButton.setOnClickListener {
            val signInIntent: Intent = GoogleManager.instance(this).signInIntent
            startActivityForResult(signInIntent, 10)
        }

        FacebookManager.listenCallback(loginFbButton, ::handleSuccess, ::handleError)
    }


    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            val frameLayout: FrameLayout = findViewById(R.id.FrameLayout2)
            frameLayout.visibility = View.VISIBLE
        }
    }

    private fun handleSuccess() {
        Log.e("Tanya", "Success")
    }

    private fun handleError() {
        Log.e("Tanya", "Error")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task)
        } else {
            progressBar?.visibility = View.GONE
            FacebookManager.getResult(requestCode, resultCode, data!!)
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            Log.e("Tanya", "token ${account.idToken}")
            Log.e("Tanya", "id ${account.id}")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Tanya", "signInResult:failed code=" + e.statusCode)
            e.stackTrace
//            updateUI(null)
        }
    }
}