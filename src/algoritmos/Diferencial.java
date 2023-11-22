package algoritmos;

import procesaFichero.Configurador;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Diferencial extends Evolutivo {
    private final int  PADRE,OBJETIVO,ALEATORIO1,ALEATORIO2;
    private int evaluaciones;
    private final String operadorSeleccion;

    public Diferencial(double[][] distancias, Configurador config, int poblacion, String operadorSeleccion) {
        super(distancias,config,poblacion);
        this.PADRE=0;//posicion del padre en el array de elegidos
        this.OBJETIVO=1;//posicion del objetivo en el array de elegidos
        this.ALEATORIO1=2;//posicion del aleatorio1 en el array de elegidos
        this.ALEATORIO2=3;//posicion del aleatorio2 en el array de elegidos
        this.operadorSeleccion = operadorSeleccion;
    }

    public Individuo ejecutar(long semilla){
        Random random = new Random(semilla);
        //crea la poblacion incial y define los elites
        ArrayList<Individuo> poblacion = crearPoblacionInicial(random);
        evaluaciones+=tamPoblacion;


        Instant start=Instant.now();
        Instant end = Instant.now();


        while (evaluaciones<config.getEvaluaciones() && Duration.between(start,end).toMillis()<config.getLimiteSegundos()*100 ) {
            //aumenta la generacion en 1
            generacion++;


            for (int i = 0; i < tamPoblacion; i++) {
                //Seleccion
                List<Integer> elegidos;
                if (operadorSeleccion.equals("EDA"))
                    elegidos = operadorSeleccionEDA(poblacion,random,i);
                else
                    elegidos = operadorSeleccionEDB(poblacion,random,i);

                //Recombinacion
                Individuo hijo = recombinacionTernaria(poblacion,random,elegidos,i);
                evaluaciones++;

                //Reemplazamiento
                if(hijo.getFitness()<poblacion.get(i).getFitness()) //el hijo mejora al padre y sustituye al padre
                    poblacion.set(i,hijo);

            }


            end=Instant.now();
        }


        return poblacion.stream().min(Comparator.comparing(Individuo::getFitness)).orElse(null);
    }

    private List<Integer> operadorSeleccionEDB(ArrayList<Individuo> p, Random random, int padre){
        List<Integer> elegidos = new ArrayList<>();
        elegidos.add(padre);

        Individuo obj;
        do {
            obj = torneo(random, p, config.getDiferencialKBest(), true);
        }while (elegidos.contains(obj.getIndice()));
        elegidos.add(obj.getIndice());

        Individuo aleatorio1;
        do {
            aleatorio1 = torneo(random, p, config.getDiferencialKBest(), true);
        }while (elegidos.contains(aleatorio1.getIndice()));
        elegidos.add(aleatorio1.getIndice());

        Individuo aleatorio2;
        do {
            aleatorio2 = torneo(random, p, config.getDiferencialKBest(), true);
        }while (elegidos.contains(aleatorio2.getIndice()));
        elegidos.add(aleatorio2.getIndice());

        return elegidos;
    }

    //TODO PROBAR QUE FUNCIONA BIEN
    private Individuo recombinacionTernaria(ArrayList<Individuo> p, Random random, List<Integer> elegidos, int indice){
        int corte = random.nextInt(0,tamSolucion-1);

        ArrayList<Integer> solPadre = new ArrayList<>(p.get(elegidos.get(PADRE)).getSolucion());
        int aux=solPadre.get(corte);
        solPadre.set(corte,solPadre.get(corte+1));
        solPadre.set(corte+1,aux);

        ArrayList<Integer> nuevo = new ArrayList<>(p.get(elegidos.get(ALEATORIO1)).getSolucion());
        intercambio(p, elegidos, nuevo, solPadre, corte,ALEATORIO1);
        intercambio(p, elegidos, nuevo, nuevo, corte+1,ALEATORIO2);

        return cruceOX2(nuevo,p.get(elegidos.get(OBJETIVO)).getSolucion(),random,indice);
    }

    private void intercambio(ArrayList<Individuo> p, List<Integer> elegidos, ArrayList<Integer> nuevo, ArrayList<Integer> solPadre, int corte,int aleatorio) {
        int aux;
        int index = nuevo.indexOf(solPadre.get(corte));
        aux= nuevo.get(index);
        nuevo.set(index, p.get(elegidos.get(aleatorio)).getSolucion().get(index));
        nuevo.set(nuevo.indexOf(solPadre.get(corte)),aux);
    }

    private List<Integer> operadorSeleccionEDA(ArrayList<Individuo> p, Random random, int padre){
        ArrayList<Integer> elegidos = new ArrayList<>();
        elegidos.add(padre);

        //individuo objetivo
        Individuo obj;
        do {
            obj = torneo(random, p, config.getDiferencialKBest(), true);
        }while (elegidos.contains(obj.getIndice()));
        elegidos.add(obj.getIndice());

        //Individuos aleatorios
        int aleatorio1,aleatorio2;
        do {
            aleatorio1=random.nextInt(0,tamPoblacion);
        }while (elegidos.contains(aleatorio1));
        elegidos.add(aleatorio1);
        do {
            aleatorio2=random.nextInt(0,tamPoblacion);
        }while (elegidos.contains(aleatorio2));
        elegidos.add(aleatorio2);

        return elegidos;
    }


}
