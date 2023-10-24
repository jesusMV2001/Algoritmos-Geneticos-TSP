import procesaFichero.LectorDatos;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        LectorDatos l = new LectorDatos("d15112.tsp");

        System.out.println(Arrays.deepToString(l.getCiudades()));

    }
}