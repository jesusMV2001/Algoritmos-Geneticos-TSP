package procesaFichero;

import java.io.*;
import java.util.ArrayList;

public class CreaFicheros {
    public CreaFicheros() {
    }

    public void fichero(ArrayList<String> logs, String semilla, String archivo, String algoritmo, Configurador config) {
        String nombre = "logs/"+ algoritmo +"_"+ semilla +"_"+ archivo +"_log.txt";

        try {
            File f = new File(nombre);

            if (f.exists() && config.isReemplazarLogs()) {
                if(f.delete()){
                    crearFichero(f,nombre, logs);
                }
            } else {
                crearFichero(f,nombre, logs);
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error al crear el archivo: " + e.getMessage());
        }
    }

    void crearFichero(File f, String nombre, ArrayList<String> logs) throws IOException {
        boolean creado = f.createNewFile();
        if (creado) {
            FileWriter fileWriter = new FileWriter(nombre);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String log : logs) {
                bufferedWriter.write("-------------------------------------------------------------------------------------");
                bufferedWriter.newLine();
                bufferedWriter.write(log);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        }
    }

    public void crearCSV(Configurador config, ArrayList<double [][]> datos, int filas, int cols){


        try{
            // Generacion del fichero con los datos en formato CSV
            FileWriter fichero = new FileWriter("salida.txt");
            PrintWriter pw = new PrintWriter(fichero);

            for (int i = 0; i < config.getAlgoritmos().size(); i++) {
                pw.print(config.getAlgoritmos().get(i) + ","); // Nombre tabla
                for (int j = 0; j < cols / 2; j++) pw.print("Solucion,Tiempo,"); // Cabecera tabla
                //Datos
                pw.println();
                for (int j = 0; j < filas; j++){
                    pw.print("Ejecucion_" + (j+1) + ",");
                    for (int k = 0; k < cols; k++) {
                        pw.print(datos.get(i)[j][k] + ",");
                    }
                    pw.println();
                }
                pw.println();
            }

        } catch (Exception ignore) {
        }
    }

}
