#!/usr/bin/env python2
import argparse
import numpy as np
import matplotlib.pyplot as plt


INTERESTING_PROVERS = set(["veriT", "CVC4", "Z3", "AllSMT"]) # "Rodin", "AtelierB", "CVC3", "AtB+AllSMT"
TXT_Y_OFFSET = 0.5
BAR_WIDTH = 0.35

def extractData(resPath, totalOnly):
    data = list()
    project = ""
    provers = list()
    with open(resPath) as lines:
        for line in lines:
            line = line.strip()
            if len(line) == 0:
                # empty line
                continue
            elif '/' in line:
                s = tuple(line.replace('/', ' ').split())
                assert len(s) == 3
                prover, discharged, total = s
                if prover in INTERESTING_PROVERS:
                    prover = prover.replace('+', '+\n')
                    data.append((project, prover, int(discharged), int(total)))
                    if not prover in provers:
                        provers.append(prover)
            else:
                # Project
                project = line
    
    if totalOnly:
        totalData = list()
        for prover in provers:
            disc = sum([d[2] for d in data if d[1] == prover])
            total = sum([d[3] for d in data if d[1] == prover])
            totalData.append(("TOTAL", prover, disc, total))
        return totalData
    else:
        return data
        
def plotProjectLabels(ind, projects, manPOsBefore, manPOsAfter, poCounts, remainingOnly):
    if remainingOnly:
        top = [max(r1,r2) for r1,r2 in zip(manPOsBefore, manPOsAfter)]
    else:
        top = poCounts
    last_prj = ""
    txt = None
    for x, pos, prj in zip(ind, top, projects):
        if prj != last_prj:
            last_prj = prj
            txt = plt.text(x, pos+TXT_Y_OFFSET, prj, rotation=70, horizontalalignment='left', verticalalignment='bottom')
        else:
            # highest among the provers
            txt.set_y(max(txt.get_position()[1], pos+TXT_Y_OFFSET))

def plotResults(projects, provers, discBefore, discAfter, poCounts, remainingOnly, totalOnly):
    N = len(discBefore)
    ind = np.arange(N)    # the x locations for the groups

    manPOsBefore = poCounts - discBefore
    manPOsAfter = poCounts - discAfter

    if remainingOnly:
        bottomBefore = N*[0]
        bottomAfter = N*[0]
    else:
        plt.bar(ind, discBefore, BAR_WIDTH, color='g', label='Discharged SMT 1.3')
        plt.bar(ind+BAR_WIDTH, discAfter, BAR_WIDTH, color='#10ee20', label='Discharged SMT 1.4')
        bottomBefore = discBefore
        bottomAfter = discAfter

    plt.bar(ind, manPOsBefore, BAR_WIDTH, bottom=bottomBefore, color='#003366', label='Remaining SMT 1.3')
    plt.bar(ind+BAR_WIDTH, manPOsAfter, BAR_WIDTH, bottom=bottomAfter, color='#66aaff', label='Remaining SMT 1.4')

    if not totalOnly:
        plotProjectLabels(ind, projects, manPOsBefore, manPOsAfter, poCounts, remainingOnly)

    plt.ylabel('Proof Obligations')
    title = "%s POs with new SMT provers" % ("Remaining" if remainingOnly else "Discharged")
    if totalOnly:
        title = "Total " + title
    plt.title(title)
    plt.xticks(ind+BAR_WIDTH, provers, rotation='vertical')
    plt.yticks()
    plt.legend()

    plt.show()

def plotBeforeAfter(beforePath, afterPath, remainingOnly, totalOnly):
    beforeData = extractData(beforePath, totalOnly)
    afterData = extractData(afterPath, totalOnly)
    assert len(beforeData) == len(afterData)
    # print beforeData
    # print afterData
    for before, after in zip(beforeData, afterData):
        assert all(before[i] == after[i] for i in [0,1,3]), "bad data: %r VS %r" % (before, after)

    projects = [d[0] for d in beforeData]
    provers = [d[1] for d in beforeData]
    poCounts = np.array([d[3] for d in beforeData])
    discBefore = np.array([d[2] for d in beforeData])
    discAfter = np.array([d[2] for d in afterData])

    plotResults(projects, provers, discBefore, discAfter, poCounts, remainingOnly, totalOnly)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('before', help=u'Text file with SMT perf results before')
    parser.add_argument('after', help=u'Text file with SMT perf results after')
    parser.add_argument('-r', '--remaining-only', help=u'Plot only the remaining POs', action='store_true')
    parser.add_argument('-t', '--total', help=u'Total POs', action='store_true')
    args = parser.parse_args()
    plotBeforeAfter(args.before, args.after, args.remaining_only, args.total)
