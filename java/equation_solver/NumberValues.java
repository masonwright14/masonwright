package edu.vanderbilt.solver;

public final class NumberValues {

    private final double lValue;
    private final double mValue;
    
    public NumberValues(final double l, final double m) {
        this.lValue = l;
        this.mValue = m;
    }
    
    public double getL() {
        return this.lValue;
    }
    
    public double getM() {
        return this.mValue;
    }
    
    /*
     * returns -1.0 if the value type is Value.NONE
     */
    double getValue(final Value value) {
        switch (value) {
        case NONE: return -1.0;
        case _0: return 0.0;
        case _1_2: 
            final double half = 0.5;
            return half;
        case _1: return 1.0;
        case _2: return 2.0;
        case L_M: return this.lValue / this.mValue;
        case M_L: return this.mValue / this.lValue;
        case L_LmM: return this.lValue / (this.lValue - this.mValue);
        case LmM_L: return (this.lValue - this.mValue) / this.lValue;
        case M_LmM: return this.mValue / (this.lValue - this.mValue);
        case M_MmL: return this.mValue / (this.mValue - this.lValue);
        case LmM_M: return (this.lValue - this.mValue) / this.mValue;
        case L_LpM: return this.lValue / (this.lValue + this.mValue);
        case M_MpL: return this.mValue / (this.mValue + this.lValue);
        case L_2LmM: return this.lValue / (2 * this.lValue - this.mValue);
        case LmM_2LmM: return (this.lValue - this.mValue) 
            / (2 * this.lValue - this.mValue);
        case MmL_M: return (this.mValue - this.lValue) / this.mValue;
        case _2MmL_M: return (2 * this.mValue - this.lValue) 
            / this.mValue;
        case Lm2M_LmM: return (this.lValue - 2 * this.mValue) 
            / (this.lValue - this.mValue);
        default: throw new IllegalArgumentException();
        }
    }
}
