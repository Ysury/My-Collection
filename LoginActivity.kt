package com.collect.mycollection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.content.Intent
import android.widget.Toast
import database.MyDatabaseHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Récupération des références des EditTexts
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        // Initialisation du helper de base de données
        dbHelper = MyDatabaseHelper(this)

        // Ajout d'un écouteur de clic sur le bouton de connexion
        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Vérification de l'authentification de l'utilisateur
            if (isUserAuthenticated(username, password)) {
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Nom d'utilisateur ou mot de passe invalide", Toast.LENGTH_SHORT).show()
            }
        }

        // Ajout d'un écouteur de clic sur le bouton d'inscription
        val signUpButton: Button = findViewById(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Méthode pour vérifier si l'utilisateur est authentifié
    private fun isUserAuthenticated(username: String, password: String): Boolean {
        // Récupération de la base de données en mode lecture seule
        val db = dbHelper.readableDatabase

        // Définition des colonnes à sélectionner
        val projection = arrayOf(MyDatabaseHelper.Companion.COLUMN_USER_USERNAME, MyDatabaseHelper.Companion.COLUMN_USER_PASSWORD)

        // Définition de la clause WHERE
        val selection = "${MyDatabaseHelper.Companion.COLUMN_USER_USERNAME} = ? AND ${MyDatabaseHelper.Companion.COLUMN_USER_PASSWORD} = ?"

        // Définition des arguments pour la clause WHERE
        val selectionArgs = arrayOf(username, password)

        // Requête de sélection des données de l'utilisateur
        val cursor = db.query(
            MyDatabaseHelper.Companion.TABLE_USER,    // table
            projection,    // colonnes à sélectionner
            selection,     // clause WHERE
            selectionArgs, // valeurs pour la clause WHERE
            null,          // groupBy
            null,          // having
            null           // orderBy
        )

        // Vérification si l'utilisateur a été trouvé dans la base de données
        val isUserAuthenticated = cursor.count > 0

        // Fermeture du curseur et de la base de données
        cursor.close()
        db.close()

        return isUserAuthenticated
    }
}
