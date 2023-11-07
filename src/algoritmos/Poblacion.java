package algoritmos;

import java.util.ArrayList;

public class Poblacion {
    private ArrayList<Individuo> poblacion;

    public Poblacion() {
        this.poblacion = new ArrayList<>();
    }

    public boolean addIndividuo(Individuo i){
        return poblacion.add(i);
    }

    public ArrayList<Individuo> getPoblacion() {
        return poblacion;
    }

}
