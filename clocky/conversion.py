# -*- coding: utf-8 -*-
"""
Created on Tue Jun  3 10:52:28 2025
@author: sraga
"""

import json
import os
import re
import pytz
from icalendar import Calendar
from datetime import datetime

ics_path = "C:\\Users\\sraga\\Documents\\JSON\\FONDEMENT.ics"

output_dir = "C:\\Users\\sraga\\Documents\\JSON\\data"
os.makedirs(output_dir, exist_ok=True)

with open(ics_path, "rb") as f:
    cal = Calendar.from_ical(f.read())
    

for event in cal.walk():
    if event.name != "VEVENT":
        continue

#chnt fuseau horaire
    dtstart = event.get("DTSTART").dt
    dtend = event.get("DTEND").dt
    if dtstart.tzinfo is not None:
        dtstart = dtstart.astimezone(pytz.timezone("Europe/Paris"))
    if dtend.tzinfo is not None:
        dtend = dtend.astimezone(pytz.timezone("Europe/Paris"))
    summary = str(event.get("SUMMARY")).strip()
    description = str(event.get("DESCRIPTION")).replace("\\n", "\n").strip()

    # Nom du module
    module_nom = summary

    # Type de cours : CM / TDx / TPy
    type_cours = "CM"
    match_td = re.search(r"TD\s?(\d)", description, re.IGNORECASE)
    match_tp = re.search(r"TP\s?(\d)", description, re.IGNORECASE)
    
    if match_td:
        type_cours = f"TD{match_td.group(1)}"
    elif match_tp:
        type_cours = f"TP{match_tp.group(1)}"
    elif "Projet" in summary or "DS" in summary:
        type_cours = "DS/Projet"  # si pas TD/TP

    # Extraction du professeur 
    profs = []
    for line in description.splitlines():
        if re.search(r"[A-Z]{2,} [A-Z]", line):
            profs.append(line.strip())
    nom_prof = profs[0] if profs else ""

    # Nom du fichier
    cours_nom = summary.replace(" ", "").replace("/", "_")
    nom_fichier = f"presence_{cours_nom}_{dtstart.strftime('%Y%m%d')}_{dtstart.strftime('%H')}.json"
    chemin_fichier = os.path.join(output_dir, nom_fichier)

    # Contenu JSON
    contenu = {
        "module": module_nom,
        "type": type_cours,
        "prof": nom_prof,
        "date": dtstart.strftime("%Y-%m-%d"),
        "heure_debut": dtstart.strftime("%H:%M"),
        "etudiants": []
    }

    # Écriture du fichier
    with open(chemin_fichier, "w", encoding="utf-8") as json_file:
        json.dump(contenu, json_file, indent=2, ensure_ascii=False)

    print(f"Fichier généré : {nom_fichier}")
