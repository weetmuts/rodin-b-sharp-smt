(benchmark SSF_pilot.TCTM_Ref1.INITIALISATION_inv6_INV.smt
  :origin { SSF_pilot | TCTM_Ref1 | INITIALISATION/inv6/INV }
  :status sat
  :logic QF_LIA
  :extrafuns ((x Int))
  :assumption (> x 0)
  :formula (not (and (forall (?y Int) (implies (and (>= ?y 0) (<= ?y (- x 1))) false)) 
                     (forall (?y Int) (implies false (and (>= ?y 0) (<= ?y (- x 1)))))))
)


