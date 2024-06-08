# FFT Audio Analyzer

## Opis projektu
FFT Audio Analyzer to aplikacja na Androida służąca do pomiaru temperatury. Aplikacja pobiera sygnał audio z mikrofonu urządzenia, przeprowadza analizę widma częstotliwościowego za pomocą algorytmu FFT (Fast Fourier Transform) i wyświetla wyniki na wykresie. 

## Funkcje
- **Analiza FFT**: Sygnał czasowy jest przekształcany na widmo częstotliwościowe za pomocą algorytmu FFT.
- **Wyświetlanie wyników**: Widmo częstotliwości jest wyświetlane na wykresie w czasie rzeczywistym.
- **Przeliczanie na temperaturę**: Wyniki analizy są przeliczane na wartość temperatury za pomocą współczynników `a` i `b`.

## Wymagania
- Android Studio
- Urządzenie z systemem Android z mikrofonem
- Uprawnienia do nagrywania audio na urządzeniu
- Płytka PCB podłączona do gniazda jack

## Instalacja
1. Sklonuj repozytorium:
2. Otwórz projekt w Android Studio.
3. Podłącz urządzenie z systemem Android do komputera.
4. Zainstaluj aplikację na urządzeniu za pomocą Android Studio.

## Użycie
1. Uruchom aplikację na urządzeniu z systemem Android.
2. Podłącz płytkę PCB do gniazda jack.
3. Ustaw częstotliwość próbkowania (FS) i częstotliwość sygnału (F) za pomocą sliderów.
4. Wprowadź współczynniki `a` i `b` w odpowiednich polach tekstowych.
5. Naciśnij przycisk "Start", aby rozpocząć nagrywanie i analizę sygnału.
6. Wyniki analizy FFT będą wyświetlane na wykresie w czasie rzeczywistym.
7. Aby zatrzymać nagrywanie, naciśnij ponownie przycisk "Stop".

## Struktura projektu
- `MainActivity.java`: Główna aktywność aplikacji, inicjalizacja parametrów i interfejsu użytkownika.
- `ChartDrawer.java`: Klasa odpowiedzialna za rysowanie wykresu i obsługę interakcji użytkownika.
- `Readout.java`: Wątek odpowiedzialny za nagrywanie dźwięku, analizę FFT i obliczanie temperatury.
- `FFT.java`: Implementacja algorytmu FFT.

## Autor
- **Miłosz Klim, Technologie komputerowe semestr VI**
