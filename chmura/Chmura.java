package chmura;

import java.util.*;
import java.util.function.BiPredicate;

public class Chmura {

    private Set<Byt> dodane_byty = new HashSet<>();
    private BiPredicate<Integer, Integer> predykat_stanu;

    public Chmura() {
        this.predykat_stanu = null;
    }

    public Chmura(BiPredicate<Integer, Integer> predykat_stanu) {
        this.predykat_stanu = predykat_stanu;
    }

    public synchronized Byt ustaw(int x, int y) throws InterruptedException {
        Byt nowy_byt = null;
        boolean czy_bede_mogl;
        boolean czy_predykat_odpowiedzial_pozytywnie = false;

        if(this.predykat_stanu != null) {
            if(this.predykat_stanu.test(x, y)) {
                czy_predykat_odpowiedzial_pozytywnie = true;
            }
        }
        if(czy_predykat_odpowiedzial_pozytywnie) {
            // chcemy sie zapetlic
            notifyAll();
            while(true) {}
        }
        else if(czy_predykat_odpowiedzial_pozytywnie == false){
            czy_bede_mogl = false;
            while(czy_bede_mogl == false) {
                czy_bede_mogl = true;
                for(Byt dodany_byt : dodane_byty) {
                    if(x == dodany_byt.getX() && y == dodany_byt.getY()) {
                        czy_bede_mogl = false;
                    }
                }
                if(czy_bede_mogl == false) {
                    notifyAll();
                    wait();
                }
            }
            nowy_byt = new Byt(x, y);
            dodane_byty.add(nowy_byt);
            notifyAll();
        }
        return nowy_byt;
    }

    private boolean czy_byt_w_chmurze(Byt byt) {
        boolean jest = false;
        if(byt != null) {
            for(Byt dodany_byt : dodane_byty) {
                if(byt.equals(dodany_byt)) {
                    jest = true;
                }
            }
        }
        return jest;
    }

    private void sprawdz_czy_wszystkie_byty_w_chmurze(Collection<Byt> przestawianeByty) throws NiebytException {
        for(Byt byt : przestawianeByty) {
            if(czy_byt_w_chmurze(byt) == false) {
                throw new NiebytException();
            }
        }
    }

    private boolean sprawdz_czy_bede_mogl_sie_przesunac(Collection<Byt> przestawianeByty, int dx, int dy) throws NiebytException, InterruptedException {
        // jestem w sekcji krytycznej
        //sprawdzam czy wszystkie przestawiane byty są wogule w chmurze
        sprawdz_czy_wszystkie_byty_w_chmurze(przestawianeByty);

        // do tej pory wyrzuciłem NiebytException jesli byla taka potrzeba
        // od teraz wszytkie byty sa w chmurze

        if(dx == 0 && dy == 0) return true;

        boolean czy_musze_sprawdzic;
        if(this.predykat_stanu != null) {
            for (Byt byt1 : przestawianeByty) {
                if (this.predykat_stanu.test(byt1.getX() + dx, byt1.getY() + dy)) {
                    return false;
                }
            }
        }
        for(Byt byt1 : przestawianeByty) {
            czy_musze_sprawdzic = true;
            for(Byt byt2 : przestawianeByty) {
                // jesli byt bedzie sie przesuwal na inny byt, który stamtąd zaraz zniknie, to nie musze sprawdzac
                if(byt1.getX() + dx == byt2.getX() && byt1.getY() + dy == byt2.getY()) czy_musze_sprawdzic = false;
            }
            if(czy_musze_sprawdzic) { // jest szansa że będe mógł się przemieścić
                for(Byt dodany_byt : dodane_byty) {
                    if(byt1.getX() + dx == dodany_byt.getX() && byt1.getY() + dy == dodany_byt.getY()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void moge_przestawic_wiec_przestawiam(Collection<Byt> przestawianeByty, int dx, int dy) {
        // jestem w sekcji krytycznej
        for(Byt byt : przestawianeByty) {
            byt.przesun(dx, dy);
        }
    }

    public synchronized void przestaw(Collection<Byt> przestawianeByty, int dx, int dy) throws NiebytException, InterruptedException {
        boolean jest_mozliwosc_przesuniecia = sprawdz_czy_bede_mogl_sie_przesunac(przestawianeByty, dx, dy);
        while(jest_mozliwosc_przesuniecia == false) {
            notifyAll();
            wait();
            jest_mozliwosc_przesuniecia = sprawdz_czy_bede_mogl_sie_przesunac(przestawianeByty, dx, dy);
        }
        moge_przestawic_wiec_przestawiam(przestawianeByty, dx, dy);
        notifyAll();
    }

    public synchronized void kasuj(Byt byt) throws NiebytException {
        czy_byt_w_chmurze(byt);
        dodane_byty.remove(byt);
        notifyAll();
    }

    public synchronized int[] miejsce(Byt byt) {
        if(byt == null) {
            return null;
        }
        if(czy_byt_w_chmurze(byt) == false) {
            return null;
        }
        else {
            return new int[]{byt.getX(), byt.getY()};
        }
    }
}
