package android.example.com.singin

import android.content.Intent
import android.example.com.singin.delegate.TrimDelegate
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : AppCompatActivity() {
    var progressBar: ProgressBar? = null
    val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private var trimedString by TrimDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fbButton: Button = findViewById(R.id.fb)
//        val firstFragment = FirstFragment.newInstance(12, "Delegate")
//        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame_layout, firstFragment).commit()
        val googleButton: Button = findViewById(R.id.google)
        val loginGoogleButton: SignInButton = findViewById(R.id.btn_sign_in)
        val loginFbButton: LoginButton = findViewById(R.id.login_button)
//        progressBar = findViewById(R.id.progressBar)

        fbButton.setOnClickListener {
            loginFbButton.setPermissions(
                listOf(
                    "user_birthday",
                    "user_gender",
                    "user_link",
                    "user_photos",
                    "email",
                    "user_hometown",
                    "user_likes",
                    "user_location",
                    "user_posts"
                )
            )
            progressBar?.visibility = View.VISIBLE
            loginFbButton.performClick()
        }
        loginGoogleButton.setOnClickListener {
            val googleManager = GoogleManager.instance(this)
            val signInIntent: Intent = googleManager.signInIntent
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
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else {
            progressBar?.visibility = View.GONE
            FacebookManager.getResult(requestCode, resultCode, data!!)
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                Log.e("Tanya", "token ${account.idToken}")
                Log.e("Tanya", "id ${account.id}")
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                SavedPreference.setEmail(
                    this,
                    account.email.toString()
                )
                SavedPreference.setUsername(this, account.displayName.toString())
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}