mkdir -p rozpakowane/$1/walidacja
cp walidacja/WalidacjaChmury.java rozpakowane/$1/walidacja/
tar xzf paczki/$1.tar.gz -C rozpakowane/$1/
cd rozpakowane/$1

if ! javac walidacja/WalidacjaChmury.java; then
    echo Błąd kompilacji klasy WalidacjaChmury
elif ! java walidacja/WalidacjaChmury; then
    echo Błąd wykonania programu WalidacjaChmury
fi
