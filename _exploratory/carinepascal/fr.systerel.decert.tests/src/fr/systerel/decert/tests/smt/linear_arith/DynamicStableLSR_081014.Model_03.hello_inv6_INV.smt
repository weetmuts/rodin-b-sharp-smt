(benchmark DynamicStableLSR_081014.Model_03.hello_inv6_INV.smt.smt
  :origin { DynamicStableLSR_081014 | Model_03 | hello/inv6/INV }
  :status sat
  :logic QF_LIA
  :extrafuns ((n Int))
  :assumption (>= n 0)
  :formula (< 0 (+ n 1))
)


