package exercicis

import classesEmpleat.Adreca
import classesEmpleat.Empleat
import classesEmpleat.Telefon
import com.db4o.Db4oEmbedded
import exercicis.util.bd.GestionarRutesBD
import exercicis.util.bd.Ruta
import java.sql.DriverManager

fun main() {
    val bd = Db4oEmbedded.openFile ("Rutes.db4o")
    val routeList= GestionarRutesBD().llist()

    for (r in routeList){
        bd.store(r)
    }

    bd.close()
}