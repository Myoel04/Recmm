package com.example.proyecto_after_aplicacion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun Frame3(modifier: Modifier = Modifier, navController: NavController) {

    //---------------------------------------VARIABLES------------------------------------------//

    val contexto = LocalContext.current
    val sharedPreferences = contexto.getSharedPreferences("AppPrefs", 0) // Acceso a SharedPreferences.

    //---------------------------------------CODIGO------------------------------------------//


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 8.dp)
            .padding(top = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // el ícono de cerrar sesión y el título.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp), //espacio entre icono texto
                verticalAlignment = Alignment.CenterVertically
            ) {
//icono de cerrar sesion
                IconButton(
                    onClick = {
                        // cierra la sesión, cambia a false y nos manda al login
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                        navController.navigate("ventanaLogin") {
                            popUpTo("ventCategorias") { inclusive = true }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cerrar), // Icono "cerrar".
                        contentDescription = "Cerrar Sesión",
                        modifier = Modifier.size(30.dp) // Tamaño ajustado.
                    )
                }

                // Título alineado a la derecha del ícono.
                Text(
                    text = "Categorías de Comida",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(start = 37.dp) // Espacio entre el ícono y el texto.
                )
            }

            // las categorias de comida
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    FoodItem(
                        imageResource = R.drawable.pizza1,
                        text = "Comida Rápida",
                        onClick = { navController.navigate("ventanaRecetas/Comida Rápida") }
                    )
                }
                item {
                    FoodItem(
                        imageResource = R.drawable.marisco,
                        text = "Mariscos",
                        onClick = { navController.navigate("ventanaRecetas/Mariscos") }
                    )
                }
                item {
                    FoodItem(
                        imageResource = R.drawable.pasta,
                        text = "Pasta",
                        onClick = { navController.navigate("ventanaRecetas/Pasta") }
                    )
                }
                item {
                    FoodItem(
                        imageResource = R.drawable.carnes,
                        text = "Carnes",
                        onClick = { navController.navigate("ventanaRecetas/Carnes") }
                    )
                }
                item {
                    FoodItem(
                        imageResource = R.drawable.postres,
                        text = "Postres",
                        onClick = { navController.navigate("ventanaRecetas/Postres") }
                    )
                }
                item {
                    FoodItem(
                        imageResource = R.drawable.sopas,
                        text = "Sopas",
                        onClick = { navController.navigate("ventanaRecetas/Sopas") }
                    )
                }
                item {
                    FoodItem(
                        imageResource = R.drawable.comidalatina,
                        text = "Comida Latina",
                        onClick = { navController.navigate("ventanaRecetas/Comida Latina") }
                    )
                }
                item {
                    FoodItem(
                        imageResource = R.drawable.ensaladas,
                        text = "Ensaladas",
                        onClick = { navController.navigate("ventanaRecetas/Ensaladas") }
                    )
                }
            }

            // boton para ver todas las recetas sin filtro
            Button(
                onClick = { navController.navigate("ventanaRecetas/todas") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAB9))
            ) {
                Text(
                    text = "VER TODAS LAS RECETAS",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


//---------------------------------------CONTROLADOR CARD------------------------------------------//


//controla los card como interactivos
@Composable
fun FoodItem(imageResource: Int, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)//hace q sea clickable la imagen
            .fillMaxWidth()
    ) {
        //propiedades de los card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDAB9)),
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
        ) {
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = text,
                modifier = Modifier.fillMaxSize()
            )
        }
        //espacio entre imagen/texto
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            color = Color.Black,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(widthDp = 404, heightDp = 788, showSystemUi = true)
@Composable
private fun Frame3Preview() {
    val navController = rememberNavController()
    Frame3(modifier = Modifier, navController = navController)
}