package chmura.testy;

import chmura.Byt;
import chmura.Chmura;
import chmura.NiebytException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Created by skiline11 on 22.11.17.
 */

// UWAGA ---> NIE MOZNA KORZYSTAC Z SEMAFOROW - TRZEBA KORZYSTAC TYLKO ZO CHMURY
public class producenci_i_konsumenci {

    private static int x = 0, y = 0;
    private static Chmura chmura = new Chmura();
    private static int ile_dodanych = 0, ile_usunietych = 0;
    private static Byt ochrona;
    private static Map<Integer, Byt> dodane_byty = new HashMap<>();
    private static int ile = 100;

    /*
    Problem producentów i konsumentów : // ustaw i przestaw
    Producent id : wejscie : byt = ustaw(id, 0), blockingQueue.add(byt)
    Konsument id : wejscie : byt = blockingQueue.take(), przestaw(byt, 0, 1) - przestaw o 1 w górę
     */

    private static class Producent extends Thread {
        private int id;
        public Producent(int id) {this.id = id;}

        @Override
        public void run() {
            try {
                ochrona = chmura.ustaw(-1, -1);
                ile_dodanych++;
                System.out.println("Producent " + this.id + " dodaje byt na pozycje (" + ile_dodanych + ", 0) " +
                        "- aktualnie w buforze jest " + (ile_dodanych - ile_usunietych) + " bytow");
                Byt byt = chmura.ustaw(ile_dodanych, 0);
                dodane_byty.put(ile_dodanych, byt);
                chmura.kasuj(ochrona);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NiebytException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Konsument extends Thread {
        private int id;
        public Konsument(int id) {this.id = id;}

        @Override
        public void run() {
            try {
                ochrona = chmura.ustaw(-1, -1);
                while(ile_dodanych == ile_usunietych) {
                    chmura.kasuj(ochrona);
                    ochrona = chmura.ustaw(-1, -1);
                }
                // ile_dodanych > ile_usunietych
                ile_usunietych++;
                System.out.println("Konsument zjada byt z pozycji (" + ile_usunietych + ", 0) " +
                        "- aktualnie w buforze jest " + (ile_dodanych - ile_usunietych) + " bytow");
                chmura.kasuj(dodane_byty.get(ile_usunietych));
                chmura.kasuj(ochrona);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NiebytException e) {
                e.printStackTrace();
            }

        }
    }




    public static void main(String[] args) throws InterruptedException {
        Thread producenci[] = new Thread[ile];
        Thread konsumenci[] = new Thread[ile];
        for(int i = 0; i < ile; i++) {
            producenci[i] = new Producent(i);
            konsumenci[i] = new Konsument(i);
            konsumenci[i].start();
            producenci[i].start();
        }

        for(int i = 0; i < ile; i++) {
            producenci[i].join();
            konsumenci[i].join();
        }
    }
}
