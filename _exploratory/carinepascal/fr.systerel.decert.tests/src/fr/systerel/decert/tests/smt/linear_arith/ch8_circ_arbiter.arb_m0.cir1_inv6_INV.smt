(benchmark ch8_circ_arbiter.arb_m0.cir1_inv6_INV.smt
          :status sat
          :logic QF_LIA
          :origin {ch8_circ_arbiter | arb_m0 | cir1/inv6/INV}
          :extrafuns ( (a1 Int) (r1 Int) )
          :assumption (<= a1 r1)
          :assumption (<= r1 (+ a1 1))
          :assumption (not (= r1 a1))
          :formula (= r1 (+ a1 1))
)