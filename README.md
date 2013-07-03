# Rouge

It's that falling block game, but with [Pedestal](http://pedestal.io) and [core.async](http://github.com/clojure/core.async)!

## Play it!

1. Clone <https://github.com/swannodette/core.async> and run `lein install` in that project.
2. Launch a REPL (in rouge) with `lein repl`
3. `(start)`
4. Visit <http://localhost:3000/rouge-data-ui.html>
5. Win!

Right now the game is completely rendered in Pedestal's Data UI, but that's pretty much the coolest thing ever. Enjoy!

![Screenshot!](screenshot.png)
## Other stuff

I based this Pedestal version of that falling block game on a console version I
wrote. You'll find that version at
<https://github.com/rkneufeld/rouge/tree/console>.
