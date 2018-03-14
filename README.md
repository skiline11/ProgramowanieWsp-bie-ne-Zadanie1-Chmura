## Wprowadzenie

Chmura bytów wymiaru n przyporządkowuje bytom miejsca, jednoznacznie identyfikowane przez ciąg n współrzędnych całkowitych. Zachowuje przy tym niezmiennik chmury: każdy byt jest w innym miejscu. Chmura bytów może służyć do synchronizacji procesów współbieżnych.

## Specyfikacja

W pakiecie chmura, implementującym w Javie chmurę bytów wymiaru 2, jest definicja typu Byt, kontrolowanego wyjątku NiebytException i klasy Chmura.

Klasa Chmura ma dwa publiczne konstruktory i cztery publiczne obiektowe metody.
    
Konstruktor : `Chmura()` buduje chmurę, która w stanie początkowym nie ma żadnego bytu.

Konstruktor : `Chmura(BiPredicate<Integer, Integer> stan)`
buduje chmurę, której początkową zawartość określa dwuargumentowy predykat stan. W miejscu (x, y) jest byt wtedy i tylko wtedy, gdy stan.test(x, y) ma wartość true.

Metoda : `Byt ustaw(int x, int y) throws InterruptedException`
daje jako wynik nowy byt, dodany do chmury w miejscu (x, y).

Metoda : `void przestaw(Collection<Byt> byty, int dx, int dy) throws NiebytException, InterruptedException`
przemieszcza na raz wszystkie byty kolekcji byty o wektor (dx, dy). Byt z miejsca (x, y) trafia na miejsce (x + dx, y + dy). Jeśli którykolwiek z bytów kolekcji byty nie jest w chmurze, metoda zgłasza wyjątek NiebytException. Jeżeli wymaga tego niezmiennik chmury, metody ustaw() i przestaw() wstrzymują wątek do czasu, gdy ich wykonanie będzie możliwe. W przypadku przerwania zgłaszają wyjątek InterruptedException.

Metoda : `void kasuj(Byt byt) throws NiebytException`
usuwa byt z chmury. Jeśli byt nie jest w chmurze, metoda zgłasza wyjątek NiebytException.

Metoda : `int[] miejsce(Byt byt)`
daje dwuelementową tablicę ze współrzędnymi x i y bytu, lub null, jeśli byt nie jest w chmurze.

## Polecenie
  
  1) Zrealizuj w Javie chmurę bytów wymiaru 2. Do pakietu chmura dołącz wszystkie potrzebne definicje pomocnicze.
  
  2) Napisz dwa programy przykładowe, demonstrujące zastosowanie chmury bytów do synchronizacji wątków. W pierwszym programie rozwiąż problem producentów i konsumentów a w drugim problem czytelników i pisarzy.
