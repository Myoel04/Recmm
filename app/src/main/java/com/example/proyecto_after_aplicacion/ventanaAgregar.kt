package com.example.proyecto_after_aplicacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Frame5(navController: NavController) {

    //--------------------------DECLARACION-------------------------//

    val nombreReceta = remember { mutableStateOf("") }
    val descripcionReceta = remember { mutableStateOf("") }
    val tiempoReceta = remember { mutableStateOf("") }
    val dificultadReceta = remember { mutableStateOf("") }
    val preparacionReceta = remember { mutableStateOf("") }
    val categoriaReceta = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()//para operaciones asincronas

    // Lista de categorías
    val categorias = listOf(
        "Comida rápida", "Mariscos", "Pasta", "Carnes",
        "Postres", "Sopas", "Comida Latina", "Ensaladas"
    )
    // Lista de opciones para dificultad
    val dificultades = listOf("Alta", "Media", "Baja")

    // Control de menús desplegables
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedDificultad by remember { mutableStateOf(false) }

    //--------------------------FUNCION--------------------------//

    //metodo de guardar la receta
    fun guardarReceta() {
        val user = FirebaseAuth.getInstance().currentUser
        val creatorId = user?.uid

        if (creatorId == null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Debes estar autenticado para guardar una receta.")
            }
            return
        }

//qeu los campos no esten vacios
        if (nombreReceta.value.isBlank() ||
            descripcionReceta.value.isBlank() ||
            tiempoReceta.value.isBlank() ||
            dificultadReceta.value.isBlank() ||
            preparacionReceta.value.isBlank() ||
            categoriaReceta.value.isBlank()
        ) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Por favor, llena todos los campos.")
            }
            return
        }
//campo tiempo control
        val tiempo = tiempoReceta.value.toIntOrNull()
        if (tiempo == null || tiempo <= 0) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("El tiempo debe ser un número válido y mayor que 0.")
            }
            return
        }

        val receta = hashMapOf(
            "nombre" to nombreReceta.value.trim(),
            "descripcion" to descripcionReceta.value.trim(),
            "tiempo" to tiempo,
            "dificultad" to dificultadReceta.value.trim(),
            "preparacion" to preparacionReceta.value.trim(),
            "categoria" to categoriaReceta.value.trim().lowercase(),
            "creatorId" to creatorId
        )
//guardar receta
        FirebaseFirestore.getInstance().collection("recetasApp")
            .add(receta)
            .addOnSuccessListener {
                nombreReceta.value = ""
                descripcionReceta.value = ""
                tiempoReceta.value = ""
                dificultadReceta.value = ""
                preparacionReceta.value = ""
                categoriaReceta.value = ""

                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Receta guardada exitosamente.")
                }
                navController.navigate("ventCategorias")
            }
            .addOnFailureListener { e ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error al guardar la receta: ${e.message}")
                }
            }
    }

//---------------------------------------CODIGO------------------------------------------//
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Volver a la pantalla anterior",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { navController.navigate("ventCategorias") }
                )
                Spacer(modifier = Modifier.width(65.dp))
                Text(
                    text = "Crear Receta",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
//campo del nombre
            OutlinedTextField(
                value = nombreReceta.value,
                onValueChange = { input -> nombreReceta.value = input },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color(0xFFF5F5F5))
            )

            Spacer(modifier = Modifier.height(16.dp))
//campo de descripcion
            OutlinedTextField(
                value = descripcionReceta.value,
                onValueChange = { input -> descripcionReceta.value = input },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color(0xFFF5F5F5))
            )

            Spacer(modifier = Modifier.height(16.dp))
//campo de tiempo
            OutlinedTextField(
                value = tiempoReceta.value,
                onValueChange = { tiempoReceta.value = it },
                label = { Text("Tiempo (minutos)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color(0xFFF5F5F5))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Menú desplegable para categoría
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = categoriaReceta.value,
                    onValueChange = {},
                    label = { Text("Categoría") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedCategoria = true },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Expandir menú",
                            modifier = Modifier.clickable { expandedCategoria = !expandedCategoria }
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color(0xFFF5F5F5))
                )
                //desplegable de categoria
                DropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                categoriaReceta.value = categoria.lowercase()
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menú desplegable para dificultad
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dificultadReceta.value,
                    onValueChange = {},
                    label = { Text("Dificultad") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedDificultad = true },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Expandir menú",
                            modifier = Modifier.clickable { expandedDificultad = !expandedDificultad }
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color(0xFFF5F5F5))
                )
                //desplegable dificultad
                DropdownMenu(
                    expanded = expandedDificultad,
                    onDismissRequest = { expandedDificultad = false }
                ) {
                    dificultades.forEach { dificultad ->
                        DropdownMenuItem(
                            text = { Text(dificultad) },
                            onClick = {
                                dificultadReceta.value = dificultad
                                expandedDificultad = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
//campo de preparacion
            OutlinedTextField(
                value = preparacionReceta.value,
                onValueChange = { input -> preparacionReceta.value = input },
                label = { Text("Preparación") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color(0xFFF5F5F5))
            )

            Spacer(modifier = Modifier.height(20.dp))
//boton guardar receta
            Button(
                onClick = { guardarReceta() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAB9)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Agregar receta", color = Color.Black)
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}


@Preview(widthDp = 400, heightDp = 760, showSystemUi = true)
@Composable
private fun Frame5Preview() {
    val navController = rememberNavController()
    Frame5(navController = navController)
}
