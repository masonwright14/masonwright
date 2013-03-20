package edu.vanderbilt.solver;

public enum Value {
    NONE, // no unique solution
    _0, // 0
    _1_2, // 1/2
    _1, // 1
    _2, // 2
    L_M, // l / m
    M_L, // m / l
    L_LmM, // l / (l - m)
    LmM_L, // (l - m) / l
    M_LmM, // m / (l - m)
    M_MmL, // m / (m - l)
    LmM_M, // (l - m) / m
    L_LpM, // l / (l + m)
    M_MpL, // m / (m + l)
    L_2LmM, // l / (2 * l - m)
    LmM_2LmM, //  (l - m) / (2 * l - m)
    MmL_M, // (m - l) / m
    _2MmL_M, // (2 * m - l) / m
    Lm2M_LmM // (l - 2 * m) / (l - m)
}
