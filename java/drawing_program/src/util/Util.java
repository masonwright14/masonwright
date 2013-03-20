package util;

import java.util.ArrayList;
import java.util.List;

public abstract class Util {

    
    public static <T> List<T> cloneList( List<T> toClone ) {
        ArrayList<T> result = new ArrayList<T>();
        result.addAll( toClone );
        return result;
    }
}
