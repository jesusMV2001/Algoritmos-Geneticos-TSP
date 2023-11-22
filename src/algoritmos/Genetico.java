package algoritmos;

import procesaFichero.Configurador;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Genetico extends Evolutivo {
    private final int numElite;
    private final int kbest;
    private List<Individuo> elite;
    private final String cruce;

    public Genetico(double[][] distancias, Configurador config, int poblacion, int nelite, int kbest, String cruce) {
        super(distancias,config,poblacion);
        this.elite=new ArrayList<>();
        this.numElite=nelite;
        this.kbest=kbest;
        this.cruce = cruce;
    }

    @Override
    public Individuo ejecutar(long semilla){
        Random random = new Random(semilla);
        //crea la poblacion incial y define los elites
        ArrayList<Individuo> padres = crearPoblacionInicial(random);
        evaluaciones+=tamPoblacion;


        Instant start=Instant.now();
        Instant end = Instant.now();

        while (evaluaciones<config.getEvaluaciones() && Duration.between(start,end).toMillis()<config.getLimiteSegundos()*1000 ) {
            //aumenta la generacion en 1
            generacion++;
            //encuentra elites de la poblacion de padres
            elite = buscaElites(padres);

            //Seleccion, cruce y mutacion
            ArrayList<Individuo> descendientes = cruceMutacion(seleccion(random,padres),random);

            //reemplazamiento
            if(descendientes.containsAll(elite))//si contiene todos los elite reemplaza al padre
                padres = descendientes;
            else //busca que elites no contiene y los añade mediante un torneo con kworst
                for (Individuo individuo : elite)
                    if (!descendientes.contains(individuo))
                        descendientes.set(descendientes.indexOf(torneo(random, descendientes, config.getKworst(), false)), individuo);

            end=Instant.now();
        }

        return buscaElites(padres).get(0);
    }

    private ArrayList<Individuo> cruceMutacion(ArrayList<Individuo> p, Random random){
        ArrayList<Individuo> cruzada = new ArrayList<>();
        if(Objects.equals(cruce, "OX2")){
            for(int i = 0; i<tamPoblacion ; i=i+2){

                //probabilidad de cruce
                if(random.nextDouble() < config.getProbCruce()){
                    Individuo sol1 = cruceOX2(p.get(i).getSolucion(),p.get(i+1).getSolucion(),random,i);
                    Individuo sol2 = cruceOX2(p.get(i+1).getSolucion(),p.get(i).getSolucion(),random,i+1);
                    cruzada.add(sol1);
                    cruzada.add(sol2);
                    evaluaciones+=2;
                }else{
                    Individuo p1 = new Individuo(p.get(i).getSolucion(),generacion,distancias,i);
                    Individuo p2 = new Individuo(p.get(i).getSolucion(),generacion,distancias,i+1);
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
                    cruzada.add(p1);
                    cruzada.add(p2);
                }
            }
        }else{//MOC
            //TODO MOC
        }

        return cruzada;
    }

    private List<Individuo> cruceMOC(Individuo p1, Individuo p2, Random random,int i){
        ArrayList<Integer> sol1 = new ArrayList<>();
        ArrayList<Integer> sol2 = new ArrayList<>();

        int puntoCorte = random.nextInt(p1.getSolucion().size());

        ArrayList<Integer> valoresP1 = new ArrayList<>(p1.getSolucion().subList(0,puntoCorte));
        ArrayList<Integer> valoresP2 = new ArrayList<>(p2.getSolucion().subList(0,puntoCorte));

        extracted(p1, p2, puntoCorte, valoresP2, sol1);
        extracted(p2, p1, puntoCorte, valoresP1, sol2);

        List<Individuo> devolver = new ArrayList<>();
        devolver.add(new Individuo(sol1,generacion,distancias,i));
        devolver.add(new Individuo(sol2,generacion,distancias,i+1));

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

    private ArrayList<Individuo> seleccion(Random random, ArrayList<Individuo> padres){
        ArrayList<Individuo> descendientes = new ArrayList<>();
        while (descendientes.size()<tamPoblacion){
            Individuo p1 = torneo(random, padres,kbest,true);
            Individuo p2;
            do {
                p2 = torneo(random, padres,kbest,true);
            } while (p1.equals(p2));
            descendientes.add(p1);
            descendientes.add(p2);
        }
        return descendientes;
    }

    private List<Individuo> buscaElites(ArrayList<Individuo> p){
        //TODO CAMBIAR Y QUE RECORRA LA LISTA Y CREAR NUEVOS INDIVIDUOS CON EL NEW NO GUARDAR UNA REFERENCIA
        //define elites
        Comparator<Individuo> comparador = Comparator.comparingDouble(Individuo::getFitness);
        ArrayList<Individuo> copiaArrayList = new ArrayList<>(p);
        copiaArrayList.sort(comparador);
        return copiaArrayList.subList(0,numElite);
    }

}
