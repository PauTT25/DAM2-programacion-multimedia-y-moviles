package com.example.listacompra

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.listacompra.ui.theme.ListaCompraTheme

private const val TAG = "ListaCompra"

data class Producto(
    val id: Int,
    val nombre: String,
    val comprado: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListaCompraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListaCompraScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ListaCompraScreen(modifier: Modifier = Modifier) {
    val productos = remember { mutableStateListOf<Producto>() }
    var nombreNuevo by remember { mutableStateOf("") }
    var siguienteId by remember { mutableIntStateOf(1) }

    val pendientes = productos.count { !it.comprado }
    val margen = dimensionResource(R.dimen.margin_medium)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(margen)
    ) {
        // Contador (plurals)
        Text(
            text = pluralStringResource(R.plurals.pending_products, pendientes, pendientes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.margin_small)))

        // Fila de entrada
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_small))
        ) {
            OutlinedTextField(
                value = nombreNuevo,
                onValueChange = { nombreNuevo = it },
                label = { Text(stringResource(R.string.product_name_label)) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    val nombre = nombreNuevo.trim()
                    if (nombre.isNotEmpty()) {
                        productos.add(Producto(id = siguienteId++, nombre = nombre))
                        Log.d(TAG, "Producto añadido: $nombre")
                        nombreNuevo = ""
                    }
                }
            ) {
                Text(stringResource(R.string.add_button))
            }
        }

        Spacer(Modifier.height(margen))

        // Lista o mensaje de lista vacía
        if (productos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_list_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_small))
            ) {
                items(productos, key = { it.id }) { producto ->
                    ProductoCard(
                        producto = producto,
                        onToggle = {
                            val index = productos.indexOfFirst { it.id == producto.id }
                            if (index != -1) {
                                productos[index] = producto.copy(comprado = !producto.comprado)
                            }
                        },
                        onDelete = {
                            productos.remove(producto)
                            Log.d(TAG, "Producto eliminado: ${producto.nombre}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val margen = dimensionResource(R.dimen.margin_medium)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(margen)) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(end = dimensionResource(R.dimen.icon_size) * 2)
            )

            // Icono de estado en la esquina superior derecha
            Icon(
                imageVector = if (producto.comprado) Icons.Filled.CheckCircle else Icons.Filled.ShoppingCart,
                contentDescription = stringResource(
                    if (producto.comprado) R.string.cd_bought else R.string.cd_pending
                ),
                tint = if (producto.comprado) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(dimensionResource(R.dimen.icon_size))
            )

            // Botón de eliminar en la esquina inferior derecha
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListaCompraPreview() {
    ListaCompraTheme {
        ListaCompraScreen()
    }
}