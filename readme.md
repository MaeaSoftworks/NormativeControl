# Normative Control
![Normative Control](resources/normative-control.svg)

Normative Control service

Powered by 
[Tellurium](tellurium/src/main/resources/tellurium.svg),
[Polonium](polonium/src/main/resources/polonium.svg) &
[Livermorium](livermorium/src/main/resources/livermorium.svg).

Written using [Kotlin](https://kotlinlang.org/) by Mæa Softworks with ❤.

## Before start
Please, check `readme.md` files in module folders to quick guides & cheat sheets:
- [Tellurium readme](tellurium/readme.md)
- [Polonium readme](polonium/readme.md)

## Tasks for the future
1. [ ] fix todos (all todos you can find in `//todo` comments);
2. [ ] add more spellchecks in accordance with [guidelines](resources/Guidelines.pdf);
3. [ ] add more handlers to [parsers](polonium/src/main/kotlin/com/maeasoftworks/polonium/parsers);
4. [ ] add more rules to [Rules](polonium/src/main/kotlin/com/maeasoftworks/polonium/model/Rules.kt);
5. [ ] handle more types of content in [Renderer](livermorium/src/main/kotlin/com/maeasoftworks/livermorium/rendering/Renderer.kt);
6. [ ] add endpoint to get user settings of style checking and run check with this config (optional, requires new page on frontend).

### Links

- [Guidelines](resources/Guidelines.pdf)

- [REST API documentation](https://normative-control-api.herokuapp.com/docs)

- [Web App](https://normative-control.herokuapp.com/)

- [Frontend application repository](https://github.com/EliteHacker228/normative-control)