package tina.hazelcast.sample;


public interface SchedulerTask {	
    void sendOut(String userId, String notification);
}
