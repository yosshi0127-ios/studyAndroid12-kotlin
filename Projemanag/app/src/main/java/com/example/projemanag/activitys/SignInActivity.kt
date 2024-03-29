package com.example.projemanag.activitys

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivitySignInBinding
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        setupFirebase()

        binding?.btnSignIn?.setOnClickListener {
            firebaseSignIn()
        }
    }

    private fun setupFirebase() {
        auth = Firebase.auth
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarSignInActivity)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarSignInActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun firebaseSignIn() {

        val email: String = binding?.etEmail?.text.toString().trim { it <= ' '}
        val password: String = binding?.etPassword?.text.toString().trim { it <= ' '}

        // バリデーションチェック
        if (!validateForm(email, password)) {
            return
        }

        // プログレスダイアログ表示
        showProgressDialog(resources.getString(R.string.please_wait))

        val currentUser = auth.currentUser
        if (currentUser != null) {
            hideProgressDialog()
            // カレントユーザーがいるので、処理
            Log.d("Sign in", "signInWithEmail:success")

            // User情報の取得を行う。結果は、signInSuccessに送る
            FirestoreClass().signUser(this)

        } else {

            // サインイン実行
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                hideProgressDialog()

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Sign in", "signInWithEmail:success")

                    // User情報の取得を行う。結果は、signInSuccessに送る
                    FirestoreClass().signUser(this)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("Sign in", "signInWithEmail:failure")
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    /// Validationチェック
    private fun validateForm(email: String, password: String): Boolean {

        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter a email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            } else -> {
                true
            }
        }
    }

    fun signInSuccess(loggedInUser: User?) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}