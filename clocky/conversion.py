import os
import json
import glob
import uuid
import re
import pandas as pd
from icalendar import Calendar
import pytz
from datetime import datetime

# Dossiers et fichiers sources
ics_folder = "C:\\Users\\sraga\\Documents\\JSON\\"
ics_files = glob.glob(os.path.join(ics_folder, "*.ics"))
xls_path = "C:\\Users\\sraga\\Documents\\JSON\\1AIR.xlsx"
output_dir = "C:\\Users\\sraga\\Documents\\JSON\\data"
os.makedirs(output_dir, exist_ok=True)

# Lecture Excel
df_students = pd.read_excel(xls_path)
df_students["Prénom"] = df_students["Prénom"].astype(str).str.strip()
df_students["Nom"] = df_students["Nom"].astype(str).str.strip()



users_json = []

# Étudiants
for _, row in df_students.iterrows():
    prenom = row["Prénom"].lower()
    nom = row["Nom"].lower()
    email = f"{prenom}.{nom}@uha.fr"
    name = f"{prenom.capitalize()} {nom.capitalize()}"
    td = row["GroupeTD"]
    tp = row["GroupeTP"]

    student = {
        "email": email,
        "password": "changeme",
        "role": "STUDENT",
        "name": name,
        "photoResId": 0,
        "studentInfo": {
            "groupeTD": f"TD{td}",
            "groupeTP": f"TP{tp}"
        }
    }

    users_json.append(student)
    

# Liste profs
profs_noms = [
    "Joel DION",
    "Maxime DEVANNE",
    "Germain Forestier",
    "Frédéric FONDEMENT",
    "Michel HASSENFORDER",
    "Jean-Marc PERRONNE",
    "Philippe STUDER",
    "Laurent THIRY",
    "Christian VIGOUROUX",
    "Jonathan WEBER"
]



# Dossiers profs

for nom_complet in profs_noms:
    prenom, nom = nom_complet.split(" ")
    email = f"{prenom.lower()}.{nom.lower().replace(' ', '')}@uha.fr"
    prof = {
        "email": email,
        "password": "changeme",
        "role": "TEACHER",
        "name": nom_complet,
        "photoResId": 0,
        "studentInfo": None
    }
    users_json.append(prof)
    

with open(os.path.join(output_dir, "users.json"), "w", encoding="utf-8") as f:
    json.dump(users_json, f, indent=2, ensure_ascii=False)

cours_json_list = []

def safe_filename(text):
    return re.sub(r'[^\w\-_.]', '_', text)
# Traitement fichiers ICS

for ics_path in ics_files:
    print(f"\n[+] Traitement : {ics_path}")
    nom_fichier_ics = os.path.splitext(os.path.basename(ics_path))[0].upper()
    nom_prof_complet = None
    for prof in profs_noms:
        if prof.split()[-1].upper() == nom_fichier_ics:
            nom_prof_complet = prof
            break
    if nom_prof_complet is None:
        nom_prof_complet = "inconnu"

    email_prof = "inconnu@uha.fr"
    if nom_prof_complet != "inconnu":
        prenom, nom = nom_prof_complet.split()
        email_prof = f"{prenom.lower()}.{nom.lower()}@uha.fr"

    with open(ics_path, "rb") as f:
        cal = Calendar.from_ical(f.read())

    for event in cal.walk():
        if event.name != "VEVENT":
            continue

        dtstart = event.get("DTSTART").dt
        if dtstart.tzinfo is not None:
            dtstart = dtstart.astimezone(pytz.timezone("Europe/Paris"))

        summary = str(event.get("SUMMARY")).strip()
        description = str(event.get("DESCRIPTION")).replace("\\n", "\n").strip()

        if "1A info" not in description:
            continue

        type_cours = "CM"
        match_td = re.search(r"TD\s?(\d)", description, re.IGNORECASE)
        match_tp = re.search(r"TP\s?(\d)", description, re.IGNORECASE)
        if match_td:
            type_cours = f"TD{match_td.group(1)}"
        elif match_tp:
            type_cours = f"TP{match_tp.group(1)}"
        elif "Projet" in summary or "DS" in summary:
            type_cours = "CM"

        cours_id = str(uuid.uuid4())
        cours_json = {
            "nom": summary,
            "type": type_cours,
            "enseignantEmail": email_prof,
            "date_cours": dtstart.strftime("%Y-%m-%d %H:%M")
        }

        cours_json_list.append(cours_json)

# Sauvegarde du fichier cours.json
with open(os.path.join(output_dir, "cours.json"), "w", encoding="utf-8") as f:
    json.dump(cours_json_list, f, indent=2, ensure_ascii=False)
    print(f"Fichier cours.json généré avec {len(cours_json_list)} cours")