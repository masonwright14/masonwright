package coalitiongames;

import java.util.List;
import java.util.UUID;

public abstract class EachDraftHelper {
    
    public static int getAgentIndexById(
        final List<Agent> agents,
        final UUID uuid
    ) {
        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).getUuid().equals(uuid)) {
                return i;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    public static int getTeamIndex(
        final List<List<Integer>> teams, 
        final int index
    ) {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).contains(index)) {
                return i;
            }
        }
        
        throw new IllegalArgumentException("not on a team");
    }
    
    public static boolean isAgentTaken(
        final List<List<Integer>> teams, 
        final List<Agent> agents,
        final UUID uuid
    ) {
        for (List<Integer> team: teams) {
            for (Integer index: team) {
                final Agent agent = agents.get(index);
                if (agent.getUuid().equals(uuid)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static boolean isAgentTaken(
        final List<List<Integer>> teams, 
        final int index
    ) {
        for (List<Integer> team: teams) {
            if (team.contains(index)) {
                return true;
            }
        }
        
        return false;
    }
}
