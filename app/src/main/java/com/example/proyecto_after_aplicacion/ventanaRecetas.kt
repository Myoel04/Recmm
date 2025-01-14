package com.example.proyecto_after_aplicacion

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyecto_after_aplicacion.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Frame4(navController: NavController, categoria: String) {

    //---------------------------------------VARIABLES------------------------------------------//

    val db = FirebaseFirestore.getInstance()
    val recetas = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val searchQuery = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val recetaAEliminar = remember { mutableStateOf<Pair<String, Map<String, Any>>?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    //actualizacion de las recetas a tiempo real
    var recetasListener by remember { mutableStateOf<ListenerRegistration?>(null) }


    //---------------------------------------FUNCIONES------------------------------------------//


    //efecto al cambiar la categoria
    LaunchedEffect(categoria) {
        val normalizedCategory = categoria.lowercase().trim()

        // limpia el listener y se evita duplicaciones
        recetasListener?.remove()

//consulta segun la categoria que se seleccione
        val query = if (normalizedCategory == "todas") {
            db.collection("recetasApp")//todas las recetas
        } else {
            db.collection("recetasApp").whereEqualTo("categoria", normalizedCategory)//filtro de las recetas por categoria
        }

        // obtener los datos a tiempo real
        recetasListener = query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                println("Error al escuchar los cambios en Firestore: $exception")
                return@addSnapshotListener
            }

//actualizacion del estado de las recetasa
            if (snapshot != null) {
                recetas.value = snapshot.documents.map { document ->
                    val data = document.data ?: emptyMap()
                    data + ("id" to document.id)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Limpia el listener cuando se desmonta el Composable
            recetasListener?.remove()
        }
    }

//filtro para la búsqueda
    val recetasFiltradas = recetas.value.filter { receta ->
        val nombre = (receta["nombre"] as? String)?.lowercase() ?: ""
        val descripcion = (receta["descripcion"] as? String)?.lowercase() ?: ""
        val query = searchQuery.value.lowercase()
        nombre.contains(query) || descripcion.contains(query)
    }


    //función para eliminar la receta, solo quien crea la receta la puede eliminar
    fun eliminarReceta() {
        recetaAEliminar.value?.let { (id, receta) ->
            val creatorId = receta["creatorId"] as? String
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            if (creatorId == null) {
                println("ERROR: La receta no tiene un campo 'creatorId'.")
                return
            }

            if (currentUserId == null) {
                println("ERROR: El usuario no está autenticado.")
                return
            }
            //si el usuario es el qeu creo la receta se puede eliminar
            if (currentUserId == creatorId) {

                db.collection("recetasApp").document(id)
                    .delete()
                    .addOnSuccessListener {
                        println("Receta eliminada correctamente en Firestore: $id")
                        recetas.value = recetas.value.filter { it["id"] != id } // Actualiza la lista local.
                    }
                    .addOnFailureListener { exception ->
                        println("Error al eliminar la receta en Firestore: ${exception.message}")
                    }
            } else {
                //sino salta un mensaje de que no tienes permiso para eliminarla
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("No tienes permiso para eliminar esta receta.")
                }
                println("ERROR: El usuario autenticado no es el creador de la receta.")
            }
        } ?: run {
            println("ERROR: No se seleccionó ninguna receta para eliminar.")
        }

        // Cerrar el cuadro de diálogo
        showDialog.value = false
    }

    //---------------------------------------CODIGO------------------------------------------//


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Volver a la pantalla anterior",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            navController.navigate("ventCategorias")
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text("Buscar") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = categoria.uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

//si no hay recetas se lanza mensaje
            if (recetasFiltradas.isEmpty()) {
                Text("No se encontraron recetas.", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recetasFiltradas) { receta ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5E1))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                val id = receta["id"] as? String ?: ""
                                val nombre = receta["nombre"] as? String ?: "Sin nombre"
                                val descripcion = receta["descripcion"] as? String ?: "Sin descripción"
                                val dificultad = receta["dificultad"] as? String ?: ""
                                val tiempo = (receta["tiempo"] as? Number)?.toInt() ?: 0
                                val preparacion = receta["preparacion"] as? String ?: ""
                                val categoriaReceta = receta["categoria"] as? String ?: ""

                                Text(
                                    text = nombre,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = descripcion,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    //boton para ver la receta con todos sus campos
                                    Button(
                                        onClick = {
                                            //uso encoded para pasar los parametros con mayor seguridad
                                            val encodedNombre = Uri.encode(nombre)
                                            val encodedDescripcion = Uri.encode(descripcion)
                                            val encodedPreparacion = Uri.encode(preparacion)

                                            navController.navigate(
                                                "verReceta/$id/$encodedNombre/$encodedDescripcion/$dificultad/$tiempo/$encodedPreparacion/$categoriaReceta"
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3C79A)),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text("Ver")
                                    }
                                    //icono para eliminar una receta
                                    IconButton(onClick = {
                                        recetaAEliminar.value = id to receta
                                        showDialog.value = true
                                    }) {
                                        Image(
                                            painter = painterResource(id = R.drawable.contenedor),
                                            contentDescription = "Eliminar receta",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//botón flotante para que nos lleve a agregar una nueva receta
        FloatingActionButton(
            onClick = {
                navController.navigate("ventanaAgregar")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFE3C79A)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar receta"
            )
        }
//dialogo de confirmación eliminar una receta
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("¿Eliminar esta receta?") },
                text = { Text("Esta acción eliminará la receta tanto de la aplicación como de Firestore. ¿Deseas continuar?") },
                confirmButton = {
                    TextButton(onClick = { eliminarReceta() }) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("No")
                    }
                }
            )
        }
        //mensajes emergentes
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun Frame4Preview() {
    val navController = rememberNavController()
    Frame4(navController = navController, categoria = "Carnes")
}