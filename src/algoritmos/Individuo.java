package algoritmos;

import java.util.ArrayList;

public class Individuo {
    private double fitness;
    private ArrayList<Integer> solucion;
    private boolean evaluado;
    private int generacion;

    public Individuo(ArrayList<Integer> solucion, int generacion) {
        this.solucion = solucion;
        this.evaluado = false;
        this.generacion = generacion;
    }

    public void evaluar(double[][] distancias){
        evaluado=true;
        double sum=0;
        for (int i = 0; i < solucion.size(); i++) {
            sum += distancias[solucion.get(i)][solucion.get((i+1)%solucion.size())];
        }
        fitness=sum;
    }

    public double getFitness() {
        return fitness;
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
}
