package pe.edu.idat.idatparking

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pe.edu.idat.idatparking.adapter.MovimientoAdapter
import pe.edu.idat.idatparking.repository.MovimientoRepository

class HistorialActivity : AppCompatActivity() {

    private lateinit var movimientoRepository:
            MovimientoRepository

    private lateinit var movimientoAdapter:
            MovimientoAdapter

    private lateinit var txtResumen: TextView
    private lateinit var txtSinMovimientos: TextView
    private lateinit var rvHistorial: RecyclerView
    private lateinit var btnVolver: Button

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        movimientoRepository =
            MovimientoRepository(this)

        txtResumen =
            findViewById(R.id.txtResumenHistorial)

        txtSinMovimientos =
            findViewById(R.id.txtSinMovimientos)

        rvHistorial =
            findViewById(R.id.rvHistorial)

        btnVolver =
            findViewById(R.id.btnVolverHistorial)

        movimientoAdapter =
            MovimientoAdapter(emptyList())

        rvHistorial.layoutManager =
            LinearLayoutManager(this)

        rvHistorial.adapter =
            movimientoAdapter

        btnVolver.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarInformacion()
    }

    private fun cargarInformacion() {

        val estadistica =
            movimientoRepository.obtenerEstadistica()

        txtResumen.text = """
            Capacidad total: ${estadistica.capacidadTotal}
            Vehículos dentro: ${estadistica.ocupados}
            Espacios disponibles: ${estadistica.disponibles}
        """.trimIndent()

        val movimientos =
            movimientoRepository.listarHistorial()

        movimientoAdapter.actualizarLista(
            movimientos
        )

        if (movimientos.isEmpty()) {
            txtSinMovimientos.visibility =
                View.VISIBLE

            rvHistorial.visibility =
                View.GONE
        } else {
            txtSinMovimientos.visibility =
                View.GONE

            rvHistorial.visibility =
                View.VISIBLE
        }
    }
}