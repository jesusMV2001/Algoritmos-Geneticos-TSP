import algoritmos.Genetico;
import procesaFichero.Configurador;
import procesaFichero.LectorDatos;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        LectorDatos l = new LectorDatos("pr144.tsp");
        Configurador c = new Configurador(args[0]);

        Genetico g = new Genetico(l.getDistancias(),l.getCiudades(),c,c.getPoblacion().get(0));

        g.ejecutar(2);

        //System.out.println(Arrays.deepToString(l.getDistancias()));

    }
}