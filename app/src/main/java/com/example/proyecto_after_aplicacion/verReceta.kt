package com.example.proyecto_after_aplicacion

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Frame6(
    modifier: Modifier = Modifier,
    navController: NavController?,
    recetaId: String,
    nombre: String,
    descripcionInicial: String,
    dificultadInicial: String,
    tiempoInicial: Int,
    preparacionInicial: String,
    categoriaInicial: String
) {

    //---------------------------------------VARIABLES------------------------------------------//

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var descripcion by remember { mutableStateOf(descripcionInicial) }
    var dificultad by remember { mutableStateOf(dificultadInicial) }
    var tiempo by remember { mutableStateOf(tiempoInicial.toString()) }
    var preparacion by remember { mutableStateOf(preparacionInicial) }
    var categoria by remember { mutableStateOf(categoriaInicial) }
    var isModificado by remember { mutableStateOf(false) } // Detectar cambios
    var showDialogoConfirmacion by remember { mutableStateOf(false) } // Controlar el dialogo confirmación
    var expandedCategoria by remember { mutableStateOf(false) } // Control del menú desplegable de categoría
    var expandedDificultad by remember { mutableStateOf(false) } // Control del menú desplegable de dificultad

    // Lista de las categorías disponibles
    val categorias = listOf("Comida rápida", "Mariscos", "Pasta", "Carnes", "Postres", "Sopas", "Comida Latina", "Ensaladas")

    // Lista de opciones para dificultad
    val dificultades = listOf("Alta", "Media", "Baja")


    //---------------------------------------FUNCIONES------------------------------------------//


    // Método para detectar si se han realizado cambios
    fun detectorCambios(newValue: String, currentValue: String, onUpdate: (String) -> Unit) {
        if (newValue != currentValue) {
            onUpdate(newValue)
            isModificado = true
        }
    }

    // Método para guardar los cambios
    fun guardarCambios() {
        // Revisa que el tiempo puesto es válido
        val tiempoInt = tiempo.toIntOrNull() ?: 0
        if (tiempoInt <= 0) {
            Toast.makeText(context, "El tiempo debe ser un número válido mayor que 0.", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el id del usuario actual
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(context, "Debes estar autenticado para guardar cambios.", Toast.LENGTH_SHORT).show()
            return
        }

        // Referencia al documento de la receta en Firestore
        val recetaRef = db.collection("recetasApp").document(recetaId)

        // Validar si el usuario actual es el creador de la receta
        recetaRef.get().addOnSuccessListener { document ->
            val creatorId = document.getString("creatorId")
            if (creatorId == currentUserId) {
                // Si el usuario es el dueño de la receta, guarda los cambios
                val normalizedCategory = categoria.lowercase()
                recetaRef.update(
                    mapOf(
                        "descripcion" to descripcion,
                        "dificultad" to dificultad,
                        "tiempo" to tiempoInt,
                        "preparacion" to preparacion,
                        "categoria" to normalizedCategory
                    )
                ).addOnSuccessListener {
                    Toast.makeText(context, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                    isModificado = false
                }.addOnFailureListener {
                    Toast.makeText(context, "Error al guardar los cambios", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Si el usuario no es el creador, muestra un mensaje de error
                Toast.makeText(context, "No tienes permiso para editar esta receta.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error al validar el creador de la receta.", Toast.LENGTH_SHORT).show()
        }
    }

    //---------------------------------------CODIGO------------------------------------------//

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Scroll vertical habilitado
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Flecha Atrás",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        if (isModificado) {
                            showDialogoConfirmacion = true
                        } else {
                            navController?.popBackStack()
                        }
                    }
            )
            Spacer(modifier = Modifier.width(13.dp))
            Text(
                text = nombre,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Descripción", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = descripcion,
            onValueChange = { detectorCambios(it, descripcion) { descripcion = it } },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFF5F5F5)),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Menú desplegable para dificultad
        Text("Dificultad", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dificultad,
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
            DropdownMenu(
                expanded = expandedDificultad,
                onDismissRequest = { expandedDificultad = false }
            ) {
                dificultades.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            detectorCambios(item, dificultad) { dificultad = it }
                            expandedDificultad = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tiempo (minutos)", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = tiempo,
            onValueChange = { detectorCambios(it, tiempo) { tiempo = it } },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFF5F5F5)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Categoría", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = categoria,
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
            DropdownMenu(
                expanded = expandedCategoria,
                onDismissRequest = { expandedCategoria = false }
            ) {
                categorias.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            detectorCambios(item.lowercase(), categoria) { categoria = it }
                            expandedCategoria = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Preparación", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = preparacion,
            onValueChange = { detectorCambios(it, preparacion) { preparacion = it } },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFF5F5F5)),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { guardarCambios() },
            enabled = isModificado,
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isModificado) Color(0xFFFFDAB9) else Color.Gray),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Guardar Cambios", color = Color.Black, style = MaterialTheme.typography.labelLarge)
        }
    }


    //---------------------------------------DIALOGO------------------------------------------//


    // Diálogo de confirmación al pulsar la flecha atrás para descartar cambios o cancelar y guardarlos
    if (showDialogoConfirmacion) {
        AlertDialog(
            onDismissRequest = { showDialogoConfirmacion = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialogoConfirmacion = false
                    navController?.popBackStack()
                }) {
                    Text("Descartar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogoConfirmacion = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Cambios sin guardar") },
            text = { Text("Tienes cambios sin guardar. ¿Deseas descartarlos o cancelar y guardar?") }
        )
    }
}

@Preview(widthDp = 400, heightDp = 788)
@Composable
private fun Frame6Preview() {
    Frame6(
        modifier = Modifier,
        navController = null,
        recetaId = "exampleId",
        nombre = "Ejemplo Receta",
        descripcionInicial = "Descripción de ejemplo",
        dificultadInicial = "Media",
        tiempoInicial = 30,
        preparacionInicial = "Preparación de ejemplo",
        categoriaInicial = "Postres"
    )
}
