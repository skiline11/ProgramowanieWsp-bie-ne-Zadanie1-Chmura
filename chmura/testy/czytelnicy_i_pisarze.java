package chmura.testy;

import chmura.Byt;
import chmura.Chmura;
import chmura.NiebytException;
import javafx.util.Pair;



public class czytelnicy_i_pisarze {


    /*
     INFORMACJA : Czytelnia jest skonstruowana w nastepujacy sposob :
     Przychodzi czytelnik.
        Jeśli ktoś pisze lub czekają pisarze, to czytelnik zasypia na "czekaja_czytelnicy".
        W przeciwnym razie czytelnik wchodzi do czytelni.
        Gdy jestem ostatnim z wychodzących czytelników to jesli czeka pisarz, to go wołam.
     Przychodzi pisarz.
        Jesli ktos czyta lub pisze, to pisarz zasypia na "czekają_pisarze".
        W przeciwnym razie pisarz wchodzi do czytelni.
        Gdy pisarz wychodzi to :
            Jesli czekają czytelnicy to ich wołam.
            Jesli czytelnicy nie czekają, ale pisarze czekają, to wołam jednego z nich.
    */

    private static Chmura chmura = new Chmura();
    private static final int ilu_czytelnikow = 100, ilu_pisarzy = 100;
    private static Byt przed_budynkiem_czytelni, w_poczekalni;
    private static Byt czekaja_czytelnicy, czekaja_pisarze;
    private static Pair<Integer, Integer> czytelnicy_coordinates = new Pair<>(2, 0);
    private static Pair<Integer, Integer> mutex_coordinates = new Pair<>(0, 0);
    private static Pair<Integer, Integer> mutex2_coordinates = new Pair<>(3, 0);
    private static Pair<Integer, Integer> pisarze_coordinates = new Pair<>(1, 0);

    private static int ilu_czyta = 0, ilu_pisze = 0;
    private static int ilu_czytelnikow_czeka = 0, ilu_czytelnikow_czekalo = 0, ilu_pisarzy_czeka = 0;


    private static void out(String s) {
        System.out.println(s);
    }

