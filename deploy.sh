#!/usr/bin/env sh
set -e
cd src
npm run build
cd dist

touch .nojekyll

git init
git add -A
git commit -m 'deploy'

git remote add pages git@github.com:QuinnBast/QuinnBast.github.io.git
git push -f -u pages master:gh_pages