package algoritmos;

import procesaFichero.Configurador;

import java.util.*;

public class Genetico {
    private double[][] distancias;
    private double[][] ciudades;
    private Configurador config;
    private int tamPoblacion;
    private int generacion;
    private int tamSolucion;

    public Genetico(double[][] distancias, double[][] ciudades, Configurador config, int poblacion) {
        this.distancias = distancias;
        this.ciudades = ciudades;
        this.config = config;
        this.tamPoblacion=poblacion;
        this.generacion=0;
        this.tamSolucion=distancias.length;
    }

    public int[] ejecutar(long semilla){
        Random random = new Random(semilla);
        Poblacion poblacionInicial = crearPoblacionInicial(random);



        System.out.println(poblacionInicial.getPoblacion().size());

        return null;
    }

    private Poblacion crearPoblacionInicial(Random random){
        Poblacion pInicial = new Poblacion();

        //Aleatorio
        for (int i = 0; i <config.getGeneracionAleatoria()*tamPoblacion; i++) {
            pInicial.addIndividuo(crearIndividuoAleatorio(random));
        }

        //Greedy
        while(pInicial.getPoblacion().size() != tamPoblacion){
            pInicial.addIndividuo(crearIndividuoGreedy(random));
        }

        for (int i = 0; i < pInicial.getPoblacion().size(); i++) {
            System.out.println(pInicial.getPoblacion().get(i).getFitness());
        }

        return pInicial;
    }

    private Individuo crearIndividuoAleatorio(Random random){
        ArrayList<Integer> lista=new ArrayList<>();
        for (int i = 0; i < tamSolucion; i++) {
            lista.add(i);
        }
        ArrayList<Integer> solucion = new ArrayList<>();

        for (int i = 0; i < tamSolucion; i++) {
            int j=random.nextInt(0,lista.size());
            solucion.add(lista.get(j));
            lista.remove(j);
        }

        return new Individuo(solucion,generacion,distancias);
    }

    private Individuo crearIndividuoGreedy(Random random){
        ArrayList<Integer> solucion = new ArrayList<>();

        for (int i = 0; i < tamSolucion; i++) {
            HashMap<Integer, Double> mejores = new HashMap<>();
            for (int j = 0; j < distancias[i].length; j++) {
                mejores.put(j,distancias[i][j]);
            }
            ArrayList<Integer> mapaOrdenado = ordenarMapa(mejores,solucion);
            solucion.add(mapaOrdenado.get(random.nextInt(0,Math.min(config.getGreedy(),mapaOrdenado.size()))));
        }

        return new Individuo(solucion,generacion,distancias);
    }

    private static ArrayList<Integer> ordenarMapa(Map<Integer, Double> mapa, ArrayList<Integer> sol) {
        List<Map.Entry<Integer, Double>> Ordenados = mapa.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();


        ArrayList<Integer> r = new ArrayList<>();
        for (Map.Entry<Integer, Double> flujosOrdenado : Ordenados) {
            if(sol.stream().noneMatch(n-> Objects.equals(n, flujosOrdenado.getKey())))
                r.add(flujosOrdenado.getKey());
        }
        return r;
    }

}
