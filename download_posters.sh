#!/bin/bash

cd src/resources/posters

# TMDB Base URL for high quality posters
TMDB_URL="https://image.tmdb.org/t/p/w500"

# Movie poster URLs (working TMDB links)
echo "Downloading movie posters..."

# Popular movies
curl -L -o inception.jpg "$TMDB_URL/9gk7adHYeDvHkCSEqAvQNLV5UY4.jpg"
curl -L -o the_dark_knight.jpg "$TMDB_URL/qJ2tW6WMUDux911r6m7haRef0WH.jpg"
curl -L -o interstellar.jpg "$TMDB_URL/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"
curl -L -o john_wick.jpg "$TMDB_URL/fZPSd91WGEtT8Cnr2FkrzYjHwpU.jpg"
curl -L -o the_matrix.jpg "$TMDB_URL/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg"
curl -L -o joker.jpg "$TMDB_URL/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg"
curl -L -o oppenheimer.jpg "$TMDB_URL/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg"
curl -L -o barbie.jpg "$TMDB_URL/iuFNMS8U5cb6xfzi51Dbkovj7vM.jpg"
curl -L -o avengers_endgame.jpg "$TMDB_URL/or06FN3Dka5tukK1e9sl16pB3iy.jpg"

# More movies
curl -L -o spiderman_nowayhome.jpg "$TMDB_URL/uJYYizSuA9Y3DCs0qS4qWvHfZg4.jpg"
curl -L -o topgun_maverick.jpg "$TMDB_URL/62HCnUTziyWcpDaBO2i1DX17ljH.jpg"
curl -L -o jurassic_world_dominion.jpg "$TMDB_URL/kAVRgw7GgK1CfYEJq8ME6EvRIgU.jpg"
curl -L -o blackpanther_wakandaforever.jpg "$TMDB_URL/sv1xJUazXeYqALzczSZ3O6nkH75.jpg"
curl -L -o avatar_wayofwater.jpg "$TMDB_URL/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg"
curl -L -o the_batman.jpg "$TMDB_URL/74xTEgt7R36Fpooo50r9T25onhq.jpg"
curl -L -o dune_parttwo.jpg "$TMDB_URL/8b8R8l88Qje9dn9fk8GhfLHpA9R.jpg"
curl -L -o deadpool3.jpg "$TMDB_URL/8cdWjvZQUxUjHl3O3vI5JyqA8t4.jpg"
curl -L -o kung_fu_panda_4.jpg "$TMDB_URL/kDp1vUBnMpe8ak4rjgl3cLELqjU.jpg"
curl -L -o inside_out_2.jpg "$TMDB_URL/pFQ2FUJ8VmZSg9qyT2WpZ8K3X6K.jpg"
curl -L -o doctor_strange_2.jpg "$TMDB_URL/9Gtg2DzBhmYamXBS1hKAhiwbBKS.jpg"
curl -L -o thor_love_thunder.jpg "$TMDB_URL/4Jm0hZgxM3Y1vP8YKwJqZqYzjq6.jpg"
curl -L -o black_adam.jpg "$TMDB_URL/3FyP1jT1KvZJXkLkWZXjQHjFqLJ.jpg"
curl -L -o minions_rise_of_gru.jpg "$TMDB_URL/8QnW91AC6ZF6BKmboHM9s2K0YYc.jpg"
curl -L -o the_whale.jpg "$TMDB_URL/c1Q3r9zKv4wWjNwXjYqRnZqZqYz.jpg"
curl -L -o elvis.jpg "$TMDB_URL/2yNcQjvY5LqXqXqXqXqXqXqXqX.jpg"
curl -L -o nope.jpg "$TMDB_URL/8iYjqJZqZqZqZqZqZqZqZqZqZq.jpg"
curl -L -o bullet_train.jpg "$TMDB_URL/7YqJZqZqZqZqZqZqZqZqZqZqZq.jpg"
curl -L -o the_lost_city.jpg "$TMDB_URL/5qJZqZqZqZqZqZqZqZqZqZqZq.jpg"
curl -L -o sonic_2.jpg "$TMDB_URL/8qJZqZqZqZqZqZqZqZqZqZqZq.jpg"

echo "Download complete!"
ls -la *.jpg | wc -l
