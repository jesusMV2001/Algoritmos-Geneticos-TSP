package algoritmos;

import netscape.javascript.JSObject;
import procesaFichero.Configurador;

import java.util.*;

public abstract class Evolutivo {

    public ArrayList<String> logs;
    protected int tamPoblacion;
    protected Configurador config;
    protected  double[][] distancias;
    protected int evaluaciones;
    protected int generacion;
    protected int tamSolucion;

    public Evolutivo(double[][] distancias, Configurador config, int poblacion){
        this.logs = new ArrayList<>();
        this.config=config;
        this.generacion=0;
        this.evaluaciones=0;
        this.distancias=distancias;
        this.tamPoblacion=poblacion;
        this.tamSolucion=distancias.length;
    }

    public abstract Individuo ejecutar(long semilla);

    public abstract void limpiar();

    protected Individuo cruceOX2(ArrayList<Integer> p1, ArrayList<Integer> p2, Random random, int indice){
        List<Integer> valores = new ArrayList<>();
        for (Integer integer : p1)
            if (random.nextDouble() < config.getProbSeleccionOX2())
                valores.add(integer);

        ArrayList<Integer> solp2 = new ArrayList<>(p2);

        int pos=0;
        for (int i = 0; i < solp2.size(); i++)
            if(valores.contains(solp2.get(i)))
                solp2.set(i,valores.get(pos++));

        //compueba si se hace mutacion
        if(random.nextDouble()<config.getProbMutacion())
            dosopt(solp2,random);


        return new Individuo(solp2,generacion,distancias,indice);
    }

    protected void dosopt (ArrayList<Integer> sol,Random random){
        int pos1=random.nextInt(0,sol.size());
        int pos2=random.nextInt(0,sol.size());
        int aux = sol.get(pos2);
        sol.set(pos2,sol.get(pos1));
        sol.set(pos1,aux);
    }

    protected abstract String crearJSON(ArrayList<Individuo> p);

    protected Individuo torneo(Random random, ArrayList<Individuo> p, int k,boolean buscaMejor){
        int pos = -1;

        if(buscaMejor) {
            double mejor = Double.MAX_VALUE;
            for (int i = 0; i < k; i++) {
                int r = random.nextInt(0, tamPoblacion);
                if (mejor > p.get(r).getFitness()) {
                    mejor = p.get(r).getFitness();
                    pos = r;
                }
            }
        }else{
            double peor = Double.MIN_VALUE;
            for (int i = 0; i < k; i++) {
                int r = random.nextInt(0, tamPoblacion);
                if (peor < p.get(r).getFitness()) {
                    peor = p.get(r).getFitness();
                    pos = r;
                }
            }
        }

        return p.get(pos);
    }

    protected ArrayList<Individuo> crearPoblacionInicial(Random random){
        ArrayList<Individuo> pInicial = new ArrayList<>();

        //Aleatorio
        for (int i = 0; i <config.getGeneracionAleatoria()*tamPoblacion; i++)
            pInicial.add(crearIndividuoAleatorio(random,i));


        //Greedy
        while(pInicial.size() != tamPoblacion)
            pInicial.add(crearIndividuoGreedy(random,pInicial.size()));


        return pInicial;
    }

    protected Individuo crearIndividuoAleatorio(Random random, int indice){
        ArrayList<Integer> lista=new ArrayList<>();
        for (int i = 0; i < tamSolucion; i++)
            lista.add(i);

        ArrayList<Integer> solucion = new ArrayList<>();

        for (int i = 0; i < tamSolucion; i++) {
            int j=random.nextInt(0,lista.size());
            solucion.add(lista.get(j));
            lista.remove(j);
        }

        return new Individuo(solucion,generacion,distancias,indice);
    }

    protected Individuo crearIndividuoGreedy(Random random, int indice){
        ArrayList<Integer> solucion = new ArrayList<>();
        HashSet<Integer> indicesSeleccionados = new HashSet<>();

        int inicio=random.nextInt(0,tamSolucion);

        Map<Integer, Double> mejores = new HashMap<>();
        for (int index = inicio; index < tamSolucion+inicio; index++) {
            int i = index % tamSolucion;
            mejores.clear();
            for (int j = 0; j < distancias[i].length; j++) {
                if (!indicesSeleccionados.contains(j)) {
                    mejores.put(j, distancias[i][j]);
                }
            }

            ArrayList<Integer> mapaOrdenado = ordenarMapa(mejores);
            int seleccionado = mapaOrdenado.get(random.nextInt(Math.min(config.getGreedy(), mapaOrdenado.size())));
            solucion.add(seleccionado);
            indicesSeleccionados.add(seleccionado);

        }
        return new Individuo(solucion,generacion,distancias, indice);
    }

    protected static ArrayList<Integer> ordenarMapa(Map<Integer, Double> mapa) {
        List<Map.Entry<Integer, Double>> Ordenados = mapa.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();


        ArrayList<Integer> r = new ArrayList<>();
        for (Map.Entry<Integer, Double> flujosOrdenado : Ordenados)
            r.add(flujosOrdenado.getKey());

        return r;
    }

}
