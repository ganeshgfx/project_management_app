package com.ganeshgfx.projectmanagement

import android.app.Activity
import android.content.Intent
import android.content.res.TypedArray
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.ActivityLoginBinding
import com.ganeshgfx.projectmanagement.viewModels.LoginVM
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var googleSignInClient: GoogleSignInClient

    private val viewModel: LoginVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel.isLogged.observe(this) {
            if (it) {
                log("Login done",it)
                startApp()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.login.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        launcher.launch(googleSignInClient.signInIntent)
    }

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    if (account != null) {
                        Snackbar.make(binding.root, "Please Wait...", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(fetchColor(androidx.appcompat.R.attr.colorPrimary))
                            .show()
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        FirebaseAuth.getInstance().signInWithCredential(credential)
                    }
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun startApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun fetchColor(color: Int): Int {
        val typedValue = TypedValue()
        val a: TypedArray = obtainStyledAttributes(
            typedValue.data,
            intArrayOf(color)
        )
        val v = a.getColor(0, 0)
        a.recycle()
        return v
    }
}