package algoritmos;

import java.util.ArrayList;

public class Individuo {
    private double fitness;
    private final ArrayList<Integer> solucion;
    private boolean evaluado;
    private final int generacion;
    private int indice;

    public Individuo(ArrayList<Integer> solucion, int generacion, double[][] distancias, int indice) {
        this.fitness=0;
        this.indice = indice;
        this.solucion = solucion;
        this.evaluado = false;
        this.generacion = generacion;
        evaluar(distancias);
    }

    public Individuo(Individuo copia){
        this.indice=copia.indice;
        this.fitness=copia.fitness;
        this.solucion = new ArrayList<>(copia.getSolucion());
        this.evaluado=copia.evaluado;
        this.generacion= copia.generacion;
    }

    public void evaluar(double[][] distancias){
        evaluado=true;
        double sum=0;
        for (int i = 0; i < solucion.size()-1; i++) {
            sum += distancias[solucion.get(i)][solucion.get(i+1)];
        }
        fitness=sum;
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Comprueba si es el mismo objeto en memoria.
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false; // Comprueba si los tipos son diferentes o el objeto es nulo.
        }

        // Compara los campos para determinar la igualdad.
        return indice==((Individuo) obj).indice;
    }

    public ArrayList<Integer> getSolucion() {
        return solucion;
    }

    public boolean isEvaluado() {
        return evaluado;
    }

    public int getGeneracion() {
        return generacion;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }
}
