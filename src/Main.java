import algoritmos.Diferencial;
import algoritmos.Genetico;
import procesaFichero.Configurador;
import procesaFichero.LectorDatos;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        LectorDatos l = new LectorDatos("ch130.tsp");
        Configurador c = new Configurador(args[0]);

        Genetico g = new Genetico(l.getDistancias(),c,c.getPoblacion().get(0),c.getElite().get(1),c.getKbest().get(0),c.getCruces().get(0));

        System.out.println(g.ejecutar(77770715).getFitness());

        Diferencial d = new Diferencial(l.getDistancias(),c,100,"EDA");

        System.out.println(d.ejecutar(77770715).getFitness());


    }
}