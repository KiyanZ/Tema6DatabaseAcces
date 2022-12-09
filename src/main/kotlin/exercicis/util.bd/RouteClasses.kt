package exercicis.util.bd

import java.io.Serializable

class Coordenades(val latitud: Double, val longitud: Double) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1
    }
}

class PuntGeo(var nom: String, var coord: Coordenades) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1
    }
}

class Ruta(
    var nom: String?,
    var desnivell: Int?,
    var desnivellAcumulat: Int?,
    var llistaDePunts: MutableList<PuntGeo> = mutableListOf<PuntGeo>()
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 1
    }

    fun addPunt(p: PuntGeo) {
        llistaDePunts.add(p)
    }

    fun getPunt(i: Int): PuntGeo {
        return llistaDePunts[i]
    }

    fun getPuntNom(i: Int): String {
        return llistaDePunts[i].nom
    }

    fun getPuntLatitud(i: Int): Double {
        return llistaDePunts[i].coord.latitud
    }

    fun getPuntLongitud(i: Int): Double {
        return llistaDePunts[i].coord.longitud
    }

    fun size(): Int {
        return llistaDePunts.size
    }

    fun mostrarRuta() {
        // Aquest és el mètode que heu d'implementar vosaltres
        println("Ruta: " + this.nom)
        println("Desnivell: " + this.desnivell)
        println("Desnivell acumulat: " + this.desnivellAcumulat)
        val numPoints = this.llistaDePunts.size
        println("Te $numPoints punts")
        for (e in this.llistaDePunts) {
            println("${e.nom} (${e.coord.latitud},- ${e.coord.longitud})")
        }
        println()
    }
}
