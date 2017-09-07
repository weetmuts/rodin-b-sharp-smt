#!/usr/bin/env python2
import os
import glob
from sets import Set
from xml.dom import minidom

###
# Generation de statistiques sur des fichiers .bpr (preuve Rodin)
#
# Le script a ete ecrit pour etre execute dans un dossier contenant les projets
# Rodin que l'on veut analyser. C'est a dire, des dossiers contenant des
# fichiers de preuve Rodin (.bpr).
#
# Le script affiche en console, au format csv, les resultats de preuve obtenus
# pour chaque PO (chaque PO de chaque projet), avec chaque configuration testee.
#
# Besoin initial :
# Pour chaque projet, scanner tous les fichiers de preuve pour savoir :
# - combien de PO (proof objective) Rodin a genere pour ce projet
# - combien de PO ont ete dechargees par les prouveurs de l'atelier-B
# - combien de PO ont ete dechargees par les solveurs SMT
# - combien de PO ont ete dechargees par chaque solveur SMT
###

# Ensemble des projets academiques
academic = Set(['ch2_car', 'ch4_file_2', 'ch6_brp', 'ch7_conc',
                'ch8_circ_arbiter', 'ch8_circ_light', 'ch8_circ_pulser',
                'ch8_circ_road', 'ch910_ring', 'ch911_tree', 'ch912_mobile', 'ch913_ieee',
                'ch915_bin', 'ch915_gcd', 'ch915_inv', 'ch915_maxi',
                'ch915_mini', 'ch915_part', 'ch915_rev', 'ch915_search',
                'ch915_sort', 'ch915_sqrt', 'ch916_doors', 'ch917_train',
                'gen_hotel_new', 'library_new', 'linear_sort', 'routing_new',
                'DynamicStableLSR_081014', 'Quick', 'TreeFileSystem',
                '2_well-foundedness_with correction', '6_connectivity', 'Schorr-Waite'])

# Ensemble des projets industriels
industrial = Set(['BepiColombo_Models_v6.4', 'BoschSwitch', 'CDIS', 'New_pilot',
                  'SimpleLyra', 'SSF1.eventB', 'SSF_minipilot', 'SSF_pilot',
                  'ssf'])

# Classe pour stocker les informations sur les obligations de preuve :
# leur origine (Academic ou Industrial ou Unknown), le projet dont elles sont
# issues, leur nom (dans la forme donnee dans Rodin), et les resultats (ok ou
# ko) obtenus avec chaque configuration (Rodin, Atelier-B ou un solveur SMT)
# testee.
class PO:
    def __init__(self, origin, project, name):
        self.origin = origin
        self.project = project
        self.name = name
        self.results = {}

    # renvoie une representation de la PO au format csv
    def __repr__(self):
        po_repr = self.origin + ';' + self.project + ';' + self.name
        for config in configs:
            po_repr = po_repr + ';'
            if config in self.results:
                po_repr = po_repr + self.results[config]
        return po_repr

    # ajoute le resultat obtenu sur cette PO avec la configuration donnee
    def add(self, config, status):
        if not config in self.results:
            self.results[config] = status

# dictionnaire des pos : les clefs sont au format
# [nom_du_projet]_[nom_de_la_po] pour etre uniques et pouvoir etre reconstruites
# facilement. Les elements du dictionnaire sont des instances de la classe PO.
pos = {}
# liste des configuration, dans l'ordre de traitement (c'est-a-dire, l'ordre
# dans lequel le script traite les dossiers de configurations
configs = []

# renvoie l'origine du projet donne, pour cela, teste si le projet est dans
# l'ensemble academic ou dans l'ensemble industrial, sinon renvoie Unknown
def project_origin(project):
    if project in academic:
        return "Academic"
    elif project in industrial:
        return "Industrial"
    else:
        return "Unknown"

# renvoie le statut d'une PO en fonction de son attribut "confidence" (extrait
# du fichier bps) : ok pour 1000, ko sinon. Si la valeur donnee n'est pas
# conforme au standard Rodin, une erreur est affichee, et la po est consideree
# ko.
def po_status(confidence):
    if confidence == '1000':
        return 'ok'
    elif confidence == '0' or confidence == '-99':
        return 'ko'
    else:
        print 'Unknown confidence rate : ' + confidence
        return 'ko'

# boucle sur les obligations de preuve contenues dans le fichier bps donne
# pour chaque po, la fonction enregistre ses informations dans une instance de
# PO et la stocke dans le dictionnaire pos a la clef
# [nom_du_projet]_[nom_de_la_config]_[nom_de_la_po]
def pos_loop(origin, project, config, bps_name, bps_root):
    for po_node in bps_root.getElementsByTagName('org.eventb.core.psStatus'):
        name = po_node.getAttribute('name')
        po_key = project + "_" + bps_name + "_" + name
        
        if not po_key in pos:
#            print ("pos[" + po_key + "] = PO(" + origin + ", " + project + ", "
#                   + name + ")")
            pos[po_key] = PO(origin, project, name)

        confidence = po_node.getAttribute('org.eventb.core.confidence')
        status = po_status(confidence)
#        print "pos[" + po_key + "].add(" + config + ", " + status + ")"
        pos[po_key].add(config, status)

# boucle sur les fichiers de preuve contenus dans le dossier
# [nom_du_projet]/[nom_de_la_configuration]/ et pour chaque fichier, appelle
# pos_loop apres avoir ouvert son noeud principal
def b_proof_files_loop(origin, project, config):
    path = project + '/' + config + '/'
    for bps_path in glob.glob(path + '*.bps'):
        if os.path.isfile(bps_path):
            bps_name = os.path.splitext(os.path.basename(bps_path))[0]
            bps = minidom.parse(bps_path)
            bps_root = bps.documentElement
            pos_loop(origin, project, config, bps_name, bps_root)

# boucles sur les dossiers (projets) contenus dans le dossier courant et
# les dossiers (configurations) contenus dans ceux-ci, stocke chaque
# configuration dans la liste des configurations et appelle b_proof_files_loop
def main_loop():
    for project in sorted(os.listdir('.')):
        if os.path.isdir(project):
            origin = project_origin(project)
            for config in sorted(os.listdir(project)):
                if os.path.isdir(project + "/" + config):
                    if config not in configs:
                        configs.append(config)
                    b_proof_files_loop(origin, project, config)

# affiche les pos au format csv avec une ligne d'en-tete
def csv_format():
    print ';'.join(map(str,['Origin', 'Project', 'PO'] + configs))
    for po_key in sorted(pos.iterkeys()):
        print pos[po_key]

main_loop()
csv_format()
