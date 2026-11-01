#!/usr/bin/env bash
# Startup test: boots a real Paper server with the freshly built jar (plus nightcore) and fails if CombatPets
# doesn't enable cleanly or logs any error/exception. Runs in GitHub Actions before a release is published.
set -uo pipefail

MC_VERSION="${MC_VERSION:-26.3}"
NIGHTCORE_VERSION="${NIGHTCORE_VERSION:-2.16.7}"
UA="CombatPets-Reforged-CI (github.com/korosensei4014-commits/CombatPets)"

JAR=$(ls build/libs/CombatPets-Reforged-*.jar | grep -v -- '-sources' | head -1)
[ -f "$JAR" ] || { echo "::error::Plugin jar not found"; exit 1; }

rm -rf smoke && mkdir -p smoke/plugins
cp "$JAR" smoke/plugins/
# nightcore (required dependency). The Maven artifact is only an API jar, so the full plugin jar is taken from
# Modrinth (preferring the version we build against), or else the latest GitHub release.
fetch_nightcore() {
  local url=""
  local versions
  versions=$(curl -fsSL -A "$UA" "https://api.modrinth.com/v2/project/nightcore/version" 2>/dev/null) || versions=""
  if [ -n "$versions" ]; then
    url=$(echo "$versions" | jq -r --arg v "$NIGHTCORE_VERSION" \
      '[.[] | select(.version_number == $v or (.version_number | startswith($v)))][0].files[0].url // empty')
    [ -n "$url" ] || url=$(echo "$versions" | jq -r '.[0].files[0].url // empty')
  fi
  if [ -z "$url" ]; then
    url=$(curl -fsSL -A "$UA" "https://api.github.com/repos/nulli0n/nightcore/releases/latest" 2>/dev/null \
      | jq -r '[.assets[] | select(.name | endswith(".jar"))][0].browser_download_url // empty')
  fi
  [ -n "$url" ] || return 1
  echo "nightcore: $url"
  curl -fsSL -A "$UA" -o smoke/plugins/nightcore.jar "$url"
}
if ! fetch_nightcore; then
  echo "::warning::Could not download nightcore; startup test skipped."
  exit 0
fi

PAPER_URL=$(curl -fsSL -A "$UA" "https://fill.papermc.io/v3/projects/paper/versions/${MC_VERSION}/builds/latest" \
  | jq -r '.downloads."server:default".url')
[ -n "$PAPER_URL" ] && [ "$PAPER_URL" != "null" ] || { echo "::error::No Paper build found for ${MC_VERSION}"; exit 1; }
curl -fsSL -A "$UA" -o smoke/paper.jar "$PAPER_URL" || { echo "::error::Could not download Paper"; exit 1; }

cd smoke
echo "eula=true" > eula.txt
cat > server.properties <<PROPS
online-mode=false
level-type=minecraft\:flat
generate-structures=false
spawn-protection=0
view-distance=4
simulation-distance=4
server-port=25599
PROPS

mkfifo console
# Keep the console pipe open so the server doesn't see EOF.
sleep 600 > console &
HOLD=$!
java -Xms1G -Xmx2G -jar paper.jar --nogui < console > server.log 2>&1 &
SERVER=$!

# Wait for startup ("Done (") for up to 6 minutes.
for _ in $(seq 1 360); do
  grep -q 'Done (' server.log && break
  kill -0 $SERVER 2>/dev/null || break
  sleep 1
done
STARTED=0; grep -q 'Done (' server.log && STARTED=1

# Let the plugin's repeating tasks run for a bit, then stop.
[ $STARTED = 1 ] && sleep 15
echo "stop" > console
for _ in $(seq 1 60); do kill -0 $SERVER 2>/dev/null || break; sleep 1; done
kill $SERVER 2>/dev/null; kill $HOLD 2>/dev/null

echo "----- CombatPets lines -----"
grep -n -i 'combatpets' server.log || true
echo "----------------------------"

FAIL=0
[ $STARTED = 1 ] || { echo "::error::Server did not finish starting"; FAIL=1; }
if grep -q 'Failed to run bootstrapper for plugins/nightcore.jar\|Could not load .plugins/nightcore' server.log; then
  echo "::error::The downloaded nightcore jar failed to load (test setup problem, not CombatPets)"; FAIL=1
fi
grep -q 'Enabling CombatPets' server.log || { echo "::error::CombatPets was not enabled"; FAIL=1; }
if grep -nE 'Error occurred while enabling CombatPets|Could not load .*CombatPets|\[CombatPets\].*(ERROR|Exception)|ERROR\]: \[CombatPets\]' server.log; then
  echo "::error::CombatPets logged an error"; FAIL=1
fi
# Any stack trace that goes through the plugin.
if grep -nE 'CombatPets-Reforged-[^ ]*\.jar//' server.log; then
  echo "::error::Exception stack trace from CombatPets"; FAIL=1
fi

if [ $FAIL = 1 ]; then
  echo "----- server.log (last 200 lines) -----"; tail -200 server.log
fi
exit $FAIL
