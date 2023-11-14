package algoritmos;

import procesaFichero.Configurador;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Genetico  {
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


        Instant start=Instant.now();
        Instant end = Instant.now();

        while (evaluaciones<config.getEvaluaciones() && Duration.between(start,end).toMillis()<config.getLimiteSegundos()*1000 ) { //TODO añadir comprobacion de 60 segundos
            //aumenta la generacion en 1
            generacion++;
            //encuentra elites de la poblacion de padres
            elite = buscaElites(padres);

            //Seleccion, cruce y mutacion
            Poblacion descendientes = cruceMutacion(seleccion(random,padres),random);

            //reemplazamiento
            if(descendientes.getPoblacion().containsAll(elite))//si contiene todos los elite reemplaza al padre
                padres = descendientes;
            else //busca que elites no contiene y los añade mediante un torneo con kworst
                for (Individuo individuo : elite)
                    if (!descendientes.getPoblacion().contains(individuo))
                        descendientes.getPoblacion().set(descendientes.getPoblacion().indexOf(torneo(random, descendientes, config.getKworst(), false)), individuo);

            end=Instant.now();
        }

        ArrayList<Integer> a = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            a.add(i);
        }

        ArrayList<Integer> b = new ArrayList<>();
        b.add(9);b.add(3);b.add(7);b.add(8);b.add(2);b.add(6);b.add(5);b.add(1);b.add(4);

        Individuo p1 = new Individuo(a,generacion,distancias);
        Individuo p2 = new Individuo(b,generacion,distancias);
        List<Individuo> l= cruceMOC(p1,p2,random);
        System.out.println(l.get(0).getSolucion());

        return buscaElites(padres).get(0);
    }

    private Poblacion cruceMutacion(Poblacion p, Random random){
        Poblacion cruzada = new Poblacion();
        if(Objects.equals(cruce, "OX2")){
            for(int i = 0; i<tamPoblacion ; i=i+2){

                //probabilidad de cruce
                if(random.nextDouble() < config.getProbCruce()){
                    Individuo sol1 = cruceOX2(p.getIndividuo(i),p.getIndividuo(i+1),random);
                    Individuo sol2 = cruceOX2(p.getIndividuo(i+1),p.getIndividuo(i),random);
                    cruzada.addIndividuo(sol1);
                    cruzada.addIndividuo(sol2);
                    evaluaciones+=2;
                }else{
                    Individuo p1 = new Individuo(p.getIndividuo(i).getSolucion(),generacion,distancias);
                    Individuo p2 = new Individuo(p.getIndividuo(i).getSolucion(),generacion,distancias);
                    //se hace la probabilidad de mutacion para los dos padres
                    if(random.nextDouble()<config.getProbMutacion()){
                        dosopt(p1.getSolucion(),random);
                        p1.evaluar(distancias);
                        evaluaciones++;
                    }
                    if(random.nextDouble()<config.getProbMutacion()){
                        dosopt(p2.getSolucion(),random);
                        p2.evaluar(distancias);
                        evaluaciones++;
                    }
                    cruzada.addIndividuo(p1);
                    cruzada.addIndividuo(p2);
                }
            }
        }else{//MOC
            //TODO MOC
        }

        return cruzada;
    }

    private List<Individuo> cruceMOC(Individuo p1, Individuo p2, Random random){
        ArrayList<Integer> sol1 = new ArrayList<>();
        ArrayList<Integer> sol2 = new ArrayList<>();

        int puntoCorte = random.nextInt(p1.getSolucion().size());

        ArrayList<Integer> valoresP1 = new ArrayList<>(p1.getSolucion().subList(0,puntoCorte));
        ArrayList<Integer> valoresP2 = new ArrayList<>(p2.getSolucion().subList(0,puntoCorte));

        extracted(p1, p2, puntoCorte, valoresP2, sol1);
        extracted(p2, p1, puntoCorte, valoresP1, sol2);

        List<Individuo> devolver = new ArrayList<>();
        devolver.add(new Individuo(sol1,generacion,distancias));
        devolver.add(new Individuo(sol2,generacion,distancias));

        return devolver;
    }

    private static void extracted(Individuo p1, Individuo p2, int puntoCorte, ArrayList<Integer> valoresP2, ArrayList<Integer> sol1) {
        int pos= puntoCorte;

        for (int i = 0; i < p1.getSolucion().size(); i++)
            if(!valoresP2.contains(p1.getSolucion().get(i)))
                sol1.add(p2.getSolucion().get(pos++));
            else
                sol1.add(p1.getSolucion().get(i));
    }

    private Individuo cruceOX2(Individuo p1, Individuo p2, Random random){
        List<Integer> valores = new ArrayList<>();
        for (int i = 0; i < p1.getSolucion().size(); i++)
            if(random.nextDouble()<config.getProbSeleccionOX2())
                valores.add(p1.getSolucion().get(i));

        ArrayList<Integer> solp2 = new ArrayList<>(p2.getSolucion());

        int pos=0;
        for (int i = 0; i < solp2.size(); i++)
            if(valores.contains(solp2.get(i)))
               solp2.set(i,valores.get(pos++));


        if(random.nextDouble()<config.getProbMutacion())
            dosopt(solp2,random);


        return new Individuo(solp2,generacion,distancias);
    }

    private Poblacion seleccion(Random random, Poblacion padres){
        Poblacion descendientes = new Poblacion();
        while (descendientes.getPoblacion().size()<tamPoblacion){
            Individuo p1 = torneo(random, padres,kbest,true);
            Individuo p2;
            do {
                p2 = torneo(random, padres,kbest,true);
            } while (p1.equals(p2));
            descendientes.addIndividuo(p1);
            descendientes.addIndividuo(p2);
        }
        return descendientes;
    }
    private void dosopt (ArrayList<Integer> sol,Random random){
        int pos1=random.nextInt(0,sol.size());
        int pos2=random.nextInt(0,sol.size());
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
                if (mejor > p.getIndividuo(r).getFitness()) {
                    mejor = p.getIndividuo(r).getFitness();
                    pos = r;
                }
            }
        }else{
            double mejor = Double.MIN_VALUE;
            for (int i = 0; i < k; i++) {
                int r = random.nextInt(0, tamPoblacion);
                if (mejor < p.getIndividuo(r).getFitness()) {
                    mejor = p.getIndividuo(r).getFitness();
                    pos = r;
                }
            }
        }

        return p.getIndividuo(pos);
    }

    private Poblacion crearPoblacionInicial(Random random){
        Poblacion pInicial = new Poblacion();

        //Aleatorio
        for (int i = 0; i <config.getGeneracionAleatoria()*tamPoblacion; i++)
            pInicial.addIndividuo(crearIndividuoAleatorio(random));


        //Greedy
        while(pInicial.getPoblacion().size() != tamPoblacion)
            pInicial.addIndividuo(crearIndividuoGreedy(random));


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
        for (int i = 0; i < tamSolucion; i++)
            lista.add(i);

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
        return new Individuo(solucion,generacion,distancias);
    }

    private static ArrayList<Integer> ordenarMapa(Map<Integer, Double> mapa) {
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
