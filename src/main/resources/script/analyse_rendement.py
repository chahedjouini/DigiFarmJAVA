import argparse
import matplotlib.pyplot as plt
import pandas as pd
import pymysql
import os  # ✅ Added for handling directories
from sqlalchemy import create_engine
import sys
sys.stdout.reconfigure(encoding='utf-8')
# 📌 Gestion des arguments en ligne de commande
parser = argparse.ArgumentParser(description="Prédiction du rendement agricole")
parser.add_argument('--densite', type=float, help="Densité de plantation")
parser.add_argument('--eau', type=float, help="Besoins en eau")
parser.add_argument('--cout', type=float, help="Coût moyen")
args = parser.parse_args()

# ✅ Si des valeurs sont passées en arguments, calcul du rendement immédiat
if args.densite and args.eau and args.cout:
    coef_densite = 0.02
    coef_eau = 0.001
    coef_cout = 0.005

    rendement_estime = round(
        (args.densite * coef_densite) +
        (args.eau * coef_eau) +
        (args.cout * coef_cout),
        2
    )
    print(rendement_estime)  # ✅ Retourne la valeur pour Symfony
    exit()

# 🔥 Connexion SQLAlchemy pour récupérer toutes les cultures
DATABASE_URL = "mysql+pymysql://root:@127.0.0.1/digifarm2"
engine = create_engine(DATABASE_URL)

query = "SELECT nom, rendement_moyen, densite_plantation, besoins_eau, cout_moyen FROM culture"
df = pd.read_sql(query, engine)

engine.dispose()

# ✅ Fonction d'estimation
def estimer_rendement(row):
    coef_densite = 0.02
    coef_eau = 0.001
    coef_cout = 0.005
    return round(
        (float(row['densite_plantation']) * coef_densite) +
        (float(row['besoins_eau']) * coef_eau) +
        (float(row['cout_moyen']) * coef_cout),
        2
    )

df['rendement_estime'] = df.apply(estimer_rendement, axis=1)

# ✅ Vérifier et créer le dossier 'public/' si inexistant
output_dir = "public"
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

# ✅ Générer le graphique
plt.figure(figsize=(10, 6))
plt.bar(df['nom'], df['rendement_moyen'], label='Rendement Moyen', color='blue', alpha=0.6)
plt.bar(df['nom'], df['rendement_estime'], label='Rendement Estimé', color='orange', alpha=0.6)

plt.xlabel("Culture")
plt.ylabel("Rendement (T/ha)")
plt.title("Comparaison des Rendements")
plt.legend()

# ✅ Sauvegarder avec un chemin valide
output_path = os.path.join(output_dir, "rendement.png")
plt.savefig(output_path)
plt.close()

print("✅ Statistiques générées avec succès !")
