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
df_students["GroupeTD"] = df_students["GroupeTD"].astype(str).str.strip()
df_students["GroupeTP"] = df_students["GroupeTP"].astype(str).str.strip()

etudiants_general = []
td_groupes = {"1": [], "2": []}
tp_groupes = {"1": [], "2": [], "3": []}
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
    etudiants_general.append(student)
    if td in td_groupes:
        td_groupes[td].append(student)
    if tp in tp_groupes:
        tp_groupes[tp].append(student)

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

def safe_filename(text):
    return re.sub(r'[^\w\-_.]', '_', text)

# Dossiers profs
prof_dirs = {}
for nom_complet in profs_noms:
    prenom, nom = nom_complet.split(" ")
    email = f"{prenom.lower()}.{nom.lower().replace(' ', '')}@uha.fr"
    prof_dir = os.path.join(output_dir, safe_filename(nom_complet))
    os.makedirs(os.path.join(prof_dir, "cours"), exist_ok=True)
    os.makedirs(os.path.join(prof_dir, "presence"), exist_ok=True)
    prof_dirs[nom_complet] = prof_dir

# users.json spécifiques par dossier prof
for nom_complet, prof_dir in prof_dirs.items():
    email_prof = f"{nom_complet.split()[0].lower()}.{nom_complet.split()[1].lower()}@uha.fr"
    users_for_prof = [u for u in users_json if u["role"] == "STUDENT"] + [{
        "email": email_prof,
        "password": "changeme",
        "role": "TEACHER",
        "name": nom_complet,
        "photoResId": 0,
        "studentInfo": None
    }]
    with open(os.path.join(prof_dir, "users.json"), "w", encoding="utf-8") as f:
        json.dump(users_for_prof, f, indent=2, ensure_ascii=False)

# Traitement fichiers ICS
for ics_path in ics_files:
    print(f"\n Traitement : {ics_path}")

    # Déduire le prof à partir du nom du fichier
    nom_fichier_ics = os.path.splitext(os.path.basename(ics_path))[0].upper()
    nom_prof_complet = None
    for prof in profs_noms:
        if prof.split()[-1].upper() == nom_fichier_ics:
            nom_prof_complet = prof
            break

    if nom_prof_complet is None:
        nom_prof_complet = "inconnu"
        prof_dir = os.path.join(output_dir, "inconnu")
        os.makedirs(os.path.join(prof_dir, "cours"), exist_ok=True)
        os.makedirs(os.path.join(prof_dir, "presence"), exist_ok=True)
    else:
        prof_dir = prof_dirs[nom_prof_complet]

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
            num = match_td.group(1)
            type_cours = f"TD{num}"
        elif match_tp:
            num = match_tp.group(1)
            type_cours = f"TP{num}"
        elif "Projet" in summary or "DS" in summary:
            type_cours = "CM"

        if type_cours == "CM":
            etudiants_cours = etudiants_general
        elif type_cours in ["TD1", "TD2"]:
            etudiants_cours = td_groupes.get(type_cours[-1], [])
        elif type_cours in ["TP1", "TP2", "TP3"]:
            etudiants_cours = tp_groupes.get(type_cours[-1], [])
        else:
            etudiants_cours = []

        email_prof = "inconnu@uha.fr"
        if nom_prof_complet != "inconnu":
            email_prof = f"{nom_prof_complet.split()[0].lower()}.{nom_prof_complet.split()[1].lower()}@uha.fr"

        cours_id = str(uuid.uuid4())

        cours_json = {
            "id": cours_id,
            "nom": summary,
            "type": type_cours,
            "enseignantEmail": email_prof,
            "date_cours": dtstart.strftime("%Y-%m-%dT%H:%M:%S")
        }

        presence_json = {
            "id": cours_id,
            "coursId": summary,
            "date": dtstart.strftime("%Y-%m-%d"),
            "typeCours": type_cours,
            "etudiants": [{"email": etu["email"], "statut": "PRESENT"} for etu in etudiants_cours]
        }

        summary_safe = safe_filename(summary)
        nom_fichier_cours = f"cours_{summary_safe}_{dtstart.strftime('%Y%m%d_%H%M')}.json"
        nom_fichier_presence = f"presence_{summary_safe}_{dtstart.strftime('%Y%m%d_%H%M')}.json"

        with open(os.path.join(prof_dir, "cours", nom_fichier_cours), "w", encoding="utf-8") as f:
            json.dump(cours_json, f, indent=2, ensure_ascii=False)
            print(f" Cours enregistré: {nom_fichier_cours}")

        with open(os.path.join(prof_dir, "presence", nom_fichier_presence), "w", encoding="utf-8") as f:
            json.dump(presence_json, f, indent=2, ensure_ascii=False)
            print(f" Présence enregistrée: {nom_fichier_presence}")

