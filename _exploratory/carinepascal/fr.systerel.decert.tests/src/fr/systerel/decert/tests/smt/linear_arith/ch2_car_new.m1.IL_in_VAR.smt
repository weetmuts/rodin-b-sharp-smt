(benchmark ch2_car_new.m1.IL_in_VAR.smt
  :origin { ch2_car_new | m1 | IL_in/VAR }
  :status sat
  :logic QF_LIA
  :extrafuns ((a Int) (b Int))
  :assumption (>= a 0)
  :assumption (>= b 0)
  :assumption (> a 0)
  :formula (< (+ (* 2 (- a 1)) b 1) (+ (* 2 a) b))
)


