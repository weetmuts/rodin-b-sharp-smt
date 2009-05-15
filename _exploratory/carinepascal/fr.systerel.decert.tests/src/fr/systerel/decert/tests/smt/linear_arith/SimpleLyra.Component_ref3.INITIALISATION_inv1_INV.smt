(benchmark SimpleLyra.Component_ref3.INITIALISATION_inv1_INV.smt
  :origin { SimpleLyra | Component_ref3 | INITIALISATION/inv1/INV }
  :status sat
  :logic QF_LIA
  :extrafuns ((x Int))
  :assumption (>= x 0)
  :formula (and (>= 0 0) (<= 0 x))
)