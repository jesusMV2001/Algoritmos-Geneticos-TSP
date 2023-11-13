package algoritmos;

import procesaFichero.Configurador;

import java.util.*;

public class Genetico {
    private final double[][] distancias;
    private final Configurador config;
    private final int tamPoblacion;
    private int generacion;
    private final int tamSolucion;
    private final int numElite;
    private final int kbest;
    private int evaluaciones;
    private List<Individuo> elite;
    private final String cruce;

    public Genetico(double[][] distancias, Configurador config, int poblacion, int nelite, int kbest, String cruce) {
        this.evaluaciones=0;
        this.distancias = distancias;
        this.config = config;
        this.tamPoblacion=poblacion;
        this.generacion=0;
        this.tamSolucion=distancias.length;
        this.elite=new ArrayList<>();
        this.numElite=nelite;
        this.kbest=kbest;
        this.cruce = cruce;
    }

    public Individuo ejecutar(long semilla){
        Random random = new Random(semilla);

        //crea la poblacion incial y define los elites
        Poblacion padres = crearPoblacionInicial(random);
        evaluaciones+=tamPoblacion;

        while (evaluaciones<config.getEvaluaciones()) {
            generacion++;
            //encuentra elites
            elite = buscaElites(padres);

            //seleccion
            Poblacion descendientes = new Poblacion();
            while (descendientes.getPoblacion().size()<tamPoblacion) {
                Individuo p1 = torneo(random, padres,kbest,true);
                Individuo p2;
                do {
                    p2 = torneo(random, padres,kbest,true);
                } while (p1.equals(p2));


                //cruce y mutacion
                cruceMutacion(descendientes,random,p1,p2);
            }


            //reemplazamiento
            if(descendientes.getPoblacion().containsAll(elite)){
                padres = descendientes;
            }else{
                //se remplaza mediante torneo los peores por los elites
                for (Individuo individuo : elite) {
                    if (!descendientes.getPoblacion().contains(individuo)) {
                        int index = descendientes.getPoblacion().indexOf(torneo(random, descendientes, config.getKworst(), false));
                        descendientes.getPoblacion().set(index, individuo);
                    }
                }
            }
        }

        for (int i = 0; i < padres.getPoblacion().size(); i++) {
            System.out.println(padres.getPoblacion().get(i).getSolucion());
        }
        System.out.println(generacion);

        return buscaElites(padres).get(0);
    }

    private void cruceMutacion(Poblacion p, Random random, Individuo p1, Individuo p2){
        Individuo sol1 = new Individuo(p1.getSolucion(),p1.getGeneracion(),distancias);
        Individuo sol2 = new Individuo(p2.getSolucion(),p2.getGeneracion(),distancias);
        if(Objects.equals(cruce, "OX2")){
            double porciento = random.nextDouble();
            //probabilidad de cruce
            if(porciento < config.getProbCruce()){
                sol1 = cruceOX2(p1,p2,random);
                sol2 = cruceOX2(p2,p1,random);
                evaluaciones+=2;
            }
            //mutacion hijo1
            if(random.nextDouble()<config.getProbMutacion()){
                dosopt(sol1.getSolucion(),random);
                sol1.evaluar(distancias);
                evaluaciones++;
            }
            //mutacion hijo2
            if(random.nextDouble()<config.getProbMutacion()){
                dosopt(sol2.getSolucion(),random);
                sol2.evaluar(distancias);
                evaluaciones++;
            }
        }else{
            //TODO
        }

        p.addIndividuo(sol1);
        p.addIndividuo(sol2);
    }

    private Individuo cruceOX2(Individuo p1, Individuo p2, Random random){
        List<Integer> valores = new ArrayList<>();
        for (int i = 0; i < p1.getSolucion().size(); i++) {
            if(random.nextDouble()<config.getProbSeleccionOX2()){
                valores.add(p1.getSolucion().get(i));
            }
        }
        ArrayList<Integer> solp2 = p2.getSolucion();

        int pos=0;
        for (int i = 0; i < solp2.size(); i++) {
            if(valores.contains(solp2.get(i))){
                solp2.set(i,valores.get(pos));
                pos++;
            }
        }


        return new Individuo(solp2,generacion,distancias);
    }
    private void dosopt (ArrayList<Integer> sol,Random random){
        int pos1=random.nextInt(0,sol.size());
        int pos2;
        do{
            pos2=random.nextInt(0,sol.size());
        }while (pos1==pos2);
        int aux = sol.get(pos2);
        sol.set(pos2,sol.get(pos1));
        sol.set(pos1,aux);
    }

    private Individuo torneo(Random random, Poblacion p, int k,boolean buscaMejor){
        int pos = -1;
        if(buscaMejor) {
            double mejor = Double.MAX_VALUE;
            for (int i = 0; i < k; i++) {
                int r = random.nextInt(0, tamPoblacion);
                if (mejor > p.getPoblacion().get(r).getFitness()) {
                    mejor = p.getPoblacion().get(r).getFitness();
                    pos = r;
                }
            }
        }else{
            double peor = Double.MIN_VALUE;
            for (int i = 0; i < k; i++) {
                int r = random.nextInt(0, tamPoblacion);
                if (peor < p.getPoblacion().get(r).getFitness()) {
                    peor = p.getPoblacion().get(r).getFitness();
                    pos = r;
                }
            }
        }

        return p.getPoblacion().get(pos);
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



        return pInicial;
    }

    private List<Individuo> buscaElites(Poblacion p){
        //define elites
        Comparator<Individuo> comparador = Comparator.comparingDouble(Individuo::getFitness);
        ArrayList<Individuo> copiaArrayList = new ArrayList<>(p.getPoblacion());
        copiaArrayList.sort(comparador);
        return copiaArrayList.subList(0,numElite);
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
