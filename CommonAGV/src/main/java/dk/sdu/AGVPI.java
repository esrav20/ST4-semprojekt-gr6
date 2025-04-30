package dk.sdu;

import java.io.IOException;

public interface AGVPI {
    default void connectionAGV(String URL) throws IOException {
    }
    default int getStatus(){
        return 0;
    }

}
