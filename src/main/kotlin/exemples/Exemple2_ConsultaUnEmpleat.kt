package exemples

import com.db4o.Db4oEmbedded
import com.db4o.ObjectSet
import classesEmpleat.Empleat

fun main() {
    val bd = Db4oEmbedded.openFile("Empleats.db4o")
    val patro = Empleat()
    val llista = bd.queryByExample<Empleat>(patro)

    for(e in llista){
        println(
            "Nif: " + e.nif + ". Nom: " + e.nom + ". Població: " + e.adreca?.poblacio
        )
        if (e.correus_e != null)
            print("Primer correu: " + e.correus_e?.get(0) + ".")

        if (e.telefons != null)
            print("Primer telèfon: " + e.telefons!![0].numero + ".")
        println()
    }
    bd.close();
}