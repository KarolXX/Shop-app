SHOP APP
W aplikacji istnieją dwie encje: product & category.
Product moze być powiązany z kategorią (ale nie musi). Category musi być powiązana z co najmniej jednym produktem.

Product ma właściwości: name, amount(ilosc), active, category(powiązana kategoria)
Pobierając wszystkie produkty z DB pobieramy tak naprawde te których właściwość active == TRUE i amount > 0 
To że amount <= 0 nie oznacza że active == FALSE - te dwie właściwośći są od siebie nie zależne, właściwość active jest potrzebna podczas usuwania produktu ( o tym poniżej )
Usunięcie produktu nie skutkuje natychmiastowym usunięciem z DB, skutkuje ustawieniem flagi active na FALSE a całkowite usunięcie następuje po 60 sekundach - w trakcie tych 60 sekund aplikacja powinna mieć możliwość odzyskania usunietego produktu (ustawienie flagi active na TRUE) 
Usunięcie produktu powiązanego z kategorią nie usuwa kategorii CHYBA ŻE ta kategoria nie ma innych powiązanych produktów ( wtedy kategoria przestaje mieć powiązanie ze swoim jedynym produktem co jest zabronione więc kategria powinna zostac usunieta )

Category ma właściwości: name, totalQuantity, products(powiązane produkty)
totalQuantity - jest to suma ilości wszystkich powiązanych z daną kategrią produktów ( suma właściwości amount z każdego powiązanego produktu ). Jeśli właściwość amount zmienia się w jednym z powiązanych produktów wtedy właściwość totalQuantity powinna zostać odpowiednio zmieniona. Tak samo w przypadku usunięcia i ewentualnego przywrócenia produktu - należy to odzwierciedlać we właściwości totalQuantity 
Usunięcie kategorii nie skutkuje usunięciem powiązanych z nią produktów. Powoduje to jedynie przerwanie powiązania ( ustawieniem właściwości category na NULL )


