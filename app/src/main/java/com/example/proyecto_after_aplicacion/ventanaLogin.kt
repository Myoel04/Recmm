package com.example.proyecto_after_aplicacion

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.proyecto_after_aplicacion.ui.theme.Proyecto_after_AplicacionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Frame1(modifier: Modifier = Modifier, contNavegador: NavController) {

    //---------------------------------------VARIABLES------------------------------------------//

    val autentificacion = FirebaseAuth.getInstance()//usamos la instancia para la autenticación
    val email = remember { mutableStateOf("") }
    val contrasena = remember { mutableStateOf("") }
    val errorMensaje = remember { mutableStateOf<String?>(null) }

//---------------------------------------CODIGO------------------------------------------//

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // espacio

            Text(
                text = "¡Bienvenido!",
                color = Color.Black,
                style = TextStyle(fontSize = 25.sp),
                modifier = Modifier.padding(vertical = 9.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(//label de usuario
                    text = "USUARIO",
                    color = Color.Black,
                    style = TextStyle(fontSize = 12.sp)
                )
                TextField(//text fiel de usuario
                    value = email.value,
                    onValueChange = { email.value = it },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFF5F5F5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Text(//contraseña texto
                    text = "CONTRASEÑA",
                    color = Color.Black,
                    style = TextStyle(fontSize = 12.sp)
                )
                TextField(//campo contraseña
                    value = contrasena.value,
                    onValueChange = { contrasena.value = it },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFF5F5F5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    visualTransformation = PasswordVisualTransformation(),//ocultamos la contraseña
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(//boton para iniciar sesion si las credenciales son correctas
                    onClick = {
                        if (email.value.isEmpty() || contrasena.value.isEmpty()) {//si esta vacio un campo mensaje
                            errorMensaje.value = "Por favor ingrese usuario y contraseña"
                            return@Button
                        }
    //si son conrrectas nos lleva a la ventana de las categorias
                        autentificacion.signInWithEmailAndPassword(email.value, contrasena.value)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    contNavegador.navigate("ventCategorias")
                                } else {//sino salta un mensaje de error de inicio de sesión
                                    errorMensaje.value =
                                        "Error de inicio de sesión: ${task.exception?.message}"
                                }
                            }
                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAB9)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Entrar",
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(//boton de registro
                    onClick = { contNavegador.navigate("ventana2") },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAB9)), // Color aplicado directamente
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Registrarse",
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espacio inferior pequeño
        }

        //---------------------------------------MANEJO ERRORES------------------------------------------//

//manejar los mensajes de error
        val contexto = LocalContext.current
        LaunchedEffect(errorMensaje.value) {
            errorMensaje.value?.let { message ->
                //se muestra el mensaje  con un toast
                Toast.makeText(
                    contexto,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
                errorMensaje.value = null
            }
        }
    }
}

@Preview(widthDp = 400, heightDp = 802, showSystemUi = true)
@Composable
private fun Frame1Preview() {
    Proyecto_after_AplicacionTheme {
        Frame1(contNavegador = rememberNavController())
    }
}
