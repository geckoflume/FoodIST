#!/bin/bash
command -v jq >/dev/null 2>&1 || {
  echo >&2 "This script requires jq but it's not installed. Aborting."
  exit 1
}

days=("MONDAY" "TUESDAY" "WEDNESDAY" "THURSDAY" "FRIDAY" "SATURDAY" "SUNDAY")
statuses=("Student" "Professor" "Researcher" "Staff" "General Public")

gen_weekdays() {
  for day in ${days[*]}; do
    json+="$(jq -n \
      --arg day "$day" \
      --arg from "$1" \
      --arg to "$2" \
      --argjson cafeteria "$3" \
      --argjson status "$4" \
      '{day_of_week: $day, from_time: $from, to_time: $to, cafeteria_id: $cafeteria, status: $status}'),"
  done
}

gen_cafeterias() {
  for cafeteria in $(seq 1 "$1"); do
    gen_weekdays "$2" "$3" "$cafeteria" "$4"
  done
  printf "Opening hours successfully generated for status %s.\n\n" "${statuses[$4]}"
}

printf "Welcome to the FoodIST default opening times generator.\nThis will generate a ready-to-use \"opening_hours.json\" file containing the same opening hours, for all weekdays, for all cafeterias for a given status.\n\n"

read -r -p "Please enter the output file path (the existing file will be overwritten): [opening_hours.json] " file
file=${file:-opening_hours.json}

read -r -p "Please enter the cafeteria total count: [16] " cafeterias_count
cafeterias_count=${cafeterias_count:-16}

echo
json="["
for status in ${!statuses[*]}; do
  read -r -p "Please enter the opening time for ${statuses[status]}: [09:00] " opening_time
  opening_time=${opening_time:-09:00}

  read -r -p "Please enter the closing time for ${statuses[status]}: [17:00] " closing_time
  closing_time=${closing_time:-17:00}

  gen_cafeterias "$cafeterias_count" "$opening_time" "$closing_time" "$status"
done
json=${json%?} # to remove the last unnecessary coma
json+="]"

echo "$json" | jq '.' >"$file"

printf "Your opening times file was successfully generated to \"%s\".\nPlease rename it to \"opening_hours.json\", move it to app/src/main/assets/ and build the FoodIST application uning \"./gradlew build\".\n" "$file"
