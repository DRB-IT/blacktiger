package dk.drb.blacktiger.fixture.rest;

/**
 *
 * @author michael
 */
public class SystemRestDataFixture {
    
    public static String standardInfoAsJson() {
        return "{\"averageCpuLoad\":{\"oneMinute\":20.0,\"fiveMinutes\":0.0,\"tenMinutes\":0.0},\"load\":{\"disk\":12.5,\"cpu\":25.0,\"net\":0.0,\"memory\":33.33333333333333},\"cores\":4}";
    }
}
