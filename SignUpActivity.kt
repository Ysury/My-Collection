package com.collect.mycollection

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import database.MyDatabaseHelper


class SignUpActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var myDatabaseHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Référencement des vues
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signupButton)

        myDatabaseHelper = MyDatabaseHelper(this)

        // Ajout d'un écouteur de clic sur le bouton S'inscrire
        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Vérifier si les champs d'inscription sont remplis
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                // Insérer les données utilisateur dans la base de données
                val db = myDatabaseHelper.writableDatabase
                val values = ContentValues().apply {
                    put(MyDatabaseHelper.COLUMN_USER_USERNAME, name)
                    put(MyDatabaseHelper.COLUMN_USER_PASSWORD, password)
                    put(MyDatabaseHelper.COLUMN_USER_EMAIL, email)
                }
                val newRowId = db?.insert(MyDatabaseHelper.TABLE_USER, null, values)

                // Vérifier si l'insertion a réussi
                if (newRowId != null && newRowId > -1) {
                    Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()

                    // Rediriger vers l'activité de connexion
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Fermer l'activité en cours pour éviter d'y revenir en appuyant sur le bouton Retour
                } else {
                    Toast.makeText(this, "Une erreur est survenue, veuillez réessayer", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


