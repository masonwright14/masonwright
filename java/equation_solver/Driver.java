package edu.vanderbilt.solver;

import java.util.HashMap;
import java.util.Map;

public final class Driver {
    
    private Data data;
    
    private Map<ConditionTest, Value> conditionMap;
    
    public static void main(final String[] args) {
        new Driver().start();
    }
    
    public void start() {
        this.data = new Data();
        setupTests();
        this.data.setValues(this.conditionMap);
        this.data.printByValue();
    }
    
    private void setupTests() {
        this.conditionMap = new HashMap<ConditionTest, Value>();
        
        /////////////////////////////////////////////////
        // VALUE: NONE
        
        /*
         * 4 constants
         */
        ConditionTest test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                final int constants = 4;
                return c.constants() == constants;
            }
        };
        Value value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * 3 constants
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                final int constants = 3;
                return c.constants() >= constants;
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * 1 constant
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 1;
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * var1 / var1 = anyconst/anyconst or anyconst/anyconst = var1 / var1
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 2
                    && (
                           (!Condition.isConstant(c.a()) 
                           && !Condition.isConstant((c.b()))
                           && c.a() == c.b()
                           )
                           ||
                           (!Condition.isConstant(c.c()) 
                           && !Condition.isConstant((c.d()))
                           && c.c() == c.d()
                           )                           
                       );
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * var1 / var1 = var2 / var2 or var1 / var2 = var1 / var2
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 0
                   && (
                       (c.a() == c.b()
                       && c.c() == c.d()
                       )
                       ||
                       (c.a() == c.c()
                       && c.b() == c.d()
                       )
                   );
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * no x, no y-x
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return !c.has(Term.X) && !c.has(Term.YmX);
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * no y, no y-x
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return !c.has(Term.Y) && !c.has(Term.YmX);
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * x / a = b / y, OR a / x = y / b
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 2 
                    && c.hasDiagonal(Term.Y, Term.X);
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * x / a = b / y-x, OR a / x = (y-x) / b
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 2 
                    && c.hasDiagonal(Term.YmX, Term.X);
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * y / a = b / y-x, OR a / y = (y-x) / b
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 2 
                    && c.hasDiagonal(Term.YmX, Term.Y);
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * has two of the same variable term only
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 2 && (
                    c.countTerm(Term.X) == 2
                    || c.countTerm(Term.Y) == 2
                    || c.countTerm(Term.YmX) == 2
                );
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * x / y = y / (y-x)
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.countTerm(Term.Y) == 2
                    && c.hasNonDiagonal(Term.X, Term.Y)
                    && c.hasDiagonal(Term.X, Term.YmX);
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * x / y = (y-x) / x
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.countTerm(Term.X) == 2
                    && c.hasNonDiagonal(Term.YmX, Term.X)
                    && c.hasDiagonal(Term.YmX, Term.Y);
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        /*
         * x / (y-x) = (y-x) / y
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.countTerm(Term.YmX) == 2
                    && c.hasNonDiagonal(Term.X, Term.YmX)
                    && c.hasDiagonal(Term.X, Term.Y);
            }
        };
        value = Value.NONE;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: 0
        
        /*
         * (y - x) / y = any1 / any1, or (y - x) / any1 = y / any1, or inverse
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return 
                (c.a() == Term.Y && c.b() == Term.YmX && c.c() == c.d())
                || (c.b() == Term.Y && c.a() == Term.YmX && c.c() == c.d())
                || (c.c() == Term.Y && c.d() == Term.YmX && c.a() == c.b())
                || (c.d() == Term.Y && c.c() == Term.YmX && c.a() == c.b())
                || (c.a() == Term.Y && c.c() == Term.YmX && c.b() == c.d())
                || (c.c() == Term.Y && c.a() == Term.YmX && c.b() == c.d())
                || (c.b() == Term.Y && c.d() == Term.YmX && c.a() == c.c())
                || (c.d() == Term.Y && c.b() == Term.YmX && c.a() == c.c());
            }
        };
        value = Value._0;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: 1 / 2
        
        /*
         * (y - x) / x = any1 / any1, or (y - x) / any1 = x / any1, or inverse
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return 
                (c.a() == Term.X && c.b() == Term.YmX && c.c() == c.d())
                || (c.b() == Term.X && c.a() == Term.YmX && c.c() == c.d())
                || (c.c() == Term.X && c.d() == Term.YmX && c.a() == c.b())
                || (c.d() == Term.X && c.c() == Term.YmX && c.a() == c.b())
                || (c.a() == Term.X && c.c() == Term.YmX && c.b() == c.d())
                || (c.c() == Term.X && c.a() == Term.YmX && c.b() == c.d())
                || (c.b() == Term.X && c.d() == Term.YmX && c.a() == c.c())
                || (c.d() == Term.X && c.b() == Term.YmX && c.a() == c.c());
            }
        };
        value = Value._1_2;
        this.conditionMap.put(test, value);
        
        /*
         * y / (y-x) = (y-x) / x, or the inverse
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 0
                && c.has(Term.X)
                && c.has(Term.YmX)
                && (
                    c.a() == c.d()
                    && c.b() == c.c()
                );
            }
        };
        value = Value._1_2;
        this.conditionMap.put(test, value);
        
        
        
        //////////////////////////////////////
        // VALUE: 1
        
        /*
         * x / y = const1 / const1, or x / const1 = y / const1,
         * or inverse
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 2
                && c.has(Term.X)
                && c.has(Term.Y)
                && (
                    c.a() == c.b()
                    || c.c() == c.d()
                    || c.a() == c.c()
                    || c.b() == c.d()
                );
            }
        };
        value = Value._1;
        this.conditionMap.put(test, value);
        
        /*
         * x / y = y / x or y / x = x / y
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 0
                && c.has(Term.X)
                && c.has(Term.Y)
                && (
                    c.a() == c.d()
                    && c.b() == c.c()
                );
            }
        };
        value = Value._1;
        this.conditionMap.put(test, value);
        
        /*
         * x / y = a / a or permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return 
                  (c.a() == Term.X && c.b() == Term.Y && c.c() == c.d())
                  || (c.a() == Term.Y && c.b() == Term.X && c.c() == c.d())
                  || (c.c() == Term.X && c.d() == Term.Y && c.a() == c.b())
                  || (c.c() == Term.Y && c.d() == Term.X && c.a() == c.b())
                  || (c.a() == Term.X && c.c() == Term.Y && c.b() == c.d())
                  || (c.a() == Term.Y && c.c() == Term.X && c.b() == c.d())
                  || (c.b() == Term.X && c.d() == Term.Y && c.a() == c.c())
                  || (c.b() == Term.Y && c.d() == Term.X && c.a() == c.c());
            }
        };
        value = Value._1;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: 2
        
        // y / (y-x) = (y-x) / y, or inverse
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.constants() == 0
                && c.has(Term.Y)
                && c.has(Term.YmX)
                && (
                    c.a() == c.d()
                    && c.b() == c.c()
                );
            }
        };
        value = Value._2;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: l / m
        
        /*
         * x / y = l / m, or some permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.L) 
                    && c.has(Term.M) 
                    && c.has(Term.X) 
                    && c.has(Term.Y)
                    && c.hasNonDiagonal(Term.X, Term.Y)
                    && c.hasNonDiagonal(Term.X, Term.L);
            }
        };
        value = Value.L_M;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: m / l
        
        /*
         * x / y = l / m, or some permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.L) 
                    && c.has(Term.M) 
                    && c.has(Term.X) 
                    && c.has(Term.Y)
                    && c.hasNonDiagonal(Term.X, Term.Y)
                    && c.hasNonDiagonal(Term.X, Term.M);
            }
        };
        value = Value.M_L;
        this.conditionMap.put(test, value);
        
        /*
         * y / (y-x) = l / (l-m)
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.Y) 
                    && c.has(Term.YmX) 
                    && c.has(Term.L) 
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.Y, Term.L)
                    && c.hasNonDiagonal(Term.Y, Term.YmX);
            }
        };
        value = Value.M_L;
        this.conditionMap.put(test, value);
        
        /*
         * x / (y-x) = m / (l - m)
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.X) 
                    && c.has(Term.YmX) 
                    && c.has(Term.M) 
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.X, Term.M)
                    && c.hasNonDiagonal(Term.X, Term.YmX);
            }
        };
        value = Value.M_L;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = l / (l - m)
        
        /*
         * x / y = l / (l - m)
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.X) 
                    && c.has(Term.Y) 
                    && c.has(Term.L) 
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.X, Term.L)
                    && c.hasNonDiagonal(Term.X, Term.Y);
            }
        };
        value = Value.L_LmM;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = (l - m) / l
        
        /*
         * x / y = (l-m) / l, or some permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.L) 
                    && c.has(Term.LmM) 
                    && c.has(Term.X) 
                    && c.has(Term.Y)
                    && c.hasNonDiagonal(Term.X, Term.Y)
                    && c.hasNonDiagonal(Term.X, Term.LmM);
            }
        };
        value = Value.LmM_L;
        this.conditionMap.put(test, value);
        
        /*
         * x / (y-x) = (l-m) / m, or permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.M) 
                    && c.has(Term.LmM) 
                    && c.has(Term.X) 
                    && c.has(Term.YmX)
                    && c.hasNonDiagonal(Term.X, Term.YmX)
                    && c.hasNonDiagonal(Term.X, Term.LmM);
            }
        };
        value = Value.LmM_L;
        this.conditionMap.put(test, value);
        
        /*
         * y / (y-x) = l / m, or permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.M) 
                    && c.has(Term.L) 
                    && c.has(Term.Y) 
                    && c.has(Term.YmX)
                    && c.hasNonDiagonal(Term.Y, Term.L)
                    && c.hasNonDiagonal(Term.Y, Term.YmX);
            }
        };
        value = Value.LmM_L;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = m / (l - m)
        
        /*
         * x / y = m / (l-m), or permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.X) 
                    && c.has(Term.Y) 
                    && c.has(Term.M) 
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.X, Term.M)
                    && c.hasNonDiagonal(Term.X, Term.Y);
            }
        };
        value = Value.M_LmM;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = (l - m) / m
        
        /*
         * x / y = (l - m) / m, or permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.X) 
                    && c.has(Term.Y) 
                    && c.has(Term.M) 
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.X, Term.LmM)
                    && c.hasNonDiagonal(Term.X, Term.Y);
            }
        };
        value = Value.LmM_M;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = l / (l + m)
        
        /*
         * x / (y - x) = l / m, or permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.X) 
                    && c.has(Term.YmX) 
                    && c.has(Term.L) 
                    && c.has(Term.M)
                    && c.hasNonDiagonal(Term.X, Term.L)
                    && c.hasNonDiagonal(Term.X, Term.YmX);
            }
        };
        value = Value.L_LpM;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = m / (m + l)
        
        /*
         * x / (y - x) = m / l, or permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.X) 
                    && c.has(Term.YmX) 
                    && c.has(Term.L) 
                    && c.has(Term.M)
                    && c.hasNonDiagonal(Term.X, Term.M)
                    && c.hasNonDiagonal(Term.X, Term.YmX);
            }
        };
        value = Value.M_MpL;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = l / (2 * l - m)
        
        /*
         * x / (y - x) = l / (l - m), or permutation
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.X) 
                    && c.has(Term.YmX) 
                    && c.has(Term.L)
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.X, Term.L)
                    && c.hasNonDiagonal(Term.X, Term.YmX);
            }
        };
        value = Value.L_2LmM;
        this.conditionMap.put(test, value);      
        
        //////////////////////////////////////
        // VALUE: x / y = (l - m) / (2 * l - m)
     
        /*
         * x / (y - x) = (l - m) / l
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.X) 
                    && c.has(Term.YmX) 
                    && c.has(Term.L) 
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.X, Term.LmM)
                    && c.hasNonDiagonal(Term.X, Term.YmX);
            }
        };
        value = Value.LmM_2LmM;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = (m - l) / m
        
        /*
         * y / (y - x) = m / l
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.Y) 
                    && c.has(Term.YmX) 
                    && c.has(Term.M) 
                    && c.has(Term.L)
                    && c.hasNonDiagonal(Term.Y, Term.M)
                    && c.hasNonDiagonal(Term.Y, Term.YmX);
            }
        };
        value = Value.MmL_M;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = m / (m - l)
        
        /*
         * (y - x) / y = l / (l - m)
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.Y) 
                    && c.has(Term.YmX) 
                    && c.has(Term.L) 
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.YmX, Term.L)
                    && c.hasNonDiagonal(Term.YmX, Term.Y);
            }
        };
        value = Value.M_MmL;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = (2 * m - l) / m
        
        /*
         * y / (y - x) = m / (l - m)
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.Y) 
                    && c.has(Term.YmX) 
                    && c.has(Term.M) 
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.Y, Term.M)
                    && c.hasNonDiagonal(Term.Y, Term.YmX);
            }
        };
        value = Value._2MmL_M;
        this.conditionMap.put(test, value);
        
        //////////////////////////////////////
        // VALUE: x / y = (l - 2 * m) / (l - m)
        
        /*
         * y / (y - x) = (l - m) / m
         */
        test = new ConditionTest() {
            @Override
            public boolean test(final Condition c) {
                return c.has(Term.Y) 
                    && c.has(Term.YmX) 
                    && c.has(Term.M) 
                    && c.has(Term.LmM)
                    && c.hasNonDiagonal(Term.Y, Term.LmM)
                    && c.hasNonDiagonal(Term.Y, Term.YmX);
            }
        };
        value = Value.Lm2M_LmM;
        this.conditionMap.put(test, value);      
    }
}
