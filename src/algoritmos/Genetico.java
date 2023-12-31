package algoritmos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import procesaFichero.Configurador;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

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
        StringBuilder log= new StringBuilder();

        Instant start=Instant.now();
        Instant end = Instant.now();

        while (evaluaciones<config.getEvaluaciones() && Duration.between(start,end).toMillis()<config.getLimiteSegundos()*1000 ) {
            log.append("Generacion actual: ").append(generacion).append("\n").append("Evaluaciones acumuladas: ").append(evaluaciones).append("\n").append("Elites: \n");
            //aumenta la generacion en 1
            generacion++;
            //encuentra elites de la poblacion de padres
            elite = buscaElites(padres);


            //Seleccion, cruce y mutacion
            ArrayList<Individuo> descendientes = cruceMutacion(seleccion(random,padres),random);

            //comprueba si estan los elites, sino los añade a la poblacion
            for (Individuo individuo : elite)
                if (!descendientes.contains(individuo)) {
                    int index = torneo(random, descendientes, config.getKworst(), false).getIndice();
                    individuo.setIndice(index);
                    descendientes.set(index, individuo);
                }

            //reemplaza la poblacion de padres
            padres = descendientes;

            if(config.isMostrarPoblacionCompletaLogs())
                log.append(crearJSON(padres));
            else
                log.append(crearJSON((ArrayList<Individuo>) elite));
            logs.add(log.toString());
            log = new StringBuilder();

            end=Instant.now();
        }

        return elite.get(0);
    }

    @Override
    protected String crearJSON(ArrayList<Individuo> p){
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        // Crear un filtro dinámico
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        if (!config.isIncluirSolucionLogs())
            filterProvider.addFilter("dynamicFilter",
                    SimpleBeanPropertyFilter.serializeAllExcept("solucion"));
        else
            filterProvider.addFilter("dynamicFilter",
                    SimpleBeanPropertyFilter.serializeAll());

        try {
            objectMapper.setFilterProvider(filterProvider);
            return objectMapper.writeValueAsString(p);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void limpiar() {
        elite.clear();
        logs.clear();
        evaluaciones=0;
        generacion=0;
    }

    private ArrayList<Individuo> cruceMutacion(ArrayList<Individuo> p, Random random){
        ArrayList<Individuo> cruzada = new ArrayList<>();
        if(Objects.equals(cruce, "OX2")){
            for(int i = 0; i<tamPoblacion ; i=i+2){

                //probabilidad de cruce
                if(random.nextDouble() < config.getProbCruce()){
                    //se crean los dos hijos
                    Individuo sol1 = cruceOX2(p.get(i).getSolucion(),p.get(i+1).getSolucion(),random,i);
                    Individuo sol2 = cruceOX2(p.get(i+1).getSolucion(),p.get(i).getSolucion(),random,i+1);
                    cruzada.add(sol1);
                    cruzada.add(sol2);
                    evaluaciones+=2;
                }else{
                    mutaPadres(p.get(i),p.get(i+1),random);
                    cruzada.add(p.get(i));
                    cruzada.add(p.get(i+1));
                }
            }
        }else{//MOC
            for(int i = 0; i<tamPoblacion ; i=i+2) {
                //probabilidad de cruce
                if (random.nextDouble() < config.getProbCruce()) {
                    List<Individuo> sol1 = cruceMOC(p.get(i), p.get(i + 1), random, i);
                    cruzada.addAll(sol1);
                    evaluaciones += 2;
                } else {
                    mutaPadres(p.get(i),p.get(i+1),random);
                    cruzada.add(p.get(i));
                    cruzada.add(p.get(i+1));
                }
            }
        }

        return cruzada;
    }

    private void mutaPadres(Individuo padre1, Individuo padre2, Random random){
        //se hace la probabilidad de mutacion para los dos padres
        if (random.nextDouble() < config.getProbMutacion()) {
            dosopt(padre1.getSolucion(), random);
            padre1.evaluar(distancias);
            evaluaciones++;
        }
        if (random.nextDouble() < config.getProbMutacion()) {
            dosopt(padre2.getSolucion(), random);
            padre2.evaluar(distancias);
            evaluaciones++;
        }
    }

    private List<Individuo> cruceMOC(Individuo p1, Individuo p2, Random random,int i){
        ArrayList<Integer> sol1 = new ArrayList<>();
        ArrayList<Integer> sol2 = new ArrayList<>();

        int puntoCorte = random.nextInt(p1.getSolucion().size());

        ArrayList<Integer> valoresP1 = new ArrayList<>(p1.getSolucion().subList(0,puntoCorte));
        ArrayList<Integer> valoresP2 = new ArrayList<>(p2.getSolucion().subList(0,puntoCorte));

        intercambio(p1, p2, puntoCorte, valoresP2, sol1);
        intercambio(p2, p1, puntoCorte, valoresP1, sol2);

        if(random.nextDouble()<config.getProbMutacion())
            dosopt(sol1,random);
        if(random.nextDouble()<config.getProbMutacion())
            dosopt(sol2,random);

        List<Individuo> devolver = new ArrayList<>();
        devolver.add(new Individuo(sol1,generacion,distancias,i));
        devolver.add(new Individuo(sol2,generacion,distancias,i+1));

        return devolver;
    }

    private static void intercambio(Individuo p1, Individuo p2, int puntoCorte, ArrayList<Integer> valoresP2, ArrayList<Integer> sol1) {
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
        List<Individuo> result = new ArrayList<>(p.subList(0, numElite));

        for (int i = numElite; i < p.size(); i++) {
            Individuo elementoActual = p.get(i);

            Individuo mayorDeLosMenores = result.stream().min(Comparator.comparing(Individuo::getFitness)).orElseThrow();

            if (elementoActual.getFitness() < mayorDeLosMenores.getFitness()) {
                result.remove(mayorDeLosMenores);
                result.add(elementoActual);
            }
        }

       return result;
    }

}
