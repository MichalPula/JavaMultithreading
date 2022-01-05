package blocking_queues;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ClassImplementingDelayed implements Delayed {

    private final String name;
    private final long delayTime;

    public ClassImplementingDelayed(String name, long delayTime) {
        this.name = name;
        this.delayTime = delayTime + System.currentTimeMillis();//16050
    }

    @Override
    public long getDelay(TimeUnit unit) {
        //long remainingDelay = delayTime - System.currentTimeMillis();
        //return unit.convert(remainingDelay, TimeUnit.MILLISECONDS);
        return delayTime - System.currentTimeMillis();
        //jeśli zwrócone zostanie 0 lub wartość ujemna
        //opóźnienie dobiegło końca i można zezwolić na usunięcie elementu z kolejki
    }

    @Override
    public int compareTo(Delayed obj) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), obj.getDelay(TimeUnit.MILLISECONDS));
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClassImplementingDelayed{");
        sb.append("name='").append(name);
        sb.append(", delayTime=").append(delayTime);
        sb.append('}');
        return sb.toString();
    }
}
