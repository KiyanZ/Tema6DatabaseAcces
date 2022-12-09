package exercicis.util.bd

import exercicis.util.bd.Coordenades
import exercicis.util.bd.PuntGeo
import exercicis.util.bd.Ruta
import java.sql.Connection
import java.sql.DriverManager

class GestionarRutesBD {
    val con: Connection

    init {
        val url = "jdbc:sqlite:Rutes.sqlite"
        con = DriverManager.getConnection(url)

        val tableCreationStatement = con.createStatement()
        val rutesSentence = "CREATE TABLE IF NOT EXISTS RUTES( " +
                "num_r INTEGER CONSTRAINT cp_rutes PRIMARY KEY, " +
                "nom_r TEXT, " +
                "desn REAL, " +
                "des_ac REAL)"
        val puntsSentence = "CREATE TABLE IF NOT EXISTS PUNTS( " +
                "num_r INTEGER, " +
                "num_p INTEGER, " +
                "nom_p TEXT, " +
                "latitud REAL, " +
                "longitud REAL, " +
                "FOREIGN KEY (num_r) REFERENCES RUTES, " +
                "PRIMARY KEY (num_r, num_p)) "
        tableCreationStatement.executeUpdate(rutesSentence)
        tableCreationStatement.executeUpdate(puntsSentence)
    }

    fun close() {
        con.close()
    }

    private fun getMinID(): Int {
        val checkST = con.createStatement()
        val numRutaRes = checkST.executeQuery(
            "select coalesce(min(t.num_r) + 1, 0)\n" +
                    "from rutes t left outer join\n" +
                    "     rutes t2\n" +
                    "     on t.num_r  = t2.num_r - 1\n" +
                    "where t2.num_r is null;"
        )
        numRutaRes.next()
        val numRuta = numRutaRes.getInt(1)
        return numRuta
    }

    fun insert(r: Ruta) {
        var numRuta = getMinID()
        val st = con.createStatement()
        val updateRuta = "INSERT INTO RUTES VALUES ($numRuta, '${r.nom}', ${r.desnivell}, ${r.desnivellAcumulat})"
        println(updateRuta)
        st.executeUpdate(updateRuta)
        createPuntList(r, numRuta)
    }

    private fun createPuntList(r: Ruta, numRuta: Int) {
        val st = con.createStatement()
        r.llistaDePunts.forEachIndexed { numPunt, puntGeo ->
            val updatePuntGeo = "INSERT INTO PUNTS VALUES( " +
                    "$numRuta, " +
                    "$numPunt, " +
                    "'${puntGeo.nom}', " +
                    "${puntGeo.coord.latitud}, " +
                    "${puntGeo.coord.longitud} " +
                    ")"
            println(updatePuntGeo)
            st.executeUpdate(updatePuntGeo)
        }
    }

    private fun getPuntList(i: Int): MutableList<PuntGeo> {
        val puntsQuery = "SELECT p.nom_p, p.latitud, p.longitud \n" +
                "FROM RUTES r natural join  PUNTS p \n" +
                "WHERE r.num_r = $i"
        val llistapuntsRES = con.createStatement().executeQuery(puntsQuery)
        val llistapunts = mutableListOf<PuntGeo>()
        while (llistapuntsRES.next()) {
            val nom = llistapuntsRES.getString(1)
            val lat = llistapuntsRES.getDouble(2)
            val long = llistapuntsRES.getDouble(3)
            llistapunts.add(PuntGeo(nom, Coordenades(lat, long)))
        }
        return llistapunts
    }

    fun search(i: Int): Ruta {
        val rutaRES = con.createStatement().executeQuery("SELECT nom_r,desn,des_ac FROM RUTES r WHERE num_r = $i")
        rutaRES.next()
        val nom = rutaRES.getString(1)
        val desn = rutaRES.getInt(2)
        val des_ac = rutaRES.getInt(3)

        return Ruta(nom, desn, des_ac, getPuntList(i))
    }

    fun llist(): ArrayList<Ruta> {
        val rutaRES = con.createStatement().executeQuery("SELECT nom_r,desn,des_ac,num_r FROM RUTES r")
        val rutes = ArrayList<Ruta>()
        while (rutaRES.next()) {
            val nom = rutaRES.getString(1)
            val desn = rutaRES.getInt(2)
            val des_ac = rutaRES.getInt(3)
            val num = rutaRES.getInt(4)
            rutes.add(Ruta(nom, desn, des_ac, getPuntList(num)))
        }
        return rutes
    }

    fun getrutaID(r: Ruta): Int {
        val consulta =
            con.createStatement().executeQuery("SELECT COALESCE(MIN(num_r),-1) from RUTES WHERE nom_r == '${r.nom}'")
        consulta.next()
        return consulta.getInt(1)
    }

    fun save(r: Ruta) {
        val idRuta = getrutaID(r)
        //asumimos que no hay dos entradas con el mismo nombre
        if (idRuta == -1) {
            insert(r)
        } else {
            //eliminem els punts corresponents a aquesta ruta
            con.autoCommit = false
            deletePoints(idRuta)
            //canviem els valors
            val consulta = "UPDATE rutes " +
                    "SET desn = ${r.desnivell}, des_ac = ${r.desnivellAcumulat} " +
                    "WHERE nom_r == '${r.nom}'"
            con.createStatement().executeUpdate(consulta)
            //creem els punts
            createPuntList(r, idRuta)
            con.commit()
            con.autoCommit = true
        }
    }

    fun deletePoints(idRuta: Int) {
        con.createStatement().executeUpdate("DELETE FROM PUNTS WHERE num_r == $idRuta")
    }

    fun eraseRoute(idRuta: Int) {
        con.autoCommit = false
        deletePoints(idRuta)
        con.createStatement().executeUpdate("DELETE FROM RUTES WHERE num_r == $idRuta")
        con.commit()
        con.autoCommit = true
    }
}

