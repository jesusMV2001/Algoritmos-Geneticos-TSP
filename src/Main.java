import algoritmos.Genetico;
import procesaFichero.Configurador;
import procesaFichero.LectorDatos;

public class Main {
    public static void main(String[] args) {
        LectorDatos l = new LectorDatos("d15112.tsp");
        Configurador c = new Configurador(args[0]);

        Genetico g = new Genetico(l.getDistancias(),c,c.getPoblacion().get(0),c.getElite().get(1),c.getKbest().get(0),c.getCruces().get(0));

        System.out.println(g.ejecutar(2).getFitness());

        //System.out.println(Arrays.deepToString(l.getDistancias()));

    }
}