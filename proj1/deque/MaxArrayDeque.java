package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> com;
    public MaxArrayDeque(Comparator<T> c){
        com = c;
    }
    public T max(){
       return max(com);
    }
    public T max(Comparator<T> c){
        if(isEmpty()) return null;
        int maxIndex = 0;
        for(int i = 1; i < this.size(); i++){
            if(c.compare(get(i),get(maxIndex)) > 0) maxIndex = i;
        }
        return get(maxIndex);
    }
/*    public boolean equals(Object o){
        if(o == null) return false;
        if(o == this) return true;
        if(this.getClass() != o.getClass()) return false;
        MaxArrayDeque p = (MaxArrayDeque) o;
        if(this.com != p.com) return false;
        return super.equals(o);
    }*/
}
