package dk.drb.blacktiger.fixture.rest;

/**
 *
 * @author michael
 */
public class SystemRestDataFixture {
    
    public static String standardInfoAsJson() {
        return "{\"averageCpuLoad\":{\"oneMinute\":20.0,\"fiveMinutes\":0.0,\"tenMinutes\":0.0},\"load\":{\"disk\":0,\"cpu\":25.0,\"net\":0.0,\"memory\":0},\"cores\":4}";
    }
}
