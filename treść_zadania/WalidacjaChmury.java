package walidacja;

import java.util.Arrays;
import java.util.Collection;

import chmura.Byt;
import chmura.Chmura;
import chmura.NiebytException;

public class WalidacjaChmury {

    public static void main(String[] args) {
        Chmura chmura = new Chmura((x, y) -> x < y);
        Byt byt = null;
        try {
            byt = chmura.ustaw(0, 0);
        } catch (InterruptedException e) {
            System.exit(1);
        }
        try {
            Collection<Byt> byty = Arrays.asList(byt);
            chmura.przestaw(byty, 1, -1);
        } catch (NiebytException | InterruptedException e) {
            System.exit(2);
        }
        @SuppressWarnings("unused")
        int[] miejsce = chmura.miejsce(byt);
        try {
            chmura.kasuj(byt);
        } catch (NiebytException e) {
            System.exit(3);
        }
        new Chmura();
        System.out.println("OK");
    }

}
