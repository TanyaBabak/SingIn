package android.example.com.singin

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class GoogleManager {

    companion object {
        fun instance(context: Context): GoogleSignInClient {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("612448163760-h8g489gheubo4b4g8d3pcpid40707e4j.apps.googleusercontent.com")
                .requestEmail()
                .build()
            return GoogleSignIn.getClient(context, gso)
        }
    }
}