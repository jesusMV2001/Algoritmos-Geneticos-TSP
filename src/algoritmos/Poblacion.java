package algoritmos;

import java.util.ArrayList;

public class Poblacion {
    private ArrayList<Individuo> poblacion;
    private ArrayList<Individuo> elites;

    public Poblacion() {
        this.poblacion = new ArrayList<>();
        this.elites = new ArrayList<>();
    }

    public boolean addIndividuo(Individuo i){
        return poblacion.add(i);
    }

    public ArrayList<Individuo> getPoblacion() {
        return poblacion;
    }

    public ArrayList<Individuo> getElites() {
        return elites;
    }
}
