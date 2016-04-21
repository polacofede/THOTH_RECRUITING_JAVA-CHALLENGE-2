Enunciado
=========

Continuamente se reciben posiciones en cada mercado. Las posiciones son posibles operaciones de compra o de venta, a un costo unitario y con una cantidad maxima de unidades.

Una operacion de compra o venta requiere colocar operaciones en las mejores n ofertas existentes hasta llegar a la cantidad requerida.

Por ejemplo, en cierto momento pueden existir las siguinete posiciones:
A	Compra	10	3.55
B	Compra  23	3.32
C	Compra	100	3.05

Una operacion de compra de 100 unidades deberia colocarse como:
Comprar 10 unidades a (A), 23 unidades a (B) y 67 unidades a C

Las posiciones van cambiando a medida que son tomadas por otros participantes, asi que continuamente se estan recibiendo cambios en la cantidad y el precio de las mismas.

Por eso es muy importante poder contar con un mecanismo eficiente para consultar y actualizar dichas posiciones.


Problema
--------

Se adjunta una implementacion muy simple de un Market, **NaiveMarket**, que si bien es funcionalmente correcta, es demasiado lenta.

Se pide implementar otra instance de Market (**EfficientMarket**) que sea lo mas rapida posible y pase el test *implementationPerformance*, sin modificar la interfaz Market.
