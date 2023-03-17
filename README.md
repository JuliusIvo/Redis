#Redis
--------

Darbas atliktas naudojant Java Spring

-------

Sukuriamas Redis konteineris naudojant docker komandinę eilutę

-------

Sukuriamas Redis projektas naudojant start.spring.io

Nurodomi dependencies - Lombok, Spring Data Reactive Redis

-------

Sukuriama Java klasė - Account
Account turi šiuos fieldus - number (kuris naudojamas kaip ID), name, balance, currency (enum tipas - gali būti USD arba EUR), type (enum tipas - gali būti JURIDICAL arba PRIVATE)

-------

Toliau susikuriamas objektas RedisTemplate, kuriam nurodoma @Autowired, tai reiškia, kad bus naudojami default Redis nustatymai, dirbant su duombaze.
Tai daroma, norint įvykdyti redis transakcijas.

--------

Taip pat sukuriau ir savo RedisConfiguration failą, kuriame nurodyti nustatymai, kurie turėtų iš esmės leisti man įvykdyti visus reikalaujamus Redis funkcionalumus,
tačiau, jis leido tik įdėti ir paimti objektus, neleido vykdyti MULTI transakcijų, taigi, nusprendžiau naudoti Spring sukurtą RedisTemplate.

--------

Aprašomos 2 transakcijos - MoneyTransfer, CurrencyChange

--------

MoneyTransfer - leidžia iš vienos banko paskyros pervesti pinigus į kitą, tuo atvėju, jei sutampa Currency (pvz. jei viena paskyroje nurodyta USD, o kitoje EUR, transakcija yra discard'inama)
MoneyTransfer naudoja 3 kintamuosius- accountFrom, accountTo, amount
Iš acount from atimamas amount (jei balance>=amount) ir accountTo pridedamas amount.
Panaudojama Redis operacija WATCH(accountFrom), norint, kad ši paskyra negalėtų dalyvauti dviejuose transakcijose vienu metu
(negaletų vienu metu pervesti dviems paskyroms visą savo balansą)

---------

CurrencyChange - leidžia paskyrai pakeisti savo valiutą, jei bandoma keisti valitą į tą, kuria paskyra jau ir naudoja, transakcija yra discard'inama.
CurrencyChange naudoja tik vieną kintamąji - account
Jei account.Currency keičiamas iš USD į EUR, account.balance yra sumažinamas 4%
Jei account.Currency keičiamas iš EUR į USD, account.balance yra padidinamas 4%
Redis operacija WATCH() nenaudojama.