    private static class Czytelnik extends Thread {
        private int id;
        private Czytelnik(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                out("czyt " + id + " chce wejsc do czytelni");
                przed_budynkiem_czytelni = chmura.ustaw(mutex_coordinates.getKey(), mutex_coordinates.getValue()); // P(przed_budynkiem_czytelni)
//                out("czyt " + id + " zajął przed_budynkiem_czytelni i sprawdza info");
                if(ilu_pisze > 0 || chmura.miejsce(czekaja_pisarze) != null) {
                    ilu_czytelnikow_czeka++;
                    if(ilu_czytelnikow_czeka == 1) {
                        out("czyt " + id + " jestem pierwszym czytelnikiem ktory czeka, wiec tworze sem do czekania czytelnikow, ale nie zawieszam sie na nim");
                        czekaja_czytelnicy = chmura.ustaw(czytelnicy_coordinates.getKey(), czytelnicy_coordinates.getValue());
                    }
                    out("czyt " + id + " zwalnia przed_budynkiem_czytelni, bo ktos pisze, lub czeka w kolejce do pisania, i zasypia na 'czytelnicy'");
                    chmura.kasuj(przed_budynkiem_czytelni); // V(przed_budynkiem_czytelni)
                    czekaja_czytelnicy = chmura.ustaw(czytelnicy_coordinates.getKey(), czytelnicy_coordinates.getValue()); // <-- tutaj zawieszają się czytelnicy
                    out("czyt " + id + " zostal obudzony z 'czytelnicy' - z dziedziczeniem sekcji krytycznej");
                    if(ilu_czytelnikow_czeka == 0) {
                        chmura.kasuj(czekaja_czytelnicy);
                    }

                    if(ilu_czytelnikow_czeka > 0) {
                        ilu_czytelnikow_czeka--;
                        chmura.kasuj(czekaja_czytelnicy);
                        w_poczekalni = chmura.ustaw(mutex2_coordinates.getKey(), mutex2_coordinates.getValue()); // czekam na reszte
                        ilu_czyta++;
                        chmura.kasuj(w_poczekalni);
                    }
                    else {
                        // jestem ostatnim czytelnikiem wiec musze obudzic tych ktorzy czekaja na w_poczekalni
                        ilu_czyta++;
                        chmura.kasuj(w_poczekalni);
                    }
                }
                else {
                    out("czyt " + id + " bedzie mogl wejsc do czytelni bo nikt nie pisze i nie czeka na pisanie");
                    ilu_czyta++;
                    out("czyt " + id + " zwalnia przed_budynkiem_czytelni");
                    chmura.kasuj(przed_budynkiem_czytelni);
                }

                out("***** Czytelnik " + id + " czyta");

                Thread.sleep(1000);

                out("***** Czytelnik " + id + " wychodzi z czytelni");

//                out("czyt " + id + " zajal przed_budynkiem_czytelni ( po tym jak wszyscy czytelnicy weszli do czytania )");
                ilu_czyta--;
                if(ilu_czyta == 0) {
                    out("czyt " + id + " nikt juz nie czyta");
                    if(ilu_pisarzy_czeka > 0) {
                        ilu_pisarzy_czeka--;
                        out("Czytelnik " + id + " czeka pisarz wiec go wolam");
                        chmura.kasuj(czekaja_pisarze);
                    }
                    else {
                        out("Czytelnik " + id + " zwalnia przed_budynkiem_czytelni bo żaden pisarz nie czeka");
                        chmura.kasuj(przed_budynkiem_czytelni); // Przykład przychodzi 1 czytelnik i wychodzi, wtedy nikt nie czyta i trzeba zwolnic przed_budynkiem_czytelni
                    }
                }
                else {
                    out("czyt " + id + " ktos jeszcze czyta wiec oddaje przed_budynkiem_czytelni");
                    chmura.kasuj(przed_budynkiem_czytelni);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NiebytException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Pisarz extends Thread {
        private int id;
        private Pisarz(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                out("pisarz " + id + " chce wejsc do czytelni");
                przed_budynkiem_czytelni = chmura.ustaw(mutex_coordinates.getKey(), mutex_coordinates.getValue()); // P(przed_budynkiem_czytelni)
                out("pisarz " + id + " zajal przed_budynkiem_czytelni i sprawdza info");
                if(ilu_czyta + ilu_pisze > 0) {
                    out("pisarz " + id + " zwalnia przed_budynkiem_czytelni bo ktos czyta lub pisze");
                    ilu_pisarzy_czeka++;
                    if(ilu_pisarzy_czeka == 1) {
                        // Przychodzi pierwszy pisarz i tworzy 'czekaja_pisarze' na ktorym sie bedzia sie wieszac
                        out("tutaj1");
                        czekaja_pisarze = chmura.ustaw(pisarze_coordinates.getKey(), pisarze_coordinates.getValue());
                        out("tutaj2");
                    }
                    chmura.kasuj(przed_budynkiem_czytelni);
                    czekaja_pisarze = chmura.ustaw(pisarze_coordinates.getKey(), pisarze_coordinates.getValue()); // tutaj zawieszają się pisarze
                    if(ilu_pisarzy_czeka == 0) {
                        chmura.kasuj(czekaja_pisarze);
                    }
                }
                out("pisarz " + id + " bedzie mogl wejsc do czytelni");
                // dziedziczenie sekcji krytycznej
                ilu_pisze = 1;
                out("pisarz " + id + " oddaje przed_budynkiem_czytelni");
                chmura.kasuj(przed_budynkiem_czytelni);

                out("***** Pisarz " + id + " pisze");
                Thread.sleep(1000);

                out("***** Pisarz " + id + " wychodzi z czytelni");
                przed_budynkiem_czytelni = chmura.ustaw(mutex_coordinates.getKey(), mutex_coordinates.getValue());
                out("pisarz " + id + " zajal przed_budynkiem_czytelni i zmienia info bo wychodzi z czytelni");
                ilu_pisze = 0;

                if(ilu_czytelnikow_czeka > 0) {
                    out("pisarz " + id + " - czekaja czytelnicy wiec wolam ich");
                    ilu_czytelnikow_czekalo = ilu_czytelnikow_czeka;
                    ilu_czytelnikow_czeka--;
                    w_poczekalni = chmura.ustaw(mutex2_coordinates.getKey(), mutex2_coordinates.getValue()); // tworze sem na ktorym beda sie synchronizowac czytelnicy
                    chmura.kasuj(czekaja_czytelnicy);
                }
                else if(ilu_pisarzy_czeka > 0) {
                    ilu_pisarzy_czeka--;
                    out("pisarz " + id + " - nie czekaja czytelnicy wiec wolam pisarzy");
                    chmura.kasuj(czekaja_pisarze);
                }
                else {
                    out("pisarz " + id + " bedzie kasowal przed_budynkiem_czytelni bo nikt nie czeka");
                    chmura.kasuj(przed_budynkiem_czytelni); // Przychodzi pisarz i wychodzi
                    out("pisarz " + id + " skasowal przed_budynkiem_czytelni");
                }
            } catch (NiebytException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }



    public static void main(String[] args) throws InterruptedException {
//        czekaja_czytelnicy = chmura.ustaw(czytelnicy_coordinates.getKey(), czytelnicy_coordinates.getValue());
//        czekaja_pisarze = chmura.ustaw(pisarze_coordinates.getKey(), pisarze_coordinates.getValue());


        Thread czytelnicy[] = new Thread[ilu_czytelnikow];
        Thread pisarze[] = new Thread[ilu_pisarzy];

        for(int i = 0; i < ilu_czytelnikow; i++) {
            czytelnicy[i] = new Czytelnik(i);
        }
        for(int i = 0; i < ilu_pisarzy; i++) {
            pisarze[i] = new Pisarz(i);
        }
        czytelnicy[1].start();
        Thread.sleep(500);
        czytelnicy[2].start();
        czytelnicy[3].start();
        pisarze[1].start();
        czytelnicy[4].start();
        czytelnicy[5].start();
        czytelnicy[6].start();
        czytelnicy[7].start();
        pisarze[2].start();
        pisarze[3].start();
        czytelnicy[8].start();
        czytelnicy[9].start();
        czytelnicy[10].start();
        Thread.sleep(6000);
        pisarze[4].start();
        pisarze[5].start();
        czytelnicy[11].start();
        czytelnicy[12].start();
        czytelnicy[13].start();
        pisarze[6].start();


        for(int i = 1; i < 10; i++) {
            czytelnicy[i].join();
        }
        for(int i = 1; i < 4; i++) {
            pisarze[i].join();
        }
    }
}
