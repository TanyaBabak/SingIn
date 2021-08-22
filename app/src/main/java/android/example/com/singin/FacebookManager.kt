package android.example.com.singin

import android.content.Intent
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException


class FacebookManager {


    companion object {
        lateinit var callbackManager: CallbackManager
        fun listenCallback(
            loginButton: LoginButton,
            successCallback: () -> Unit,
            errorCallback: () -> Unit
        ) {
            callbackManager = CallbackManager.Factory.create()
            loginButton
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        for (permission in result!!.recentlyDeniedPermissions) {
                            Log.e("tanya", permission)
                        }
                        Log.e("Tanya", result?.accessToken?.applicationId.toString())
                        GraphRequest.newMeRequest(
                            result.getAccessToken()
                        ) { json, response ->
                            if (response.error != null) {
                                // handle error
                                println("ERROR")
                            } else {
                                println("Success")
                                try {
                                    val jsonresult = json.toString()
                                    println("JSON Result$jsonresult")
                                    Log.e("Tanya", json.getString("name"))
                                    Log.e("Tanya", json.getString("id"))
                                    Log.e("Tanya", json.getString("gender"))
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        }.executeAsync()
                        successCallback.invoke()
                    }

                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException?) {
                        Log.e("Tanya", error.toString())
                        errorCallback.invoke()
                    }

                })
        }

        fun getResult(requestCode: Int, resultCode: Int, data: Intent) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}