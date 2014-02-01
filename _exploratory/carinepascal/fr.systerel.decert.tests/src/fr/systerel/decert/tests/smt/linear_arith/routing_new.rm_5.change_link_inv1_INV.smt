(benchmark routing_new.rm_5.change_link_inv1_INV.smt
          :status sat
          :logic QF_LIA
          :origin {routing_new | rm_5 | change_link/inv1/INV}
          :extrafuns ( (z Int) (y Int) (x Int) )
          :assumption (>= x 0)
          :assumption (>= y 0)
          :assumption (>= z 0)
          :assumption (< x y)
          :assumption (and (>= y 0) (<= y x))
          :formula (= x z)
)
