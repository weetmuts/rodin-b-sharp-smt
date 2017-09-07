#!/usr/bin/env python2
import os
import argparse
import xml.etree.ElementTree as ET

RES_DIR = "results"
ATTR_CONFIDENCE = "org.eventb.core.confidence"

def extractResults(dirPath):
    for d in os.listdir(RES_DIR):
        print d
        for p in ['Rodin', 'AtelierB', 'veriT', 'CVC3', 'CVC4', 'Z3', 'AllSMT', 'AtB+AllSMT']:
            poCount = 0
            proven = 0
            for bps in os.listdir(os.path.join(RES_DIR, d, p)):
                if os.path.splitext(bps)[1] == '.bps':
                    tree = ET.parse(os.path.join(RES_DIR, d, p, bps))
                    root = tree.getroot()
                    for status in root:
                        poCount += 1
                        assert ATTR_CONFIDENCE in status.attrib, "missing confidence in %s" % os.path.join(RES_DIR, d, p, bps)
                        if int(status.attrib[ATTR_CONFIDENCE]) == 1000:
                            proven += 1
            print "  %s%s%d /%s%d" % (p, ' '*(18-len(p)+3-len(str(proven))),
                                      proven, ' '*(6-len(str(poCount))), poCount)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('RESULTS', help=u"Path to the 'results' directory created by the performance application")
    args = parser.parse_args()
    extractResults(args.RESULTS)

