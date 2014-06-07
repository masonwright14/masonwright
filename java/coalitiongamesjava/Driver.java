package coalitiongames;

import org.gnu.glpk.GLPK;

abstract class Driver {
    
    /**
     * @param args  
     */
    public static void main(final String[] args) {
        System.out.println(GLPK.glp_version());
    }
}
