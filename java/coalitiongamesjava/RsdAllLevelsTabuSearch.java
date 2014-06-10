package coalitiongames;

import java.util.List;

abstract class RsdAllLevelsTabuSearch {

    /**
     * All-levels RSD tabu search works as follows. An approximate CEEI
     * is found by tabu search, restricting the number of agents per team
     * to feasible numbers in {kMin, kMin + 1, . . ., kMax}, as given by
     * RsdUtil.getFeasibleNextTeamSizes(). 
     * Note that this requires a modification
     * of tabu search, to take a list of min, max pairs for tabu search, and
     * at each step, let agents choose their favorite affordable bundle from
     * the MIP resulting from any of these min/max pair ranges of sizes.
     * 
     * If there is no market clearing error,
     * this result is returned. Else, the RSD order is used.
     * The first agent chooses its favorite affordable bundle
     * of teammates that leaves a feasible number left, 
     * and all those teammates are "out" of consideration and
     * finally allocated to this team; there will always be such a bundle,
     * because every agent is always allotted a bundle of a feasible size
     * by the tabu search algorithm, and the first agent to choose will always
     * have the agents of this bundle left to be taken.
     * 
     * If the next agent remaining in RSD order still has all members of its
     * favorite affordable, feasible bundle (from the tabu search) available, 
     * it takes that bundle, and so on.
     * 
     * If at some point the next agent remaining in RSD order 
     * does not have all the agents of its favorite affordable, 
     * feasible bundle available, then we run a new tabu search for the 
     * remaining agents, allowing only the feasible numbers
     * of agents per team to be chosen. Continue until all 
     * agents are assigned to teams.
     * 
     * @param agents a list of all agents with their budgets and preferences
     * @param gammaZ an error function to use for updating prices
     * @param kMax maximum agents per team, including self
     * @param kMin minimum agents per team, including self
     * @param rsdOrder shuffled list of numbers from {0, 1, . . . (n - 1)}, 
     * indicating the random serial dictatorship order to use, based on
     * indexes of Agents in "agents" list
     * @return a SearchResult, including an allocation, price vector,
     * and other data
     */   
    public static SearchResult rsdTabuSearchAllLevels(
        final List<Agent> agents,
        final GammaZ gammaZ,
        final int kMax,
        final int kMin,
        final List<Integer> rsdOrder
    ) {
        return null;
    }
    

}
