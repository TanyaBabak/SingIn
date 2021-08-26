package android.example.com.singin

import android.content.Intent
import android.os.Bundle
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
                        Log.e("Tanya", result.accessToken?.token!!)
                        val request = GraphRequest.newMeRequest(
                            result.accessToken
                        ) { json, response ->
                            if (response.error != null) {
                                // handle error
                                println("ERROR")
                            } else {
                                println("Success")
                                val jsonresult = json.toString()
                                println("JSON Result$jsonresult")
                                Log.e("Tanya", "last_name".checkParameters(json) ?: "")
                                Log.e("Tanya", "first_name".checkParameters(json) ?: "")
                                Log.e("Tanya", "id".checkParameters(json) ?: "")
                                Log.e("Tanya", "gender".checkParameters(json) ?: "")
                                Log.e("Tanya", "birthday".checkParameters(json) ?: "")
                                Log.e("Tanya", "link".checkParameters(json) ?: "")
                                Log.e("Tanya", "location".checkParameters(json) ?: "")
                                Log.e("Tanya", "hometown".checkParameters(json) ?: "")
                            }
                        }

                        val parametersBundle = Bundle().apply {
                            putString(
                                "fields",
                                "id,birthday,gender,last_name,first_name,link,location,hometown,email"
                            )
                            putString("edges", "likes")
                        }

                        request.run {
                            this.parameters = parametersBundle
                            this.executeAsync()
                        }
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

        fun checkException(parameterValue: String) {
            try {
                Log.e("Tanya", parameterValue)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        fun getResult(requestCode: Int, resultCode: Int, data: Intent) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}