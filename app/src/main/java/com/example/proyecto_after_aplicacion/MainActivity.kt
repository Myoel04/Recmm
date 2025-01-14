package com.example.proyecto_after_aplicacion

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto_after_aplicacion.ui.theme.Proyecto_after_AplicacionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_after_AplicacionTheme {
                val contNavegador = rememberNavController()
//controlar las rutas y ventanaas de la app
                NavHost(navController = contNavegador, startDestination = "ventanaLogin") {

                    //definimos las ventanas
                    composable("ventanaLogin") { Frame1(contNavegador = contNavegador) }

                    composable("ventana2") { ventana2().Frame2(contNavegador = contNavegador) }

                    composable("ventCategorias") { Frame3(navController = contNavegador) }

                    composable(//definimos cogiendo la categoria de las recetas
                        "ventanaRecetas/{categoria}",
                        arguments = listOf(navArgument("categoria") { type = NavType.StringType })
                    ) { backStackEntry ->//obtengo la categoria
                        val categoria = backStackEntry.arguments?.getString("categoria") ?: "Todas"
                        Frame4(navController = contNavegador, categoria = categoria)
                    }

                    composable("ventanaAgregar") { Frame5(navController = contNavegador) }
                    //defino una ventana que acoge todos los argumentos declarados

                    composable(
                        "verReceta/{id}/{nombre}/{descripcion}/{dificultad}/{tiempo}/{preparacion}/{categoria}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.StringType },
                            navArgument("nombre") { type = NavType.StringType },
                            navArgument("descripcion") { type = NavType.StringType },
                            navArgument("dificultad") { type = NavType.StringType },
                            navArgument("tiempo") { type = NavType.IntType },
                            navArgument("preparacion") { type = NavType.StringType },
                            navArgument("categoria") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        //aqui obtiene los argumentos de la ventana anterior
                        val id = backStackEntry.arguments?.getString("id") ?: ""
                        val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre") ?: "")
                        val descripcion = Uri.decode(backStackEntry.arguments?.getString("descripcion") ?: "")
                        val dificultad = backStackEntry.arguments?.getString("dificultad") ?: ""
                        val tiempo = backStackEntry.arguments?.getInt("tiempo") ?: 0
                        val preparacion = Uri.decode(backStackEntry.arguments?.getString("preparacion") ?: "")
                        val categoria = backStackEntry.arguments?.getString("categoria") ?: ""
//llamamos a la ventana y le pasamos los argumentos
                        Frame6(
                            navController = contNavegador,
                            recetaId = id,
                            nombre = nombre,
                            descripcionInicial = descripcion,
                            dificultadInicial = dificultad,
                            tiempoInicial = tiempo,
                            preparacionInicial = preparacion,
                            categoriaInicial = categoria
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMainActivity() {
    Proyecto_after_AplicacionTheme {
        val navController = rememberNavController()
        Frame1(contNavegador = navController)
    }
}